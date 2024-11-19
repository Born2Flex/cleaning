package ua.edu.ukma.cleaning.commercial.proposal;

import java.util.List;

public interface CommercialProposalService {
    CommercialProposalDto create(CommercialProposalDto commercialProposal);
    CommercialProposalDto update(CommercialProposalDto commercialProposal);
    CommercialProposalDto getById(Long id);
    List<CommercialProposalDto> getAll();
    Boolean deleteById(Long id);
}
