/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Main;

import Catalano.Imaging.FastBitmap;
import Catalano.Imaging.Filters.Artistic.FakeHDR;
import javax.swing.JOptionPane;

/**
 *
 * @author Diego
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        //Original image
        //http://pixabay.com/pt/casamento-mountain-lake-veja-bergsee-175364/
        FastBitmap fb = new FastBitmap("lake.jpg");
        JOptionPane.showMessageDialog(null, fb.toIcon(), "Original image", JOptionPane.PLAIN_MESSAGE);
        
        FakeHDR f = new FakeHDR(0.3);
        f.applyInPlace(fb);
        
        JOptionPane.showMessageDialog(null, fb.toIcon(), "Fake HDR", JOptionPane.PLAIN_MESSAGE);
        
    }
    
}
