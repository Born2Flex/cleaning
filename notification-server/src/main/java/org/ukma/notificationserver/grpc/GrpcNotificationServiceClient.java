package org.ukma.notificationserver.grpc;

import com.google.protobuf.Empty;
import com.google.protobuf.Timestamp;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import notification.NotificationResponse;
import notification.NotificationServiceGrpc;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.ukma.notificationserver.mails.MailService;
import org.ukma.notificationserver.models.OrderNotification;
import org.ukma.notificationserver.models.OrderNotificationType;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@RequiredArgsConstructor
@Slf4j
@Component
@EnableScheduling
public class GrpcNotificationServiceClient {
    @GrpcClient("notification-service")
    NotificationServiceGrpc.NotificationServiceStub notificationServiceStub;

    private final MailService mailService;

    @Scheduled(cron = "0 * * * * *")
    public void getUpcomingOrderNotifications() {
        log.info("Sending upcoming order notifications");
        StreamObserver<NotificationResponse> responseProcessor = new StreamObserver<NotificationResponse>() {
            @Override
            public void onNext(NotificationResponse response) {
                Timestamp orderTimestamp = response.getOrderTime();
                Instant instantOrderTime = Instant.ofEpochSecond(orderTimestamp.getSeconds(), orderTimestamp.getNanos());
                LocalDateTime orderTime = LocalDateTime.ofInstant(instantOrderTime, ZoneOffset.UTC);
                Timestamp creationTimestamp = response.getCreationTime();
                Instant instantCreationTime = Instant.ofEpochSecond(creationTimestamp.getSeconds(), creationTimestamp.getNanos());
                LocalDateTime creationTime = LocalDateTime.ofInstant(instantCreationTime, ZoneOffset.UTC);
                OrderNotification notification = new OrderNotification(
                        OrderNotificationType.valueOf(response.getType().name()),
                        response.getEmail(),
                        response.getOrderId(),
                        orderTime,
                        creationTime
                );
                log.info("Notification from gRPC server: {}", notification);
                mailService.sendOrderNotificationForUser(notification);
            }

            @Override
            public void onError(Throwable throwable) {
                log.error("Error receiving notifications: {}", throwable.getMessage());
            }

            @Override
            public void onCompleted() {
                log.info("grpc request completed!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            }
        };
        notificationServiceStub.getUpcomingOrderNotifications(Empty.newBuilder().build(), responseProcessor);
    }

}
