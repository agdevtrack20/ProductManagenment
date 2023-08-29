package entities;

import java.math.BigDecimal;
import java.time.LocalDate;

public final class Food extends Product{
    private LocalDate bestBefore;
    public Food(int id, String name, BigDecimal price, Rating rating, LocalDate bestBefore) {
        super(id, name, price, rating);
        this.bestBefore = bestBefore;
    }

    public LocalDate getBestBefore() {
        return bestBefore;
    }
    @Override
    public BigDecimal getDiscount() {
        if(bestBefore==LocalDate.now()){
            return super.getDiscount();
        }
        return BigDecimal.ZERO;
    }

    @Override
    public Product applyRating(Rating rating) {
        return new Food(getId(),getName(),getPrice(),rating,bestBefore);
    }

    @Override
    public String toString() {
        return "Food{"+ super.toString() +
                "bestBefore=" + bestBefore +
                '}';
    }


}
