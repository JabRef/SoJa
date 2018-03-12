package core;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.util.Collection;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.Border;
import model.friend.Friend;
import net.sf.jabref.BibtexEntry;

/**
 *
 * @author Thien Rong
 */
public interface ActionHandler {

    public void handleViewTag(String tag);

    public void handleViewFriend(Friend friend);

    public void handleChatFriend(Friend friend);

    public void handlePrepareEmail(Collection<BibtexEntry> entries, String to, String subject, String msg);

    public void handleSubscribe(Friend friend, BibtexEntry entry);

    public void handleAddBibtexCopy(Friend friend, BibtexEntry entry);

    public void handleSendFriendRequest(Friend friend);

    public void handleFriendRequest(Friend friend);

    public void handleAcceptedFriendRequest(Friend friend);

    public void handleEditFriend();

    public void handleRemoveFriend(Friend f);

    public static class TestActionHandler implements ActionHandler {

        public void handleViewTag(String tag) {
            System.out.println("handle tag " + tag);
        }

        public void handleViewFriend(Friend friend) {
            System.out.println("handle friend " + friend);
        }

        public void handlePrepareEmail(Collection<BibtexEntry> entries, String to, String subject, String msg) {
            System.out.println("handle Prepare Email");
        }

        public void handleSubscribe(Friend friend, BibtexEntry entry) {
            System.out.println("handle subscribe " + friend + " to " + entry);
        }

        public void handleChatFriend(Friend friend) {
            System.out.println("handle chat friend");
        }

        public void handleAddBibtexCopy(Friend friend, BibtexEntry entry) {
            System.out.println("handle add bibtex copy " + entry + " from " + friend);
        }

        public void handleAcceptedFriendRequest(Friend friend) {
            System.out.println("handle accepted friend request " + friend);
        }

        public void handleFriendRequest(Friend friend) {
            System.out.println("handle recv friend request " + friend);
        }

        public void handleEditFriend() {
            System.out.println("handle edit friend");
        }

        public void handleRemoveFriend(Friend f) {
            System.out.println("handle remove friend " + f);
        }

        public void handleSendFriendRequest(Friend friend) {
            System.out.println("handle send friend request " + friend);
        }
    }
}
