package com.example.multishare;

import org.brickred.socialauth.android.SocialAuthAdapter;
import org.brickred.socialauth.android.SocialAuthError;
import org.brickred.socialauth.android.SocialAuthListener;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AppEventsLogger;
import com.facebook.FacebookAuthorizationException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.FacebookRequestError;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphObject;
import com.facebook.model.GraphUser;
import com.facebook.widget.FacebookDialog;

public class PreviewActivity extends ActionBarActivity {

	String status_update_msg;
	private GraphUser facebook_user;
	private static String TAG = "debug";
	private boolean canPresentShareDialog;
	private static final String PERMISSION = "publish_actions";
	private SocialAuthAdapter twitter_adapter;
	private SocialAuthAdapter linkedIn_adapter;

	private UiLifecycleHelper uiHelper;
	
	private Boolean FacebookEnable;
	private Boolean TwitterEnable;
	private Boolean LinkedInEnable;
	
	private enum PendingAction {
		NONE, POST_PHOTO, POST_STATUS_UPDATE
	}

	private Session.StatusCallback callback = new Session.StatusCallback() {
		@Override
		public void call(Session session, SessionState state,
				Exception exception) {
			onSessionStateChange(session, state, exception);
		}
	};

	private void onSessionStateChange(Session session, SessionState state,
			Exception exception) {
		if (pendingAction != PendingAction.NONE
				&& (exception instanceof FacebookOperationCanceledException || exception instanceof FacebookAuthorizationException)) {
			new AlertDialog.Builder(PreviewActivity.this)
					.setTitle(R.string.cancelled)
					.setMessage(R.string.permission_not_granted)
					.setPositiveButton(R.string.ok, null).show();
			pendingAction = PendingAction.NONE;
		} else if (state == SessionState.OPENED_TOKEN_UPDATED) {
			handlePendingAction();
		}
	}

	private PendingAction pendingAction = PendingAction.NONE;
	private MyApplication myapp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		uiHelper = new UiLifecycleHelper(this, callback);
		uiHelper.onCreate(savedInstanceState);

		setContentView(R.layout.preview_layout);

		// Get string sent from MainActivity
		Intent previewIntent = new Intent();
		previewIntent = getIntent();
		Bundle bundle = previewIntent.getExtras();
		if (bundle != null) {
			Log.d(TAG, "bundle is not null");
			String jsonString = bundle.getString("user");
			
			FacebookEnable = bundle.getBoolean("FacebookEnable");
			Log.d(TAG,"Value of postToFacebook in MainActivity");
			Log.d(TAG,""+FacebookEnable);
			
			TwitterEnable = bundle.getBoolean("TwitterEnable");
			Log.d(TAG,"Value of postToFacebook in MainActivity");
			Log.d(TAG,""+TwitterEnable);
			
			LinkedInEnable = bundle.getBoolean("LinkedInEnable");
			Log.d(TAG,"Value of postToFacebook in MainActivity");
			Log.d(TAG,""+LinkedInEnable);
			
			try {
				if(jsonString != null) {
				JSONObject jsonObj = new JSONObject(jsonString);
				facebook_user = GraphObject.Factory.create(jsonObj, GraphUser.class);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

			status_update_msg = bundle.getString("statusUpdate");
		} else {
			Log.d(TAG, "bundle is null");

		}
		// session = (Session)
		// previewIntent.getSerializableExtra("facebook_sess");
		// Session.setActiveSession(session);

		previewStatus();

		// Sets action bar's colour to colour defined in ACTION_BAR_COLOUR
		getSupportActionBar().setBackgroundDrawable(
				new ColorDrawable(MainActivity.ACTION_BAR_COLOUR));

		canPresentShareDialog = FacebookDialog.canPresentShareDialog(this,
				FacebookDialog.ShareDialogFeature.SHARE_DIALOG);

		// sanity check
		if (Session.getActiveSession() == null) {
			Log.d(TAG, "session is null - on create");
		}

		myapp = (MyApplication) getApplication();
		twitter_adapter = myapp.getTwitterAdapter();
		linkedIn_adapter = myapp.getLinkedInAdapter();
	}

	/**
	 * Display user's message on screen
	 */
	public void previewStatus() {
		TextView facebookStatus = (TextView) findViewById(R.id.facebookStatus);
		facebookStatus.setText(status_update_msg);
	}

	public void shareMessage(View view) {
		Intent intent = new Intent(this, MainActivity.class);
		Session session = Session.getActiveSession();
		Bundle bundle = new Bundle();

		if (session != null && FacebookEnable == true ) {
			Log.d(TAG, "Session is not null");
			performPublish(PendingAction.POST_STATUS_UPDATE,
					canPresentShareDialog);
			Toast.makeText(this, "Mesasage posted on Facebook",
					Toast.LENGTH_SHORT).show();
			bundle.putBoolean("SessionNullCheck", false);
			if (facebook_user != null) {
				Log.d(TAG, "user is not null - returnSession");
				JSONObject jsonObj = facebook_user.getInnerJSONObject();
				String jsonString = jsonObj.toString();
				bundle.putBoolean("UserNullCheck", false);
				bundle.putString("user", jsonString);

			} else {
				bundle.putBoolean("UserNullCheck", true);
				Log.d(TAG, "user is  null - returnSession");
			}

		} if(session == null) {
			Toast.makeText(this, "Facebok post unsuccessful",
					Toast.LENGTH_SHORT).show();
			bundle.putBoolean("SessionNullCheck", true);
		}
		if(twitter_adapter != null && TwitterEnable == true) {
			twitter_adapter.updateStatus(status_update_msg, new MessageListener(), false);
		}

		if(linkedIn_adapter != null && LinkedInEnable == true) {
			linkedIn_adapter.updateStatus(status_update_msg, new MessageListener(), false);
		}
		bundle.putBoolean("FromPreviewActivity", true);
		intent.putExtras(bundle);
		startActivity(intent);
	}

	private void performPublish(PendingAction action, boolean allowNoSession) {
		Session session = Session.getActiveSession();
		if (session != null) {
			pendingAction = action;
			if (hasPublishPermission()) {
				Log.d(TAG, "Handled published permissions");
				// We can do the action right away.
				handlePendingAction();
				return;
			} else if (session.isOpened()) {
				// We need to get new permissions, then complete the action when
				// we get called back.
				Log.d(TAG,
						"Session already opened - create new publish permissions");
				session.requestNewPublishPermissions(new Session.NewPermissionsRequest(
						this, PERMISSION));
				handlePendingAction();
				return;
			}
		}

		if (allowNoSession) {
			pendingAction = action;
			handlePendingAction();
		}
	}

	private boolean hasPublishPermission() {
		Session session = Session.getActiveSession();
		return session != null
				&& session.getPermissions().contains("publish_actions");
	}

	@SuppressWarnings("incomplete-switch")
	private void handlePendingAction() {
		PendingAction previouslyPendingAction = pendingAction;
		// These actions may re-set pendingAction if they are still pending, but
		// we assume they
		// will succeed.
		pendingAction = PendingAction.NONE;

		switch (previouslyPendingAction) {
		case POST_PHOTO:
			// postPhoto();
			break;
		case POST_STATUS_UPDATE:
			Log.d(TAG, "Post status update gonna happen");
			postStatusUpdate();
			break;
		}
	}

	private FacebookDialog.Callback dialogCallback = new FacebookDialog.Callback() {
		@Override
		public void onError(FacebookDialog.PendingCall pendingCall,
				Exception error, Bundle data) {
			Log.d("HelloFacebook", String.format("Error: %s", error.toString()));
		}

		@Override
		public void onComplete(FacebookDialog.PendingCall pendingCall,
				Bundle data) {
			Log.d("HelloFacebook", "Success!");
		}
	};

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		uiHelper.onActivityResult(requestCode, resultCode, data, dialogCallback);
	}

	private void postStatusUpdate() {
		if (canPresentShareDialog) {
			Log.d(TAG, "share dialog is true");
			FacebookDialog shareDialog = createShareDialogBuilder().build();
			uiHelper.trackPendingDialogCall(shareDialog.present());

		}
		if (facebook_user != null && hasPublishPermission()) {
			Log.d(TAG, "user is not null and permission good");
			final String message = status_update_msg;
			Request request = Request.newStatusUpdateRequest(
					Session.getActiveSession(), message, null, null,
					new Request.Callback() {
						@Override
						public void onCompleted(Response response) {
							showPublishResult(message,
									response.getGraphObject(),
									response.getError());
						}
					});
			request.executeAsync();
		} else {
			Log.d(TAG, "user is null or no permission");
			pendingAction = PendingAction.POST_STATUS_UPDATE;
		}
	}

	private interface GraphObjectWithId extends GraphObject {
		String getId();
	}

	private void showPublishResult(String message, GraphObject result,
			FacebookRequestError error) {
		String title = null;
		String alertMessage = null;
		Log.d(TAG, "within showPublishResult");
		if (error == null) {
			title = getString(R.string.success);
			String id = result.cast(GraphObjectWithId.class).getId();
			alertMessage = getString(R.string.successfully_posted_post,
					message, id);
		} else {
			title = getString(R.string.error);
			alertMessage = error.getErrorMessage();
		}

		new AlertDialog.Builder(this).setTitle(title).setMessage(alertMessage)
				.setPositiveButton(R.string.ok, null).show();
	}

	private FacebookDialog.ShareDialogBuilder createShareDialogBuilder() {
		return new FacebookDialog.ShareDialogBuilder(this)
				.setName("Hello Facebook")
				.setDescription(
						"The 'Hello Facebook' sample application showcases simple Facebook integration")
				.setLink("http://developers.facebook.com/android");
	}

	@Override
	protected void onResume() {
		super.onResume();
		uiHelper.onResume();

		// Call the 'activateApp' method to log an app event for use in
		// analytics and advertising reporting. Do so in
		// the onResume methods of the primary Activities that an app may be
		// launched into.

		// Twitter stuff

		AppEventsLogger.activateApp(this);
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

	private final class MessageListener implements SocialAuthListener<Integer> {
		@Override
		public void onExecute(String provider, Integer t) {
			Integer status = t;
			if (status.intValue() == 200 || status.intValue() == 201
					|| status.intValue() == 204)
				Toast.makeText(PreviewActivity.this,
						"Message posted on " + provider, Toast.LENGTH_LONG)
						.show();
			else
				Toast.makeText(PreviewActivity.this,
						"Message not posted on " + provider, Toast.LENGTH_LONG)
						.show();
		}

		@Override
		public void onError(SocialAuthError e) {

		}
	}

}
