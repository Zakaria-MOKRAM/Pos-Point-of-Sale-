package ma.jway.rms.dto.models;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ma.jway.rms.dto.enums.DiscountType;
import ma.jway.rms.dto.enums.OrderStatus;
import ma.jway.rms.dto.enums.OrderType;

import java.util.List;
import java.util.Optional;

@Data
@Entity
@Table(name = "orders")
@EqualsAndHashCode(callSuper = false)
public class Order extends BaseModel {
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems;

    @Column(name = "status")
    private OrderStatus status;

    @Column(name = "sub_total")
    private Double subTotal;

    @Column(name = "discount_type")
    private DiscountType discountType;

    @Column(name = "discount")
    private Double discountAmount;

    @Column(name = "total_payable")
    private Double totalAfterDiscount;

    @Column(name = "tva_percentage")
    private Double vatPercentage;

    @Column(name = "tva_amount")
    private Double vatAmount;

    @Column(name = "ttc")
    private Double totalCost;

    @Column(name = "order_type")
    private OrderType orderType;

    @Column(name = "guests")
    private Integer guests = 1;

    @Column(name = "overdue")
    private Boolean overdue = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "table_id")
    @JsonIgnore
    private DiningTable diningTable;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "order")
    private List<Payment> payments;

    public void calculateTotals() {
        totalAfterDiscount = subTotal - (discountType == DiscountType.PERCENTAGE
                ? subTotal * (discountAmount != null ? discountAmount / 100.0 : 0.0)
                : (discountAmount != null ? discountAmount : 0.0));

        vatAmount = Optional.ofNullable(vatPercentage)
                .map(v -> totalAfterDiscount * v / 100.0)
                .orElse(0.0);

        totalCost = totalAfterDiscount + vatAmount;
    }
}