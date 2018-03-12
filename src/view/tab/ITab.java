package view.tab;

import core.SidePanel;
import javax.swing.JPanel;

/**
 *
 * @author Thien Rong
 */
public abstract class ITab extends JPanel {

    SidePanel main;

    public ITab(SidePanel main) {
        this.main = main;
    }
}
