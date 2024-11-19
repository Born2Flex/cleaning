package ua.edu.ukma.cleaning.order;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderRepository extends JpaRepository<OrderEntity, Long> {
    List<OrderEntity> findAllByStatusInAndClientEmail(List<Status>  status, String clientEmail);
    List<OrderEntity> findAllByOrderTimeBetweenAndStatusNot(LocalDateTime start, LocalDateTime end, Status status);
    Page<OrderEntity> findAllByStatus(Status status, Pageable pageable);
    Page<OrderEntity> findOrdersByExecutorsContains(Long executorId, Pageable pageable);
    List<OrderEntity> findOrdersByExecutorsContains(Long executorId);
    Page<OrderEntity> findOrdersByClientEmail(String clientEmail, Pageable pageable);
}
