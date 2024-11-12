package ma.jway.rms.dto.responses;

public record CategoryResponse(
                Long id,
                String name,
                String reference,
                String description,
                String iconPath) {

}