package com.example.multishare;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;

public class AddAccountActivity extends FragmentActivity {

	private FacebookAccountFragment fragment;
		
	private static final String TAG = "debug";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (savedInstanceState == null) {
	        // Add the fragment on initial activity setup
	       fragment = new FacebookAccountFragment();
	        getSupportFragmentManager()
	        .beginTransaction()
	        .add(android.R.id.content, fragment)
	        .commit();
	    } else {
	        // Or set the fragment from restored state info
	        fragment = (FacebookAccountFragment) getSupportFragmentManager()
	        .findFragmentById(android.R.id.content);
	    }
		
	}

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		super.onActivityResult(arg0, arg1, arg2);
	}
	
	
	
	
	
	

}
