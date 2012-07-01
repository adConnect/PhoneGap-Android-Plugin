package com.android.adcnx.adlib.plugin;

import org.apache.cordova.api.PluginResult;
import org.json.JSONArray;

import com.android.adcnx.adlib.AdBlock;
import com.phonegap.api.Plugin;

public class AdConnectLibrary extends Plugin
{	
	private static AdBlock _lib;
	
	@Override
	public PluginResult execute(String action, JSONArray data, String callbackID)
	{
		PluginResult result = null;
		System.out.println("balls!");
		webView.loadUrl("http://google.com");
		
		
		
		return result;
	}

}
