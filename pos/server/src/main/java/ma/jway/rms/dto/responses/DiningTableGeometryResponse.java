package ma.jway.rms.dto.responses;

import ma.jway.rms.dto.enums.DiningTableShape;

public record DiningTableGeometryResponse(
        Double x,
        Double y,
        Double width,
        Double height,
        DiningTableShape shape
) {
}
