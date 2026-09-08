package lk.ijse.shopapi.service.impl;

import lk.ijse.shopapi.dto.request.*;
import lk.ijse.shopapi.dto.response.ProductResponse;
import lk.ijse.shopapi.entity.*;
import lk.ijse.shopapi.exception.*;
import lk.ijse.shopapi.repository.*;
import lk.ijse.shopapi.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    @Override
    @Transactional
    public ProductResponse create(ProductRequest request) {

        if (productRepository.existsBySku(request.getSku())) {
            throw new DuplicateResourceException("SKU already exists: " + request.getSku());
        }

        if (request.getSellingPrice().compareTo(request.getCostPrice()) < 0) {
            throw new BadRequestException("Selling price cannot be below cost price");
        }

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Category not found with id: " + request.getCategoryId()));

        Product product = Product.builder()
                .category(category)
                .name(request.getName())
                .sku(request.getSku())
                .description(request.getDescription())
                .sellingPrice(request.getSellingPrice())
                .costPrice(request.getCostPrice())
                .stockQuantity(request.getStockQuantity())
                .reorderLevel(request.getReorderLevel() == null ? 10 : request.getReorderLevel())
                .active(true)
                .build();

        product = productRepository.save(product);
        log.info("Product created: {} ({})", product.getName(), product.getSku());
        return toResponse(product);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> findAll(boolean includeInactive) {
        List<Product> products = includeInactive
                ? productRepository.findAll()
                : productRepository.findByActiveTrue();
        return products.stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> search(String name, Long categoryId) {
        List<Product> products;

        if (name != null && !name.isBlank()) {
            products = productRepository
                    .findByNameContainingIgnoreCaseAndActiveTrue(name);
        } else if (categoryId != null) {
            products = productRepository.findByCategoryId(categoryId);
        } else {
            products = productRepository.findByActiveTrue();
        }

        return products.stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse findById(Long id) {
        return toResponse(getProductOrThrow(id));
    }

    @Override
    @Transactional
    public ProductResponse update(Long id, ProductRequest request) {
        Product product = getProductOrThrow(id);

        if (!product.getSku().equals(request.getSku())
                && productRepository.existsBySku(request.getSku())) {
            throw new DuplicateResourceException("SKU already exists: " + request.getSku());
        }

        if (request.getSellingPrice().compareTo(request.getCostPrice()) < 0) {
            throw new BadRequestException("Selling price cannot be below cost price");
        }

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Category not found with id: " + request.getCategoryId()));

        product.setCategory(category);
        product.setName(request.getName());
        product.setSku(request.getSku());
        product.setDescription(request.getDescription());
        product.setSellingPrice(request.getSellingPrice());
        product.setCostPrice(request.getCostPrice());
        product.setStockQuantity(request.getStockQuantity());
        if (request.getReorderLevel() != null) {
            product.setReorderLevel(request.getReorderLevel());
        }

        return toResponse(productRepository.save(product));
    }

    @Override
    @Transactional
    public ProductResponse updateStock(Long id, StockUpdateRequest request) {
        Product product = getProductOrThrow(id);
        product.setStockQuantity(request.getStockQuantity());
        log.info("Stock updated for {}: {}", product.getSku(), request.getStockQuantity());
        return toResponse(productRepository.save(product));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Product product = getProductOrThrow(id);
        product.setActive(false);
        productRepository.save(product);
        log.info("Product deactivated: {}", product.getSku());
    }

    private Product getProductOrThrow(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Product not found with id: " + id));
    }

    private ProductResponse toResponse(Product p) {
        return ProductResponse.builder()
                .id(p.getId())
                .name(p.getName())
                .sku(p.getSku())
                .description(p.getDescription())
                .sellingPrice(p.getSellingPrice())
                .costPrice(p.getCostPrice())
                .stockQuantity(p.getStockQuantity())
                .reorderLevel(p.getReorderLevel())
                .active(p.getActive())
                .categoryId(p.getCategory().getId())
                .categoryName(p.getCategory().getName())
                .lowStock(p.getStockQuantity() <= p.getReorderLevel())
                .build();
    }
}