package lk.ijse.shopapi.repository;

import lk.ijse.shopapi.entity.Expense;
import lk.ijse.shopapi.repository.projection.ExpenseBreakdownProjection;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    List<Expense> findByExpenseDateBetweenOrderByExpenseDateDesc(
            LocalDate from, LocalDate to);

    @Query(value = """
            SELECT COALESCE(SUM(e.amount), 0)
            FROM expenses e
            WHERE YEAR(e.expense_date) = :year
              AND MONTH(e.expense_date) = :month
            """, nativeQuery = true)
    BigDecimal findTotalExpensesForMonth(@Param("year") int year,
                                         @Param("month") int month);

    @Query(value = """
            SELECT ec.name       AS categoryName,
                   SUM(e.amount) AS totalAmount,
                   COUNT(e.id)   AS entryCount
            FROM expenses e
            JOIN expense_categories ec ON ec.id = e.expense_category_id
            WHERE YEAR(e.expense_date) = :year
              AND MONTH(e.expense_date) = :month
            GROUP BY ec.name
            ORDER BY totalAmount DESC
            """, nativeQuery = true)
    List<ExpenseBreakdownProjection> findExpenseBreakdownForMonth(
            @Param("year") int year, @Param("month") int month);
}