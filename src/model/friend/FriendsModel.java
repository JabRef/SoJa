package model.friend;

import model.*;
import core.Store;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import util.visitor.FriendVisitor;

/**
 * Base Model for friends, sub classes are the model for jtree and jlist
 * @author Thien Rong
 */
public class FriendsModel implements Persistable<FriendsModel> {

    // for faster reference only
    transient Map<String, Friend> friends = new HashMap<String, Friend>();
    // groupName -> Group object
    HashMap<String, Group> groups = new HashMap<String, Group>();
    Store s;
    // needed when load because got to add it unless listener when finish loading for tree
    private transient Group deftGrp;
    static final String DEFAULT_GROUP_NAME = "Not Grouped";
    private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
    public static final String GROUP_ADD = "Group Add";
    public static final String FRIEND_ADD = "Friend Add";
    public static final String FRIEND_REMOVE = "Friend Remove";
    private static final String PERSIST_NAME = "Groups";
    private transient boolean newUser;

    public FriendsModel(Store s) {
        this.s = s;
    }

    public void addFriendToDeftGroup(Friend friend) {
        this.addFriend(friend, deftGrp);
    }

    @SuppressWarnings("unchecked")
    public FriendsModel load() {
        try {
            ArrayList<Group> g = (ArrayList<Group>) s.readObject(PERSIST_NAME);
            //ArrayList<Group> g = (ArrayList<Group>) s.readXML("Groups");
            for (Group group : g) {
                this.addGroup(group);
            }
        } catch (FileNotFoundException ex) {
            newUser = true;
        } catch (Exception ex) {
            s.debugMessage(ex, PERSIST_NAME, "load");
        }
        // add default group if load fail (if pass will not since duplicate)
        addGroup(new Group(DEFAULT_GROUP_NAME));

        return this;
    }

    /**
     * Visit 1 level down (Should not have future levels)
     * @param groupName if not found, will not do anything
     * @param v visitor
     */
    public void visitFriends(String groupName, FriendVisitor v) {
        Group g = groups.get(groupName);
        if (g != null) { // Just in case
            g.visitFriend(v);
        }

    }

    public void visitAllFriends(FriendVisitor v) {
        for (Friend friend : friends.values()) {
            v.visitFriend(friend);
        }
    }

    public Group getGroup(String grpName) {
        return groups.get(grpName);
    }

    public Collection<Friend> getFriends() {
        return friends.values();
    }

    public HashMap<String, Group> getGroups() {
        return groups;
    }

    public void addFriend(Friend friend, Group g) {
        String FUID = friend.getFUID();

        if (friends.containsKey(FUID) == false) {
            friends.put(FUID, friend);
            g.addFriend(friend);
            propertyChangeSupport.firePropertyChange(FRIEND_ADD, null, new FriendGroup(friend, g));
            save();
        }
    }

    public void addGroup(Group g) {
        // if exists dont add
        String grpName = g.name;
        if (groups.containsKey(grpName)) {
            return;
        }

        // add to root
        groups.put(grpName, g);
        // set as default group if name match
        if (g.name.equals(FriendsModel.DEFAULT_GROUP_NAME)) {
            this.deftGrp = g;
        }

        propertyChangeSupport.firePropertyChange(GROUP_ADD, null, g);
        save();
        // add friends if any (during load)
        for (Friend friend : g.getFriends()) {
            friends.put(friend.getFUID(), friend);
            propertyChangeSupport.firePropertyChange(FRIEND_ADD, null, new FriendGroup(friend, g));
        }
    }

    public void removeFriend(Friend friend) {
        friends.remove(friend.FUID);
        Group g = null;
        for (Group group : groups.values()) {
            if (group.remove(friend)) {
                g = group;
                break;
            }
        }

        if (g != null) {
            save();
            propertyChangeSupport.firePropertyChange(FRIEND_REMOVE, new FriendGroup(friend, g), null);
        }

    }

    public Friend findFriend(String FUID) {
        return friends.get(FUID);
    }

    public void save() {
        try {
            ArrayList<Group> g = new ArrayList<Group>(groups.values());
            s.writeObject(g, PERSIST_NAME);
            //s.writeXML(g, "Groups");

        } catch (Exception ex) {
            System.out.println("Friend & Groups not saved: " + ex.getMessage());
        }
    }

    /**
     * Add PropertyChangeListener.
     *
     * @param listener
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    /**
     * Remove PropertyChangeListener.
     *
     * @param listener
     */
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

    public void delete() {
        try {
            s.deleteObject(PERSIST_NAME);
        } catch (Exception ex) {
            System.out.println("Requests not deleted: " + ex.getMessage());
        }
    }

    public boolean isNew() {
        return newUser;
    }
}
