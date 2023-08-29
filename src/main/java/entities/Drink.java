package entities;

import java.math.BigDecimal;
import java.time.LocalTime;

public final class Drink extends Product{
    public Drink(int id, String name, BigDecimal price, Rating rating) {
        super(id, name, price, rating);
    }

    @Override
    public BigDecimal getDiscount() {
        if (LocalTime.now().isAfter(LocalTime.of(17, 30)) &&
                LocalTime.now().isBefore(LocalTime.of(18, 30))) {
            return super.getDiscount();
        }
        return BigDecimal.ZERO;
    }

    @Override
    public Product applyRating(Rating rating) {
        return new Drink(getId(),getName(),getPrice(),rating);
    }
}
