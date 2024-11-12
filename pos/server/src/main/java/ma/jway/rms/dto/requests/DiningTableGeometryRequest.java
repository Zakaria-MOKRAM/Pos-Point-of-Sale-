package ma.jway.rms.dto.requests;

import ma.jway.rms.dto.enums.DiningTableShape;

public record DiningTableGeometryRequest(
        Double x,
        Double y,
        Double width,
        Double height,
        DiningTableShape shape
) {

}
