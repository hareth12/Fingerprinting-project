/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package harriscorners;

import Catalano.Core.IntPoint;
import Catalano.Imaging.Corners.HarrisCornersDetector;
import Catalano.Imaging.FastBitmap;
import java.util.ArrayList;
import javax.swing.JOptionPane;

/**
 * Sample: Harris Corners Detector.
 * @author Diego Catalano
 */
public class HarrisCorners {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        //Load an image.
        FastBitmap fb = new FastBitmap("blocks.gif");
        
        //Convert to grascale.
        fb.toGrayscale();
        
        //Process Harris Corners Detector.
        HarrisCornersDetector hcd = new HarrisCornersDetector();
        ArrayList<IntPoint> lst = hcd.ProcessImage(fb);
        
        //Convert to RGB.
        fb.toRGB();
        
        //Show the image setting the points with red color.
        for (IntPoint p : lst) {
            fb.setRGB(p, 255, 0, 0);
        }
        
        //Show the result.
        JOptionPane.showMessageDialog(null, fb.toIcon());
    }
}
