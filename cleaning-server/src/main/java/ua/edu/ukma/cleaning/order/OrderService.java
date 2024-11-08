package ua.edu.ukma.cleaning.order;

import org.springframework.data.domain.Pageable;
import ua.edu.ukma.cleaning.order.dto.*;
import ua.edu.ukma.cleaning.order.review.ReviewDto;

import java.util.List;

public interface OrderService {
    OrderForUserDto createOrder(OrderCreationDto order);
    OrderForUserDto updateOrderForUser(OrderForUserDto order);
    OrderForAdminDto updateOrderForAdmin(OrderForAdminDto order);
    OrderForUserDto updateReview(ReviewDto order);
    OrderForUserDto getOrderByIdForUser(Long id);
    OrderForAdminDto getOrderByIdForAdmin(Long id);
    OrderForUserDto getOrderByIdForEmployee(Long id);
    Boolean cancelOrderById(Long orderId);
    OrderPageDto findOrdersByPage(Pageable pageable);
    OrderPageDto findOrdersByStatusAndPage(Status status, Pageable pageable);
    OrderPageDto findOrdersByExecutorId(Long id, Pageable pageable);
    OrderPageDto findOrdersByUserEmail(String email, Pageable pageable);
    OrderListDto changeOrderStatus(Long orderId, Status status);
    List<OrderListDto> getUpcomingOrders();
}
