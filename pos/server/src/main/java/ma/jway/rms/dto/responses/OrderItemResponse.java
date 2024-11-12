package ma.jway.rms.dto.responses;

import java.util.List;

import ma.jway.rms.dto.models.Customization;
import ma.jway.rms.dto.models.Item;

public record OrderItemResponse(
        Long id,
        Integer quantity,
        Boolean customized,
        Item item,
        List<Customization> customizations) {
}
