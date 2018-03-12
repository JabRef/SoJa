package core;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.friend.Friend;
import model.friend.FriendsModel;
import model.friend.Group;
import util.ObjectCleaner;

/**
 * Handle loading and storing of models  
 * @author Thien Rong
 */
public class Store {

    public static void main(String[] args) throws IOException {
        try {
            Store s = new Store("A");
            String path = "test";
            Group g = new Group("A");
            g.addFriend(new Friend("test", "test", "ip", 1, 1));
            g.addFriend(new Friend("test", "test", "ip2", 1, 1));
            s.writeXML(g, path);
            Group g2 = (Group) s.readXML(path);
            System.out.println(g2);
            for (Friend friend : g2.getFriends()) {
                System.out.println(friend);
            }
            FriendsModel m = new FriendsModel(s);
            System.out.println(m);
            m.addGroup(g2);
            m.save();
            FriendsModel m2 = new FriendsModel(s).load();
            System.out.println(m2.getGroups());
            System.out.println(m2.getFriends());
        } catch (Exception ex) {
            Logger.getLogger(Store.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    String name;

    public Store(String name) {
        this.name = name;
    }

    public void writeXML(Object obj, String path) {
        BufferedOutputStream bos = null;
        XMLEncoder e = null;
        try {
            bos = new BufferedOutputStream(new FileOutputStream(path + name + ".xml"));
            e = new XMLEncoder(bos);
            e.writeObject(obj);
        } catch (IOException ex) {
            //throw ex;
            debugMessage(ex, path, "writeXML");
        } finally {
            if (e != null) {
                e.close();
            }
        }
    }

    public Object readXML(String path) throws Exception {
        XMLDecoder d = null;
        BufferedInputStream bin = null;
        Object o = null;
        try {
            bin = new BufferedInputStream(new FileInputStream(path + name + ".xml"));
            d = new XMLDecoder(bin);
            o = d.readObject();
        } catch (Exception ex) {
            debugMessage(ex, path, "readXML");
            throw ex;
        } finally {
            if (d != null) {
                d.close();
            }
        }
        return o;
    }

    public void deleteObject(String path) {
        File f = new File(path + name + ".dat");
        f.delete();

    }

    public void writeObject(Serializable obj, String path) {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(path + name + ".dat");
            ObjectOutputStream o = new ObjectOutputStream(fos);
            o.writeObject(obj);
        } catch (IOException ex) {
            debugMessage(ex, path, "writeObject");
        //throw ex;
        } finally {
            ObjectCleaner.cleanCloseable(fos);
        }
    }

    public Object readObject(String path) throws Exception {
        FileInputStream fin = null;
        Object obj = null;
        try {
            fin = new FileInputStream(path + name + ".dat");
            ObjectInputStream i = new ObjectInputStream(fin);
            obj = i.readObject();
        } catch (Exception ex) {
            debugMessage(ex, path, "readObject");
            throw ex;
        } finally {
            ObjectCleaner.cleanCloseable(fin);
        }
        return obj;
    }

    public void debugMessage(Exception ex, String path, String action) {
        System.out.println(path + " not " + action);
        if (false == (ex instanceof FileNotFoundException)) {
            ex.printStackTrace();
        }
    }
}
