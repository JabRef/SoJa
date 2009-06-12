package net.sf.jabref.export.layout.format;

import net.sf.jabref.export.layout.LayoutFormatter;
import net.sf.jabref.Globals;

/**
 * Used to fix [ 1588028 ] export HTML table doi url.
 * 
 * Will prepend "http://dx.doi.org/" if only doi number and not a URL is given.
 * 
 * @author $Author: mark-schenk $
 * @version $Revision: 2632 $ ($Date: 2008-03-26 23:17:09 +0100 (Wed, 26 Mar 2008) $)
 *
 */
public class DOICheck implements LayoutFormatter {

	public String format(String fieldText) {
		
		if (fieldText == null){
			return null;
		}
		
		fieldText = fieldText.trim();
		
		if (fieldText.length() == 0){
			return fieldText;
		}

		/*
		* Author: mark-schenk
		* If DOI is only number, or doi:number, add the required http://dx.doi.org/ prefix
		*/
		
		// Remove possible 'doi:'
		if (fieldText.matches("^doi:/*.*")){
			fieldText = fieldText.replaceFirst("^doi:/*", "");
			fieldText = Globals.DOI_LOOKUP_PREFIX + fieldText;
			return fieldText;
		}
		// If starts with '10.'
		if (fieldText.startsWith("10.")) {
			fieldText = Globals.DOI_LOOKUP_PREFIX + fieldText;
			return fieldText;
		}

		return fieldText;
	}
}
