package lk.ijse.shopapi.repository;

import lk.ijse.shopapi.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByActiveTrue();
    List<Product> findByCategoryId(Long categoryId);
    List<Product> findByNameContainingIgnoreCaseAndActiveTrue(String name);
    boolean existsBySku(String sku);
}