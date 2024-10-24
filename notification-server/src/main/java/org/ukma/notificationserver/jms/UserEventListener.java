package org.ukma.notificationserver.jms;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import org.ukma.notificationserver.jms.models.UserEvent;
import org.ukma.notificationserver.mails.MailService;

@Component
@Slf4j
@RequiredArgsConstructor
public class UserEventListener {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final MailService mailService;

    @PostConstruct
    void init() {
        objectMapper.registerModule(new JavaTimeModule());
    }

    @SneakyThrows
    @JmsListener(destination = "${user.event.topic}", containerFactory = "topicFactory")
    public void topicListener(String userEventMessage) {
        UserEvent userEvent = objectMapper.readValue(userEventMessage, UserEvent.class);
        switch (userEvent.type()) {
            case CREATE -> mailService.sendUserCreateEmail(userEvent);
            case DELETE -> mailService.sendUserDeletionEmail(userEvent);
        }
        log.info("Received {}", userEvent);
    }
}
