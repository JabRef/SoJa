package model.friend;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.security.KeyPair;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import util.security.RSA;
import view.friend.Status;

/**
 * @author Thien Rong
 */
public class Friend implements Serializable {

    private static final long serialVersionUID = 2L;

    public static void main(String[] args) throws Exception {
        String[] keys1 = RSA.toBase64(RSA.generateKeyPair());
        String[] keys2 = RSA.toBase64(RSA.generateKeyPair());
        Friend f1 = new Friend(keys1[0], "test", "1", 1, 2);
        Friend f2 = new Friend(keys2[0], "test", "1", 1, 2);
        ObjectOutputStream o = new ObjectOutputStream(System.out);
        o.writeObject(f1);
        Map<Friend, String> ss = new HashMap<Friend, String>();
        ss.put(f1, "A");
        System.out.println(ss.get(f2));
    }
    // assume there is a unique global id
    // uses as RSA public key
    String FUID;
    String name;
    // Store the unique entry keys 
    //List<String> notInterestedEntries = new ArrayList<String>();
    // @TODO don't hardcode ip:port
    String ip;
    int port;
    int filePort;
    String currStatus = "What are you doing now?";
    private String avatarURL = null;
    String tags;
    // if not friend then false
    boolean isFriend;
    // transient
    transient PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
    transient boolean connected = false;
    transient List<Status> status = new Vector<Status>();

    /**
     * Don't use equals(Friend f) else HashMap won't .equals
     * @param obj
     * @return
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Friend other = (Friend) obj;
        if ((this.FUID == null) ? (other.FUID != null) : !this.FUID.equals(other.FUID)) {
            return false;
        }
        return true;
    }

    public boolean isConnected() {
        return connected;
    }

    /**
     * Need to be called by FriendStringCodec only since the rest don't have avatar url
     * @param FUID
     * @param name
     * @param ip
     * @param port
     * @param filePort
     * @param avatarURL
     */
    public Friend(String FUID, String name, String ip, int port, int filePort, String avatarURL, String tags) {
        this.FUID = FUID;
        this.name = name;
        this.ip = ip;
        this.port = port;
        this.filePort = filePort;
        this.avatarURL = avatarURL;
        this.tags = tags;
    }

    public Friend(String FUID, String name, String ip, int port, int filePort) {
        this(FUID, name, ip, port, filePort, null, null);
    }

    /**
     * @return the name (Online) if connected, else name (empty spaces so no need resize)
     */
    @Override
    public String toString() {
        String displayTxt = name;
        if (connected) {
            displayTxt += " (Online) " + currStatus;
        }
        return displayTxt;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + (this.FUID != null ? this.FUID.hashCode() : 0);
        return hash;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public void setConnected(boolean connected) {
        boolean oldValue = this.connected;
        this.connected = connected;
        propertyChangeSupport.firePropertyChange("connected", oldValue, connected);
    }

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    public String getName() {
        return name;
    }

    public String getFUID() {
        return FUID;
    }

    public int getFilePort() {
        return filePort;
    }

    public void setCurrStatus(String currStatus) {
        String oldValue = this.currStatus;
        this.currStatus = currStatus;
        propertyChangeSupport.firePropertyChange("currStatus", oldValue, currStatus);
    }

    /**
     * Add PropertyChangeListener.
     * @param listener
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    /**
     * Remove PropertyChangeListener.
     * @param listener
     */
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

    private void readObject(ObjectInputStream s) throws ClassNotFoundException, IOException {
        s.defaultReadObject();
        propertyChangeSupport = new PropertyChangeSupport(this);
    }

    /**
     * For XMLEncoder. DO NOT USE
     */
    public Friend() {
    }

    public void setFUID(String FUID) {
        this.FUID = FUID;
    }

    public void setFilePort(int filePort) {
        this.filePort = filePort;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getAvatarURL() {
        if (avatarURL == null || avatarURL.trim().length() == 0) {
            return "";
        }
        return avatarURL;
    }

    public String getTags() {
        if (tags == null) {
            return "";
        }
        return tags;
    }

    public String getCurrStatus() {
        return currStatus;
    }

    public void setStatus(List<Status> status) {
        for (Status status1 : status) {
            status1.setFriend(this);
        }
        this.status = status;
    }
}
