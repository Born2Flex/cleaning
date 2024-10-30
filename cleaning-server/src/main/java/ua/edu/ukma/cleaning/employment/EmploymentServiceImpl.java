package ua.edu.ukma.cleaning.employment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ua.edu.ukma.cleaning.order.OrderRepository;
import ua.edu.ukma.cleaning.order.Status;
import ua.edu.ukma.cleaning.storage.ResourceWithType;
import ua.edu.ukma.cleaning.storage.StorageService;
import ua.edu.ukma.cleaning.user.UserServerClient;
import ua.edu.ukma.cleaning.user.UserServerClientFeign;
import ua.edu.ukma.cleaning.user.dto.UserDto;
import ua.edu.ukma.cleaning.utils.exceptionHandler.exceptions.AlreadyAppliedException;
import ua.edu.ukma.cleaning.utils.exceptionHandler.exceptions.CantChangeEntityException;
import ua.edu.ukma.cleaning.utils.exceptionHandler.exceptions.NoSuchEntityException;
import ua.edu.ukma.cleaning.security.SecurityContextAccessor;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmploymentServiceImpl implements EmploymentService {
    private final EmploymentRepository repository;
    private final EmploymentMapper employmentMapper;
    private final OrderRepository orderRepository;
    private final StorageService storageService;

    private final UserServerClient userServerClient;

    private final UserServerClientFeign userServerClientFeign;

    @Override
    public EmploymentDto create(MultipartFile resumeFile) {
        if (repository.findByApplicantId(SecurityContextAccessor.getAuthenticatedUserId()).isPresent()) {
            log.info("User id = {} try to send more than 1 application for a job", SecurityContextAccessor.getAuthenticatedUserId());
            throw new AlreadyAppliedException("You have already applied for this position");
        }
        EmploymentEntity employmentRequest = new EmploymentEntity();
        employmentRequest.setCreationTime(LocalDateTime.now());
        employmentRequest.setApplicantId(SecurityContextAccessor.getAuthenticatedUserId());
        EmploymentDto dto = employmentMapper.toDto(repository.save(employmentRequest));
        storageService.storeFile(employmentRequest, resumeFile);
        log.info("Created new employment request with id = {}", employmentRequest.getId());
        return dto;
    }

    @Transactional
    @Override
    public Boolean succeed(Long userId) {
        //TODO implement user role change functionality
//        UserDto user = userServerClient.getById(userId);
        UserDto user = userServerClientFeign.getById(userId);
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
        UserDto employee = userServerClient.getById(userId);
        long countOfUnfinishedOrders = orderRepository.findOrdersByExecutorsContains(employee.getId()).stream()
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

    private EmploymentEntity findEmploymentOrThrow(Long userId) {
        return repository.findByApplicantId(userId).orElseThrow(() -> {
            log.info("Can`t find application by user id = {}", userId);
            return new NoSuchEntityException("Can`t find application by user id: " + userId);
        });
    }

    @Override
    public ResourceWithType loadResume() {
        Long applicantId = SecurityContextAccessor.getAuthenticatedUserId();
        return storageService.loadAsResource(repository.findByApplicantId(applicantId).orElseThrow());
    }
}
