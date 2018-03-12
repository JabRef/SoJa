/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import net.sf.jabref.plugin.PluginCore;
import net.sf.jabref.plugin.util.Util;
import org.java.plugin.Plugin;
import org.java.plugin.PluginLifecycleException;

/**
 *
 * @author Thien Rong
 */
public class Loader {

    public static void main(String[] args) {
        URL u = get("images");
        System.out.println(u);
    }
    // sync with plugin.xml if this changed too
    static final String PLUGIN_ID = "com.google.code.jabrefpp";
    private static ClassLoader singleton;
    private static Map<String, URL> cache = new HashMap<String, URL>();

    public static ClassLoader getLoader() {
        if (singleton != null) {
            return singleton;
        }

        try {
            Plugin plugin = PluginCore.getManager().getPlugin(PLUGIN_ID);
            singleton = Util.getClassLoader(plugin);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        // use default. Maybe testing not at plugin
        if (singleton == null) {
            singleton = ClassLoader.getSystemClassLoader();
        }
        return singleton;
    }

    public static URL get(String url) {
        URL u = cache.get(url);
        if (u == null) {
            u = getLoader().getResource(url);
            cache.put(url, u);
        }
        return u;
    }
}
