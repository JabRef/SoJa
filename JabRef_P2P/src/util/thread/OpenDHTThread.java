package util.thread;

import core.SidePanel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.friend.Friend;
import model.friend.MyProfile;
import util.OpenDHTHelper;
import util.visitor.TagFreqVisitor;
import view.friend.FriendsPanel;

/**
 * Gather user lists in bg since slow to verify and get 1-by-1
 * @author Thien Rong
 */
public class OpenDHTThread extends Thread {

    public static void main(String[] args) {
        new OpenDHTThread(null).start();
    }
    SidePanel main;
    OpenDHTHelper dht = new OpenDHTHelper();
    boolean active = true;
    Set<Friend> friends = new HashSet<Friend>();
    int delay = 60000; // should be longer than 1 query else will have overlap queries

    public OpenDHTThread(SidePanel main) {
        this.main = main;
    }

    public void run() {
        while (active) {
            try {
                updateMyDetails();
                // upload current user lists
                for (Friend friend : friends) {
                    updateUserList(friend.getFUID());
                }

                updateCache();

                try {
                    Thread.sleep(delay);
                } catch (InterruptedException ex) {
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * Delegate method
     * @param newStatus
     * @param my
     * @throws Exception
     */
    public void updateStatus(String newStatus, MyProfile my) {
        try {
            dht.updateStatus(newStatus, my);
        } catch (Exception ex) {
            Logger.getLogger(OpenDHTThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void updateMyDetails() {
        MyProfile my = main.getMyProfile();

        // update my details and to main user list
        if (my != null) {
            // try get tags if empty
            if (my.getTags().length() == 0) {
                TagFreqVisitor tfVisitor = main.getLocalTagFreqVisitor();
                main.setMyTags(tfVisitor.getTagFreq());
            }
            my = main.getMyProfile();
           
            try {
                dht.updateMyDetails(my);
                // upload current user lists
                updateUserList(my.getFUID());
            } catch (Exception ex) {
                Logger.getLogger(OpenDHTThread.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * Delegate method
     * @param userPublicKey
     * @throws Exception
     */
    public void updateUserList(String userPublicKey) {
        try {
            dht.updateUserList(userPublicKey);
        } catch (Exception ex) {
            Logger.getLogger(OpenDHTThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Collection<Friend> getFromCache() {
        // update if empty
        try {
            if (friends.size() == 0) {
                updateCache();
            }
        } catch (Exception ex) {
            System.out.println("ignored ");
        }
        return friends;
    }

    private void updateCache() throws Exception {
        // update user list
        friends.addAll(dht.getAll(dht.getUserList()));
    }

    
}
