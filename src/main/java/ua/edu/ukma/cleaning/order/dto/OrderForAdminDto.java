package ua.edu.ukma.cleaning.order.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ua.edu.ukma.cleaning.user.address.AddressDto;
import ua.edu.ukma.cleaning.order.Status;
import ua.edu.ukma.cleaning.order.review.ReviewDto;
import ua.edu.ukma.cleaning.user.dto.EmployeeDto;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class OrderForAdminDto {
    @NotNull(message = "Order id can't be null")
    private Long id;
    @Positive(message = "Price of order should be positive")
    private Double price;
    private LocalDateTime orderTime;
    private AddressDto address;
    @NotNull(message = "Order status can't be null")
    private Status status;
    @NotNull(message = "Order duration can't be null")
    private Duration duration;
    private ReviewDto review;
    private Map<String, Integer> commercialProposals;
    @NotNull(message = "Order executors can't be null")
    @Size(min = 1, message = "Order should have at least 1 executor")
    private List<EmployeeDto> executors;
}
