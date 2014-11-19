package com.example.pencilsketch;

import Catalano.Imaging.FastBitmap;
import Catalano.Imaging.Concurrent.Filters.BradleyLocalThreshold;
import Catalano.Imaging.Filters.Artistic.PencilSketch;
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
        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.teste);
        
        //Load fast bitmap
        FastBitmap fb = new FastBitmap(bm);
        
        fb.toGrayscale();
        fb.toRGB();
        
        //Pencil Sketch
        PencilSketch ps = new PencilSketch();
        ps.applyInPlace(fb);
        
        //Display the result
        image.setImageBitmap(fb.toBitmap());
        
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
}
