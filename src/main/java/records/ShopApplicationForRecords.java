package records;

import entities.Rating;

import java.math.BigDecimal;

public class ShopApplicationForRecords {
    public static void main2(String[] args){
        Drink d1=new Drink(2000,"Lichi juice", BigDecimal.valueOf(15), Rating.FOUR_STAR);
        System.out.println(d1);

        //checking equals() method
        Drink d2=new Drink(2000,"Lichi juice", BigDecimal.valueOf(15), Rating.FOUR_STAR);
        System.out.println("Checking ..."+d1.equals(d2));
    }
}
