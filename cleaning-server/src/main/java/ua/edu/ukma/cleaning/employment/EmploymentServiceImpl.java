package ua.edu.ukma.cleaning.employment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.edu.ukma.cleaning.order.OrderRepository;
import ua.edu.ukma.cleaning.order.Status;
import ua.edu.ukma.cleaning.user.*;
import ua.edu.ukma.cleaning.utils.exceptionHandler.exceptions.AlreadyAppliedException;
import ua.edu.ukma.cleaning.utils.exceptionHandler.exceptions.CantChangeEntityException;
import ua.edu.ukma.cleaning.utils.exceptionHandler.exceptions.NoSuchEntityException;
import ua.edu.ukma.cleaning.user.security.SecurityContextAccessor;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmploymentServiceImpl implements EmploymentService {
    private final EmploymentRepository repository;
    private final EmploymentMapper employmentMapper;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final OrderRepository orderRepository;

    @Override
    public EmploymentDto create(String motivationList) {
        if (repository.findByApplicant_Id(SecurityContextAccessor.getAuthenticatedUserId()).isPresent()) {
            log.info("User id = {} try to send more than 1 application for a job", SecurityContextAccessor.getAuthenticatedUserId());
            throw new AlreadyAppliedException("You have already applied for this position");
        }
        EmploymentEntity employmentRequest = employmentMapper.toEntity(motivationList);
        employmentRequest.setApplicant(SecurityContextAccessor.getAuthenticatedUser());
        log.info("Created new employment request with id = {}", employmentRequest.getId());
        return employmentMapper.toDto(repository.save(employmentRequest));
    }

    @Transactional
    @Override
    public Boolean succeed(Long userId) {
        UserEntity user = findUserOrThrow(userId);
        EmploymentEntity employmentRequest = findEmploymentOrThrow(userId);
        repository.delete(employmentRequest);
        log.debug("Admin id = {} accepted Employment request id = {}",
                SecurityContextAccessor.getAuthenticatedUserId(), employmentRequest.getId());
        return true;
    }

    @Override
    public Boolean cancel(Long userId) {
        EmploymentEntity employmentRequest = findEmploymentOrThrow(userId);
        repository.delete(employmentRequest);
        log.debug("Admin id = {} cancelled Employment request id = {}",
                SecurityContextAccessor.getAuthenticatedUserId(), employmentRequest.getId());
        return true;
    }

    @Override
    public List<EmploymentDto> getAll() {
        return employmentMapper.toDtoList(repository.findAll());
    }

    @Transactional
    @Override
    public Boolean unemployment(Long userId) {
        UserEntity employee = findUserOrThrow(userId);
        long countOfUnfinishedOrders = orderRepository.findOrdersByExecutorsId(employee.getId()).stream()
                .filter(order -> order.getStatus() != Status.CANCELLED && order.getStatus() != Status.DONE)
                .filter(order -> order.getOrderTime().isAfter(LocalDateTime.now()))
                .count();
        if (countOfUnfinishedOrders != 0) {
            log.info("Admin with id: " + SecurityContextAccessor.getAuthenticatedUserId()
                    + ", can`t unemploy user with id: " + employee.getId());
            throw new CantChangeEntityException("Can`t unemploy this user");
        }
        return true;
    }

    private UserEntity findUserOrThrow(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> {
            log.info("Can`t find user by id = {}", userId);
            return new NoSuchEntityException("Can`t find user by id: " + userId);
        });
    }

    private EmploymentEntity findEmploymentOrThrow(Long userId) {
        return repository.findByApplicant_Id(userId).orElseThrow(() -> {
            log.info("Can`t find application by user id = {}", userId);
            return new NoSuchEntityException("Can`t find application by user id: " + userId);
        });
    }
}
