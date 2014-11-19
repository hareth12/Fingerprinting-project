package com.example.convolution;

import Catalano.Imaging.FastBitmap;
import Catalano.Imaging.Filters.Convolution;
import android.os.Bundle;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

public class MainActivity extends Activity {
	
	//Button button;
	FastBitmap original;
	FastBitmap temp;
	Convolution c;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        startBlurButton();
        startSharpenButton();
        startEmbossButton();
        
        ImageView image = (ImageView)findViewById(R.id.imageView1);
        
        //Load image from resources.
        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.lenna);
        
        //Load Fast bitmap.
        original = new FastBitmap(bm);
        
        //Display the original image.
        image.setImageBitmap(original.toBitmap());
        
    }
    
    public void startBlurButton(){
        Button button1 = (Button)findViewById(R.id.button1);
        button1.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				
				temp = new FastBitmap(original);
				
				//Blur kernel
		        int[][] kernel = {
		        		{1,1,1},
		        		{1,1,1},
		        		{1,1,1}};
		        
		        //Apply the convolution
		        c = new Convolution(kernel);
		        c.applyInPlace(temp);
		        
		        //Show the result
		        ImageView image = (ImageView)findViewById(R.id.imageView1);
		        image.setImageBitmap(temp.toBitmap());
				
			}
		});
    }
    
    public void startSharpenButton(){
        Button button2 = (Button)findViewById(R.id.button2);
        button2.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				temp = new FastBitmap(original);
				
				//Sharpen kernel
		        int[][] kernel = {
		                {0 -1,0},
		                {-1,5,-1},
		                {0,-1,0}};
		        
		        //Apply the convolution
		        c = new Convolution(kernel);
		        c.applyInPlace(temp);
		        
		        //Show the result
		        ImageView image = (ImageView)findViewById(R.id.imageView1);
		        image.setImageBitmap(temp.toBitmap());
			}
		});
    }
    
    public void startEmbossButton(){
        Button button3 = (Button)findViewById(R.id.button3);
        button3.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				temp = new FastBitmap(original);
				
				//Blur kernel
		        int[][] kernel = {
		                {-2,0,0},
		                {0,1,0},
		                {0,0,2}};
		        
		        //Apply the convolution
		        c = new Convolution(kernel);
		        c.applyInPlace(temp);
		        
		        //Show the result
		        ImageView image = (ImageView)findViewById(R.id.imageView1);
		        image.setImageBitmap(temp.toBitmap());
			}
		});
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
}
