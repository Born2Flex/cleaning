package ua.edu.ukma.cleaning.jms;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import ua.edu.ukma.cleaning.jms.models.UserEvent;
import ua.edu.ukma.cleaning.jms.models.UserEventType;
import ua.edu.ukma.cleaning.order.UserDeletingProcessor;

@Component
@Slf4j
@RequiredArgsConstructor
public class UserEventListener {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final UserDeletingProcessor processor;

    @SneakyThrows
    @JmsListener(destination = "${user.event.topic}", containerFactory = "topicFactory")
    public void topicListener(String userEventMessage) {
        UserEvent userEvent = objectMapper.readValue(userEventMessage, UserEvent.class);
        if (userEvent.type() == UserEventType.DELETE) {
            processor.processUserDeleting(userEvent);
            log.info("Received {}", userEvent);
        } else {
            log.info("Skipped: {}", userEvent);
        }
    }
}
