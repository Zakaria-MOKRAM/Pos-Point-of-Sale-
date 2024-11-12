package ma.jway.rms.dto.requests;

import ma.jway.rms.dto.enums.PaymentType;

public record PaymentRequest(
        Long orderId,
        PaymentType paymentType,
        Double amount
) {
}
