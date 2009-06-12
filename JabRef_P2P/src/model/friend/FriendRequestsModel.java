package model.friend;

import model.*;
import core.Store;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Thien Rong
 */
public class FriendRequestsModel implements Serializable, Persistable<FriendRequestsModel> {

    private static final long serialVersionUID = 1L;
    private static final String PERSIST_NAME = "Requests";
    ArrayList<Friend> requests = new ArrayList<Friend>();
    private transient PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
    public static final String REQUEST_ADD = "request_add",  REQUEST_REMOVE = "request_remove";
    transient Store s;

    public FriendRequestsModel(Store s) {
        this.s = s;
    }

    public boolean addRequest(Friend request) {
        if (requests.add(request)) {
            propertyChangeSupport.firePropertyChange(REQUEST_ADD, null, request);
            save();
            return true;
        }
        return false;
    }

    public boolean removeRequest(Friend request) {
        if (requests.remove(request)) {
            propertyChangeSupport.firePropertyChange(REQUEST_REMOVE, request, null);
            save();
            return true;
        }
        return false;
    }

    public ArrayList<Friend> getRequests() {
        return requests;
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

    @SuppressWarnings("unchecked")
    public FriendRequestsModel load() {
        try {
            ArrayList<Friend> r = (ArrayList<Friend>) s.readObject(PERSIST_NAME);
            this.requests = r;
        } catch (Exception ex) {
            s.debugMessage(ex, PERSIST_NAME, "load");
        }
        return this;
    }

    public void save() {
        s.writeObject(this.requests, PERSIST_NAME);
    }

    public void delete() {
        s.deleteObject(PERSIST_NAME);
    }
}
