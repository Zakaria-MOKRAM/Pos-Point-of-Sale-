package ma.jway.rms.dto.models;

import javax.persistence.*;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@Table(name = "icons")
@EqualsAndHashCode(callSuper = false)
public class Icon extends BaseModel {
    @Column(name = "label")
    private String label;

    @Column(name = "filename", nullable = false)
    private String filename;

    @Column(name = "path")
    private String path;

    @Column(name = "tag", nullable = false)
    private String tag;
}