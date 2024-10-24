package ua.edu.ukma.cleaning.jms.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class OrderNotification implements Serializable {
    private final OrderNotificationType type;
    private final String email;
    private final Long orderId;
    private final LocalDateTime orderTime;
    private LocalDateTime creationTime;
}
