package lk.ijse.shopapi.service;

import lk.ijse.shopapi.dto.request.PaymentRequest;
import lk.ijse.shopapi.dto.response.PaymentResponse;

public interface PaymentService {
    PaymentResponse pay(PaymentRequest request);
    PaymentResponse findByOrder(Long orderId);
}