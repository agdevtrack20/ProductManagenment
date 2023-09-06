import entities.*;
import exceptions.ProductManagerException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Ths is a shop class Application containing the main method and this is the entry of the
 * product management application
 * @version 1.0
 * @author avinash
 */
public class ShopApplication {
    public static void main(String[] args){

        ProductManager pm=ProductManager.getInstance();
        AtomicInteger clientCount=new AtomicInteger(0);
        Callable<String> client=()->{
            String clientId ="Client"+clientCount.incrementAndGet();
            String threadName=Thread.currentThread().getName();
            int productId= ThreadLocalRandom.current().nextInt(63)+101;
            String languageTag=ProductManager.getSupportedLocale().stream()
                    .skip(ThreadLocalRandom.current().nextInt(4))
                    .findFirst().get();
            StringBuilder log=new StringBuilder();
            log.append(clientId+" "+threadName+"\n-\tstart of log\t-\n");
            log.append(pm.getDiscounts(languageTag)
                    .entrySet()
                    .stream()
                    .map(entry->entry.getKey()+"\t"+entry.getValue())
                    .collect(Collectors.joining("\n"))
            );

            Product product=pm.reviewProduct(productId,Rating.FOUR_STAR,"Yet Another review");
            log.append((product !=null)
            ?"\nProduct "+productId+" reviewed\n"
                    :"\nProduct "+productId+" not Reviewed\n"
            );
            pm.printProductReport(productId,languageTag,clientId);
            log.append(clientId+" generated report for "+productId+" product");

            log.append("\n=\tend of log=\n");
            return log.toString();
        };

        List<Callable<String>> clients= Stream.generate(()->client)
                .limit(5)
                        .collect(Collectors.toList());
        ExecutorService executorService= Executors.newFixedThreadPool(3);

        try {
            List<Future<String>> results = executorService.invokeAll(clients);
            executorService.shutdown();
            results.stream().forEach(result->{
                try {
                    System.out.println(result.get());
                } catch (InterruptedException|ExecutionException e) {
                    Logger.getLogger(ShopApplication.class.getName())
                            .log(Level.SEVERE,"Error retrieving client log",e);
                }

            });
        } catch (InterruptedException e) {
            Logger.getLogger(ShopApplication.class.getName()).log(Level.SEVERE, "error invoking clients", e);
        }




//        pm.printProductReport(103);
//        pm.parseProduct("F,1000,herbal Tea,1.69,0,2021-09-01");

        pm.createProduct(1000,"Water",BigDecimal.valueOf(10.0),Rating.NOT_RATED);
        pm.reviewProduct(1000,Rating.FIVE_STAR,"nice");
        pm.reviewProduct(1000,Rating.FOUR_STAR,"nice");
        pm.reviewProduct(1000,Rating.THREE_STAR,"good");
        pm.reviewProduct(1000,Rating.FIVE_STAR,"awesome");
        pm.reviewProduct(1000,Rating.TWO_STAR,"wow");
//        pm.printProductReport(1000, );
        System.out.println(pm.products);

//        pm.dumpData();
//        System.out.println("here after dumping"+pm.products);
//        pm.restoreData();
//        System.out.println("here after restoring"+pm.products);
//        pm.printProductReport(1000);

//        try{
//            pm.parseReview("1000,4,nice hot cup of Herbal tea");
//        }catch (ProductManagerException e){
//            Throwable cause=e.getCause();
//        }

//        pm.printProductReport(1000);


//        Product p2=pm.createProduct(1001,"Juice",BigDecimal.valueOf(11.0),Rating.NOT_RATED);
//        p2=pm.reviewProduct(1001,Rating.THREE_STAR,"good to drink");
//        p2=pm.reviewProduct(p2,Rating.TWO_STAR,"good ");
//        p2=pm.reviewProduct(p2,Rating.THREE_STAR,"nice");
//        p2=pm.reviewProduct(p2,Rating.THREE_STAR,"just wow");
//        p2=pm.reviewProduct(p2,Rating.THREE_STAR,"very nice");
//        p2=pm.reviewProduct(p2,Rating.THREE_STAR,"very good");
////        pm.printProductReport(p2);
//
//        System.out.println(pm.products);
//
//
//        pm.printProducts((px1,px2) ->
//                px2.getRating().ordinal() - px1.getRating().ordinal(),p->p.getPrice().floatValue()<12);
//
//        pm.getDiscounts().forEach(
//                (rating,discount)->System.out.println(rating+" \t"+discount)
//        );



//        pm.printProducts((p1,p2) ->
//                p2.getPrice().compareTo(p1.getPrice()));

//        Comparator<Product> ratingSorter =(p1,p2)->
//                p2.getRating().ordinal()-p1.getRating().ordinal();
//        Comparator<Product> priceSorter =(p1,p2)->
//                p2.getPrice().compareTo(p1.getPrice());

//        pm.printProducts(ratingSorter.thenComparing(priceSorter).reversed());

/*
        Product p2= pm.createProduct(1001,"Juices",BigDecimal.valueOf(20),
        Rating.FOUR_STAR,LocalDate.now().plusDays(10));
        System.out.println(p1);
        System.out.println(p2);



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
