package records;

import entities.Rating;

import java.math.BigDecimal;
import java.time.LocalDate;

public record Food(int id, String name, BigDecimal price, Rating rating, LocalDate bestBefore) implements Product {
    @Override
    public BigDecimal discount() {
        return BigDecimal.valueOf(5);
    }
}
