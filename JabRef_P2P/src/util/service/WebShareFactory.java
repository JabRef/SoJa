package util.service;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ImageIcon;
import util.Loader;
import view.ImageConstants;

/**
 * To handle the sharing information, eg shareaholic.com, sharethis.com,
 *  ie 8 accelerator
 * replace the url's {title},{desc}, {url} to get the correct url
 * @author Thien Rong
 */
public class WebShareFactory implements ImageConstants {

    public static void main(String[] args) {
        File f = new File("C:\\Users\\Vista\\Desktop\\Shared\\fyp\\JabRef_P2P\\src\\images\\used");
        for (File file : f.listFiles()) {
            String name = file.getName();
            if (name.contains("web_")) {
                String nameWOExt = name.substring(0, name.length() - 4);
                System.out.println("String " + nameWOExt.toUpperCase() + " = BASEPATH + \"" +
                        name + "\";");
            }
        }
    }

    /**
     * Separate types(email) to make it easier to debug
     * @return
     */
    public static List<WebShareService> getEmailServices() {
        List<WebShareService> services = new ArrayList<WebShareService>();

        WebShareService live = new WebShareService(WEB_HOTMAIL, "Windows Live", "http://mail.live.com/default.aspx?rru=compose%3fsubject%3d{title}%26body%3d{desc}");
        services.add(live);

        WebShareService gmail = new WebShareService(WEB_GMAIL, "Google Gmail", "https://mail.google.com/mail/?view=cm&tf=1&to=&su={title}&body={desc}&fs=1#compose");
        services.add(gmail);

        WebShareService yahoo = new WebShareService(WEB_YAHOO, "Yahoo Mail(Expt)", "http://us.mc590.mail.yahoo.com/mc/compose?To=&Subj={title}&Content={desc}");
        services.add(yahoo);

        //AOL  lnametwt:password123 (for testing)
        WebShareService aol = new WebShareService(WEB_AOL, "AOL", "http://webmail.aol.com/1/aol/en-us/compose-message.aspx?body={desc}&subject={title}");
        services.add(aol);

        WebShareService deft = new WebShareService(WEB_EMAIL, "Desktop Mail", "mailto:?subject={title}&body={desc}");
        services.add(deft);

        return services;
    }

    public static List<WebShareService> getSocialService() {
        List<WebShareService> services = new ArrayList<WebShareService>();

        WebShareService facebook = new WebShareService(WEB_FACEBOOK, "Facebook", "http://www.facebook.com/share.php?u={url}&t={title}");
        services.add(facebook);

        WebShareService myspace = new WebShareService(WEB_MYSPACE, "MySpace", "http://www.myspace.com/Modules/PostTo/Pages/?l=3&u={url}&t={title}&c={desc}");
        services.add(myspace);

        WebShareService delicious = new WebShareService(WEB_DELICIOUS, "Delicious", "http://delicious.com/save?url={url}&title={title}&notes={desc}&tags=bibtexbibliographysocial&noui=no&share=yes");
        services.add(delicious);

        WebShareService stumbleUpon = new WebShareService(WEB_SU, "StumbleUpon", "http://www.stumbleupon.com/submit?url={url}&title={title}");
        services.add(stumbleUpon);

        WebShareService friendFeed = new WebShareService(WEB_FRIENDFEED, "FriendFeed", "http://friendfeed.com/?url={url}&title={title}");
        services.add(friendFeed);

        return services;
    }

    public static class WebShareService {

        static Desktop d = Desktop.getDesktop();
        ImageIcon icon;
        String label;
        String url;

        public WebShareService(String iconPath, String label, String url) {
            this.icon = new ImageIcon(Loader.get(iconPath));
            this.label = label;
            this.url = url;
        }

        public ImageIcon getIcon() {
            return icon;
        }

        public String getLabel() {
            return label;
        }

        public void doAction(String linkUrl, String title, String desc) throws IOException, URISyntaxException {
            // add url to desc if this service don't use url (eg email)
            if (url.contains("{url}") == false) {
                desc = linkUrl + "\n\n" + desc;
            }
            d.browse(new URI(this.url.replaceAll("\\{url\\}", e(linkUrl)).replaceAll("\\{title\\}", e(title)).replaceAll("\\{desc\\}", e(desc))));
        }

        /**
         * short method name used to encode for url
         * @return encoded url
         */
        private String e(String data) throws UnsupportedEncodingException {
            return URLEncoder.encode(data, "UTF-8");
        }
    }
}
