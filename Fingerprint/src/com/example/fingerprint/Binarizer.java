package com.example.fingerprint;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import java.io.File;

import Catalano.Imaging.FastBitmap;



/**
 * Created by kenny on 11/12/14.
 */

// Gribov was here!! 
public class Binarizer extends Activity{

    //Threshold threshold;
    FastBitmap img;
       
    //threshold = new Threshold();
    //threshold.applyInPlace(bm); 
    public Binarizer(){
    	File img_path;
        img_path = openImage();
        Bitmap bm = BitmapFactory.decodeFile(img_path.getAbsolutePath()) ;
        img = new FastBitmap(bm);
    }
    
    


    private File openImage(){
        BitmapFactory bMapmaker = new BitmapFactory();
        Bitmap bMap;
        String path;
        File img, imgPath = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "Fingerprints");
        // open image (temporary way of opening image)

        path = imgPath.toURI().toString() + File.separator + "Fingerprint.jpg";
        img = new File(path);
        //convert to FastBitmap
        bMap = bMapmaker.decodeFile(path);
        return img;
    }


}
