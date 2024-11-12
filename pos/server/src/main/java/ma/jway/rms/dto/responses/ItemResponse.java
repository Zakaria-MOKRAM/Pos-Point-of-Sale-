package ma.jway.rms.dto.responses;

import ma.jway.rms.dto.enums.ItemCompositionStatus;

import java.util.List;

public record ItemResponse(
        Long id,
        String name,
        Double price,
        String description,
        Boolean saleStatus,
        Boolean purchaseStatus,
        Integer freeAddsOnLimit,
        Integer paidAddsOnLimit,
        ItemCompositionStatus compositionStatus,
        List<ItemResponse> compositions,
        CategoryResponse category,
        List<ResourceResponse> resources,
        List<AddOnResponse> addsOn,
        byte[] image) {
}
