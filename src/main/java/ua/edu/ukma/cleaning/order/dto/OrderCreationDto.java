package ua.edu.ukma.cleaning.order.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ua.edu.ukma.cleaning.address.AddressDto;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class OrderCreationDto {
    @Positive(message = "Price of order should be positive")
    private Double price;
    @NotNull(message = "Order time can't be null")
    private LocalDateTime orderTime;
    @NotNull(message = "Client id can't be null")
    private Long clientId;
    private String comment;
    @NotNull(message = "Address of order can't be null")
    private AddressDto address;
    @NotNull(message = "Duration of order can't be null")
    private Duration duration;
    @NotNull(message = "Order can't be null")
    private Map<Long, Integer> proposals;
}
