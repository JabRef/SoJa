/*
 All programs in this directory and subdirectories are published under the 
 GNU General Public License as described below.

 This program is free software; you can redistribute it and/or modify it 
 under the terms of the GNU General Public License as published by the Free 
 Software Foundation; either version 2 of the License, or (at your option) 
 any later version.

 This program is distributed in the hope that it will be useful, but WITHOUT 
 ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or 
 FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for 
 more details.

 You should have received a copy of the GNU General Public License along 
 with this program; if not, write to the Free Software Foundation, Inc., 59 
 Temple Place, Suite 330, Boston, MA 02111-1307 USA

 Further information about the GNU GPL is available at:
 http://www.gnu.org/copyleft/gpl.ja.html
 */

package net.sf.jabref.groups;

import java.awt.Color;
import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

import net.sf.jabref.GUIGlobals;
import net.sf.jabref.Globals;
import net.sf.jabref.Util;

/**
 * Renders a GroupTreeNode using its group's getName() method, rather that its
 * toString() method.
 * 
 * @author jzieren
 */
public class GroupTreeCellRenderer extends DefaultTreeCellRenderer {
    /** The cell over which the user is currently dragging */
    protected Object highlight1Cell = null;
    protected Object[] highlight2Cells = null;
    protected Object[] highlight3Cells = null;
    protected Object highlightBorderCell = null;

    public static ImageIcon
      groupRefiningIcon = GUIGlobals.getImage("groupRefining"),
      groupIncludingIcon = GUIGlobals.getImage("groupIncluding"),
      groupRegularIcon = null;


    public Component getTreeCellRendererComponent(JTree tree, Object value,
            boolean selected, boolean expanded, boolean leaf, int row,
            boolean hasFocus) {
        if (value == highlight1Cell)
            selected = true; // show as selected
        Component c = super.getTreeCellRendererComponent(tree, value, selected,
                expanded, leaf, row, hasFocus);
        // this is sometimes called from deep within somewhere, with a dummy 
        // value (probably for layout etc.), so we've got to check here!
        if (!(value instanceof GroupTreeNode))
            return c;
        AbstractGroup group = ((GroupTreeNode) value).getGroup();
        if (group == null || !(c instanceof JLabel))
            return c; // sanity check
        JLabel label = (JLabel) c;
        if (highlightBorderCell != null && highlightBorderCell == value)
            label.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        else
            label.setBorder(BorderFactory.createEmptyBorder());
        boolean italics = Globals.prefs.getBoolean("groupShowDynamic")
        && group.isDynamic();
        boolean red = false;
        if (highlight2Cells != null) {
            for (int i = 0; i < highlight2Cells.length; ++i) {
                if (highlight2Cells[i] == value) {
                    // label.setForeground(Color.RED);
                    red = true;
                    break;
                }
            }
        }
        boolean underline = false;
        if (highlight3Cells != null) {
            for (int i = 0; i < highlight3Cells.length; ++i) {
                if (highlight3Cells[i] == value) {
                    underline = true;
                    break;
                }
            }
        }
        StringBuffer sb = new StringBuffer();
        sb.append("<html>");
        if (red)
            sb.append("<font color=\"#FF0000\">");
        if (underline)
            sb.append("<u>");
        if (italics)
            sb.append("<i>");
        sb.append(Util.quoteForHTML(group.getName()));
        if (italics)
            sb.append("</i>");
        if (underline)
            sb.append("</u>");
        if (red)
            sb.append("</font>");
        sb.append("</html>");
        final String text = sb.toString();
        if (!label.getText().equals(text))
            label.setText(text);
        label.setToolTipText("<html>" + group.getShortDescription() + "</html>");
        if (Globals.prefs.getBoolean("groupShowIcons")) {
            switch (group.getHierarchicalContext()) {
            case AbstractGroup.REFINING:
                if (label.getIcon() != groupRefiningIcon)
                    label.setIcon(groupRefiningIcon);
                break;
            case AbstractGroup.INCLUDING:
                if (label.getIcon() != groupIncludingIcon)
                    label.setIcon(groupIncludingIcon);
                break;
            default:
                if (label.getIcon() != groupRegularIcon)
                    label.setIcon(groupRegularIcon);
                break;
            }
        } else {
            label.setIcon(null);
        }
        return c;
    }

    /**
     * For use when dragging: The sepcified cell is always rendered as selected.
     * 
     * @param cell
     *            The cell over which the user is currently dragging.
     */
    void setHighlight1Cell(Object cell) {
        this.highlight1Cell = cell;
    }

    /**
     * Highlights the specified cells (in red), or disables highlight if cells ==
     * null.
     */
    void setHighlight2Cells(Object[] cells) {
        this.highlight2Cells = cells;
    }

    /**
     * Highlights the specified cells (by unterlining), or disables highlight if
     * cells == null.
     */
    void setHighlight3Cells(Object[] cells) {
        this.highlight3Cells = cells;
    }

    /**
     * Highlights the specified cells (by drawing a border around it), 
     * or disables highlight if highlightBorderCell == null.
     */
    void setHighlightBorderCell(Object highlightBorderCell) {
        this.highlightBorderCell = highlightBorderCell;
    }
}
