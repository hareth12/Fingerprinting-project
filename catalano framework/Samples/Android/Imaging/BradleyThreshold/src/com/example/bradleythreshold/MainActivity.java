package com.example.bradleythreshold;

import Catalano.Imaging.FastBitmap;
import Catalano.Imaging.Filters.BradleyLocalThreshold;
import android.os.Bundle;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.Menu;
import android.widget.ImageView;

public class MainActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ImageView image = (ImageView)findViewById(R.id.imageView1);
        
        //Load image from resources
        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.sample20);
        
        //Load fast bitmap
        FastBitmap fb = new FastBitmap(bm);
        
        //Convert to grayscale
        fb.toGrayscale();
        
        //Apply Bradley local threshold
        BradleyLocalThreshold brad = new BradleyLocalThreshold();
        brad.applyInPlace(fb);
        
        //Display the result
        image.setImageBitmap(fb.toBitmap());
        
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
}
