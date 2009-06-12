package view.friend;

import core.SidePanel;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import model.friend.Friend;
import model.friend.FriendRequestsModel;
import test.FrameCreator;

/**
 * 
 * @author Thien Rong
 */
public class FriendRequestsView extends JPanel implements PropertyChangeListener {

    public static void main(String[] args) {
        JFrame f = FrameCreator.createTestFrame();
        FriendRequestsView v = new FriendRequestsView(null);
        f.add(v);
        v.addRequest(new Friend("name", "FUID", "ip", 1, 2));
        v.addRequest(new Friend("name", "FUID", "ip", 1, 2));
        v.addRequest(new Friend("name", "FUID", "ip", 1, 2));
        v.addRequest(new Friend("name", "FUID", "ip", 1, 2));

        FrameCreator.packAndShow(f);
    }
    DefaultListModel m = new DefaultListModel();
    JList l = new JList(m);
    SidePanel main;

    public FriendRequestsView(final SidePanel main) {
        this.main = main;
        this.setLayout(new BorderLayout());
        l.setCellRenderer(new FriendRequestsRenderer());
        //l.setOpaque(false);
        this.add(new JScrollPane(l));

        JPanel pnlCtrl = new JPanel();
        JButton btnAccept = new JButton("Accept");
        btnAccept.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                Friend r = getSelectedRequest();
                if (r != null) {
                    removeRequest(r);
                    main.handleAcceptedFriendRequest(r);
                }
            }
        });
        pnlCtrl.add(btnAccept);

        JButton btnReject = new JButton("Reject");
        btnReject.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                Friend r = getSelectedRequest();
                if (r != null) {
                    removeRequest(r);
                }
            }
        });
        pnlCtrl.add(btnReject);

        this.add(pnlCtrl, BorderLayout.SOUTH);

        main.getRequestModel().addPropertyChangeListener(this);
        // show only if not empty
        this.setVisible(false);
    }

    public Friend getSelectedRequest() {
        return (Friend) l.getSelectedValue();
    }

    public void addRequest(Friend request) {
        m.addElement(request);
        this.setVisible(!m.isEmpty());
    }

    public void removeRequest(Friend request) {
        m.removeElement(request);
        this.setVisible(!m.isEmpty());
    }

    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(FriendRequestsModel.REQUEST_ADD)) {
            Friend r = (Friend) evt.getNewValue();
            this.addRequest(r);
        }
    }

    class FriendRequestsRenderer extends DefaultListCellRenderer {

        @Override
        public Component getListCellRendererComponent(
                JList list,
                Object value,
                int index,
                boolean isSelected,
                boolean cellHasFocus) {
            final Friend r = ((Friend) value);

            JLabel c = (JLabel) super.getListCellRendererComponent(list, r.getName() + " sent you a friend request", index, isSelected, cellHasFocus);
            //c.setOpaque(false);
            return c;
        /*JPanel pnlCtrl = new JPanel();
        pnlCtrl.setOpaque(false);
        JButton btnAccept = new JButton("Accept");
        btnAccept.addActionListener(new ActionListener() {

        public void actionPerformed(ActionEvent e) {
        removeRequest(r);
        main.handleAcceptedFriendRequest(r);
        }
        });
        pnlCtrl.add(btnAccept);

        JButton btnReject = new JButton("Reject");
        btnReject.addActionListener(new ActionListener() {

        public void actionPerformed(ActionEvent e) {
        removeRequest(r);
        }
        });
        pnlCtrl.add(btnReject);
        return pnlCtrl;*/
        //return new SplitPanel(c, pnlCtrl, BorderLayout.SOUTH);

        }
    }
}
