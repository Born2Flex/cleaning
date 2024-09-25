package ua.edu.ukma.cleaning.user.address;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ua.edu.ukma.cleaning.utils.exceptionHandler.exceptions.AccessDeniedException;
import ua.edu.ukma.cleaning.utils.exceptionHandler.exceptions.NoSuchEntityException;
import ua.edu.ukma.cleaning.user.security.SecurityContextAccessor;

import java.util.List;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepository;
    private final AddressMapper addressMapper;

    @Override
    public AddressDto create(AddressDto addressDto) {
        AddressEntity addressEntity = addressMapper.toEntity(addressDto);
        addressEntity.setUser(SecurityContextAccessor.getAuthenticatedUser());
        log.info("Created new address with id = {}",addressEntity.getId());
        return addressMapper.toDto(addressRepository.save(addressEntity));
    }

    @Transactional
    @Override
    public AddressDto update(AddressDto addressDto) {
        AddressEntity addressEntity = addressRepository.findById(addressDto.getId()).orElseThrow(() ->
        {
            log.info("Can`t find address by id = {}", addressDto.getId());
            return new NoSuchEntityException("Can`t find address by id: " + addressDto.getId());
        });
        addressRepository.delete(addressEntity);
        AddressEntity newAddress = addressMapper.toEntity(addressDto);
        newAddress.setUser(SecurityContextAccessor.getAuthenticatedUser());
        log.debug("Address updated: " + addressEntity);
        return addressMapper.toDto(addressRepository.save(newAddress));
    }

    @Override
    public Boolean deleteById(Long id) {
        AddressEntity addressEntity = addressRepository.findById(id).orElseThrow(() ->
        {
            log.info("Can`t find address by id = {}", id);
            return new NoSuchEntityException("Can`t find address by id: " + id);
        });
        if (addressEntity != null && !Objects.equals(addressEntity.getUserId(),
                SecurityContextAccessor.getAuthenticatedUserId())) {
            log.info("User id = {} try to delete address of user id = {}",
                    SecurityContextAccessor.getAuthenticatedUserId(), addressEntity.getUserId());
            throw new AccessDeniedException("Access denied");
        }
        addressRepository.deleteById(id);
        log.info("Address with id = {} was deleted", id);
        return true;
    }

    @Override
    public AddressDto getById(Long id) {
        AddressEntity addressEntity = addressRepository.findById(id).orElseThrow(() ->
        {
            log.info("Can`t find address by id = {}", id);
            return new NoSuchEntityException("Can`t find address by id: " + id);
        });
        return addressMapper.toDto(addressEntity);
    }

    @Override
    public List<AddressDto> getUserAddresses() {
        return addressMapper.toListDto(addressRepository
                .findAddressEntitiesByUserId(SecurityContextAccessor.getAuthenticatedUserId()));
    }
}
