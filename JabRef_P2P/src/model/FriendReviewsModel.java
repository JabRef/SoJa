package model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import net.sf.jabref.BibtexEntry;
import util.CustomBibtexField;

/**
 * 0.1 | 13/6/2009
 * + Shift changes logic to CustomBibtexField
 * Cached Reviews for faster access
 * Does not handle remove
 * @author Thien Rong
 */
public class FriendReviewsModel {

    public static void main(String[] args) {
        Map<String, String> test = new TreeMap<String, String>();
        test.put("", null);
        System.out.println(test.size());
    }
    public static final String ADD = "add",  UPDATE = "update";

    // BUID-> Map<FUID->FriendReview>
    protected Map<String, Map<String, FriendReview>> reviews = new TreeMap<String, Map<String, FriendReview>>();
    /*// BUID-> Map<FUID->FriendReview>
    protected Map<String, Map<String, String>> reviews = new TreeMap<String, Map<String, String>>();
    // BUID-> Map<FUID->rating>
    protected Map<String, Map<String, Integer>> ratings = new TreeMap<String, Map<String, Integer>>();*/

    /**
     * Set the value of string
     *
     * @param string new value of string
     */
    public void setEntries(String FUID, Collection<BibtexEntry> entries) {
        for (BibtexEntry bibtexEntry : entries) {
            String currBUID = CustomBibtexField.getBUID(bibtexEntry);
            if (currBUID != null) {
                Map<String, FriendReview> itemReviews = reviews.get(currBUID);
                // create if empty
                if (itemReviews == null) {
                    itemReviews = new TreeMap<String, FriendReview>();
                    reviews.put(currBUID, itemReviews);
                }

                FriendReview prevReview = itemReviews.get(FUID);
                int rating = CustomBibtexField.getRating(bibtexEntry);
                String reviewStr = bibtexEntry.getField("review");
                FriendReview currReview = new FriendReview(FUID, reviewStr, rating);

                if (CustomBibtexField.hasMeaningfulChange(prevReview, currReview)) {
                    if (CustomBibtexField.isMeaningful(currReview)) {
                        itemReviews.put(FUID, currReview);
                    } else { // remove if not meaningful
                        itemReviews.remove(FUID);
                    }

                    // don't really matter update or add, since just get all
                    // and re-update
                    //String type = (prevReview == null) ? UPDATE : ADD;
                    String type = UPDATE;
                    //System.out.println(type + " " + prevReview + "/" + currReview);
                    propertyChangeSupport.firePropertyChange(type, prevReview, currReview);
                }
            }
        }
    }

    public Map<String, FriendReview> getReviews(String BUID) {
        return reviews.get(BUID);
    }
    private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

    /**
     * Add PropertyChangeListener.
     *
     * @param listener
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    /**
     * Remove PropertyChangeListener.
     *
     * @param listener
     */
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }
}
