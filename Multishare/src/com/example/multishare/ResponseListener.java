package com.example.multishare;

import org.brickred.socialauth.android.SocialAuthAdapter;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.widget.Toast;

import com.facebook.android.DialogError;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;

public class ResponseListener implements DialogListener, Parcelable {
	
	private SocialAuthAdapter adapter;
	

	
	ResponseListener(SocialAuthAdapter adapter) {
		this.adapter = adapter;
	}
	
	@Override
	public void onComplete(Bundle values) {
		// Variable to receive message status
		Log.d("Share-Bar", "Authentication Successful");

		// Get name of provider after authentication
		final String providerName = values.getString(SocialAuthAdapter.PROVIDER);
		Log.d("Share-Bar", "Provider Name = " + providerName);
		

		// Please avoid sending duplicate message. Social Media Providers
		// block duplicate messages.

	}
	

	@Override
	public void onCancel() {
		Log.d("Share-Bar", "Authentication Cancelled");
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onFacebookError(FacebookError e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onError(DialogError e) {
		// TODO Auto-generated method stub
		
	}


}
