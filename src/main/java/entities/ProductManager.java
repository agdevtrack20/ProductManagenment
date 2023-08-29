package entities;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Collectors;


/**
 * It's a manager class containing factory methods
 * @author avinash
 * @version 1.0
 */
public class ProductManager {
    private Product product;
    private Review review;

    Review[] reviews=new Review[5];
    public Map<Product, List<Review>> products= new HashMap<>();

    private static Map<String, ResourceFormatter> formatters=
            Map.of("en-GB", new ResourceFormatter(Locale.UK),
                    "en-US", new ResourceFormatter(Locale.US),
                    "es-US", new ResourceFormatter(new Locale("es", "US")),
                    "fr-FR", new ResourceFormatter(Locale.FRANCE),
                    "zh-CN", new ResourceFormatter(Locale.CHINA)
            );
    private ResourceFormatter formatter;


    public ProductManager(Locale locale) {
        this(locale.toLanguageTag());
    }
    public ProductManager(String languageTag){
        changeLocale(languageTag);
    }

    public void changeLocale(String languageTag){
        formatter= formatters.getOrDefault(languageTag, formatters.get("en-GB"));
    }
    public static Set<String> getSupportedLocale(){
        return formatters.keySet();
    }


    public Product createProduct(int id, String name, BigDecimal price, Rating rating, LocalDate bestBefore){
        product= new Food(id, name,price, rating,bestBefore);
        products.putIfAbsent(product,new ArrayList<>());
        return product;
    }
    public Product createProduct(int id, String name, BigDecimal price, Rating rating){
        product= new Drink(id, name,price, rating);
        products.putIfAbsent(product,new ArrayList<>());
        return product;
    }

    public Product reviewProduct(int id, Rating rating, String comments){
        Product product =findProduct(id);
        return reviewProduct(product,rating,comments);
    }

    public Product reviewProduct(Product product,Rating rating, String comments){
        List<Review> reviews =products.get(product);
        products.remove(product,reviews);
        reviews.add(new Review(rating,comments));
        int sum=0,i=0;
        product =product.applyRating(
                Rateable.convert(
                        (int)Math.round(
                                reviews.stream()
                                        .mapToInt(r->r.getRating().ordinal())
                                        .average()
                                        .orElse(0)
                        )
                )

        );

        products.put(product,reviews);
        return this.product;
    }

    public void printProductReport(int id){
        Product product=findProduct(id);
        printProductReport(product);
    }

    public void printProductReport(Product product){
        List<Review> reviews=products.get(product);
        Collections.sort(reviews);

        StringBuilder txt = new StringBuilder();
        txt.append(formatter.formatProduct(product));
        txt.append("\n");
        txt.append(" ");

        if(reviews.isEmpty()){
            txt.append(formatter.getText("no.reviews"));
        }else{
            txt.append(reviews.stream()
                    .map(r->formatter.formatReview(r)+"\n")
                    .collect(Collectors.joining())
            );
        }


//        for(Review review:reviews){
//            txt.append(formatter.formatReview(review));
//            txt.append("\n");
//            if(reviews.isEmpty()){
//                txt.append(formatter.getText("no.reviews"));
//                txt.append("\n");
//            }
//        }

        System.out.println(txt);

    }

    public void printProducts(Comparator<Product> sorter, Predicate<Product> filter){
        StringBuilder txt = new StringBuilder();
        products.keySet()
                .stream()
                .sorted(sorter)
                .filter(filter)
                .forEach(p->txt.append((formatter.formatProduct(p)+"\n")));
        System.out.println("\n====== printProduct method ");
        System.out.println(txt);
        }


    public Product findProduct(int id){

        return products.keySet().stream().filter(p->p.getId()==id)
                .findFirst()
                .orElseGet(()->null);
    }

    public Map<String, String> getDiscounts(){
        return products.keySet()
                .stream()
                .collect(
                        Collectors.groupingBy(
                                product -> product.getRating().getStars(),
                                Collectors.collectingAndThen(
                                        Collectors.summingDouble(
                                                product->product.getDiscount().doubleValue()
                                        ),
                                        discount->formatter.moneyFormat.format(discount)
                                )
                        )
                );
    }






    public static class ResourceFormatter{
        private Locale locale;
        private ResourceBundle resources;
        private DateTimeFormatter dateFormat;
        private NumberFormat moneyFormat;

        ResourceFormatter(Locale locale){
            this.locale = locale;
            resources=ResourceBundle.getBundle("resources",locale);
            dateFormat=DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT).localizedBy(locale);
            moneyFormat=NumberFormat.getCurrencyInstance(locale);
            }
        public String formatProduct(Product product){
            return MessageFormat.format(resources.getString("product"),product.getName(),
                    moneyFormat.format(product.getPrice()),product.getRating().getStars(),
                    dateFormat.format(product.getBestBefore()));

        }
        private String formatReview(Review review){
            return MessageFormat.format(resources.getString("review"),review.getRating().getStars(),
                    review.getComments());
        }
        private String getText(String key){
            return resources.getString(key);
        }

    }


}
