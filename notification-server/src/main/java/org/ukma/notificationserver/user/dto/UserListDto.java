package org.ukma.notificationserver.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.ukma.notificationserver.user.Role;

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
