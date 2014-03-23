package com.example.multishare;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphObject;
import com.facebook.model.GraphUser;

public class PreviewActivity extends ActionBarActivity {

	String facebookString;
	String twitterString;
	private GraphUser user;
	private static String TAG = "debug";
	private Session session;

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
		
		if((session = Session.getActiveSession()) == null) {
			Log.d(TAG, "session is null - on create");
		}
		
	}

	/**
	 * Display user's message on screen
	 */
	public void previewStatus() {
		TextView facebookStatus = (TextView) findViewById(R.id.facebookStatus);
		facebookStatus.setText(facebookString);
	}
	
	public void shareMessage(View view) {
		Intent intent = new Intent(this, MainActivity.class);
		Session session = Session.getActiveSession();
		
		if(session != null) {
			Log.d(TAG, "Session is not null");
			postStatusUpdate();
			Toast.makeText(this, "Post successful", Toast.LENGTH_SHORT).show();
			startActivity(intent);
		} else {
			Toast.makeText(this, "Post unsuccessful", Toast.LENGTH_SHORT).show();
			startActivity(intent);
		}
	}

	private void postStatusUpdate() {
		Session.openActiveSession(this, true, new Session.StatusCallback() {
			public void call(Session session, SessionState state, Exception exception) {
				if(session.isOpened()) {
					Request.newMeRequest(session, new Request.GraphUserCallback() {
						
						@Override
						public void onCompleted(GraphUser user, Response response) {
							if(user != null && hasPublishedPermission()) {
								Log.d(TAG, "User is  not null");
								Request request = Request
										.newStatusUpdateRequest(Session.getActiveSession(), facebookString, null, null, new Request.Callback() {
											
											@Override
											public void onCompleted(Response response) {
												//showPublishResult(facebookString, response.getGraphObject(), response.getError());
				
											}
										});
								request.executeAsync();
							}
							else {
								Log.d(TAG, "User is null");
							}
						}
					});
				} else {
					Log.d(TAG, "Session is closed");
				}
			}
		});
		
	}
			
	private boolean hasPublishedPermission() {
		Session session = Session.getActiveSession();
		return session != null && session.getPermissions().contains("publish_actions");
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		if((session = Session.getActiveSession()) == null) {
			Log.d(TAG, "session is null - on restart");
		}
	}
	
	
	
//	private interface GraphObjectWithId extends GraphObject {
//		String getId();
//	}
//	
//    private void showPublishResult(String message, GraphObject result, FacebookRequestError error) {
//        String title = null;
//        String alertMessage = null;
//        if (error == null) {
//            title = getString(R.string.success);
//            String id = result.cast(GraphObjectWithId.class).getId();
//            alertMessage = getString(R.string.successfully_posted_post, message, id);
//        } else {
//            title = getString(R.string.error);
//            alertMessage = error.getErrorMessage();
//        }
//
//        new AlertDialog.Builder(this)
//                .setTitle(title)
//                .setMessage(alertMessage)
//                .setPositiveButton(R.string.ok, null)
//                .show();
//    }

}
