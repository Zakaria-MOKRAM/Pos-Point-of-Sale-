package ma.jway.rms.dto.models;

import javax.persistence.*;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ma.jway.rms.dto.enums.CustomizationType;
import ma.jway.rms.dto.enums.ItemType;

@Data
@Entity
@Table(name = "customizations")
@EqualsAndHashCode(callSuper = false)
public class Customization extends BaseModel {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_item_id", nullable = false)
    private OrderItem orderItem;

    @Column(name = "customization_type", nullable = false)
    private CustomizationType customizationType;

    @Column(name = "quantity", nullable = false)
    private Double quantity;

    @Column(name = "cost")
    private Double cost;

    @Column(name = "item_type", nullable = false)
    private ItemType itemType;

    @Column(name = "customized_item", nullable = false)
    private Long customizedItem;

}
