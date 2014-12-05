package com.example.fingerprint;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;

import Catalano.Core.IntPoint;
import Catalano.Imaging.Corners.SusanCornersDetector;
import Catalano.Imaging.FastBitmap;
import Catalano.Imaging.Filters.BradleyLocalThreshold;
import Catalano.Imaging.Filters.BrightnessCorrection;
import Catalano.Imaging.Filters.FourierTransform;
import Catalano.Imaging.Filters.FrequencyFilter;
import Catalano.Imaging.Filters.HistogramEqualization;
import Catalano.Imaging.Filters.Sharpen;


/**
 * Created by kenny on 11/12/14.
 */

public class Binarizer extends Activity{


    FastBitmap img;


    public Binarizer(){
        //Log.d("Binarizer", "Got to step 1") ;
        //Log.d("Binarizer", "Got to step 2") ;
        //System.out.println(openImage());
        Bitmap bm = BitmapFactory.decodeFile(openImage()) ;
        //Log.d("Binarizer", "Got to step 3");

        img = new FastBitmap(bm);
        bm.recycle();

        Log.d("openImage", "get here in openImage");
    }

    public void cropDynamically(){
        Bitmap bm = img.toBitmap();
        int startx = bm.getWidth() / 4;
        int starty = bm.getHeight() / 4;
        int endx = bm.getWidth() - 2 * startx;
        int endy = bm.getHeight() - 2 * starty;
        System.out.println(""+bm.getWidth()+ ", " + bm.getHeight() + ", "+ startx + ", " + starty+ ", " + endx+ ", " + endy);
        Log.d("crop", "initialized vars");
        Bitmap croppedBm = Bitmap.createBitmap(bm, startx, starty, endx, endy);
        Log.d("crop", "crop applied");
        Bitmap scaledBm = Bitmap.createScaledBitmap(croppedBm, 1024, 768, false);
        Log.d("crop", "bitmap scaled");
        img = new FastBitmap(scaledBm);
        bm.recycle();
}
    
    
    public void testThings() {

        SusanCornersDetector susanCornersDetector = new SusanCornersDetector();
        ArrayList<IntPoint> list = susanCornersDetector.ProcessImage(img);
        for (IntPoint p : list) {
            img.setRGB(p, 0, 0, 0);
        }

        Bitmap bm = img.toBitmap();

        int[] allpixels = new int[bm.getHeight() * bm.getWidth()];

        bm.getPixels(allpixels, 0, bm.getWidth(), 0, 0, bm.getWidth(), bm.getHeight());

        for (int i = 0; i < bm.getHeight() * bm.getWidth(); i++) {

            if (allpixels[i] == Color.BLACK)
                allpixels[i] = Color.WHITE;
        }

        bm.setPixels(allpixels, 0, bm.getWidth(), 0, 0, bm.getWidth(), bm.getHeight());

        BrightnessCorrection brightnessCorrection = new BrightnessCorrection(50);
        brightnessCorrection.applyInPlace(img);

        HistogramEqualization histogramEqualization = new HistogramEqualization();
        histogramEqualization.applyInPlace(img);
        img.toGrayscale();


        Log.d("testThings", "img to gray");
        FourierTransform ft = new FourierTransform(img);
        Log.d("testThings", "created ft");
        ft.Forward();
        Log.d("testThings", "went forward in ft");
        FrequencyFilter ff = new FrequencyFilter(35, 75);
        ff.ApplyInPlace(ft);
        ft.Backward();
        Log.d("testThings", "went backward in ft");
        img = ft.toFastBitmap();



        //this hist eq doesnt work here
        //histogramEqualization.applyInPlace(img);


        //CannyEdgeDetector cannyEdgeDetector = new CannyEdgeDetector(10,20);
        //cannyEdgeDetector.applyInPlace(img);

        FastBitmap temp;
        int i, j, k, l, cellsize = 3;
        for (i = 0; i < img.getWidth() / cellsize - 1; i++) {
            Log.d("testThings", "i = " + i);
            for (j = 0; j < img.getHeight() / cellsize - 1; i++) {
                temp = new FastBitmap(cellsize, cellsize);
                Log.d("testThings", "j = " + j);
                for (k = 0; k < cellsize; k++) {
                    Log.d("testThings", "k = " + k);
                    for (l = 0; l < cellsize; l++) {
                        temp.setRGB(k, l, img.getRGB(i * cellsize + k, j * cellsize + l));
                        Sharpen sharpen = new Sharpen();
                        sharpen.applyInPlace(temp);
                        Log.d("testThings", "i = " + i + ", j = " + j + ", k = " + k + ", l = " + l + ",  at pixel " + i * cellsize + "x" +j*cellsize);
                        img.setRGB(i * cellsize + k, j * cellsize + l, temp.getRGB(k, l));
                        Log.d("testThings", "cycled");
                    }
                }
                temp.recycle();
                Log.d("testThings", "recycled");
            }
        }

            //SobelEdgeDetector sobelEdgeDetector = new SobelEdgeDetector();
            //sobelEdgeDetector.applyInPlace(img);


            BradleyLocalThreshold bradleyLocalThreshold = new BradleyLocalThreshold(160);
            bradleyLocalThreshold.applyInPlace(img);

            //Threshold threshold = new Threshold(15);
            //threshold.applyInPlace(img);


            //Threshold with divide and conquer iterative
        /*int i, j, xpix, ypix, xmax = img.toBitmap().getWidth(), ymax = img.toBitmap().getHeight(), xcells = 2, ycells = 2, xcellsize = xmax / xcells, ycellsize = ymax / ycells;
        Bitmap[][] arr = new Bitmap[xmax][ymax];
        Bitmap newimg = Bitmap.createBitmap(img.toBitmap()), tempbm = null;
        for (i = 0; i < xcells; i++) {
            for (j = 0; j < ycells; j++) {
                arr[i][j] = Bitmap.createBitmap(img.toBitmap(), i * xcellsize, j * ycellsize, xmax - i * xcellsize, ymax - j * ycellsize);
                FastBitmap fbm = new FastBitmap(arr[i][j]);
                fbm.toGrayscale();
                Log.d("binarizer", "got fbm in gray");
                Threshold threshold = new Threshold(30);
                threshold.applyInPlace(fbm);
                Log.d("binarizer", "applied threshold");
                tempbm = fbm.toBitmap();
                Log.d("binarizer", "entering for loop ");
                for (xpix = 0; xpix < tempbm.getWidth(); xpix++) {
                    for (ypix = 0; ypix < tempbm.getHeight(); ypix++) {
                        newimg.setPixel(i * xcellsize + xpix, j * ycellsize + ypix, tempbm.getPixel(xpix, ypix));
                        Log.d("binarizer", "i =" + i + ", j =" + j +", xpix =" + xpix + ", ypix =" + ypix);
                    }
                }
                Log.d("binarizer", "copied bm with i = " + i + " and j = " + j);

            }
        }
        img.recycle();
        img = new FastBitmap(newimg);*/
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
