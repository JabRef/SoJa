package util.listener;

import net.sf.jabref.BasePanel;
import net.sf.jabref.BibtexEntry;

/**
 * Called when user click on the main table
 * #hack to allow detection and create push events instead of polling
 * @author Thien Rong
 */
public interface EntrySelectedListener {

    void entrySelected(BibtexEntry entry, BasePanel bp);
}
