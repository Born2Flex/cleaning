package org.ukma.notificationserver.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.ukma.notificationserver.address.AddressDto;
import org.ukma.notificationserver.user.Role;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserDto {
    private Long id;
    private String name;
    private String surname;
    private String patronymic;
    @NotNull(message = "Email cannot be null")
    @Email(message = "Email should be valid")
    private String email;
    @NotNull(message = "Role cannot be null")
    private Role role;
    @Pattern(regexp = "^((\\+38\\s?)?((\\(0[1-9]{2}\\))|(0[1-9]{2}))(\\s|-)?[0-9]{3}(\\s|-)?[0-9]{2}(\\s|-)?[0-9]{2})?$",
            message = "Phone number should be correct")
    private String phoneNumber;
    private List<AddressDto> addressList;
}
