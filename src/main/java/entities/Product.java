package entities;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;


/**
 * This is main entity class of the product management application
 * @version 1.0
 * @author avinash
 */
public sealed class Product implements Rateable<Product>, Serializable permits Food, Drink {
    private int id;
    private String name;
    private BigDecimal price;
    private Rating rating;
    private static final BigDecimal DISCOUNT=BigDecimal.valueOf(0.1);

    Product(int id, String name, BigDecimal price, Rating rating) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.rating = rating;
    }
//    public Product(int id, String name, BigDecimal price) {
//        this(id,name,price,Rating.NOT_RATED);
//    }
//
//
//    public Product() {
//        this.id=0;
//        this.name="no name";
//        this.price=BigDecimal.ZERO;
//        this.rating=Rating.NOT_RATED;
//    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public BigDecimal getPrice() {
        return price;
    }
    public void setPrice(BigDecimal price) {
        this.price = price;
    }
    public Rating getRating() {
        return rating;
    }
    public BigDecimal getDiscount(){
        return price.multiply(DISCOUNT);
    }


    @Override
    public Product applyRating(Rating rating){
        return new Product(getId(),getName(),getPrice(),rating);
    }


    public LocalDate getBestBefore() {
        return LocalDate.now().plusDays(100);
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", rating=" + rating.getStars() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false; //return false earlier due to this
        if(o instanceof Product product) {
            return id == product.id;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
