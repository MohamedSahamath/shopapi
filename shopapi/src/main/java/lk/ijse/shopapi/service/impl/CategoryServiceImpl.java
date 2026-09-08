package lk.ijse.shopapi.service.impl;

import lk.ijse.shopapi.dto.request.CategoryRequest;
import lk.ijse.shopapi.dto.response.CategoryResponse;
import lk.ijse.shopapi.entity.Category;
import lk.ijse.shopapi.exception.*;
import lk.ijse.shopapi.repository.*;
import lk.ijse.shopapi.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    @Override
    @Transactional
    public CategoryResponse create(CategoryRequest request) {
        if (categoryRepository.existsByName(request.getName())) {
            throw new DuplicateResourceException(
                    "Category already exists: " + request.getName());
        }

        Category category = Category.builder()
                .name(request.getName())
                .description(request.getDescription())
                .build();

        category = categoryRepository.save(category);
        log.info("Category created: {}", category.getName());
        return toResponse(category);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> findAll() {
        return categoryRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryResponse findById(Long id) {
        return toResponse(getCategoryOrThrow(id));
    }

    @Override
    @Transactional
    public CategoryResponse update(Long id, CategoryRequest request) {
        Category category = getCategoryOrThrow(id);

        if (!category.getName().equals(request.getName())
                && categoryRepository.existsByName(request.getName())) {
            throw new DuplicateResourceException(
                    "Category already exists: " + request.getName());
        }

        category.setName(request.getName());
        category.setDescription(request.getDescription());

        return toResponse(categoryRepository.save(category));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Category category = getCategoryOrThrow(id);

        if (!productRepository.findByCategoryId(id).isEmpty()) {
            throw new BadRequestException(
                    "Cannot delete category with existing products");
        }

        categoryRepository.delete(category);
        log.info("Category deleted: {}", category.getName());
    }

    private Category getCategoryOrThrow(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Category not found with id: " + id));
    }

    private CategoryResponse toResponse(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .productCount((long) productRepository
                        .findByCategoryId(category.getId()).size())
                .build();
    }
}