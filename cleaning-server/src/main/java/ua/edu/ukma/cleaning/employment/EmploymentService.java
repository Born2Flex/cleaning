package ua.edu.ukma.cleaning.employment;

import org.springframework.web.multipart.MultipartFile;
import ua.edu.ukma.cleaning.storage.ResourceWithType;

import java.util.List;

public interface EmploymentService {
    EmploymentDto create(MultipartFile resumeFile);

    Boolean succeed(Long userId);

    Boolean cancel(Long userId);

    List<EmploymentDto> getAll();

    Boolean unemployment(Long userId);

    ResourceWithType loadResume();
}
