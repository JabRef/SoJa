package view.friend;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import model.friend.Friend;
import test.FrameCreator;
import util.OpenDHTHelper;

/**
 * uses invite code and openlookup only
 * @author Thien Rong
 */
public class AddFriendPanel2 extends JPanel {

    public static void main(String[] args) {
        JFrame f = FrameCreator.createTestFrame();
        final AddFriendPanel2 p = new AddFriendPanel2();
        f.add(p);
        JButton btnAdd = new JButton("Add >>");
        btnAdd.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                System.out.println(p.getFriends());
            }
        });
        f.add(btnAdd, BorderLayout.EAST);
        FrameCreator.packAndShow(f);
    }
    JTextField txtInviteCode = new JTextField(15);

    public AddFriendPanel2() {
        this.add(new JLabel("Friend's invite code: "));
        this.add(txtInviteCode);
    }

    public List<Friend> getFriends() {
        List<Friend> result = new ArrayList<Friend>();
        String txt = txtInviteCode.getText().trim();

        OpenDHTHelper dht = new OpenDHTHelper();
        try {
            Friend f = dht.getUserDetails(txt);
            if (f != null) {
                result.add(f);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return result;
    }

    public void setText(String FUID){
        txtInviteCode.setText(FUID);
    }
}
