package view;

import antlr.collections.AST;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.SortedSet;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;
import model.TagFreq;
import core.SidePanel;
import net.sf.jabref.search.SearchExpressionParser;

/**
 *
 * @author Thien Rong
 */
public class TagCloudPanel extends ScrollableFlowPanel {

    public static void main(String[] args) {
        if (false) {
            AST a = SearchExpressionParser.checkSyntax(
                    "keywords=hello",
                    false,
                    false);
        }
        if (true) {
            JFrame f = new JFrame();
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            JTabbedPane tabbedPane = new JTabbedPane();

            //TagCloudPanel tp = new TagCloudPanel(new ActionHandler.TestActionHandler(), "Test");
            TagCloudPanel tp = new TagCloudPanel(null, 300);
            for (int i = 0; i < 100; i++) {
                int freq = (int) (Math.random() * 10);
                tp.addTag("test" + i + "(" + freq + ")", freq);
                System.out.println("i=" + i);
            }
            //JScrollPane sp = new JScrollPane(tp, JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            JScrollPane sp = new JScrollPane(tp, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            /*sp.getViewport().setViewSize(new Dimension(300, 300));
            sp.setMaximumSize(new Dimension(300,300));
            tp.setSize(500,500);
            sp.setSize(300, 300);*/
            JPanel pp = new JPanel();
            //pp.setSize(300, 300);
            pp.add(new JButton("A"));
            pp.setLayout(new BoxLayout(pp, BoxLayout.PAGE_AXIS));
            pp.add(sp);
            tabbedPane.add(pp);
            f.add(tabbedPane);
            f.setSize(300, 300);
            System.out.println(sp);
            f.setVisible(true);
        }
    }
    private SidePanel main;

    public TagCloudPanel(SidePanel main, int maxWidth) {
        super(new FlowLayout(FlowLayout.LEFT), maxWidth);
        this.main = main;
    }

    public void setTags(SortedSet<TagFreq> tagsToShow) {
        this.removeAll();
        for (TagFreq tagFreq : tagsToShow) {
            addTag(tagFreq.getText(), tagFreq.getFreq());
        }
        this.updateUI();
    }

    private void addTag(final String keyword, int freq) {
        LinkButton tagBtn = new LinkButton(keyword, freq);
        tagBtn.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                main.handleViewTag(keyword);
            }
        });
        this.add(tagBtn);
    }
}

/**
 * Taken from
 * Force the width
 * http://forums.sun.com/thread.jspa?forumID=57&threadID=701797&start=2
 */
class ScrollableFlowPanel extends JPanel implements Scrollable {

    int maxWidth;

    public ScrollableFlowPanel(LayoutManager layout, int maxWidth) {
        super(layout);
        this.maxWidth = maxWidth;
    }

    public void setBounds(int x, int y, int width, int height) {
        if (getParent() != null) {
            //System.out.println(getParent() + " width of parent = " + width);
            width = getParent().getWidth();
        }

        System.out.println("width= " + width);
        if (width > maxWidth) {
            width = maxWidth;
        }

        super.setBounds(x, y, width, height);
    }

    public Dimension getPreferredSize() {
        return new Dimension(getWidth(), getPreferredHeight());
    }

    public Dimension getPreferredScrollableViewportSize() {
        return super.getPreferredSize();
    }

    public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
        int hundredth = (orientation == SwingConstants.VERTICAL
                ? getParent().getHeight() : getParent().getWidth()) / 100;
        return (hundredth == 0 ? 1 : hundredth);
    }

    public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
        return orientation == SwingConstants.VERTICAL ? getParent().getHeight() : getParent().getWidth();
    }

    public boolean getScrollableTracksViewportWidth() {
        return true;
    }

    public boolean getScrollableTracksViewportHeight() {
        return false;
    }

    private int getPreferredHeight() {
        int rv = 0;
        for (int k = 0, count = getComponentCount(); k < count; k++) {
            Component comp = getComponent(k);
            Rectangle r = comp.getBounds();
            int height = r.y + r.height;
            if (height > rv) {
                rv = height;
            }
        }
        rv += ((FlowLayout) getLayout()).getVgap();
        return rv;
    }
}
