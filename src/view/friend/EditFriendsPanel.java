package view.friend;

import core.ActionHandler;
import core.SidePanel;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import model.friend.Friend;
import model.friend.FriendGroup;
import model.friend.FriendsModel;
import model.friend.Group;
import net.sf.jabref.BibtexEntry;
import util.Loader;
import view.ImageConstants;
import view.SplitPanel;

/**
 * 0.2 | 24 August 2009
 * + Use only invite code to add friend
 * + Hide groups
 * 
 * Contains the addFriends, add button and edit list
 * Assumption that this is in dialog, so the friends list will not be
 * added or updated from other places to ensure correctness
 * @author Thien Rong
 */
public class EditFriendsPanel extends JPanel implements ImageConstants {

    public static void main(String[] args) {
        /*
        JFrame f = FrameCreator.createTestFrame();
        FriendsModel m = new FriendsModel(new Store("A"));

        final EditFriendsPanel p = new EditFriendsPanel(m);
        f.add(p);

        m.load();

        Group g = new Group("test");
        m.addGroup(g);
        //m.addFriend(new Friend("test", "test", "ip", 1, 1), g);
        //m.addFriend(new Friend("test2", "test2", "ip", 1, 1), g);
        m.save();
        FrameCreator.packAndShow(f);*/
    }
    final DefaultListModel gModel = new DefaultListModel();
    final JList currentGroupList = new JList(gModel);
    final AddFriendPanel2 p = new AddFriendPanel2();
    final FriendsModel model;

    public EditFriendsPanel(final SidePanel main, boolean edit) {
        this.setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
        final DefaultListModel fModel = new DefaultListModel();

        // add items that was loaded already
        this.model = main.getFriendsModel();
        for (Group group : model.getGroups().values()) {
            gModel.addElement(group);
            for (Friend friend : group.getFriends()) {
                fModel.addElement(friend);
            }
        }
        currentGroupList.setSelectedIndex(0);

        // register listeners for group and for friend
        model.addPropertyChangeListener(new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals(FriendsModel.GROUP_ADD)) {
                    Group g = (Group) evt.getNewValue();
                    gModel.addElement(g);
                    if (currentGroupList.getSelectedValue() == null) {
                        currentGroupList.setSelectedIndex(0);
                    }
                } else if (evt.getPropertyName().equals(FriendsModel.FRIEND_ADD)) {
                    FriendGroup fg = (FriendGroup) evt.getNewValue();
                    fModel.addElement(fg.getF());
                } else if (evt.getPropertyName().equals(FriendsModel.FRIEND_REMOVE)) {
                    FriendGroup fg = (FriendGroup) evt.getOldValue();
                    fModel.removeElement(fg.getF());
                }
            }
        });



        JPanel pnlEasyInvite = new JPanel();
        pnlEasyInvite.setLayout(new BoxLayout(pnlEasyInvite, BoxLayout.Y_AXIS));
        JButton btnFind = new JButton("<html>Find/Add others using <br>JabRef P2P to your network</html>", new ImageIcon(Loader.get(SEARCH)));
        JButton btnInvite = new JButton("<html>Cannot find your friends?<br/>Send them an invite to join you!</html>", new ImageIcon(Loader.get(INBOX)));

        pnlEasyInvite.add(btnFind);
        pnlEasyInvite.add(btnInvite);
        currentGroupList.setPrototypeCellValue("Choose Group");
        currentGroupList.setBorder(BorderFactory.createTitledBorder("Choose Group"));


        JButton btnAdd = new JButton("Add >>");
        btnAdd.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                addFriend();
            }
        });

        btnFind.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                final FriendsPanel fp = new FriendsPanel(new ActionHandler() {

                    public void handleViewTag(String tag) {
                        throw new UnsupportedOperationException("Not supported yet.");
                    }

                    public void handleViewFriend(Friend friend) {
                        p.setText(friend.getFUID());

                        if (addFriend()) {
                            // success
                            JOptionPane.showMessageDialog(currentGroupList, "Friend added successfully");
                        }

                    }

                    public void handleChatFriend(Friend friend) {
                        throw new UnsupportedOperationException("Not supported yet.");
                    }

                    public void handlePrepareEmail(Collection<BibtexEntry> entries, String to, String subject, String msg) {
                        throw new UnsupportedOperationException("Not supported yet.");
                    }

                    public void handleSubscribe(Friend friend, BibtexEntry entry) {
                        throw new UnsupportedOperationException("Not supported yet.");
                    }

                    public void handleAddBibtexCopy(Friend friend, BibtexEntry entry) {
                        throw new UnsupportedOperationException("Not supported yet.");
                    }

                    public void handleSendFriendRequest(Friend friend) {
                        throw new UnsupportedOperationException("Not supported yet.");
                    }

                    public void handleFriendRequest(Friend friend) {
                        throw new UnsupportedOperationException("Not supported yet.");
                    }

                    public void handleAcceptedFriendRequest(Friend friend) {
                        throw new UnsupportedOperationException("Not supported yet.");
                    }

                    public void handleEditFriend() {
                        throw new UnsupportedOperationException("Not supported yet.");
                    }

                    public void handleRemoveFriend(Friend f) {
                        throw new UnsupportedOperationException("Not supported yet.");
                    }
                }, "My Friends",
                        FriendsPanel.MODE_NAME_TAG_STATUS);

                fp.setFriends(main.getDht().getFromCache());
                JDialog frameFind = new JDialog(main.getFrame(), "Find others");
                frameFind.add(new JScrollPane(fp));
                frameFind.setSize(500, 500);
                frameFind.setVisible(true);
            }
        });

        btnInvite.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                JFrame frame = new JFrame("Invite Friends");
                frame.add(new InviteFriendsPanel(main));
                frame.setSize(600, 300);
                frame.setVisible(true);
            }
        });


        final JList currentFriendList = new JList(fModel);
        currentFriendList.setBorder(BorderFactory.createTitledBorder("Current Friend List"));
        currentFriendList.setPrototypeCellValue("Current Friend List");


        //@TODO
        JPanel pnlCtrl = new JPanel(new GridLayout(1, 0));
        JButton btnEdit = new JButton("Edit *TODO*");
        JButton btnDelete = new JButton("Delete");
        btnEdit.setEnabled(false);
        pnlCtrl.add(btnEdit);
        pnlCtrl.add(btnDelete);
        btnDelete.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                Friend f = (Friend) currentFriendList.getSelectedValue();
                if (f != null) {
                    model.removeFriend(f);
                }
            }
        });

        JPanel pnlAdd;
        JPanel pnlGroups = new JPanel();
        pnlGroups.setLayout(new BoxLayout(pnlGroups, BoxLayout.LINE_AXIS));

        //pnlGroups.add(new JScrollPane(currentGroupList));
        //pnlGroups.add(btnAdd);
        pnlGroups.add(new SplitPanel(new JScrollPane(currentFriendList), pnlCtrl, BorderLayout.SOUTH));

        pnlAdd = new SplitPanel(pnlGroups, new SplitPanel(btnAdd, p, BorderLayout.WEST), BorderLayout.NORTH);

        pnlAdd.setBorder(BorderFactory.createTitledBorder("Add your friends!"));
        this.add(pnlAdd);
        this.add(pnlEasyInvite);
        this.setBorder(BorderFactory.createTitledBorder("Reminder: To try JabRef p2p, your friends need the plugin too."));
    }

    private boolean addFriend() {
        Group g = (Group) currentGroupList.getSelectedValue();
        if (g != null) {
            List<Friend> friends = p.getFriends();
            if (friends.isEmpty()) {
                JOptionPane.showMessageDialog(currentGroupList, "Invalid invite code, please ensure it is correct");
                return false;
            } else {
                for (Friend f : friends) {
                    model.addFriend(f, g);
                }
            }
        } else {
            // group null
            JOptionPane.showMessageDialog(currentGroupList, "Please select a group to add friend to");
            return false;
        }

        return true;
    }
}
