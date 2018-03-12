/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.util.Map;
import net.sf.jabref.AuthorList;
import net.sf.jabref.BibtexEntry;
import net.sf.jabref.SearchRule;
import net.sf.jabref.export.layout.format.RemoveBrackets;

/**
 *
 * @author Thien Rong
 */
public class NormSearchRule implements SearchRule {

    public static void main(String[] args) {
        AuthorList al = AuthorList.getAuthorList("Domingues, E. G. and Arango, H. and Policarpo G Abreu, J. and Campinho, C. B. and Paulillo, G.");
        for (int i = 0; i < al.size(); i++) {
            System.out.println(al.getAuthor(i).getFirstLast(true));
        }
    }

    static RemoveBrackets removeBrackets = new RemoveBrackets();

    /**
     * Set score of 0 to 100 = fields that match / all fields
     * @param searchStrings
     * @param bibtexEntry
     * @return
     */
    public int applyRule(Map<String, String> searchStrings, BibtexEntry bibtexEntry) {
        String searchString = searchStrings.values().iterator().next();
        searchString = searchString.toLowerCase();

        int score = 0;
        int counter = 0;
        int nonEmptyFields = 0;
        Object fieldContentAsObject;
        String fieldContent;
        for (String field : bibtexEntry.getAllFields()) {
            fieldContentAsObject = bibtexEntry.getField(field);
            if (fieldContentAsObject != null) {
                nonEmptyFields++;
                try {
                    fieldContent = removeBrackets.format(fieldContentAsObject.toString());
                    fieldContent = fieldContent.toLowerCase();

                    counter = fieldContent.indexOf(searchString, counter);
                    if (counter >= 0) {
                        score++;
                    }
                } catch (Throwable t) {
                    System.err.println("sorting error: " + t);
                }
            }
            counter = 0;
        }
        //System.out.println(bibtexEntry + "=" + score + "/" + nonEmptyFields);
        return 100 * score / nonEmptyFields;
    }
}
