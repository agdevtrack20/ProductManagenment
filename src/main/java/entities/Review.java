package entities;

import java.io.Serializable;

public class Review implements Comparable<Review>, Serializable {
    private Rating rating;
    private String comments;

    @Override
    public int compareTo(Review other) {
        return other.rating.ordinal()-this.rating.ordinal();
    }

    public Review(Rating rating, String comments) {
        this.rating = rating;
        this.comments = comments;
    }

    public Rating getRating() {
        return rating;
    }

    public String getComments() {
        return comments;
    }

    @Override
    public String toString() {
        return "Review{" +
                "rating=" + rating +
                ", comments='" + comments + '\'' +
                '}';
    }

}
