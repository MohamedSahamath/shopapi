package lk.ijse.shopapi.repository;

import lk.ijse.shopapi.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Optional<Customer> findByNic(String nic);
    Optional<Customer> findByUserEmail(String email);
    boolean existsByNic(String nic);
}