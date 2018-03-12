package view;

import java.awt.BorderLayout;
import java.awt.Component;
import javax.swing.JPanel;

/**
 * BorderLayout to be like SplitPane
 * @author Thien Rong
 */
public class SplitPanel extends JPanel {

    public SplitPanel(Component mainComp, Component subComp, String constraint) {
        super(new BorderLayout());
        this.add(mainComp);
        this.add(subComp, constraint);
    }
}
