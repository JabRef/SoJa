package util.visitor;

import net.sf.jabref.BasePanel;
import net.sf.jabref.BibtexEntry;

/**
 *
 * @author Thien Rong
 */
public interface EntryVisitor {

    /**
     * @param entry
     * @return true to continue or false to stop(eg checking for duplicate)
     */
    public boolean visitEntry(BibtexEntry entry, BasePanel bp);
}
