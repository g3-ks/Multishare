package com.example.multishare;

import org.brickred.socialauth.android.SocialAuthAdapter;

import android.app.Application;

public class MyApplication extends Application {
	
	private SocialAuthAdapter twitter_adapter;
	private SocialAuthAdapter linkedIn_adapter;
	
	public SocialAuthAdapter getTwitterAdapter(){
		return twitter_adapter;
	}
	
	public SocialAuthAdapter getLinkedInAdapter(){
		return linkedIn_adapter;
	}

	public void setTwitterAdapter(SocialAuthAdapter socialAuthAdapter){
		this.twitter_adapter = socialAuthAdapter;
	}
	
	public void setLinkedInAdapter(SocialAuthAdapter socialAuthAdapter){
		this.linkedIn_adapter = socialAuthAdapter;
	}
}
