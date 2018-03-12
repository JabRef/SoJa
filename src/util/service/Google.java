package util.service;

import java.net.URL;
import java.util.List;
import java.util.Map.Entry;
import util.HTTP14;
import util.HTTP14.WebResponse;

/**
 * 0.1 | 11/6/2009
 * + To import from Gmail
 * + gmail contacts too bulky with ext library
 * + ref(but bulky and not updated since Jan)
 * + http://code.google.com/p/contactlistimporter/downloads/list
 * @author Thien Rong
 */
public class Google {

    public static void main(String[] args) throws Exception {
        String loginUrl = "https://www.google.com/accounts/ServiceLoginAuth";
        String invalid = "The username or password you entered is incorrect";
        String dataNeeded = "continue=https://mail.google.com/mail/&";
//"ltmpl", "yj_blanco"), new BasicNameValuePair("continue", "https://mail.google.com/mail/"), new BasicNameValuePair("ltmplcache", "2"), new BasicNameValuePair("service", "mail"), new BasicNameValuePair("rm", "false"), new BasicNameValuePair("hl", "en"), new BasicNameValuePair("Email", getUsername()), new BasicNameValuePair("Passwd", getPassword()), new BasicNameValuePair("rmShown", "1"), new BasicNameValuePair("null", "Sign in")
        String email = "karaholicsg";
        String password = "p9EdrE5Ua";

        long time = System.currentTimeMillis();
        String cookieStr = "GMAIL_LOGIN: T"+time+"/"+(time - 16L)+"/"+time;

        //WebResponse resp = HTTP14.getConn(loginUrl, HTTP14.GET, null, null);
        StringBuilder sb = new StringBuilder();
        WebResponse resp = HTTP14.getConn(loginUrl, HTTP14.POST, dataNeeded+"Email=" + email + "&Passwd=" + password, cookieStr);
        //String content = resp.getContent();
        //System.out.println(resp.getResponseMessage());
        //System.out.println(resp.getRespField("location"));
        
        for (Entry<String, List<String>> entries : resp.getRqstHeader().entrySet()) {
            sb.append(entries.getKey() + ": " + entries.getValue() + "\n");
        }
        System.out.println(resp.getResponseMessage());
        System.out.println(sb.toString());
        System.out.println(HTTP14.writeToString(resp, "UTF-8"));

        if(resp.getResponseMessage().contains("302")){

        }else{
            throw new Exception("Login Failed");
        }

    //System.out.println(content);
    //URL url = resp.getURL();
    //System.out.println(url);
    //System.out.println(content);
        /*if (content.contains(invalid)) {
    System.out.println("INVALID");
    } else {
    System.out.println("OKAy");
    }*/

    //"https://www.google.com/accounts/CheckCookie?chtml=LoginDoneHtml"
    }
}
