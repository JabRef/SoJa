package net.sf.jabref.util;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Collection;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.sf.jabref.*;
import net.sf.jabref.undo.NamedCompound;

import com.jgoodies.forms.builder.ButtonBarBuilder;
import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;

/**
 * An Action for launching mass field.
 *
 * Functionality:
 * * Defaults to selected entries, or all entries if none are selected.
 * * Input field name
 * * Either set field, or clear field.
 */
public class MassSetFieldAction extends MnemonicAwareAction {
    private JabRefFrame frame;
    private JDialog diag;
    private JRadioButton all, selected, clear, set;
    private JTextField field, text;
    private JButton ok, cancel;
    boolean cancelled = true;
    private JCheckBox overwrite;

    public MassSetFieldAction(JabRefFrame frame) {
        putValue(NAME, "Set/clear fields");
        this.frame = frame;
    }

    private void createDialog() {
        diag = new JDialog(frame, Globals.lang("Set/clear fields"), true);

        field = new JTextField();
        text = new JTextField();
        ok = new JButton(Globals.lang("Ok"));
        cancel = new JButton(Globals.lang("Cancel"));

        all = new JRadioButton(Globals.lang("All entries"));
        selected = new JRadioButton(Globals.lang("Selected entries"));
        clear = new JRadioButton(Globals.lang("Clear fields"));
        set = new JRadioButton(Globals.lang("Set fields"));
        set.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                // Entering a text is only relevant if we are setting, not clearing:
                text.setEnabled(set.isSelected());
                // Overwrite protection makes no sense if we are clearing the field:
                overwrite.setEnabled(set.isSelected());
            }
        });

        overwrite = new JCheckBox(Globals.lang("Overwrite existing field values"), true);
        ButtonGroup bg = new ButtonGroup();
        bg.add(all);
        bg.add(selected);
        bg = new ButtonGroup();
        bg.add(clear);
        bg.add(set);
        DefaultFormBuilder builder = new DefaultFormBuilder(new FormLayout(
                "left:pref, 4dlu, fill:100dlu", ""));
        builder.appendSeparator(Globals.lang("Field name"));
        builder.append(Globals.lang("Field name"));
        builder.append(field);
        builder.nextLine();
        builder.appendSeparator(Globals.lang("Include entries"));
        builder.append(all, 3);
        builder.nextLine();
        builder.append(selected, 3);
        builder.nextLine();
        builder.appendSeparator(Globals.lang("New field value"));
        builder.append(set);
        builder.append(text);
        builder.nextLine();
        builder.append(clear);
        builder.nextLine();
        builder.append(overwrite, 3);


        ButtonBarBuilder bb = new ButtonBarBuilder();
        bb.addGlue();
        bb.addGridded(ok);
        bb.addGridded(cancel);
        bb.addGlue();
        builder.getPanel().setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
        bb.getPanel().setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
        diag.getContentPane().add(builder.getPanel(), BorderLayout.CENTER);
        diag.getContentPane().add(bb.getPanel(), BorderLayout.SOUTH);
        diag.pack();

        ok.addActionListener(new ActionListener() {
           public void actionPerformed(ActionEvent e) {
                cancelled = false;
                diag.dispose();
            }
        });

        AbstractAction cancelAction = new AbstractAction() {
                public void actionPerformed(ActionEvent e) {
                    cancelled = true;
                    diag.dispose();
                }
            };
        cancel.addActionListener(cancelAction);

        // Key bindings:
        ActionMap am = builder.getPanel().getActionMap();
        InputMap im = builder.getPanel().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        im.put(Globals.prefs.getKey("Close dialog"), "close");
        am.put("close", cancelAction);
    }

    private void prepareDialog(boolean selection) {
        selected.setEnabled(selection);
        if (selection)
            selected.setSelected(true);
        else
            all.setSelected(true);
        // Make sure one of the following ones is selected:
        if (!set.isSelected() && !clear.isSelected())
            set.setSelected(true);
    }

    public void actionPerformed(ActionEvent e) {
        BasePanel bp = frame.basePanel();
        if (bp == null)
            return;
        BibtexEntry[] entries = bp.getSelectedEntries();
        // Lazy creation of the dialog:
        if (diag == null)
            createDialog();
        cancelled = true;
        prepareDialog(entries.length > 0);
        Util.placeDialog(diag, frame);
        diag.setVisible(true);
        if (cancelled)
            return;

        Collection<BibtexEntry> entryList;
        // If all entries should be treated, change the entries array:
        if (all.isSelected())
            entryList = bp.database().getEntries();
        else
            entryList = Arrays.asList(entries);
        String toSet = text.getText();
        if (toSet.length() == 0)
            toSet = null;
        String[] fields = getFieldNames(field.getText().trim().toLowerCase());
        NamedCompound ce = new NamedCompound(Globals.lang("Set field"));
        for (int i = 0; i < fields.length; i++) {
            ce.addEdit(Util.massSetField(entryList, fields[i],
                            set.isSelected() ? toSet : null,
                            overwrite.isSelected()));

        }
        ce.end();
        bp.undoManager.addEdit(ce);
        bp.markBaseChanged();
    }

    private String[] getFieldNames(String s) {
        return s.split("[^a-z]");
    }
}
