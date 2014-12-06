package com.example.fingerprint;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import java.io.File;

import Catalano.Imaging.FastBitmap;
import Catalano.Imaging.Filters.HistogramEqualization;
import Catalano.Imaging.Filters.Threshold;


/**
 * Created by kenny on 11/12/14.
 */
public class Binarizer extends Activity {


    FastBitmap img;

    public Binarizer() {
        //Log.d("Binarizer", "Got to step 1") ;
        //Log.d("Binarizer", "Got to step 2") ;
        //System.out.println(openImage());
        Bitmap bm = BitmapFactory.decodeFile(openImage());
        //Log.d("Binarizer", "Got to step 3");

        img = new FastBitmap(bm);
        bm.recycle();

        Log.d("openImage", "get here in openImage");
    }

    public void cropDynamically() {
        Bitmap bm = img.toBitmap();
        int startx = bm.getWidth() / 4;
        int starty = bm.getHeight() / 4;
        int endx = bm.getWidth() - 2 * startx;
        int endy = bm.getHeight() - 2 * starty;
        System.out.println("" + bm.getWidth() + ", " + bm.getHeight() + ", " + startx + ", " + starty + ", " + endx + ", " + endy);
        Log.d("crop", "initialized vars");
        Bitmap croppedBm = Bitmap.createBitmap(bm, startx, starty, endx, endy);
        Log.d("crop", "crop applied");
        Bitmap scaledBm = Bitmap.createScaledBitmap(croppedBm, 1024, 768, false);
        Log.d("crop", "bitmap scaled");
        img = new FastBitmap(scaledBm);
        bm.recycle();
    }


    public void testThings() {
//THIS WORKS
        /*SusanCornersDetector susanCornersDetector = new SusanCornersDetector();
        ArrayList<IntPoint> list = susanCornersDetector.ProcessImage(img);
        for (IntPoint p : list) {
            img.setRGB(p, 0, 0, 0);
        }*/
/*
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
        Log.d("testThings", "corrected Brightness");

        HistogramEqualization histogramEqualization = new HistogramEqualization();
        histogramEqualization.applyInPlace(img);

        Log.d("testThings", "Histogram equalized");


        Sharpen sharpen = new Sharpen();
        sharpen.applyInPlace(img);
        Log.d("testThings", "sharpened");
        //ACE... idk wtf it does yet
        //AdaptiveContrastEnhancement ace = new AdaptiveContrastEnhancement(cellsize, .5, .5, 100, 100);
        //ace.applyInPlace(cell);

        Log.d("testThings", "smoothed");
        ContrastCorrection cc = new ContrastCorrection();
        cc.applyInPlace(img);
        Log.d("testThings", "corrected contrast");




        img.toGrayscale();
        Log.d("testThings", "img to gray");

        GaborFilter gf = new GaborFilter();
        gf.applyInPlace(img);
        Log.d("testThings", "Gabor filtered");

        ConservativeSmoothing cs = new ConservativeSmoothing();
        cs.applyInPlace(img);

        FourierTransform ft = new FourierTransform(img);
        Log.d("testThings", "created ft");
        ft.Forward();
        Log.d("testThings", "went forward in ft");
        FrequencyFilter ff = new FrequencyFilter(40, 70);
        ff.ApplyInPlace(ft);
        ft.Backward();
        Log.d("testThings", "went backward in ft");
        img = ft.toFastBitmap();
        divideAndConquer(0, 0, 50);

        Invert invert = new Invert();
        invert.applyInPlace(img);
KINDA WORKS*/
        //img.toGrayscale();
        //GaborFilter gf = new GaborFilter();
        //gf.applyInPlace(img);

        divideAndConquer(0, 0, 15);

        //this hist eq doesnt work here
        //histogramEqualization.applyInPlace(img);


        //CannyEdgeDetector cannyEdgeDetector = new CannyEdgeDetector(10,20);
        //cannyEdgeDetector.applyInPlace(img);
/*
        FastBitmap temp;
        int i, j, k, l, cellsize = 3;
        for (i = 0; i < (img.getWidth() / cellsize) -1; i++) {
            Log.d("testThings", "i = " + i);
            for (j = 0; j < (img.getHeight() / cellsize) -1; j++) {
                temp = new FastBitmap(cellsize, cellsize);
                Log.d("testThings", "j = " + j);
                for (k = 0; k < cellsize; k++) {
                    Log.d("testThings", "k = " + k);
                    for (l = 0; l < cellsize; l++) {
                        temp.setRGB(k, l, img.getRGB(i * cellsize + k, j * cellsize + l));
                        Log.d("testThings", "i = " + i + ", j = " + j + ", k = " + k + ", l = " + l + ",  at pixel " + i * cellsize + "x" +j*cellsize);
                    }
                }
                Sharpen sharpen = new Sharpen();
                sharpen.applyInPlace(temp);
                for (k = 0; k < cellsize; k++) {
                    Log.d("testThings", "k = " + k);
                    for (l = 0; l < cellsize; l++) {
                        Log.d("testThings", "i = " + i + ", j = " + j + ", k = " + k + ", l = " + l + ",  at pixel " + i * cellsize + "x" +j*cellsize);
                        img.setRGB(i * cellsize + k, j * cellsize + l, temp.getRGB(k, l));
                        Log.d("testThings", "cycled");
                    }
                }
                temp.recycle();
                Log.d("testThings", "recycled");
            }
        }*/

        //SobelEdgeDetector sobelEdgeDetector = new SobelEdgeDetector();
        //sobelEdgeDetector.applyInPlace(img);


        //BradleyLocalThreshold bradleyLocalThreshold = new BradleyLocalThreshold(160);
        //bradleyLocalThreshold.applyInPlace(img);

        //Threshold threshold = new Threshold(15);
        //threshold.applyInPlace(img);


        //Threshold with divide and conquer iterative
        /*int i, j, xpix, ypix, xmax = img.getWidth(), ymax = img.getHeight(), xcells = 2, ycells = 2, xcellsize = xmax / xcells, ycellsize = ymax / ycells;
        FastBitmap[][] arr = new FastBitmap[xmax][ymax];
        for (i = 0; i < xcells; i++) {
            for (j = 0; j < ycells; j++) {
                arr[i][j] = new FastBitmap(xcellsize, ycellsize);
                for (xpix = 0; xpix < xcellsize; xpix++) {
                    Log.d("testThings", "xpix = " + xpix);
                    for (ypix = 0; ypix < ycellsize; ypix++) {
                        arr[i][j].setRGB(xpix, ypix, img.getRGB(i * xcellsize + xpix, j * ycellsize + ypix));
                        Log.d("testThings", "i = " + i + ", j = " + j + ", xpix = " + xpix + ", ypix = " + ypix + ",  at pixel " + i * xcellsize + "x" + j * ycellsize);
                    }
                }

                arr[i][j].toGrayscale();
                Sharpen sharpen = new Sharpen();
                sharpen.applyInPlace(img);
                Log.d("binarizer", "got fbm in gray");
                Threshold threshold = new Threshold(30);
                threshold.applyInPlace(arr[i][j]);
                Log.d("binarizer", "applied threshold");
                Log.d("binarizer", "entering for loop ");
                for (xpix = 0; xpix < arr[i][j].getWidth(); xpix++) {
                    for (ypix = 0; ypix < arr[i][j].getHeight(); ypix++) {
                        img.setRGB(i * xcellsize + xpix, j * ycellsize + ypix, arr[i][j].getRGB(xpix, ypix));
                        Log.d("binarizer", "i =" + i + ", j =" + j +", xpix =" + xpix + ", ypix =" + ypix);
                    }
                }
                arr[i][j].recycle();
                Log.d("binarizer", "copied bm with i = " + i + " and j = " + j);

            }
        }*/
    }

    public FastBitmap copyPixels(FastBitmap src, int xstartsrc, int ystartsrc, int xendsrc, int yendsrc, int xpos, int ypos, FastBitmap dest, int xstartdest, int ystartdest, int xenddest, int yenddest) {
      if (((xstartdest + xpos < xenddest) && (xstartsrc + xpos < xendsrc)) && ((ystartdest + ypos < yenddest) && (ystartsrc + ypos < yendsrc))){
          //Log.d("copyPixels", "xstartsrc = " + xstartsrc + ", ystartsrc = " + ystartsrc + ", xendsrc = " + xendsrc + ", yendsrc = " + yendsrc + ", xpos = " + xpos + ", ypos = " + ypos + ", xstartdest = " + xstartdest + ", ystartdest = " + ystartdest + ", xenddest = " + xenddest + ", yenddest = " + yenddest);
          dest.setRGB(xstartdest + xpos, ystartdest + ypos, src.getRGB(xstartsrc + xpos, ystartsrc + ypos));
          return copyPixels(src, xstartsrc, ystartsrc, xendsrc, yendsrc, xpos + 1, ypos, dest, xstartdest, ystartdest, xenddest, yenddest);
      }
      else {
                if ((xstartdest + xpos >= xenddest) && (xstartsrc + xpos >= xendsrc)) {
                    return copyPixels(src, xstartsrc, ystartsrc, xendsrc, yendsrc, 0, ypos + 1, dest, xstartdest, ystartdest, xenddest, yenddest);
                }
                else {
                    return dest;
                }
            }
    }


    public void divideAndConquer(int x, int y, int cellsize) {
        Log.d("divideAndConquer", "x = " + x + ", y = " + y + ",w = " + img.getWidth() + ", h = " + img.getHeight());
        if (x + cellsize >= img.getHeight() && y == img.getWidth()){}
        else {
            if ((x + cellsize) < img.getHeight() && y < img.getWidth()) {
                FastBitmap cell = new FastBitmap(cellsize, cellsize);
                cell = copyPixels(img, x, y, x + cellsize, y + cellsize, 0, 0, cell, 0, 0, cellsize, cellsize);
            /*
            filters
            Sharpen sharpen = new Sharpen();
            sharpen.applyInPlace(cell);

            BradleyLocalThreshold bradleyLocalThreshold = new BradleyLocalThreshold(10);
                bradleyLocalThreshold.applyInPlace(cell);


            */
                cell.toGrayscale();

                Threshold threshold = new Threshold(getSumOfFastbitmap(cell, 0, 0, 0) /(cellsize*cellsize));
                threshold.applyInPlace(cell);
                HistogramEqualization histo = new HistogramEqualization();
                histo.applyInPlace(cell);





                Log.d("divideAndConquer", "filtered at x = " + x + ", y = " + y);
                img = copyPixels(cell, 0, 0, cellsize, cellsize, 0, 0, img, x, y, x + cellsize, y + cellsize);
                cell.recycle();
                divideAndConquer(x + cellsize, y, cellsize);
            } else {
                if (y < img.getWidth()) {
                    Log.d("divideAndConquer", "move down line");
                    divideAndConquer(0, y + cellsize, cellsize);
                } else {
                    Log.d("divideAndConquer", "uh oh");
                }
            }
        }
    }

    public int getSumOfFastbitmap(FastBitmap fb, int x, int y, int sum){
        if(x < fb.getHeight() - 1){
            sum = sum + fb.getGray(x,y);
            return getSumOfFastbitmap(fb, x + 1, y, sum);
        }
        else {
            if (y < fb.getWidth()){
                return getSumOfFastbitmap(fb, 0, y + 1, sum);
            }
            else{
                return sum;
            }
        }

    }

    public Bitmap getBitmap() {

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
    private String openImage() {
        String path;
        //File img;
        File imgPath = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "Fingerprints");
        Log.d("openImage", "get here in openImage");

        // open image (temporary way of opening image)
        path = imgPath.getAbsolutePath();

        //to make this test with fingerprint image included in package
        //createFingerPrintThere(path);
        path = path + File.separator + "Fingerprint.jpg";
        return path;
        //img = new File(path);
        //return img;
    }
}

    /*
    trying to include image in app so we can test on emus and what not easily
    private void createFingerPrintThere(String path){
        InputStream inputstream = null;
        try {
            inputstream = this.getResources().getAssets().open("test.jpg");
        } catch (IOException e) {
            e.printStackTrace();
        }

        File dest = new File(path);
        Bitmap bitmap = BitmapFactory.decodeStream(inputstream);
        try {
            FileOutputStream out;
            out = new FileOutputStream(dest);
            bitmap.compress(Bitmap.CompressFormat.JPEG , 100, out);
            out.flush();
            out.close();
        } catch (FileNotFoundException e) {

            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        /*
        OutputStream stream = null;

        try{
            stream = new FileOutputStream(path);
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }*/

    /*private File openImage(){
        String path = getFilesDir() + File.separator + "res" + File.separator 
        							+ "drawable-hdpi" + File.separator + "test.jpg";
        File img = new File(path);
        return img;
    }

}*/
