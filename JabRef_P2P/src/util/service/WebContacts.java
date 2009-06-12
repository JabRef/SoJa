package util.service;

import com.xdatasystem.contactsimporter.ContactListImporter;
import com.xdatasystem.contactsimporter.ContactListImporterFactory;
import com.xdatasystem.user.Contact;
import java.util.List;
import java.util.Map.Entry;
import util.HTTP14;
import util.HTTP14.WebResponse;

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

    public static void main(String[] args) throws Exception {
    }
}
