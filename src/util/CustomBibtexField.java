package util;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import model.FriendReview;
import model.friend.Friend;
import model.friend.FriendsModel;
import net.sf.jabref.BasePanel;
import net.sf.jabref.BibtexEntry;
import net.sf.jabref.BibtexFields;

/**
 * 0.1 | 13/6/2009
 * + Add ratings
 * + set base changed to prompt user
 * + add the logic for checking changes of reviews
 * @author Thien Rong
 */
public class CustomBibtexField {

    public static void main(String[] args) {
        System.out.println("".equals(null));
        BibtexEntry entry = new BibtexEntry();
        entry.setField(RATING_KEY, null);
        System.out.println(entry.getField(RATING_KEY));
    }
    public static final int MAX_RATING = 5;
    public static final String SHARE_KEY = "__share";
    public static final String SCORE_KEY = "__score";
    public static final String BUID_KEY = "__buid";
    public static final String RATING_KEY = "__rating";
    static final DecimalFormat df = new DecimalFormat("0.00");

    public static String[] getSearchResultFields() {
        String[] defaultFields = BibtexFields.DEFAULT_INSPECTION_FIELDS;
        String[] customResultFields = Arrays.copyOf(defaultFields, defaultFields.length + 2);
        customResultFields[customResultFields.length - 2] = SHARE_KEY;
        customResultFields[customResultFields.length - 1] = SCORE_KEY;

        return customResultFields;
    }

    //////////////////////////////// __RATING  ////////////////////////////////
    /**
     * @param entry
     * @return 0 for no or invalid field in rating, else the valid rating
     */
    public static int getRating(BibtexEntry entry) {
        String ratingStr = entry.getField(RATING_KEY);
        int rating = 0;
        if (ratingStr != null) {
            try {
                rating = Integer.parseInt(ratingStr);
                return validateRating(rating);
            } catch (Exception ignored) {
            }
        }
        return rating;
    }

    /**
     * Return rating in the valid range
     * @param rating
     * @return
     */
    public static int validateRating(int rating) {
        if (rating < 0) {
            rating = 0;
        } else if (rating > MAX_RATING) {
            rating = MAX_RATING;
        }
        return rating;
    }

    /**
     * @param bp
     * @param entry
     * @param rating
     * @return
     */
    public static void setRating(BasePanel bp, BibtexEntry entry, int rating) {
        rating = validateRating(rating);
        entry.setField(RATING_KEY, String.valueOf(rating));
        if (bp != null) {
            bp.updateEntryEditorIfShowing();
            bp.markNonUndoableBaseChanged();
        }
    }

//////////////////////////////// __SEARCH  ////////////////////////////////
    public static void addScoreToEntry(BibtexEntry entry, int score) {
        double finalScore = score / 100.0;
        if (finalScore > 1) {
            finalScore = 1;
        }

        entry.setField(SCORE_KEY, String.valueOf(df.format(finalScore)));
    }

//////////////////////////////// __SHARE  ////////////////////////////////
    public static void removeShareToEntry(BasePanel bp, BibtexEntry entry, Collection<String> idsToRemove) {
        if (null == idsToRemove) {
            return;
        }

        String currValue = entry.getField(SHARE_KEY);
        if (currValue != null) {
            Set<String> currIds = new HashSet<String>(Arrays.asList(currValue.split(",")));

            currIds.removeAll(idsToRemove);
            updateEntry(bp, entry, currIds);
        }

    }

    public static void addShareToEntry(BasePanel bp, BibtexEntry entry, Collection<String> idsToAdd) {
        if (null == idsToAdd) {
            return;
        }

        Set<String> finalIds = new HashSet<String>(idsToAdd);

        // add current ids to set, removing duplicates
        String currValue = entry.getField(SHARE_KEY);
        if (currValue != null) {
            String[] currIds = currValue.split(",");
            for (String currId : currIds) {
                finalIds.add(currId);
            }

        }
        updateEntry(bp, entry, finalIds);
    }

    public static void updateEntry(BasePanel bp, BibtexEntry entry, Collection<String> idsToShare) {
        System.out.println("Sharing " + idsToShare);
        if (idsToShare.size() == 0) {
            entry.clearField(SHARE_KEY);
        } else {
            StringBuffer sb = new StringBuffer();
            for (String id : idsToShare) {
                sb.append("," + id);
            }

            String newValue = sb.substring(1);
            entry.setField(SHARE_KEY, newValue);
        }

        bp.updateEntryEditorIfShowing();
        bp.markNonUndoableBaseChanged();
    }

    /**
     * Get share but just the FUIDs
     * @param entry
     * @return
     */
    public static List<String> getBibtexShare(BibtexEntry entry) {
        List<String> shareToList = new ArrayList<String>();
        String currValue = entry.getField(SHARE_KEY);
        if (currValue != null) {
            String[] ids = currValue.split(",");
            shareToList.addAll(Arrays.asList(ids));
        }
        return shareToList;
    }

    public static List<Friend> getBibtexShare(BibtexEntry entry, FriendsModel model) {
        List<Friend> shareToList = new ArrayList<Friend>();
        String currValue = entry.getField(SHARE_KEY);
        if (currValue != null) {
            String[] currIds = currValue.split(",");
            for (String currId : currIds) {
                Friend f = model.findFriend(currId);
                if (f != null) {
                    shareToList.add(f);
                }
            }
        }
        return shareToList;
    }

////////////////////  BUID ////////////////////////////////
    public static String getBUID(
            BibtexEntry entry) {
        if (entry == null) {
            return null;
        }

        return entry.getField(BUID_KEY);
    }

    public static void setBUID(BasePanel bp, BibtexEntry entry, String BUID) {
        entry.setField(BUID_KEY, BUID);
        if (bp != null) {
            bp.updateEntryEditorIfShowing();
            bp.markNonUndoableBaseChanged();
        }

    }

    /**
     * If no BUID, generate and set BUID
     * @param bp
     * @param entry
     * @param myFUID
     */
    public static void genBUID(BasePanel bp, BibtexEntry entry, String myFUID) {
        if (entry != null) {
            if (null == CustomBibtexField.getBUID(entry)) {
                CustomBibtexField.setBUID(bp, entry, GlobalUID.generate(myFUID));
            }

        }
    }

    public static String removeBUID(BibtexEntry entry) {
        String BUID = entry.getField(BUID_KEY);
        entry.clearField(BUID_KEY);
        return BUID;
    }

    /**
     * Only return true if meaningful change,
     *  not even if null -> rating = 0 && review = null (not meaningful)
     * @param oldReview
     * @param newReview
     * @return
     */
    public static boolean hasMeaningfulChange(FriendReview oldReview, FriendReview newReview) {
        if (oldReview == null) {
            if (isMeaningful(newReview)) {
                return true;
            }
        } else { // have prev review so check for changes instead
            if (hasChanged(oldReview, newReview)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Have rating (not zero) or have review
     * @param review
     * @return
     */
    public static boolean isMeaningful(FriendReview review) {
        if (review != null) {
            if (review.getRating() != 0 || review.getReview() != null) {
                return true;
            }
        }
        return false;
    }

    public static boolean hasChanged(FriendReview oldReview, FriendReview newReview) {

        if (oldReview.getRating() != newReview.getRating()) {
            return true;
        } else {
            String oldReviewStr = oldReview.getReview();
            String newReviewStr = newReview.getReview();
            // in case null, so check which not null then compare, else if both null => false
            if (oldReviewStr != null) {
                return oldReviewStr.equals(newReviewStr);
            } else if (newReviewStr != null) {
                return newReviewStr.equals(oldReviewStr);
            } // else both null => no change
        }
        return false;
    }

    /**
     * Won't return true if peerReview is null but myReview isn't
     * @param myReview
     * @param peerReview
     * @return
     */
    public static boolean isDiffReview(String myReview, String peerReview) {
        return ( // i don't have but friend have
                (myReview == null && peerReview != null) ||
                // both have and different
                (peerReview != null && myReview != null && peerReview.equals(myReview) == false));
    }
}
