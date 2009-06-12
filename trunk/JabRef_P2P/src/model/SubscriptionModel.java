package model;

import model.friend.Friend;
import core.SidePanel;
import core.Store;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
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
        Store s = new Store("test");

        SubscriptionModel ss = new SubscriptionModel(s).load();
        System.out.println(ss.pushTo);
        System.out.println(ss.latestCopies);
    }
    private static final long serialVersionUID = 1L;
    // BUID and set of friends who subscribe to it
    Map<String, Set<String>> pushTo = new HashMap<String, Set<String>>();
    // friend ID and set of BUID subscribing to
    Map<String, Set<String>> pullFrom = new HashMap<String, Set<String>>();
    // my subscription BUID -> latest copy of BibtexEntry with Friend from
    // TODO should be list of subscription if multiple friends change
    LinkedHashMap<String, Subscription> latestCopies = new LinkedHashMap<String, Subscription>();
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

        Subscription oldCopy = latestCopies.get(BUID);

        // check if any changes from oldCopy
        if (oldCopy != null && latestCopyStr.equals(oldCopy.entryStr)) {
            return false;
        }

        add(pullFrom, friend.getFUID(), BUID);
        // null for oldValue to indicate new
        //propertyChangeSupport.firePropertyChange(SUBSCRIPTION, null, BUID);

        // oldCopy null indicate new
        Subscription c = new Subscription(latestCopyStr, friend, new Date());
        if (oldCopy != null) {
            // remove so that latest copies is last one, still in time order
            latestCopies.remove(BUID);
            latestCopies.put(BUID, c);
            propertyChangeSupport.firePropertyChange(MODIFIED_ENTRY, null, c);
        } else {
            latestCopies.put(BUID, c);
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
        latestCopies.remove(BUID);
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
        if (latestCopies.containsKey(BUID)) {
            updateSubscription(friend, entry);
        }
    }

    public void autoUpdateSubscription(final SidePanel main, final int delay) {
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

    public LinkedHashMap<String, Subscription> getLatestCopies() {
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

    public class Subscription implements Serializable {

        String entryStr;
        Friend friend;
        Date date;

        public Subscription(String entryStr, Friend friend, Date date) {
            this.entryStr = entryStr;
            this.friend = friend;
            this.date = date;
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
    }
}