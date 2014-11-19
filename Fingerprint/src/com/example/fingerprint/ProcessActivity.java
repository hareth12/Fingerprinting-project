package com.example.fingerprint;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Gallery;

public class ProcessActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_process);
        //Binarizer x = new Binarizer();
        //ImageView IV = (ImageView) findViewById(R.id.imageView);


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
        
    }
    public void show_image(View view){
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Binarizer binarizer = new Binarizer();
        Uri androidUri = android.net.Uri.parse( binarizer.openImage().getAbsoluteFile().toURI().toString());
        intent.setDataAndType(androidUri, "image/jpeg");
        startActivity(intent);
    }

}