package view;

import core.SidePanel;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import net.sf.jabref.BibtexEntry;
import net.sf.jabref.Util;

/**
 *
 * @author Thien Rong
 */
public class SelectedEntriesPanel extends JPanel {

    private EditShareDialog dialogEditShare;

    public SelectedEntriesPanel(final SidePanel main, String title) {
        super(new FlowLayout());
        this.setBorder(BorderFactory.createTitledBorder(title));
        dialogEditShare = new EditShareDialog(main);

        JButton btnShare = new JButton("Set Share Settings");
        btnShare.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                BibtexEntry[] entries = main.getFrame().basePanel().getSelectedEntries();
                if (entries.length == 0) {
                    main.getFrame().showMessage("Please select the items to share first.");
                } else {
                    dialogEditShare.setEntries(entries, main.getFriendsModel());
                    Util.placeDialog(dialogEditShare, main.getFrame());
                    dialogEditShare.setVisible(true);
                }
            }
        });
        this.add(btnShare);

        JButton btnSendItems = new JButton("Send Items");

        btnSendItems.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                BibtexEntry[] entries = main.getFrame().basePanel().getSelectedEntries();
                if (entries.length == 0) {
                    main.getFrame().showMessage("Please select the items to send first.");
                } else {
                    main.handlePrepareEmail(Arrays.asList(entries), "", "", "");
                }
            }
        });
        this.add(btnSendItems);
    }
}
