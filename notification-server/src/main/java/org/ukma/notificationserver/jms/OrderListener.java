package org.ukma.notificationserver.jms;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import org.ukma.notificationserver.mails.MailService;
import org.ukma.notificationserver.models.OrderNotification;

@Component
@Slf4j
@RequiredArgsConstructor
public class OrderListener {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final MailService mailService;

    @PostConstruct
    void init() {
        objectMapper.registerModule(new JavaTimeModule());
    }

    @SneakyThrows
    @JmsListener(destination = "${order.notification.queue}")
    public void processOrderMessage(String notification) {
        OrderNotification orderNotification = objectMapper.readValue(notification, OrderNotification.class);
        mailService.processOrderNotification(orderNotification);
        log.trace("Received " + orderNotification);
    }
}
