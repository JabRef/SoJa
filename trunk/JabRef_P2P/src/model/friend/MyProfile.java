package model.friend;

import model.*;
import core.Store;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.List;
import java.util.Vector;
import util.security.RSA;

/**
 * 
 * @author Thien Rong
 */
public class MyProfile extends Friend implements Serializable, Persistable<MyProfile> {

    public static void main(String[] args) throws Exception {
        /* Store zz = new Store("hoho");
        MyProfile zzz = new MyProfile(zz).load();
        System.out.println(zzz);*/

        Store s = new Store("test");
        MyProfile m = new MyProfile("Joe", 8010, 8011, RSA.generateKeyPair());
        m.save();

        MyProfile z = new MyProfile(s).load();
        System.out.println("zz " + z);
        z.setCurrentIP();
        System.out.println(z);
    }
    // not transient since needed to send over to others
    /* profile view not implemented */
    int profileViews = 0;
    private static final long serialVersionUID = 2L;
    String privateKey;
    // to store guid of msg read on the dht
    List<String> readMsgGUIDs = new Vector<String>();
    transient Store s;

    private MyProfile(Store s) {
        this.s = s;
    }

    public MyProfile(String name, int mainPort, int filePort, KeyPair keys) {
        super(RSA.toBase64(keys)[1], name, getCurrentIP(), mainPort, filePort);
        this.s = new Store(name);
        this.privateKey = RSA.toBase64(keys)[0];
    }

    public static MyProfile loadProfile(String name) {
        Store s = new Store(name);
        MyProfile p = new MyProfile(s);
        return p.load();
    }

    public String getPrivateKey() {
        return privateKey;
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

    public KeyPair getKeyPair() {
        PublicKey pub = RSA.toPublicKey(FUID);
        PrivateKey priv = RSA.toPrivateKey(privateKey);
        return new KeyPair(pub, priv);
    }
}
