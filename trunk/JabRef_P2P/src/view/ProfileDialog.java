package view;

import core.SidePanel;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import util.visitor.TagFreqVisitor;
import model.ProfileDetail;
import model.friend.Friend;
import net.sf.jabref.BibtexEntry;
import util.EmailTo;
import util.Loader;
import view.friend.FriendsPanel;

/**
 *
 * @author Thien Rong
 */
public class ProfileDialog extends JFrame implements ImageConstants {

    FriendsPanel friendsPanel;
    //TagCloudPanel tagPanel;
    TagListPanel tagPanel;
    EntriesPanel entriesPanel;
    SidePanel main;
    Friend friend;

    public ProfileDialog(SidePanel main, Friend friend) {
        super(friend.getName() + "'s Profile");
        this.setLayout(new BorderLayout(2, 2));
        this.main = main;
        this.friend = friend;
        boolean isFriendAlready = main.findFriend(friend.getFUID()) != null;

        entriesPanel = new EntriesPanel(main, friend, "entries");
        friendsPanel = new FriendsPanel(main, "Friends");
        //tagPanel = new TagCloudPanel(main, 500);
        //tagPanel.setBorder(BorderFactory.createTitledBorder("Tags"));
        tagPanel = new TagListPanel(main, friend.getName() + "'s Tags");

        this.add(new SplitPanel(new JScrollPane(friendsPanel), createCtrlPanel(isFriendAlready), BorderLayout.NORTH), BorderLayout.WEST);
        //this.add(new SplitPanel(tagPanel, entriesPanel, BorderLayout.SOUTH));
        this.add(new SplitPanel(entriesPanel,
                new JScrollPane(tagPanel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED), BorderLayout.WEST));
        this.setSize(650, 500);
    }

    public JPanel createCtrlPanel(boolean isFriendAlready) {
        JPanel pnlCtrl = new JPanel(new BorderLayout());
        //pnlCtrl.setLayout(new BoxLayout(pnlCtrl, BoxLayout.PAGE_AXIS));


        JLabel label1 = new JLabel("<html><h1>" + friend.getName() + "</h1></html>",
                new ImageIcon(Loader.get(USER)), JLabel.CENTER);
        label1.setVerticalTextPosition(JLabel.BOTTOM);
        label1.setHorizontalTextPosition(JLabel.CENTER);
        pnlCtrl.add(label1, BorderLayout.NORTH);


        JPanel pnlBtns = new JPanel(new GridLayout(0, 1));
        JButton btnRequest = new JButton("Send Friend Request");
        btnRequest.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                main.handleFriendRequest(friend);
            }
        });
        if (isFriendAlready) {
            btnRequest.setEnabled(false);
        }
        pnlBtns.add(btnRequest);

        JButton btnSendMail = new JButton("Send Mail");
        btnSendMail.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                main.handlePrepareEmail(Collections.<BibtexEntry>emptyList(), EmailTo.format(friend), "", "");
            }
        });
        pnlBtns.add(btnSendMail);

        pnlCtrl.add(pnlBtns);

        return pnlCtrl;
    }

    public void setProfileDetail(ProfileDetail details) {
        // update friends
        friendsPanel.setFriends(details.getFriends());
        // update entries and tags
        Collection<BibtexEntry> entries = details.getEntry();
        entriesPanel.setEntries(entries);

        TagFreqVisitor visitor = new TagFreqVisitor();
        for (BibtexEntry bibtexEntry : entries) {
            visitor.visitEntry(bibtexEntry, null);
        }
        // update tag cloud
        Map<String, Integer> tags = visitor.getTagFreq();
        String FUID = friend.getFUID();
        main.piggyBackTags(FUID, tags);
        tagPanel.setTags(main.getTagCloudModel().getOtherTopFreq(FUID, 30));
        this.setSize(850, 450);
    }
}
