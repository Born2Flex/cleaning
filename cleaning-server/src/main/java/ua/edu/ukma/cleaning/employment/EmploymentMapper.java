package ua.edu.ukma.cleaning.employment;

import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = {UserMapper.class}, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface EmploymentMapper {
    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "applicant", ignore = true),
            @Mapping(target = "creationTime", expression = "java(java.time.LocalDateTime.now())"),
            @Mapping(target = "motivationList", source = "motivationList")
    })
    EmploymentEntity toEntity(String motivationList);

    EmploymentDto toDto(EmploymentEntity entity);

    List<EmploymentDto> toDtoList(List<EmploymentEntity> entities);
}
