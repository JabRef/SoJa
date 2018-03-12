package view.tab;

import core.SidePanel;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import model.friend.Friend;
import model.friend.FriendsTreeModel;
import net.sf.jabref.BibtexFields;
import net.sf.jabref.Util;
import net.sf.jabref.gui.ImportInspectionDialog;
import view.friend.FriendsTree;
import view.IMDialog;
import util.GlobalUID;
import util.Loader;
import view.ImageConstants;

/**
 *
 * @author Thien Rong
 */
public class FriendTab extends ITab implements ImageConstants {

    public FriendTab(final SidePanel main) {
        super(main);

        this.setLayout(new BorderLayout());
        new FriendsTreeModel(main.getFriendsModel());
        final FriendsTree treeFriends = new FriendsTree(new FriendsTreeModel(main.getFriendsModel()));
        treeFriends.addMouseListener(new MouseAdapter() {

            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    Friend f = treeFriends.getSelectedFriend();
                    if (f != null) {
                        main.handleChatFriend(f);
                    }
                }
            }
        });

        this.add(new JScrollPane(treeFriends));

        // @TODO auto connect/check with p2pp?
        /*JButton btnSendConnect = new JButton("Connect");
        btnSendConnect.addActionListener(new ActionListener() {

        public void actionPerformed(ActionEvent e) {
        Friend selectedFriend = treeFriends.getSelectedFriend();
        if (selectedFriend != null) {
        doConnect(selectedFriend);
        }
        }
        });*/

        String VIEW_PROFILE = "View Profile";
        JButton btnViewProfile = new JButton(VIEW_PROFILE, new ImageIcon(Loader.get(PROFILE)));
        btnViewProfile.setToolTipText(VIEW_PROFILE);
        btnViewProfile.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                Friend selectedFriend = treeFriends.getSelectedFriend();
                if (selectedFriend != null) {
                    main.handleViewFriend(selectedFriend);
                }
            }
        });

        String CHAT_TEXT = "Chat";
        JButton btnSendChat = new JButton(CHAT_TEXT, new ImageIcon(Loader.get(CHAT)));
        btnSendChat.setToolTipText(CHAT_TEXT);
        btnSendChat.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                Friend selectedFriend = treeFriends.getSelectedFriend();
                if (selectedFriend != null) {
                    IMDialog im = main.getIMDialog(selectedFriend);
                    im.setLocationRelativeTo(main.getFrame());
                    im.setVisible(true);
                }

            }
        });

        String BROWSE_ITEM = "Browse Items";
        JButton btnBrowseShare = new JButton(BROWSE_ITEM, new ImageIcon(Loader.get(SEARCH)));
        btnBrowseShare.setToolTipText(BROWSE_ITEM);
        btnBrowseShare.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                Friend selectedFriend = treeFriends.getSelectedFriend();
                if (selectedFriend != null) {
                    final ImportInspectionDialog importer = new ImportInspectionDialog(main.getFrame(), main.getFrame().basePanel(),
                            BibtexFields.DEFAULT_INSPECTION_FIELDS, "Browsing " + selectedFriend.getName(), false);

                    Util.placeDialog(importer, main.getFrame());
                    // generate id 1st so can put into map b4 sending
                    String requestID = GlobalUID.generate(main.getMyProfile().getFUID());
                    //importer.setProgress(0, 0);
                    main.putQuery(requestID, importer);
                    main.getDealer().sendBrowseRequest(selectedFriend.getFUID(), requestID);
                    importer.setVisible(true);
                }

            }
        });

        String EDIT_FRIENDS = "Edit Friends";
        JButton btnEditUsers = new JButton(EDIT_FRIENDS, new ImageIcon(Loader.get(USER_EDIT)));
        btnEditUsers.setToolTipText(EDIT_FRIENDS);
        btnEditUsers.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                main.handleEditFriend();
            }
        });

        // so that will not take up the whole right side
        JPanel pnlSide = new JPanel(new BorderLayout());
        JPanel pnlCtrl = new JPanel(new GridLayout(0, 1));
        //pnlCtrl.setLayout(new BoxLayout(pnlCtrl, BoxLayout.PAGE_AXIS));
        //pnlCtrl.add(btnSendConnect);
        pnlCtrl.add(btnEditUsers);
        pnlCtrl.add(btnViewProfile);
        pnlCtrl.add(btnSendChat);
        pnlCtrl.add(btnBrowseShare);

        pnlSide.add(pnlCtrl, BorderLayout.NORTH);
        add(pnlSide, BorderLayout.EAST);
    }
}
