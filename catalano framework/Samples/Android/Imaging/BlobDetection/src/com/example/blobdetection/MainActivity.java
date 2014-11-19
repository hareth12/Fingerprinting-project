package com.example.blobdetection;

import java.util.ArrayList;
import Catalano.Imaging.*;
import Catalano.Imaging.Shapes.IntRectangle;
import Catalano.Imaging.Tools.Blob;
import Catalano.Imaging.Tools.BlobDetection;

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
        
        //Load image from resources.
        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.objects);
        
        //Load fast bitmap.
        FastBitmap fb = new FastBitmap(bm);
        
        //Convert to grayscale.
        fb.toGrayscale();
        
        //Certified the objects is white
        //Threshold t = new Threshold(20);
        //t.applyInPlace(fb);
        
        //Detect Blobs.
        BlobDetection bd = new BlobDetection();
        ArrayList<Blob> blobs = bd.ProcessImage(fb);
        
        //Convert image to RGB for to draw the rectangles.
        fb.toRGB();
        
        //Used for to draw.
        FastGraphics fg = new FastGraphics(fb);
        fg.setColor(255, 0, 0);
        
        //For each blob, we draw a rectangle.
        for(Blob b : blobs){
        	IntRectangle rect = b.getBoundingBox();
        	fg.DrawRectangle(rect);
        }
        
        //Display the result.
        image.setImageBitmap(fb.toBitmap());
        
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
}
