package ua.edu.ukma.cleaning.order.review;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "reviews")
public class ReviewEntity {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "cleaning_rate", nullable = false)
    private Long cleaningRate;

    @Column(name = "employee_rate", nullable = false)
    private Long employeeRate;

    @Column(name = "details", length = 700)
    private String details;
}
