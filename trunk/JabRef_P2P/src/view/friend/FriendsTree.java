package view.friend;

import java.awt.event.ActionEvent;
import view.*;
import core.Store;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreePath;
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
 * With Tri-state checkbox from
 * - http://www.javaspecialists.co.za/archive/Issue082.html
 */
public class FriendsTree extends JTree {

    public static void main(String[] args) {
        final JFrame f = FrameCreator.createTestFrame();
        FriendsModel model = new FriendsModel(new Store("test")).load();
        final FriendsTreeModel treeModel = new FriendsTreeModel(model);

        final FriendsTree tree = new FriendsTree(treeModel);
        //CheckTreeSelectionModel selectionModel = new CheckTreeSelectionModel(model2);
        //tree.setSelectionModel(selectionModel);
        //TreeSelectionModel selectionModel = tree.getSelectionModel();
        //System.out.println("" + tree.getSelectionModel());        
        //tree.setCellRenderer(new CheckTreeCellRenderer(tree.getCellRenderer(), selectionModel));
        final CheckTreeManager checkTreeManager = new CheckTreeManager(tree);
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


        JButton btn = new JButton("test");
        f.add(btn, BorderLayout.SOUTH);
        btn.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                System.out.println(tree.getCheckedFriendIDs(checkTreeManager));
             //   tree.setCheckedFriendIDs(checkTreeManager, Arrays.asList("Smith", "Smith4"));
   System.out.println(tree.getCheckedFriendIDs(checkTreeManager));

            }
        });



        FrameCreator.packAndShow(f);
        try {
            while (true) {
                Thread.sleep(1500);
            //System.out.println(tree.getSelectedFriendIDs());
            //System.out.println(tree.getSelectedFriend());
            //System.out.println(selectionModel.getSelectionCount());
            }
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }

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
        getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        // else renderer is fixed size
        this.setRootVisible(false);
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

    public Collection<String> getCheckedFriendIDs(CheckTreeManager checkTreeManager) {
        final Collection<String> ids = new ArrayList<String>();
        FriendVisitor addFUIDVisitor = new FriendVisitor() {

            public void visitFriend(Friend f) {
                ids.add(f.getFUID());
            }
        };

        visitCheckedFriends(addFUIDVisitor, checkTreeManager);
        return ids;
    }

    // set selection
    public void setCheckedFriendIDs(CheckTreeManager checkTreeManager, Collection<String> FUIDs) {
        // need to expand to get the right rowCount
        expandAll();
        // clear current check selection if any
        checkTreeManager.getSelectionModel().clearSelection();
        for (int i = 0; i < getRowCount(); i++) {
            TreePath path = getPathForRow(i);
            Object obj = path.getLastPathComponent();
            if (treeModel.isLeaf(obj)) {
                Object userObj = ((DefaultMutableTreeNode) obj).getUserObject();
                if (userObj instanceof Friend) {
                    Friend f = (Friend) userObj;
                    if (FUIDs.contains(f.getFUID())) {
                        checkTreeManager.getSelectionModel().addSelectionPath(path);
                        System.out.println("adding " + path);
                    }
                }
            }
        }
    /*
    for (int i = 0; i < checkTreeManager.; i++) {
    Object object = arr[i];

    }
    checkTreeManager.getSelectionModel().addSelectionPaths(new TreePath[]{path});

    TreeSelectionModel model = checkTreeManager.getSelectionModel();
    List<TreePath> paths = new ArrayList<TreePath>();

    //TreePath p = new TreePath();
    //model.setSelectionPaths(paths)
    FriendVisitor checkFUIDVisitor = new FriendVisitor() {

    // @TODO allow return false => break
    public void visitFriend(Friend f) {
    if(FUIDs.contains(f.getFUID())){
    System.out.println("checked this" + f.getFUID());
    }
    }
    };

    visitSelectedFriend(addFUIDVisitor, checkTreeManager);
    return ids;*/
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

    public void visitCheckedFriends(FriendVisitor v, CheckTreeManager checkTreeManager) {
        // loop through each checked object
        TreePath[] paths = checkTreeManager.getSelectionModel().getSelectionPaths();
        if (paths == null) {
            return;
        } else {
            for (TreePath treePath : checkTreeManager.getSelectionModel().getSelectionPaths()) {
                Object obj = treePath.getLastPathComponent();
                if (treeModel.isLeaf(obj)) {
                    Object userObj = ((DefaultMutableTreeNode) obj).getUserObject();
                    v.visitFriend((Friend) userObj);
                } else if (obj.equals(treeModel.getRoot())) { // root
                    ((FriendsTreeModel) treeModel).visitAllFriends(v);
                } else { // group
                    ((FriendsTreeModel) treeModel).visitFriends(obj.toString(), v);
                }
            }
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

/**
 * Just adds the checkbox
 */
class CheckTreeCellRenderer extends JPanel implements TreeCellRenderer {

    private CheckTreeSelectionModel selectionModel;
    private TreeCellRenderer delegate;
    private TristateCheckBox checkBox = new TristateCheckBox();

    public CheckTreeCellRenderer(TreeCellRenderer delegate, CheckTreeSelectionModel selectionModel) {
        this.delegate = delegate;
        this.selectionModel = selectionModel;
        setLayout(new BorderLayout());
        setOpaque(false);
        checkBox.setOpaque(false);
    }

    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        Component renderer = delegate.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);

        TreePath path = tree.getPathForRow(row);
        if (path != null) {
            if (selectionModel.isPathSelected(path, true)) {
                checkBox.setState(TristateCheckBox.SELECTED);
            } else {
                checkBox.setState(selectionModel.isPartiallySelected(path) ? TristateCheckBox.DONT_CARE : TristateCheckBox.NOT_SELECTED);
            }
        }

        removeAll();
        add(checkBox, BorderLayout.WEST);
        add(renderer, BorderLayout.CENTER);
        return this;
    }
}