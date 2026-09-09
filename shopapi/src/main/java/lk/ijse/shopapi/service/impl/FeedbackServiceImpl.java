package lk.ijse.shopapi.service.impl;

import lk.ijse.shopapi.dto.request.FeedbackRequest;
import lk.ijse.shopapi.dto.response.FeedbackResponse;
import lk.ijse.shopapi.entity.*;
import lk.ijse.shopapi.exception.ResourceNotFoundException;
import lk.ijse.shopapi.repository.*;
import lk.ijse.shopapi.service.FeedbackService;
import lk.ijse.shopapi.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeedbackServiceImpl implements FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final ProductRepository productRepository;
    private final SecurityUtil securityUtil;

    @Override
    @Transactional
    public FeedbackResponse create(FeedbackRequest request) {
        Customer customer = securityUtil.getCurrentCustomer();

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Product not found with id: " + request.getProductId()));

        Feedback feedback = feedbackRepository.save(Feedback.builder()
                .customer(customer)
                .product(product)
                .rating(request.getRating())
                .comment(request.getComment())
                .build());

        log.info("Feedback submitted for {} by {}",
                product.getSku(), customer.getFullName());

        return toResponse(feedback);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FeedbackResponse> findByProduct(Long productId) {
        return feedbackRepository.findByProductId(productId)
                .stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<FeedbackResponse> findAll() {
        return feedbackRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional
    public void delete(Long id) {
        feedbackRepository.delete(feedbackRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Feedback not found with id: " + id)));
        log.info("Feedback deleted: id {}", id);
    }

    private FeedbackResponse toResponse(Feedback f) {
        return FeedbackResponse.builder()
                .id(f.getId())
                .customerName(f.getCustomer().getFullName())
                .productId(f.getProduct().getId())
                .productName(f.getProduct().getName())
                .rating(f.getRating())
                .comment(f.getComment())
                .createdAt(f.getCreatedAt())
                .build();
    }
}