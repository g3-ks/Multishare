/**
 * Authors: Alshahna Jamal, Keval Shah, David Hu, Hao Yang
 */
package com.example.multishare;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import org.brickred.socialauth.android.DialogListener;
import org.brickred.socialauth.android.SocialAuthAdapter;
import org.brickred.socialauth.android.SocialAuthAdapter.Provider;
import org.brickred.socialauth.android.SocialAuthError;
import org.json.JSONObject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;

public class AddAccountActivity extends ActionBarActivity {

	//----------------Twitter Constant-----
	
	static String TWITTER_CONSUMER_KEY = "wuF5iNzZ8qgTTtkCggaisw"; // place your cosumer key here
	static String TWITTER_CONSUMER_SECRET = "r0ydU55XMSCO3QnwAZBdMglYHVKMZ3PtV10jc6Eo8"; // place your consumer secret here

	// Preference Constants
	static String PREFERENCE_NAME = "twitter_oauth";
	static final String PREF_KEY_OAUTH_TOKEN = "oauth_token";
	static final String PREF_KEY_OAUTH_SECRET = "oauth_token_secret";
	static final String PREF_KEY_TWITTER_LOGIN = "isTwitterLogedIn";

	static final String TWITTER_CALLBACK_URL = "oauth://t4jsample";

	// Twitter oauth urls
	static final String URL_TWITTER_AUTH = "auth_url";
	static final String URL_TWITTER_OAUTH_VERIFIER = "oauth_verifier";
	static final String URL_TWITTER_OAUTH_TOKEN = "oauth_token";

	// Login button
	Button btnLoginTwitter;
	// Update status button
	Button btnUpdateStatus;
	// Logout button
	Button btnLogoutTwitter;
	// EditText for update
	EditText txtUpdate;
	// lbl update
	TextView lblUpdate;
	TextView lblUserName;
	
	// Shared Preferences
	private static SharedPreferences mSharedPreferences;
	
	
	// Variable Declaration
	private LoginButton authButton;

	private Button twitter_button;
	
	private Button linked_in_button;
	
	private Button signOut_twitter;
	
	private Button signOut_linkedin;

	private TextView user_name;

	private GraphUser user;

	private UiLifecycleHelper uiHelper;

	private static final String TAG = "debug";

	private SocialAuthAdapter twitter_adapter;

	private SocialAuthAdapter linked_in_adapter;

	MyApplication myApp;

	// Callback from Facebook Login
	private Session.StatusCallback callback = new Session.StatusCallback() {
		@Override
		public void call(Session session, SessionState state,
				Exception exception) {
			onSessionStateChange(session, state, exception);
		}
	};

	// Check if Facebook is logged in
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
		PackageInfo info;
		try {
			info = getPackageManager().getPackageInfo("com.example.multishare",
					PackageManager.GET_SIGNATURES);
			
			for (Signature signature : info.signatures) {
				MessageDigest md;
				md = MessageDigest.getInstance("SHA");
				md.update(signature.toByteArray());
				String something = new String(Base64.encode(md.digest(), 0));
				Log.e("hash key", something);
			}
			
		} catch (NameNotFoundException e1) {
			Log.e("name not found", e1.toString());
		} catch (NoSuchAlgorithmException e) {
			Log.e("no such an algorithm", e.toString());
		} catch (Exception e) {
			Log.e("exception", e.toString());
		}

		setContentView(R.layout.add_account_layout);

		// Sets action bar's colour to colour defined in ACTION_BAR_COLOUR
		getSupportActionBar().setBackgroundDrawable(
				new ColorDrawable(MainActivity.ACTION_BAR_COLOUR));

		// facebook login set up using the FaceBook SDK
		authButton = (LoginButton) findViewById(R.id.authButton);
		user_name = (TextView) findViewById(R.id.user_name);
		authButton.setReadPermissions(Arrays.asList("email"));
		authButton
				.setUserInfoChangedCallback(new LoginButton.UserInfoChangedCallback() {
					@Override
					public void onUserInfoFetched(GraphUser user) {
						AddAccountActivity.this.user = user;
						updateUI();
					}
				});

		// Twitter login set up through the SocialAuthAdapter SDK
		twitter_adapter = new SocialAuthAdapter(new ResponseListener());
		twitter_adapter.addProvider(Provider.TWITTER, R.drawable.twitter);
		signOut_twitter = (Button) findViewById(R.id.sign_out_button_twitter);
		signOut_twitter.setEnabled(false);
		signOut_twitter.setVisibility(View.INVISIBLE);

		// Linkedin login set up through the SocialAuthAdapter SDK
		linked_in_adapter = new SocialAuthAdapter(new ResponseListener());
		linked_in_adapter.addProvider(Provider.LINKEDIN, R.drawable.linkedin);
		signOut_linkedin = (Button) findViewById(R.id.sign_out_button_linked_in);
		signOut_linkedin.setEnabled(false);
		signOut_linkedin.setVisibility(View.INVISIBLE);
		
		// Adding our team's Twitter developer info
		try {
			twitter_adapter.addConfig(Provider.TWITTER,
					"wuF5iNzZ8qgTTtkCggaisw",
					"r0ydU55XMSCO3QnwAZBdMglYHVKMZ3PtV10jc6Eo8", null);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Adding our team's LinkedIn developer info
		try {
			linked_in_adapter.addConfig(Provider.LINKEDIN, "bh82t52rdos6",
					"zQ1LLrGbhDZ36fH8", null);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Adding twitter and linkedin buttons
		twitter_button = (Button) findViewById(R.id.twitter_button);
		linked_in_button = (Button) findViewById(R.id.linked_in_button);

		twitter_adapter.enable(twitter_button);
		linked_in_adapter.enable(linked_in_button);

	}

	// Displaying Facebook Login name
	private void updateUI() {
		Session session = Session.getActiveSession();

		if (user != null) {
			user_name.setText(user.getName());
		} else {
			user_name.setText(null);
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		Session session = Session.getActiveSession();
		if (session != null && (session.isOpened() || session.isClosed())) {
			onSessionStateChange(session, session.getState(), null);
		}

		uiHelper.onResume();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		uiHelper.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onPause() {
		super.onPause();
		uiHelper.onPause();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		uiHelper.onDestroy();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		uiHelper.onSaveInstanceState(outState);

	}

	/**
	 * This function returns the loggin in facebook user and 
	 * session to the next invoked activity: Main Activity
	 */
	public void returnSession(View view) {
		
		Intent intent = new Intent(this, MainActivity.class);
		Bundle bundle = new Bundle();
		if (user != null) {
			Log.d(TAG, "user is not null - returnSession");
			JSONObject jsonObj = user.getInnerJSONObject();
			String jsonString = jsonObj.toString();
			bundle.putString("user", jsonString);
			intent.putExtras(bundle);
		} else {
			Log.d(TAG, "user is  null - returnSession");
		}

		startActivity(intent);

	}

	/**
	 * Class takes in the configuration of Linkedin and 
	 * Twitter and completes the login process
	 *
	 */
	private final class ResponseListener implements DialogListener {
		@Override
		public void onComplete(Bundle values) {

			// Variable to receive message status
			Log.d("Share-Bar", "Authentication Successful");

			// Get name of provider after authentication
			final String providerName = values
					.getString(SocialAuthAdapter.PROVIDER);
			Log.d("Share-Bar", "Provider Name = " + providerName);
			Toast.makeText(AddAccountActivity.this,
					providerName + " connected", Toast.LENGTH_SHORT).show();
			
			if(providerName.equals("twitter")){
				signOut_twitter.setEnabled(true);
				signOut_twitter.setVisibility(View.VISIBLE);

			} else {
				signOut_linkedin.setEnabled(true);
				signOut_linkedin.setVisibility(View.VISIBLE);
			}

			myApp = (MyApplication) getApplication();
			myApp.setTwitterAdapter(twitter_adapter);
			myApp.setLinkedInAdapter(linked_in_adapter);

			// Please avoid sending duplicate message. Social Media Providers
			// block duplicate messages.

		}

		@Override
		public void onError(SocialAuthError error) {
			error.printStackTrace();
			Log.d("Share-Bar", error.getMessage());
		}

		@Override
		public void onCancel() {
			Log.d("Share-Bar", "Authentication Cancelled");
		}

		@Override
		public void onBack() {
			Log.d("Share-Bar", "Dialog Closed by pressing Back Key");

		}

	}

	/**
	 * Logs out user from linkedin and hides sign out
	 * button in the UI
	 */
	public void linkedInSignOut(View view) {
		linked_in_adapter.signOut(myApp, "linkedin");
		signOut_linkedin.setEnabled(false);
		signOut_linkedin.setVisibility(View.INVISIBLE);
		Toast.makeText(this, "Signed out from LinkedIn", Toast.LENGTH_SHORT)
				.show();
	}


	/**
	 * Logs out user from twitter and hides sign out
	 * button in the UI
	 */

	public void twitterSignOut(View view) {
		twitter_adapter.signOut(myApp, "twitter");
		signOut_twitter.setEnabled(false);
		signOut_twitter.setVisibility(View.INVISIBLE);
		Toast.makeText(this, "Signed out from Twitter", Toast.LENGTH_SHORT)
				.show();
	}

}
