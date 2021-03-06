/**
 * Authors: Alshahna Jamal, Keval Shah, David Hu, Hao Yang
 */
package com.example.multishare;

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
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphObject;
import com.facebook.model.GraphUser;

public class MainActivity extends ActionBarActivity {

	public static final int ACTION_BAR_COLOUR = 0xFF3F9FE0;

	private static final String TAG = "debug";

	private Session facebook_session;

	private GraphUser facebook_user;

	private CheckBox postToFacebook;

	private CheckBox postToTwitter;

	private CheckBox postToLinkedIn;
	
	MyApplication myApp;

	/**
	 *  UIHelper class helps keep track of a facebook session states between activities
	 */
	private UiLifecycleHelper uiHelper;

	private Session.StatusCallback callback = new Session.StatusCallback() {
		@Override
		public void call(Session session, SessionState state,
				Exception exception) {
			onSessionStateChange(session, state, exception);
		}
	};

	private void onSessionStateChange(Session session, SessionState state,
			Exception exception) {
		if (state.isOpened()) {
			Log.i(TAG, "Logged in...");
		} else if (state.isClosed()) {
			Log.i(TAG, "Logged out...");
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState); 
		uiHelper = new UiLifecycleHelper(this, callback);
		uiHelper.onCreate(savedInstanceState);
		Log.d(TAG, "Main activity - oncreate");
		setContentView(R.layout.main_layout);

		// User's way of choosing which social media to post to -- through these checkboxes
		postToFacebook = (CheckBox) this.findViewById(R.id.checkBoxFacebook);
		postToTwitter = (CheckBox) this.findViewById(R.id.checkBoxTwitter);
		postToLinkedIn = (CheckBox) this.findViewById(R.id.checkBoxLinkedIn);

		facebook_session = Session.getActiveSession();
		if (facebook_session != null) {
			if (facebook_session.isOpened()) {
				Log.d(TAG, "Session is opened - MA - onCreaete");
				Request request = Request.newMeRequest(facebook_session,
						new GraphUserCallback() {
							public void onCompleted(GraphUser user,
									Response response) {
								if (user != null) {
									Log.d(TAG,
											"setting this user in oncreate MainAct");
									MainActivity.this.facebook_user = user;
								} else {
									Log.d(TAG, "user is null oncreate MA");
								}
							}
						});
				Request.executeBatchAsync(request);
			}
		}

		// Sets action bar's colour to colour defined in ACTION_BAR_COLOUR
		getSupportActionBar().setBackgroundDrawable(
				new ColorDrawable(ACTION_BAR_COLOUR));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu items for use in the action bar
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
	}

	/*
	 * Overriding this to disable back button for MainActivity
	 */
	@Override
	public void onBackPressed() {
		Bundle bundle = getIntent().getExtras();
		if (bundle == null || !bundle.getBoolean("FromPreviewActivity")) {
			super.onBackPressed();
		}
	}

	@Override
	protected void onResume() {
		
		myApp = (MyApplication)getApplication();
		
		if(myApp.getTwitterAdapter() == null) {
			Log.d(TAG, "Twitter adapter is NULL");
		}
		
		super.onResume();
		uiHelper.onResume();

		// Get the opened session from add Account after an app has been deleted and relaunched
		Session.openActiveSessionFromCache(this);
		facebook_session = Session.getActiveSession();
		if (facebook_session != null) {
			if (facebook_session.isOpened()) {
				Log.d(TAG, "Session is opened - MA - onResume");
				Request request = Request.newMeRequest(facebook_session,
						new GraphUserCallback() {
							public void onCompleted(GraphUser user,
									Response response) {
								if (user != null) {
									Log.d(TAG,
											"setting this user in onResume MainAct");
									MainActivity.this.facebook_user = user;
								} else {
									Log.d(TAG, "user is null onResume MA");
								}
							}
						});
				Request.executeBatchAsync(request);
			}
		}

		// Get the facebook user info from the add account activity
		Intent intent = getIntent();
		Log.d(TAG, "onResume");
		Bundle bundle = intent.getExtras();
		if (bundle != null) {
			Log.d(TAG, "On Resume - bundle is not null");
			String jsonString = bundle.getString("user");
			try {
				if (jsonString != null) {
					Log.d(TAG, "user string not null");
					JSONObject jsonObj = new JSONObject(jsonString);
					facebook_user = GraphObject.Factory.create(jsonObj,
							GraphUser.class);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else {
			Log.d(TAG, "On Resume - bundle is null");
		}
	}

	/**
	 * Function called when "Preview" button is pressed. Sends message user
	 * enters through an intent to PreviewActivity, which is responsible for
	 * sharing it to the selected social media providers.
	 * 
	 * @param view
	 */
	public void previewMessage(View view) {
		EditText user_message = (EditText) findViewById(R.id.editText1);

		// Gets message user enters in and converts it to a string
		String message = user_message.getText().toString();

		Bundle bundle = new Bundle();

		Intent intent = new Intent(this, PreviewActivity.class);

		if (facebook_user != null) {
			// Get JSON representation of the user object
			JSONObject jsonObj = facebook_user.getInnerJSONObject();

			// Convert JSON representation to a string and put into a bundle
			bundle.putString("user", jsonObj.toString());

			// Attach booleans from checkboxes
			bundle.putBoolean("FacebookEnable", postToFacebook.isChecked());
			Log.d(TAG, "Value of postToFacebook in MainActivity");
			Log.d(TAG, "" + postToFacebook.isChecked());

		}

		bundle.putBoolean("TwitterEnable", postToTwitter.isChecked());
		Log.d(TAG, "Value of postToTwitter in MainActivity");
		Log.d(TAG, "" + postToTwitter.isChecked());

		bundle.putBoolean("LinkedInEnable", postToLinkedIn.isChecked());
		Log.d(TAG, "Value of postToLinkedIn in MainActivity");
		Log.d(TAG, "" + postToLinkedIn.isChecked());

		bundle.putString("statusUpdate", message);
		intent.putExtras(bundle);
		startActivity(intent);
	}

	/**
	 * Function called when "Add Account" item on action bar is pressed. Starts
	 * the AddAccount activity for users to sign-in/sign-out of accounts for
	 * different social media platforms.
	 * 
	 * @param item
	 * @return
	 */
	public boolean addAccount(MenuItem item) {
		Intent intent = new Intent(this, AddAccountActivity.class);
		startActivity(intent);
		return true;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		uiHelper.onDestroy();
	}

	@Override
	protected void onPause() {
		super.onPause();
		uiHelper.onPause();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		uiHelper.onSaveInstanceState(outState);
	}

}
