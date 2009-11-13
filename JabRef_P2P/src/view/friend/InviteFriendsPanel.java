/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package view.friend;

import core.SidePanel;
import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import util.service.WebShareFactory;

/**
 *
 * @author Thien Rong
 */
public class InviteFriendsPanel extends JPanel {

    public InviteFriendsPanel(SidePanel main) {
        this.setLayout(new BorderLayout());
        JLabel lblEasyPaste = new JLabel("<html>Email your invite code to your friends!<br>" +
                "Your friend can then just paste your invite code to add you.</html>");
        final JTextField txtPasteForEmail = new JTextField(30);
        txtPasteForEmail.setText(main.getMyProfile().getFUID());
        //txtPasteForEmail.setEditable(false);
        txtPasteForEmail.addFocusListener(new FocusAdapter() {

            public void focusGained(FocusEvent e) {
                txtPasteForEmail.selectAll();
            }
        });
        JButton copy = new JButton("Copy");
        copy.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                String selection = txtPasteForEmail.getText();
                StringSelection data = new StringSelection(selection);
                final Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                clipboard.setContents(data, data);
            }
        });

        JPanel pnlInvite = new JPanel(new BorderLayout());
        pnlInvite.add(lblEasyPaste, BorderLayout.NORTH);
        pnlInvite.add(txtPasteForEmail);
        this.add(pnlInvite, BorderLayout.NORTH);

        final String linkUrl = "http://code.google.com/p/jabrefpp/wiki/GettingStarted";

        JPanel pnlWebEmail = new JPanel();
        pnlWebEmail.setBorder(BorderFactory.createTitledBorder("Choose your Email provider"));
        for (final WebShareFactory.WebShareService webShareService : WebShareFactory.getEmailServices()) {
            JButton btn = new JButton(webShareService.getLabel(), webShareService.getIcon());
            pnlWebEmail.add(btn);
            btn.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    try {
                        webShareService.doAction(linkUrl, "Try JabRef peer-to-peer with me", "Hello,\nI have used JabRef p2p, join me to collaborate together. Install using the url above and after installation, copy the my invite code to add me\n" + txtPasteForEmail.getText());
                    } catch (Exception ex) {
                        Logger.getLogger(EditFriendsPanel.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            });
        }
        this.add(pnlWebEmail);

        JPanel pnlWebSocial = new JPanel();
        pnlWebSocial.setBorder(BorderFactory.createTitledBorder("Or post it on these services"));
        for (final WebShareFactory.WebShareService webShareService : WebShareFactory.getSocialService()) {
            JButton btn = new JButton(webShareService.getLabel(), webShareService.getIcon());
            pnlWebSocial.add(btn);
            btn.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    try {
                        webShareService.doAction(linkUrl, "Try JabRef peer-to-peer with me", "Copy the following to add me\n" + txtPasteForEmail.getText());
                    } catch (Exception ex) {
                        Logger.getLogger(EditFriendsPanel.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            });
        }
        this.add(pnlWebSocial, BorderLayout.SOUTH);

        this.setBorder(BorderFactory.createTitledBorder("Invite your friends now!"));
    }
}
