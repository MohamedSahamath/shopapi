package lk.ijse.shopapi.repository.projection;

import java.math.BigDecimal;

public interface RevenueProjection {
    BigDecimal getRevenue();
    BigDecimal getCogs();
    Long getOrderCount();
}