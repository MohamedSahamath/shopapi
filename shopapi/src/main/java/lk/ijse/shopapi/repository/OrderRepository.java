package lk.ijse.shopapi.repository;

import lk.ijse.shopapi.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByCustomerIdOrderByOrderDateDesc(Long customerId);
    boolean existsByOrderNumber(String orderNumber);
}