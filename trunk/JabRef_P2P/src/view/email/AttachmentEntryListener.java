package view.email;

import net.sf.jabref.BibtexEntry;

/**
 *
 * @author Thien Rong
 */
public interface AttachmentEntryListener {

    void entryRemoved(BibtexEntry entry);

    void entryViewed(BibtexEntry entry);
}