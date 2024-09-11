package ua.edu.ukma.cleaning.notification;


import ua.edu.ukma.cleaning.order.OrderEntity;

public interface NotificationService {
    Long create(OrderEntity order);

    void sendNotifications();
}
