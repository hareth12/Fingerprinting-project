package helperClasses;

import java.awt.*;
import java.awt.image.*;
import java.awt.geom.*;
import java.awt.font.*;

/** This plugin calculates a two dimensional (2-D) gaussian lowpass filter using
     a 2-D Gaussian or Normal density function is defined in the literature.

     Gaussian filters are important in many signal processing, image processing,
     and communication applications. These filters are characterized by narrow bandwidths and
     sharp cutoffs. A key feature of Gaussian filters is that the Fourier transform of a Gaussian is
     also a Gaussian, so the filter has the same response shape in both the time and frequency domains.

     The cutoff parameter defines the cutoff-frequency. The DC-level parameter defines the height of the dc center component.

     Erik Lieng 01/10/2002
*/

public class Gaussian_Filter{
    static double cutoff = 50;
    static double dc_level = 255;
    BufferedImage img;
    
    public Gaussian_Filter(BufferedImage img){
    	this.img = img;
    }
    
    public BufferedImage getFilter(){
    	return img;
    }
    
    public void run() {

        double value = 0;
        double distance = 0;
        int sizex = img.getWidth();
        int sizey = img.getHeight();
        int xcenter = (sizex/2)+1;
        int ycenter = (sizey/2)+1;
        for (int y = 0; y < sizey; y++){
            for (int x = 0; x < sizex; x++){
                distance = Math.abs(x-xcenter)*Math.abs(x-xcenter)+Math.abs(y-ycenter)*Math.abs(y-ycenter);
                distance = Math.sqrt(distance);
                value = dc_level*Math.exp((-1*distance*distance)/(1.442695*cutoff*cutoff));
                img.setRGB(x,y,(int) value);

            }
        }
   }


}
