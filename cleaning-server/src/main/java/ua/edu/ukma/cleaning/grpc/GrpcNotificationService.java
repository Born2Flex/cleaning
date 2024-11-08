package ua.edu.ukma.cleaning.grpc;

import com.google.protobuf.Empty;
import com.google.protobuf.Timestamp;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;
import io.grpc.stub.StreamObserver;
import notification.NotificationResponse;
import notification.NotificationServiceGrpc;
import org.springframework.security.access.prepost.PreAuthorize;
import ua.edu.ukma.cleaning.order.OrderEntity;
import ua.edu.ukma.cleaning.order.OrderRepository;
import ua.edu.ukma.cleaning.order.OrderService;
import ua.edu.ukma.cleaning.order.Status;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;

@RequiredArgsConstructor
@GrpcService
public class GrpcNotificationService extends NotificationServiceGrpc.NotificationServiceImplBase {

    private final OrderService orderService;
    private final OrderRepository orderRepository;

    @PreAuthorize("hasAuthority('ROLE_CLEANING_SERVER')")
    @Override
    public void getUpcomingOrderNotifications(Empty request, StreamObserver<NotificationResponse> responseObserver) {
        List<OrderEntity> orders =
                orderRepository.findAllByOrderTimeBetweenAndStatusNot(LocalDate.now().atStartOfDay(),
                                LocalDate.now().atStartOfDay().plusDays(1), Status.CANCELLED)
                        .stream()
                        .filter(order -> order.getStatus() == Status.PREPARING)
                        .toList();
        for(OrderEntity order : orders) {
            Instant instantOrderTime = order.getOrderTime().toInstant(ZoneOffset.UTC);
            Instant instantCreationTime = order.getCreationTime().toInstant(ZoneOffset.UTC);
            Timestamp orderTimestamp = Timestamp.newBuilder()
                .setSeconds(instantOrderTime.getEpochSecond())
                .setNanos(instantOrderTime.getNano())
                .build();
            Timestamp creationTimestamp = Timestamp.newBuilder()
                .setSeconds(instantCreationTime.getEpochSecond())
                .setNanos(instantCreationTime.getNano())
                .build();
            NotificationResponse response = NotificationResponse.newBuilder()
                .setOrderId(order.getId())
                .setOrderTime(orderTimestamp)
                .setCreationTime(creationTimestamp)
                .setEmail(order.getClientEmail())
                .setType(NotificationResponse.OrderNotificationType.REMINDING)
                .build();
            responseObserver.onNext(response);
        }
        responseObserver.onCompleted();
    }

}
