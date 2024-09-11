package ua.edu.ukma.cleaning.booking.availibility;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

public interface BookingAvailabilityService {
    Map<LocalDate, List<LocalTime>> getAvailableTime(Long countOfExecutors, Duration duration);
}
