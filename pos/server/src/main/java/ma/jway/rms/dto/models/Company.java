package ma.jway.rms.dto.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.sql.Blob;

@Data
@Entity
@Table(name = "companies")
@EqualsAndHashCode(callSuper = false)
public class Company extends BaseModel {
    @Lob
    @Column(name = "image")
    private Blob image;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
}