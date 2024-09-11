package ua.edu.ukma.cleaning.commercialProposal;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ua.edu.ukma.cleaning.utils.exceptions.NoSuchEntityException;
import ua.edu.ukma.cleaning.utils.exceptions.ProposalNameDuplicateException;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommercialProposalServiceImpl implements CommercialProposalService {
    private final CommercialProposalRepository commercialProposalRepository;
    private final CommercialProposalMapper mapper;

    @Override
    public CommercialProposalDto create(CommercialProposalDto commercialProposal) {
        if (commercialProposalRepository.findCommercialProposalEntityByName(commercialProposal.getName()).isPresent()) {
            log.info("Name of commercial proposal should be unique");
            throw new ProposalNameDuplicateException("Commercial proposal name should be unique!");
        }
        log.info("Created commercial proposal with id = {}", commercialProposal.getId());
        return mapper.toDto(commercialProposalRepository.save(mapper.toEntity(commercialProposal)));
    }

    @Override
    public CommercialProposalDto update(CommercialProposalDto commercialProposal) {
        CommercialProposalEntity proposal = commercialProposalRepository.findById(commercialProposal.getId()).orElseThrow(() -> {
            log.info("Can`t find proposal by id: " + commercialProposal.getId());
            throw new NoSuchEntityException("Can`t find proposal by id: " + commercialProposal.getId());
        });
        if (!proposal.getName().equals(commercialProposal.getName())
                && commercialProposalRepository.findCommercialProposalEntityByName(commercialProposal.getName()).isPresent() ) {
            log.info("Same commercial proposals name, when update proposal with name: {}", commercialProposal.getName());
            throw new ProposalNameDuplicateException("Commercial proposal can`t be edited!");
        }
        log.debug("Commercial proposal with id = {} successfully updated", commercialProposal.getId());
        return mapper.toDto(commercialProposalRepository.save(mapper.toEntity(commercialProposal)));
    }

    @Override
    public CommercialProposalDto getById(Long id) {
        CommercialProposalEntity entity = commercialProposalRepository.findById(id).orElseThrow(
                () -> new NoSuchEntityException("Can't find proposal with id: " + id)
        );
        return mapper.toDto(entity);
    }

    @Override
    public List<CommercialProposalDto> getAll() {
        return mapper.toDtoList(commercialProposalRepository.findAll());
    }

    @Override
    public Boolean deleteById(Long id) {
        commercialProposalRepository.deleteById(id);
        log.info("Commercial proposal with id = {} was successfully deleted", id);
        return true;
    }
}
