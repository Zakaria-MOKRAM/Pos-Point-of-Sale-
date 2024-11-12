package ma.jway.rms.dto.requests;

import java.util.List;

import org.springframework.lang.Nullable;

public record ItemRequest(
                Long id,
                Double totalPrice,
                @Nullable List<ItemCustomizationsRequest> customizations) {
}