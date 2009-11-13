package view.tab;

import core.SidePanel;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import model.friend.Friend;
import util.visitor.FriendVisitor;
import view.SplitPanel;

/**
 *
 * @author Thien Rong
 */
public class ProfileTab extends ITab {

    public static void main(String[] args) {
        if (true) {
            JFrame f = new JFrame();
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            JTabbedPane tabbedPane = new JTabbedPane();

            ProfileTab tp = new ProfileTab(null);
            JScrollPane sp = new JScrollPane(tp, JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            /*sp.getViewport().setViewSize(new Dimension(300, 300));
            sp.setMaximumSize(new Dimension(300,300));
            tp.setSize(500,500);
            sp.setSize(300, 300);*/
            JPanel pp = new JPanel();
            //pp.setSize(300, 300);
            pp.add(new JButton("A"));
            pp.setLayout(new BoxLayout(pp, BoxLayout.PAGE_AXIS));
            pp.add(sp);
            tabbedPane.add(pp);
            f.add(tabbedPane);
            f.setSize(300, 300);
            f.setVisible(true);
        }
    }

    public ProfileTab(final SidePanel main) {
        super(main);
        this.setLayout(new GridLayout(0, 1));
        final JTextField txtMyStatus = new JTextField(main.getMyProfile().getCurrStatus());
        txtMyStatus.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                final String newStatus = txtMyStatus.getText().trim();
                main.getMyProfile().setCurrStatus(newStatus);
                main.getDht().updateStatus(newStatus, main.getMyProfile());
                main.getFriendsModel().visitAllFriends(new FriendVisitor() {

                    public void visitFriend(Friend f) {
                        if (f.isConnected()) {
                            main.getDealer().sendCurrStatus(f.getFUID(), newStatus);
                        }
                    }
                });
            }
        });


        FriendTab friendTab = new FriendTab(main);

        //SplitPanel sp = new SplitPanel(tp, btnUpdateTagCloud, BorderLayout.SOUTH);
        SplitPanel all3 = new SplitPanel(friendTab, txtMyStatus, BorderLayout.NORTH);
        this.add(all3);


    }
}
