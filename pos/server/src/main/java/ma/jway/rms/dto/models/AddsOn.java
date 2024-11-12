package ma.jway.rms.dto.models;


import javax.persistence.Table;
import javax.persistence.*;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ma.jway.rms.dto.enums.AddsOnType;

@Data
@Entity
@Table(name = "adds_on")
@EqualsAndHashCode(callSuper = false)
public class AddsOn extends BaseModel {
    @Column(name = "type", nullable = false)
    private AddsOnType addsOnType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_item", nullable = false)
    private Item parentItem;

    @Column(name = "adds_on_item", nullable = false)
    private Long addsOnItem;

    @Column(name = "max", nullable = false)
    private Integer max;
}
