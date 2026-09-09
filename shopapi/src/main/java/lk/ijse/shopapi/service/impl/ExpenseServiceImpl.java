package lk.ijse.shopapi.service.impl;

import lk.ijse.shopapi.dto.request.*;
import lk.ijse.shopapi.dto.response.*;
import lk.ijse.shopapi.entity.*;
import lk.ijse.shopapi.exception.*;
import lk.ijse.shopapi.repository.*;
import lk.ijse.shopapi.service.ExpenseService;
import lk.ijse.shopapi.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExpenseServiceImpl implements ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final ExpenseCategoryRepository expenseCategoryRepository;
    private final UserRepository userRepository;
    private final SecurityUtil securityUtil;

    @Override
    @Transactional
    public ExpenseResponse create(ExpenseRequest request) {

        ExpenseCategory category = expenseCategoryRepository
                .findById(request.getExpenseCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Expense category not found with id: "
                                + request.getExpenseCategoryId()));

        User recorder = userRepository.findByEmail(securityUtil.getCurrentEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Expense expense = Expense.builder()
                .expenseCategory(category)
                .description(request.getDescription())
                .amount(request.getAmount())
                .expenseDate(request.getExpenseDate())
                .recordedBy(recorder)
                .build();

        expense = expenseRepository.save(expense);
        log.info("Expense recorded: {} - LKR {}",
                expense.getDescription(), expense.getAmount());

        return toResponse(expense);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExpenseResponse> findAll(LocalDate from, LocalDate to) {
        List<Expense> expenses = (from != null && to != null)
                ? expenseRepository.findByExpenseDateBetweenOrderByExpenseDateDesc(from, to)
                : expenseRepository.findAll();
        return expenses.stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ExpenseResponse findById(Long id) {
        return toResponse(getExpenseOrThrow(id));
    }

    @Override
    @Transactional
    public ExpenseResponse update(Long id, ExpenseRequest request) {
        Expense expense = getExpenseOrThrow(id);

        ExpenseCategory category = expenseCategoryRepository
                .findById(request.getExpenseCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Expense category not found"));

        expense.setExpenseCategory(category);
        expense.setDescription(request.getDescription());
        expense.setAmount(request.getAmount());
        expense.setExpenseDate(request.getExpenseDate());

        return toResponse(expenseRepository.save(expense));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        expenseRepository.delete(getExpenseOrThrow(id));
        log.info("Expense deleted: id {}", id);
    }

    @Override
    @Transactional
    public ExpenseCategoryResponse createCategory(ExpenseCategoryRequest request) {
        if (expenseCategoryRepository.existsByName(request.getName())) {
            throw new DuplicateResourceException(
                    "Expense category already exists: " + request.getName());
        }

        ExpenseCategory category = expenseCategoryRepository.save(
                ExpenseCategory.builder()
                        .name(request.getName())
                        .description(request.getDescription())
                        .build());

        return ExpenseCategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExpenseCategoryResponse> findAllCategories() {
        return expenseCategoryRepository.findAll().stream()
                .map(c -> ExpenseCategoryResponse.builder()
                        .id(c.getId())
                        .name(c.getName())
                        .description(c.getDescription())
                        .build())
                .toList();
    }

    private Expense getExpenseOrThrow(Long id) {
        return expenseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Expense not found with id: " + id));
    }

    private ExpenseResponse toResponse(Expense e) {
        return ExpenseResponse.builder()
                .id(e.getId())
                .categoryId(e.getExpenseCategory().getId())
                .categoryName(e.getExpenseCategory().getName())
                .description(e.getDescription())
                .amount(e.getAmount())
                .expenseDate(e.getExpenseDate())
                .recordedBy(e.getRecordedBy() != null
                        ? e.getRecordedBy().getEmail() : "system")
                .build();
    }
}