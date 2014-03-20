package com.example.multishare;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class PreviewActivity extends Activity{

	String facebookString;
	String twitterString;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.preview_layout);
		Intent previewIntent = new Intent();
		previewIntent = getIntent();
		facebookString = previewIntent.getStringExtra("statusUpdate");
		previewStatus();
	}

	public void previewStatus(){
		TextView facebookStatus = (TextView)  findViewById(R.id.facebookStatus);
		facebookStatus.setText(facebookString);
	}
	
}
