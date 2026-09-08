package lk.ijse.shopapi.service;

import lk.ijse.shopapi.dto.request.*;
import lk.ijse.shopapi.dto.response.OrderResponse;

import java.util.List;

public interface OrderService {
    OrderResponse checkout(CheckoutRequest request);
    List<OrderResponse> findMyOrders();
    List<OrderResponse> findAll();
    OrderResponse findById(Long id);
    OrderResponse updateStatus(Long id, OrderStatusRequest request);
    OrderResponse cancel(Long id);
}