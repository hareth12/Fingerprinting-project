/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package houghlinetransformation;

import Catalano.Imaging.FastBitmap;
import Catalano.Imaging.Filters.Convolution;
import Catalano.Imaging.Tools.ConvolutionKernel;
import Catalano.Imaging.Tools.HoughLine;
import Catalano.Imaging.Tools.HoughLineTransformation;
import java.util.ArrayList;
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
        
        //Load an image.
        FastBitmap fb = new FastBitmap("lines.png");
        
        //Convert to grayscale.
        fb.toGrayscale();
        
        //Detect edges using laplace.
        Convolution c = new Convolution(ConvolutionKernel.Laplacian);
        c.applyInPlace(fb);
        
        //Hough Line Transformation.
        HoughLineTransformation hlt = new HoughLineTransformation(0,400);
        hlt.ProcessImage(fb);
        
        //Retrieve the lines.
        ArrayList<HoughLine> lines = hlt.getLines();
        
        //Converto to RGB.
        fb.toRGB();
        
        //Draw the lines with red color.
        HoughLine line = lines.get(0);
        line.drawLine(fb, 255, 0, 0);
        
        //Show the result.
        JOptionPane.showMessageDialog(null, fb.toIcon());
        
    }
}
