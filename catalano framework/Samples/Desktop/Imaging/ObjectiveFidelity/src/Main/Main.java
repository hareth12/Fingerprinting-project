/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Main;

import Catalano.Imaging.FastBitmap;
import Catalano.Imaging.Concurrent.Filters.Median;
import Catalano.Imaging.Tools.ObjectiveFidelity;
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
        
        FastBitmap original = new FastBitmap("original.jpg");
        FastBitmap reconstructed = new FastBitmap("reconstructed.jpg");
        
        //Objective fidelity only works in grayscale images
        //If you need to compute for each channel, you need to extract the specified channel.
        if(!original.isGrayscale()) original.toGrayscale();
        if(!reconstructed.isGrayscale()) reconstructed.toGrayscale();
        
        ObjectiveFidelity of = new ObjectiveFidelity(original, reconstructed);
        int te = Math.abs(of.getTotalError());
        double mse = of.getMSE();
        double snr = of.getSNR();
        double psnr = of.getPSNR();
        double dsnr = of.getDerivativeSNR();
        
        JOptionPane.showMessageDialog(null, original.toIcon(), "Original image.", JOptionPane.PLAIN_MESSAGE);
        JOptionPane.showMessageDialog(null, reconstructed.toIcon(), "Original image with noise.", JOptionPane.PLAIN_MESSAGE);
        JOptionPane.showMessageDialog(null, "Objective Fidelity" + "\n\n" +
                "Total Error: " + te +"\n" +
                "Mean Square Error: " + mse + "\n" +
                "Signal Noise Ratio: " + snr + "\n" +
                "Peak Signal Noise Ratio: " + psnr + "\n" +
                "Derivative Signal Noise Ratio: " + dsnr + "\n"
                );
        
        //Apply the median filter and check the metrics.
        Median m = new Median();
        m.applyInPlace(reconstructed);
        
        of.setReconstructedImage(reconstructed);
        int te2 = Math.abs(of.getTotalError());
        double mse2 = of.getMSE();
        double snr2 = of.getSNR();
        double psnr2 = of.getPSNR();
        double dsnr2 = of.getDerivativeSNR();
        
        JOptionPane.showMessageDialog(null, reconstructed.toIcon(), "Reconstructed image", JOptionPane.PLAIN_MESSAGE);
        JOptionPane.showMessageDialog(null, "After the median filter for reduce the noise." + "\n" +
                "Objective Fidelity" + "\n\n" +
                "Total Error: " + te2 +"\n" +
                "Mean Square Error: " + mse2 + "\n" +
                "Signal Noise Ratio: " + snr2 + "\n" +
                "Peak Signal Noise Ratio: " + psnr2 + "\n" +
                "Derivative Signal Noise Ratio: " + dsnr2 + "\n"
                );
    }
}