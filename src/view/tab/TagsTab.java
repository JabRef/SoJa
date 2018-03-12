package view.tab;

import core.SidePanel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import net.sf.jabref.BibtexEntry;
import util.FrameUtil;
import util.visitor.TagFreqVisitor;
import view.SplitPanel;
import view.TagCloudWithChoice;

/**
 *
 * @author Thien Rong
 */
public class TagsTab extends ITab {

    public TagsTab(final SidePanel main) {
        super(main);
        this.setLayout(new BorderLayout());

        final TagCloudWithChoice tp = new TagCloudWithChoice(main, "Explore");

        JButton btnUpdateTagCloud = new JButton("Update Mine Tag Cloud");
        btnUpdateTagCloud.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                TagFreqVisitor tfVisitor = main.getLocalTagFreqVisitor();
                main.setMyTags(tfVisitor.getTagFreq());
                List<BibtexEntry> missingTagEntries = tfVisitor.getMissingTagEntries();

                for (BibtexEntry bibtexEntry : missingTagEntries) {
                    System.out.println("Missing tag: " + bibtexEntry);
                }

                System.out.println(tp.getSize() + "/" + tp.getPreferredSize() + "/" + tp.getMinimumSize() + "/" + tp.getMaximumSize());
                main.getManager().updateView();
            }
        });

        JButton btnAutoInsertTags = new JButton("Auto-insert Tags");
        final String autoInsertTips = "This will automatically add the following to your entries keywords \n" +
                "1) authors(eg. E. G. Domingues), \n" +
                "2) journal if any (eg. Aerospace and Electronic Systems Magazine), \n" +
                "3) year if any (eg. 2009)";
        btnAutoInsertTags.setToolTipText(autoInsertTips);
        btnAutoInsertTags.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                int choice = JOptionPane.showConfirmDialog(main.getFrame(),
                        autoInsertTips, "Auto-insert Tags", JOptionPane.OK_CANCEL_OPTION);
                if (choice == JOptionPane.OK_OPTION) {
                    FrameUtil.autoInsertTag(main.getFrame());
                }
            }
        });

        //JScrollPane pane = new JScrollPane(btnUpdateTagCloud);
        //pane.getViewport().setPreferredSize(new Dimension(300, 300));
        SplitPanel pnlTags = new SplitPanel(tp, btnUpdateTagCloud, BorderLayout.SOUTH);        
        this.add(btnAutoInsertTags, BorderLayout.SOUTH);
        this.add(pnlTags);

    }
}