///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile$
//  Purpose:  Atom representation.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Egon Willighagen
//  Version:  $Revision: 2628 $
//            $Date: 2008-03-25 17:26:10 +0100 (Tue, 25 Mar 2008) $
//            $Author: mortenalver $
//
//  Copyright (c) Egon Willighagen
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
 * Convert the contents to lower case.
 * 
 * @author $author$
 * @version $Revision: 2628 $
 */
public class ToLowerCase implements LayoutFormatter {

	public String format(String fieldText) {
		return fieldText.toLowerCase();
	}
}
