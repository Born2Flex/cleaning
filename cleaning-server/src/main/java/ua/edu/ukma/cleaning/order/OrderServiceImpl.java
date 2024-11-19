package ua.edu.ukma.cleaning.order;

import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.edu.ukma.cleaning.jms.OrderNotificationSender;
import ua.edu.ukma.cleaning.jms.models.OrderNotification;
import ua.edu.ukma.cleaning.jms.models.OrderNotificationType;
import ua.edu.ukma.cleaning.commercial.proposal.CommercialProposalRepository;
import ua.edu.ukma.cleaning.jms.models.UserEvent;
import ua.edu.ukma.cleaning.metrics.OrderQuantityMetric;
import ua.edu.ukma.cleaning.order.dto.*;
import ua.edu.ukma.cleaning.order.review.ReviewDto;
import ua.edu.ukma.cleaning.order.review.ReviewMapper;
import ua.edu.ukma.cleaning.user.Role;
import ua.edu.ukma.cleaning.utils.exception.handler.exceptions.AccessDeniedException;
import ua.edu.ukma.cleaning.utils.exception.handler.exceptions.CantChangeEntityException;
import ua.edu.ukma.cleaning.utils.exception.handler.exceptions.NoSuchEntityException;
import ua.edu.ukma.cleaning.security.SecurityContextAccessor;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService, UserDeletingProcessor {
    private static final String DEFAULT_NO_SUCH_ENTITY_MESSAGE = "Can`t find order by id: ";
    
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final CommercialProposalRepository commercialProposalRepository;
    private final ReviewMapper reviewMapper;
    private final OrderNotificationSender notificationSender;
    private final OrderQuantityMetric orderCounterMetric;

    @Override
    @Timed("order_creation_time")
    @Transactional
    public OrderForUserDto createOrder(OrderCreationDto order) {
        OrderEntity entity = orderMapper.toEntity(order);
        entity.setClientEmail(SecurityContextAccessor.getAuthenticatedUser().getUsername());
        entity.setCommercialProposals(order.getProposals().entrySet().stream()
                .collect(Collectors.toMap(
                        x -> commercialProposalRepository.findById(x.getKey()).get(),
                        Map.Entry::getValue)));
        OrderForUserDto orderDto = orderMapper.toUserDto(orderRepository.save(entity));
        String userEmail = SecurityContextAccessor.getAuthenticatedUser().getUsername();
        notificationSender.sendMessage(new OrderNotification(OrderNotificationType.CREATION, userEmail, entity.getId(), entity.getOrderTime()));
        log.info("Order with id = {} successfully created", entity.getId());
        orderCounterMetric.increment();
        return orderDto;
    }

    @Override
    public OrderForUserDto updateOrderForUser(OrderForUserDto order) {
        OrderEntity entity = orderRepository.findById(order.getId()).orElseThrow(() ->
                new NoSuchEntityException(DEFAULT_NO_SUCH_ENTITY_MESSAGE + order.getId())
        );
        if (!Objects.equals(entity.getClientEmail(), SecurityContextAccessor.getAuthenticatedUser().getUsername())) {
            log.warn("User id = {} trying to update order of user id = {}",
                    entity.getClientEmail(), SecurityContextAccessor.getAuthenticatedUser().getUsername());
            throw new AccessDeniedException();
        }
        if (entity.getStatus().ordinal() >= Status.PREPARING.ordinal()) {
            log.info("User id = {} trying to change order status, when status is {}",
                    SecurityContextAccessor.getAuthenticatedUserId(), entity.getStatus().name());
            throw new CantChangeEntityException("You can`t change order when status is " + entity.getStatus().name());
        }
        orderMapper.updateFields(entity, order);
        if (order.getStatus() == Status.CANCELLED) {
            entity.setStatus(Status.CANCELLED);
        }
        OrderForUserDto orderDto = orderMapper.toUserDto(orderRepository.save(entity));
        log.debug("Data of order id = {} successfully updated", order.getId());
        return orderDto;
    }

    @Override
    public OrderForAdminDto updateOrderForAdmin(OrderForAdminDto order) {
        OrderEntity entity = orderRepository.findById(order.getId()).orElseThrow(() ->
                new NoSuchEntityException(DEFAULT_NO_SUCH_ENTITY_MESSAGE + order.getId())
        );
        orderMapper.updateFields(entity, order);
        OrderForAdminDto orderDto = orderMapper.toAdminDto(orderRepository.save(entity));
        log.debug("Data of order id = {} was updated by administrator id = {}", orderDto.getId(),
                SecurityContextAccessor.getAuthenticatedUserId());
        return orderDto;
    }

    @Override
    public OrderForUserDto updateReview(ReviewDto review) {
        OrderEntity entity = orderRepository.findById(review.getOrderId()).orElseThrow(() ->
                new NoSuchEntityException(DEFAULT_NO_SUCH_ENTITY_MESSAGE + review.getOrderId())
        );
        if (!Objects.equals(entity.getClientEmail(), SecurityContextAccessor.getAuthenticatedUser().getUsername())) {
            log.warn("User id = {} trying to update order of user id = {}",
                    entity.getClientEmail(), SecurityContextAccessor.getAuthenticatedUser().getUsername());
            throw new AccessDeniedException();
        }
        if (entity.getStatus() != Status.DONE) {
            throw new CantChangeEntityException("You can`t add review when status isn`t Done`");
        }
        entity.setReview(reviewMapper.toEntity(review));
        OrderForUserDto orderDto = orderMapper.toUserDto(orderRepository.save(entity));
        log.info("User id = {} added review on order id = {}",
                SecurityContextAccessor.getAuthenticatedUserId(), orderDto.getId());
        return orderDto;
    }

    @Override
    public OrderForUserDto getOrderByIdForUser(Long id) {
        OrderEntity entity = orderRepository.findById(id).orElseThrow(() ->
                new NoSuchEntityException(DEFAULT_NO_SUCH_ENTITY_MESSAGE + id)
        );
        if (!Objects.equals(entity.getClientEmail(), SecurityContextAccessor.getAuthenticatedUser().getUsername())) {
            log.warn("User id = {} trying to get order of user id = {}",
                    entity.getClientEmail(), SecurityContextAccessor.getAuthenticatedUser().getUsername());
            throw new AccessDeniedException();
        }
        return orderMapper.toUserDto(entity);
    }

    @Override
    public OrderForAdminDto getOrderByIdForAdmin(Long id) {
        OrderEntity entity = orderRepository.findById(id).orElseThrow(() ->
                new NoSuchEntityException(DEFAULT_NO_SUCH_ENTITY_MESSAGE + id)
        );
        return orderMapper.toAdminDto(entity);
    }

    @Override
    public OrderForUserDto getOrderByIdForEmployee(Long id) {
        OrderEntity entity = orderRepository.findById(id).orElseThrow(() ->
                new NoSuchEntityException(DEFAULT_NO_SUCH_ENTITY_MESSAGE + id)
        );
        if (!entity.getExecutors().contains(SecurityContextAccessor.getAuthenticatedUserId())) {
            log.warn("Employee id = {} trying to get order id = {}", SecurityContextAccessor.getAuthenticatedUserId(), id);
            throw new AccessDeniedException();
        }
        return orderMapper.toUserDto(entity);
    }

    @Override
    public Boolean cancelOrderById(Long orderId) {
        OrderEntity entity = orderRepository.findById(orderId).orElseThrow(() ->
                new NoSuchEntityException(DEFAULT_NO_SUCH_ENTITY_MESSAGE + orderId)
        );
        if (!Objects.equals(entity.getClientEmail(), SecurityContextAccessor.getAuthenticatedUser().getUsername())) {
            log.warn("User id = {} trying to cancel order of user id = {}",
                    entity.getClientEmail(), SecurityContextAccessor.getAuthenticatedUser().getUsername());
            throw new AccessDeniedException();
        }
        entity.setStatus(Status.CANCELLED);
        orderRepository.save(entity);
        log.info("Order id = {} was cancelled", orderId);
        return true;
    }

    @Override
    public OrderPageDto findOrdersByPage(Pageable pageable) {
        Page<OrderEntity> orders = orderRepository.findAll(pageable);
        int totalPages = orders.getTotalPages();
        return new OrderPageDto(pageable.getPageNumber(), totalPages, orderMapper.toListDto(orders.stream().toList()));
    }

    @Override
    public OrderPageDto findOrdersByStatusAndPage(Status status, Pageable pageable) {
        Page<OrderEntity> orders = orderRepository.findAllByStatus(status, pageable);
        int totalPages = orders.getTotalPages();
        return new OrderPageDto(pageable.getPageNumber(), totalPages, orderMapper.toListDto(orders.stream().toList()));
    }

    @Override
    public OrderPageDto findOrdersByExecutorId(Long id, Pageable pageable) {
        if (SecurityContextAccessor.getAuthorities().contains("ROLE_EMPLOYEE")
                && !Objects.equals(SecurityContextAccessor.getAuthenticatedUserId(), id)) {
            log.warn("Employee id = {} trying to get orders of employee id = {}",
                    SecurityContextAccessor.getAuthenticatedUserId(), id);
            throw new AccessDeniedException();
        }
        Page<OrderEntity> orders = orderRepository.findOrdersByExecutorsContains(id, pageable);
        int totalPages = orders.getTotalPages();
        return new OrderPageDto(pageable.getPageNumber(), totalPages, orderMapper.toListDto(orders.stream().toList()));
    }

    @Override
    public OrderPageDto findOrdersByUserEmail(String email, Pageable pageable) {
        if (SecurityContextAccessor.getAuthorities().contains("ROLE_USER")
                && !Objects.equals(SecurityContextAccessor.getAuthenticatedUser().getUsername(), email)) {
            log.warn("User id = {} trying to get orders of user id = {}",
                    SecurityContextAccessor.getAuthenticatedUserId(), email);
            throw new AccessDeniedException();
        }
        Page<OrderEntity> orders = orderRepository.findOrdersByClientEmail(email, pageable);
        int totalPages = orders.getTotalPages();
        return new OrderPageDto(pageable.getPageNumber(), totalPages, orderMapper.toListDto(orders.stream().toList()));
    }

    @Override
    public OrderListDto changeOrderStatus(Long orderId, Status status) {
        OrderEntity orderEntity = orderRepository.findById(orderId).orElseThrow(() -> {
            log.warn("User {}, try to change status of order {}, but order with this id not found",
                    SecurityContextAccessor.getAuthenticatedUser(), orderId);
            return new NoSuchEntityException(DEFAULT_NO_SUCH_ENTITY_MESSAGE + orderId);
        });
        if (!orderEntity.getExecutors().contains(SecurityContextAccessor.getAuthenticatedUserId())
                && SecurityContextAccessor.getAuthenticatedUser().getRole() != Role.ADMIN) {
            log.warn("User id = {} trying to get change order by id id = {}",
                    SecurityContextAccessor.getAuthenticatedUserId(), orderEntity.getId());
            throw new AccessDeniedException();
        }
        orderEntity.setStatus(status);
        return orderMapper.toListDto(orderRepository.save(orderEntity));
    }

    @Override
    public void processUserDeleting(UserEvent deleteEvent) {
        List<OrderEntity> userOrders = orderRepository
                .findAllByStatusInAndClientEmail(List.of(Status.NOT_VERIFIED, Status.VERIFIED, Status.PREPARING), deleteEvent.email());
        userOrders.forEach(order -> order.setStatus(Status.CANCELLED));
        orderRepository.saveAll(userOrders);
        log.info("All orders of user {} was canceled", deleteEvent.email());
    }

    public List<OrderListDto> getUpcomingOrders() {
        List<OrderEntity> orders = orderRepository.findAllByOrderTimeBetweenAndStatus(
            LocalDate.now().atStartOfDay(),
            LocalDate.now().atStartOfDay().plusDays(1),
            Status.PREPARING
        );
        return orderMapper.toListDto(orders);
    }
}
