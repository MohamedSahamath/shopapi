package lk.ijse.shopapi.repository;

import lk.ijse.shopapi.entity.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    List<Feedback> findByProductId(Long productId);
    List<Feedback> findByCustomerId(Long customerId);
}