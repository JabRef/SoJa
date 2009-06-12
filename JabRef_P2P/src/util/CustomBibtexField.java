package util;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import model.friend.Friend;
import model.friend.FriendsModel;
import net.sf.jabref.BasePanel;
import net.sf.jabref.BibtexEntry;
import net.sf.jabref.BibtexFields;

/**
 *
 */
public class CustomBibtexField {

    public static final String SHARE_KEY = "__share";
    public static final String SCORE_KEY = "__score";
    public static final String BUID_KEY = "__buid";
    static final DecimalFormat df = new DecimalFormat("0.00");

    public static String[] getSearchResultFields() {
        String[] defaultFields = BibtexFields.DEFAULT_INSPECTION_FIELDS;
        String[] customResultFields = Arrays.copyOf(defaultFields, defaultFields.length + 2);
        customResultFields[customResultFields.length - 2] = SHARE_KEY;
        customResultFields[customResultFields.length - 1] = SCORE_KEY;

        return customResultFields;
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

    private static void updateEntry(BasePanel bp, BibtexEntry entry, Collection<String> idsToShare) {
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
    public static String getBUID(BibtexEntry entry) {
        if (entry == null) {
            return null;
        }
        return entry.getField(BUID_KEY);
    }

    public static void setBUID(BasePanel bp, BibtexEntry entry, String BUID) {
        entry.setField(BUID_KEY, BUID);
        if (bp != null) {
            bp.updateEntryEditorIfShowing();
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
