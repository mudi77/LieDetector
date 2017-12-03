package com.example.polygraf1;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;

public class Polygraf1Activity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.intro_pic);
		
	new Handler().postDelayed(new Thread(){
		@Override
		public void run(){
			Intent menuM = new Intent(Polygraf1Activity.this, Polygraf1_Main_Menu.class);
			Polygraf1Activity.this.startActivity(menuM);
			Polygraf1Activity.this.finish();
			overridePendingTransition(R.layout.fade_in, R.layout.fade_out);
			
		}			
	}, 2000);	
			
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		//Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.polygraf1, menu);
		return true;
	}

}
