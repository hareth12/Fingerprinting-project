package com.example.fingerprint;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;

import Catalano.Imaging.FastBitmap;
import Catalano.Imaging.Filters.Threshold;


/**
 * Created by kenny on 11/12/14.
 */

// Gribov was here!! 
public class Binarizer extends Activity{


    FastBitmap img;
       

    public Binarizer(){

        File img_path;
        img_path = openImage();
        Bitmap bm = BitmapFactory.decodeFile(img_path.getAbsolutePath()) ;
        img = new FastBitmap(bm);
    }
    
    
public void testThings() {
    Threshold threshold;
    threshold = new Threshold();
    threshold.applyInPlace(img);

    }

    public Bitmap getBitmap(){
        Bitmap bm = img.toBitmap();
        return bm;
    }
//not needed
    /*public Image getImage(){
        Picture picture = new Picture();
        Canvas canvas = picture.beginRecording(img.toBitmap().getWidth(), img.toBitmap().getHeight());
        Image image = img;

    }*/


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
    

// Different way of loading image
    /*private File openImage(){
        String path;
        File img, imgPath = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "Fingerprints");

        // open image (temporary way of opening image)
        path = imgPath.toURI().toString() + File.separator + "Fingerprint.jpg";
        img = new File(path);
        return img;
    }*/
    private File openImage(){
        String path = getFilesDir() + File.separator + "res" +File.separator + "test.jpg";
        File img = new File(path);
        return img;
    }

}
