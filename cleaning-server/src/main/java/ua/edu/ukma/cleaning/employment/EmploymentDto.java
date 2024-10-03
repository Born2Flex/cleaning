package ua.edu.ukma.cleaning.employment;

import lombok.Data;
import lombok.NoArgsConstructor;
import ua.edu.ukma.cleaning.user.dto.UserDto;

import java.time.LocalDateTime;

@NoArgsConstructor
@Data
public class EmploymentDto {
    private Long id;
    private UserDto applicant;
    private LocalDateTime creationTime;
}
