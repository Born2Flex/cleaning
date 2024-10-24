package org.ukma.notificationserver.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderNotification implements Serializable {
    private OrderNotificationType type;
    private String email;
    private Long orderId;
    private LocalDateTime orderTime;
    private LocalDateTime creationTime;
}
