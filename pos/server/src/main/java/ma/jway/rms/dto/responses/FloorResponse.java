package ma.jway.rms.dto.responses;

import java.util.List;

public record FloorResponse(
        Long id,
        String name,
        String reference,
        String description,
        List<AreaResponse> areas) {

}