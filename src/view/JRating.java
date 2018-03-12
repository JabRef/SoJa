package view;

import java.awt.Cursor;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import test.FrameCreator;
import util.Loader;
import view.ImageConstants;

public class JRating extends JPanel implements ImageConstants {

    public static void main(String[] args) {
        JFrame f = FrameCreator.createTestFrame();
        final JRating ratings = new JRating(5);
        f.add(ratings);

        ratings.addPropertyChangeListener(new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent evt) {
                System.out.println("???" + ratings.getRating());
            }
        });
        f.pack();
        f.setVisible(true);
        System.out.println(f.getSize().height);
        new Thread() {

            public void run() {
                try {
                    Thread.sleep(500);
                    ratings.setRating(3, false);
                } catch (InterruptedException ex) {
                    Logger.getLogger(JRating.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }.start();

    }
    private ImageIcon starGlow = new ImageIcon(Loader.get(ImageConstants.STAR_GLOW)),  starSelected = new ImageIcon(Loader.get(ImageConstants.STAR_FULL)),  starUnselected = new ImageIcon(Loader.get(ImageConstants.STAR_OFF));
    private JButton[] btns;
    private int rating;
    private int max_rating;
    public static final String RATING = "test";
    private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

    /**
     * Add PropertyChangeListener.
     *
     * @param listener
     */
    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    /**
     * Remove PropertyChangeListener.
     *
     * @param listener
     */
    @Override
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

    public JRating(int rating, int MAX_RATING) {
        super(new GridLayout(1, 0, 0, 0), false);
        this.max_rating = MAX_RATING;
        setupUI();

        this.setRating(rating, true);
    }

    public JRating(int MAX_RATING) {
        this(0, MAX_RATING);
    }

    public int getRating() {
        return rating;
    }

    public void setupUI() {
        btns = new JButton[max_rating];

        for (int i = 0; i < btns.length; i++) {
            final int pos = i + 1; // 1 to x

            JButton button = new JButton();
            button.setMargin(new Insets(0, 0, 0, 0));
            button.setContentAreaFilled(false);
            button.setBorderPainted(false);
            button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            button.setToolTipText("Rate it " + String.valueOf(pos));
            button.setRolloverIcon(starGlow);

            this.add(button);
            btns[i] = button;

            button.addMouseListener(new MouseAdapter() {

                @Override
                public void mouseExited(MouseEvent e) {
                    for (int i = 0; i < pos; i++) {
                        if (i < getRating()) {
                            btns[i].setIcon(starSelected);
                        } else {
                            btns[i].setIcon(starUnselected);
                        }

                    }
                }

                @Override
                public void mouseEntered(MouseEvent e) {
                    for (int i = 0; i < pos-1; i++) {
                        btns[i].setIcon(starSelected);
                    }                    
                }
            });


            button.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    setRating(pos, true);
                }
            });
        }
    }

    /**
     * Can probably create setRating alone and call false, but better not
     * @param rating
     * @param firePropertyChange fire only if internal
     */
    public void setRating(int rating, boolean firePropertyChange) {
        if (rating > max_rating) {
            rating = max_rating;
        } else if (rating < 0) {
            rating = 0;
        }

        // must not set before this else old value lost
        if (firePropertyChange) {
            int oldRating = this.rating;
            this.rating = rating;
            propertyChangeSupport.firePropertyChange(RATING, oldRating, rating);
        }

        // in case firePropertyChange false and didn't set
        this.rating = rating;
        this.updateRatingUI();
    }

    public void updateRatingUI() {
        for (int i = 0; i < btns.length; i++) {
            final int pos = i + 1;
            JButton button = btns[i];

            if (pos <= getRating()) {
                button.setIcon(starSelected);
            } else {
                button.setIcon(starUnselected);
            }
        }
    }
}
