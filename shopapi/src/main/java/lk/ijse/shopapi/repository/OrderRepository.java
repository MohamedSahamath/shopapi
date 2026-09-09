package lk.ijse.shopapi.repository;

import lk.ijse.shopapi.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import lk.ijse.shopapi.repository.projection.*;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByCustomerIdOrderByOrderDateDesc(Long customerId);
    boolean existsByOrderNumber(String orderNumber);

    @Query(value = """
            SELECT COALESCE(SUM(oi.quantity * oi.unit_price), 0)         AS revenue,
                   COALESCE(SUM(oi.quantity * oi.cost_price_at_sale), 0) AS cogs,
                   COUNT(DISTINCT o.id)                                  AS orderCount
            FROM orders o
            JOIN order_items oi ON oi.order_id = o.id
            WHERE o.status IN ('PAID', 'PROCESSING', 'SHIPPED', 'COMPLETED')
              AND YEAR(o.order_date)  = :year
              AND MONTH(o.order_date) = :month
            """, nativeQuery = true)
    RevenueProjection findRevenueForMonth(@Param("year") int year,
                                          @Param("month") int month);

    @Query(value = """
            SELECT p.sku                                          AS sku,
                   p.name                                         AS productName,
                   SUM(oi.quantity)                               AS unitsSold,
                   SUM(oi.quantity * oi.unit_price)               AS revenue,
                   SUM(oi.quantity * (oi.unit_price - oi.cost_price_at_sale)) AS profit
            FROM order_items oi
            JOIN orders   o ON o.id = oi.order_id
            JOIN products p ON p.id = oi.product_id
            WHERE o.status IN ('PAID', 'PROCESSING', 'SHIPPED', 'COMPLETED')
            GROUP BY p.id, p.sku, p.name
            ORDER BY unitsSold DESC
            LIMIT :limit
            """, nativeQuery = true)
    List<TopProductProjection> findTopSellingProducts(@Param("limit") int limit);

    @Query(value = """
            SELECT DATE_FORMAT(o.order_date, '%Y-%m')                    AS period,
                   COALESCE(SUM(oi.quantity * oi.unit_price), 0)         AS revenue,
                   COALESCE(SUM(oi.quantity * oi.cost_price_at_sale), 0) AS cogs
            FROM orders o
            JOIN order_items oi ON oi.order_id = o.id
            WHERE o.status IN ('PAID', 'PROCESSING', 'SHIPPED', 'COMPLETED')
            GROUP BY period
            ORDER BY period DESC
            LIMIT 12
            """, nativeQuery = true)
    List<MonthlyTrendProjection> findMonthlyTrend();

}