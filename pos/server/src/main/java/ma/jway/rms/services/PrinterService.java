package ma.jway.rms.services;

import com.github.anastaciocintra.escpos.EscPos;
import com.github.anastaciocintra.escpos.image.BitonalThreshold;
import com.github.anastaciocintra.escpos.image.CoffeeImageImpl;
import com.github.anastaciocintra.escpos.image.EscPosImage;
import com.github.anastaciocintra.escpos.image.RasterBitImageWrapper;

import ma.jway.rms.dto.enums.DiscountType;
import ma.jway.rms.dto.enums.ItemType;
import ma.jway.rms.dto.enums.OrderType;
import ma.jway.rms.dto.models.*;
import ma.jway.rms.repositories.*;
import ma.jway.rms.utilities.Helpers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.simple.Graphics2DRenderer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PrinterService {
    @Value("${storage.default.path}")
    private String storageFolder;

    private final OrderRepository orderRepository;
    private final PrinterRepository printerRepository;
    private final StationRepository stationRepository;
    private final CompanyRepository companyRepository;
    private final OrderItemRepository orderItemRepository;
    private final ItemRepository itemRepository;
    private final ResourceRepository resourceRepository;
    private final TemplateEngine templateEngine;

    public PrinterService(
            OrderRepository orderRepository,
            TemplateEngine templateEngine,
            PrinterRepository printerRepository,
            StationRepository stationRepository,
            CompanyRepository companyRepository,
            OrderItemRepository orderItemRepository,
            ItemRepository itemRepository,
            ResourceRepository resourceRepository) {
        this.orderRepository = orderRepository;
        this.templateEngine = templateEngine;
        this.printerRepository = printerRepository;
        this.stationRepository = stationRepository;
        this.companyRepository = companyRepository;
        this.orderItemRepository = orderItemRepository;
        this.itemRepository = itemRepository;
        this.resourceRepository = resourceRepository;
    }

    @Transactional
    public void printCheckoutReceipt(Long id) throws IOException {
        Order order = orderRepository.findById(id).orElse(null);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        Station station = stationRepository.findByReference("checkout");
        Printer printer = printerRepository.findByStation(station);

        Company company = companyRepository.findAll().get(0);

        List<HashMap<String, Object>> itemsHashMapList = new ArrayList<>();
        for (OrderItem orderItem : order.getOrderItems()) {
            boolean existingItemFound = false;
            for (HashMap<String, Object> existingItemMap : itemsHashMapList) {
                if (existingItemMap.get("id").equals(orderItem.getItem().getId())
                        && orderItem.getCustomizations().isEmpty()) {
                    int quantity = (int) existingItemMap.getOrDefault("quantity", 0) + 1;
                    existingItemMap.put("quantity", quantity);
                    existingItemMap.put("totalPrice",
                            Helpers.formatDouble(orderItem.getItem().getPrice() * quantity));
                    existingItemFound = true;
                    break;
                }
            }

            if (!existingItemFound) {
                HashMap<String, Object> itemMap = new HashMap<>();
                itemMap.put("id", orderItem.getItem().getId());
                itemMap.put("name", orderItem.getItem().getName());
                itemMap.put("price", Helpers.formatDouble(orderItem.getItem().getPrice()));
                itemMap.put("quantity", 1);
                itemMap.put("totalPrice",
                        Helpers.formatDouble(orderItem.getItem().getPrice()));

                List<HashMap<String, Object>> customizationsHashMapList = new ArrayList<>();
                if (!orderItem.getCustomizations().isEmpty()) {
                    for (Customization customization : orderItem.getCustomizations()) {
                        boolean existingCustomizationFound = false;
                        for (HashMap<String, Object> existingCustomizationMap : customizationsHashMapList) {
                            if (customization.getCustomizedItem().equals(existingCustomizationMap.get("id"))) {
                                int quantity = (int) existingCustomizationMap.getOrDefault("quantity", 0) + 1;
                                existingCustomizationMap.put("quantity", quantity);
                                existingCustomizationMap.put("totalPrice",
                                        Helpers.formatDouble(customization.getCost() * quantity));
                                existingCustomizationFound = true;
                                break;
                            }
                        }

                        if (!existingCustomizationFound) {
                            HashMap<String, Object> customizationMap = new HashMap<>();
                            customizationMap.put("id", customization.getCustomizedItem());

                            String name = "";
                            if (customization.getItemType() == ItemType.ITEM) {
                                Item i = itemRepository.findById(customization.getCustomizedItem()).orElse(null);
                                name = i.getName();
                            } else if (customization.getItemType() == ItemType.INGREDIENT) {
                                Resource resource = resourceRepository.findById(customization.getCustomizedItem())
                                        .orElse(null);
                                name = resource.getName();
                            }

                            customizationMap.put("name", name);
                            customizationMap.put("quantity", 1);
                            customizationMap.put("price", Helpers.formatDouble(customization.getCost()));
                            customizationMap.put("totalPrice", Helpers.formatDouble(customization.getCost()));
                            customizationsHashMapList.add(customizationMap);
                        }
                    }
                }

                itemMap.put("customizations", customizationsHashMapList);
                itemsHashMapList.add(itemMap);
            }
        }

        Context context = new Context();
        context.setVariable("items", itemsHashMapList);
        context.setVariable("company", company);
        context.setVariable("waiter",
                String.format("%s %s", order.getUser().getFirstname(), order.getUser().getLastname()));
        context.setVariable("orderType", order.getOrderType().name());
        context.setVariable("date", formatter.format(order.getUpdatedAt()));
        context.setVariable("order", order);
        context.setVariable("subTotal", Helpers.formatDouble(order.getSubTotal()));
        context.setVariable("discount",
                order.getDiscountType() == DiscountType.FIXED_AMOUNT ? Helpers.formatDouble(order.getDiscountAmount())
                        : Helpers.formatDouble(
                                order.getSubTotal() - (order.getDiscountAmount() * order.getSubTotal() / 100)));

        context.setVariable("hasDiscount", order.getDiscountAmount() != 0);
        context.setVariable("totalToPay", Helpers.formatDouble(order.getTotalAfterDiscount()));
        context.setVariable("avecTVA", order.getVatPercentage() != null);
        context.setVariable("tvaPercentage",
                Helpers.formatDouble(order.getVatPercentage() != null ? order.getVatPercentage() : 0));
        context.setVariable("tva", Helpers.formatDouble(order.getVatAmount()));
        context.setVariable("ttc", Helpers.formatDouble(order.getTotalCost()));

        if (order.getOrderType() == OrderType.AT_THE_TABLE) {
            context.setVariable("floor", order.getDiningTable().getArea().getFloor().getName());
            context.setVariable("area", order.getDiningTable().getArea().getName());
            context.setVariable("table", order.getDiningTable().getNumber());
        }

        String receipt = templateEngine.process("receipts/checkout.html", context);
        convertHtmlToImage(receipt, "checkout", printer);
    }

    @Transactional
    public void processOrder(Long id) throws IOException {
        Order order = orderRepository.findById(id).orElse(null);
        List<OrderItem> allItems = order.getOrderItems();

        List<OrderItem> orderItems = allItems.stream()
                .filter(item -> !item.getPrinted())
                .collect(Collectors.toList());

        Map<String, List<OrderItem>> groupedItems = new HashMap<>();

        for (OrderItem item : orderItems) {
            String station = item.getItem().getCategory().getStation().getReference();
            groupedItems.computeIfAbsent(station, k -> new ArrayList<>()).add(item);
        }

        for (Map.Entry<String, List<OrderItem>> entry : groupedItems.entrySet()) {
            List<OrderItem> items = entry.getValue();
            String stationRef = entry.getKey();
            processItems(order, items, stationRef);
        }

        for (OrderItem item : orderItems) {
            item.setPrinted(false);
            orderItemRepository.save(item);
        }
    }

    private void processItems(Order order, List<OrderItem> items, String stationRef) throws IOException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        Station station = stationRepository.findByReference(stationRef);
        Printer printer = printerRepository.findByStation(station);

        List<HashMap<String, Object>> itemsHashMapList = new ArrayList<>();
        for (OrderItem orderItem : items) {
            boolean existingItemFound = false;
            for (HashMap<String, Object> existingItemMap : itemsHashMapList) {
                if (existingItemMap.get("id").equals(orderItem.getItem().getId())
                        && orderItem.getCustomizations().isEmpty()) {
                    int quantity = (int) existingItemMap.getOrDefault("quantity", 0) + 1;
                    existingItemMap.put("quantity", quantity);
                    existingItemFound = true;
                    break;
                }
            }

            if (!existingItemFound) {
                HashMap<String, Object> itemMap = new HashMap<>();
                itemMap.put("id", orderItem.getItem().getId());
                itemMap.put("name", orderItem.getItem().getName());
                itemMap.put("quantity", 1);

                List<HashMap<String, Object>> customizationsHashMapList = new ArrayList<>();
                if (!orderItem.getCustomizations().isEmpty()) {
                    for (Customization customization : orderItem.getCustomizations()) {
                        boolean existingCustomizationFound = false;
                        for (HashMap<String, Object> existingCustomizationMap : customizationsHashMapList) {
                            if (customization.getCustomizedItem().equals(existingCustomizationMap.get("id"))) {
                                int quantity = (int) existingCustomizationMap.getOrDefault("quantity", 0) + 1;
                                existingCustomizationMap.put("quantity", quantity);
                                existingCustomizationFound = true;
                                break;
                            }
                        }

                        if (!existingCustomizationFound) {
                            HashMap<String, Object> customizationMap = new HashMap<>();
                            customizationMap.put("id", customization.getCustomizedItem());

                            String name = "";
                            if (customization.getItemType() == ItemType.ITEM) {
                                Item i = itemRepository.findById(customization.getCustomizedItem()).orElse(null);
                                name = i.getName();
                            } else if (customization.getItemType() == ItemType.INGREDIENT) {
                                Resource resource = resourceRepository.findById(customization.getCustomizedItem())
                                        .orElse(null);
                                name = resource.getName();
                            }

                            customizationMap.put("name", name);
                            customizationMap.put("quantity", 1);
                            customizationsHashMapList.add(customizationMap);
                        }
                    }
                }

                itemMap.put("customizations", customizationsHashMapList);
                itemsHashMapList.add(itemMap);
            }
        }

        Context context = new Context();
        context.setVariable("items", itemsHashMapList);
        context.setVariable("orderType", order.getOrderType().name());
        context.setVariable("date", formatter.format(order.getUpdatedAt()));
        context.setVariable("station", stationRef);
        context.setVariable("order", order);

        if (order.getOrderType() == OrderType.AT_THE_TABLE) {
            context.setVariable("floor", order.getDiningTable().getArea().getFloor().getName());
            context.setVariable("area", order.getDiningTable().getArea().getName());
            context.setVariable("table", order.getDiningTable().getNumber());
        }

        String receipt = templateEngine.process("receipts/kitchen.html", context);
        convertHtmlToImage(receipt, stationRef, printer);
    }

    private void printReceipt(Printer printer, BufferedImage bufferedImage) throws IOException {
        Socket socket = new Socket(printer.getHost(), printer.getPort());
        EscPos escpos = new EscPos(socket.getOutputStream());

        RasterBitImageWrapper imageWrapperHeader = new RasterBitImageWrapper();
        EscPosImage HeaderImage = new EscPosImage(new CoffeeImageImpl(bufferedImage),
                new BitonalThreshold());

        escpos.write(imageWrapperHeader, HeaderImage);
        escpos.write("\n\n\n");
        escpos.cut(EscPos.CutMode.FULL);

        socket.getOutputStream().flush();

        escpos.close();
        socket.close();
    }

    private void convertHtmlToImage(String html, String ref, Printer printer) throws IOException {
        File tempFile = createTempHtmlFile(html);

        try {
            String filePath = tempFile.toURI().toURL().toExternalForm();
            BufferedImage image = Graphics2DRenderer.renderToImageAutoSize(filePath, 560, BufferedImage.TYPE_INT_ARGB);
            ImageIO.write(image, "png", new File(String.format("%s/receipts/%s.png", storageFolder, ref)));
            printReceipt(printer, image);
        } finally {
            tempFile.delete();
        }
    }

    private File createTempHtmlFile(String html) throws IOException {
        File tempFile = File.createTempFile("receipt", ".html");
        try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(tempFile),
                StandardCharsets.UTF_8)) {
            writer.write(html);
        }
        return tempFile;
    }

    // private File createTempHtmlFile(String html) throws IOException {
    // File tempFile = File.createTempFile("receipt", ".html");
    // try (FileWriter writer = new FileWriter(tempFile)) {
    // writer.write(html);
    // }
    // return tempFile;
    // }
}
