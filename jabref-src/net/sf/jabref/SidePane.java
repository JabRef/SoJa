/*
 Copyright (C) 2003  Nizar N. Batada, Morten O. Alver

 All programs in this directory and
 subdirectories are published under the GNU General Public License as
 described below.

 This program is free software; you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation; either version 2 of the License, or (at
 your option) any later version.

 This program is distributed in the hope that it will be useful, but
 WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 USA

 Further information about the GNU GPL is available at:
 http://www.gnu.org/copyleft/gpl.ja.html

 */
package net.sf.jabref;

import java.awt.*;
import java.util.Collection;

import javax.swing.Box;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 * The side pane is displayed at the right side of JabRef and shows instances of
 * SidePaneComponents, for instance the GroupSelector, or the SearchManager2.
 * 
 * @version $Revision: 2209 $ ($Date: 2007-08-01 20:23:38 +0200 (Wed, 01 Aug 2007) $)
 * 
 */
public class SidePane extends JPanel {

	final Dimension PREFERRED_SIZE = new Dimension(GUIGlobals.SPLIT_PANE_DIVIDER_LOCATION, 100);

	GridBagLayout gridBagLayout = new GridBagLayout();

	GridBagConstraints constraint = new GridBagConstraints();

	JPanel mainPanel = new JPanel();

	public SidePane() {

		// For debugging the border:
		// setBorder(BorderFactory.createLineBorder(Color.BLUE));

		setLayout(new BorderLayout());
		mainPanel.setLayout(gridBagLayout);

		// Initialize constraint
		constraint.anchor = GridBagConstraints.NORTH;
		constraint.fill = GridBagConstraints.BOTH;
		constraint.gridwidth = GridBagConstraints.REMAINDER;
		constraint.insets = new Insets(1, 1, 1, 1);
		constraint.gridheight = 1;
		constraint.weightx = 1;

		/*
		 * Added Scrollpane to fix: 
		 */
		JScrollPane sp = new JScrollPane(mainPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
			JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		sp.setBorder(null);
		
		// To remove the scroll panel just change sp to mainPanel and comment
		// the JScrollPane declaration
		super.add(sp);
	}

	public void setComponents(Collection<SidePaneComponent> comps) {
		mainPanel.removeAll();

		constraint.weighty = 0;
		for (Component c : comps){
			gridBagLayout.setConstraints(c, constraint);
			mainPanel.add(c);
		}
		constraint.weighty = 1;
		Component bx = Box.createVerticalGlue();
		gridBagLayout.setConstraints(bx, constraint);
		mainPanel.add(bx);

		revalidate();
		repaint();
	}

	public void remove(Component c) {
		mainPanel.remove(c);
	}

	public Dimension getMaximumSize() {
		return getPreferredSize();
	}

	public Dimension getPreferredSize() {
		return PREFERRED_SIZE;
	}
}
