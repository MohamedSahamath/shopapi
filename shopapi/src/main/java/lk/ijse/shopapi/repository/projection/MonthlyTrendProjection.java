package lk.ijse.shopapi.repository.projection;

import java.math.BigDecimal;

public interface MonthlyTrendProjection {
    String getPeriod();
    BigDecimal getRevenue();
    BigDecimal getCogs();
}