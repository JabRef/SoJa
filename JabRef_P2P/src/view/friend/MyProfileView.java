package view.friend;

import java.awt.Component;
import java.awt.Frame;
import java.awt.GridLayout;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTextField;
import model.friend.MyProfile;
import test.FrameCreator;

/**
 *
 * @author Thien Rong
 */
public class MyProfileView extends JDialog {

    JLabel lblMsg = new JLabel();
    JTextField txtName = new JTextField();
    JTextField txtMainPort = new JTextField();
    JTextField txtFilePort = new JTextField();
    JTextField txtFUID = new JTextField();

    public static void main(String[] args) {
        JFrame f = FrameCreator.createTestFrame();
        MyProfileView v = new MyProfileView();
        v.pack();
        v.setVisible(true);
        //v.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //f.setVisible(true);

    }
    MyProfile profile;

    public MyProfileView() {
        super();//"Profile View"
        this.setTitle("My Profile");
        this.add(buildGUI());
    }

    private Component buildGUI() {
        JPanel pnlCtrl = new JPanel(new GridLayout(0, 1));
        pnlCtrl.add(lblMsg);
        pnlCtrl.add(new JLabel("Name"));
        pnlCtrl.add(txtName);
        pnlCtrl.add(new JLabel("Main Port"));
        pnlCtrl.add(txtMainPort);
        pnlCtrl.add(new JLabel("File Port"));
        pnlCtrl.add(txtFilePort);
        pnlCtrl.add(new JLabel("Public Key"));
        pnlCtrl.add(txtFUID);

        return pnlCtrl;
    }

    public void setProfile(MyProfile profile, boolean isNew) {
        if (isNew) {
            lblMsg.setText("New profile loaded");
        } else {
            lblMsg.setText("Profile found and loaded");
        }
        //this.profile = profile;
        this.txtName.setText(profile.getName());
        this.txtMainPort.setText(String.valueOf(profile.getPort()));
        this.txtFilePort.setText(String.valueOf(profile.getFilePort()));
        this.txtFUID.setText(profile.getFUID());
    }
}
