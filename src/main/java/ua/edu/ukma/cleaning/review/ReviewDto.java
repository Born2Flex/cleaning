package ua.edu.ukma.cleaning.review;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ReviewDto {
    @NotNull(message = "Order id of review can't be null")
    private Long orderId;
    @Range(min = 1, max = 5, message = "Cleaning rate should be in range from 1 to 5")
    private Long cleaningRate;
    @Range(min = 1, max = 5, message = "Employee rate should be in range from 1 to 5")
    private Long employeeRate;
    private String details;
}
