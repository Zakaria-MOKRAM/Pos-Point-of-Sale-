package ma.jway.rms.dto.requests;

import org.springframework.lang.Nullable;

public record UpdateDiningTableRequest(
        Long id,
        Integer number,
        Integer chairs,
        @Nullable Double x,
        @Nullable Double y
) {
}
