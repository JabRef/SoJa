///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile$
//  Purpose:  Atom representation.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg K. Wegner
//  Version:  $Revision: 806 $
//            $Date: 2005-03-06 22:02:25 +0100 (Sun, 06 Mar 2005) $
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
 * Change type of record to match the one used by OpenOffice formatter.
 * 
 * Based on the RemoveBrackets.java class (Revision 1.2) by mortenalver
 * @author $author$
 * @version $Revision: 806 $
 */
public class GetOpenOfficeType implements LayoutFormatter
{
    //~ Methods ////////////////////////////////////////////////////////////////

    public String format(String fieldText)
    {
        String fieldEntry = fieldText;
		if (fieldEntry.equalsIgnoreCase("Article")) return "7";
		if (fieldEntry.equalsIgnoreCase("Book")) return "1";
		if (fieldEntry.equalsIgnoreCase("Booklet")) return "2";
		if (fieldEntry.equalsIgnoreCase("Inbook")) return "5";
		if (fieldEntry.equalsIgnoreCase("Incollection")) return "5";
		if (fieldEntry.equalsIgnoreCase("Inproceedings")) return "6";
		if (fieldEntry.equalsIgnoreCase("Manual")) return "8";
		if (fieldEntry.equalsIgnoreCase("Mastersthesis")) return "9";
		if (fieldEntry.equalsIgnoreCase("Misc")) return "10";
		if (fieldEntry.equalsIgnoreCase("Other")) return "10";
		if (fieldEntry.equalsIgnoreCase("Phdthesis")) return "9";
		if (fieldEntry.equalsIgnoreCase("Proceedings")) return "3";
		if (fieldEntry.equalsIgnoreCase("Techreport")) return "13";
		if (fieldEntry.equalsIgnoreCase("Unpublished")) return "14";
	// Default, Miscelaneous
		return "10";
    }
}
///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
