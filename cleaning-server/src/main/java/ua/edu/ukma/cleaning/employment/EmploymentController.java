package ua.edu.ukma.cleaning.employment;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ua.edu.ukma.cleaning.storage.ResourceWithType;

import java.util.List;

@RestController
@RequestMapping("api/employment")
@RequiredArgsConstructor()
@Tag(name = "Employment API", description = "Endpoint for employment operations")
public class EmploymentController {
    private final EmploymentService service;

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @Operation(summary = "Create employment request", description = "Create employment request (user id take from security)")
    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public EmploymentDto createRequest(@RequestPart("resume") MultipartFile resume) {
        return service.create(resume);
    }

    @PreAuthorize("hasAnyAuthority({'ROLE_USER', 'ROLE_ADMIN'})")
    @GetMapping(value = "/load-resume", produces = {MediaType.APPLICATION_PDF_VALUE})
    public ResponseEntity<Resource> loadResume() {
        ResourceWithType resource= service.loadResume();
        return ResponseEntity.ok()
                .contentType(MediaType.valueOf(resource.getMediaType()))
                .body(resource.getResource());
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
