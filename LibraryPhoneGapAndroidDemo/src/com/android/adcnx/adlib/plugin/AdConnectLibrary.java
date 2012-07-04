package com.android.adcnx.adlib.plugin;

import org.apache.cordova.api.PluginResult;
import org.apache.cordova.api.PluginResult.Status;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.widget.FrameLayout.LayoutParams;
import android.widget.LinearLayout;

import com.android.adcnx.adlib.AdBlock;
import com.android.adcnx.adlib.AdRequest;
import com.android.adcnx.adlib.AdRequest.Gender;
import com.android.adcnx.adlib.AdSize;
import com.phonegap.api.Plugin;

public class AdConnectLibrary extends Plugin
{	
	private static final String LOG = "AdConnectLibraryPhoneGapPlugin";
	private static AdBlock _lib = null;
	private String _createCallbackID = null;
	
	private int getGravity(String gravity)
	{
		if(gravity.equalsIgnoreCase("bottom"))
		{
			return Gravity.BOTTOM;
		}
		
		return Gravity.NO_GRAVITY;
	}
	
	private PluginResult sendAsJSON(Status status, String msg)
	{
		JSONObject message = new JSONObject();
		try
		{
			message.put("msg", msg);
		}
		catch (JSONException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			
			return new PluginResult(Status.JSON_EXCEPTION, "JSON EXCEPTION");
		}
		
		return new PluginResult(status, message);
	}
	
	private void updateAdBlockCreation(String callbackID)
	{
		PluginResult result = sendAsJSON(Status.OK, "Successfully created AdBlock");
		result.setKeepCallback(false);
		
		this.success(result, callbackID);
	}
	
	private PluginResult createAdBlock(String action, JSONObject params, String callbackID)
	{
		if(params == null)
		{
			return sendAsJSON(Status.ERROR, "No params passed for action "+action);
		}
		
		_createCallbackID = callbackID;
		Log.d(LOG, "Creating a new AdBlock");
		
		final String publisherID = params.optString("id");
		
		if(publisherID.equalsIgnoreCase(""))
		{
			return sendAsJSON(Status.ERROR, "Missing publisher id");
		}
		
		Log.d(LOG, "have publisher id.");
		
		JSONObject size = params.optJSONObject("size");
		
		if(size == null)
		{
			return sendAsJSON(Status.ERROR, "Missing AdSize");
		}
		
		final int width = size.optInt("width");
		final int height = size.optInt("height");
		
		if(width < 1 || height < 1)
		{
			return sendAsJSON(Status.ERROR, "AdSize is too small");
		}
		
		final JSONObject layout = params.optJSONObject("layout");
		
		Log.d(LOG, "have AdSize");
		
		Handler h = new Handler(ctx.getContext().getMainLooper());
		
		h.post(new Runnable(){
			
			public void run()
			{
				_lib = new AdBlock(ctx.getContext(), new AdSize(width, height), publisherID);
				
				LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				
				if(layout != null)
				{
					Log.d(LOG, "setting the layout");
					
					String gravity = layout.optString("gravity");
					if(!gravity.equalsIgnoreCase(""))
					{
						Log.d(LOG, "Have gravity: "+gravity);
						layoutParams.gravity = getGravity(gravity);
					}
				}
				
				Log.d(LOG, "have a new lib");
				
				((LinearLayout) webView.getParent()).addView(_lib, layoutParams);
				
				Log.d(LOG, "successfully added adblock to webview");
				
				updateAdBlockCreation(_createCallbackID);
			}
			
		});
		
		PluginResult result = sendAsJSON(Status.NO_RESULT, "Awaiting AdBlockCreation"); 
		result.setKeepCallback(true);
		return result;
	}
	
	private PluginResult loadAd(String action, JSONObject params, String callbackID)
	{
		if(_lib == null)
		{
			return sendAsJSON(Status.ERROR, "Cannot load ad. " +
					"Have not created AdBlock library yet.");
		}
		
		AdRequest request = new AdRequest();
		
		if(params != null)
		{
				request.setTestMode(params.optBoolean("isInTestMode"));
				
				String bday = params.optString("bday");
				
				if(!bday.equalsIgnoreCase(""))
				{
					request.setBirthDate(bday);
				}
				
				String gender = params.optString("gender");
				
				if(!gender.equalsIgnoreCase(""))
				{
					try
					{
						Gender g = Gender.valueOf(gender);
						request.setGender(g);
					}
					catch(Exception e)
					{
						e.printStackTrace();
					}
				}
				
				String lang = params.optString("lang");
				
				if(!lang.equalsIgnoreCase(""))
				{
					request.setLanguage(lang);
				}
		}
		
		_lib.loadAd(request);
		return sendAsJSON(Status.OK, "Loading ads.");
	}
	
	@Override
	public PluginResult execute(String action, JSONArray data, String callbackID)
	{
		
		JSONObject params = data.optJSONObject(0);
			
		if(action.equalsIgnoreCase("create"))
		{
			return createAdBlock(action, params, callbackID);
		}
		
		else if(action.equalsIgnoreCase("loadAd"))
		{
			return loadAd(action, params, callbackID);
		}
		
		return sendAsJSON(Status.INVALID_ACTION, "Unsupported Operation: "+action);
	}

}
