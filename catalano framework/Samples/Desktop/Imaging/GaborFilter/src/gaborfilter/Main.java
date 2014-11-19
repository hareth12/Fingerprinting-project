/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gaborfilter;

import Catalano.Imaging.FastBitmap;
import Catalano.Imaging.Filters.GaborFilter;
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
        // Load an image
        FastBitmap fb = new FastBitmap("c:\\files\\lines.png");
        
        //You must to convert to RGB when you are working with PNG files.
        fb.toRGB();
        
        //Convert to grayscale
        fb.toGrayscale();
        
        //Initialize Gabor Filter
        //Gabor filter is pre configured for find lines in 45 degrees.
        GaborFilter gf = new GaborFilter();
        
        //Find Horizontal lines
        //gf.setTheta(0.1);
        
        //Apply Gabor filter
        gf.applyInPlace(fb);
        
        //Display image
        JOptionPane.showMessageDialog(null, fb.toIcon());
    }
}
