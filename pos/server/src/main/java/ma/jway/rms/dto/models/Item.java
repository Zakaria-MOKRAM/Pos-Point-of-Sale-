package ma.jway.rms.dto.models;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ma.jway.rms.dto.enums.ItemCompositionStatus;

import java.sql.Blob;

@Data
@Entity
@Table(name = "items")
@EqualsAndHashCode(callSuper = false)
public class Item extends BaseModel {

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "price", nullable = false)
    private Double price;

    @Lob
    @Column(name = "image")
    @JsonIgnore
    private Blob image;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(name = "purchase_status", nullable = false)
    private Boolean purchaseStatus;

    @Column(name = "sale_status", nullable = false)
    private Boolean saleStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "composition_status", nullable = false)
    private ItemCompositionStatus compositionStatus;

    @Column(name = "free_adds_on_limit")
    private Integer freeAddsOnLimit; // Maximum number of free add-ons for this item

    @Column(name = "paid_adds_on_limit")
    private Integer paidAddsOnLimit; // Maximum number of paid add-ons for this item
}
