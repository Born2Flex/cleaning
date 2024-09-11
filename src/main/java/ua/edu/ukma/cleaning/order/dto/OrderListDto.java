package ua.edu.ukma.cleaning.order.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ua.edu.ukma.cleaning.user.address.AddressDto;
import ua.edu.ukma.cleaning.order.Status;
import ua.edu.ukma.cleaning.order.review.ReviewDto;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class OrderListDto {
    private Long id;
    private Double price;
    private LocalDateTime orderTime;
    private AddressDto address;
    private Status status;
    private ReviewDto review;
}
