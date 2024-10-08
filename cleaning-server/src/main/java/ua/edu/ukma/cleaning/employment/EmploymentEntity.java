package ua.edu.ukma.cleaning.employment;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.MediaType;
import ua.edu.ukma.cleaning.storage.Storageable;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@NoArgsConstructor
@Getter
@Setter
@Table(name = "employment")
public class EmploymentEntity implements Storageable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "applicant_id")
    private Long applicantId;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "creation_time", nullable = false)
    private LocalDateTime creationTime;

    @Override
    public String getDir() {
        return "employment";
    }
    @Override
    public List<String> getAllowedFileTypes() {
        return List.of(MediaType.APPLICATION_PDF_VALUE);
    }
}
