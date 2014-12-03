package com.example.fingerprint;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Environment;
import android.util.Log;
import android.widget.ImageView;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;

import Catalano.Core.IntPoint;
import Catalano.Imaging.Concurrent.Filters.BradleyLocalThreshold;
import Catalano.Imaging.Concurrent.Filters.SobelEdgeDetector;
import Catalano.Imaging.Corners.SusanCornersDetector;
import Catalano.Imaging.FastBitmap;
import Catalano.Imaging.Filters.CannyEdgeDetector;
import Catalano.Imaging.Filters.Crop;
import Catalano.Imaging.Filters.HistogramEqualization;
import Catalano.Imaging.Filters.Threshold;


/**
 * Created by kenny on 11/12/14.
 */

// Gribov was here!! 
public class Binarizer extends Activity{


    FastBitmap img;
    Bitmap bm;

    public Binarizer(){
        Log.d("Binarizer", "Got to step 1") ;
        Log.d("Binarizer", "Got to step 2") ;
        System.out.println(openImage());
        bm = BitmapFactory.decodeFile(openImage()) ;
        Log.d("Binarizer", "Got to step 3");

        img = new FastBitmap(bm);

        Log.d("openImage", "get here in openImage");
    }

    public void cropDynamically(){
        int startx = bm.getWidth() / 4;
        int starty = bm.getHeight() / 4;
        int endx = bm.getWidth() - 2 * startx;
        int endy = bm.getHeight() - 2 * starty;
        System.out.println(""+bm.getWidth()+ ", " + bm.getHeight() + ", "+ startx + ", " + starty+ ", " + endx+ ", " + endy);
        Log.d("crop", "initialized vars");
        Bitmap croppedBm = Bitmap.createBitmap(bm, startx, starty, endx, endy);
        Log.d("crop", "crop applied");
        img = new FastBitmap(croppedBm);
    }
    
    
    public void testThings() {
    	//Bitmap.Config conf = Bitmap.Config.ARGB_8888;
        img.toGrayscale();
        //cropDynamically();
        Log.d("crop", "crop completed");
        /*SusanCornersDetector susanCornersDetector = new SusanCornersDetector(5,10);
        ArrayList<IntPoint> list = susanCornersDetector.ProcessImage(img);
        Bitmap temp = Bitmap.createBitmap(img.getWidth(), img.getHeight(), conf);
        for(IntPoint p : list){
            img.setRGB(p,255,255,255);
        }*/
        //CannyEdgeDetector cannyEdgeDetector = new CannyEdgeDetector(10,20);
        //cannyEdgeDetector.applyInPlace(img);
        //HistogramEqualization histogramEqualization = new HistogramEqualization();
        //histogramEqualization.applyInPlace(img);
        //SobelEdgeDetector sobelEdgeDetector = new SobelEdgeDetector();
        //sobelEdgeDetector.applyInPlace(img);
        //BradleyLocalThreshold bradleyLocalThreshold = new BradleyLocalThreshold(150);
        //bradleyLocalThreshold.applyInPlace(img);
        Threshold threshold = new Threshold(150);
    	threshold.applyInPlace(img);

    }

    public Bitmap getBitmap(){
    	
        return img.toBitmap();    

    }


    // EASY FUNCTIONS WE NEED TO MAKE OR FIND:
    
    //private void imrotate() {}
    //private void rgb2gray() {}
    //private void ridgeSegment() {} 
    
    // THIS ONES GONNA BE A BITCH TO WRITE:
	/*% RIDGESEGMENT - Normalises fingerprint image and segments ridge region
	%
	% Function identifies ridge regions of a fingerprint image and returns a
	% mask identifying this region.  It also normalises the intesity values of
	% the image so that the ridge regions have zero mean, unit standard
	% deviation.
	%
	% This function breaks the image up into blocks of size blksze x blksze and
	% evaluates the standard deviation in each region.  If the standard
	% deviation is above the threshold it is deemed part of the fingerprint.
	% Note that the image is normalised to have zero mean, unit standard
	% deviation prior to performing this process so that the threshold you
	% specify is relative to a unit standard deviation.
	%
	% Usage:   [normim, mask, maskind] = ridgesegment(im, blksze, thresh)
	%
	% Arguments:   im     - Fingerprint image to be segmented.
	%              blksze - Block size over which the the standard
	%                       deviation is determined (try a value of 16).
	%              thresh - Threshold of standard deviation to decide if a
	%                       block is a ridge region (Try a value 0.1 - 0.2)
	%
	% Returns:     normim - Image where the ridge regions are renormalised to
	%                       have zero mean, unit standard deviation.
	%              mask   - Mask indicating ridge-like regions of the image, 
	%                       0 for non ridge regions, 1 for ridge regions.
	%              maskind - Vector of indices of locations within the mask. 
	%
	% Suggested values for a 500dpi fingerprint image:
	%
	%   [normim, mask, maskind] = ridgesegment(im, 16, 0.1)
	%
	% See also: RIDGEORIENT, RIDGEFREQ, RIDGEFILTER */
    

// Different wa y of loading image
    private String openImage(){
        String path;
        //File img;
        File imgPath = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "Fingerprints");
        Log.d("openImage", "get here in openImage");

        // open image (temporary way of opening image)
        path = imgPath.getAbsolutePath().toString() + File.separator + "Fingerprint.jpg";
        return path;
        //img = new File(path);
        //return img;
    }
    /*private File openImage(){
        String path = getFilesDir() + File.separator + "res" + File.separator 
        							+ "drawable-hdpi" + File.separator + "test.jpg";
        File img = new File(path);
        return img;
    }*/

}
