package ua.edu.ukma.cleaning.address;

import java.util.List;

public interface AddressService {
    AddressDto create(AddressDto addressDto);
    AddressDto update(AddressDto addressDto);
    Boolean deleteById(Long id);
    AddressDto getById(Long id);
    List<AddressDto> getUserAddresses();
}
