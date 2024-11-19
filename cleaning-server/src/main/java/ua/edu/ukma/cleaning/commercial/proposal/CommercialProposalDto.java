package ua.edu.ukma.cleaning.commercial.proposal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Duration;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class CommercialProposalDto {
    private Long id;
    @NotNull(message = "Proposal name can't be null")
    @NotBlank(message = "Proposal name can't be blank")
    private String name;
    @NotNull(message = "Short description can't be null")
    @NotBlank(message = "Short description can't be null")
    private String shortDescription;
    @NotNull(message = "Description can't be null")
    @NotBlank(message = "Description can't be null")
    private String fullDescription;
    @NotNull(message = "Number of employees can't be null")
    @NotBlank(message = "Number of employees can't be blank")
    private String requiredCountOfEmployees;
    @NotNull(message = "Price can't be null")
    @Positive(message = "Price must be positive number")
    private Double price;
    @NotNull(message = "Duration of proposal can't be null")
    private Duration time;
    @NotNull(message = "Commercial proposal type can't be null")
    private ComercialProposalType type;
}
