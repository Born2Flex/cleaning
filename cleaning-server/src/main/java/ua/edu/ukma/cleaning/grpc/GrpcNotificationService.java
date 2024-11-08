package ua.edu.ukma.cleaning.grpc;

import com.google.protobuf.Empty;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import io.grpc.stub.StreamObserver;
import notification.NotificationResponse;
import notification.NotificationServiceGrpc;
import org.springframework.security.access.prepost.PreAuthorize;
import ua.edu.ukma.cleaning.order.*;
import ua.edu.ukma.cleaning.order.dto.OrderListDto;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@GrpcService
public class GrpcNotificationService extends NotificationServiceGrpc.NotificationServiceImplBase {

    private final OrderService orderService;
    private final NotificationResponseMapper notificationResponseMapper;

    @PreAuthorize("hasAuthority('ROLE_CLEANING_SERVER')")
    @Override
    public void getUpcomingOrderNotifications(Empty request, StreamObserver<NotificationResponse> responseObserver) {
        List<OrderListDto> orders = orderService.getUpcomingOrders();
        log.info("orders length: {}", orders.size());
        for(OrderListDto order : orders) {
            NotificationResponse response = notificationResponseMapper.toNotificationResponse(order);
            responseObserver.onNext(response);
        }
        responseObserver.onCompleted();
    }

}
