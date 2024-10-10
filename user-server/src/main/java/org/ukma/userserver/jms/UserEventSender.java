package org.ukma.userserver.jms;

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
public class UserEventSender {
    @Autowired
    JmsTemplate jmsTemplate;

    @Value("${user.event.topic}")
    private String topic;

    private final ObjectMapper objectMapper;

    public void sendEvent(UserEvent userEvent) {
        try{
            jmsTemplate.convertAndSend(topic, objectMapper.writeValueAsString(userEvent));
        } catch(Exception e){
            log.error("Recieved Exception during send Message: ", e);
        }
    }
}
