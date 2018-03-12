package view;

import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import util.Loader;

/**
 *
 * @author Thien Rong
 */
public abstract class AbstractIMDialog extends JFrame implements ImageConstants {

    public static void main(String[] args) {
        System.out.println(" " + new AbstractIMDialog() {
        }.replaceSmiley(":) test"));
        System.out.println("" + new ImageIcon(ANGRY).getIconHeight());
        System.out.println(findLink("www.google.com"));
                System.out.println(findLink("http://www.google.com  http://www.google.com  "));
    }
    /**
     * Steps to add smileys
     * 1) copy image to basePath
     * 2) create URL linked to image
     * 3) put into smileys Map
     */
    LinkedHashMap<String, URL> smileys = new LinkedHashMap<String, URL>();

    String replaceSmiley(String text) {
        StringBuffer sb = new StringBuffer(text);
        for (Entry<String, URL> entry : smileys.entrySet()) {
            int index;
            String key = entry.getKey();
            while ((index = sb.indexOf(key)) != -1) {
                sb.replace(index, index + key.length(), "<IMG SRC=\"" + entry.getValue() + "\" />");
            }
        }
        return sb.toString();
    }

    // @TODO not done yet
    static String findLink(String text) {
        Matcher matcher = Pattern.compile(
                //"(?i)(\\b(http://|https://|www.|ftp://|file:/|mailto:)\\S+)(\\s+)").matcher(text);
                "(?i)(\\b(http://|https://|www.|ftp://|file:/|mailto:)\\S+)(\\s+)").matcher(text);

        if (matcher.find()) {
            String url = matcher.group(1);
            String prefix = matcher.group(2);
            String endingSpaces = matcher.group(3);

            Matcher dotEndMatcher = Pattern.compile("([\\W&&[^/]]+)$").matcher(url);

            //Ending non alpha characters like [.,?%] shouldn't be included
            // in the url.
            String endingDots = "";
            if (dotEndMatcher.find()) {
                endingDots = dotEndMatcher.group(1);
                url = dotEndMatcher.replaceFirst("");
            }

            text = matcher.replaceFirst("<a href='" + url + "'>" + url + "</a>" + endingDots + endingSpaces);
        }
        return text;
    }

    public AbstractIMDialog() {
        smileys.put("X(", Loader.get(ANGRY));
        smileys.put("x(", Loader.get(ANGRY));
        smileys.put(":\">", Loader.get(BLUSH));
        smileys.put(":((", Loader.get(CRY));
        smileys.put("B-))", Loader.get(COOL));
        smileys.put(">:))", Loader.get(DEVIL_SMILE));
        smileys.put(":|", Loader.get(INDIFFERENT));
        smileys.put(":-|", Loader.get(INDIFFERENT));
        smileys.put(":D", Loader.get(LAUGH));
        smileys.put(":-D", Loader.get(LAUGH));
        smileys.put(":(", Loader.get(SAD));
        smileys.put(":-(", Loader.get(SAD));
        smileys.put(":O", Loader.get(SURPISED));
        smileys.put(":o", Loader.get(SURPISED));
        smileys.put(":-O", Loader.get(SHOCKED));
        smileys.put(":-o", Loader.get(SHOCKED));
        smileys.put(":)", Loader.get(SMILE));
        smileys.put(":-)", Loader.get(SMILE));
        smileys.put(":P", Loader.get(TONGUE));
        smileys.put(":-P", Loader.get(TONGUE));
        smileys.put(";-)", Loader.get(WINK));
        smileys.put(";)", Loader.get(WINK));
    }
}
