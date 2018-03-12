package view.friend;

import java.util.logging.Level;
import java.util.logging.Logger;
import view.*;
import core.ActionHandler;
import core.Store;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.Collection;
import java.util.List;
import java.util.Vector;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import model.friend.Friend;
import model.friend.FriendsModel;
import model.friend.Group;
import test.FrameCreator;
import util.Loader;
import util.OpenDHTHelper;

/**
 *
 * @author Thien Rong
 */
public class FriendsPanel extends JPanel implements ImageConstants {

    public static void main(String[] args) throws Exception {
        if (true) {
            JFrame f = FrameCreator.createTestFrame();
            JTabbedPane tabbedPane = new JTabbedPane();

            final FriendsPanel fp = new FriendsPanel(new ActionHandler.TestActionHandler(), "My Friends",
                    FriendsPanel.MODE_NAME_TAG_STATUS);
//            for (int i = 0; i < 100; i++) {
//                int freq = (int) (Math.random() * 10);
////                fp.addFriend(new Friend("John", "John", "123", 1, 1));
//                //System.out.println("i="+i);
//            }

            final FriendsModel m = new FriendsModel(new Store("test")).load();

            final Group g = new Group("test");
            m.addGroup(g);

            final OpenDHTHelper dht = new OpenDHTHelper();
            for (Friend ff : dht.getAll(dht.getUserList())) {
                //System.out.println("trying to get " + ff);
                if (ff != null) {
                    synchronized (m) {
                        m.addFriend(ff, g);
                    }

                }
            }


            m.addFriend(new Friend("t", "Joe", "localhost", 1, 2, null, "hello,byebye"), g);
            m.addFriend(new Friend("t2", "Joe", "localhost", 1, 2, "ss", "hello,byebye,gergreg,gergerw,gerwgr"), g);
            m.addFriend(new Friend("t3", "Joe2", "localhost", 1, 2), g);
            fp.setFriends(m.getFriends());
            m.delete();

            JScrollPane sp = new JScrollPane(fp, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
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
    // allow setting of different mode
    private int renderMode = MODE_NAME;
    public static final int MODE_NAME = 1;
    public static final int MODE_NAME_ICON = 2;
    public static final int MODE_NAME_TAG = 3;
    public static final int MODE_NAME_TAG_STATUS = 4;

    public FriendsPanel(ActionHandler actionHandler, String title, int renderMode) {
        super(new BorderLayout());
        this.actionHandler = actionHandler;
        this.renderMode = renderMode;
        this.setBorder(BorderFactory.createTitledBorder(title));

        this.add(label, BorderLayout.NORTH);

        panelFriends.setLayout(new BoxLayout(panelFriends, BoxLayout.PAGE_AXIS));
        //panelFriends.setLayout(new GridLayout(0, 2));
        this.add(panelFriends);
    }

    private void addFriend(final Friend friend) {
        Component pnl;
        switch (renderMode) {
            case MODE_NAME_TAG:
                pnl = renderFriend1(friend);
                break;
            case MODE_NAME_TAG_STATUS:
                pnl = renderFriend3(friend);
                break;
            case MODE_NAME_ICON:
                pnl = renderFriend4(friend);
                break;
            default:
                pnl = renderFriend1(friend);
        }
        panelFriends.add(pnl);
    }

    public Component renderFriend1(final Friend friend) {
        //  if (this.getComponentCount() < MAX) {
        LinkButton btn = new LinkButton("<html>" + friend.getName() + "</html>", 1);
        //btn.setIcon(new ImageIcon(Loader.get(USER)));
        btn.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                actionHandler.handleViewFriend(friend);
            }
        });

        return btn;
    }

    public Component renderFriend4(final Friend friend) {
        panelFriends.setLayout(new GridLayout(0, 3));
        JButton btn = new JButton(friend.getName(), getAvatarFromWeb(friend.getAvatarURL()));
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setVerticalTextPosition(SwingConstants.BOTTOM);
        btn.setHorizontalTextPosition(SwingConstants.CENTER);
        //btn.setHorizontalTextPosition(SwingConstants.CENTER);
        btn.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                actionHandler.handleViewFriend(friend);
            }
        });
        return btn;
    }

    public Component renderFriend3(final Friend friend) {
        OpenDHTHelper dht = new OpenDHTHelper();

        List<Status> status;
        try {
            status = dht.getStatus(friend.getFUID());
        } catch (Exception ex) {
            ex.printStackTrace();
            status = new Vector<Status>();
        }
        friend.setStatus(status);

        JPanel pnl = new JPanel();
        pnl.setLayout(new BoxLayout(pnl, BoxLayout.PAGE_AXIS));

        JPanel pnlTop = new JPanel();
        JButton btnTop = new JButton(getAvatarFromWeb(friend.getAvatarURL()));
        btnTop.setBorderPainted(false);
        btnTop.setMargin(new Insets(0, 0, 0, 0));

        pnlTop.add(btnTop);
        pnlTop.add(new JLabel(friend.getName()));
        pnl.add(pnlTop);
        btnTop.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                actionHandler.handleViewFriend(friend);
            }
        });

        String tags = friend.getTags();
        if (tags.length() == 0) {
            tags = "none";
        }
        if (tags.length() > 300) {
            tags = tags.substring(0, 297) + "...";

        }

        JTextArea txtTags = new JTextArea(tags);
        txtTags.setOpaque(false);
        txtTags.setEditable(false);
        txtTags.setBorder(BorderFactory.createTitledBorder("Tags"));
        txtTags.setWrapStyleWord(true);
        txtTags.setLineWrap(true);

        StringBuilder sb = new StringBuilder();
        for (Status string : status) {
            sb.append(string.getStatus() + "\n\n");
        }

        String statusTxt = sb.toString();
        if (statusTxt.length() == 0) {
            statusTxt = "no update";
        }
        JTextArea txtStatus = new JTextArea(statusTxt);
        txtStatus.setOpaque(false);
        txtStatus.setEditable(false);
        txtStatus.setBorder(BorderFactory.createTitledBorder("Status Update"));

        pnl.add(txtTags); // used because label align not working
        pnl.add(txtStatus);

        return pnl;
    }

    public Component renderFriend2(Friend friend) {
        JPanel pnl = new JPanel();
        pnl.setLayout(new BoxLayout(pnl, BoxLayout.PAGE_AXIS));

        JPanel pnlTop = new JPanel();
        JButton btnTop = new JButton(getAvatarFromWeb(friend.getAvatarURL()));
        btnTop.setBorderPainted(false);
        btnTop.setMargin(new Insets(0, 0, 0, 0));

        pnlTop.add(btnTop);
        pnlTop.add(new JLabel(friend.getName()));
        pnl.add(pnlTop);

        String tags = friend.getTags();
        if (tags.length() == 0) {
            tags = "none";
        }
        JTextArea txtTags = new JTextArea(tags);
        txtTags.setOpaque(false);
        txtTags.setBorder(BorderFactory.createTitledBorder("Tags"));
        pnl.add(txtTags); // used because label align not working
        return pnl;
    }

    public void setFriends(Collection<Friend> friends) {
        label.setText(friends.size() + " friends");
        panelFriends.removeAll();
        for (Friend f : friends) {
            addFriend(f);
        }
    }

    public Icon getAvatarFromWeb(String url) {
        try {
            if (url != null) {
                BufferedImage i = ImageIO.read(new URL(url));
                if (i != null) {
                    // TODO resize
                    return new ImageIcon(i);
                }
            }
        } catch (Exception ex) {
        }
        // return default
        return new ImageIcon(Loader.get(USER));
    }
}
