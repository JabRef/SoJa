package view;

import core.SidePanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import model.friend.Friend;
import net.sf.jabref.BibtexEntry;
import util.EmailTo;

/**
 *
 */
public class IMDialog extends AbstractIMDialog {

    public static void main(String[] args) {
        IMDialog i = new IMDialog(new Friend("Joe", "Joe", "1.0.0.0", 1, 2), null);
        i.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        i.setVisible(true);
    }
    SidePanel main;
    Friend friend;
    JTextPane txtMsg = new JTextPane();
    JTextField txtInput = new JTextField();
    Color senderColor = Color.BLUE;
    Color warningColor = Color.RED;
    Color regularTextColor = Color.BLACK;
    Color sentTextColor = Color.BLACK;
    SimpleDateFormat df = new SimpleDateFormat("h:mm a");

    public IMDialog(final Friend friend, final SidePanel main) {
        this.setTitle("IM with " + friend.getName());
        this.friend = friend;
        this.main = main;

        HTMLEditorKit htmlKit = new HTMLEditorKit();
        txtMsg.setEditorKit(htmlKit);
        txtMsg.setFocusable(false);
        txtMsg.setDocument(new HTMLDocument());
        txtMsg.setEditable(false);

        txtInput.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                String input = txtInput.getText().trim();
                if (input.length() != 0) {
                    if (friend.isConnected()) {
                        appendTx(input);
                    } else {
                        appendWarning("Message was not delivered as " + friend.getName() + " is offline.");
                    }
                }
            }
        });

        //JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, new JScrollPane(txtMsg), new JScrollPane(txtInput));
        //this.add(split);
        this.add(new JScrollPane(txtMsg));
        JButton btnSendItems = new JButton("Send Items");
        btnSendItems.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                main.handlePrepareEmail(Collections.<BibtexEntry>emptyList(), EmailTo.format(friend), "", "");
            }
        });
        SplitPanel panel = new SplitPanel(txtInput, btnSendItems, BorderLayout.EAST);
        this.add(panel, BorderLayout.SOUTH);
        this.setSize(400, 400);
    }

    public synchronized void appendRx(String message) {
        Date date = new Date();

        // style 1
        /*append(senderColor, friend.getName(), true);
        append(regularTextColor, " wrote ", true);
        append(regularTextColor, "at " + df.format(date), false);
        append(regularTextColor, "\n  " + message + "\n\n", false);
        //append("<br>&nbsp;&nbsp;&nbsp;&nbsp;" + replaceSmiley(message) + "<br/><br/>");
         */

        //String header = replaceFormatting(senderColor, "[" + df.format(date) + "] " + friend.getName() + ": ", true);
        String header = replaceFormatting(senderColor, friend.getName() + ": ", true);
        append(header + replaceSmiley(message) + "<br/>");
    }

    public synchronized void appendTx(String message) {
        Date date = new Date();

        /* style 1
        append(regularTextColor, "I wrote ", true);
        append(regularTextColor, "at " + df.format(date), false);
        //append(sentTextColor, "\n  " + message + "\n\n", false);
        append("<br>&nbsp;&nbsp;&nbsp;&nbsp;" + replaceSmiley(message) + "<br/><br/>");
         */
        //String header = replaceFormatting(senderColor, "[" + df.format(date) + "] me: ", true);
        String header = replaceFormatting(senderColor, "me: ", true);
        append(header + replaceSmiley(message) + "<br/>");

        main.getDealer().sendMsg(friend.getFUID(), message);
        txtInput.setText(null);
    }

    public synchronized void appendWarning(String message) {
        append(warningColor, message + "\n\n", true);
    }

    // http://www.experts-exchange.com/Programming/Languages/Java/Q_23865811.html
    private synchronized void append(final Color c, final String s, final boolean isBold) {
        // http://java.sun.com/developer/JDCTechTips/2005/tt0727.html
        EventQueue.invokeLater(new Thread() {

            @Override
            public void run() {
                this.setPriority(Thread.NORM_PRIORITY);
                append2(c, s, isBold);
            }
        });
    }

    private synchronized void append(final String s) {
        // http://java.sun.com/developer/JDCTechTips/2005/tt0727.html
        EventQueue.invokeLater(new Thread() {

            @Override
            public void run() {
                this.setPriority(Thread.MAX_PRIORITY);
                append2(s);
            }
        });
    }

    private synchronized void append2(String s) {
        try {
            Document doc = txtMsg.getDocument();
            int len = doc.getLength();
            txtMsg.getEditorKit().read(new StringReader(s), doc, len);
            txtMsg.setCaretPosition(doc.getLength());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private synchronized String replaceFormatting(Color c, String s, boolean isBold) {
        StringBuffer sb = new StringBuffer();
        if (isBold) {
            sb.append("<b>");
        }

        sb.append(s);

        if (isBold) {
            sb.append("</b>");
        }
        return sb.toString();
    }

    /**
     * Used for plain only
     * @param c
     * @param s
     * @param isBold
     */
    private synchronized void append2(Color c, String s, boolean isBold) {
        StyleContext sc = StyleContext.getDefaultStyleContext();
        AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY,
                StyleConstants.Foreground, c);
        aset = sc.addAttribute(aset, StyleConstants.Bold, isBold);
        try {
            txtMsg.setEditable(true);
            Document doc = txtMsg.getDocument();
            int len = doc.getLength();
            doc.insertString(len, s, aset);
            txtMsg.setCaretPosition(doc.getLength());
            txtMsg.setEditable(false);
        /*if (txtMsg.getDocument().getLength() > MAX_DOC_SIZE) {
        txtMsg.getDocument().remove(0, s.length());
        }*/

        } catch (Exception e) {
            e.printStackTrace();
        }
    }//end append2
}//end class IMDialog
