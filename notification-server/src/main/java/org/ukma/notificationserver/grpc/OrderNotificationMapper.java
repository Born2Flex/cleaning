package org.ukma.notificationserver.grpc;

import notification.NotificationResponse;
import org.mapstruct.*;
import org.ukma.notificationserver.models.OrderNotification;
import org.ukma.notificationserver.models.OrderNotificationType;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, injectionStrategy = InjectionStrategy.CONSTRUCTOR, uses = TimestampMapper.class)
public interface OrderNotificationMapper {

    @Mapping(target = "type", source = "type", qualifiedByName = "mapNotificationType")
    @Mapping(target = "email", source = "email")
    @Mapping(target = "orderId", source = "orderId")
    @Mapping(target = "orderTime", source = "orderTime")
    @Mapping(target = "creationTime", source = "creationTime")
    OrderNotification toNotificationResponse(NotificationResponse notificationResponse);

    @Named("mapNotificationType")
    default OrderNotificationType mapNotificationType(NotificationResponse.OrderNotificationType notificationType) {
        return OrderNotificationType.valueOf(notificationType.name());
    }
}
