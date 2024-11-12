package ma.jway.rms.dto.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@Table(name = "resources")
@EqualsAndHashCode(callSuper = false)
public class Resource extends BaseModel {
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "unit_of_measure")
    private String unitOfMeasure;

    @Column(name = "is_item", nullable = false)
    private boolean isItem;  // Indicates if this resource is also considered as an item
}
