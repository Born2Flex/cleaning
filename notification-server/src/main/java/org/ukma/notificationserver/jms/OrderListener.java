package org.ukma.notificationserver.jms;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import org.ukma.notificationserver.OrderNotification;

@Component
@Slf4j
public class OrderListener {
    @JmsListener(destination = "${active-mq.topic}")
    public void processOrderMessage(OrderNotification notification) {


    }
}
