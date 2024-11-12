package ma.jway.rms.dto.models;

import javax.persistence.*;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@Entity
@Table(name = "stations")
@EqualsAndHashCode(callSuper = false)
public class Station extends BaseModel {
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "reference", nullable = false, unique = true)
    private String reference;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @OneToMany(mappedBy = "station", cascade = CascadeType.ALL)
    private List<Category> categories;
}
