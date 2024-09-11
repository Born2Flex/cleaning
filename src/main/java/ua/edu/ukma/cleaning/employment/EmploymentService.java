package ua.edu.ukma.cleaning.employment;

import java.util.List;

public interface EmploymentService {
    EmploymentDto create(String motivationList);

    Boolean succeed(Long userId);

    Boolean cancel(Long userId);

    List<EmploymentDto> getAll();

    Boolean unemployment(Long userId);
}
