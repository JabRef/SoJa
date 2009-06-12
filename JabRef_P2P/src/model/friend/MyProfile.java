package model.friend;

import model.*;
import core.Store;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * 
 * @author Thien Rong
 */
public class MyProfile extends Friend implements Serializable, Persistable<MyProfile> {

    public static void main(String[] args) {
        Store s = new Store("test");
        MyProfile m = new MyProfile("Joe", "Joe", 8010, 8011);
        m.save();

        MyProfile z = new MyProfile(s).load();
        z.setCurrentIP();
        System.out.println(z);
    }

    // not transient since needed to send over to others
    int profileViews = 0;
    private static final long serialVersionUID = 1L;
    Store s;

    public MyProfile(Store s) {
        this.s = s;
    }

    public MyProfile(String name, String FUID, int mainPort, int filePort) {
        super(name, FUID, getCurrentIP(), mainPort, filePort);
    }

    public static String getCurrentIP() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException ex) {
            System.out.println("Unable to find IP: " + ex.getMessage());
        }
        return null;
    }

    public void setCurrentIP() {
        this.ip = getCurrentIP();
    }

    public MyProfile load() {
        try {
            MyProfile m = (MyProfile) s.readObject("Profile");
            return m;
        } catch (Exception ex) {
            System.out.println("MyProfile not load: " + ex.getMessage());
        }
        return this;
    }

    public void save() {
        try {
            s.writeObject(this, "Profile");
        } catch (Exception ex) {
            System.out.println("MyProfile not saved: " + ex.getMessage());
        }
    }

    public void delete() {
        try {
            s.deleteObject("Profile");
        } catch (Exception ex) {
            System.out.println("MyProfile not deleted: " + ex.getMessage());
        }
    }

    /*    @Override
    public String toString() {
    return name + ", " + FUID + ", " + ip + ", " + filePort + ", " + port;
    }*/
    /**** GETTERS ********/
    public int getProfileViews() {
        return profileViews;
    }
}
