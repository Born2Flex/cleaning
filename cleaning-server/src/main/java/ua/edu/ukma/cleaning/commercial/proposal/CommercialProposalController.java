package ua.edu.ukma.cleaning.commercial.proposal;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/commercial-proposals")
@RequiredArgsConstructor
@Tag(name = "Commercial Proposal API", description = "Endpoint for operations with commercial proposal")
public class CommercialProposalController {
    private final CommercialProposalService commercialProposalService;

    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get proposal by id", description = "Get proposal by id")
    @GetMapping("/{id}")
    public CommercialProposalDto getProposal(@PathVariable Long id) {
        return commercialProposalService.getById(id);
    }

    @PreAuthorize("permitAll()")
    @Operation(summary = "Get all proposals", description = "Get all proposals")
    @GetMapping
    public List<CommercialProposalDto> getAllProposals() {
        return commercialProposalService.getAll();
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @Operation(summary = "Edit proposal", description = "Edit proposal")
    @PutMapping
    public CommercialProposalDto editProposal(@Valid @RequestBody CommercialProposalDto proposalDto) {
        return commercialProposalService.update(proposalDto);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @Operation(summary = "Create proposal", description = "Create proposal")
    @PostMapping
    public CommercialProposalDto createProposal(@Valid @RequestBody CommercialProposalDto proposalDto) {
        return commercialProposalService.create(proposalDto);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @Operation(summary = "Delete commercial proposal", description = "Delete commercial proposal(soft delete)")
    @DeleteMapping("/{id}")
    public Boolean deleteProposal(@PathVariable Long id) {
        return commercialProposalService.deleteById(id);
    }
}
