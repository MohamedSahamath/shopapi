package lk.ijse.shopapi.repository;

import lk.ijse.shopapi.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByActiveTrue();
    List<Product> findByCategoryId(Long categoryId);
    List<Product> findByNameContainingIgnoreCaseAndActiveTrue(String name);
    boolean existsBySku(String sku);

    @Query(value = """
            SELECT p.* FROM products p
            WHERE p.active = true
              AND p.stock_quantity <= p.reorder_level
            ORDER BY p.stock_quantity ASC
            """, nativeQuery = true)
    List<Product> findLowStockProducts();
}