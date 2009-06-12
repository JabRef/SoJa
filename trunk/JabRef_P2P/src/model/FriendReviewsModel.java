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
    protected Map<String, Map<String, String>> reviews = new TreeMap<String, Map<String, String>>();

    /**
     * Set the value of string
     *
     * @param string new value of string
     */
    public void setEntries(String FUID, Collection<BibtexEntry> entries) {
        for (BibtexEntry bibtexEntry : entries) {
            String currBUID = CustomBibtexField.getBUID(bibtexEntry);
            if (currBUID != null) {
                Map<String, String> itemReviews = reviews.get(currBUID);
                if (itemReviews == null) {
                    itemReviews = new TreeMap<String, String>();
                    reviews.put(currBUID, itemReviews);
                }

                String prevReview = itemReviews.get(FUID);
                String currReview = bibtexEntry.getField("review");
                // new - cannot use prevReview == null because it can be null and don't contains
                if (false == itemReviews.containsKey(FUID)) {
                    itemReviews.put(FUID, currReview);
                    //System.out.println("ADD " + currReview);
                    propertyChangeSupport.firePropertyChange(ADD, null, new FriendReview(FUID, currReview));

                // update if different 
                } else if (CustomBibtexField.isDiffReview(prevReview, currReview)) {
                    //System.out.println("UPDATE " + prevReview + "/" + currReview);
                    itemReviews.put(FUID, currReview);
                    propertyChangeSupport.firePropertyChange(UPDATE, prevReview, new FriendReview(FUID, currReview));
                }
            }
        }
    }

    public Map<String, String> getReviews(String BUID) {
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
