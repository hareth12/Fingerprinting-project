/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Main;

import Catalano.Imaging.Filters.Convolution;
import Catalano.Imaging.FastBitmap;
import javax.swing.JOptionPane;

/**
 *
 * @author Diego
 */
public class SharpenSample {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        //Loads an image.
        FastBitmap fb = new FastBitmap("lenna.jpg");
        
        //Sharpen using kernel.
        int[][] kernel = {
            {0 -1,0},
            {-1,5,-1},
            {0,-1,0}};
        
        //Convolution process.
        Convolution c = new Convolution(kernel);
        c.applyInPlace(fb);
        
        //Show the result.
        JOptionPane.showMessageDialog(null, fb.toIcon());
        
    }
}
