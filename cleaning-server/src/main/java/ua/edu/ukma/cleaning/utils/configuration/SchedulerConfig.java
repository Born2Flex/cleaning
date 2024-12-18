package ua.edu.ukma.cleaning.utils.configuration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import ua.edu.ukma.cleaning.jms.models.OrderNotification;
import ua.edu.ukma.cleaning.jms.OrderNotificationSender;
import ua.edu.ukma.cleaning.jms.models.OrderNotificationType;
import ua.edu.ukma.cleaning.order.OrderEntity;
import ua.edu.ukma.cleaning.order.OrderRepository;
import ua.edu.ukma.cleaning.order.Status;

import java.time.LocalDate;
import java.util.List;

@Configuration
@Slf4j
@EnableScheduling
@RequiredArgsConstructor
public class SchedulerConfig {
    private final OrderRepository orderRepository;
    private final OrderNotificationSender notificationSender;

    @Scheduled(cron = "0 0 8 * * *")
    public void setOrdersStatusPreparing() {
        List<OrderEntity> orders =
                orderRepository.findAllByOrderTimeBetweenAndStatusNot(LocalDate.now().atStartOfDay(),
                LocalDate.now().atStartOfDay().plusDays(1), Status.CANCELLED)
                        .stream()
                        .filter(order -> order.getStatus() != Status.NOT_VERIFIED)
                        .toList();
        orders.forEach(order -> order.setStatus(Status.PREPARING));
        orderRepository.saveAll(orders);
        log.info("Orders status set but notifications haven`t send");
    }
}
