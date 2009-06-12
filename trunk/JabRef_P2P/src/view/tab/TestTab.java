package view.tab;

import core.SidePanel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JTextField;
import view.EntriesPanel;
import view.NewUserWizard;

/**
 * Use for debugging inside JabRef.
 * @author Thien Rong
 */
public class TestTab extends ITab {

    public TestTab(final SidePanel main) {
        super(main);
        final JTextField txt = new JTextField(20);
        txt.addActionListener(new ActionListener() {

            EntriesPanel ep;

            public void actionPerformed(ActionEvent e) {
                /*                if (ep == null) {
                try {
                JFrame f = FrameCreator.createTestFrame();
                ep = new EntriesPanel(main, null, "");
                BibtexEntry e1 = new BibtexEntry();
                e1.setId("test1");
                BibtexEntry e2 = new BibtexEntry();
                e1.setId("test2");

                ep.setEntries(Arrays.asList(e1, e2));
                f.add(ep);
                f.setSize(500, 500);
                f.setVisible(true);
                } catch (Exception ex) {
                ex.printStackTrace();
                }
                } else {

                ep.getSelectedEntries();
                }

                 */
                NewUserWizard u = new NewUserWizard(main);
                u.setVisible(true);
            }
        });
        this.add(txt);
    }
}
