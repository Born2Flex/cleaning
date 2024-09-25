package org.ukma.notificationserver;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderNotification {
    private OrderNotificationType type;
    private String email;
    private Long orderId;
    private LocalDateTime orderTime;
}
