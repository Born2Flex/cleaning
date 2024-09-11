package ua.edu.ukma.cleaning.employment;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Mappings;
import ua.edu.ukma.cleaning.user.UserMapper;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = {UserMapper.class})
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
