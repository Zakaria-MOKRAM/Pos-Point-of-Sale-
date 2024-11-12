package ma.jway.rms.dto.models;

import javax.persistence.*;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@Table(name = "agents")
@EqualsAndHashCode(callSuper = false)
public class Agent extends BaseModel {
    @Column(name = "name", unique = true)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "tva_rate", nullable = false)
    private Double tvaRate;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "email")
    private String email;
}