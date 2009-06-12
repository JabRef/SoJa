/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import java.awt.Cursor;
import java.awt.Font;
import java.awt.Insets;
import javax.swing.JButton;

/**
 *
 * @author Thien Rong
 */
public class LinkButton extends JButton {

    public static void main(String[] args) {        
        new LinkButton("A", 80);
    }
    private Cursor tagCursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
    static final int MIN = 12,  MAX = 30;

    public LinkButton(String text, int size) {
        super(text);        
        this.setBorderPainted(false);
        this.setContentAreaFilled(false);
        this.setOpaque(false);
        this.setFont(getFont().deriveFont(deriveSize(size)));
        this.setMargin(new Insets(0, 0, 0, 0));
        this.setCursor(tagCursor);
    }

    private float deriveSize(int freq) {
        int diff = MAX - MIN + 1;
        // (problem => too small)
        //int finalSize = (diff * freq / total) + MIN;
        // absolute size using log        
        int finalSize = (int) (diff * Math.min(10, Math.log(Math.max(2, freq - 10))) / 10 + MIN);        
        // (if > diff will be much much larger) Math.round((12 * (tag.getUsercount() - userCountMin)) / ((userCountMax - userCountMin) + 1)) + 12;
        // absolute size, 5 type of sizes
        //int finalSize = (int) (MIN + (diff * Math.min(1, freq / 100f)));
        //System.out.println("(" + diff + "," + freq + "," + total + ") + " + MIN + "=" + finalSize);

        return finalSize;
    }
}
