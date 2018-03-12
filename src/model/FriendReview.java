package model;

/**
 * 0.1 | 13/6/2009
 * + Add ratings too
 * Temp storage of reviews
 * @author Thien Rong
 */
public class FriendReview {

    String FUID;
    String review;
    int rating;

    public FriendReview(String FUID, String review, int rating) {
        this.FUID = FUID;
        this.review = review;
        this.rating = rating;
    }

    public String getFUID() {
        return FUID;
    }

    public void setFUID(String FUID) {
        this.FUID = FUID;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }
}
