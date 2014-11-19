/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Main;

import Catalano.Imaging.FastBitmap;
import Catalano.Imaging.Tools.ImageStatistics;
import Catalano.Statistics.Histogram;
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
        
        //Loads an image.
        FastBitmap fb = new FastBitmap("lenna.jpg");
        
        //Convert to grayscale
        fb.toGrayscale();
        
        //Image statistics
        ImageStatistics stat = new ImageStatistics(fb);
        
        //Get the gray histogram
        Histogram histR = stat.getHistogramGray();
        
        //Show the results
        StringBuilder sb = new StringBuilder();
        sb.append("Mean: " + histR.getMean() + "\n");
        sb.append("Median: " + histR.getMedian()+ "\n");
        sb.append("Mode: " + histR.getMode()+ "\n");
        sb.append("Min: " + histR.getMin()+ "\n");
        sb.append("Max: " + histR.getMax()+ "\n");
        sb.append("Standart deviation: " + histR.getStdDev()+ "\n");
        sb.append("Entropy: " + histR.getEntropy());
        
        JOptionPane.showMessageDialog(null, sb);
    }
}
