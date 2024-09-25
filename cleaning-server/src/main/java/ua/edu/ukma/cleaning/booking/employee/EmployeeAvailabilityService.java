package ua.edu.ukma.cleaning.booking.employee;


import ua.edu.ukma.cleaning.user.dto.EmployeeDto;

import java.util.List;

public interface EmployeeAvailabilityService {
    List<EmployeeDto> getAllAvailableEmployees(Long orderId);
}
