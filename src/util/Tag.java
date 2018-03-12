package util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.sf.jabref.AuthorList;
import net.sf.jabref.AuthorList.Author;
import net.sf.jabref.BibtexEntry;
import net.sf.jabref.BibtexEntryType;

/**
 *
 * @author Thien Rong
 */
public class Tag {

    public static void main(String[] args) {
        BibtexEntry entry = new BibtexEntry();
        entry.setField("author", "Domingues, E. G. and Arango, H. and Policarpo G Abreu, J. and Campinho, C. B. and Paulillo, G.");
        entry.setField("year", "2009");
        entry.setField("journal", "Computers and Digital Techniques, IEE Proceedings -");
        entry.setField("keywords", "229, 9fb;1bb 211;334,");
        System.out.println(Tag.extractKeywords(entry));
        entry.setType(BibtexEntryType.ARTICLE);
        System.out.println(extractPossibleTags(entry));
    }

    public static void updateTags(BibtexEntry entry, Set<String> keywords) {
        StringBuffer sb = new StringBuffer();
        for (String k : keywords) {
            sb.append(k).append(",");
        }
        entry.setField("keywords", sb.toString());
    }

    /**
     * TODO Stopwords
     * @param entry
     * @return
     */
    public static Set<String> extractKeywords(BibtexEntry entry) {
        Set<String> validKeywords = new HashSet<String>();
        String kw = entry.getField("keywords");
        if (kw == null) {
            return validKeywords;
        }

        String[] keywords = kw.split("[,;]\\s*");
        for (int i = 0; i < keywords.length; i++) {
            keywords[i] = keywords[i].trim();
            if (keywords[i].length() > 2) {
                validKeywords.add(keywords[i]);
            }
        }

        return validKeywords;
    }

    public static Set<String> extractPossibleTags(BibtexEntry entry) {
        Set<String> keywords = new HashSet<String>();

        for (String author : extractAuthors(entry)) {
            keywords.add(author);
        }

        Integer year = extractYear(entry);
        if (year != null) {
            keywords.add(String.valueOf(year));
        }

        //keywords.add(extractType(entry));

        String journal = extractJournal(entry);
        if (journal != null) {
    //        keywords.add(journal);

        //System.out.println(keywords);

        }
        return keywords;
    }

    /**
     * Not Used
     * @param entry
     * @return
     */
    private static String extractType(BibtexEntry entry) {
        return entry.getType().getName();
    }

    private static Integer extractYear(BibtexEntry entry) {
        String yearText = entry.getField("year");
        try {
            return Integer.parseInt(yearText);
        } catch (Exception ex) {
        }
        return null;
    }

    private static String extractJournal(BibtexEntry entry) {
        String journal = entry.getField("journal");
        if (journal != null) {
            String[] journals = journal.split("[,;]\\s*");
            return journals[0];
        }
        return null;
    }

    private static Collection<String> extractAuthors(BibtexEntry entry) {
        List<String> result = new ArrayList<String>();
        String authorText = entry.getField("author");
        if (authorText == null) {
            return result;
        }

        AuthorList authors = AuthorList.getAuthorList(authorText);
        for (int i = 0; i < authors.size(); i++) {
            Author a = authors.getAuthor(i);
            result.add(a.getFirstLast(true));
        }

        return result;
    }
}
