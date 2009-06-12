package model;

/**
 * Temp storage of reviews
 * @author Thien Rong
 */
public class FriendReview {

    String FUID;
    String review;

    public FriendReview(String FUID, String review) {
        this.FUID = FUID;
        this.review = review;
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
}
