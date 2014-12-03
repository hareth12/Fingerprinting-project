package com.example.fingerprint;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Gallery;
import android.widget.ImageView;

public class ProcessActivity extends Activity {
    Binarizer img = new Binarizer();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_process);
		

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.process, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void scanFinger(View view) {
		Intent intent = new Intent (this, CameraActivity.class) ;
		startActivity(intent) ;
		
	}

    public void binarizer(View view) {
        Log.d("binarizer", "entering test things");
        img.testThings();
        Log.d("binarizer", "got past test things");
    }

    public void show_image(View view) {
        //Create Binarizer x. Default constructor uses test.jpg in res.
        ImageView display = (ImageView) findViewById(R.id.imageview);
        display.setImageBitmap(img.getBitmap());
    }
    @Override
    public void onBackPressed(){
        Intent intent = new Intent(ProcessActivity.this, ProcessActivity.class) ;
        startActivity(intent) ;
    }

}