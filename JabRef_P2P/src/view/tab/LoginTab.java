package view.tab;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;
import core.NetworkDealer;
import core.SidePanel;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import model.friend.Friend;
import model.friend.MyProfile;
import net.sf.jabref.Globals;

/**
 *
 * @author Thien Rong
 */
public class LoginTab extends ITab {

    //SidePanel main;
    public LoginTab(final SidePanel main) {
        //  super(main.getFrame());
        //  this.main = main;
        super(main);
        this.setLayout(new BorderLayout());
        // name and port fields
        final JTextField txtName = new JTextField("");
        DefaultFormBuilder builder = new DefaultFormBuilder(
                new FormLayout("left:pref, 4dlu, fill:pref", ""));
        builder.append(Globals.lang("Unique ID/Name") + ":");
        builder.append(txtName);
        builder.append(Globals.lang("Port") + ":");
        SpinnerNumberModel model = new SpinnerNumberModel(5150, 1, 65536, 1);
        final JSpinner spnPort = new JSpinner(model);
        builder.append(spnPort);
        builder.append(Globals.lang("File Port") + ":");
        SpinnerNumberModel model2 = new SpinnerNumberModel(5151, 1, 65536, 1);
        final JSpinner spnFilePort = new JSpinner(model2);
        builder.append(spnFilePort);
        this.add(builder.getPanel(), BorderLayout.NORTH);

        // start button
        JButton btnStart = new JButton(Globals.lang("Start"));
        btnStart.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                try {
                    NetworkDealer dealer =
                            NetworkDealer.openNetworkDealer(main.getFrame(),
                            new MyProfile(txtName.getText(), txtName.getText(),
                            (Integer) spnPort.getValue(), (Integer) spnFilePort.getValue()),// use name as FUID as a start
                            main);
                    if (null == dealer) {
                        main.getFrame().showMessage("Fail to start");
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
                        new MyProfile("Joe", "Joe", 5152, 5153),
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
                        new MyProfile("Smith", "Smith", 5150, 5151),
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

        this.add(pnlDebug, BorderLayout.SOUTH);
    }
}
