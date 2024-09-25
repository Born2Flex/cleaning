package ua.edu.ukma.cleaning.notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Service;
import ua.edu.ukma.cleaning.commercialProposal.NewCommercialProposalEvent;
import ua.edu.ukma.cleaning.order.OrderCreationEvent;
import ua.edu.ukma.cleaning.order.OrderEntity;
import ua.edu.ukma.cleaning.user.UserPasswordChangedEvent;

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

    @ApplicationModuleListener
    public void notifyOnOrderCreated(OrderCreationEvent event) {
        log.warn("OrderEvent: {}", event);
    }

    @ApplicationModuleListener
    public void notifyOnNewCommercialProposal(NewCommercialProposalEvent event) {
        log.warn("NewCommercialProposalEvent: {}", event);
    }

    @ApplicationModuleListener
    public void notifyOnPasswordChanged(UserPasswordChangedEvent event) {
        log.warn("UserPasswordChangedEvent: Password has changed for user with email {}", event.userEmail());
    }
}
