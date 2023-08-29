package records;

import entities.Rating;

import java.math.BigDecimal;

public record Drink(int id, String name, BigDecimal price, Rating rating) implements Product {
    @Override
    public BigDecimal discount() {
        return BigDecimal.valueOf(7);
    }
}
