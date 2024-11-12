package ma.jway.rms.dto.models;


import javax.persistence.*;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ma.jway.rms.dto.enums.DiningTableShape;

@Data
@Entity
@Table(name = "tables_geometry")
@EqualsAndHashCode(callSuper = false)
public class DiningTableGeometry extends BaseModel {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "table_id", nullable = false)
    private DiningTable diningTable;

    @Column(name = "x", nullable = false)
    private Double x;

    @Column(name = "y", nullable = false)
    private Double y;

    @Column(name = "width", nullable = false)
    private Double width;

    @Column(name = "height", nullable = false)
    private Double height;

    @Column(name = "shape", nullable = false)
    private DiningTableShape shape;
}
