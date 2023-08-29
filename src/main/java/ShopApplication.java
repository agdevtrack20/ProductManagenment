import entities.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.Locale;

/**
 * Ths is a shop class Application containing the main method and this is the entry of the
 * product management application
 * @version 1.0
 * @author avinash
 */
public class ShopApplication {
    public static void main(String[] args){

        ProductManager pm=new ProductManager(Locale.UK);

        Product p20= pm.createProduct(1000,"Water",BigDecimal.valueOf(10.0),Rating.NOT_RATED);
        p20=pm.reviewProduct(1000,Rating.FIVE_STAR,"nice");
        p20=pm.reviewProduct(p20,Rating.FOUR_STAR,"nice");
        p20=pm.reviewProduct(p20,Rating.THREE_STAR,"good");
        p20=pm.reviewProduct(p20,Rating.FIVE_STAR,"awesome");
        p20=pm.reviewProduct(p20,Rating.TWO_STAR,"wow");
        pm.printProductReport(p20);

        Product p21=pm.createProduct(1001,"Juice",BigDecimal.valueOf(11.0),Rating.NOT_RATED);
        p21=pm.reviewProduct(1001,Rating.THREE_STAR,"good to drink");
        p21=pm.reviewProduct(p21,Rating.THREE_STAR,"good ");
        p21=pm.reviewProduct(p21,Rating.THREE_STAR,"nice");
        p21=pm.reviewProduct(p21,Rating.THREE_STAR,"just wow");
        p21=pm.reviewProduct(p21,Rating.THREE_STAR,"very nice");
        p21=pm.reviewProduct(p21,Rating.THREE_STAR,"very good");
        pm.printProductReport(p21);

        System.out.println(pm.products);

        pm.printProducts((p1,p2) ->
                p2.getRating().ordinal() - p1.getRating().ordinal());
        pm.printProducts((p1,p2) ->
                p2.getPrice().compareTo(p1.getPrice()));

        Comparator<Product> ratingSorter =(p1,p2)->
                p2.getRating().ordinal()-p1.getRating().ordinal();
        Comparator<Product> priceSorter =(p1,p2)->
                p2.getPrice().compareTo(p1.getPrice());

        pm.printProducts(ratingSorter.thenComparing(priceSorter).reversed());

/*
        Product p21= pm.createProduct(1001,"Juices",BigDecimal.valueOf(20),
        Rating.FOUR_STAR,LocalDate.now().plusDays(10));
        System.out.println(p20);
        System.out.println(p21);



        System.out.println("=========================");
        Product p1 =new Drink(100,"Herbal Tea",BigDecimal.valueOf(100.0),Rating.THREE_STAR);
        System.out.print(p1.getId()+" ");
        System.out.print(p1.getName()+" ");
        System.out.print(p1.getPrice()+" ");
        System.out.print(p1.getDiscount()+" ");
        System.out.print(p1.getRating().getStars()+"\n");

        Product p2=new Drink(101,"Herbal Coffee",BigDecimal.valueOf(200), Rating.FOUR_STAR);
        System.out.println("Second Product"+" "+p2.getId()+" "+p2.getName()+" "
                +p2.getPrice()+" "+p2.getRating().getStars());

        Product p3=new Food(110,"Herbs",BigDecimal.valueOf(1000),
                Rating.FIVE_STAR,LocalDate.now());
        System.out.println("Third Product"+" "+p3.getId()+" "+p3.getName()+" "
                +p3.getPrice()+" "+p3.getRating().getStars());

        p3=p3.applyRating(Rating.THREE_STAR);
        System.out.println("Third Product"+" "+p3.getId()+" "+p3.getName()+" "
                +p3.getPrice()+" "+p3.getRating().getStars());

        Product p4=new Food(105,"Mango Juice",
                BigDecimal.valueOf(50.0),Rating.FIVE_STAR, LocalDate.now().plusDays(2));

        Product p6=new Food(104,"Chocolate",BigDecimal.valueOf(300),
                Rating.FIVE_STAR,LocalDate.now().plusDays(2));
        Product p7=new Drink(104,"Chocolate",BigDecimal.valueOf(300),
                Rating.FIVE_STAR);



        Product p10=new Food(120,"banana",BigDecimal.valueOf(50),
                Rating.FOUR_STAR,LocalDate.now().plusDays(5));
        System.out.println(((Food) p10).getBestBefore()); //casting require to access bestBefore Method when there is no bestBefore method in product


        System.out.println("===================================");
        System.out.println(p1);
        System.out.println(p4);
        System.out.println(p6.equals(p7));



        System.out.println(Rating.values()[2].getStars());
*/

    }

}