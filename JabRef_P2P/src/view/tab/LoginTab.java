package view.tab;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;
import core.NetworkDealer;
import core.SidePanel;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import model.friend.MyProfile;
import net.sf.jabref.Globals;
import util.security.RSA;
import view.friend.MyProfileView;

/**
 *
 * @author Thien Rong
 */
public class LoginTab extends ITab {

    MyProfileView profileView;
    //SidePanel main;

    public LoginTab(final SidePanel main) {
        //  super(main.getFrame());
        //  this.main = main;
        super(main);
        profileView = new MyProfileView();
        this.setLayout(new BorderLayout());
        // name and port fields
        final JTextField txtName = new JTextField("");

        String labelTip = "<html>For new user, just enter a name. You will then be registered on this computer with the name. " +
                "<br/>To login the next time, just enter the same name and your information will be loaded.</html>";
        JPanel topPanel = new JPanel(new GridLayout(0, 1));
        topPanel.add(new JLabel(labelTip));
        topPanel.add(new JLabel(Globals.lang("Name")));
        topPanel.add(txtName);
        this.add(topPanel, BorderLayout.NORTH);

        SpinnerNumberModel model = new SpinnerNumberModel(5150, 1, 65536, 1);
        final JSpinner spnPort = new JSpinner(model);
        SpinnerNumberModel model2 = new SpinnerNumberModel(5151, 1, 65536, 1);
        final JSpinner spnFilePort = new JSpinner(model2);
        // hide from view, since user need not know
        /*DefaultFormBuilder builder = new DefaultFormBuilder(
        new FormLayout("left:pref, 4dlu, fill:pref", ""));

        builder.append(Globals.lang("Name") + ":");
        builder.append(txtName);
        builder.append(Globals.lang("Port") + ":");
        builder.append(spnPort);
        builder.append(Globals.lang("File Port") + ":");
        builder.append(spnFilePort);
        this.add(builder.getPanel(), BorderLayout.NORTH);*/

        // start button
        JButton btnStart = new JButton(Globals.lang("Login/Register"));
        btnStart.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                try {
                    NetworkDealer dealer =
                            NetworkDealer.openNetworkDealer(main.getFrame(),
                            new MyProfile(txtName.getText(),
                            (Integer) spnPort.getValue(),
                            (Integer) spnFilePort.getValue(),
                            RSA.generateKeyPair()),// use name as FUID as a start
                            main);
                    if (null == dealer) {
                        main.getFrame().showMessage("Fail to start, please try changing the port.");
                    } else {
                        main.setDealer(dealer);
                        main.updateLoginUI(LoginTab.this);
                    }
                } catch (NumberFormatException nfe) {
                    main.getFrame().showMessage("Port must be a number");
                }

            }
        });
        // 2 btns to make testing faster
        JButton btnJoe = new JButton(Globals.lang("Start As Joe"));
        btnJoe.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                NetworkDealer dealer =
                        NetworkDealer.openNetworkDealer(main.getFrame(),
                        createOrLoadProfile(
                        new MyProfile("Joe", 5152, 5153, RSA.toKeyPair(RSA.testJoePublicKey, RSA.testJoePrivateKey))),
                        main);
                if (null == dealer) {
                    main.getFrame().showMessage("Fail to start");
                } else {
                    main.setDealer(dealer);
                    main.updateLoginUI(LoginTab.this);
                }
            }
        });
        JButton btnSmith = new JButton(Globals.lang("Start As Smith"));
        btnSmith.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                NetworkDealer dealer =
                        NetworkDealer.openNetworkDealer(main.getFrame(),
                        new MyProfile("Smith", 5150, 5151, RSA.toKeyPair(RSA.testSmithPublicKey, RSA.testSmithPrivateKey)),
                        main);
                if (null == dealer) {
                    main.getFrame().showMessage("Fail to start");
                } else {
                    main.setDealer(dealer);
                    main.updateLoginUI(LoginTab.this);
                }
            }
        });

        JPanel pnlCtrl = new JPanel();
        pnlCtrl.add(btnStart);
        this.add(pnlCtrl);

        JPanel pnlDebug = new JPanel();
        pnlDebug.setBorder(BorderFactory.createTitledBorder("FOR DEBUG, DON'T USE"));
        pnlDebug.add(btnJoe);
        pnlDebug.add(btnSmith);
        if (main.debug) {
            this.add(pnlDebug, BorderLayout.SOUTH);
        }
    }

    private MyProfile createOrLoadProfile(MyProfile deftTestingProfile) {
        return deftTestingProfile;
    }

    private MyProfile createOrLoadProfile(String name) {
        MyProfile p = MyProfile.loadProfile(name);
        if (p != null) {
            profileView.setProfile(p, false);
        } else {
            p = new MyProfile(name, 5850, 5851, RSA.generateKeyPair());
            profileView.setProfile(p, true);
        }
        //profileView.setVisible(true);
        return p;
    }
}
