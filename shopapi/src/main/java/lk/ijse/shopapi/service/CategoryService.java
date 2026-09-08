package lk.ijse.shopapi.service;

import lk.ijse.shopapi.dto.request.CategoryRequest;
import lk.ijse.shopapi.dto.response.CategoryResponse;

import java.util.List;

public interface CategoryService {
    CategoryResponse create(CategoryRequest request);
    List<CategoryResponse> findAll();
    CategoryResponse findById(Long id);
    CategoryResponse update(Long id, CategoryRequest request);
    void delete(Long id);
}