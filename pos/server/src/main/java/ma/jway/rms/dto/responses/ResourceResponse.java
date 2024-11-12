package ma.jway.rms.dto.responses;

import org.springframework.lang.Nullable;

public record ResourceResponse(
        Long id,
        String name,
        String description,
        String unitOfMeasure,
        boolean isItem,
        @Nullable boolean mandatory,
        @Nullable Double amount
) {
}
