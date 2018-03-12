package util.thread;

import core.SidePanel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import javax.swing.Timer;
import model.friend.Friend;
import util.visitor.FriendVisitor;
import util.visitor.TagFreqVisitor;

/**
 * 0.1 | 10 Sep 2009
 * + Add collect for own tag
 * @author Thien Rong
 */
public class TagsCollectorThread extends Thread {

    boolean active = true;
    Map<String, Timer> timers = new HashMap<String, Timer>();
    // delay for timer
    int defaultDelay;
    // delay to check for new friends to collect tags
    int checkNewFriendsDelay;
    SidePanel main;

    public TagsCollectorThread(int defaultDelay, int checkNewFriendsDelay, SidePanel main) {
        this.defaultDelay = defaultDelay;
        this.checkNewFriendsDelay = checkNewFriendsDelay;
        this.main = main;
    }

    public void run() {
        while (active) {
            try {
                Thread.sleep(checkNewFriendsDelay);
            } catch (InterruptedException ex) {
            }

            TagFreqVisitor tfVisitor = main.getLocalTagFreqVisitor();
            main.setMyTags(tfVisitor.getTagFreq());

            main.getFriendsModel().visitAllFriends(new FriendVisitor() {

                public void visitFriend(Friend f) {
                    if (f.isConnected()) {
                        setTimer(f.getFUID(), false);
                    } else {
                        removeTimer(f.getFUID());
                    }
                }
            });

        }

    }

    /**
     * If reset and not exists => create and start
     * If reset and exists => restart
     * If not reset and not exists => create and start
     * If not reset and exists => do nothing
     *
     * @param FUID
     * @param reset whether to reset timer if exists
     */
    private void setTimer(final String FUID, final boolean reset) {
        Timer t = timers.get(FUID);
        boolean exists = (t != null);

        if (t == null) {
            t = new Timer(defaultDelay, new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    System.out.println("auto collecting tags");
                    main.getDealer().sendBrowseRequest(FUID, null);
                    // don't need reset unless the packet loss since will get
                    // result and auto reset
                    //resetTimer(FUID);
                }
            });
            t.setRepeats(false);
            timers.put(FUID, t);
        }

        t.setInitialDelay(defaultDelay);
        if (!exists || reset) {
            System.out.println("starting auto collecting tags for " + FUID);
            t.restart();
        }
    }

    /**
     * Called when browse or anything manual, so reset timer
     * @param FUID
     */
    public void resetTimer(String FUID) {
        setTimer(FUID, true);
    }

    private void removeTimer(String FUID) {
        Timer t = timers.remove(FUID);
        if (t != null) {
            t.stop();
        }
    }
}
