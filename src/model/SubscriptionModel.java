package model;

import model.friend.Friend;
import core.SidePanel;
import core.Store;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sf.jabref.BibtexEntry;
import util.BibtexStringCodec;
import util.CustomBibtexField;

/**
 * Keep track of subscriptions to and from others
 * @author Thien Rong
 */
public class SubscriptionModel implements Serializable, Persistable<SubscriptionModel> {

    public static void main(String[] args) throws Exception {
        /*SubscriptionModel model = new SubscriptionModel();
        model.addSubscriber("Test", "User");
        model.save();*/
        Store s = new Store("Joe");

        SubscriptionModel ss = new SubscriptionModel(s).load();
        System.out.println(ss.pushTo);
        System.out.println(ss.latestCopies);
        for (CompositeSubscriptionKey compositeSubscriptionKey : ss.latestCopies.keySet()) {
            System.out.println(compositeSubscriptionKey.BUID);
            System.out.println(compositeSubscriptionKey.FUID);
        }
    }
    private static final long serialVersionUID = 1L;
    // BUID and set of friends who subscribe to it
    Map<String, Set<String>> pushTo = new HashMap<String, Set<String>>();
    // friend ID and set of BUID subscribing to
    Map<String, Set<String>> pullFrom = new HashMap<String, Set<String>>();
    // my subscription BUID, friend FUID -> Subscription latest copy of BibtexEntry
    LinkedHashMap<CompositeSubscriptionKey, Subscription> latestCopies = new LinkedHashMap<CompositeSubscriptionKey, Subscription>();
    private transient PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
    public static String SUBSCRIBER = "SUBSCRIBER",  SUBSCRIPTION = "SUBSCRIPTION", //
             NEW_ENTRY = "NEW_ENTRY",  MODIFIED_ENTRY = "MODIFIED_ENTRY", //
             REFRESHING = "REFRESHING",  DELETED_ENTRY = "DELETED_ENTRY";
    transient Store s;

    public SubscriptionModel(Store s) {
        this.s = s;
    }

    public void addSubscriber(String BUID, String FUID) {
        add(pushTo, BUID, FUID);
        // null for oldValue to indicate new
        propertyChangeSupport.firePropertyChange(SUBSCRIBER, null, BUID);
        save();
    }

    /**
     * Only call if entries are inside
     * Add/Modify to pullFrom and latestCopies
     * @param friend
     * @param entry
     *
     */
    public boolean updateSubscription(Friend friend, BibtexEntry entry) {
        String BUID = CustomBibtexField.getBUID(entry);
        if (BUID == null) {
            return false;
        }

        String latestCopyStr;
        try {
            latestCopyStr = BibtexStringCodec.toString(entry);
        } catch (IOException ex) {
            ex.printStackTrace();
            return false;
        }

        CompositeSubscriptionKey key = new CompositeSubscriptionKey(BUID, friend.getFUID());
        Subscription oldCopy = latestCopies.get(key);
        // check if any changes from oldCopy
        if (oldCopy != null) {
            if (oldCopy.entryStr.equals(latestCopyStr)) {
                return false;
            }

        }

        add(pullFrom, friend.getFUID(), BUID);
        // null for oldValue to indicate new
        //propertyChangeSupport.firePropertyChange(SUBSCRIPTION, null, BUID);

        // oldCopy null indicate new
        Subscription c = new Subscription(latestCopyStr, BUID, friend, new Date());

        if (oldCopy != null) {
            // remove so that latest copies is last one, still in time order
            latestCopies.remove(oldCopy.getKey());
            latestCopies.put(c.getKey(), c);
            propertyChangeSupport.firePropertyChange(MODIFIED_ENTRY, null, c);
        } else {
            latestCopies.put(c.getKey(), c);
            propertyChangeSupport.firePropertyChange(NEW_ENTRY, null, c);
        }

        save();
        return true;
    }

    public void deleteSubscription(Subscription c) {
        BibtexEntry entry = c.getEntry();
        String BUID = CustomBibtexField.getBUID(entry);
        if (BUID == null) {
            return;
        }

        Friend friend = c.getFriend();
        latestCopies.remove(c.getKey());
        Set<String> BUIDs = pullFrom.get(friend.getFUID());
        BUIDs.remove(BUID);

        propertyChangeSupport.firePropertyChange(DELETED_ENTRY, c, null);
        save();

    }

    /**
     * Check if part of subcription 1st. If yes then check for changes and fire
     * listeners accordingly
     * @param friend
     * @param entry
     */
    public void checkExistingSubscription(Friend friend, BibtexEntry entry) {
        String BUID = CustomBibtexField.getBUID(entry);
        if (BUID == null) {
            return;
        }

        CompositeSubscriptionKey key = new CompositeSubscriptionKey(BUID, friend.getFUID());
        if (latestCopies.containsKey(key)) {
            updateSubscription(friend, entry);
        }

    }

    public void autoUpdateSubscription(final SidePanel main,
            final int delay) {
        new Thread() {

            public void run() {
                while (true) {
                    try {
                        Thread.sleep(delay);
                    } catch (InterruptedException ex) {
                    }

                    updateSubscriptions(main);
                }

            }
        }.start();
    }

    /**
     * Send request for any changes
     * @param main
     */
    public void updateSubscriptions(SidePanel main) {
        propertyChangeSupport.firePropertyChange(REFRESHING, null, true);
        for (String FUID : pullFrom.keySet()) {
            Friend f = main.findFriend(FUID);
            if (f != null && f.isConnected()) {
                main.getDealer().sendSubscriptionUpdateRequest(f.getFUID());
            }

        }
    }

    private void add(Map<String, Set<String>> map, String key, String value) {
        Set<String> values = map.get(key);
        if (values == null) {
            values = new HashSet<String>();
            map.put(key, values);
        }

        values.add(value);
    }

    public LinkedHashMap<CompositeSubscriptionKey, Subscription> getLatestCopies() {
        return latestCopies;
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

    public void save() {
        s.writeObject(this, "Subscription");
    }

    /**
     * Load the model if no error else, return this
     * @return
     */
    public SubscriptionModel load() {
        try {
            SubscriptionModel model = (SubscriptionModel) s.readObject("Subscription");
            model.s = this.s;
            model.propertyChangeSupport = new PropertyChangeSupport(this);
            return model;
        } catch (Exception ex) {
            s.debugMessage(ex, "Subscription", "load");
        }

        return this;
    }

    public void delete() {
        s.deleteObject("Subscription");
    }

    /**
     * Composite key of BUID and FUID
     */
    public class CompositeSubscriptionKey implements Serializable {

        String BUID;
        String FUID;

        public CompositeSubscriptionKey(String BUID, String FUID) {
            this.BUID = BUID;
            this.FUID = FUID;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final CompositeSubscriptionKey other = (CompositeSubscriptionKey) obj;
            if ((this.BUID == null) ? (other.BUID != null) : !this.BUID.equals(other.BUID)) {
                return false;
            }
            if ((this.FUID == null) ? (other.FUID != null) : !this.FUID.equals(other.FUID)) {
                return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
            int hash = 3;
            hash = 59 * hash + (this.BUID != null ? this.BUID.hashCode() : 0);
            hash = 59 * hash + (this.FUID != null ? this.FUID.hashCode() : 0);
            return hash;
        }
    }

    public class Subscription implements Serializable {

        String entryStr;
        Friend friend;
        Date date;
        CompositeSubscriptionKey key;

        public Subscription(String entryStr, String BUID, Friend friend, Date date) {
            this.entryStr = entryStr;
            this.friend = friend;
            this.date = date;
            key = new CompositeSubscriptionKey(BUID, friend.getFUID());
        }

        public BibtexEntry getEntry() {
            return BibtexStringCodec.fromString(entryStr);
        }

        public Friend getFriend() {
            return friend;
        }

        public Date getDate() {
            return date;
        }

        public CompositeSubscriptionKey getKey() {
            return key;
        }
    }
}