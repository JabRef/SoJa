package view.email;

import java.awt.Component;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeSelectionModel;
import model.BibtexMessage;
import model.friend.Friend;
import core.SidePanel;
import model.friend.FriendsModel;

/**
 * View for mails
 * @author Thien Rong
 */
public class MessagesTree extends JTree {

    public MessagesTree(SidePanel main) {
        super(main.getMessagesModel());
        getSelectionModel().setSelectionMode(
                TreeSelectionModel.SINGLE_TREE_SELECTION);
        // else renderer is fixed size
        setRowHeight(0);
        setCellRenderer(new MyMessageRenderer(main.getFriendsModel()));
    }

    public BibtexMessage getSelectedMessage() {
        Object obj = getLastSelectedPathComponent();
        if (null == obj) {
            return null;
        }

        DefaultMutableTreeNode node = (DefaultMutableTreeNode) obj;
        if (false == node.getUserObject() instanceof BibtexMessage) {
            return null;
        }

        return (BibtexMessage) node.getUserObject();
    }
}

class MyMessageRenderer extends DefaultTreeCellRenderer {

    FriendsModel model;

    public MyMessageRenderer(FriendsModel model) {
        this.model = model;
    }

    public Component getTreeCellRendererComponent(
            JTree tree,
            Object value,
            boolean sel,
            boolean expanded,
            boolean leaf,
            int row,
            boolean hasFocus) {

        super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
        if (leaf) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
            BibtexMessage msg = (BibtexMessage) node.getUserObject();

            Friend f = model.findFriend(msg.getFromFUID());
            if (f != null) {
                setText("<html><b>" + f.getName() + "</b><br>&nbsp;&nbsp;" + msg.getSummary(40) + "</html>");
                setToolTipText(msg.getMsg());
            } else {
                setText(msg.getSubject());
            }
        }
        return this;
    }
}
