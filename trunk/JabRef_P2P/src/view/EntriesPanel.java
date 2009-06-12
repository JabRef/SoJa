package view;

import core.SidePanel;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import model.friend.Friend;
import net.sf.jabref.BasePanel;
import net.sf.jabref.BibtexEntry;
import net.sf.jabref.BibtexFields;
import net.sf.jabref.JabRefFrame;
import net.sf.jabref.gui.ImportInspectionDialog;
import util.Loader;

/**
 * Uses the importInspection but removing the progressbar, buttons
 * @author Thien Rong
 */
public class EntriesPanel extends JPanel implements ImageConstants {

    ImportEntriesPanel importer;

    public EntriesPanel(final SidePanel main, final Friend friend, String undoName) {
        super(new BorderLayout());
        importer = new ImportEntriesPanel(main.getFrame(), main.getFrame().basePanel(),
                BibtexFields.DEFAULT_INSPECTION_FIELDS, undoName, false);

        this.add(extractView(importer));

        JPanel pnlCtrl = new JPanel();
        JButton btnAllow = new JButton("Add a copy into my database");
        btnAllow.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                for (BibtexEntry bibtexEntry : importer.getSelectedEntries()) {
                    main.handleAddBibtexCopy(friend, bibtexEntry);
                }
            }
        });
        pnlCtrl.add(btnAllow);

        JButton btnSubscribe = new JButton("Subscribe And Keep Me Updated", new ImageIcon(Loader.get(RSS)));
        btnSubscribe.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                for (BibtexEntry bibtexEntry : importer.getSelectedEntries()) {
                    main.handleSubscribe(friend, bibtexEntry);
                }

            }
        });
        pnlCtrl.add(btnSubscribe);

        this.add(pnlCtrl, BorderLayout.SOUTH);
    }

    private Component extractView(ImportEntriesPanel importer) {
        return importer.getMain();
    }

    public void setEntries(Collection<BibtexEntry> entries) {
        importer.removeAllEntries();
        importer.addEntries(entries);
    }

    public List<BibtexEntry> getSelectedEntries() {
        return importer.getSelectedEntries();
    }

    class ImportEntriesPanel extends ImportInspectionDialog {

        public ImportEntriesPanel(JabRefFrame frame, BasePanel panel, String[] fields, String undoName, boolean newDatabase) {
            super(frame, panel, fields, undoName, newDatabase);
            glTable.getActionMap().remove("delete");

            TableColumnModel cm = glTable.getTableHeader().getColumnModel();
            TableColumn keepCol = cm.getColumn(0); // hide keep
            keepCol.setMinWidth(0);
            keepCol.setMaxWidth(0);
            TableColumn fileCol = cm.getColumn(FILE_COL); // hide file col
            fileCol.setMinWidth(0);
            fileCol.setMaxWidth(0);
        }

        public Component getMain() {
            return contentPane;
        }

        /**
         * Select all first then use removeSelected         
         */
        public void removeAllEntries() {
            int count = glTable.getRowCount();
            if (count > 0) {
                selectionModel.setSelectionInterval(0, count - 1);
            }
            this.removeSelectedEntries();

            this.contentPane.setDividerLocation(0.5);
        }

        /**
         * Find Selected instead of Checked
         * @return
         */
        @Override
        public List<BibtexEntry> getSelectedEntries() {
            return selectionModel.getSelected();
        }
    }

    /*
    class EntriesTable extends JTable {

    protected JLabel // images label
    duplLabel = new JLabel(GUIGlobals.getImage("duplicate"))//
    ,  urlLabel = new JLabel(GUIGlobals.getImage("wwwSmall"));
    GeneralRenderer renderer = new GeneralRenderer(Color.white);
    private String[] fields;
    private PreviewPanel preview;
    // 2 extra fields
    int EXTRA = 2;

    EntriesTable(TableModel model, String[] fields) {
    super(model);

    this.fields = fields;
    preview = new PreviewPanel(null, null, Globals.prefs.get("preview1"));
    duplLabel.setToolTipText(Globals.lang("Possible duplicate of existing entry. Click to resolve."));
    }

    public TableCellRenderer getCellRenderer(int row, int column) {
    return renderer;
    }

    public boolean isCellEditable(int row, int column) {
    return false;
    }

    /**
     * 0 is duplicate and 1 is the url
     * @param col
     * @return
     *
    public Class<?> getColumnClass(int col) {
    if (col < EXTRA) {
    return JLabel.class;
    } else {
    return String.class;
    }
    }

    class EntryTableFormat implements TableFormat<BibtexEntry> {

    public int getColumnCount() {
    return EXTRA + fields.length;
    }

    public String getColumnName(int i) {
    if (i >= EXTRA) {
    return Util.nCase(fields[i - EXTRA]);
    }
    return "";
    }

    public Object getColumnValue(BibtexEntry entry, int i) {
    if (i < EXTRA) {
    Object o;
    switch (i) {
    case 0:
    return entry.isGroupHit() ? duplLabel : null;
    case 1:
    o = entry.getField("url");
    if (o != null) {
    urlLabel.setToolTipText((String) o);
    return urlLabel;
    } else {
    return null;
    }
    default:
    return null;
    }

    } else {
    String field = fields[i - EXTRA];
    if (field.equals("author") || field.equals("editor")) {
    String contents = entry.getField(field);
    return (contents != null) ? AuthorList.fixAuthor_Natbib(contents) : "";
    } else {
    return entry.getField(field);
    }
    }
    }
    }
    }
    protected void setupComparatorChooser() {
    // First column:
    java.util.List<Comparator<BibtexEntry>> comparators = comparatorChooser
    .getComparatorsForColumn(0);
    comparators.clear();
    
    comparators = comparatorChooser.getComparatorsForColumn(1);
    comparators.clear();
    
    // Icon columns:
    for (int i = 2; i < EXTRA; i++) {
    comparators = comparatorChooser.getComparatorsForColumn(i);
    comparators.clear();
    if (i == FILE_COL)
    comparators.add(new IconComparator(new String[] { GUIGlobals.FILE_FIELD }));
    else if (i == PDF_COL)
    comparators.add(new IconComparator(new String[] { "pdf" }));
    else if (i == PS_COL)
    comparators.add(new IconComparator(new String[] { "ps" }));
    else if (i == URL_COL)
    comparators.add(new IconComparator(new String[] { "url" }));
    
    }
    // Remaining columns:
    for (int i = EXTRA; i < EXTRA + fields.length; i++) {
    comparators = comparatorChooser.getComparatorsForColumn(i);
    comparators.clear();
    comparators.add(new FieldComparator(fields[i - PAD]));
    }
    
    sortedList.getReadWriteLock().writeLock().lock();
    comparatorChooser.appendComparator(PAD, 0, false);
    sortedList.getReadWriteLock().writeLock().unlock();
    
    }
     */
}