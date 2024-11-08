package org.ukma.notificationserver.grpc;

import com.google.protobuf.Empty;
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

@RequiredArgsConstructor
@Slf4j
@Component
@EnableScheduling
public class GrpcNotificationServiceClient {

    @GrpcClient("notification-service")
    NotificationServiceGrpc.NotificationServiceStub notificationServiceStub;

    private final MailService mailService;
    private final OrderNotificationMapper orderNotificationMapper;

    @Scheduled(cron = "0 * * * * *")
    public void getUpcomingOrderNotifications() {
        log.info("Sending upcoming order notifications");
        StreamObserver<NotificationResponse> responseProcessor = new StreamObserver<NotificationResponse>() {
            @Override
            public void onNext(NotificationResponse response) {
                OrderNotification notification = orderNotificationMapper.toNotificationResponse(response);
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
