package ua.edu.ukma.cleaning.order.review;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ua.edu.ukma.cleaning.order.dto.*;

@Slf4j
@RestController
@RequestMapping("/api/orders/review")
@RequiredArgsConstructor
@Tag(name = "Order Review API", description = "Endpoint for operations order review")
public class ReviewController {

    private final ReviewService reviewService;

    @Operation(summary = "Create review for order", description = "Create review for order")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    @PostMapping
    public OrderForUserDto createReview(@Valid @RequestBody ReviewDto review) {
        return reviewService.createReview(review);
    }

    @Operation(summary = "Create review with image for order", description = "Create review with image for order")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    @PostMapping(value = "/with-image", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<OrderForUserDto> createReview(@Valid @RequestPart(value = "review") ReviewDto review,
                                               @RequestPart(value = "image", required = false) MultipartFile imageFile) {
        if (imageFile != null && imageFile.getContentType() != null) {
            if (!MediaType.IMAGE_JPEG_VALUE.equals(imageFile.getContentType())) {
                return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).contentType(MediaType.APPLICATION_JSON).body(null);
            }
            else {
                return ResponseEntity.ok(reviewService.createReviewWithImage(review, imageFile));
            }
        }
        return ResponseEntity.ok(reviewService.createReview(review));
    }

    @Operation(summary = "Get review by id with image", description = "Get review by id with image")
    @PreAuthorize("hasAnyAuthority('ROLE_USER','ROLE_ADMIN')")
    @GetMapping(value = "/{id}/with-image", produces = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<MultiValueMap<String, Object>> getReviewWithImage(@PathVariable Long id) {
        MultiValueMap<String, Object> formData = new LinkedMultiValueMap<>();
        ReviewDto reviewDto = reviewService.getReview(id);
        Resource reviewImage = reviewService.getReviewImage(reviewDto);
        HttpHeaders reviewHeaders = new HttpHeaders();
        reviewHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<ReviewDto> reviewHttpEntity = new HttpEntity<>(reviewDto, reviewHeaders);
        HttpHeaders reviewImageHeaders = new HttpHeaders();
        reviewImageHeaders.setContentType(MediaType.IMAGE_JPEG);
        HttpEntity<Resource> reviewImageHttpEntity = new HttpEntity<>(reviewImage, reviewImageHeaders);
        formData.add("review", reviewHttpEntity);
        formData.add("image", reviewImageHttpEntity);
        return ResponseEntity.ok().contentType(MediaType.MULTIPART_FORM_DATA).body(formData);
    }

    @Operation(summary = "Get review by id", description = "Get review by id")
    @PreAuthorize("hasAnyAuthority('ROLE_USER','ROLE_ADMIN')")
    @GetMapping("/{id}")
    public ReviewDto getReview(@PathVariable Long id) {
        return reviewService.getReview(id);
    }

    @Operation(summary = "Get image for review by review id", description = "Get image for review by review id")
    @PreAuthorize("hasAnyAuthority('ROLE_USER','ROLE_ADMIN')")
    @GetMapping(value = "/{id}/image", produces = { MediaType.IMAGE_JPEG_VALUE })
    public ResponseEntity<Resource> getReviewImage(@PathVariable Long id) {
        return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(reviewService.getReviewImage(id));
    }

}
