/*
 Copyright (C) 2003 Morten O. Alver, Nizar N. Batada

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

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

/**
 * 
 * This is a non-modal help Dialog. The contents of the help is specified by
 * calling showPage().
 * 
 * @version $Revision: 2209 $ ($Date: 2007-08-01 20:23:38 +0200 (Wed, 01 Aug 2007) $)
 * 
 */
public class HelpDialog extends JDialog implements HyperlinkListener {

	private JabRefFrame frame;

	private HelpContent content;

	private BackAction back = new BackAction();

	private ForwardAction forward = new ForwardAction();

	private ContentsAction contents = new ContentsAction();

	// Initializes, but does not show the help dialog.
	public HelpDialog(JabRefFrame bf) {
		super(bf, Globals.lang("JabRef help"), false);
		frame = bf;
		content = new HelpContent(bf.prefs);
		content.addHyperlinkListener(this);
		setSize(GUIGlobals.helpSize);

		JToolBar tlb = new JToolBar();
		tlb.add(back);
		tlb.add(forward);
		tlb.addSeparator();
		tlb.add(contents);
		tlb.setFloatable(false);

		// Make ESC close dialog, and set shortkeys for back and forward.
		InputMap im = tlb.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		ActionMap am = tlb.getActionMap();
		im.put(bf.prefs.getKey("Close dialog"), "close");
		am.put("close", new CloseAction());
		im.put(bf.prefs.getKey("Back, help dialog"), "left");
		am.put("left", back);
		im.put(bf.prefs.getKey("Forward, help dialog"), "right");
		am.put("right", forward);

		// Set shortkeys for back and forward specifically for the EditorPane.
		im = content.getInputMap(JComponent.WHEN_FOCUSED);
		am = content.getActionMap();
		im.put(bf.prefs.getKey("Back, help dialog"), "left");
		am.put("left", back);
		im.put(bf.prefs.getKey("Forward, help dialog"), "right");
		am.put("right", forward);

		getContentPane().add(tlb, BorderLayout.NORTH);
		getContentPane().add(content.getPane());
		forward.setEnabled(false);
		back.setEnabled(false);
	}

	public void showPage(String url) {
		if (!isVisible()) {
			Util.placeDialog(this, frame);
			content.reset();
			back.setEnabled(false);
			setVisible(true);
		} else {
			back.setEnabled(true);
		}
		forward.setEnabled(false);
		content.setPage(url);
		content.requestFocus();
	}

	public void hyperlinkUpdate(HyperlinkEvent e) {
		if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
			content.setPage(e.getURL());
			back.setEnabled(true);
			forward.setEnabled(false);
		}
	}

	class CloseAction extends AbstractAction {
		public CloseAction() {
			super(Globals.lang("Close"));
			// , new ImageIcon(GUIGlobals.closeIconFile));
			putValue(SHORT_DESCRIPTION, Globals.lang("Close the help window"));
		}

		public void actionPerformed(ActionEvent e) {
			dispose();
		}
	}

	class BackAction extends AbstractAction {
		public BackAction() {
			super("Back", GUIGlobals.getImage("left"));
			// putValue(SHORT_DESCRIPTION, "Show the previous page");
		}

		public void actionPerformed(ActionEvent e) {
			setEnabled(content.back());
			forward.setEnabled(true);
		}
	}

	class ForwardAction extends AbstractAction {
		public ForwardAction() {
			super("Forward", GUIGlobals.getImage("right"));
		}

		public void actionPerformed(ActionEvent e) {
			setEnabled(content.forward());
			back.setEnabled(true);
		}
	}

	class ContentsAction extends AbstractAction {
		public ContentsAction() {
			super("Contents", GUIGlobals.getImage("helpContents"));
		}

		public void actionPerformed(ActionEvent e) {
			content.setPage(GUIGlobals.helpContents);
			back.setEnabled(true);
		}
	}
}
