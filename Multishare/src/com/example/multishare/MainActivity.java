package com.example.multishare;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends ActionBarActivity{
	
	public static final int ACTION_BAR_COLOUR = 0xFF3F9FE0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_layout);
		
		// Sets action bar's colour to colour defined in ACTION_BAR_COLOUR 
		getSupportActionBar().setBackgroundDrawable(new ColorDrawable(ACTION_BAR_COLOUR));
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    // Inflate the menu items for use in the action bar
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.menu, menu);
	    return super.onCreateOptionsMenu(menu);
	}

	
	/**
	 * Function called when "Preview" button is pressed
	 * @param view
	 */
	public void previewMessage(View view){
		EditText editText1 = (EditText) findViewById(R.id.editText1);
		String message = editText1.getText().toString();
		
		// Starting PreviewActivity and passing a string
		Intent intent = new Intent(this, PreviewActivity.class);
		intent.putExtra("statusUpdate", message);
		startActivity(intent);
	}
	
	public boolean addAccount(MenuItem item) {
		Intent intent = new Intent(this, AddAccountActivity.class);
		startActivity(intent);
		return true;
	}

}
