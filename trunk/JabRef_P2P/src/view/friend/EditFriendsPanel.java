package view.friend;

import core.SidePanel;
import core.Store;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import model.friend.Friend;
import model.friend.FriendGroup;
import model.friend.FriendsModel;
import model.friend.Group;
import test.FrameCreator;
import util.FriendStringCodec;
import view.SplitPanel;

/**
 * Contains the addFriends, add button and edit list
 * Assumption that this is in dialog, so the friends list will not be
 * added or updated from other places to ensure correctness
 * @author Thien Rong
 */
public class EditFriendsPanel extends JPanel {

    public static void main(String[] args) {
        JFrame f = FrameCreator.createTestFrame();
        FriendsModel m = new FriendsModel(new Store("A"));

    /*final EditFriendsPanel p = new EditFriendsPanel(m);
    f.add(p);

    m.load();

    Group g = new Group("test");
    m.addGroup(g);
    //m.addFriend(new Friend("test", "test", "ip", 1, 1), g);
    //m.addFriend(new Friend("test2", "test2", "ip", 1, 1), g);
    m.save();
    FrameCreator.packAndShow(f);*/
    }

    public EditFriendsPanel(SidePanel main) {
        this.setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
        final DefaultListModel fModel = new DefaultListModel();
        final DefaultListModel gModel = new DefaultListModel();
        final JList currentGroupList = new JList(gModel);
        // add items that was loaded already
        final FriendsModel model = main.getFriendsModel();
        for (Group group : model.getGroups().values()) {
            gModel.addElement(group);
            for (Friend friend : group.getFriends()) {
                fModel.addElement(friend);
            }
        }

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

        final AddFriendPanel p = new AddFriendPanel();

        JLabel lblEasyPaste = new JLabel("<html>Copy and paste the following in your email invite.<br>" +
                "Your friend can then just paste your information <br>using the 'Text Input' above to add you.</html>");
        final JTextArea txtPasteForEmail = new JTextArea(FriendStringCodec.toString(main.getMyProfile()));
        txtPasteForEmail.setEditable(false);
        txtPasteForEmail.addFocusListener(new FocusAdapter() {

            public void focusGained(FocusEvent e) {
                txtPasteForEmail.selectAll();
            }
        });
        JButton copy = new JButton("Copy");
        copy.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                String selection = txtPasteForEmail.getText();
                StringSelection data = new StringSelection(selection);
                final Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                clipboard.setContents(data, data);
            }
        });

        JPanel pnlEasyInvite = new JPanel(new BorderLayout());
        pnlEasyInvite.add(lblEasyPaste, BorderLayout.NORTH);
        pnlEasyInvite.add(new JScrollPane(txtPasteForEmail));
        pnlEasyInvite.add(copy, BorderLayout.SOUTH);
        pnlEasyInvite.setBorder(BorderFactory.createTitledBorder("Invite your friends now!"));
        this.add(new SplitPanel(pnlEasyInvite, p, BorderLayout.NORTH));


        currentGroupList.setPrototypeCellValue("Choose Group");
        currentGroupList.setBorder(BorderFactory.createTitledBorder("Choose Group"));
        this.add(new JScrollPane(currentGroupList));


        JButton btnAdd = new JButton("Add >>");
        btnAdd.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                Group g = (Group) currentGroupList.getSelectedValue();
                if (g != null) {
                    for (Friend f : p.getFriends()) {
                        model.addFriend(f, g);
                    }
                }
            }
        });

        this.add(btnAdd);

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
        this.add(new SplitPanel(new JScrollPane(currentFriendList), pnlCtrl, BorderLayout.SOUTH));
        this.setBorder(BorderFactory.createTitledBorder("Reminder: To try JabRef p2p, your friends need the plugin too."));
    }
}
