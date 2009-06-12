package view.friend;

import view.*;
import core.Store;
import java.awt.Component;
import java.util.ArrayList;
import java.util.Collection;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeSelectionModel;
import model.friend.Friend;
import model.friend.FriendsModel;
import model.friend.FriendsTreeModel;
import model.friend.Group;
import test.FrameCreator;
import util.Loader;
import util.visitor.FriendVisitor;

/**
 * View for Friends
 */
public class FriendsTree extends JTree {

    public static void main(String[] args) {
        final JFrame f = FrameCreator.createTestFrame();
        FriendsModel model = new FriendsModel(new Store("test")).load();
        final FriendsTreeModel model2 = new FriendsTreeModel(model);
        final FriendsTree tree = new FriendsTree(model2);
        f.add(tree);

        Group g = new Group("Friends");
        model.addGroup(g);
        //tree.expandPath(new TreePath(node.getPath()));
        Friend ff = new Friend("Smith", "Smith", "127.0.0.1", 5150, 5151);
        ff.setConnected(true);
        model.addFriend(ff, g);
        model.addFriend(new Friend("Smith2", "Smith", "127.0.0.1", 5150, 5151), g);
        model.addFriend(new Friend("Smith3", "Smith", "127.0.0.1", 5150, 5151), g);
        model.addFriend(new Friend("Smith4", "Smith", "127.0.0.1", 5150, 5151), g);

        FrameCreator.packAndShow(f);
        /*try {
        while (true) { 
        Thread.sleep(1000);
        System.out.println(tree.getSelectedFriendIDs());
        System.out.println(tree.getSelectedFriend());

        }
        } catch (InterruptedException ex) {
        Logger.getLogger(FriendsTree.class.getName()).log(Level.SEVERE, null, ex);
        }*/
        model.visitAllFriends(new FriendVisitor() {

            public void visitFriend(Friend f2) {
                f2.setConnected(true);
            //TreeModelEvent e = new TreeModelEvent(model, new Object[]{"."});
            //System.out.println(e);
            }
        });
    }

    public FriendsTree(final FriendsTreeModel model) {
        super(model);
        getSelectionModel().setSelectionMode(
                TreeSelectionModel.SINGLE_TREE_SELECTION);
        // else renderer is fixed size 
        setRowHeight(0);
        this.setCellRenderer(new MyFriendRenderer());
        expandAll();
    }

    public void expandAll() {
        for (int i = 0; i < getRowCount(); i++) {
            expandRow(i);
        }
    }

    public Friend getSelectedFriend() {
        Object obj = getLastSelectedPathComponent();

        if (null == obj || false == (treeModel.isLeaf(obj))) {
            return null;
        }

        Object userObj = ((DefaultMutableTreeNode) obj).getUserObject();
        return (Friend) userObj;
    }

    public Collection<Friend> getSelectedFriends() {
        final Collection<Friend> friends = new ArrayList<Friend>();
        FriendVisitor addFriendVisitor = new FriendVisitor() {

            public void visitFriend(Friend f) {
                friends.add(f);
            }
        };
        visitSelectedFriend(addFriendVisitor);
        return friends;
    }

    public Collection<String> getSelectedFriendIDs() {
        final Collection<String> ids = new ArrayList<String>();
        FriendVisitor addFUIDVisitor = new FriendVisitor() {

            public void visitFriend(Friend f) {
                ids.add(f.getFUID());
            }
        };

        visitSelectedFriend(addFUIDVisitor);
        return ids;
    }

    public void visitSelectedFriend(FriendVisitor v) {
        Object obj = getLastSelectedPathComponent();

        if (null == obj) {
            return;
        }

        // only leaf nodes are friends
        if (treeModel.isLeaf(obj)) {
            Object userObj = ((DefaultMutableTreeNode) obj).getUserObject();
            v.visitFriend((Friend) userObj);
            return;
        } else if (obj.equals(treeModel.getRoot())) { // root
            ((FriendsTreeModel) treeModel).visitAllFriends(v);
        } else { // group
            ((FriendsTreeModel) treeModel).visitFriends(obj.toString(), v);
        }
    }
}

class MyFriendRenderer extends DefaultTreeCellRenderer implements ImageConstants {

    final ImageIcon onlineIcon = new ImageIcon(Loader.get(SMILE)),  offlineIcon = new ImageIcon(Loader.get(OFFLINE));

    public Component getTreeCellRendererComponent(
            JTree tree,
            Object value,
            boolean sel,
            boolean expanded,
            boolean leaf,
            int row,
            boolean hasFocus) {

        super.getTreeCellRendererComponent(
                tree, value, sel,
                expanded, leaf, row,
                hasFocus);

        if (leaf) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
            Friend friend = (Friend) node.getUserObject();
            if (friend.isConnected()) {
                setIcon(onlineIcon);
                setToolTipText("I am online @" + friend.getIp());
            } else {
                setIcon(offlineIcon);
                setToolTipText("Offline");
            }
        }/* else {
        setIcon(rootIcon);
        setToolTipText(null);
        }*/
        return this;
    }
}
