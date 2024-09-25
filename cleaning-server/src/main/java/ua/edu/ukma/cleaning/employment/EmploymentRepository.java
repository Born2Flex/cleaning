package ua.edu.ukma.cleaning.employment;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmploymentRepository extends JpaRepository<EmploymentEntity, Long> {
    Optional<EmploymentEntity> findByApplicant_Id(Long applicantId);
}
