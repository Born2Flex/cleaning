package ua.edu.ukma.cleaning.order;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.edu.ukma.cleaning.jms.OrderNotificationSender;
import ua.edu.ukma.cleaning.jms.OrderNotification;
import ua.edu.ukma.cleaning.jms.OrderNotificationType;
import ua.edu.ukma.cleaning.address.AddressMapper;
import ua.edu.ukma.cleaning.address.AddressRepository;
import ua.edu.ukma.cleaning.commercialProposal.CommercialProposalRepository;
import ua.edu.ukma.cleaning.order.dto.*;
import ua.edu.ukma.cleaning.order.review.ReviewDto;
import ua.edu.ukma.cleaning.order.review.ReviewMapper;
import ua.edu.ukma.cleaning.utils.exceptionHandler.exceptions.AccessDeniedException;
import ua.edu.ukma.cleaning.utils.exceptionHandler.exceptions.CantChangeEntityException;
import ua.edu.ukma.cleaning.utils.exceptionHandler.exceptions.NoSuchEntityException;
import ua.edu.ukma.cleaning.security.SecurityContextAccessor;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final CommercialProposalRepository commercialProposalRepository;
    private final AddressRepository addressRepository;
    private final AddressMapper addressMapper;
    private final ReviewMapper reviewMapper;
    private final OrderNotificationSender notificationSender;

    @Override
    @Transactional
    public OrderForUserDto createOrder(OrderCreationDto order) {
        OrderEntity entity = orderMapper.toEntity(order);
        entity.setClient(SecurityContextAccessor.getAuthenticatedUser());
        entity.setCommercialProposals(order.getProposals().entrySet().stream()
                .collect(Collectors.toMap(
                        x -> commercialProposalRepository.findById(x.getKey()).get(),
                        Map.Entry::getValue)));
        entity.setAddress(addressRepository.findById(order.getAddress().getId())
                .orElseGet(() -> addressRepository.save(addressMapper.toEntity(order.getAddress()))));
        OrderForUserDto orderDto = orderMapper.toUserDto(orderRepository.save(entity));
        notificationSender.sendMessage(new OrderNotification(OrderNotificationType.CREATION, entity.getClient().getEmail(), entity.getId(), entity.getOrderTime()));
        log.info("Order with id = {} successfully created", entity.getId());
        return orderDto;
    }

    @Override
    public OrderForUserDto updateOrderForUser(OrderForUserDto order) {
        OrderEntity entity = orderRepository.findById(order.getId()).orElseThrow(() ->
                new NoSuchEntityException("Can`t find order by id: " + order.getId())
        );
        if (!Objects.equals(entity.getClient().getId(), SecurityContextAccessor.getAuthenticatedUserId())) {
            log.warn("User id = {} trying to update order of user id = {}",
                    SecurityContextAccessor.getAuthenticatedUserId(), entity.getClient().getId());
            throw new AccessDeniedException("Access denied");
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
                new NoSuchEntityException("Can`t find order by id: " + order.getId())
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
                new NoSuchEntityException("Can`t find order by id: " + review.getOrderId())
        );
        if (!Objects.equals(entity.getClient().getId(), SecurityContextAccessor.getAuthenticatedUserId())) {
            log.warn("User id = {} trying to update order of user id = {}",
                    SecurityContextAccessor.getAuthenticatedUserId(), entity.getClient().getId());
            throw new AccessDeniedException("Access denied");
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
                new NoSuchEntityException("Can`t find order by id: " + id)
        );
        if (!Objects.equals(entity.getClient().getId(), SecurityContextAccessor.getAuthenticatedUserId())) {
            log.warn("User id = {} trying to get order of user id = {}",
                    SecurityContextAccessor.getAuthenticatedUserId(), entity.getClient().getId());
            throw new AccessDeniedException("Access denied");
        }
        return orderMapper.toUserDto(entity);
    }

    @Override
    public OrderForAdminDto getOrderByIdForAdmin(Long id) {
        OrderEntity entity = orderRepository.findById(id).orElseThrow(() ->
                new NoSuchEntityException("Can`t find order by id: " + id)
        );
        return orderMapper.toAdminDto(entity);
    }

    @Override
    public OrderForUserDto getOrderByIdForEmployee(Long id) {
        OrderEntity entity = orderRepository.findById(id).orElseThrow(() ->
                new NoSuchEntityException("Can`t find order by id: " + id)
        );
        if (!entity.getExecutors().contains(SecurityContextAccessor.getAuthenticatedUser())) {
            log.warn("Employee id = {} trying to get order id = {}", SecurityContextAccessor.getAuthenticatedUserId(), id);
            throw new AccessDeniedException("Access denied");
        }
        return orderMapper.toUserDto(entity);
    }

    @Override
    public Boolean cancelOrderById(Long orderId) {
        OrderEntity entity = orderRepository.findById(orderId).orElseThrow(() ->
                new NoSuchEntityException("Can`t find order by id: " + orderId)
        );
        if (!Objects.equals(entity.getClient().getId(), SecurityContextAccessor.getAuthenticatedUserId())) {
            log.warn("User id = {} trying to cancel order of user id = {}",
                    SecurityContextAccessor.getAuthenticatedUserId(), entity.getClient().getId());
            throw new AccessDeniedException("Access denied");
        }
        entity.setStatus(Status.CANCELLED);
        orderRepository.save(entity);
        log.info("Order id = {} was cancelled", orderId);
        return true;
    }

    @Override
    public List<OrderListDto> getAllOrders() {
        return orderMapper.toListDto(orderRepository.findAll());
    }

    @Override
    public List<OrderListDto> getAllOrdersByStatus(Status status) {
        return orderMapper.toListDto(orderRepository.findAllByStatus(status));
    }

    @Override
    public List<OrderListDto> getAllOrdersByUserId(Long id) {
        return orderMapper.toListDto(orderRepository.findAllByStatusNotAndClientIdIs(Status.CANCELLED, id));
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
            throw new AccessDeniedException("Access denied");
        }
        Page<OrderEntity> orders = orderRepository.findOrdersByExecutorsId(id, pageable);
        int totalPages = orders.getTotalPages();
        return new OrderPageDto(pageable.getPageNumber(), totalPages, orderMapper.toListDto(orders.stream().toList()));
    }

    @Override
    public OrderPageDto findOrdersByUserId(Long id, Pageable pageable) {
        if (SecurityContextAccessor.getAuthorities().contains("ROLE_USER")
                && !Objects.equals(SecurityContextAccessor.getAuthenticatedUserId(), id)) {
            log.warn("User id = {} trying to get orders of user id = {}",
                    SecurityContextAccessor.getAuthenticatedUserId(), id);
            throw new AccessDeniedException("Access denied");
        }
        Page<OrderEntity> orders = orderRepository.findOrdersByClientId(id, pageable);
        int totalPages = orders.getTotalPages();
        return new OrderPageDto(pageable.getPageNumber(), totalPages, orderMapper.toListDto(orders.stream().toList()));
    }

    @Override
    public OrderListDto changeOrderStatus(Long orderId, Status status) {
        OrderEntity orderEntity = orderRepository.findById(orderId).orElseThrow(() -> {
            log.warn("User {}, try to change status of order {}, but order with this id not found",
                    SecurityContextAccessor.getAuthenticatedUser(), orderId);
            return new NoSuchEntityException("Can`t find order by id: " + orderId);
        });
        if (!orderEntity.getExecutors().contains(SecurityContextAccessor.getAuthenticatedUser())
                && SecurityContextAccessor.getAuthenticatedUser().getRole() != Role.ADMIN) {
            log.warn("User id = {} trying to get change order by id id = {}",
                    SecurityContextAccessor.getAuthenticatedUserId(), orderEntity.getId());
            throw new AccessDeniedException("Access denied");
        }
        orderEntity.setStatus(status);
        return orderMapper.toListDto(orderRepository.save(orderEntity));
    }
}
