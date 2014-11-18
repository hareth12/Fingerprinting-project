package com.example.fingerprint;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
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
    FastBitmap fbm;
    File img;
    Bitmap bm ;
    Threshold threshold;

    public Binarizer(){
        img = openImage();
        //fbm = FastBitmap.(bm);
        //threshold = new Threshold();
        //threshold.applyInPlace(bm);


        Intent intent = new Intent(this, ProcessActivity.class);
        intent.setAction(android.content.Intent.ACTION_VIEW);
        Uri androidUri = android.net.Uri.parse(img.getAbsoluteFile().toURI().toString());
        intent.setData(androidUri);
        startActivity(intent);
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
