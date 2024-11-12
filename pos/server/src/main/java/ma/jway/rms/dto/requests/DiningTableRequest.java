package ma.jway.rms.dto.requests;

public record DiningTableRequest(
        Integer number,
        Integer chairs,
        Long area,
        DiningTableGeometryRequest geometry
) {
}
