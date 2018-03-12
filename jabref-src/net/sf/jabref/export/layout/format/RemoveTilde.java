///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile$
//  Purpose:  Atom representation.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg K. Wegner
//  Version:  $Revision: 2107 $
//            $Date: 2007-06-12 23:32:01 +0200 (Tue, 12 Jun 2007) $
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

import net.sf.jabref.export.layout.LayoutFormatter;

/**
 * Replace a non-command tilde ~ by a space.
 * 
 * Usefull for formatting Latex code.
 * 
 * @author $author$
 * @version $Revision: 2107 $
 */
public class RemoveTilde implements LayoutFormatter {

	public String format(String fieldText) {
		
		StringBuffer result = new StringBuffer(fieldText.length());

		char[] c = fieldText.toCharArray();
		
		for (int i = 0; i < c.length; i++) {

			if (c[i] != '~'){
				result.append(c[i]);
				// Skip the next character if the current one is a backslash
				if (c[i] == '\\' && i + 1 < c.length){
					i++;
					result.append(c[i]);
				}
			} else {
				result.append(' ');
			}
		}
		
		return result.toString();
	}
}
