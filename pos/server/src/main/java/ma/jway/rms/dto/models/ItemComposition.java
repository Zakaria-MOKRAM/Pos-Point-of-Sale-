package ma.jway.rms.dto.models;

import javax.persistence.*;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@Table(name = "item_composition")
@EqualsAndHashCode(callSuper = false)
public class ItemComposition extends BaseModel {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_item", nullable = false)
    private Item parentItem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sub_item", nullable = false)
    private Item subItem;

    @Column(name = "quantity", nullable = false)
    private Integer quantity = 1;
}
