package ua.edu.ukma.cleaning.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ua.edu.ukma.cleaning.user.Role;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserListDto {
    private Long id;
    private String name;
    private String surname;
    private String patronymic;
    private String email;
    private Role role;
}
