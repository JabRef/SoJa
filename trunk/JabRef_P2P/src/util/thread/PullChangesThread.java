/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package util.thread;

import net.sf.jabref.BasePanel;
import net.sf.jabref.JabRefFrame;
import util.listener.PullChangedListener;

/**
 *
 * @author Thien Rong
 */
public class PullChangesThread extends Thread {

    JabRefFrame frame;
    long delay;
    boolean active = true;
    PullChangedListener listener;

    public PullChangesThread(JabRefFrame frame, long delay, PullChangedListener listener) {
        this.frame = frame;
        this.delay = delay;
        this.listener = listener;
    }

    public void run() {
        try {
            if (listener != null) {
                while (active) {
                    Thread.sleep(delay);
                    for (int i = 0; i < frame.getTabbedPane().getTabCount(); i++) {
                        BasePanel bp = frame.baseAt(i);
                        if (false == bp.isBaseChanged()) {
                            //if (Globals.fileUpdateMonitor.hasBeenModified(bp.getFileMonitorHandle())) {
                            listener.fileChanged(bp);
                        }
                    //}
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
