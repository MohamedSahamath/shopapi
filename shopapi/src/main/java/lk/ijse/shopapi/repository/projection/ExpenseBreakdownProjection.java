package lk.ijse.shopapi.repository.projection;

import java.math.BigDecimal;

public interface ExpenseBreakdownProjection {
    String getCategoryName();
    BigDecimal getTotalAmount();
    Long getEntryCount();
}