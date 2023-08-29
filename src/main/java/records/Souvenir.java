package records;

import java.math.BigDecimal;

public final class Souvenir implements Product{
    @Override
    public String name() {
        return "Soup";
    }

    @Override
    public BigDecimal discount() {
        return BigDecimal.valueOf(3);
    }
}
