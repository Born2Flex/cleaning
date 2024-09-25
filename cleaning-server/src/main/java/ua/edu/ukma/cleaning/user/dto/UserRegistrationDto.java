package ua.edu.ukma.cleaning.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserRegistrationDto {
    private String name;
    private String surname;
    private String patronymic;
    @NotNull(message = "Password cannot be null")
    @Size(min = 12, message = "Password is too short!")
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d).+$", message = "Password must contain at least one uppercase letter, one lowercase letter, and one digit")
    private String password;
    @NotNull(message = "Email cannot be null")
    @Email(message = "Email should be valid")
    private String email;
}
