package lk.ijse.shopapi.repository.projection;

import java.math.BigDecimal;

public interface TopProductProjection {
    String getSku();
    String getProductName();
    Long getUnitsSold();
    BigDecimal getRevenue();
    BigDecimal getProfit();
}