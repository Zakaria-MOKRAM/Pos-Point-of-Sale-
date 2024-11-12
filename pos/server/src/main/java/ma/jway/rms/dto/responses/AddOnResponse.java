package ma.jway.rms.dto.responses;

import ma.jway.rms.dto.enums.AddsOnType;
import org.springframework.lang.Nullable;

public record AddOnResponse(
        Long id,
        AddsOnType type,
        Integer max,
        @Nullable ResourceResponse ingredient,
        @Nullable ItemResponse item 
) {
}
