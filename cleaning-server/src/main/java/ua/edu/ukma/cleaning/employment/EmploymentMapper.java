package ua.edu.ukma.cleaning.employment;

import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface EmploymentMapper {
    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "applicantId", ignore = true),
            @Mapping(target = "creationTime", expression = "java(java.time.LocalDateTime.now())"),
    })
    EmploymentEntity toEntity(String motivationList);

    @Mapping(target = "applicant", ignore = true)
    EmploymentDto toDto(EmploymentEntity entity);

    List<EmploymentDto> toDtoList(List<EmploymentEntity> entities);
}
