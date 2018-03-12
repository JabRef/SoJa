package view;

import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import net.sf.jabref.BasePanel;
import net.sf.jabref.BibtexEntry;
import util.CustomBibtexField;

/**
 *
 * @author Thien Rong
 */
public class ReviewDialog extends JDialog {


    // to store the BUID of entry for faster comparison
    BibtexEntry entry;
    BasePanel bp;
    JTabbedPane pane = new JTabbedPane();

    public ReviewDialog(BibtexEntry entry, BasePanel bp) {
        this.setTitle("Peer Reviews for " + entry.getCiteKey());
        this.entry = entry;
        this.bp = bp;

        this.add(pane);
        this.setSize(400, 300);
    }

    void handleChange(String peerReview, String FUID) {
        JTextArea txtFriend = new JTextArea(peerReview);
        txtFriend.setLineWrap(true);
        txtFriend.setEditable(false);
        pane.add(FUID, new JScrollPane(txtFriend));
        pane.setSelectedIndex(pane.getTabCount() - 1);
    }
}
