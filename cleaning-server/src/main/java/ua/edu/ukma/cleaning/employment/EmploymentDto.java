package ua.edu.ukma.cleaning.employment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import ua.edu.ukma.cleaning.user.EmployeeDto;

import java.time.LocalDateTime;

@NoArgsConstructor
@Data
public class EmploymentDto {
    private Long id;
    private EmployeeDto applicant;
    private LocalDateTime creationTime;
    @NotBlank(message = "List can`t be blank")
    @Size(max = 1000, message = "List can`t be more than 1000 characters")
    private String motivationList;
}
