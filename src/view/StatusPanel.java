package view;

import core.SidePanel;
import core.SidePanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.util.List;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import model.friend.Friend;
import test.FrameCreator;
import view.friend.FriendsPanel;
import view.friend.Status;

/**
 *
 * @author Thien Rong
 */
public class StatusPanel extends JPanel {

    public static void main(String[] args) {
        List<Status> statuses = new Vector<Status>();
        Status s = new Status("mama99", 99);
        s.setFriend(new Friend("t", "Joe", "localhost", 1, 2, null, "hello,byebye"));

        statuses.add(s);
        s = new Status("mama9gwqemgvvvvvvqewvpowqe8", 98);
        s.setFriend(new Friend("t2", "Joe", "localhost", 1, 2, "ss", "hello,byebye,gergreg,gergerw,gerwgr"));
        statuses.add(s);
        s = new Status("mama100<br>;lwqvmevwmvpoewmvpowemqv", 100);
        //s.setFriend(new Friend("t3", "Joe2", "localhost", 1, 2));
        statuses.add(s);

        JFrame f = FrameCreator.createTestFrame();
        StatusPanel sp = new StatusPanel(null, true);
        sp.setStatus(statuses);
        f.add(sp);
        FrameCreator.packAndShow(f);
    }
    List<Status> status = new Vector<Status>();
    boolean showOwner;
    // just for the rendering
    FriendsPanel renderer = new FriendsPanel(null, "", 1);
    SidePanel main;

    public StatusPanel(SidePanel main, boolean showOwner) {
        this.main = main;
        this.showOwner = showOwner;
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    }

    public void setStatus(List<Status> statuses) {
        status.addAll(statuses);
        updateView();
    }

    public void updateView() {
        this.removeAll();
        for (Status s : status) {
            JPanel pnlStatus = new JPanel();
            pnlStatus.setLayout(new BoxLayout(pnlStatus, BoxLayout.X_AXIS));

            if (showOwner) {
                Friend f = s.getFriend();
                if (f != null) {
                    Component c = renderer.renderFriend4(f);
                    pnlStatus.add(c, BorderLayout.WEST);                   
                }
            }
            pnlStatus.add(new JLabel(s.getStatus()));
            pnlStatus.setAlignmentX(Component.LEFT_ALIGNMENT);
            this.add(pnlStatus);

        }
        this.updateUI();
    }
}
