package com.example.multishare;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

public class AddAccountActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Sets action bar's colour to colour defined in ACTION_BAR_COLOUR
		getSupportActionBar().setBackgroundDrawable(new ColorDrawable(MainActivity.ACTION_BAR_COLOUR));
	}
	
}
