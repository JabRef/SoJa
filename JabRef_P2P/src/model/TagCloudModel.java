package model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 *
 * @author Thien Rong
 */
public class TagCloudModel {

    public static void main(String[] args) {
        TagCloudModel m = new TagCloudModel();
        Map<String, Integer> myTags = new HashMap<String, Integer>();
        myTags.put("apple", 12);
        myTags.put("orange", 23);
        myTags.put("red", 44);
        m.setMyTags(myTags);

        Map<String, Integer> others = new HashMap<String, Integer>();
        others.put("apple", 12);
        others.put("red", 1);
        others.put("banana", 99);
        m.setFriendTag("Joe", others);

        System.out.println(myTags);
        System.out.println(others);

        System.out.println(m.mergeTags(myTags, others));
        System.out.println(m.getOtherTopFreq(2));
        System.out.println(m.simToMe("Joe"));
    }
    public static final String MINE_CHANGED = "MINE";
    public static final String OTHERS_CHANGED = "OTHERS";
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
    Map<String, Map<String, Integer>> othersTags = new HashMap<String, Map<String, Integer>>();
    Map<String, Integer> myTags = new HashMap<String, Integer>();

    /**
     * Note: Reference will be different
     * @param myTags
     */
    public void setMyTags(Map<String, Integer> myTags) {
        Map<String, Integer> oldValue = this.myTags;
        this.myTags = myTags;
        propertyChangeSupport.firePropertyChange(MINE_CHANGED, oldValue, myTags);
    }

    public void setFriendTag(String friendFUID, Map<String, Integer> friendTags) {
        Map<String, Integer> oldValue = othersTags.get(friendFUID);
        othersTags.put(friendFUID, friendTags);
        propertyChangeSupport.firePropertyChange(OTHERS_CHANGED, oldValue, friendTags);
    }

    /**
     * Similarity using Jacquard coefficient
     * @param FUIDs
     * @return
     */
    public float simToMe(String FUID) {
        Map<String, Integer> friendTags = othersTags.get(FUID);

        Set<String> clonedMyTags = new HashSet<String>();
        clonedMyTags.addAll(myTags.keySet());
        clonedMyTags.retainAll(friendTags.keySet());

        Set<String> allTags = new HashSet<String>();
        allTags.addAll(myTags.keySet());
        allTags.addAll(friendTags.keySet());

        //int totalSize = clonedMyTags.size() + friendTags.size();
        System.out.println(clonedMyTags.size() + "/" + allTags.size());
        return 1.0f * clonedMyTags.size() / allTags.size();
    }

    public SortedSet<TagFreq> getMineTopFreq(int n) {
        return getTopFreq(n, myTags);
    }

    public SortedSet<TagFreq> getOtherTopFreq(int n) {
        Map<String, Integer> othersMergedTags = new HashMap<String, Integer>();

        for (Entry<String, Map<String, Integer>> entry : othersTags.entrySet()) {
            othersMergedTags = this.mergeTags(othersMergedTags, entry.getValue());
        }

        return getTopFreq(n, othersMergedTags);
    }

    public SortedSet<TagFreq> getEveryoneTopFreq(int n) {
        Map<String, Integer> allTags = new HashMap<String, Integer>();
        allTags.putAll(myTags);

        for (Entry<String, Map<String, Integer>> entry : othersTags.entrySet()) {
            allTags = this.mergeTags(allTags, entry.getValue());
        }

        return getTopFreq(n, allTags);
    }

    private Map<String, Integer> mergeTags(Map<String, Integer> map1, Map<String, Integer> map2) {
        Map<String, Integer> mergedTags = new HashMap<String, Integer>();
        Map<String, Integer> clonedTags = new HashMap<String, Integer>();
        clonedTags.putAll(map2);

        for (Entry<String, Integer> entry : map1.entrySet()) {
            String key = entry.getKey();
            int totalFreq = entry.getValue();
            // add together freq if map2 contains
            Integer map2freqForTag = clonedTags.remove(key);
            if (map2freqForTag != null) {
                totalFreq += map2freqForTag;
            }
            mergedTags.put(key, totalFreq);
        }
        mergedTags.putAll(clonedTags);

        return mergedTags;
    }

    public SortedSet<TagFreq> getOtherTopFreq(String FUID, int n) {
        SortedSet<TagFreq> tfs = new TreeSet<TagFreq>(TagFreq.COMP_BY_FREQ);
        Map<String,Integer> tags = othersTags.get(FUID);
        if(tags == null){
            return tfs;
        }
        return getTopFreq(n, tags);
    }

    private SortedSet<TagFreq> getTopFreq(int n, Map<String, Integer> tagFreq) {
        SortedSet<TagFreq> tfs = new TreeSet<TagFreq>(TagFreq.COMP_BY_FREQ);
        for (Entry<String, Integer> entry : tagFreq.entrySet()) {
            TagFreq k = new TagFreq(entry.getKey(), entry.getValue());
            tfs.add(k);
        }

        // **OPTIMISIED TO REDUCE OPERATION, ELSE JUST USE THE WHILE > n REMOVE
        // if already smaller than n, just return it
        // else either remove total-n items
        // or just get first n
        // depending on which is smaller so less operation
        // n > (total-n) choose removed total-n items, n < (total-n) get first n
        int total = tfs.size();
        if (total <= n) {
            // do nothing since size okay already
        } else if (n > total - n) {
            while (tfs.size() > n) {
                tfs.remove(tfs.last());
            }
        } else {
            // by right should just be alpha then add
            Set<TagFreq> itemsToKeep = new TreeSet<TagFreq>(TagFreq.COMP_BY_FREQ);
            for (int i = 0; i < n; i++) {
                TagFreq f = tfs.first();
                itemsToKeep.add(f);
                tfs.remove(f);
            }
            tfs.clear();
            tfs.addAll(itemsToKeep);
        }

        // sort final result alphabetically
        SortedSet<TagFreq> tfsAlpha = new TreeSet<TagFreq>(TagFreq.COMP_BY_TEXT);
        tfsAlpha.addAll(tfs);

        return tfsAlpha;
    }
}
