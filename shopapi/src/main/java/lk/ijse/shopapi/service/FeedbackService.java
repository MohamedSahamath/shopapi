package lk.ijse.shopapi.service;

import lk.ijse.shopapi.dto.request.FeedbackRequest;
import lk.ijse.shopapi.dto.response.FeedbackResponse;

import java.util.List;

public interface FeedbackService {
    FeedbackResponse create(FeedbackRequest request);
    List<FeedbackResponse> findByProduct(Long productId);
    List<FeedbackResponse> findAll();
    void delete(Long id);
}