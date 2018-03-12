package view.email;

import core.SidePanel;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import model.friend.Friend;
import net.sf.jabref.BibtexEntry;
import net.sf.jabref.Globals;
import net.sf.jabref.MetaData;
import net.sf.jabref.PreviewPanel;
import util.Loader;
import view.ImageConstants;

/**
 * @author Thien Rong
 */
public class BibtexEntryView extends JFrame implements ImageConstants {

    PreviewPanel preview = new PreviewPanel(null, new MetaData(), Globals.prefs.get("preview1"));
    Friend friend;
    BibtexEntry currEntry;

    public BibtexEntryView(final SidePanel main, boolean showSubscribe) {
        this.setLayout(new BorderLayout());
        this.add(preview);

        JPanel pnlCtrl = new JPanel();
        JButton btnAllow = new JButton("Add a copy into my database");
        btnAllow.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                main.handleAddBibtexCopy(friend, currEntry);                
            }
        });
        pnlCtrl.add(btnAllow);

        if (showSubscribe) {
            JButton btnSubscribe = new JButton("Subscribe And Keep Me Updated", new ImageIcon(Loader.get(RSS)));
            btnSubscribe.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    main.handleSubscribe(friend, currEntry);
                }
            });
            pnlCtrl.add(btnSubscribe);
        }
        this.add(pnlCtrl, BorderLayout.SOUTH);
    }

    public void setCurrEntry(Friend friend, BibtexEntry entry) {
        this.friend = friend;
        this.currEntry = entry;
        preview.setEntry(entry);
        this.setTitle(friend.getName() + "'s " + entry.getCiteKey());
    }
}