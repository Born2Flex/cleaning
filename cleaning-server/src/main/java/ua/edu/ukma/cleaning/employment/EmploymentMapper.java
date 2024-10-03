package ua.edu.ukma.cleaning.employment;

import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface EmploymentMapper {
    @Mapping(target = "applicant", ignore = true)
    EmploymentDto toDto(EmploymentEntity entity);

    List<EmploymentDto> toDtoList(List<EmploymentEntity> entities);
}
