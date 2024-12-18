package ua.edu.ukma.cleaning.order;

import org.mapstruct.*;
import ua.edu.ukma.cleaning.commercial.proposal.CommercialProposalEntity;
import ua.edu.ukma.cleaning.order.dto.OrderCreationDto;
import ua.edu.ukma.cleaning.order.dto.OrderForAdminDto;
import ua.edu.ukma.cleaning.order.dto.OrderForUserDto;
import ua.edu.ukma.cleaning.order.dto.OrderListDto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface OrderMapper {
    @Mapping(target = "creationTime", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "status", expression = "java(ua.edu.ukma.cleaning.order.Status.NOT_VERIFIED)")
    OrderEntity toEntity(OrderCreationDto order);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "commercialProposals", ignore = true)
    @Mapping(target = "price", ignore = true)
    @Mapping(target = "orderTime", ignore = true)
    @Mapping(target = "address", ignore = true)
    @Mapping(target = "review", ignore = true)
    void updateFields(@MappingTarget OrderEntity entity, OrderForAdminDto order);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "price", ignore = true)
    @Mapping(target = "address", ignore = true)
    @Mapping(target = "duration", ignore = true)
    @Mapping(target = "commercialProposals", ignore = true)
    @Mapping(target = "status", expression = "java(ua.edu.ukma.cleaning.order.Status.NOT_VERIFIED)")
    void updateFields(@MappingTarget OrderEntity entity, OrderForUserDto order);

    @Mapping(target = "commercialProposals", source = "commercialProposals", qualifiedByName = "mapCommercialProposals")
    OrderForAdminDto toAdminDto(OrderEntity entity);

    @Mapping(target = "commercialProposals", source = "commercialProposals", qualifiedByName = "mapCommercialProposals")
    OrderForUserDto toUserDto(OrderEntity entity);

    List<OrderListDto> toListDto(List<OrderEntity> entity);

    OrderListDto toListDto(OrderEntity entity);

    @Named("mapCommercialProposals")
    default Map<String, Integer> mapCommercialProposals(Map<CommercialProposalEntity, Integer> commercialProposals) {
        Map<String, Integer> result = new HashMap<>();
        for (Map.Entry<CommercialProposalEntity, Integer> entry : commercialProposals.entrySet()) {
            String name = entry.getKey().getName();
            Integer value = entry.getValue();
            result.put(name, value);
        }
        return result;
    }
}
