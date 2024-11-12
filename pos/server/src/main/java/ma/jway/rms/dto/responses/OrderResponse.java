package ma.jway.rms.dto.responses;

import ma.jway.rms.dto.enums.DiscountType;
import ma.jway.rms.dto.enums.OrderStatus;
import ma.jway.rms.dto.enums.OrderType;

import java.time.LocalDateTime;
import java.util.List;

public record OrderResponse(
        Long id,
        Double discount,
        DiscountType DiscountType,
        Integer guests,
        OrderType orderType,
        OrderStatus orderStatus,
        Double totalPrice,
        LocalDateTime createdAt,
        List<OrderItemResponse> orderItemResponses) {
}
