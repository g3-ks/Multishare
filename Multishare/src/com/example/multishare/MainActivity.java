package com.example.multishare;

import org.brickred.socialauth.android.DialogListener;
import org.brickred.socialauth.android.SocialAuthAdapter;
import org.brickred.socialauth.android.SocialAuthAdapter.Provider;
import org.brickred.socialauth.android.SocialAuthError;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import com.facebook.Request;
import com.facebook.Request.GraphUserCallback;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphObject;
import com.facebook.model.GraphUser;

public class MainActivity extends ActionBarActivity{
	
	public static final int ACTION_BAR_COLOUR = 0xFF3F9FE0;

	private static final String TAG = "debug";
	
	private Session session;
	
	private GraphUser user;
	
	private SocialAuthAdapter adapter;
	
	protected CheckBox postToFacebook;
	
	private CheckBox postToTwitter;
	
	private CheckBox postToLinkedIn;
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_layout);
		
		postToFacebook = (CheckBox) this.findViewById(R.id.checkBoxFacebook);
		postToTwitter = (CheckBox) this.findViewById(R.id.checkBoxTwitter);
		postToLinkedIn = (CheckBox) this.findViewById(R.id.checkBoxLinkedIn);
		
		session = Session.getActiveSession();
		if(session != null) {	
			if(session.isOpened()) {
				Log.d(TAG, "Session is opened - MA - onCreaete");
				 Request request =  Request.newMeRequest(session, new GraphUserCallback() {       
                     public void onCompleted(GraphUser user, Response response) {                                
                         if (user != null) {
                        	 Log.d(TAG, "setting this user in oncreate MainAct");
                            MainActivity.this.user = user;
                         }
                         else {
                        	 Log.d(TAG, "user is null oncreate MA");
                         }
                     }
                 });
         Request.executeBatchAsync(request);
			}
		}		
		
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

	
	
	
	@Override
	protected void onResume() {
		super.onResume();
		Intent intent = getIntent();
		Log.d(TAG, "onResume");
		//Bundle bundle = new Bundle();
		Bundle bundle = intent.getExtras();
		if(bundle != null) {
			Log.d(TAG, "bundle is not null");
			String jsonString = bundle.getString("user");
			try {
				JSONObject jsonObj = new JSONObject(jsonString);
				user = GraphObject.Factory.create(jsonObj, GraphUser.class);
				
			} catch (JSONException e) {
					e.printStackTrace();
			}
		}
		else {
			Log.d(TAG, "bundle is null");	
		}
		//Twitter stuff
		

		
		
	}
	
	


	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
	}


	/**
	 * Function called when "Preview" button is pressed
	 * @param view
	 */
	public void previewMessage(View view){
		EditText editText1 = (EditText) findViewById(R.id.editText1);
		String message = editText1.getText().toString();
		Bundle bundle = new Bundle();
		
		// Starting PreviewActivity and passing a string
		Intent intent = new Intent(this, PreviewActivity.class);
		if(user != null) {
			JSONObject jsonObj = user.getInnerJSONObject();
			String jsonString = jsonObj.toString();
			
			bundle.putString("user", jsonString);
			
		}		
		
		// Attach booleans from checkboxes
		bundle.putBoolean("FacebookEnable", postToFacebook.isChecked());
		Log.d(TAG,"Value of postToFacebook in MainActivity");
		Log.d(TAG,""+postToFacebook.isChecked());
		
		bundle.putBoolean("TwitterEnable", postToTwitter.isChecked());
		Log.d(TAG,"Value of postToTwitter in MainActivity");
		Log.d(TAG,""+postToTwitter.isChecked());
		
		bundle.putBoolean("LinkedInEnable", postToLinkedIn.isChecked());
		Log.d(TAG,"Value of postToLinkedIn in MainActivity");
		Log.d(TAG,""+postToLinkedIn.isChecked());
		
		//intent.putExtra("statusUpdate", message);
		//intent.putExtra("facebook_sess", this.session);
		bundle.putString("statusUpdate", message);
		intent.putExtras(bundle);
		startActivity(intent);

	}
	
	public boolean addAccount(MenuItem item) {
		Intent intent = new Intent(this, AddAccountActivity.class);
		startActivity(intent);
		return true;
	}
	
	

}
