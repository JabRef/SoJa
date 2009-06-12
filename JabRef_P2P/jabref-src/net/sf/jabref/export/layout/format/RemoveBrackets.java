///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile$
//  Purpose:  Atom representation.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg K. Wegner
//  Version:  $Revision: 151 $
//            $Date: 2004-01-31 20:32:33 +0100 (Sat, 31 Jan 2004) $
//            $Author: mortenalver $
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
 * Remove brackets formatter.
 *
 * @author $author$
 * @version $Revision: 151 $
 */
public class RemoveBrackets implements LayoutFormatter
{
    //~ Methods ////////////////////////////////////////////////////////////////

    public String format(String fieldText)
    {
        String fieldEntry = fieldText;
        StringBuffer sb = new StringBuffer(fieldEntry.length());

        for (int i = 0; i < fieldEntry.length(); i++)
        {
            //System.out.print(fieldEntry.charAt(i));
            if ((fieldEntry.charAt(i) != '{') && (fieldEntry.charAt(i) != '}'))
            {
                //System.out.print(fieldEntry.charAt(i));
                sb.append(fieldEntry.charAt(i));
            }
        }

        fieldEntry = sb.toString();
        return fieldEntry;
    }
}
///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
