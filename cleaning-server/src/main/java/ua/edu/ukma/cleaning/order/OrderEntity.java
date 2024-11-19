package ua.edu.ukma.cleaning.order;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Cascade;
import ua.edu.ukma.cleaning.address.AddressDto;
import ua.edu.ukma.cleaning.commercial.proposal.CommercialProposalEntity;
import ua.edu.ukma.cleaning.order.review.ReviewEntity;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "orders")
public class OrderEntity {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "price", nullable = false)
    private Double price;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "order_time", nullable = false)
    private LocalDateTime orderTime;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "creation_time", nullable = false)
    private LocalDateTime creationTime;

    @Column(name = "client_email")
    private String clientEmail;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "order_executor", joinColumns = @JoinColumn(name = "order_id"))
    @Column(name = "executors")
    private Set<Long> executors;

    @Column(name = "comment", length = 500)
    private String comment;

    @Convert(converter = AddressConverter.class)
    @Column(name = "address")
    private AddressDto address;

    @OneToOne()
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    @JoinColumn(name = "review", referencedColumnName = "id")
    private ReviewEntity review;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status;

    @Column(name = "duration", nullable = false)
    private Duration duration;

    @ElementCollection(fetch = FetchType.EAGER)
    @MapKeyJoinColumn(name = "commercial_proposal_id")
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    @CollectionTable(name = "order_commercial_proposals_mapping", joinColumns = @JoinColumn(name = "order_id"))
    @Column(name = "quantity")
    private Map<CommercialProposalEntity, Integer> commercialProposals;
}
