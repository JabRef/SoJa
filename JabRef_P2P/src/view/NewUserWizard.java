package view;

import core.SidePanel;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import view.friend.EditFriendsPanel;

/**
 * To Guide New User about features
 * @author Thien Rong
 */
public class NewUserWizard extends JDialog {

    public NewUserWizard(SidePanel main) {
        super(main.getFrame());
        this.setLayout(new BorderLayout(10, 10));
        this.setTitle("Guide for New User");

        JTabbedPane tab = new JTabbedPane(JTabbedPane.LEFT);       
        EditFriendsPanel friendsPanel = new EditFriendsPanel(main, false);
        tab.add("1. Invite/Add Friends", friendsPanel);
        tab.add("2. Friends List", generateTab("<html>" +
                " If you invite your friends, you will see their friend request when they connect to you.<br>" +
                " You can then choose to accept or reject the request. If you accept, they will be in your friend list.<br>" +
                "<br><br>" +
                " If you added your friends by inputing their information, they will be in your friend list already.<br>" +
                "<br><br>" +
                " When your friends are in your friends list, you can share/send items to them, chat with them, <br>" +
                " browse their items and their friends, and see their status." +
                "<br>" +
                " Grouping of friends is not allowed yet." +
                "</html>"));
        tab.add("3. Sharing Items", generateTab("<html>" +
                " To share items, choose the items on the main list and click the 'Set Share Settings'<br>" +
                " Choose friends your friends list and either allow or remove share to selected friends."));
        tab.add("4. Sending Items", generateTab("<html>" +
                " You can send items to friends through 2 ways. <br>" +
                " 1) Choose items on the main list and click 'Send Items'<br>" +
                " 2) Chat with a friend and press the 'Send Items' at the chat dialog<br>" +
                " Both ways will bring you to the compose email window. You can edit the friends to send to<br>" +
                " and the items you wish to send over. Items send over will automatically be shared to them.</html>"));
        tab.add("5. Search Items", generateTab("<html>" +
                " The Search box is available and will search for shared items in your network.<br>" +
                " There is a simple score ranking and you can sort your result based on it.<br>" +
                "<br><br>" +
                " The search box will store your histories for the current session to allow you to search again.<br>" +
                " Pressing down let you view through your histories.</html>"));
        tab.add("6. Chat/View Profile", generateTab("<html>" +
                " Double clicking your friend on your friend list will bring up the chat window.<br>" +
                " You may then chat with them if they are online.</html>"));
        tab.add("7. Subscribing to Items", generateTab("<html>" +
                " You can subscribe to items shared to you and get updates when they are changed<br>" +
                " The time shown is the time you found changes to the items. It is not the time the items was changed.</html>"));
        tab.add("8. Tag Clouds", generateTab("<html>" +
                " The tag clouds are occassionally updated to show the most popular tags in your network.<br>" +
                " Clicking on the tag will search for items matching the tag.</html>"));

        this.add(tab);
        JButton btnCancel = new JButton("I know how to use it already and Skip Guide");
        btnCancel.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                setVisible(false);
            }
        });
        this.add(btnCancel, BorderLayout.SOUTH);
        this.pack();
    }

    public JComponent generateTab(String text){
        JPanel pnl = new JPanel();
        pnl.add(new JLabel(text));
        return pnl;
    }
}
