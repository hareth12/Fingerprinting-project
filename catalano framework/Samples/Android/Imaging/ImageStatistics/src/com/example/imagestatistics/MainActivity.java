package com.example.imagestatistics;

import java.text.DecimalFormat;

import Catalano.Imaging.FastBitmap;
import Catalano.Imaging.Tools.ImageStatistics;
import Catalano.Statistics.Histogram;
import android.os.Bundle;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        ImageView image = (ImageView)findViewById(R.id.imageView1);
        
        //Load image from resources
        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.lenna);
        
        //Load fast bitmap
        FastBitmap fb = new FastBitmap(bm);
        
        //Convert to grayscale
        fb.toGrayscale();
        
        //Image statistics
        ImageStatistics stat = new ImageStatistics(fb);
        
        //Get the histogram
        Histogram histG = stat.getHistogramGray();
        
        //Show the values
        TextView txtMean = (TextView)findViewById(R.id.txtMean);
        TextView txtMedian = (TextView)findViewById(R.id.txtMedian);
        TextView txtMode = (TextView)findViewById(R.id.txtMode);
        TextView txtMin = (TextView)findViewById(R.id.txtMinimum);
        TextView txtMax = (TextView)findViewById(R.id.txtMaximum);
        TextView txtStdDev = (TextView)findViewById(R.id.txtStdDev);
        TextView txtEntropy = (TextView)findViewById(R.id.txtEntropy);
        
        txtMean.setText(String.valueOf(formattedDouble(histG.getMean())));
        txtMedian.setText(String.valueOf(formattedDouble(histG.getMedian())));
        txtMode.setText(String.valueOf(formattedDouble(histG.getMode())));
        txtMin.setText(String.valueOf(formattedDouble(histG.getMin())));
        txtMax.setText(String.valueOf(formattedDouble(histG.getMax())));
        txtStdDev.setText(String.valueOf(formattedDouble(histG.getStdDev())));
        txtEntropy.setText(String.valueOf(formattedDouble(histG.getEntropy())));
        
    }
    
    public double formattedDouble(double value){
        DecimalFormat df = new DecimalFormat("###.##");
        return Double.valueOf(df.format(value));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
}
