package com.example.multishare;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Base64;
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
