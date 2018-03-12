package model;

import java.io.Serializable;
import java.util.Collection;
import net.sf.jabref.BibtexEntry;
import util.BibtexStringCodec;
import util.GlobalUID;

/**
 * 0.2 | 14 Sep 2009
 * + Added GUID to know if read
 */
public class BibtexMessage implements Serializable {

    public static void main(String[] args) {
        System.out.println(new BibtexMessage(null, "sub", "msg", "", "to").getSummary(2));
        System.out.println(new BibtexMessage(null, "sub", "msg", "", "to").getSummary(3));
        System.out.println(new BibtexMessage(null, "sub", "msgxxxx", "", "to").getSummary(4));
        System.out.println(new BibtexMessage(null, "sub", "msg\n\n\n", "", "to").getSummary(5));
        System.out.println(new BibtexMessage(null, "sub", "\n123\n\n", "", "to").getSummary(5));
    }
    // to is string because might send to people u don't know. So might not be able to get back name
    private String to;
    // bibtex Entry isn't serializable, also need to codec it anyway
    private String entries;
    private String subject;
    private String msg;
    // not created at constructor and set only when receive
    private String fromFUID;
    private String GUID;

    public BibtexMessage(String GUID, String subject, String msg, String entries, String to) {
        this.subject = subject;
        this.msg = msg;
        this.entries = entries;
        this.to = to;
        if (GUID == null) {
            this.GUID = GlobalUID.generate("");
        } else {
            this.GUID = GUID;
        }
    }

    public void setFriend(String fromFUID) {
        this.fromFUID = fromFUID;
    }

    public Collection<BibtexEntry> getEntry() {
        return BibtexStringCodec.fromStringList(entries);
    }

    public String getFromFUID() {
        return fromFUID;
    }

    public String getMsg() {
        return msg;
    }

    public String getSummary(int maxLen) {
        StringBuffer sb = new StringBuffer(subject);
        if (msg.length() > 0) {
            sb.append(" - ").append(msg);
        }
        if (maxLen < 3 || (sb.length() <= maxLen)) {
            return sb.toString();
        } else {
            return sb.substring(0, maxLen - 3) + "...";
        }
    }

    public String getSubject() {
        return subject;
    }

    public String getTo() {
        return to;
    }

    public String getGUID() {
        return GUID;
    }

    @Override
    public String toString() {
        return subject;
    }
}
