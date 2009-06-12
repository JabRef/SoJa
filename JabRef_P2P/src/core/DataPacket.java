package core;

import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serializable;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.Border;

/**
 *
 * @author Thien Rong
 */
public class DataPacket implements Serializable {

    String destFUID;
    String header;
    String msg;
    Serializable object;
    // tag to each request, so can know whether to drop resposne based on a previous request
    String requestID;

    public DataPacket(String destFUID, String requestID, String header, String msg, Serializable object) {
        this.destFUID = destFUID;
        this.requestID = requestID;
        this.header = header;
        this.msg = msg;
        this.object = object;
    }
}
