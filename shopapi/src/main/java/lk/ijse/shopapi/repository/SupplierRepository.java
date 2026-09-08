package lk.ijse.shopapi.repository;

import lk.ijse.shopapi.entity.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SupplierRepository extends JpaRepository<Supplier, Long> {
    List<Supplier> findByActiveTrue();
    List<Supplier> findByCompanyNameContainingIgnoreCase(String name);
}