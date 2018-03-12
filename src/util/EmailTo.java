package util;

import java.util.HashSet;
import java.util.Set;
import model.friend.Friend;
import util.If;

/**
 * Parse xxx <FUID1>, bbb<FUID2>, ... to a list containing FUID1, FUID2, ...
 * @author Thien Rong
 */
public class EmailTo {

    private static final If extractor = new If(null, "<", ">");

    public static void main(String[] args) {
        String a = "xxx <FUID1>, bbb<FUID2>, fdpsafds";
        Set<String> FUIDs = EmailTo.parse(a);
        System.out.println(FUIDs.size() == 2);
        a = "xxx <FUID<>>, bbb<FUID2>, fdpsafds";
        FUIDs = EmailTo.parse(a);
        System.out.println(FUIDs.size() == 2);

        a = "xxx <>>, bbb<FUID2>, fdpsafds";
        FUIDs = EmailTo.parse(a);
        System.out.println(FUIDs.size() == 1);

        a = "Joe<Joe>,Joe<Joe>,Smith<Smith>,Smith<Smith>,";
        FUIDs = EmailTo.parse(a);
        System.out.println(FUIDs.size() == 2);
    }

    private EmailTo() {
    }

    /**
     * Used during reply so won't send to self.
     * @param toStr
     * @return
     */
    public static String removeSelf(String toStr, String myFUID) {
        String[] curr = toStr.split(",");
        StringBuffer result = new StringBuffer();
        for (int i = 0; i < curr.length; i++) {
            String string = curr[i];
            String possibleFUID = extractor.process(string);
            if (possibleFUID != null && possibleFUID.length() > 0 && possibleFUID.equals(myFUID) == false) {
                result.append(string).append(",");
            }
        }
        return result.toString();
    }

    public static Set<String> parse(String toStr) {
        String[] result = toStr.split(",");
        Set<String> FUIDs = new HashSet<String>();
        for (int i = 0; i < result.length; i++) {
            String string = result[i];
            String possibleFUID = extractor.process(string);
            if (possibleFUID != null && possibleFUID.length() > 0) {
                FUIDs.add(possibleFUID);
            }
        }
        return FUIDs;
    }

    public static String format(Friend f) {
        return f.getName() + "<" + f.getFUID() + ">";
    }
}
