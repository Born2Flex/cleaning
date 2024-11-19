package ua.edu.ukma.cleaning.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ua.edu.ukma.cleaning.commercial.proposal.CommercialProposalEntity;
import ua.edu.ukma.cleaning.order.OrderEntity;
import ua.edu.ukma.cleaning.order.OrderRepository;
import ua.edu.ukma.cleaning.order.Status;
import ua.edu.ukma.cleaning.user.Role;
import ua.edu.ukma.cleaning.user.UserServerClientFeign;
import ua.edu.ukma.cleaning.user.dto.UserListDto;

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
    private final UserServerClientFeign userServerClientFeign;

    @Override
    public Map<LocalDate, List<LocalTime>> getAvailableTime(Long countOfExecutors, Duration duration) {
        List<UserListDto> employees = userServerClientFeign.getAllByRole(Role.EMPLOYEE);
        Map<LocalDate, List<LocalTime>> allTimeMap = getAllTimeMapForWeekFromTomorrow();

        return allTimeMap.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> filterAvailableTimes(entry.getKey(), entry.getValue(), employees.size(), countOfExecutors, duration),
                        (a, b) -> b,
                        TreeMap::new
                ));
    }

    private List<LocalTime> filterAvailableTimes(
            LocalDate date,
            List<LocalTime> timeList,
            int totalEmployees,
            Long countOfExecutors,
            Duration duration
    ) {
        List<OrderEntity> bookedOrders = getBookedOrdersForDate(date);
        return timeList.stream()
                .filter(time -> isTimeSlotAvailable(time, bookedOrders, totalEmployees, countOfExecutors, duration))
                .toList();
    }

    private boolean isTimeOverlap(OrderEntity booked, LocalTime time, Duration duration) {
        LocalTime bookedStartTime = booked.getOrderTime().toLocalTime();
        LocalTime bookedEndTime = bookedStartTime.plusMinutes(booked.getDuration().toMinutes());
        LocalTime timeEnd = time.plusMinutes(duration.toMinutes());

        return (bookedEndTime.plusMinutes(40).isAfter(time) && bookedStartTime.isBefore(timeEnd)) ||
                (bookedStartTime.minusMinutes(40).isBefore(timeEnd) && bookedEndTime.isAfter(time)) ||
                (bookedStartTime.isBefore(time) && bookedEndTime.isAfter(time));
    }

    private boolean isTimeSlotAvailable(
            LocalTime time,
            List<OrderEntity> bookedOrders,
            int totalEmployees,
            Long countOfExecutors,
            Duration duration
    ) {
        long countOfInvalid = bookedOrders.stream()
                .filter(booked -> isTimeOverlap(booked, time, duration))
                .distinct()
                .map(OrderEntity::getCommercialProposals)
                .flatMap(e -> e.keySet().stream())
                .mapToInt(CommercialProposalEntity::getRequiredCountOfEmployees)
                .sum();
        return totalEmployees - countOfInvalid - countOfExecutors >= 0;
    }

    private List<OrderEntity> getBookedOrdersForDate(LocalDate date) {
        return orderRepository.findAllByOrderTimeBetweenAndStatusNot(
                date.atStartOfDay(),
                date.plusDays(1).atStartOfDay(),
                Status.CANCELLED
        );
    }

    private Map<LocalDate, List<LocalTime>> getAllTimeMapForWeekFromTomorrow() { // return time map for a week from tomorrow
        LocalDate dateOfStart = LocalDate.now().plusDays(1);
        LocalTime timeOfStart = LocalTime.of(9, 0);
        return LongStream.range(0, 7)
                .boxed()
                .collect(Collectors.toMap(
                        dateOfStart::plusDays,
                        day -> IntStream.range(0, 12)
                                .mapToObj(timeOfStart::plusHours)
                                .toList()
                ));
    }
}
