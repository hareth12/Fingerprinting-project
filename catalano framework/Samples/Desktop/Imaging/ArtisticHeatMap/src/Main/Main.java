/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Main;

import Catalano.Imaging.FastBitmap;
import Catalano.Imaging.Filters.Artistic.HeatMap;
import Catalano.Imaging.Filters.DistanceTransform;
import Catalano.Imaging.Filters.FillHoles;
import Catalano.Imaging.Filters.Threshold;
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
        //http://pixabay.com/pt/brown-passeio-cavalo-animal-r%C3%A1pido-48394/
        FastBitmap fb = new FastBitmap("horse.png");
        JOptionPane.showMessageDialog(null, fb.toIcon(), "Original image", JOptionPane.PLAIN_MESSAGE);
        
        //Pre-process
        fb.toGrayscale();
        Threshold t = new Threshold(1);
        t.applyInPlace(fb);
        
        FillHoles fh = new FillHoles(0, 1000);
        fh.applyInPlace(fb);
        
        JOptionPane.showMessageDialog(null, fb.toIcon(), "Pre-process", JOptionPane.PLAIN_MESSAGE);
        
        //Distance transform
        DistanceTransform dt = new DistanceTransform();
        dt.Compute(fb);
        fb = dt.toFastBitmap();
        JOptionPane.showMessageDialog(null, fb.toIcon(), "Distance Transform", JOptionPane.PLAIN_MESSAGE);
        
        //HeatMap
        HeatMap heat = new HeatMap();
        heat.applyInPlace(fb);
        JOptionPane.showMessageDialog(null, fb.toIcon(), "Heat Map", JOptionPane.PLAIN_MESSAGE);
        
    }
    
}
