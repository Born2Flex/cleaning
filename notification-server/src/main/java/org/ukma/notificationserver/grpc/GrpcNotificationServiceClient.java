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
    private final int MAX_RETRIES = 3;
    private final MailService mailService;
    private final OrderNotificationMapper orderNotificationMapper;

    @Scheduled(cron = "0 * * * * *")
    public void getUpcomingOrderNotifications() {
        getUpcomingOrderNotifications(0);
    }

    private void getUpcomingOrderNotifications(int retryCount) {
        log.info("Receiving upcoming order notifications");
        StreamObserver<NotificationResponse> responseProcessor = new StreamObserver<NotificationResponse>() {
            @Override
            public void onNext(NotificationResponse response) {
                OrderNotification notification = orderNotificationMapper.toNotificationResponse(response);
                log.info("Notification from gRPC server: {}", notification);
                mailService.sendOrderNotificationForUser(notification);
            }

            @Override
            public void onError(Throwable throwable) {
                if (isUnauthorizedError(throwable) && retryCount < MAX_RETRIES) {
                    log.warn("Unauthorized error received, retrying request... (Attempt {}/{})", retryCount + 1, MAX_RETRIES);
                    getUpcomingOrderNotifications(retryCount + 1);
                } else {
                    log.error("Error receiving notifications after {} attempts: {}", retryCount + 1, throwable.getMessage());
                }
            }

            @Override
            public void onCompleted() {
                log.info("Receiving upcoming order notifications completed!");
            }
        };
        notificationServiceStub.getUpcomingOrderNotifications(Empty.newBuilder().build(), responseProcessor);
    }

    private boolean isUnauthorizedError(Throwable throwable) {
        return throwable.getMessage().contains("UNAUTHENTICATED") ||
                (throwable.getCause() != null && throwable.getCause().getMessage().contains("UNAUTHENTICATED"));
    }

}
