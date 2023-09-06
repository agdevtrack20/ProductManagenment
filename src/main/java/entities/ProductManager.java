package entities;

import exceptions.ProductManagerException;

import java.io.*;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.DateTimeException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.*;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collector;
import java.util.stream.Collectors;


/**
 * It's a manager class containing factory methods
 * @author avinash
 * @version 1.0
 */
public class ProductManager {
    private static final Logger logger=Logger.getLogger(ProductManager.class.getName());
    private ResourceBundle config=ResourceBundle.getBundle("config");
    private MessageFormat reviewFormat=new MessageFormat(config.getString("review.data.format"));
    private MessageFormat productFormat =new MessageFormat(config.getString("product.data.format"));

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

    private Path reportsFolder=Path.of(config.getString("reports.folder"));
    private Path dataFolder=Path.of(config.getString("data.folder"));
    private Path tempFolder=Path.of(config.getString("temp.folder"));



    public ProductManager(Locale locale) {
        this(locale.toLanguageTag());
        loadAllData();
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
        try{
            Product product =findProduct(id);
        }catch (ProductManagerException e){
//            e.printStackTrace();
            logger.log(Level.INFO, e.getMessage());
            return null;
        }

        return reviewProduct(product,rating,comments);
    }

    public Product reviewProduct(Product product,Rating rating, String comments){
        List<Review> reviews =products.get(product);
        products.remove(product,reviews);
        reviews.add(new Review(rating,comments));
        int sum=0,i=0;
        this.product =product.applyRating(
                Rateable.convert(
                        (int)Math.round(
                                reviews.stream()
                                        .mapToInt(r->r.getRating().ordinal())
                                        .average()
                                        .orElse(0)
                        )
                )

        );

        products.put(this.product,reviews);
        return this.product;
    }

    public void printProductReport(int id){
        try{
            Product product=findProduct(id);
            printProductReport(product);
        }catch (ProductManagerException e){
            logger.log(Level.INFO, e.getMessage());
        } catch (IOException e) {
            logger.log(Level.SEVERE,"errror priniting product report"+e.getMessage());
        }


    }

    public void printProductReport(Product product) throws IOException {
        List<Review> reviews=products.get(product);
        Collections.sort(reviews);

        Path productFile=reportsFolder.resolve(MessageFormat.format(config.getString("report.file"),product.getId()));

        try(PrintWriter out = new PrintWriter(new OutputStreamWriter(Files.newOutputStream(productFile, StandardOpenOption.CREATE),"UTF-8"))){
            out.append(formatter.formatProduct(product)+System.lineSeparator());

            if(reviews.isEmpty()){
                out.append(formatter.getText("no.reviews")+System.lineSeparator());
            }else{
                out.append(reviews.stream()
                        .map(r->formatter.formatReview(r)+System.lineSeparator())
                        .collect(Collectors.joining())
                );
            }
        }


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

     private void dumpData(){
            try{
                if(Files.notExists(tempFolder)) {
                    Files.createDirectory(tempFolder);
                }
                    Path tempFile=tempFolder.resolve(MessageFormat.format(config.getString("temp.file"), Instant.now()));
                    try(ObjectOutputStream out=new ObjectOutputStream
                            (Files.newOutputStream(tempFile,StandardOpenOption.CREATE))){
                        out.writeObject(products);
                        products=new HashMap<>();

                }

            }catch (IOException e){
                logger.log(Level.SEVERE,"Error Dumping data"+e.getMessage());
            }
     }

     @SuppressWarnings("unchecked")
    private void restoreData(){
        try{
            Path tempFile=Files.list(tempFolder)
                    .filter(path->path.getFileName().toString().endsWith("tmp"))
                    .findFirst().orElseThrow();
            try(ObjectInputStream in=new ObjectInputStream(Files.newInputStream(tempFile,
                    StandardOpenOption.DELETE_ON_CLOSE))){
                products=(HashMap)in.readObject();
            }

        }catch (Exception e){
            logger.log(Level.SEVERE,"Error Restoring data"+e.getMessage());
        }
    }


    private void loadAllData(){
        try {
            products=Files.list(dataFolder)
                    .filter(file->file.getFileName().toString().startsWith("product"))
                    .map(file->loadProduct(file))
                    .filter(product -> product!=null )
                    .collect(Collectors.toMap(product ->product,product ->loadReviews(product)  ));
        } catch (IOException e) {
            logger.log(Level.SEVERE,"Error loading data"+e.getMessage());
        }
    }

    private Product loadProduct(Path file){
        Product product =null;
        try {
            product=parseProduct(Files.lines(dataFolder.resolve(file),Charset.forName("UTF-8"))
                    .findFirst().orElseThrow());
        } catch (IOException e) {
            logger.log(Level.WARNING,"Error loading product"+e.getMessage());
        }
        return product;
    }



        private List<Review> loadReviews(Product product){
            List<Review> reviews =null;
            Path file=dataFolder.resolve(MessageFormat.format(config.getString("reviews.data.file"),product.getId()));
            if(Files.notExists(file)){
                reviews=new ArrayList<>();
            }else{
                try {
                    reviews=Files.lines(file, Charset.forName("UTF-8"))
                            .map(text-> parseReview(text))
                            .filter(review ->review!=null )
                            .collect(Collectors.toList());
                } catch (IOException e) {
                    logger.log(Level.WARNING,"Error loading file"+e.getMessage());
                }
            }
            return reviews;
        }


    private Review parseReview(String text) {
        Review review=null;

        try{
            Object[] values=reviewFormat.parse(text);
            review=new Review(Rateable.convert(Integer.parseInt((String) values[0])), (String) values[1]);

        }catch (ParseException | NumberFormatException e){
            logger.log(Level.WARNING,"Error Parsing review"+text,e.getMessage());
//            throw new ProductManagerException("unable to parse review",e);

        }
        return review;

    }

    private Product parseProduct(String text){
        Product product=null;

        try{
            Object[] values=productFormat.parse(text);
            int id=Integer.parseInt((String) values[1]);
            String name=(String) values[2];
            BigDecimal price = BigDecimal.valueOf(Double.parseDouble((String) values[3]));
            Rating rating= Rateable.convert(Integer.parseInt((String) values[4]));

            switch((String)values[0]){
                case "D":product=new Drink(id,name,price,rating);
                break;
                case "F": LocalDate bestBefore=LocalDate.parse((String)values[5]);
                product=new Food(id, name, price, rating, bestBefore);
            }
        }catch (ParseException | NumberFormatException | DateTimeException e){
        logger.log(Level.WARNING,"Error Parsing product"+text+" "+e.getMessage());
        }

        return product;

    }

    public Product findProduct(int id) throws ProductManagerException{

        return products.keySet().stream().filter(p->p.getId()==id)
                .findFirst()
                .orElseThrow(()->new ProductManagerException("product with id "+id+" not found"));
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
