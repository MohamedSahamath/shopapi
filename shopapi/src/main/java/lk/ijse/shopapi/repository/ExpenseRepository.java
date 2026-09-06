package lk.ijse.shopapi.repository;

import lk.ijse.shopapi.entity.Expense;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {
}