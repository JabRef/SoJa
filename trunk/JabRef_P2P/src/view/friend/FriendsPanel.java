package view.friend;

import view.*;
import core.ActionHandler;
import core.Store;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import model.friend.Friend;
import model.friend.FriendsModel;
import model.friend.Group;
import test.FrameCreator;

/**
 *
 * @author Thien Rong
 */
public class FriendsPanel extends JPanel implements ImageConstants {

    public static void main(String[] args) {
        if (true) {
            JFrame f = FrameCreator.createTestFrame();
            JTabbedPane tabbedPane = new JTabbedPane();

            final FriendsPanel fp = new FriendsPanel(new ActionHandler.TestActionHandler(), "My Friends");
            for (int i = 0; i < 100; i++) {
                int freq = (int) (Math.random() * 10);
//                fp.addFriend(new Friend("John", "John", "123", 1, 1));
            //System.out.println("i="+i);
            }

            FriendsModel m = new FriendsModel(new Store("test")).load();
            Group g = new Group("test");
            m.addGroup(g);
            m.addFriend(new Friend("t", "Joe", "localhost", 1, 2), g);
            m.addFriend(new Friend("t2", "Joe2", "localhost", 1, 2), g);
            fp.setFriends(m.getFriends());
            JScrollPane sp = new JScrollPane(fp, JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
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
    private JLabel label = new JLabel("Getting friends information...");
    private JPanel panelFriends = new JPanel();//new JPanel(new GridLayout(0, 3));
    //private static final int MAX = 30;
    private ActionHandler actionHandler;

    public FriendsPanel(ActionHandler actionHandler, String title) {
        super(new BorderLayout());
        this.actionHandler = actionHandler;
        this.setBorder(BorderFactory.createTitledBorder(title));

        this.add(label, BorderLayout.NORTH);

        panelFriends.setLayout(new BoxLayout(panelFriends, BoxLayout.PAGE_AXIS));
        this.add(panelFriends);
    }

    private void addFriend(final Friend friend) {
        //  if (this.getComponentCount() < MAX) {        
        LinkButton btn = new LinkButton("<html>" + friend.getName() + "</html>", 1);
        //btn.setIcon(new ImageIcon(Loader.get(USER)));
        panelFriends.add(btn);
        btn.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                actionHandler.handleViewFriend(friend);
            }
        });
    //   }
    }

    public void setFriends(Collection<Friend> friends) {
        label.setText(friends.size() + " friends");
        panelFriends.removeAll();
        for (Friend f : friends) {
            addFriend(f);
        }
    }
}
