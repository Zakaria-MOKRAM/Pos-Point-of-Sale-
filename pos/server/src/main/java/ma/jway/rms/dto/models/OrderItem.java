package ma.jway.rms.dto.models;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@Entity
@Table(name = "order_items")
@EqualsAndHashCode(callSuper = false)
public class OrderItem extends BaseModel {
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    @JsonIgnore
    @OneToMany(mappedBy = "orderItem", cascade = CascadeType.ALL)
    private List<Customization> customizations;

    @Column(name = "total_price", nullable = false)
    private Double totalPrice;

    @Column(name = "printed", nullable = false)
    private Boolean printed = false;
}
