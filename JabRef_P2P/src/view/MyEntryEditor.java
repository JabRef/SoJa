package view;

import javax.swing.SwingUtilities;
import core.NetworkDealer;
import core.SidePanel;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Map;
import java.util.TreeMap;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import model.friend.Friend;
import net.sf.jabref.BasePanel;
import net.sf.jabref.BibtexEntry;
import util.CustomBibtexField;

/**
 * Hack add-on to Entry Editor
 * @author Thien Rong
 */
public class MyEntryEditor extends JPanel implements PropertyChangeListener {

    JButton btnViewReviews = new JButton();
    JLabel lblPeersWithItem = new JLabel();
    SidePanel main;
    //
    BibtexEntry entry;
    BasePanel bp;

    public MyEntryEditor(final SidePanel main) {
        this.main = main;

        this.setLayout(new BorderLayout());
        this.setBorder(BorderFactory.createTitledBorder(main.getName()));
        JButton btnPeer = new JButton("Refresh");
        btnPeer.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                System.out.println("getting peer reviews");
                forceRefreshReview();
            }
        });

        JPanel pnlCtrl = new JPanel(new GridLayout(0, 1));
        pnlCtrl.add(btnPeer);
        pnlCtrl.add(lblPeersWithItem);
        pnlCtrl.add(btnViewReviews);

        this.add(pnlCtrl, BorderLayout.NORTH);
    }

    public void setReviews() {
        String BUID = CustomBibtexField.getBUID(entry);

        boolean hasReview = false;
        if (BUID != null) {
            Map<String, String> reviews = main.getFriendReviewsModel().getReviews(BUID);

            if (reviews != null) {
                String myReview = entry.getField("review");
                final Map<String, String> diffReviews = new TreeMap<String, String>();
                for (Map.Entry<String, String> review : reviews.entrySet()) {
                    if (CustomBibtexField.isDiffReview(myReview, review.getValue())) {
                        diffReviews.put(review.getKey(), review.getValue());
                    }
                }
                // remove ex-listener
                for (ActionListener actionListener : btnViewReviews.getActionListeners()) {
                    btnViewReviews.removeActionListener(actionListener);
                }
                btnViewReviews.addActionListener(new ActionListener() {

                    public void actionPerformed(ActionEvent e) {
                        ReviewDialog dialog = new ReviewDialog(entry, bp);
                        for (Map.Entry<String, String> entry : diffReviews.entrySet()) {
                            dialog.handleChange(entry.getValue(), entry.getKey());
                        }
                        dialog.setVisible(true);
                    }
                });
                btnViewReviews.setText(diffReviews.size() + " diff reviews");
                btnViewReviews.setVisible(diffReviews.size() != 0);
                lblPeersWithItem.setText(reviews.size() + " peers with item");

                hasReview = true;
            }
        }

        if (hasReview == false) {
            btnViewReviews.setVisible(false);
            lblPeersWithItem.setText("No Reviews found");
        }

        /*        pnlCtrl.invalidate();
        pnlCtrl.validate();
        pnlCtrl.updateUI();
        pnlCtrl.repaint();
        
        this.validate();
        this.repaint();
        this.updateUI();
        btnViewReviews.invalidate();
        btnViewReviews.validate();
        btnViewReviews.repaint();
        btnViewReviews.updateUI();
         */

        //  this.setLayout(null);
        this.invalidate();
        this.validate();
        Container c = this.getParent();
        if (c != null) {
            c.invalidate();
            c.validate();
        }

    //System.out.println("update leh");
    }

    public void setEntry(BibtexEntry entry, BasePanel bp) {
        this.entry = entry;
        this.bp = bp;

        try {
            SwingUtilities.invokeLater(new Runnable() {

                public void run() {
                    setReviews();
                }
            });

        } catch (Exception ex) {
        }
    }

    public void forceRefreshReview() {
        NetworkDealer d = main.getDealer();
        if (d != null) {
            for (Friend f : main.getDealer().getConnectedFriends()) {
                main.getDealer().sendBrowseRequest(f.getFUID(), null);
            }
        }
    }

    public void propertyChange(PropertyChangeEvent evt) {
        setReviews();
    }
}