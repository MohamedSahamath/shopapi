package lk.ijse.shopapi.service;

import lk.ijse.shopapi.dto.request.*;
import lk.ijse.shopapi.dto.response.ProductResponse;

import java.util.List;

public interface ProductService {
    ProductResponse create(ProductRequest request);
    List<ProductResponse> findAll(boolean includeInactive);
    List<ProductResponse> search(String name, Long categoryId);
    ProductResponse findById(Long id);
    ProductResponse update(Long id, ProductRequest request);
    ProductResponse updateStock(Long id, StockUpdateRequest request);
    void delete(Long id);
}