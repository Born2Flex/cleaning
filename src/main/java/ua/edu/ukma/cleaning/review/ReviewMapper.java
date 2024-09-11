package ua.edu.ukma.cleaning.review;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ReviewMapper {
    @Mapping(target = "orderId", source = "id")
    ReviewDto toDto(ReviewEntity review);

    ReviewEntity toEntity(ReviewDto reviewDto);
}
