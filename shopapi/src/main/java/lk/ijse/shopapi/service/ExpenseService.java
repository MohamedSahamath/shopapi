package lk.ijse.shopapi.service;

import lk.ijse.shopapi.dto.request.*;
import lk.ijse.shopapi.dto.response.*;

import java.time.LocalDate;
import java.util.List;

public interface ExpenseService {
    ExpenseResponse create(ExpenseRequest request);
    List<ExpenseResponse> findAll(LocalDate from, LocalDate to);
    ExpenseResponse findById(Long id);
    ExpenseResponse update(Long id, ExpenseRequest request);
    void delete(Long id);

    ExpenseCategoryResponse createCategory(ExpenseCategoryRequest request);
    List<ExpenseCategoryResponse> findAllCategories();
}