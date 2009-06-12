package core;

import javax.swing.Icon;
import javax.swing.JPanel;
import model.friend.FriendsModel;
import net.sf.jabref.BasePanel;
import net.sf.jabref.BibtexDatabase;
import net.sf.jabref.BibtexEntry;
import net.sf.jabref.Globals;
import net.sf.jabref.MetaData;
import net.sf.jabref.external.PushToApplication;
import net.sf.jabref.plugin.PluginCore;
import org.java.plugin.PluginLifecycleException;
import org.java.plugin.PluginManager;
import view.EditShareDialog;
import net.sf.jabref.GUIGlobals;

/**
 *
 * @author Thien Rong
 */
public class SharePlugin implements PushToApplication {

    Store s = new Store("test");
    FriendsModel friendsModel = new FriendsModel(s);
    private String name = "P2P Test";
    // push to application
    private JPanel settings = null;
    private EditShareDialog dialogEditShare;//TODO = new EditShareDialog(friendsModel);

    public SharePlugin() throws PluginLifecycleException {
        PluginManager a = PluginCore.getManager();
    //System.out.println("=====" + a.getPlugin("SidePlugin"));
        /*for (PluginDescriptor pluginDescriptor : a.getRegistry().getPluginDescriptors()) {
    System.out.println(pluginDescriptor);
    }*/
    }

    public String getName() {
        return name;
    }

    public String getApplicationName() {
        return "P2P Test Push";
    }

    public String getTooltip() {
        return Globals.lang("Edit Sharing Settings");
    }

    public String getKeyStrokeName() {
        return null;
    }

    public JPanel getSettingsPanel() {
        // lazy init
        if (settings == null) {
            settings = new JPanel();
        }
        return settings;
    }

    public void storeSettings() {
    }

    public void pushEntries(BibtexDatabase database, BibtexEntry[] entries, String keyString, MetaData metaData) {
        dialogEditShare.setEntries(entries, friendsModel);
        //Util.placeDialog(dialogEditShare, frame);
        dialogEditShare.setVisible(true);
    }

    public void operationCompleted(BasePanel panel) {
    }

    public boolean requiresBibtexKeys() {
        return false;
    }

    public Icon getIcon() {
        return GUIGlobals.getImage("right");
    }
}
