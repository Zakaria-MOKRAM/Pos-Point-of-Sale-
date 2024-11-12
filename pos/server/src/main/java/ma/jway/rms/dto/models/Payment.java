package ma.jway.rms.dto.models;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ma.jway.rms.dto.enums.PaymentType;

@Data
@Entity
@Table(name = "payments")
@EqualsAndHashCode(callSuper = false)
public class Payment extends BaseModel {
    @Column(name = "payment_type", nullable = false)
    private PaymentType paymentType;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(name = "amount", nullable = false)
    private Double amount;
}
