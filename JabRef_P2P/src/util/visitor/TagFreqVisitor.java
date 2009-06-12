package util.visitor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeSet;
import model.TagFreq;
import net.sf.jabref.BasePanel;
import net.sf.jabref.BibtexEntry;
import util.Tag;

/**
 *
 * @author Thien Rong
 */
public class TagFreqVisitor implements EntryVisitor {

    Map<String, Integer> tagFreq = new HashMap<String, Integer>();
    int totalFreq = 0;
    List<BibtexEntry> missingTagEntries = new ArrayList<BibtexEntry>();

    public static void main(String[] args) {
        TagFreqVisitor vv = new TagFreqVisitor();
        vv.tagFreq.put("a", 1);
        vv.tagFreq.put("b", 1);
        vv.tagFreq.put("c", 1);
        //System.out.println(vv.getTopFreq(2));
        vv.tagFreq.clear();
        vv.tagFreq.put("a", 1);
        vv.tagFreq.put("b", 2);
        vv.tagFreq.put("c", 3);
        //System.out.println(vv.getTopFreq(2));
        vv.tagFreq.clear();
        vv.tagFreq.put("a", 1);
        vv.tagFreq.put("b", 1);
        vv.tagFreq.put("c", 3);
        //System.out.println(vv.getTopFreq(2));
    }

    private Collection<String> extractKeywords(BibtexEntry entry) {
        Collection<String> kw = Tag.extractKeywords(entry);
        if (kw.isEmpty()) {
            missingTagEntries.add(entry);
        }

        return kw;
    }

    private void incrFreq(String keyword) {
        Integer freq = tagFreq.get(keyword);
        if (freq == null) {
            freq = 0;
        }
        tagFreq.put(keyword, ++freq);
    }

    public boolean visitEntry(BibtexEntry entry, BasePanel bp) {
        for (String kw : this.extractKeywords(entry)) {
            this.incrFreq(kw);
            totalFreq++;
        }

        return true;
    }

    public int getTotalFreq() {
        return totalFreq;
    }

    public Map<String, Integer> getTagFreq() {
        return tagFreq;
    }

    public List<BibtexEntry> getMissingTagEntries() {
        return missingTagEntries;
    }
}

