package com.example.multishare;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_layout);
	}
	
	public void shareMessage(View view){
		EditText editText1 = (EditText) findViewById(R.id.editText1);
		String message = editText1.getText().toString();
		EditText editText2 = (EditText) findViewById(R.id.editText2);
		editText2.setText(message);
	}
	
	
	
}
