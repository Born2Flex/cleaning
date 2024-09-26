package ua.edu.ukma.cleaning.jms;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class OrderNotificationSender {
    @Autowired
    JmsTemplate jmsTemplate;

    @Value("${order.notification.queue}")
    private String queue;

    private final ObjectMapper objectMapper;

    public void sendMessage(OrderNotification order){
        try{
            jmsTemplate.convertAndSend(queue, objectMapper.writeValueAsString(order));
        } catch(Exception e){
            log.error("Recieved Exception during send Message: ", e);
        }
    }
}
