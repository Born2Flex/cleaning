package ua.edu.ukma.cleaning.user.address;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringExclude;
import org.hibernate.annotations.SQLDelete;
import ua.edu.ukma.cleaning.user.UserEntity;

@Entity
@NoArgsConstructor
@Getter
@Setter
@SQLDelete(sql = "UPDATE addresses SET user_id = null WHERE id=?")
@Table(name = "addresses")
public class AddressEntity {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "city", nullable = false)
    private String city;

    @Column(name = "street", nullable = false)
    private String street;

    @Column(name = "house_number", nullable = false)
    private String houseNumber;

    @Column(name = "flat_number")
    private String flatNumber;

    @Column(name = "zip")
    private String zip;

    @ToStringExclude
    @ManyToOne
    @JoinColumn(name="user_id")
    private UserEntity user;

    @Column(name = "user_id", insertable=false, updatable=false)
    private Long userId;
}
