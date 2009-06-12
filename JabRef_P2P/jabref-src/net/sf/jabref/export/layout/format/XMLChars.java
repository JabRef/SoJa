///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile$
//  Purpose:  Atom representation.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg K. Wegner, Morten O. Alver
//  Version:  $Revision: 2488 $
//            $Date: 2007-11-14 01:25:31 +0100 (Wed, 14 Nov 2007) $
//            $Author: coezbek $
//
//  Copyright (c) Dept. Computer Architecture, University of Tuebingen, Germany
//
//  This program is free software; you can redistribute it and/or modify
//  it under the terms of the GNU General Public License as published by
//  the Free Software Foundation version 2 of the License.
//
//  This program is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU General Public License for more details.
///////////////////////////////////////////////////////////////////////////////

package net.sf.jabref.export.layout.format;

import java.util.Map;
import java.util.regex.Pattern;

import net.sf.jabref.Globals;
import net.sf.jabref.export.layout.LayoutFormatter;

/**
 * Changes {\^o} or {\^{o}} to ?
 * 
 * @author $author$
 * @version $Revision: 2488 $
 */
public class XMLChars implements LayoutFormatter {
	Pattern pattern = Pattern.compile(".*\\{\\\\.*[a-zA-Z]\\}.*");

	public String format(String fieldText) {

		fieldText = firstFormat(fieldText);

		for (Map.Entry<String, String> entry : Globals.XML_CHARS.entrySet()){
			String s = entry.getKey();
			String repl = entry.getValue();
			if (repl != null)
				fieldText = fieldText.replaceAll(s, repl);
		}
		return restFormat(fieldText);
	}

	private String firstFormat(String s) {
		return s.replaceAll("&|\\\\&", "&#x0026;").replaceAll("--", "&#x2013;");
	}

	boolean[] forceReplace;
	
	private String restFormat(String toFormat) {
		
		String fieldText = toFormat.replaceAll("\\}", "").replaceAll("\\{", "");

		// now some copy-paste problems most often occuring in abstracts when
		// copied from PDF
		// AND: this is accepted in the abstract of bibtex files, so are forced
		// to catch those cases

		if (forceReplace == null){
			 forceReplace = new boolean[126];
			 for (int i = 0; i < 40; i++){
				 forceReplace[i] = true;
			 }
			 forceReplace[32] = false;
			 for (int i : new int[] { 44, 45, 63, 64, 94, 95, 96, 124 }){
				 forceReplace[i] = true;
			 }
		}
		
		StringBuffer buffer = new StringBuffer(fieldText.length() * 2);
		
		for (int i = 0; i < fieldText.length(); i++) {
			int code = (fieldText.charAt(i));
		
			// TODO: Check whether > 125 is correct here or whether it should rather be >=  
			if (code > 125 || forceReplace[code]) {
				buffer.append("&#" + code + ";");
			} else {
				buffer.append((char) code);
			}
		}
		fieldText = buffer.toString();

		// use common abbreviations for <, > instead of code
		for (Map.Entry<String, String> entry : Globals.ASCII2XML_CHARS.entrySet()){
			String s = entry.getKey();
			String repl = entry.getValue();
		
			if (repl != null)
				fieldText = fieldText.replaceAll(s, repl);
		}

		return fieldText;
	}
}
