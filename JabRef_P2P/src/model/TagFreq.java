package model;

import java.util.Comparator;

/**
 *
 * @author Thien Rong
 */
public class TagFreq {

    String text;
    int freq;
    
    public static final Comparator<TagFreq> COMP_BY_TEXT = new Comparator<TagFreq>() {

        public int compare(TagFreq o1, TagFreq o2) {
            return o1.text.compareTo(o2.text);
        }
    };
    public static final Comparator<TagFreq> COMP_BY_FREQ = new Comparator<TagFreq>() {

        public int compare(TagFreq o1, TagFreq o2) {
            // descending so > => first
            if (o1.freq > o2.freq) {
                return -1;
            } else if (o1.freq < o2.freq) {
                return 1;
            } else {
                return o1.text.compareTo(o2.text);
            }
        }
    };

    public TagFreq(String text, int freq) {
        this.text = text;
        this.freq = freq;
    }

    public boolean equals(TagFreq t) {
        return text.equals(t.text);
    }

    public String toString() {
        return text + "(" + freq + ")";
    }

    public int getFreq() {
        return freq;
    }

    public String getText() {
        return text;
    }


}