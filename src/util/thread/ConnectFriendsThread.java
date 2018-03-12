package util.thread;

import core.SidePanel;
import model.friend.Friend;
import util.visitor.FriendVisitor;

/**
 * Will auto-connect to unconnected friends
 * @author Thien Rong
 */
public class ConnectFriendsThread extends Thread {

    SidePanel main;
    int delay;

    public ConnectFriendsThread(SidePanel main, int delay) {
        this.main = main;
        this.delay = delay;
        this.setDaemon(true);
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(delay);
            } catch (InterruptedException ex) {
            }

            main.getFriendsModel().visitAllFriends(new FriendVisitor() {

                public void visitFriend(Friend f) {
                    if (false == f.isConnected()) {
                        main.doConnect(f, true);
                    }
                }
            });
        }
    }
}
