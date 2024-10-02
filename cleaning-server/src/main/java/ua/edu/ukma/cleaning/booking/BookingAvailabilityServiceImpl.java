package ua.edu.ukma.cleaning.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ua.edu.ukma.cleaning.commercialProposal.CommercialProposalEntity;
import ua.edu.ukma.cleaning.order.OrderEntity;
import ua.edu.ukma.cleaning.order.OrderRepository;
import ua.edu.ukma.cleaning.order.Status;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingAvailabilityServiceImpl implements BookingAvailabilityService {
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    @Override
    public Map<LocalDate, List<LocalTime>> getAvailableTime(Long countOfExecutors, Duration duration) {
        LocalDate dateOfStart = LocalDate.now().plusDays(1);
        LocalTime timeOfStart = LocalTime.of(9, 0);
        List<UserEntity> employees = userRepository.findAllByRole(Role.EMPLOYEE);
        Map<LocalDate, List<LocalTime>> allTimeMap = LongStream.range(0, 7)
                .boxed()
                .collect(Collectors.toMap(
                        dateOfStart::plusDays,
                        day -> IntStream.range(0, 12)
                                .mapToObj(timeOfStart::plusHours)
                                .collect(Collectors.toList())
                ));
        TreeMap<LocalDate, List<LocalTime>> availableDate = new TreeMap<>();
        allTimeMap.forEach((date, timeList) -> {
            List<OrderEntity> bookedOrder = orderRepository.findAllByOrderTimeBetweenAndStatusNot(date.atStartOfDay(),
                    date.plusDays(1).atStartOfDay(), Status.CANCELLED);
            List<LocalTime> localTimes = timeList.stream()
                    .filter(time -> {
                        long countOfInvalid = bookedOrder.stream()
                                .filter(booked -> (booked.getOrderTime().toLocalTime().plusMinutes(
                                        40 + booked.getDuration().toMinutes()).isAfter(time)
                                        && booked.getOrderTime().toLocalTime()
                                        .isBefore(time.plusMinutes(duration.toMinutes())))
                                        || (booked.getOrderTime().toLocalTime().minusMinutes(40)
                                        .isBefore(time.plusMinutes(duration.toMinutes()))
                                        && booked.getOrderTime().toLocalTime().plusMinutes(booked.getDuration()
                                        .toMinutes()).isAfter(time))
                                        || (booked.getOrderTime().toLocalTime().isBefore(time)
                                        && booked.getOrderTime().toLocalTime().plusMinutes(booked.getDuration()
                                        .toMinutes()).isAfter(time)))
                                .distinct()
                                .map(OrderEntity::getCommercialProposals)
                                .flatMap(e -> e.keySet().stream())
                                .mapToInt(CommercialProposalEntity::getRequiredCountOfEmployees)
                                .sum();
                        return employees.size() - countOfInvalid - countOfExecutors >= 0;
                    })
                    .toList();
            availableDate.put(date, localTimes);
        });
        return availableDate;
    }
}
