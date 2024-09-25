package ua.edu.ukma.cleaning.user.address;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/addresses")
@RequiredArgsConstructor
@Tag(name = "Address API", description = "Endpoint for operations with addresses")
public class AddressController {
    public final AddressService addressService;

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @Operation(summary = "Get address by id", description = "Get address by id")
    @GetMapping("/{id}")
    public AddressDto getAddress(@PathVariable Long id) {
        return addressService.getById(id);
    }

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @Operation(summary = "Get all user addresses", description = "Get all user addresses")
    @GetMapping("/by-user")
    public List<AddressDto> getUserAddresses() {
        return addressService.getUserAddresses();
    }

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @Operation(summary = "Change address", description = "Change address")
    @PutMapping()
    public AddressDto editAddress(@Valid @RequestBody AddressDto addressDto) {
        return addressService.update(addressDto);
    }

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @Operation(summary = "Create new address for user", description = "Create new address for user")
    @PostMapping()
    public AddressDto createAddress(@Valid @RequestBody AddressDto addressDto) {
        return addressService.create(addressDto);
    }

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @Operation(summary = "Delete address", description = "Delete address")
    @DeleteMapping("/{id}")
    public Boolean deleteAddress(@PathVariable Long id) {
        return addressService.deleteById(id);
    }
}
