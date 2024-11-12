package ma.jway.rms.dto.requests;

import org.springframework.lang.Nullable;

import ma.jway.rms.dto.enums.CustomizationType;
import ma.jway.rms.dto.enums.ItemType;

public record ItemCustomizationsRequest(
                Long id,
                CustomizationType customizationType,
                ItemType itemType,
                Double quantity,
                @Nullable Double cost) {
}
