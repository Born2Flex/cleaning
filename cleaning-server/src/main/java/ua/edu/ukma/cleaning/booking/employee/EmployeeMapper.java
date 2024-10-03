package ua.edu.ukma.cleaning.booking.employee;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import ua.edu.ukma.cleaning.user.EmployeeDto;
import ua.edu.ukma.cleaning.user.dto.UserListDto;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface EmployeeMapper {
    EmployeeDto toEmployee(UserListDto userListDto);

    List<EmployeeDto> toEmployeeList(List<UserListDto> userListDtoList);
}
