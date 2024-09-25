package ua.edu.ukma.cleaning.utils.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import ua.edu.ukma.cleaning.notification.NotificationService;
import ua.edu.ukma.cleaning.order.OrderEntity;
import ua.edu.ukma.cleaning.order.OrderRepository;
import ua.edu.ukma.cleaning.order.Status;

import java.time.LocalDate;
import java.util.List;

@Configuration
@Slf4j
@EnableScheduling
public class SchedulerConfig {
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private NotificationService notificationService;

    @Scheduled(cron = "0 0 8 * * *")
    public void setOrdersStatusPreparing() {
        List<OrderEntity> orders =
                orderRepository.findAllByOrderTimeBetweenAndStatusNot(LocalDate.now().atStartOfDay(),
                LocalDate.now().atStartOfDay().plusDays(1), Status.CANCELLED)
                        .stream()
                        .filter(order -> order.getStatus() != Status.NOT_VERIFIED)
                        .toList();
        orders.forEach(order -> order.setStatus(Status.PREPARING));
//        orders.forEach(order -> mailService.sendOrderNotificationForUser(order)); TODO: Use noty server
        orderRepository.saveAll(orders);
        log.info("Orders status set, and mails sent");
    }

    @Scheduled(fixedDelay = 1000 * 60 * 10)
    public void sendNotificationsForUser() {
        notificationService.sendNotifications();
    }
}
