package ua.edu.ukma.cleaning.booking.employee;


import ua.edu.ukma.cleaning.user.EmployeeDto;

import java.util.List;

public interface EmployeeAvailabilityService {
    List<EmployeeDto> getAllAvailableEmployees(Long orderId);
}
