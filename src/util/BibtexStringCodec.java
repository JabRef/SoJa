package util;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Collection;
import java.util.Set;
import net.sf.jabref.BasePanel;
import net.sf.jabref.BibtexEntry;
import net.sf.jabref.Globals;
import net.sf.jabref.export.LatexFieldFormatter;
import net.sf.jabref.imports.BibtexParser;

/**
 * Reusable Formatter and StringWriter
 * Encode Bibtex to/from String(for transfer)
 */
public class BibtexStringCodec {

    static StringWriter sw = new StringWriter(200);
    static LatexFieldFormatter formatter = new LatexFieldFormatter();

    /**
     * @param entry the BibtexEntry to encode
     * @return the latex string
     * @throws java.io.IOException unlikely, unless Entry is really broken
     */
    public static String toString(BibtexEntry entry) throws IOException {
        // clear for any previous use
        sw.getBuffer().setLength(0);
        entry.write(sw, formatter, false);

        return sw.toString();
    }

    /**
     * If myFUID is not empty and BUID is empty, generate BUID
     * Also resolve string and also share to friend
     * // TODO remove the citeKey but on clone version?
     * @param bp
     * @param myFUID
     * @param entry
     * @return
     * @throws java.io.IOException
     */
    public static String toStringForPeer(BasePanel bp, BibtexEntry entry, String myFUID) throws IOException {
        CustomBibtexField.genBUID(bp, entry, myFUID);
        BibtexEntry resolvedEntry = bp.database().resolveForStrings(entry, false);
        return toString(resolvedEntry);
    }

    /**
     * @param latex the string to parse
     * @return null if invalid
     */
    public static BibtexEntry fromString(String latex) {
        BibtexEntry entry = BibtexParser.singleFromString(latex);
        return entry;
    }

    /**
     * Call toStringForPeer for each entry and add NEWLINE after each entry
     * @param bp
     * @param entries
     * @param myFUID
     * @param friendsTo if null, don't add else add friends (Used during email)
     * @return
     * @throws java.io.IOException
     */
    public static String toStringListForPeer(BasePanel bp, Collection<BibtexEntry> entries, String myFUID, Set<String> friendsTo) throws IOException {
        // Cannot reuse since toString will clear and reuse
        StringWriter sw2 = new StringWriter(200);
        for (BibtexEntry entry : entries) {
            CustomBibtexField.addShareToEntry(bp, entry, friendsTo);
            sw2.write(toStringForPeer(bp, entry, myFUID));
            sw2.write(Globals.NEWLINE);
        }
        return sw2.toString();
    }

    public static String toStringList(Collection<BibtexEntry> entries) throws IOException {
        // clear for any previous use
        sw.getBuffer().setLength(0);
        for (BibtexEntry entry : entries) {
            entry.write(sw, formatter, false);
            sw.write(Globals.NEWLINE);
        }
        return sw.toString();
    }

    /**
     * @param latex
     * @return null if invalid
     */
    public static Collection<BibtexEntry> fromStringList(String latex) {
        Collection<BibtexEntry> entries = BibtexParser.fromString(latex);
        return entries;
    }
}
