package util;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import model.friend.Friend;
import model.friend.FriendsModel;
import net.sf.jabref.BasePanel;
import net.sf.jabref.BibtexEntry;
import net.sf.jabref.DuplicateCheck;
import net.sf.jabref.GUIGlobals;
import net.sf.jabref.JabRefFrame;
import net.sf.jabref.SearchRuleSet;
import net.sf.jabref.Util;
import net.sf.jabref.external.AccessLinksForEntries;
import net.sf.jabref.gui.FileListEntry;
import util.visitor.EntryVisitor;
import util.visitor.TagFreqVisitor;

/**
 * Contains method to search for entries, get entries
 * @TODO sort the entries to provide binary search speed?
 * @author Thien Rong
 */
public class FrameUtil {

    /**
     * @param frame
     * @return the directory in settings. If invalid, return the current directory instead
     */
    public static File getSaveDirectory(JabRefFrame frame) {
        String fileDir = frame.basePanel().metaData().getFileDirectory(GUIGlobals.FILE_FIELD);
        File dir = new File(fileDir);
        if (false == dir.isDirectory()) {
            dir = new File(".");
        }

        return dir;
    }

    /**
     * @param frame
     * @param filePath
     * @return if File exists, the File with relative Path resolved. Else return null
     */
    public static File checkFileExists(JabRefFrame frame, String filePath) {
        // check file exists first
        String fileDir = frame.basePanel().metaData().getFileDirectory(GUIGlobals.FILE_FIELD);
        File f = Util.expandFilename(filePath, fileDir);
        boolean fileExists = (f != null && f.isFile());

        if (fileExists) {
            return f;
        } else {
            return null;
        }
    }

    /**     
     * @param frame
     * @param entry
     * @param filePath
     * @return true if entry filelist has 1 file containing the filePath, else false
     */
    public static boolean validateFileLink(JabRefFrame frame, BibtexEntry entry, String filePath) {
        List<FileListEntry> fileListEntries = AccessLinksForEntries.getExternalLinksForEntries(Arrays.asList(entry));
        for (FileListEntry fileListEntry : fileListEntries) {
            if (fileListEntry.getLink().equals(filePath)) {
                return true;
            }
        }
        return false;
    }

    public static Collection<BibtexEntry> getEntriesSharedToFriend(Friend friend, FriendsModel friendsModel, JabRefFrame frame) {

        FriendEntryVisitor handler = new FriendEntryVisitor(friend, friendsModel);
        run(frame, handler);

        return handler.getEntries();
    }

    /**
     * Return the visitor containing the tag freq map
     * and entries without tag/keyword
     * @param frame
     * @return
     */
    public static TagFreqVisitor getTagFreqVisitor(JabRefFrame frame) {
        TagFreqVisitor handler = new TagFreqVisitor();
        run(frame, handler);
        return handler;
    }


    public static void autoInsertTag(JabRefFrame frame) {
        AutoTagVisitor handler = new AutoTagVisitor();
        run(frame, handler);

    }

    public static Collection<BibtexEntry> getEntriesPassSearch(Friend friend, FriendsModel model, SearchRuleSet searchRules, Map<String, String> searchOptions, JabRefFrame frame) {
        SearchEntryVisitor handler = new SearchEntryVisitor(friend, model, searchRules, searchOptions);
        run(frame, handler);

        return handler.getEntries();
    }

    public static BibtexEntry getEntryWithBUID(String BUIDToCheck, JabRefFrame frame) {
        CheckBUIDExistsVisitor handler = new CheckBUIDExistsVisitor(BUIDToCheck);
        run(frame, handler);

        return handler.getEntry();
    }

    private static void run(JabRefFrame frame, EntryVisitor handler) {
        outerLoop:
        for (int i = 0; i < frame.getTabbedPane().getTabCount(); i++) {
            BasePanel bp = frame.baseAt(i);
            for (BibtexEntry bibtexEntry : bp.database().getEntries()) {
                if (false == handler.visitEntry(bibtexEntry, bp)) {
                    break outerLoop;
                }
            }
        }
    }
}

class FriendEntryVisitor implements EntryVisitor {

    Friend friendToSend;
    FriendsModel friendsModel;
    Collection<BibtexEntry> entries = new ArrayList<BibtexEntry>();

    public FriendEntryVisitor(Friend friendToSend, FriendsModel friendsModel) {
        this.friendsModel = friendsModel;
        this.friendToSend = friendToSend;
    }

    public boolean visitEntry(BibtexEntry entry, BasePanel bp) {
        List<Friend> shareToFriends = CustomBibtexField.getBibtexShare(entry, friendsModel);
        for (Friend friend : shareToFriends) {
            if (friendToSend.equals(friend)) {
                entries.add(entry);
                break;
            }
        }
        return true;
    }

    public Collection<BibtexEntry> getEntries() {
        return entries;
    }
}

/**
 * Reuse FriendEntryVisitor to check share
 * @author Thien Rong
 */
class SearchEntryVisitor extends FriendEntryVisitor implements EntryVisitor {

    SearchRuleSet searchRules;
    Map<String, String> searchOptions;
    Collection<BibtexEntry> validEntries = new ArrayList<BibtexEntry>();

    public SearchEntryVisitor(Friend friend, FriendsModel model, SearchRuleSet searchRules, Map<String, String> searchOptions) {
        super(friend, model);
        this.searchRules = searchRules;
        this.searchOptions = searchOptions;
    }

    public boolean visitEntry(BibtexEntry entry, BasePanel bp) {
        // if share allowed, entries will be added
        entries.clear(); // of superclass
        super.visitEntry(entry, bp);
        // not added => not shared
        if (entries.size() == 0) {
            return true;
        }

        int score = searchRules.applyRule(searchOptions, entry);
        if (score > 0) {
            CustomBibtexField.addScoreToEntry(entry, score);
            validEntries.add(entry);
        }
        return true;
    }

    public Collection<BibtexEntry> getEntries() {
        return validEntries;
    }
}

class CheckBUIDExistsVisitor implements EntryVisitor {

    String BUIDToCheck;
    BibtexEntry entry;

    public CheckBUIDExistsVisitor(String BUIDToCheck) {
        this.BUIDToCheck = BUIDToCheck;
    }

    public boolean visitEntry(BibtexEntry entry, BasePanel bp) {
        String buid = CustomBibtexField.getBUID(entry);
        if (buid != null && buid.equals(BUIDToCheck)) {
            this.entry = entry;
            return false;
        }

        return true;
    }

    public BibtexEntry getEntry() {
        return entry;
    }
}

class StrictDuplicateCheckVisitor implements EntryVisitor {

    BibtexEntry bibToComp;
    boolean duplicateFound = false;

    public StrictDuplicateCheckVisitor(BibtexEntry bibToComp) {
        this.bibToComp = bibToComp;
    }

    public boolean isDuplicateFound() {
        return duplicateFound;
    }

    public boolean visitEntry(BibtexEntry entry, BasePanel bp) {
        if (DuplicateCheck.compareEntriesStrictly(entry, bibToComp) > 1) {
            duplicateFound = true;
            return false;
        }

        return true;

    }
}

class AutoTagVisitor implements EntryVisitor {

    public boolean visitEntry(BibtexEntry entry, BasePanel bp) {
        Set<String> keywords = Tag.extractKeywords(entry);
        int prevSize = keywords.size();

        Set<String> possibleKeywords = Tag.extractPossibleTags(entry);
        keywords.addAll(possibleKeywords);

        if (prevSize != keywords.size()) {
            Tag.updateTags(entry, keywords);
            bp.updateEntryEditorIfShowing();
        }

        return true;
    }
}