package com.example.multishare;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import org.brickred.socialauth.android.DialogListener;
import org.brickred.socialauth.android.SocialAuthAdapter;
import org.brickred.socialauth.android.SocialAuthAdapter.Provider;
import org.brickred.socialauth.android.SocialAuthError;
import org.json.JSONObject;

import twitter4j.Twitter;
import twitter4j.auth.RequestToken;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.os.Bundle;
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
import com.facebook.widget.ProfilePictureView;

public class AddAccountActivity extends Activity {

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

	// Progress dialog
	ProgressDialog pDialog;

	// Twitter
	private static Twitter twitter;
	private static RequestToken requestToken;
	
	// Shared Preferences
	private static SharedPreferences mSharedPreferences;
	
	
	//-------------------------------------
	
	private LoginButton authButton;
	
	private ProfilePictureView profile_picture;
	
	Button twitter_button;
	
	private TextView user_name;
	
	private GraphUser user;
	
	private UiLifecycleHelper uiHelper;
		
	private static final String TAG = "debug";
	
	private SocialAuthAdapter adapter;
	
	MyApplication myApp;
	
	private Session.StatusCallback callback = new Session.StatusCallback() {
	    @Override
	    public void call(Session session, SessionState state, Exception exception) {
	        onSessionStateChange(session, state, exception);
	    }
	};
	
	private void onSessionStateChange(Session session, SessionState state, Exception exception) {
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
		    info = getPackageManager().getPackageInfo("com.example.multishare", PackageManager.GET_SIGNATURES);
		    for (Signature signature : info.signatures) {
		        MessageDigest md;
		        md = MessageDigest.getInstance("SHA");
		        md.update(signature.toByteArray());
		        String something = new String(Base64.encode(md.digest(), 0));
		        //String something = new String(Base64.encodeBytes(md.digest()));
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
		
		//facebook stuff
		authButton = (LoginButton) findViewById(R.id.authButton);
		//profile_picture = (ProfilePictureView) findViewById(R.id.profilePicture);		
		user_name = (TextView)findViewById(R.id.user_name);	
		authButton.setReadPermissions(Arrays.asList("email"));
		authButton.setUserInfoChangedCallback(new LoginButton.UserInfoChangedCallback() {
			@Override
            public void onUserInfoFetched(GraphUser user) {
                AddAccountActivity.this.user = user;
                updateUI();
            }
        });
		
		//Twitter stuff
		adapter = new SocialAuthAdapter(new ResponseListener());
		adapter.addProvider(Provider.TWITTER, R.drawable.twitter);
		adapter.addCallBack(Provider.TWITTER, "http://google.com");
	
		try {
			adapter.addConfig(Provider.TWITTER, "wuF5iNzZ8qgTTtkCggaisw", "r0ydU55XMSCO3QnwAZBdMglYHVKMZ3PtV10jc6Eo8", null);
		} catch(Exception e) {
			e.printStackTrace();
		}
		twitter_button = (Button)findViewById(R.id.twitter_button);
		adapter.enable(twitter_button);
		
		
		
		
		
	}
	
	 private void updateUI() {
	        Session session = Session.getActiveSession();
	       
	        if (user != null) {
	            user_name.setText(user.getName());
	            //profile_picture.setProfileId(user.getId());
	        } else {
	            user_name.setText(null);
	        }
	    }

	
	@Override
	public void onResume() {
	    super.onResume();
	    Session session = Session.getActiveSession();
	    if (session != null &&
	           (session.isOpened() || session.isClosed()) ) {
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
	
	public void returnSession(View view) {
		//Session session = Session.getActiveSession();
		Intent intent = new Intent(this, MainActivity.class);
		Bundle bundle = new Bundle();
		if(user != null) {
			Log.d(TAG, "user is not null - returnSession");
			JSONObject jsonObj = user.getInnerJSONObject();
			String jsonString = jsonObj.toString();
			bundle.putString("user", jsonString);
			intent.putExtras(bundle);
		}
		else {
			Log.d(TAG, "user is  null - returnSession");
		}
			
		


		startActivity(intent);
	       
	}
	
	private final class ResponseListener implements DialogListener {
		@Override
		public void onComplete(Bundle values) {

			// Variable to receive message status
			Log.d("Share-Bar", "Authentication Successful");

			// Get name of provider after authentication
			final String providerName = values.getString(SocialAuthAdapter.PROVIDER);
			Log.d("Share-Bar", "Provider Name = " + providerName);
			Toast.makeText(AddAccountActivity.this, providerName + " connected", Toast.LENGTH_SHORT).show();

			myApp = (MyApplication) getApplication();
			myApp.setSocialAuthAdapter(adapter);
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
	
	
	
	

}
