package util.listener;

import net.sf.jabref.BasePanel;

/**
 *
 * @author Thien Rong
 */
public interface PullChangedListener {

    void fileChanged(BasePanel bp);
}