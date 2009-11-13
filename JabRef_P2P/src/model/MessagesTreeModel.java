package model;

import core.Store;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;

/**
 *
 * @author Thien Rong
 */
public class MessagesTreeModel extends DefaultTreeModel implements Persistable<MessagesTreeModel> {

    public static void main(String[] args) throws Exception {
        Store s = new Store("test");
        MessagesTreeModel model = new MessagesTreeModel(s);
        model.msgs.add(new BibtexMessage(null, "sub", "msg", "", "to"));
        model.msgs.add(new BibtexMessage(null, "sub", "msg", "", "to"));
        model.msgs.add(new BibtexMessage(null, "sub", "msgxxxx", "", "to"));
        model.msgs.add(new BibtexMessage(null, "sub", "msg\n\n\n", "", "to"));
        model.msgs.add(new BibtexMessage(null, "sub", "\n123\n\n", "", "to"));

        model.save();

        MessagesTreeModel model2 = new MessagesTreeModel(s).load();
        System.out.println(model2.getCount());
    }
    private static final long serialVersionUID = 1L;
    // arraylist for serialiable
    ArrayList<BibtexMessage> msgs = new ArrayList<BibtexMessage>();
    Map<BibtexMessage, MutableTreeNode> msgToNodeMap = new HashMap<BibtexMessage, MutableTreeNode>();
    Store s;

    public MessagesTreeModel(Store s) {
        super(new DefaultMutableTreeNode("Messages"));
        this.s = s;
    }

    public boolean addMessage(BibtexMessage msg) {
        boolean alreadyContains = false;//removeSimilarUnreadEntry(msg);
        //System.out.println("alreadyContains " + alreadyContains);
        if (false == alreadyContains) {
            if (msgs.add(msg)) {
                addNode(msg);
                updateCount();
                save();
                return true;
            }
        }
        return false;
    }

    public boolean containsMessage(BibtexMessage msg){
        for (BibtexMessage bibtexMessage : msgs) {
            if(msg.getGUID().equals(bibtexMessage.getGUID())){
                return true;
            }
        }
        return false;
    }

    public void addNode(BibtexMessage msg) {
        DefaultMutableTreeNode newChild = new DefaultMutableTreeNode(msg);
        this.insertNodeInto(newChild, (DefaultMutableTreeNode) root, msgToNodeMap.size());
        msgToNodeMap.put(msg, newChild);
    }

    public void deleteMessage(BibtexMessage msg) {
        if (msgs.remove(msg)) {
            this.removeNodeFromParent(msgToNodeMap.remove(msg));
            updateCount();
            save();
        }
    }

    /**
     * Only Root node is not a leaf?
     * @param node
     * @return
     */
    @Override
    public boolean isLeaf(Object node) {
        return this.getRoot().equals(node) == false;
    }

    /**
     * @TODO USE FOR SUBSCRIBE BUT NOT EMAIL
     * Comparing the BUID so won't duplicate multiple entry
     * @param msgToCompare
     * @return true if contains duplicate entry => dont add
     */
    /*public boolean removeSimilarUnreadEntry(BibtexMessage msgToCompare) {
    boolean duplicateEntry = f\alse;
    BibtexEntry entryToCompare = msgToCompare.getEntry();
    String buid1 = CustomBibtexField.getBUID(entryToCompare);
    if (buid1 == null) {
    return false;
    }

    Collection<BibtexMessage> toRemoveList = new ArrayList<BibtexMessage>();
    for (BibtexMessage bibtexMessage : msgs) {
    BibtexEntry entry = bibtexMessage.getEntry();
    String buid2 = CustomBibtexField.getBUID(entry);

    if (buid1.equals(buid2)) {
    if (DuplicateCheck.compareEntriesStrictly(entry, entryToCompare) < 1) {
    toRemoveList.add(bibtexMessage);
    } else {
    duplicateEntry = true;
    }
    }
    }

    for (BibtexMessage bibtexMessage : toRemoveList) {
    if (msgs.remove(bibtexMessage)) {
    MutableTreeNode node = msgToNodeMap.get(bibtexMessage);
    if (node != null) {
    this.removeNodeFromParent(node);
    }
    }
    }
    return duplicateEntry;
    }*/
    private void updateCount() {
        ((DefaultMutableTreeNode) root).setUserObject("Messages (" + msgs.size() + ")");
        this.nodeChanged(root);
    }

    public int getCount() {
        return msgs.size();
    }

    public void save() {
        s.writeObject(msgs, "Messages");
    }

    @SuppressWarnings("unchecked")
    public MessagesTreeModel load() {
        try {
            List<BibtexMessage> ss = (List<BibtexMessage>) s.readObject("Messages");
            if (ss != null) {
                for (BibtexMessage bibtexMessage : ss) {
                    this.addMessage(bibtexMessage);
                }
            }
        } catch (Exception ex) {
            s.debugMessage(ex, "Messages", "load");
        }
        return this;
    }

    public void delete() {
        s.deleteObject("Messages");
    }
}