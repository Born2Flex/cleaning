package ua.edu.ukma.cleaning.commercial.proposal;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.Duration;

@Entity
@NoArgsConstructor
@Getter
@Setter
@SQLDelete(sql = "UPDATE commercial_proposals SET deleted = true WHERE id=?")
@SQLRestriction(value = "deleted=false")
@Table(name = "commercial_proposals")
public class CommercialProposalEntity {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", unique = true, nullable = false)
    private String name;

    @Column(name = "short_description", length = 100)
    private String shortDescription;

    @Column(name = "full_description", length = 500)
    private String fullDescription;

    @Column(name = "price", nullable = false)
    private Double price;

    @Temporal(TemporalType.TIME)
    @Column(name = "duration", nullable = false)
    private Duration time;

    @Column(name = "count_of_employee", nullable = false)
    private Integer requiredCountOfEmployees;

    @Column(name = "deleted")
    private Boolean deleted = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private ComercialProposalType type;
}
