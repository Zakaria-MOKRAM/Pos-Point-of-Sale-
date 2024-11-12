package ma.jway.rms.services;

import ma.jway.rms.dto.enums.DiningTableStatus;
import ma.jway.rms.dto.enums.OrderStatus;
import ma.jway.rms.dto.enums.OrderType;
import ma.jway.rms.dto.models.*;
import ma.jway.rms.dto.requests.ItemCustomizationsRequest;
import ma.jway.rms.dto.requests.ItemRequest;
import ma.jway.rms.dto.requests.OrderRequest;
import ma.jway.rms.dto.responses.OrderResponse;
import ma.jway.rms.repositories.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final ItemRepository itemRepository;
    private final OrderItemRepository orderItemRepository;
    private final CustomizationRepository customizationRepository;
    private final IngredientRepository ingredientRepository;
    private final DiningTableRepository diningTableRepository;
    private final UserRepository userRepository;

    @Transactional
    public List<OrderResponse> getOrders() {
        List<OrderResponse> orderResponses = new ArrayList<>();

        List<Order> orders = orderRepository.findAll();
        for (Order order : orders) {
            orderResponses.add(new OrderResponse(
                    order.getId(),
                    order.getDiscountAmount(),
                    order.getDiscountType(),
                    order.getGuests(),
                    order.getOrderType(),
                    order.getStatus(),
                    order.getTotalAfterDiscount(),
                    order.getCreatedAt(),
                    new ArrayList<>()));
        }

        return orderResponses;
    }

    @Transactional
    public HashMap<String, Object> getLatestOrderByDiningTable(Long id) {
        DiningTable diningTable = diningTableRepository.findById(id).orElse(null);
        Order order = orderRepository.findByDiningTable(diningTable).get(0);

        HashMap<String, Object> orderResponse = new HashMap<>();

        orderResponse.put("id", order.getId());
        orderResponse.put("discount", order.getDiscountAmount());
        orderResponse.put("discountType", order.getDiscountType());
        orderResponse.put("subTotal", order.getSubTotal());
        orderResponse.put("total", order.getTotalAfterDiscount());
        orderResponse.put("guests", order.getGuests());
        orderResponse.put("orderType", order.getOrderType());
        orderResponse.put("status", order.getStatus());

        List<HashMap<String, Object>> items = order.getOrderItems() != null ? order.getOrderItems().stream()
                .collect(Collectors.groupingBy(orderItem -> orderItem.getItem().getId()))
                .entrySet().stream()
                .map(entry -> {
                    HashMap<String, Object> item = new HashMap<>();
                    item.put("id", entry.getKey());
                    item.put("customized", entry.getValue().get(0).getCustomizations().size() > 0);
                    item.put("quantity", entry.getValue().size());
                    item.put("details", createItemDetails(entry.getValue().get(0).getItem()));
                    item.put("selected", false);
                    item.put("printed", true);
                    return item;
                })
                .collect(Collectors.toList()) : new ArrayList<>();

        orderResponse.put("items", items);

        return orderResponse;
    }

    private HashMap<String, Object> createItemDetails(Item item) {
        HashMap<String, Object> details = new HashMap<>();
        details.put("id", item.getId());
        details.put("name", item.getName());
        details.put("price", item.getPrice());
        return details;
    }

    @Transactional
    public Order createOrder(OrderRequest orderRequest) {
        Order order = new Order();
        order.setStatus(OrderStatus.PLACED);
        order.setGuests(orderRequest.guests());

        User user = userRepository.findById(orderRequest.user()).orElse(null);
        order.setUser(user);

        if (orderRequest.orderType() == OrderType.AT_THE_TABLE) {
            DiningTable diningTable = diningTableRepository.findById(orderRequest.diningTable()).orElse(null);
            order.setDiningTable(diningTable);

            diningTable.setStatus(DiningTableStatus.RESERVED);
            diningTableRepository.save(diningTable);
        }

        order = orderRepository.save(order);

        List<OrderItem> orderItems = new ArrayList<>();

        for (ItemRequest itemRequest : orderRequest.items()) {
            Item item = itemRepository.findById(itemRequest.id()).orElse(null);

            if (item != null) {
                List<ItemCustomizationsRequest> itemCustomizations = itemRequest.customizations();

                OrderItem orderItem = createOrderItem(item, order, itemRequest);
                orderItemRepository.save(orderItem);

                List<Customization> customizations = new ArrayList<>();
                if (itemCustomizations != null && !itemCustomizations.isEmpty()) {
                    for (ItemCustomizationsRequest ic : itemCustomizations) {
                        Customization customization = new Customization();
                        customization.setItemType(ic.itemType());
                        customization.setCustomizedItem(ic.id());
                        customization.setCustomizationType(ic.customizationType());
                        customization.setQuantity(ic.quantity());
                        customization.setCost(ic.cost());
                        customization.setOrderItem(orderItem);

                        customizationRepository.save(customization);
                        customizations.add(customization);
                    }
                }
                orderItem.setCustomizations(customizations);
                orderItems.add(orderItem);
            }
        }

        orderItemRepository.saveAll(orderItems);

        order.setOrderItems(orderItems);
        order.setOrderType(orderRequest.orderType());
        order.setSubTotal(orderItems.stream().mapToDouble(OrderItem::getTotalPrice).sum());
        order.setDiscountType(orderRequest.discountType());
        order.setDiscountAmount(orderRequest.discount());
        order.setVatPercentage(orderRequest.tvaRate());
        order.calculateTotals();
        orderRepository.save(order);

        return order;
    }

    private OrderItem createOrderItem(Item item, Order order, ItemRequest itemRequest) {
        OrderItem orderItem = new OrderItem();
        orderItem.setItem(item);
        orderItem.setOrder(order);

        if (itemRequest.customizations() != null && !itemRequest.customizations().isEmpty()) {
            Double customizationSum = 0.0;
            for (ItemCustomizationsRequest itemCustomizations : itemRequest.customizations()) {
                customizationSum += itemCustomizations.cost();
            }
            orderItem.setTotalPrice(itemRequest.totalPrice() + customizationSum);
        } else {
            orderItem.setTotalPrice(itemRequest.totalPrice());
        }

        orderItem = orderItemRepository.save(orderItem);

        List<Customization> customizations = new ArrayList<>();

        List<ItemCustomizationsRequest> itemCustomizations = itemRequest.customizations();
        if (itemCustomizations != null && itemCustomizations.size() > 0) {
            for (ItemCustomizationsRequest itemCustomization : itemCustomizations) {
                Customization customization = new Customization();
                customization.setQuantity(itemCustomization.quantity());
                customization.setItemType(itemCustomization.itemType());
                customization.setCost(itemCustomization.cost());
                customization.setOrderItem(orderItem);
                customization.setCustomizationType(itemCustomization.customizationType());

                switch (itemCustomization.itemType()) {
                    case INGREDIENT:
                        Resource ingredient = ingredientRepository.findById(itemCustomization.id()).orElse(null);
                        if (ingredient != null) {
                            customization.setCustomizedItem(ingredient.getId());
                            customizations.add(customization);
                        }
                        break;
                    case ITEM:
                        Item customItem = itemRepository.findById(itemCustomization.id()).orElse(null);
                        if (customItem != null) {
                            customization.setCustomizedItem(customItem.getId());
                            customizations.add(customization);
                        }
                        break;
                }
            }
        }

        customizationRepository.saveAll(customizations);
        orderItem.setCustomizations(customizations);

        return orderItem;
    }

}
