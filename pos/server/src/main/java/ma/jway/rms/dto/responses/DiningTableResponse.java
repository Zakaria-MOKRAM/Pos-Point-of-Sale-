package ma.jway.rms.dto.responses;

import ma.jway.rms.dto.enums.DiningTableStatus;

public record DiningTableResponse(
        Long id,
        Integer number,
        Integer chairs,
        DiningTableStatus status,
        DiningTableGeometryResponse geometry
) {
}
