///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile$
//  Purpose:  Atom representation.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg K. Wegner
//  Version:  $Revision: 2268 $
//            $Date: 2007-08-20 01:37:05 +0200 (Mon, 20 Aug 2007) $
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



/**
 * Create DocBook editors formatter.
 *
 * @author $author$
 * @version $Revision: 2268 $
 */
public class CreateDocBookEditors extends CreateDocBookAuthors
{
    //~ Methods ////////////////////////////////////////////////////////////////

    public String format(String fieldText)
    {
        //		<editor><firstname>L.</firstname><surname>Xue</surname></editor>

        StringBuffer sb = new StringBuffer(100);
        //fieldText = (new ConvertSpecialCharactersForXML()).format(fieldText);

        if (fieldText.indexOf(" and ") == -1)
        {
          sb.append("<editor>");
          singleAuthor(sb, fieldText);
          sb.append("</editor>");
        }
        else
        {
            String[] names = fieldText.split(" and ");
            for (int i=0; i<names.length; i++)
            {
              sb.append("<editor>");
              singleAuthor(sb, names[i]);
              sb.append("</editor>");
              if (i < names.length -1)
                sb.append("\n       ");
            }
        }

        fieldText = sb.toString();

        return fieldText;
    }

}
///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
