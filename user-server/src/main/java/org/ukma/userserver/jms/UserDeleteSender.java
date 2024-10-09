package org.ukma.userserver.jms;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.jms.TextMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class UserDeleteSender {
    @Autowired
    JmsTemplate jmsTemplate;

    @Value("${user.delete.topic}")
    private String topic;

    private final ObjectMapper objectMapper;

    public void sendMessage(UserDeleteMessage userDeleteMessage) {
        try{
            jmsTemplate.convertAndSend(topic, objectMapper.writeValueAsString(userDeleteMessage));
        } catch(Exception e){
            log.error("Recieved Exception during send Message: ", e);
        }
    }
}
