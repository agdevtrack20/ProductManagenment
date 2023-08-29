package entities;

/**
 * interface for product class containing basic abstract method and some important
 * default, private and static attributes and methods
 * @param <T>
 */
public interface Rateable<T> {
    public static final Rating DEFAULT_RATING = Rating.NOT_RATED;
    public T applyRating(Rating rating);
    public default Rating getDefaultRating(){
        return DEFAULT_RATING;
    }
    public static Rating convert(int x){
        return (x>=0&&x<=5)?Rating.values()[x]:DEFAULT_RATING;
    }
    public default T applyRating(int x){
        Rating rating=convert(x);
        return applyRating(rating);
    }
}
