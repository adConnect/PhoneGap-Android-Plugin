package com.adcnx.demo.phonegap;

import org.apache.cordova.DroidGap;

import com.android.adcnx.adlib.Ad;
import com.android.adcnx.adlib.AdBlock;
import com.android.adcnx.adlib.AdListener;
import com.android.adcnx.adlib.AdRequest;
import com.android.adcnx.adlib.AdRequest.ErrorCode;
import com.android.adcnx.adlib.AdSize;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnLayoutChangeListener;
import android.webkit.WebView;
import android.widget.FrameLayout;

public class LibraryPhoneGapAndroidDemoActivity extends DroidGap 
{
   
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        
        //super.setIntegerProperty("loadUrlTimeoutValue", 60000);
        super.loadUrl("file:///android_asset/www/index.html");
    }
}