package ma.jway.rms.dto.models;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ma.jway.rms.dto.enums.DiningTableStatus;

import javax.persistence.*;

@Data
@Entity
@Table(name = "tables")
@EqualsAndHashCode(callSuper = false)
public class DiningTable extends BaseModel {
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "number", nullable = false)
    private Integer number;

    @Column(name = "chairs", nullable = false)
    private Integer chairs = 1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "area_id", nullable = false)
    private Area area;

    @Column(name = "status", nullable = false)
    private DiningTableStatus status = DiningTableStatus.AVAILABLE;

    @Column(name = "deleted", nullable = false)
    private Boolean deleted = false;
}