package view.friend;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import model.friend.Friend;
import net.sf.jabref.Globals;
import test.FrameCreator;
import util.FriendStringCodec;

/**
 * 2 ways to add. 1st manual, 2nd textarea paste from friend
 * Use getFriends method for getting the result
 * @author Thien Rong
 */
public class AddFriendPanel extends JPanel {

    public static void main(String[] args) {
        JFrame f = FrameCreator.createTestFrame();
        final AddFriendPanel p = new AddFriendPanel();
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
    JTextArea txtInput = new JTextArea("Paste the text, eg \nname\nIP address\nMain port\nFile Port");
    final JTextField txtName = new JTextField("");
    final JTextField txtIP = new JTextField("");
    final JSpinner spnPort,  spnFilePort;
    JTabbedPane tabbedPane = new JTabbedPane();

    public AddFriendPanel() {

        DefaultFormBuilder builder = new DefaultFormBuilder(
                new FormLayout("left:pref, 4dlu, fill:pref", ""));
        builder.append(Globals.lang("Unique ID/Name") + ":");
        builder.append(txtName);
        builder.append(Globals.lang("IP") + ":");
        builder.append(txtIP);
        builder.append(Globals.lang("Port") + ":");
        SpinnerNumberModel model = new SpinnerNumberModel(5150, 1, 65536, 1);
        spnPort = new JSpinner(model);
        builder.append(spnPort);
        builder.append(Globals.lang("File Port") + ":");
        SpinnerNumberModel model2 = new SpinnerNumberModel(5151, 1, 65536, 1);
        spnFilePort = new JSpinner(model2);
        builder.append(spnFilePort);

        tabbedPane.addTab("Manual Input", builder.getPanel());
        tabbedPane.addTab("Text Input", txtInput);

        this.add(tabbedPane);
    }

    public List<Friend> getFriends() {
        List<Friend> f = new ArrayList<Friend>();
        if (tabbedPane.getSelectedIndex() == 0) {
            f.add(new Friend(txtName.getText(), txtName.getText(), txtIP.getText(),
                    (Integer) spnPort.getValue(),
                    (Integer) spnFilePort.getValue()));
        } else {
            Friend friend = FriendStringCodec.fromString(txtInput.getText());
            if (friend != null) {
                f.add(friend);
            }
        }

        return f;
    }
}
