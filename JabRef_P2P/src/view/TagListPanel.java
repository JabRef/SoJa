package view;

import core.ActionHandler;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.SortedSet;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import model.TagFreq;

/**
 *
 * @author Thien Rong
 */
public class TagListPanel extends JPanel {

    public static void main(String[] args) {
        if (true) {
            JFrame f = new JFrame();
            f.setLayout(new BorderLayout());
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            JTabbedPane tabbedPane = new JTabbedPane();

            TagListPanel tp = new TagListPanel(new ActionHandler.TestActionHandler(), "Test");
            for (int i = 0; i < 100; i++) {
                int freq = (int) (Math.random() * 100);
                tp.addTag("test", freq);
                //System.out.println("i="+i);
            }
            JScrollPane sp = new JScrollPane(tp, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
            /*sp.getViewport().setViewSize(new Dimension(300, 300));
            /*sp.setMaximumSize(new Dimension(300,300));
            tp.setSize(500,500);
            sp.setSize(300, 300);*/
            JPanel pp = new JPanel();
            //pp.setSize(300, 300);
            //pp.add(new JButton("A"));
            pp.setLayout(new BorderLayout());
            //pp.setLayout(new BoxLayout(pp, BoxLayout.PAGE_AXIS));
            pp.add(sp, BorderLayout.NORTH);
            //tabbedPane.add(pp);
            f.add(pp);
            //f.add(tabbedPane);
            //f.setSize(300, 300);
            f.pack();
            f.setVisible(true);
        }
    }
    private ActionHandler handler;

    public TagListPanel(ActionHandler handler, String title) {
        this.handler = handler;
        this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        if (title != null) {
            this.setBorder(BorderFactory.createTitledBorder(title));
        }
    }

    public void setTags(SortedSet<TagFreq> tagFreq) {
        this.removeAll();
        for (TagFreq tf : tagFreq) {
            addTag(tf.getText(), tf.getFreq());
        }
    }

    private void addTag(final String keyword, int freq) {
        LinkButton btn = new LinkButton(keyword + "(" + freq + ")", freq);
        this.add(btn);
        btn.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                handler.handleViewTag(keyword);
            }
        });
    }
}
