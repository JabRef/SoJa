package view.tab;

import core.SidePanel;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import model.BibtexMessage;
import model.friend.Friend;
import net.sf.jabref.BibtexEntry;
import util.Loader;
import view.ImageConstants;
import view.SplitPanel;
import view.email.MessagesTree;
import view.email.EmailView;

/**
 *
 * @author Thien Rong
 */
public class InboxTab extends ITab implements ImageConstants {

    //private BibtexMessageDialog messageDialog;//messageDialog = new BibtexMessageDialog(main);
    public InboxTab(final SidePanel main) {
        super(main);
        this.setLayout(new BorderLayout());

        final MessagesTree treeMessages = new MessagesTree(main);
        treeMessages.addMouseListener(new MouseAdapter() {

            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() > 1) {
                    BibtexMessage msg = treeMessages.getSelectedMessage();
                    openMail(msg);
                }
            }
        });
        add(new JScrollPane(treeMessages));

        JPanel pnlCtrl = new JPanel(new GridLayout(0, 1));
        JButton btnView = new JButton("View", new ImageIcon(Loader.get(INBOX)));
        btnView.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                BibtexMessage msg = treeMessages.getSelectedMessage();
                openMail(msg);
            }
        });
        pnlCtrl.add(btnView);

        JButton btnDelete = new JButton("Delete", new ImageIcon(Loader.get(DELETE)));
        btnDelete.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                int option = JOptionPane.showConfirmDialog(main.getFrame(), "Confirm delete?");
                if (option == JOptionPane.YES_OPTION) {
                    BibtexMessage msg = treeMessages.getSelectedMessage();
                    deleteMail(msg);
                }
            }
        });
        pnlCtrl.add(btnDelete);

        add(new SplitPanel(new JPanel(), pnlCtrl, BorderLayout.NORTH), BorderLayout.EAST);

    //SubscriptionPanel subscriptionPanel = new SubscriptionPanel(main, "My Subscriptions");
    //add(subscriptionPanel, BorderLayout.NORTH);
    }

    void openMail(BibtexMessage msg) {
        if (msg != null) {
            Friend friend = main.findFriend(msg.getFromFUID());
            if (friend != null) {
                //JFrame f = FrameCreator.createTestFrame();
                EmailView view = new EmailView(main, friend);
                view.setSubject(msg.getSubject());
                view.setMsg(msg.getMsg());
                for (BibtexEntry bibtexEntry : msg.getEntry()) {
                    view.addEntryFromFriend(bibtexEntry);
                }
                view.addAllFriendTo(msg.getTo());

                view.pack();
                view.setLocationRelativeTo(main.getFrame());
                view.setVisible(true);
            }
        //messageDialog.setCurrMsg(msg);
        //Util.placeDialog(messageDialog, main.getFrame());
        //messageDialog.setVisible(true);
        }
    }

    private void deleteMail(BibtexMessage msg) {
        if (msg != null) {
            main.getMessagesModel().deleteMessage(msg);
        }
    }
}
