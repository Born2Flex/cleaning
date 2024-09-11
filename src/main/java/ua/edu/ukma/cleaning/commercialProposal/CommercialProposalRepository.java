package ua.edu.ukma.cleaning.commercialProposal;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommercialProposalRepository extends JpaRepository<CommercialProposalEntity, Long> {
    Optional<CommercialProposalEntity> findCommercialProposalEntityByName(String name);
}
