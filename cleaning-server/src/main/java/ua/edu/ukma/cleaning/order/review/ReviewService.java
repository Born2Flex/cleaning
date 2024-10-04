package ua.edu.ukma.cleaning.order.review;


import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;
import ua.edu.ukma.cleaning.order.dto.OrderForUserDto;

import java.util.Map;

public interface ReviewService {

    OrderForUserDto createReview(ReviewDto reviewDto);
    OrderForUserDto createReviewWithImage(ReviewDto reviewDto, MultipartFile reviewImage);
    ReviewDto getReview(Long id);
    Resource getReviewImage(Long id);
    Resource getReviewImage(ReviewDto reviewDto);
}
