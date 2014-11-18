package com.example.fingerprint;

import android.app.Activity;
import android.os.Environment;

import java.io.File;

import Catalano.Imaging.FastBitmap;
import Catalano.Imaging.Filters.Threshold;



/**
 * Created by kenny on 11/12/14.
 */
public class Binarizer extends Activity{

    private int windowSize = 41;
    private float pixelBrightnessDifferenceLimit = 0.15f;
    FastBitmap bm ;
    Threshold threshold;

    public Binarizer(){
        bm = openImage();
        threshold = new Threshold();
        threshold.applyInPlace(bm);
    }



    private FastBitmap openImage(){
        FastBitmap bMap;
        File imgPath;
        String path;

        // open image (temporary way of opening image)
        imgPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        path = imgPath.getAbsolutePath();
        path += "/Fingerprints/Fingerprint.jpg";
        //convert to bitmap
        System.out.println("uri: "+path);
        bMap = new FastBitmap(path);
        return bMap;
    }


}
