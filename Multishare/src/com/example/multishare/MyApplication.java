package com.example.multishare;

import org.brickred.socialauth.android.SocialAuthAdapter;

import android.app.Application;

public class MyApplication extends Application {
	
	private SocialAuthAdapter socialAuthAdapter;
	
	public SocialAuthAdapter getSocialAuthAdapter(){
		return socialAuthAdapter;
	}

	public void setSocialAuthAdapter (SocialAuthAdapter socialAuthAdapter){
		this.socialAuthAdapter = socialAuthAdapter;
	}
}
