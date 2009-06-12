/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import javax.swing.JFrame;

/**
 *
 * @author Thien Rong
 */
public class FrameCreator {

    public static JFrame createTestFrame() {
        JFrame f = new JFrame();
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        return f;
    }

    public static void packAndShow(JFrame f){
        f.pack();
        f.setVisible(true);
    }
}
