package net.sf.jabref.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;

import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import net.sf.jabref.BasePanel;
import net.sf.jabref.BibtexEntry;
import net.sf.jabref.EntryEditor;
import net.sf.jabref.FocusRequester;
import net.sf.jabref.GUIGlobals;
import net.sf.jabref.Globals;
import net.sf.jabref.PreviewPanel;
import net.sf.jabref.RightClickMenu;
import net.sf.jabref.Util;
import net.sf.jabref.external.ExternalFileMenuItem;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.event.ListEvent;
import ca.odell.glazedlists.event.ListEventListener;

/**
 * List event, mouse, key and focus listener for the main table that makes up the
 * most part of the BasePanel for a single bib database.
 */
public class MainTableSelectionListener implements ListEventListener<BibtexEntry>, MouseListener,
        KeyListener, FocusListener {

    PreviewPanel[] previewPanel = null;
    int activePreview = 1;
    PreviewPanel preview;
    MainTable table;
    BasePanel panel;
    EventList<BibtexEntry> tableRows;
    private boolean previewActive = Globals.prefs.getBoolean("previewEnabled");
    private boolean workingOnPreview = false;

    // Register the last character pressed to quick jump in the table. Together
    // with storing the last row number jumped to, this is used to let multiple
    // key strokes cycle between all entries starting with the same letter:
    private int[] lastPressed = new int[20];
    private int lastPressedCount = 0;
    private long lastPressedTime = 0;
    private long QUICK_JUMP_TIMEOUT = 2000;

    //private int lastCharPressed = -1;

    public MainTableSelectionListener(BasePanel panel, MainTable table) {
        this.table = table;
        this.panel = panel;
        this.tableRows = table.getTableRows();
        instantiatePreviews();
        this.preview = previewPanel[activePreview];
    }

    private void instantiatePreviews() {
		previewPanel = new PreviewPanel[] {
			new PreviewPanel(panel.database(), null, panel, panel.metaData(), Globals.prefs
				.get("preview0")),
			new PreviewPanel(panel.database(), null, panel, panel.metaData(), Globals.prefs
				.get("preview1")) };
		//BibtexEntry testEntry = PreviewPrefsTab.getTestEntry();
		//previewPanel[0].setEntry(testEntry);
		//previewPanel[1].setEntry(testEntry);
	}

    public void updatePreviews() {
        try {
            previewPanel[0].readLayout(Globals.prefs.get("preview0"));
            previewPanel[1].readLayout(Globals.prefs.get("preview1"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void listChanged(ListEvent<BibtexEntry> e) {
        EventList<BibtexEntry> selected = e.getSourceList();
        Object newSelected = null;
        while (e.next()) {
            if (e.getType() == ListEvent.INSERT) {
                if (newSelected != null)
                    return; // More than one new selected. Do nothing.
                else {
                    if (e.getIndex() < selected.size())
                        newSelected = selected.get(e.getIndex());
                }

            }
        }


        if (newSelected != null) {

            // Ok, we have a single new entry that has been selected. Now decide what to do with it:
            final BibtexEntry toShow = (BibtexEntry) newSelected;
            final int mode = panel.getMode(); // What is the panel already showing?
            if ((mode == BasePanel.WILL_SHOW_EDITOR) || (mode == BasePanel.SHOWING_EDITOR)) {
                // An entry is currently being edited.
                EntryEditor oldEditor = panel.getCurrentEditor();
                // Get an old or new editor for the entry to edit:
                EntryEditor newEditor = panel.getEntryEditor(toShow);
                // Show the new editor unless it was already visible:
                if ((newEditor != oldEditor) || (mode != BasePanel.SHOWING_EDITOR)) {
                    panel.showEntryEditor(newEditor);
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            table.ensureVisible(table.getSelectedRow());
                        }
                    });

                }
            } else {
                // Either nothing or a preview was shown. Update the preview.
                if (previewActive) {
                    updatePreview(toShow, false);
                }

            }
        }

    }

    private void updatePreview(final BibtexEntry toShow, final boolean changedPreview) {
        updatePreview(toShow, changedPreview, 0);
    }

    private void updatePreview(final BibtexEntry toShow, final boolean changedPreview, int repeats) {
        if (workingOnPreview) {
            if (repeats > 0)
                return; // We've already waited once. Give up on this selection.
            Timer t = new Timer(50, new ActionListener() {
                public void actionPerformed(ActionEvent actionEvent) {
                    updatePreview(toShow, changedPreview, 1);
                }
            });
            t.setRepeats(false);
            t.start();
            return;
        }
        EventList<BibtexEntry> list = table.getSelected();
        // Check if the entry to preview is still selected:
        if ((list.size() != 1) || (list.get(0) != toShow)) {
            return;
        }
        final int mode = panel.getMode();
        workingOnPreview = true;
        final Runnable update = new Runnable() {
            public void run() {
                // If nothing was already shown, set the preview and move the separator:
                if (changedPreview || (mode == BasePanel.SHOWING_NOTHING)) {
                    panel.showPreview(preview);
                    panel.adjustSplitter();
                }
                workingOnPreview = false;
            }
        };
        final Runnable worker = new Runnable() {
            public void run() {
                preview.setEntry(toShow);
                SwingUtilities.invokeLater(update);
            }
        };
        (new Thread(worker)).start();
    }

    public void editSignalled() {
        if (table.getSelected().size() == 1) {
            editSignalled(table.getSelected().get(0));
        }
    }

    public void editSignalled(BibtexEntry entry) {
        final int mode = panel.getMode();
        EntryEditor editor = panel.getEntryEditor(entry);
        if (mode != BasePanel.SHOWING_EDITOR) {
            panel.showEntryEditor(editor);
            panel.adjustSplitter();
        }
        new FocusRequester(editor);
    }

    public void mouseReleased(MouseEvent e) {
        // First find the row on which the user has clicked.
        final int row = table.rowAtPoint(e.getPoint());


        // Check if the user has right-clicked. If so, open the right-click menu.
        if (e.isPopupTrigger()) {
            final int col = table.columnAtPoint(e.getPoint());
            // Check if the user has clicked on an icon cell to open url or pdf.
            final String[] iconType = table.getIconTypeForColumn(col);
            
            if (iconType == null)
                processPopupTrigger(e, row);
            else
                showIconRightClickMenu(e, row, iconType);
        }
    }

     public void mousePressed(MouseEvent e) {
         
        // First find the column on which the user has clicked.
        final int col = table.columnAtPoint(e.getPoint()),
                row = table.rowAtPoint(e.getPoint());

        // A double click on an entry should open the entry's editor.
        if (e.getClickCount() == 2) {

            BibtexEntry toShow = tableRows.get(row);
            editSignalled(toShow);
        }

        // Check if the user has clicked on an icon cell to open url or pdf.
        final String[] iconType = table.getIconTypeForColumn(col);

        // Check if the user has right-clicked. If so, open the right-click menu.
        if (e.isPopupTrigger()) {
            if (iconType == null)
                processPopupTrigger(e, row);
            else
                showIconRightClickMenu(e, row, iconType);

            return;
        }

         // Workaround for Windows. Right-click is not popup trigger on mousePressed, but
         // on mouseReleased. Therefore we need to avoid taking action at this point, because
         // action will be taken when the button is released:
        if (Globals.ON_WIN && (iconType != null) && (e.getButton() != MouseEvent.BUTTON1))
            return;


        if (iconType != null) {

            Object value = table.getValueAt(row, col);
            if (value == null) return; // No icon here, so we do nothing.

            final BibtexEntry entry = tableRows.get(row);

            // Get the icon type. Corresponds to the field name.
            int hasField = -1;
            for (int i = iconType.length - 1; i >= 0; i--)
                if (entry.getField(iconType[i]) != null)
                    hasField = i;
            if (hasField == -1)
                return;
            final String fieldName = iconType[hasField];

            // Open it now. We do this in a thread, so the program won't freeze during the wait.
            (new Thread() {
                public void run() {
                    panel.output(Globals.lang("External viewer called") + ".");

                    Object link = entry.getField(fieldName);
                    if (link == null) {
                        Globals.logger("Error: no link to " + fieldName + ".");
                        return; // There is an icon, but the field is not set.
                    }

                    try {
                        // See if this is a simple file link field, or if it is a file-list
                        // field that can specify a list of links:
                        if (fieldName.equals(GUIGlobals.FILE_FIELD)) {

                            // We use a FileListTableModel to parse the field content:
                            FileListTableModel fileList = new FileListTableModel();
                            fileList.setContent((String)link);
                            // If there are one or more links, open the first one:
                            if (fileList.getRowCount() > 0) {
                                FileListEntry flEntry = fileList.getEntry(0);
                                ExternalFileMenuItem item = new ExternalFileMenuItem
                                        (panel.frame(), entry, "",
                                        flEntry.getLink(), flEntry.getType().getIcon(),
                                        panel.metaData(), flEntry.getType());
                                boolean success = item.openLink();
                                if (!success) {
                                    panel.output(Globals.lang("Unable to open link."));
                                }
                            }
                        } else {
                            Util.openExternalViewer(panel.metaData(), (String)link, fieldName);
                        }

                    }
                    catch (IOException ex) {
                        panel.output(Globals.lang("Error") + ": " + ex.getMessage());
                    }
                }

            }).start();
        }
    }

    /**
     * Process general right-click events on the table. Show the table context menu at
     * the position where the user right-clicked.
     * @param e The mouse event defining the popup trigger.
     * @param row The row where the event occured.
     */
    protected void processPopupTrigger(MouseEvent e, int row) {
         int selRow = table.getSelectedRow();
         if (selRow == -1 ||// (getSelectedRowCount() == 0))
                 !table.isRowSelected(table.rowAtPoint(e.getPoint()))) {
             table.setRowSelectionInterval(row, row);
             //panel.updateViewToSelected();
         }
         RightClickMenu rightClickMenu = new RightClickMenu(panel, panel.metaData());
         rightClickMenu.show(table, e.getX(), e.getY());
     }

    /**
     * Process popup trigger events occuring on an icon cell in the table. Show
     * a menu where the user can choose which external resource to open for the
     * entry. If no relevant external resources exist, let the normal popup trigger
     * handler do its thing instead.
     * @param e The mouse event defining this popup trigger.
     * @param row The row where the event occured.
     * @param iconType A string array containing the resource fields associated with
     *  this table cell.
     */
    private void showIconRightClickMenu(MouseEvent e, int row, String[] iconType) {
        BibtexEntry entry = tableRows.get(row);
        JPopupMenu menu = new JPopupMenu();
        int count = 0;

        // See if this is a simple file link field, or if it is a file-list
        // field that can specify a list of links:
        if (iconType[0].equals(GUIGlobals.FILE_FIELD)) {
            // We use a FileListTableModel to parse the field content:
            Object o = entry.getField(iconType[0]);
            FileListTableModel fileList = new FileListTableModel();
            fileList.setContent((String)o);
            // If there are one or more links, open the first one:
            for (int i=0; i<fileList.getRowCount(); i++) {
                FileListEntry flEntry = fileList.getEntry(i);
                String description = flEntry.getDescription();
                if ((description == null) || (description.trim().length() == 0))
                    description = flEntry.getLink();
                menu.add(new ExternalFileMenuItem(panel.frame(), entry, description,
                        flEntry.getLink(), flEntry.getType().getIcon(), panel.metaData(),
                        flEntry.getType()));
                count++;
            }

        }
        else {
            for (int i=0; i<iconType.length; i++) {
                Object o = entry.getField(iconType[i]);
                if (o != null) {
                    menu.add(new ExternalFileMenuItem(panel.frame(), entry, (String)o, (String)o,
                            GUIGlobals.getTableIcon(iconType[i]).getIcon(),
                            panel.metaData(), iconType[i]));
                    count++;
                }
            }
        }
        if (count == 0) {
            processPopupTrigger(e, row);
            return;
        }
        menu.show(table, e.getX(), e.getY());
    }

    public void entryEditorClosing(EntryEditor editor) {
        preview.setEntry(editor.getEntry());
        if (previewActive)
            panel.showPreview(preview);
        else
            panel.hideBottomComponent();
        panel.adjustSplitter();
        new FocusRequester(table);
    }


    public void mouseClicked(MouseEvent e) {

    }

    public void mouseEntered(MouseEvent e) {

    }

    public void mouseExited(MouseEvent e) {

    }

    public void setPreviewActive(boolean enabled) {
        previewActive = enabled;
        if (!previewActive) {
            panel.hideBottomComponent();
        } else {
            if (table.getSelected().size() > 0 ) {
                updatePreview(table.getSelected().get(0), false);
            }
        }
    }

    public void switchPreview() {
        if (activePreview < previewPanel.length - 1)
            activePreview++;
        else
            activePreview = 0;
        if (previewActive) {
            this.preview = previewPanel[activePreview];

            if (table.getSelected().size() > 0) {
                updatePreview(table.getSelected().get(0), true);
            }
        }
    }

    /**
     * Receive key event on the main table. If the key is a letter or a digit,
     * we should select the first entry in the table which starts with the given
     * letter in the column by which the table is sorted.
     * @param e The KeyEvent
     */
    public void keyTyped(KeyEvent e) {
        if ((!e.isActionKey()) && Character.isLetterOrDigit(e.getKeyChar())
	    //&& !e.isControlDown() && !e.isAltDown() && !e.isMetaDown()) {
	    && (e.getModifiers() == 0)) {
            long time = System.currentTimeMillis();
            if (time - lastPressedTime > QUICK_JUMP_TIMEOUT)
                lastPressedCount = 0; // Reset last pressed character
            // Update timestamp:
            lastPressedTime = time;
            // Add the new char to the search array:
            int c = e.getKeyChar();
            if (lastPressedCount < lastPressed.length)
                lastPressed[lastPressedCount++] = c;

            int sortingColumn = table.getSortingColumn(0);
            if (sortingColumn == -1)
                return; // No sorting? TODO: look up by author, etc.?
            // TODO: the following lookup should be done by a faster algorithm,
            // such as binary search. But the table may not be sorted properly,
            // due to marked entries, search etc., which rules out the binary search.
            int startRow = 0;
            /*if ((c == lastPressed) && (lastQuickJumpRow >= 0)) {
                if (lastQuickJumpRow < table.getRowCount()-1)
                    startRow = lastQuickJumpRow+1;
            }*/

            boolean done = false;
            while (!done) {
                for (int i=startRow; i<table.getRowCount(); i++) {
                    Object o = table.getValueAt(i, sortingColumn);
                    if (o == null)
                        continue;
                    String s = o.toString().toLowerCase();
                    if (s.length() >= lastPressedCount)
                        for (int j=0; j<lastPressedCount; j++) {
                            if (s.charAt(j) != lastPressed[j])
                                break; // Escape the loop immediately when we find a mismatch
                            else if (j == lastPressedCount-1) {
                                // We found a match:
                                table.setRowSelectionInterval(i, i);
                                table.ensureVisible(i);
                                return;
                            }
                        }
                    //if ((s.length() >= 1) && (s.charAt(0) == c)) {
                    //}
                }
                // Finished, no result. If we didn't start at the beginning of
                // the table, try that. Otherwise, exit the while loop.
                if (startRow > 0)
                    startRow = 0;
                else
                    done = true;

            }
            
        } else if (e.getKeyChar() == KeyEvent.VK_ESCAPE) {
            lastPressedCount = 0;

        }
    }

    public void keyReleased(KeyEvent e) {
    }

    public void keyPressed(KeyEvent e) {
    }

    public void focusGained(FocusEvent e) {

    }

    public void focusLost(FocusEvent e) {
        lastPressedCount = 0; // Reset quick jump when focus is lost.
    }
}
