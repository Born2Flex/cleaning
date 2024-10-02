package ua.edu.ukma.cleaning.booking.employee;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ua.edu.ukma.cleaning.order.OrderEntity;
import ua.edu.ukma.cleaning.order.OrderRepository;
import ua.edu.ukma.cleaning.order.Status;
import ua.edu.ukma.cleaning.user.EmployeeDto;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collection;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmployeeAvailabilityServiceImpl implements EmployeeAvailabilityService {
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final UserMapper userMapper;

    @Override
    public List<EmployeeDto> getAllAvailableEmployees(Long orderId) {
        OrderEntity order = orderRepository.findById(orderId).orElseThrow();
        LocalDateTime startTime = order.getOrderTime().toLocalDate().atStartOfDay();
        LocalDateTime endTime = order.getOrderTime().toLocalDate().plusDays(1).atStartOfDay();
        List<OrderEntity> orders = orderRepository.findAllByOrderTimeBetweenAndStatusNot(startTime,
                endTime, Status.CANCELLED);

        List<UserEntity> allUsers = userRepository.findAllByRole(Role.EMPLOYEE);
        List<UserEntity> unavailableEmployees = orders.stream()
                .filter(x -> isBetween(x, order.getOrderTime().toLocalTime().minusMinutes(40)
                        , order.getOrderTime().toLocalTime().plus(order.getDuration()).plusMinutes(40)))
                .map(OrderEntity::getExecutors)
                .flatMap(Collection::stream)
                .toList();
        return userMapper.toEmployeeDtoList(allUsers.stream()
                .filter(x -> !unavailableEmployees.contains(x))
                .toList());
    }

    private static boolean isBetween(OrderEntity order, LocalTime start, LocalTime end) {
        return ((order.getOrderTime().toLocalTime().isBefore(end))
                && (order.getOrderTime().toLocalTime().isAfter(start)))
                ||
                ((order.getOrderTime().toLocalTime().plus(order.getDuration())
                        .plusMinutes(40).isBefore(end))
                        && (order.getOrderTime().toLocalTime().plus(order.getDuration())
                        .plusMinutes(40).isAfter(start)))
                ||
                (order.getOrderTime().toLocalTime().isAfter(start)
                        && (order.getOrderTime().toLocalTime().plus(order.getDuration())
                        .plusMinutes(40).isBefore(end)));
    }
}
