package ma.jway.rms.dto.responses;

import java.util.List;

public record AreaResponse(
        Long id,
        String name,
        String description,
        List<DiningTableResponse> tables) {

}