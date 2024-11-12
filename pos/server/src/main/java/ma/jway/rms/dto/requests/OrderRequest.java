package ma.jway.rms.dto.requests;

import ma.jway.rms.dto.enums.DiscountType;
import ma.jway.rms.dto.enums.OrderType;
import org.springframework.lang.Nullable;

import java.util.List;

public record OrderRequest(
        Long user,
        List<ItemRequest> items,
        OrderType orderType,
        @Nullable Double discount,
        @Nullable DiscountType discountType,
        @Nullable Integer guests,
        @Nullable Long diningTable,
        @Nullable Double tvaRate) {
}