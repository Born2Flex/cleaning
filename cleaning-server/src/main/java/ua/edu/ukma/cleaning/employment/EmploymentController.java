package ua.edu.ukma.cleaning.employment;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/employment")
@RequiredArgsConstructor()
@Tag(name = "Employment API", description = "Endpoint for employment operations")
public class EmploymentController {
    private final EmploymentService service;

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @Operation(summary = "Create employment request", description = "Create employment request (user id take from security)")
    @PostMapping
    public EmploymentDto createRequest(@RequestBody String motivationList) {
        return service.create(motivationList);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @Operation(summary = "Succeed employment request", description = "Succeed employment request")
    @PutMapping("/{userId}/succeed")
    public Boolean succeed(@PathVariable Long userId) {
        return service.succeed(userId);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @Operation(summary = "Cancel employment request", description = "Cancel employment request")
    @PutMapping("/{userId}/cancel")
    public Boolean cancel(@PathVariable Long userId) {
        return service.cancel(userId);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @Operation(summary = "Unemployment", description = "Unemployed employee")
    @PutMapping("/{userId}/unemployment")
    public Boolean unemployment(@PathVariable Long userId) {
        return service.unemployment(userId);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @Operation(summary = "Get all employment request", description = "Get all employment request")
    @GetMapping
    public List<EmploymentDto> getAllEmploymentRequests() {
        return service.getAll();
    }
}
