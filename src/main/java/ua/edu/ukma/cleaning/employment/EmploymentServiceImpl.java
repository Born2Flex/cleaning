package ua.edu.ukma.cleaning.employment;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.edu.ukma.cleaning.order.OrderRepository;
import ua.edu.ukma.cleaning.order.Status;
import ua.edu.ukma.cleaning.user.Role;
import ua.edu.ukma.cleaning.user.UserEntity;
import ua.edu.ukma.cleaning.user.UserRepository;
import ua.edu.ukma.cleaning.utils.exceptionHandler.exceptions.AlreadyAppliedException;
import ua.edu.ukma.cleaning.utils.exceptionHandler.exceptions.CantChangeEntityException;
import ua.edu.ukma.cleaning.utils.exceptionHandler.exceptions.NoSuchEntityException;
import ua.edu.ukma.cleaning.user.security.SecurityContextAccessor;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmploymentServiceImpl implements EmploymentService {
    private final EmploymentRepository repository;
    private final EmploymentMapper mapper;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;

    @Override
    public EmploymentDto create(String motivationList) {
        if (repository.findByApplicant_Id(SecurityContextAccessor.getAuthenticatedUserId()).isPresent()) {
            log.info("User id = {} try to send more than 1 application for a job", SecurityContextAccessor.getAuthenticatedUserId());
            throw new AlreadyAppliedException("You have already applied for this position");
        }
        EmploymentEntity employmentRequest = mapper.toEntity(motivationList);
        employmentRequest.setApplicant(SecurityContextAccessor.getAuthenticatedUser());
        log.info("Created new employment request with id = {}", employmentRequest.getId());
        return mapper.toDto(repository.save(employmentRequest));
    }

    @Transactional
    @Override
    public Boolean succeed(Long userId) {
        UserEntity user = userRepository.findById(userId).orElseThrow(() -> {
            log.info("Can`t find user by id = {}", userId);
            throw  new NoSuchEntityException("Can`t find user by id: " + userId);
        });
        EmploymentEntity employmentRequest = repository.findByApplicant_Id(userId).orElseThrow(() -> {
            log.info("Can`t find application by user id = {}", userId);
            throw  new NoSuchEntityException("Can`t find application by user id: " + userId);
        });
        repository.delete(employmentRequest);
        user.setAddressList(Collections.emptyList());
        user.setRole(Role.EMPLOYEE);
        userRepository.save(user);
        log.debug("Admin id = {} accepted Employment request id = {}",
                SecurityContextAccessor.getAuthenticatedUserId(), employmentRequest.getId());
        return true;
    }

    @Override
    public Boolean cancel(Long userId) {
        EmploymentEntity employmentRequest = repository.findByApplicant_Id(userId).orElseThrow(() -> {
            log.info("Can`t find application by user id = {}", userId);
            throw  new NoSuchEntityException("Can`t find application by user id: " + userId);
        });
        repository.delete(employmentRequest);
        log.debug("Admin id = {} cancelled Employment request id = {}",
                SecurityContextAccessor.getAuthenticatedUserId(), employmentRequest.getId());
        return true;
    }

    @Override
    public List<EmploymentDto> getAll() {
        return mapper.toDtoList(repository.findAll());
    }

    @Override
    public Boolean unemployment(Long userId) {
        UserEntity employee = userRepository.findById(userId).orElseThrow(() -> {
            log.info("Can`t find user by id = {}", userId);
            throw new NoSuchEntityException("Can`t find user by id: " + userId);
        });
        long countOfUnfinishedOrders = orderRepository.findOrdersByExecutorsId(employee.getId()).stream()
                .filter(order -> order.getStatus() != Status.CANCELLED && order.getStatus() != Status.DONE)
                .filter(order -> order.getOrderTime().isAfter(LocalDateTime.now()))
                .count();
        if (countOfUnfinishedOrders != 0) {
            log.info("Admin with id: " + SecurityContextAccessor.getAuthenticatedUserId()
                    + ", can`t unemploy user with id: " + employee.getId());
            throw new CantChangeEntityException("Can`t unemploy this user");
        }
        employee.setRole(Role.USER);
        userRepository.save(employee);
        log.info("User id = {} was fired by Admin id = {}", userId,
                SecurityContextAccessor.getAuthenticatedUserId());
        return true;
    }
}