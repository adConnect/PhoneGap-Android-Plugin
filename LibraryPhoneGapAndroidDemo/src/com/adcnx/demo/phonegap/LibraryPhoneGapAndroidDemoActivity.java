package com.adcnx.demo.phonegap;

import org.apache.cordova.DroidGap;

import android.os.Bundle;

public class LibraryPhoneGapAndroidDemoActivity extends DroidGap 
{
   
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        super.loadUrl("file:///android_asset/www/index.html");
    }
}