package ua.edu.ukma.cleaning.grpc;

import notification.NotificationResponse;
import org.mapstruct.*;
import ua.edu.ukma.cleaning.order.dto.OrderListDto;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, injectionStrategy = InjectionStrategy.CONSTRUCTOR, uses = TimestampMapper.class)
public interface NotificationResponseMapper {

    @Mapping(target = "type", expression = "java(notification.NotificationResponse.OrderNotificationType.REMINDING)")
    @Mapping(target = "email", source = "clientEmail")
    @Mapping(target = "orderId", source = "id")
    @Mapping(target = "orderTime", source = "orderTime")
    @Mapping(target = "creationTime", source = "creationTime")
    NotificationResponse toNotificationResponse(OrderListDto order);

}
