package ma.jway.rms.dto.responses;

import ma.jway.rms.dto.enums.RoleType;

public record AvailableUsersResponse(
        String firstname,
        String lastname,
        String username,
        RoleType role) {
}
