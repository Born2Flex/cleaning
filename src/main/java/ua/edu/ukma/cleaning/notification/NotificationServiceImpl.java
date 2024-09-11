package ua.edu.ukma.cleaning.notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import ua.edu.ukma.cleaning.order.OrderEntity;
import ua.edu.ukma.cleaning.order.OrderEvent;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    private final NotificationRepository repository;

    @Override
    public Long create(OrderEntity order) {
        NotificationEntity notification = new NotificationEntity();
        notification.setOrder(order);
        return repository.save(notification).getId();
    }

    @Override
    public void sendNotifications() {
        List<NotificationEntity> notificationsToSend = repository.findAll();
        notificationsToSend.stream()
                .map(NotificationEntity::getOrder)
                .forEach(notification -> log.info("Notification send: {}", notification));
        repository.deleteAll(notificationsToSend);
    }

    @EventListener
    public void notifyOnEventCreated(OrderEvent event) {
        log.warn("OrderEvent: {}", event);
    }
}
