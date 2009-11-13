package view.friend;

import core.SidePanel;
import javax.swing.JDialog;

/**
 * Contain Dialog for the EditFriendsDialog
 * @author Thien Rong
 */
public class EditFriendDialog extends JDialog {

    public EditFriendDialog(SidePanel main) {
        super(main.getFrame());
        this.setTitle("Edit Friends");

        this.add(new EditFriendsPanel(main, true));
        this.pack();
    }
}
