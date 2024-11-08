package org.ukma.notificationserver.address;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddressDto {
    @NotNull(message = "City can't be null!")
    @NotBlank(message = "City can't be blank!")
    private String city;
    @NotNull(message = "Street can't be null!")
    @NotBlank(message = "Street can't be blank!")
    private String street;
    @NotNull(message = "HouseNumber can't be null!")
    @NotBlank(message = "HouseNumber can't be blank!")
    private String houseNumber;
    private String flatNumber;
    private String zip;
}
