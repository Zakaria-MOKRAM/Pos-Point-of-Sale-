package ma.jway.rms.services;

import javax.transaction.Transactional;

import ma.jway.rms.dto.enums.AddsOnType;
import ma.jway.rms.dto.enums.ItemCompositionStatus;
import ma.jway.rms.dto.models.*;
import ma.jway.rms.dto.responses.AddOnResponse;
import ma.jway.rms.dto.responses.CategoryResponse;
import ma.jway.rms.dto.responses.ItemResponse;
import ma.jway.rms.dto.responses.ResourceResponse;
import ma.jway.rms.repositories.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Service
public class ItemsService {
    private final ItemRepository itemRepository;
    private final ItemCompositionRepository itemCompositionRepository;
    private final ItemDetailsRepository itemDetailsRepository;
    private final AddsOnRepository addsOnRepository;
    private final ResourceRepository resourceRepository;
    @Value("${storage.default.path}")
    private String storageFolder;

    public ItemsService(
            ItemRepository itemRepository,
            ItemCompositionRepository itemCompositionRepository,
            ItemDetailsRepository itemDetailsRepository,
            AddsOnRepository addsOnRepository,
            ResourceRepository resourceRepository) {
        this.itemRepository = itemRepository;
        this.itemCompositionRepository = itemCompositionRepository;
        this.itemDetailsRepository = itemDetailsRepository;
        this.addsOnRepository = addsOnRepository;
        this.resourceRepository = resourceRepository;
    }

    @Transactional
    public List<ItemResponse> fetchSalableItems() throws SQLException, IOException {
        List<Item> items = itemRepository.findSalableItems();
        List<ItemResponse> itemResponses = new ArrayList<>();

        for (Item item : items) {
            ItemResponse itemResponse = createItemResponse(item);
            itemResponses.add(itemResponse);
        }

        return itemResponses;
    }

    private ItemResponse createItemResponse(Item item) throws SQLException, IOException {
        List<ItemResponse> itemCompositionResponse = new ArrayList<>();

        if (item.getCompositionStatus() == ItemCompositionStatus.COMPOSED) {
            List<ItemComposition> itemCompositions = itemCompositionRepository.findByParentItem(item);
            for (ItemComposition itemComposition : itemCompositions) {
                ItemResponse subItemResponse = createItemResponse(itemComposition.getSubItem());
                itemCompositionResponse.add(subItemResponse);
            }
        }

        List<ResourceResponse> resourceResponses = new ArrayList<>();
        List<ItemDetails> itemDetails = itemDetailsRepository.findByItem(item);

        for (ItemDetails itemDetail : itemDetails) {
            resourceResponses.add(new ResourceResponse(
                    itemDetail.getResource().getId(),
                    itemDetail.getResource().getName(),
                    itemDetail.getResource().getDescription(),
                    itemDetail.getResource().getUnitOfMeasure(),
                    itemDetail.getResource().isItem(),
                    itemDetail.isMandatory(),
                    itemDetail.getQuantity()));
        }

        List<AddOnResponse> addOnResponses = new ArrayList<>();
        List<AddsOn> adds = addsOnRepository.findByParentItem(item);

        for (AddsOn addsOn : adds) {
            Resource resource = null;
            Item fetchedItem = null;

            if (addsOn.getAddsOnType() == AddsOnType.ITEM) {
                fetchedItem = itemRepository.findById(addsOn.getAddsOnItem()).orElse(null);
            } else {
                resource = resourceRepository.findById(addsOn.getAddsOnItem()).orElse(null);
            }

            ResourceResponse resourceResponse = null;
            if (resource != null) {
                resourceResponse = new ResourceResponse(
                        resource.getId(),
                        resource.getName(),
                        resource.getDescription(),
                        resource.getUnitOfMeasure(),
                        resource.isItem(),
                        false,
                        null);
            }

            ItemResponse itemResponse = null;
            if (fetchedItem != null) {
                itemResponse = new ItemResponse(
                        fetchedItem.getId(),
                        fetchedItem.getName(),
                        fetchedItem.getPrice(),
                        fetchedItem.getDescription(),
                        fetchedItem.getSaleStatus(),
                        fetchedItem.getPurchaseStatus(),
                        fetchedItem.getFreeAddsOnLimit(),
                        fetchedItem.getPaidAddsOnLimit(),
                        fetchedItem.getCompositionStatus(),
                        new ArrayList<>(),
                        createCategoryResponse(fetchedItem.getCategory()),
                        new ArrayList<>(),
                        new ArrayList<>(),
                        (fetchedItem.getImage() != null)
                                ? fetchedItem.getImage().getBytes(1, (int) fetchedItem.getImage().length())
                                : new byte[0]);
            }

            addOnResponses.add(new AddOnResponse(
                    addsOn.getId(),
                    addsOn.getAddsOnType(),
                    addsOn.getMax(),
                    resourceResponse,
                    itemResponse));
        }

        CategoryResponse categoryResponse = createCategoryResponse(item.getCategory());
        return new ItemResponse(
                item.getId(),
                item.getName(),
                item.getPrice(),
                item.getDescription(),
                item.getSaleStatus(),
                item.getPurchaseStatus(),
                item.getFreeAddsOnLimit(),
                item.getPaidAddsOnLimit(),
                item.getCompositionStatus(),
                itemCompositionResponse,
                categoryResponse,
                resourceResponses,
                addOnResponses,
                (item.getImage() != null) ? item.getImage().getBytes(1, (int) item.getImage().length()) : new byte[0]);
    }

    private CategoryResponse createCategoryResponse(Category category) {
        String iconPath = String.format("%s/%s", category.getIcon().getPath(), category.getIcon().getFilename());
        return new CategoryResponse(
                category.getId(),
                category.getName(),
                category.getReference(),
                category.getDescription(),
                iconPath);
    }

    public void saveImage(Blob image, Long id) throws IOException, SQLException {
        if (image != null) {
            byte[] imageBytes = image.getBytes(1, (int) image.length());
            BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(imageBytes));

            File outputFile = new File(String.format("%s/%s.jpg", storageFolder, id.toString()));
            ImageIO.write(bufferedImage, "jpg", outputFile);
        }
    }
}
