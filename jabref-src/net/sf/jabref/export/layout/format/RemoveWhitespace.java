///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile$
//  Purpose:  Atom representation.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg K. Wegner
//  Version:  $Revision: 2339 $
//            $Date: 2007-09-10 21:42:01 +0200 (Mon, 10 Sep 2007) $
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
 * Remove non printable character formatter.
 * 
 * Based on the RemoveBrackets.java class (Revision 1.2) by mortenalver
 * 
 * @author $author$
 * @version $Revision: 2339 $
 */
public class RemoveWhitespace implements LayoutFormatter {

    public String format(String fieldEntry) {

        StringBuilder sb = new StringBuilder(fieldEntry.length());

        for (char c : fieldEntry.toCharArray()) {
            if (!Character.isWhitespace(c) || Character.isSpaceChar(c)) {
                sb.append(c);
            }
        }

        return sb.toString();
    }
}