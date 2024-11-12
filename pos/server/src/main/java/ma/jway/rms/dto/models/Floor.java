package ma.jway.rms.dto.models;

import javax.persistence.*;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@Entity
@Table(name = "floors")
@EqualsAndHashCode(callSuper = false)
public class Floor extends BaseModel {
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "reference", nullable = false)
    private String reference;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @OneToMany(mappedBy = "floor", cascade = CascadeType.ALL)
    private List<Area> areas;
}
