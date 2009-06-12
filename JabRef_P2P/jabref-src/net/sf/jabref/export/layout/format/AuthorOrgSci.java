package net.sf.jabref.export.layout.format;

import net.sf.jabref.AuthorList;
import net.sf.jabref.AuthorList.Author;
import net.sf.jabref.export.layout.LayoutFormatter;

/**
 * Will return the Authors to match the OrgSci format:
 * 
 * <ul>
 * <li>That is the first author is LastFirst, but all others are FirstLast.</li>
 * <li>First names are abbreviated</li>
 * <li>Spaces between abbreviated first names are NOT removed. Use
 * NoSpaceBetweenAbbreviations to achieve this.</li>
 * </ul>
 * <p>
 * See the testcase for examples.
 * </p>
 * <p>
 * Idea from: http://stuermer.ch/blog/bibliography-reference-management-with-jabref.html
 * </p>
 * 
 * @author $Author: coezbek $
 * @version $Revision: 1748 $ ($Date: 2006-09-03 17:20:38 +0200 (Sun, 03 Sep 2006) $)
 * 
 */
public class AuthorOrgSci implements LayoutFormatter {

	public String format(String fieldText) {
		AuthorList a = AuthorList.getAuthorList(fieldText);
		if (a.size() == 0) {
			return fieldText;
		}
		Author first = a.getAuthor(0);
		StringBuffer sb = new StringBuffer();
		sb.append(first.getLastFirst(true));
		for (int i = 1; i < a.size(); i++) {
			sb.append(", ").append(a.getAuthor(i).getFirstLast(true));
		}
		return sb.toString();
	}
}
