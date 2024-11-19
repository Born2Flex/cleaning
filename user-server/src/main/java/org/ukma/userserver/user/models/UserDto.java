package org.ukma.userserver.user.models;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.ukma.userserver.address.AddressDto;

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
//  Examples: (202)5550125, (202) 555-0125, (202)-555-0125
    @Pattern(regexp = "^((\\(\\d{3}\\))|\\d{3})[- .]?\\d{3}[- .]?\\d{4}$",
            message = "Phone number should be correct")
    private String phoneNumber;
    private List<AddressDto> addressList;
}
