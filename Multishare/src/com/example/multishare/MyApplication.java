/**
 * Authors: Alshahna Jamal, Keval Shah, David Hu, Hao Yang
 */
package com.example.multishare;

import org.brickred.socialauth.android.SocialAuthAdapter;

import android.app.Application;

/**
 * This class is used to get and set the adapters
 * for this specific application. 
 * 
 * Linkedin and Twitter are made compatible for Multishare
 * through the use of the SocialAuthAdapter SDK. They both
 * have their own adapters through here so that users can control
 * posting to one, both or neither.
 *
 */

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
