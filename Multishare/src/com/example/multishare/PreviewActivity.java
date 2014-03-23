package com.example.multishare;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.TextView;

public class PreviewActivity extends ActionBarActivity {

	String facebookString;
	String twitterString;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.preview_layout);

		// Get string sent from MainActivity
		Intent previewIntent = new Intent();
		previewIntent = getIntent();
		facebookString = previewIntent.getStringExtra("statusUpdate");

		previewStatus();

		// Sets action bar's colour to colour defined in ACTION_BAR_COLOUR
		getSupportActionBar().setBackgroundDrawable(new ColorDrawable(MainActivity.ACTION_BAR_COLOUR));

	}

	/**
	 * Display user's message on screen
	 */
	public void previewStatus() {
		TextView facebookStatus = (TextView) findViewById(R.id.facebookStatus);
		facebookStatus.setText(facebookString);
	}

}
