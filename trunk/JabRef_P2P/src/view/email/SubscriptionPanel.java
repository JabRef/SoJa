package view.email;

import core.SidePanel;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import model.SubscriptionModel;
import model.SubscriptionModel.Subscription;
import model.friend.Friend;
import net.sf.jabref.BibtexEntry;
import util.CustomBibtexField;
import util.Loader;
import view.ImageConstants;
import view.SplitPanel;

/**
 *
 * @author Thien Rong
 */
public class SubscriptionPanel extends JPanel implements PropertyChangeListener, ImageConstants {

    // Use to view attachment
    BibtexEntryView entryView;
    JLabel lbl = new JLabel("No Subscriptions yet", new ImageIcon(Loader.get(FOLDER_RSS)), JLabel.CENTER);
    SubscriptionModel model;
    SimpleDateFormat df = new SimpleDateFormat("dd MMM, h:mm a");
    JButton btnRefresh = new JButton("Refresh", new ImageIcon(Loader.get(RELOAD)));

    // BUID->Component so can remove
    Map<String, Component> comps = new HashMap<String, Component>();
    JPanel pnlItems = new JPanel();

    public SubscriptionPanel(final SidePanel main, String title) {
        //this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        this.setLayout(new BorderLayout());
        entryView = new BibtexEntryView(main, false);
        this.model = main.getSubscriptionModel();
        model.addPropertyChangeListener(this);
        // manually show latest copies since it won't be fired
        for (Subscription subscriptionChange : model.getLatestCopies().values()) {
            pnlItems.add(createSubscriptionView(subscriptionChange), 0);
        }

        this.setBorder(BorderFactory.createTitledBorder(title));

        model.autoUpdateSubscription(main, 5000);
        btnRefresh.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                refresh(main);
            }
        });
        this.add(new SplitPanel(lbl, btnRefresh, BorderLayout.EAST), BorderLayout.NORTH);

        pnlItems.setLayout(new GridLayout(0, 1, 4, 4));
        //pnlItems.setLayout(new BoxLayout(pnlItems, BoxLayout.PAGE_AXIS));
        JScrollPane pane = new JScrollPane(pnlItems);
        pane.getViewport().setPreferredSize(new Dimension(0, 280));
        JPanel pnlEmpty = new JPanel();
        this.add(new SplitPanel(pnlEmpty, pane, BorderLayout.NORTH));
    }

    private void refresh(SidePanel main) {
        model.updateSubscriptions(main);
    }

    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(SubscriptionModel.DELETED_ENTRY)) {
            Subscription change = (Subscription) evt.getOldValue();
            String BUID = CustomBibtexField.getBUID(change.getEntry());
            pnlItems.remove(comps.remove(BUID));
        } else if (evt.getPropertyName().equals(SubscriptionModel.MODIFIED_ENTRY)) {
            Subscription change = (Subscription) evt.getNewValue();
            pnlItems.remove(comps.get(CustomBibtexField.getBUID(change.getEntry())));
            pnlItems.add(createSubscriptionView(change), 0);
        } else if (evt.getPropertyName().equals(SubscriptionModel.NEW_ENTRY)) {
            Subscription change = (Subscription) evt.getNewValue();
            pnlItems.add(createSubscriptionView(change), 0);
        }
        int size = model.getLatestCopies().size();
        lbl.setText(size + " Subscriptions");

        pnlItems.updateUI();
    }

    Component createSubscriptionView(final Subscription change) {
        final BibtexEntry entry = change.getEntry();
        final Friend friend = change.getFriend();

        JPanel pnlView = new JPanel(new BorderLayout(2, 2));
        JLabel lbl = new JLabel(df.format(change.getDate()) + ", " +
                friend.getName() + "'s " + entry.getCiteKey());

        //new ImageIcon(Loader.get(RSS)), JLabel.LEFT));
        JPanel pnlCtrl = new JPanel(new FlowLayout());
        JButton btnView = new JButton("View");
        btnView.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                entryView.setCurrEntry(friend, entry);
                entryView.setSize(500, 300);
                entryView.setVisible(true);
            }
        });
        JButton btnDelete = new JButton("Delete");
        btnDelete.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                model.deleteSubscription(change);
            }
        });
        pnlCtrl.add(btnView);
        pnlCtrl.add(btnDelete);

        pnlView.add(lbl);
        pnlView.add(pnlCtrl, BorderLayout.SOUTH);

        comps.put(CustomBibtexField.getBUID(entry), pnlView);
        return pnlView;
    }
}


