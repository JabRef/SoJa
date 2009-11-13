package view;

import antlr.collections.AST;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.SortedSet;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import model.TagFreq;
import core.SidePanel;
import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.TreeSet;
import model.TagCloudModel;
import net.sf.jabref.search.SearchExpressionParser;

/**
 * Uses tag cloud panel with dropdown
 * @author Thien Rong
 */
public class TagCloudWithChoice extends JPanel {

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
            TagCloudWithChoice tp = new TagCloudWithChoice(null, "Test");
            SortedSet<TagFreq> tfs = new TreeSet<TagFreq>(TagFreq.COMP_BY_TEXT);
            for (int i = 0; i < 100; i++) {
                int freq = (int) (Math.random() * 10);
                tfs.add(new TagFreq("test" + i + "(" + freq + ")", freq));
                System.out.println("i=" + i);
            }

            JScrollPane sp = new JScrollPane(tp, JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

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
            f.setVisible(true);
        }
    }
    private SidePanel main;
    private boolean showMine = true;
    //private TagCloudPanel cloudPanel;
    private TagListPanel listPanel;

    public TagCloudWithChoice(SidePanel main, String title) {
        super(new BorderLayout());
        this.main = main;

        //cloudPanel = new TagCloudPanel(main, 250);
        //cloudPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 2, 2));

        listPanel = new TagListPanel(main, null);

        JLabel lblShow = new JLabel("Show: ");
        String[] showStrings = {"Mine", "My Friends"};
        final JComboBox showList = new JComboBox(showStrings);
        showList.setSelectedIndex(0);
        showList.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                showMine = (showList.getSelectedIndex() == 0);
                updateView();
            }
        });

        main.getTagCloudModel().addPropertyChangeListener(new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent evt) {
                // update if showing the one that is changed
                System.out.println("inside tagPanel property changed");
                if (showMine) {
                    if (evt.getPropertyName().equals(TagCloudModel.MINE_CHANGED)) {
                        updateView();
                    }
                } else {
                    if (evt.getPropertyName().equals(TagCloudModel.OTHERS_CHANGED)) {
                        updateView();
                    }
                }

            }
        });
        this.add(new SplitPanel(showList, lblShow, BorderLayout.WEST), BorderLayout.NORTH);

//        JScrollPane sp = new JScrollPane(cloudPanel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
//        this.add(cloudPanel);
        //JScrollPane sp = new JScrollPane(listPanel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        JScrollPane sp = new JScrollPane(listPanel);
        sp.setMaximumSize(new Dimension(300, 200));
        sp.setPreferredSize(new Dimension(300, 200));

        //this.add(sp, BorderLayout.NORTH);
        this.add(sp);


        //this.setBorder(BorderFactory.createTitledBorder(title));
    }

    public void updateView() {
        SortedSet<TagFreq> tagsToShow;
        if (showMine) {
            tagsToShow = main.getTagCloudModel().getMineTopFreq(30);
        } else {
            tagsToShow = main.getTagCloudModel().getOtherTopFreq(30);
        }

        //cloudPanel.setTags(tagsToShow);
        listPanel.setTags(tagsToShow);
        System.out.println("listPanel " + listPanel);

        this.updateUI();
    }
}
