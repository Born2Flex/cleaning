package ua.edu.ukma.cleaning.order.review;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ua.edu.ukma.cleaning.order.OrderService;
import ua.edu.ukma.cleaning.order.dto.OrderForUserDto;
import ua.edu.ukma.cleaning.storage.StorageService;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReviewMapper reviewMapper;
    private final StorageService storageService;
    private final OrderService orderService;

    @Override
    public OrderForUserDto createReview(ReviewDto reviewDto) {
        return createReviewWithImage(reviewDto, null);
    }

    @Override
    public OrderForUserDto createReviewWithImage(ReviewDto reviewDto, MultipartFile reviewImage) {
        OrderForUserDto orderDto = orderService.updateReview(reviewDto);
        if(reviewImage != null) {
            storageService.storeFile(reviewDto, reviewImage);
        }
        return orderDto;
    }

    @Override
    public ReviewDto getReview(Long id) {
        ReviewEntity reviewEntity = reviewRepository.findById(id).orElse(null);
        return reviewMapper.toDto(reviewEntity);
    }


    @Override
    public Resource getReviewImage(Long id) {
        ReviewDto reviewDto = getReview(id);
        return getReviewImage(reviewDto);
    }

    @Override
    public Resource getReviewImage(ReviewDto reviewDto) {
        if(reviewDto == null) {
            return null;
        }
        return storageService.loadAsResource(reviewDto).getResource();
    }
}
