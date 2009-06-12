package view.email;

import java.awt.event.KeyEvent;
import java.io.IOException;
import view.*;
import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;
import core.SidePanel;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.FocusManager;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import model.BibtexMessage;
import model.friend.Friend;
import model.friend.FriendsTreeModel;
import net.sf.jabref.BibtexEntry;
import net.sf.jabref.Globals;
import util.BibtexStringCodec;
import util.EmailTo;
import view.friend.FriendsTree;

/**
 *
 * @author Thien Rong
 */
public class EmailView extends JFrame implements AttachmentEntryListener {

    public static void main(String[] args) {
        EmailView v = new EmailView(null, null);
        for (int i = 0; i < 10; i++) {
            v.addEntryToUpload(new BibtexEntry());
        }

        v.pack();
        v.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        v.setVisible(true);
    }
    // Use to view attachment
    BibtexEntryView entryView;
    // null if sending, else the person who send message
    Friend fromFriend;
    // FUIDs of people to send to
    Set<String> friendsTo = new HashSet<String>();
    Set<BibtexEntry> entries = new HashSet<BibtexEntry>();
    AttachmentsView view = new AttachmentsView(this);
    JTextField txtTo = new JTextField(45);
    JTextField txtSubject = new JTextField();
    JTextArea txtMsg = new JTextArea(5, 0) {

        /**
         * To allow tab to skip to next field
         */
        @Override
        public boolean isManagingFocus() {
            return false;
        }
    };

    public EmailView(final SidePanel main, Friend fromFriend) {
        super("Mail");
        this.setLayout(new BorderLayout(2, 2));
        this.fromFriend = fromFriend;
        entryView = new BibtexEntryView(main, true);

        JPanel headerView, pnlCtrl;
        if (fromFriend == null) {
            headerView = createSendView(main);
            pnlCtrl = createSendControl(main);
        } else {
            headerView = createRecvView();
            pnlCtrl = createRecvControl(main);
        }


        disableTabInTextArea(txtMsg);

        this.add(headerView, BorderLayout.NORTH);
        this.add(new JScrollPane(txtMsg));
        this.add(pnlCtrl, BorderLayout.SOUTH);
    }

    private void disableTabInTextArea(JTextArea txt) {
        final Object MyFocusActionKey = new Object();
        final KeyStroke PressedTab = KeyStroke.getKeyStroke(KeyEvent.VK_TAB, 0, false);
        ActionMap actionMap = txt.getActionMap();
        actionMap.put(MyFocusActionKey, new AbstractAction("MyFocusAction") {

            public void actionPerformed(ActionEvent e) {
                Object source = e.getSource();
                if (source instanceof Component) {
                    FocusManager.getCurrentKeyboardFocusManager().focusNextComponent((Component) source);
                }
            }
        });
        txt.getInputMap().put(PressedTab, MyFocusActionKey);
    }

    private JPanel createRecvControl(final SidePanel main) {
        JPanel pnlCtrl = new JPanel();
        JButton btnReply = new JButton("Reply");
        btnReply.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                if (fromFriend != null) {
                    String replyTo = EmailTo.removeSelf(txtTo.getText(), main.getMyProfile().getFUID()) +
                            "," + EmailTo.format(fromFriend);

                    String replyMsg = txtMsg.getText();
                    if (replyMsg.length() > 0) {
                        replyMsg = "\n\n>>>>>>>>>>>\n" + replyMsg;
                    }

                    main.handlePrepareEmail(entries, replyTo, "Re: " + txtSubject.getText(), replyMsg);
                    EmailView.this.setVisible(false);
                }
            }
        });
        JButton btnCancel = new JButton("Close");

        btnCancel.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                EmailView.this.setVisible(false);
            }
        });


        pnlCtrl.add(btnReply);
        pnlCtrl.add(btnCancel);
        return pnlCtrl;
    }

    private JPanel createSendView(final SidePanel main) {
        txtSubject.addKeyListener(new KeyAdapter() {

            @Override
            public void keyReleased(KeyEvent e) {
                String display = txtSubject.getText();
                if (display.length() > 0) {
                    EmailView.this.setTitle(txtSubject.getText());
                }
            }
        });

        JButton btnAddRecipient = new JButton("Add...");
        btnAddRecipient.setFocusable(false);
        btnAddRecipient.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                FriendsTree tree = new FriendsTree(new FriendsTreeModel(main.getFriendsModel()));
                if (JOptionPane.OK_OPTION == JOptionPane.showConfirmDialog(
                        EmailView.this,
                        new JScrollPane(tree),
                        "Choose friends to send message to",
                        JOptionPane.OK_CANCEL_OPTION)) {
                    // reparse in case user manual delete or add
                    reparseFriendsTo();
                    for (Friend friend : tree.getSelectedFriends()) {
                        addFriendTo(friend);
                    }
                }
            }
        });

        JButton btnAddSelected = new JButton("Add Selected Items");
        btnAddSelected.setToolTipText("Choose items from the main area and press this to add them");
        btnAddSelected.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                for (BibtexEntry bibtexEntry : main.getFrame().basePanel().getSelectedEntries()) {
                    addEntryToUpload(bibtexEntry);
                }
            }
        });
        DefaultFormBuilder builder = new DefaultFormBuilder(
                new FormLayout("left:pref, 2dlu, default", ""));
        builder.append(Globals.lang("To") + ":");
        builder.append(new SplitPanel(txtTo, btnAddRecipient, BorderLayout.EAST));

        builder.append(Globals.lang("Subject") + ":");
        builder.append(txtSubject);

        builder.append(Globals.lang("Attachments") + ":");
        JScrollPane pane = new JScrollPane(view, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        pane.getViewport().setPreferredSize(new Dimension(0, 70));
        builder.append(new SplitPanel(pane, btnAddSelected, BorderLayout.EAST));

        return builder.getPanel();
    }

    private JPanel createSendControl(final SidePanel main) {
        JPanel pnlCtrl = new JPanel();
        JButton btnSend = new JButton("Send");
        btnSend.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                BibtexMessage message = sendMessage(main);
            }
        });
        JButton btnSave = new JButton("Save (Not Implemented)");
        btnSave.setEnabled(false);

        JButton btnCancel = new JButton("Cancel");

        btnCancel.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                EmailView.this.setVisible(false);
            }
        });

        pnlCtrl.add(btnSend);
        pnlCtrl.add(btnSave);
        pnlCtrl.add(btnCancel);

        return pnlCtrl;
    }

    private BibtexMessage sendMessage(SidePanel main) {
        String subject = txtSubject.getText().trim();
        reparseFriendsTo();
        if (friendsTo.size() == 0) {
            JOptionPane.showMessageDialog(EmailView.this, "Please enter friends to send to", "Error found", JOptionPane.WARNING_MESSAGE);
            txtTo.requestFocusInWindow();
            return null;
        }

        if (subject.length() == 0) {
            JOptionPane.showMessageDialog(EmailView.this, "Subject cannot be empty", "Error found", JOptionPane.WARNING_MESSAGE);
            txtSubject.requestFocusInWindow();
            return null;
        }

        // subject and entries convertion okay
        // check any unknown FUID
        List<String> unknownFUIDs = new ArrayList<String>();
        List<Friend> friends = new ArrayList<Friend>();
        for (String FUID : friendsTo) {
            Friend friend = main.findFriend(FUID);
            if (friend == null) {
                unknownFUIDs.add(FUID);
            } else {
                friends.add(friend);
            }
        }

        // TODO allow send invitation or ignore unknown
        if (unknownFUIDs.isEmpty() == false) {
            String errMsg = "The following friends are not found. Please invite or add them first\n";
            for (String unknown : unknownFUIDs) {
                errMsg += unknown + " \n";
            }
            JOptionPane.showMessageDialog(EmailView.this, errMsg, "Error found", JOptionPane.WARNING_MESSAGE);
            return null;
        }

        // confirm can create convert to string
        String entriesStr = null;
        try {
            entriesStr = BibtexStringCodec.toStringListForPeer(main.getFrame().basePanel(), entries, main.getMyProfile().getFUID(), friendsTo);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(EmailView.this, "Error converting items", "Error found", JOptionPane.WARNING_MESSAGE);
            return null;

        }

        BibtexMessage message = new BibtexMessage(subject, txtMsg.getText(),
                entriesStr, txtTo.getText());
        for (Friend friend : friends) {
            main.getDealer().queueBibtexMessage(friend.getFUID(), message);
        }
        EmailView.this.setVisible(false);
        return message;
    }

    private JPanel createRecvView() {
        DefaultFormBuilder builder = new DefaultFormBuilder(
                new FormLayout("left:pref, 2dlu, default", ""));
        builder.append(Globals.lang("To") + ":");
        builder.append(txtTo);

        builder.append(Globals.lang("Subject") + ":");
        builder.append(txtSubject);

        builder.append(Globals.lang("Attachments") + ":");
        JScrollPane pane = new JScrollPane(view, JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        pane.getViewport().setPreferredSize(new Dimension(0, 35));
        builder.append(pane);

        txtTo.setEditable(false);
        txtSubject.setEditable(false);
        txtMsg.setEditable(false);

        return builder.getPanel();
    }

    public void addEntryToUpload(BibtexEntry entry) {
        if (entries.add(entry)) {
            view.addEntryToUpload(entry);
        }
    }

    public void addEntryFromFriend(BibtexEntry entry) {
        if (entries.add(entry)) {
            view.addEntryFromFriend(entry);
        }
    }

    /**
     * When get from friends, just replace whole text,
     * because you might not know the Friends they send to.
     * @param actualText
     */
    public void addAllFriendTo(String actualText) {
        friendsTo.addAll(EmailTo.parse(actualText));
        txtTo.setText(actualText);
    }

    public void addFriendTo(Friend friend) {
        if (friendsTo.add(friend.getFUID())) {
            txtTo.setText(txtTo.getText() + EmailTo.format(friend) + ",");
        }
    }

    public void setSubject(String subject) {
        if (subject.length() > 0) {
            txtSubject.setText(subject);
            this.setTitle(subject);
        }

    }

    public void setMsg(String msg) {
        if (msg.length() > 0) {
            txtMsg.setText(msg);
        }

    }

    @Override
    public void entryRemoved(BibtexEntry entry) {
        entries.remove(entry);
    }

    @Override
    public void entryViewed(BibtexEntry entry) {
        if (fromFriend != null) {
            entryView.setCurrEntry(fromFriend, entry);
            entryView.setSize(500, 300);
            entryView.setLocationRelativeTo(this);
            entryView.setVisible(true);
        }
    }

    /**
     * Reparse in case user enter or delete manually
     */
    private void reparseFriendsTo() {
        friendsTo.clear();
        friendsTo.addAll(EmailTo.parse(txtTo.getText()));
    }
}
