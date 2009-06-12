package view;

import view.friend.FriendsTree;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import model.friend.Friend;
import net.sf.jabref.BibtexEntry;

import util.CustomBibtexField;
import core.SidePanel;
import javax.swing.JTextArea;
import model.friend.FriendsModel;
import model.friend.FriendsModel;
import model.friend.FriendsTreeModel;

/**
 *
 * @author Thien Rong
 */
public class EditShareDialog extends JDialog {

    JTextArea txtCurrentShare = new JTextArea();
    BibtexEntry[] entries;
    FriendsTree treeFriends;
    SidePanel main;

    public EditShareDialog(SidePanel main) {
        this.main = main;

        this.setTitle("Edit Sharing Settings");
        this.setLayout(new BorderLayout());
        txtCurrentShare.setEditable(false);
        txtCurrentShare.setRows(5);
        this.add(new JScrollPane(txtCurrentShare), BorderLayout.NORTH);

        treeFriends = new FriendsTree(new FriendsTreeModel(main.getFriendsModel()));
        this.add(new JScrollPane(treeFriends));

        JPanel ctrlPanel = new JPanel();
        JButton btnShare = new JButton("Share with selected");
        btnShare.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                editShare(entries, true, treeFriends.getSelectedFriendIDs());
                EditShareDialog.this.setVisible(false);
            }
        });

        JButton btnDontShare = new JButton("Remove Share from selected");
        btnDontShare.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                editShare(entries, false, treeFriends.getSelectedFriendIDs());
                EditShareDialog.this.setVisible(false);
            }
        });
        ctrlPanel.add(btnShare);
        ctrlPanel.add(btnDontShare);

        this.add(ctrlPanel, BorderLayout.SOUTH);
    }

    public void setEntries(BibtexEntry[] entries, FriendsModel model) {
        this.entries = entries;
        txtCurrentShare.setText(generateShareString(entries, model));
        this.pack();
    }

    /**
     * Because each entry can be shared to different people, this will have
     * generate an the list of friends shared to or empty
     * @param entries
     * @param model
     * @return the share String to display
     */
    public String generateShareString(BibtexEntry[] entries, FriendsModel model) {
        // Set so only unique
        StringBuffer sb = new StringBuffer(200);
        for (int i = 0; i < entries.length; i++) {
            BibtexEntry bibtexEntry = entries[i];
            List<Friend> sharedToFriends = CustomBibtexField.getBibtexShare(bibtexEntry, model);
            if (sharedToFriends.isEmpty()) {
                sb.append(bibtexEntry + " is currently not shared with anyone");
            } else {
                StringBuilder sb2 = new StringBuilder();
                for (Friend friend : sharedToFriends) {
                    sb2.append(", " + friend.getName());
                }
                String names = sb2.substring(2);
                sb.append(bibtexEntry + " is currently shared with " + names);
            }
            sb.append("\n");
        }

        return sb.toString().trim();
    }

    private void editShare(BibtexEntry[] entries, boolean toShare, Collection<String> FUIDs) {
        if (toShare) {
            for (BibtexEntry bibtexEntry : entries) {
                CustomBibtexField.addShareToEntry(main.getFrame().basePanel(), bibtexEntry, FUIDs);
            }

        } else {
            for (BibtexEntry bibtexEntry : entries) {
                CustomBibtexField.removeShareToEntry(main.getFrame().basePanel(), bibtexEntry, FUIDs);
            }
        }
    }
}