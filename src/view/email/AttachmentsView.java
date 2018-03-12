package view.email;

import view.*;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import net.sf.jabref.BibtexEntry;
import test.FrameCreator;

/**
 *
 * @author Thien Rong
 */
public class AttachmentsView extends JPanel {

    public static void main(String[] args) throws InterruptedException {
        JFrame f = FrameCreator.createTestFrame();
        AttachmentsView v = new AttachmentsView(new AttachmentEntryListener() {

            public void entryRemoved(BibtexEntry entry) {
                System.out.println("Removed " + entry);
            }

            public void entrySubscribed(BibtexEntry entry) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            public void entryViewed(BibtexEntry entry) {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        });
        f.add(new JScrollPane(v));
        f.setSize(300, 300);
        f.setVisible(true);

        Thread.sleep(1000);
        v.addEntryToUpload(new BibtexEntry());
        v.addEntryToUpload(new BibtexEntry());
        //v.addEntryFromFriend(new BibtexEntry());

    }
    AttachmentEntryListener listener;

    public AttachmentsView(AttachmentEntryListener listener) {
        //super(new FlowLayout(FlowLayout.LEFT, 2, 2));
        super(new GridLayout(0, 1, 2, 2));
        //this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        //this.add(Box.createVerticalGlue());
        this.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        this.listener = listener;
    }

    public void addEntryToUpload(final BibtexEntry entry) {
        JButton btnRemove = new JButton("Remove");
        final SplitPanel sp = new SplitPanel(createView(entry), btnRemove, BorderLayout.EAST);
        this.add(sp, 0);

        btnRemove.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                remove(sp);
                listener.entryRemoved(entry);
                updateUI();
            }
        });
        this.updateUI();
    }

    public void addEntryFromFriend(final BibtexEntry entry) {
        JPanel pnlCtrl = new JPanel();

        JButton btnView = new JButton("View");
        pnlCtrl.add(btnView);
        final SplitPanel sp = new SplitPanel(createView(entry), pnlCtrl, BorderLayout.EAST);
        this.add(sp, 0);

        btnView.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                listener.entryViewed(entry);
            }
        });
        this.updateUI();
    }

    /**
     * Shared when both uploading and receiving
     * @param entry
     * @return
     */
    private Component createView(BibtexEntry entry) {
        String display = entry.getCiteKey();
        if (display == null || display.length() == 0) {
            display = entry.toString();
        }
        return new JLabel(display);
    }
}


