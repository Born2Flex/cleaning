package ua.edu.ukma.cleaning.order.review;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;
import org.springframework.http.MediaType;
import ua.edu.ukma.cleaning.storage.Storageable;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ReviewDto implements Storageable {
    @NotNull(message = "Order id of review can't be null")
    private Long orderId;
    @Range(min = 1, max = 5, message = "Cleaning rate should be in range from 1 to 5")
    private Long cleaningRate;
    @Range(min = 1, max = 5, message = "Employee rate should be in range from 1 to 5")
    private Long employeeRate;
    private String details;

    @JsonIgnore
    @Override
    public String getDir() {
        return "review";
    }

    @JsonIgnore
    @Override
    public Long getId() {
        return this.orderId;
    }

    @JsonIgnore
    @Override
    public List<String> getAllowedFileTypes() {
        return List.of(MediaType.IMAGE_JPEG_VALUE);
    }
}
