package ua.edu.ukma.cleaning.order;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderRepository extends JpaRepository<OrderEntity, Long> {
    List<OrderEntity> findAllByStatusNotAndClientIdIs(Status status, Long clientId);
    List<OrderEntity> findAllByStatus(Status status);
    List<OrderEntity> findAllByOrderTimeBetweenAndStatusNot(LocalDateTime start, LocalDateTime end, Status status);
    Page<OrderEntity> findAll(Pageable pageable);
    Page<OrderEntity> findAllByStatus(Status status, Pageable pageable);
    Page<OrderEntity> findOrdersByExecutorsId(Long executorId, Pageable pageable);
    List<OrderEntity> findOrdersByExecutorsId(Long executorId);
    Page<OrderEntity> findOrdersByClientId(Long clientId, Pageable pageable);
}
