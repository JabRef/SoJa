package model.friend;

import java.io.FileNotFoundException;
import model.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import util.visitor.FriendVisitor;

/**
 * @author Thien Rong
 */
public class FriendsTreeModel extends DefaultTreeModel {

    public static void main(String[] args) throws Exception {
        /*
        FriendsModel mBase = new FriendsModel(new Store("A"));
        FriendsTreeModel m = new FriendsTreeModel(mBase);
        // got to load after listeners set
        mBase.load();
        
        Group g = new Group("test");
        mBase.addGroup(g);
        System.out.println(mBase.getGroups());
        System.out.println("G"+mBase.getGroup(g.name));
        mBase.addFriend(new Friend("test", "test", "ip", 1, 1), g);
        mBase.addFriend(new Friend("test2", "test2", "ip", 1, 1), g);

        mBase.visitAllFriends(new FriendVisitor() {

        public void visitFriend(Friend f) {
        System.out.println(f.propertyChangeSupport);
        }
        });
         */

        XMLEncoder e = new XMLEncoder(new BufferedOutputStream(
                new FileOutputStream("Test.xml")));
        Group g = new Group("A");
        g.addFriend(new Friend("test", "test", "ip", 1, 1));
        g.addFriend(new Friend("test", "test", "ip2", 1, 1));
        e.writeObject(g);
        e.close();

        XMLDecoder d = new XMLDecoder(new BufferedInputStream(
                new FileInputStream("Test.xml")));
        Group g2 = (Group) d.readObject();
        d.close();
        System.out.println(g2);
        for (Friend friend : g2.getFriends()) {
            System.out.println(friend);
        }
    }
    // group -> node so can get back which node is the group
    transient Map<Group, DefaultMutableTreeNode> nodes = new HashMap<Group, DefaultMutableTreeNode>();
    // reference since cannot extends
    FriendsModel model;

    public FriendsTreeModel(FriendsModel model) {
        super(new DefaultMutableTreeNode(new RootGroup()));
        this.model = model;
        for (Group group : model.getGroups().values()) {
            addGroup(group);
            for (Friend friend : group.getFriends()) {
                addFriend(friend, group);
            }
        }
        model.addPropertyChangeListener(new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals(FriendsModel.GROUP_ADD)) {
                    Group g = (Group) evt.getNewValue();
                    addGroup(g);
                } else if (evt.getPropertyName().equals(FriendsModel.FRIEND_ADD)) {
                    FriendGroup fg = (FriendGroup) evt.getNewValue();
                    addFriend(fg.getF(), fg.getG());
                } else if (evt.getPropertyName().equals(FriendsModel.FRIEND_REMOVE)) {
                    FriendGroup fg = (FriendGroup) evt.getOldValue();
                    removeFriend(fg.getF(), fg.getG());
                }
            }
        });

    }

    /**
     * @param g to add, and set the default node if matches name
     */
    public void addGroup(Group g) {
        DefaultMutableTreeNode node = new DefaultMutableTreeNode(g);
        this.getRootNode().add(node);
        nodes.put(g, node);

        this.nodesWereInserted(root, new int[]{root.getIndex(node)});
    }

    public void removeFriend(Friend f, Group g) {
        DefaultMutableTreeNode parent = nodes.get(g);
        if (parent == null) {
            System.out.println("group not found " + g.name + " so friend not removed: " + f);
            return;
        }

        for (int i = 0; i < parent.getChildCount(); i++) {
            DefaultMutableTreeNode child = (DefaultMutableTreeNode) parent.getChildAt(i);
            Friend f2 = (Friend) child.getUserObject();
            if (f2.equals(f)) {
                this.removeNodeFromParent(child);
                break;
            }
        }
    }

    public Friend findFriend(String FUID) {
        return model.findFriend(FUID);
    }

    /**
     * Don't add to this node, instead add to the default node(Not Grouped)
     * @return
     */
    private DefaultMutableTreeNode getRootNode() {
        return (DefaultMutableTreeNode) root;
    }

    public void addFriend(Friend friend, Group g) {
        DefaultMutableTreeNode parent = nodes.get(g);
        if (parent == null) {
            System.out.println("group not found " + g.name + " so friend not added: " + friend);
            return;
        }

        final DefaultMutableTreeNode node = new DefaultMutableTreeNode(friend);
        node.setAllowsChildren(false);
        parent.add(node);

        friend.addPropertyChangeListener(new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent evt) {
                nodeChanged(node);
            }
        });
        this.nodesWereInserted(parent, new int[]{parent.getIndex(node)});
    }

    /**
     *
     * @param node
     * @return true if contains user object instanceof (Friend)
     */
    @Override
    public boolean isLeaf(Object node) {
        Object obj = ((DefaultMutableTreeNode) node).getUserObject();
        return (obj != null && obj instanceof Friend);
    }

    public void visitFriends(String groupName, FriendVisitor v) {
        model.visitFriends(groupName, v);
    }

    public void visitAllFriends(FriendVisitor v) {
        model.visitAllFriends(v);
    }
}