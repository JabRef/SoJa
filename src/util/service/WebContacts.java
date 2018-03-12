package util.service;

import com.xdatasystem.contactsimporter.ContactListImporter;
import com.xdatasystem.contactsimporter.ContactListImporterFactory;
import com.xdatasystem.user.Contact;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import java.util.List;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import winterwell.jtwitter.Twitter;

/**
 * 0.1 | 11/6/2009
 * + To import from Gmail, Yahoo (hotmail not working)
 * + gmail contacts library too bulky with ext library
 * + from
 * + http://code.google.com/p/contactlistimporter/downloads/list
 * @author Thien Rong
 */
public class WebContacts {

    /**
     *
     * @param email need to be full xxx@gmail.com or xxx@yahoo.com
     * @param password
     * @return List with contacts if any
     * @throws java.lang.Exception
     */
    public static List<Contact> getContacts(String email, String password) throws Exception {
        ContactListImporter l = ContactListImporterFactory.guess(email, password);
        List<Contact> ll = l.getContactList();
        return ll;
    }

    public static void sendYahoo(String[] to, final String email, final String password) throws Exception {
        String smtpHost = "smtp.mail.yahoo.com";
        String SMTP_PORT = "25";
        Properties props = System.getProperties();
        final String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";

        // Setup mail server
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.host", smtpHost);
        props.put("mail.debug", "true");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", SMTP_PORT);
        //    props.put("mail.smtp.socketFactory.port", SMTP_PORT);
        //    props.put("mail.smtp.socketFactory.class", SSL_FACTORY);
        //   props.put("mail.smtp.socketFactory.fallback", "false");
        props.setProperty("mail.smtp.quitwait", "false");
        // Get session
        Session session = Session.getInstance(props, new javax.mail.Authenticator() {

            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(email, password);
            }
        });

        WebContacts.sendInvite(to, email, session);
    }

    public static void sendHotmail(String[] to, final String email, final String password) throws Exception {
        String smtpServer = "smtp.live.com";
        String SMTP_PORT = "587";
        final String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";

        Properties props = new Properties();
        // -- Attaching to default Session, or we could start a new one --
        props.put("mail.from", email);
        props.put("mail.debug", "true");

        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.host", smtpServer);
        props.put("mail.smtp.host", smtpServer);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", SMTP_PORT);
        props.put("mail.smtp.socketFactory.port", SMTP_PORT);
        props.put("mail.smtp.socketFactory.fallback", "false");
        props.setProperty("mail.smtp.quitwait", "false");

        Session session = Session.getInstance(props, new javax.mail.Authenticator() {

            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(email, password);
            }
        });

        WebContacts.sendInvite(to, email, session);
    }

    private static void sendInvite(String[] to, String email, Session session) throws NoSuchProviderException, MessagingException {
        Transport t = session.getTransport();
        t.connect();

        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress(email));

        InternetAddress[] to_address = new InternetAddress[to.length];
        for (int i = 0; i < to_address.length; i++) {
            to_address[i] = new InternetAddress(to[i]);
            message.addRecipient(Message.RecipientType.TO, to_address[i]);
        }
        System.out.println(Message.RecipientType.TO);

        message.setSubject("sending in a group");
        message.setText("Welcome to JavaMail");
        t.sendMessage(message, message.getAllRecipients());
    }

    public static void sendGmail(String[] to, final String email, final String password) throws NoSuchProviderException, MessagingException {
        final String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";
        Properties props = new Properties();
        props.setProperty("mail.transport.protocol", "smtp");
        props.setProperty("mail.host", "smtp.gmail.com");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class", SSL_FACTORY);
        props.put("mail.smtp.socketFactory.fallback", "false");
        props.put("mail.smtp.quitwait", "false");

        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {

                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(email, password);
                    }
                });
        WebContacts.sendInvite(to, email, session);
    }

    public static void main(String[] args) throws Exception {
        String email = "sky@ne.de";
        String password = "bug2menot";
        ContactListImporter i = ContactListImporterFactory.hotmail(email, password);
        i.getContactList();
        for (Contact contact : getContacts(email, password)) {
            System.out.println(contact);
        }
        //sendYahoo(new String[]{"bnxx6778@gmail.com"}, email, password);

        // Make a Twitter object
        Twitter twitter = new Twitter("bugmenot20091", "bugmenot");
        // Print Daniel Winterstein's status
        System.out.println(twitter.getStatus("winterstein"));
        // Set my status
        for (Twitter.User user : twitter.getFriends()) {
            System.out.println(user + ": " + user.getStatus());
        }
    }
}
