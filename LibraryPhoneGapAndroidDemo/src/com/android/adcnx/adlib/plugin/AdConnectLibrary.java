package com.android.adcnx.adlib.plugin;

import java.util.HashMap;

import org.apache.cordova.api.PluginResult;
import org.apache.cordova.api.PluginResult.Status;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout.LayoutParams;
import android.widget.LinearLayout;

import com.android.adcnx.adlib.Ad;
import com.android.adcnx.adlib.AdBlock;
import com.android.adcnx.adlib.AdListener;
import com.android.adcnx.adlib.AdRequest;
import com.android.adcnx.adlib.AdRequest.ErrorCode;
import com.android.adcnx.adlib.AdRequest.Gender;
import com.android.adcnx.adlib.AdSize;
import com.phonegap.api.Plugin;

public class AdConnectLibrary extends Plugin implements AdListener
{	
	private static final String LOG = "AdConnectLibraryPhoneGapPlugin";
	private static AdBlock _lib = null;
	private static AdRequest _currAdRequest = null;
	
	private static final int MAX_LISTENERS = 5;
	private static final HashMap<String, String> _listeners = new HashMap<String, String>(MAX_LISTENERS);
	
	public AdConnectLibrary()
	{
		super();
		
		_listeners.put("failedToReceiveAdListener", null);
		_listeners.put("receiveAdListener", null);
		_listeners.put("presentScreenListener", null);
		_listeners.put("leaveApplicationListener", null);
		_listeners.put("dismissScreenListener", null);
	}
	
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
			
			return new PluginResult(status, msg);
		}
		
		return new PluginResult(status, message);
	}
	
	private void updateAdBlockCreation(String callbackID)
	{
		PluginResult result = sendAsJSON(Status.OK, "Successfully created AdBlock");
		result.setKeepCallback(false);
		
		this.success(result, callbackID);
	}
	
	private PluginResult createAdBlock(JSONObject params, final String callbackID)
	{
		final String action = "create";
		
		if(_lib != null)
		{
			return sendAsJSON(Status.ERROR, "There is already an instance of the library" +
					"currently running.");
		}
		
		if(params == null)
		{
			return sendAsJSON(Status.ERROR, "No params passed for action "+action);
		}
		
		//_createCallbackID = callbackID;
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
		final AdConnectLibrary self = this;
		h.post(new Runnable(){
			
			public void run()
			{
				_lib = new AdBlock(ctx.getContext(), new AdSize(width, height), publisherID);
				
				_lib.setAdListener(self);
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
				
				updateAdBlockCreation(callbackID);
			}
			
		});
		
		PluginResult result = sendAsJSON(Status.NO_RESULT, "Awaiting AdBlockCreation"); 
		result.setKeepCallback(true);
		return result;
	}
	
	private PluginResult loadAd(JSONObject params, String callbackID)
	{
		Log.d(LOG, "called load ad function");
		
		if(_lib == null)
		{
			Log.d(LOG, "_lib is null");
			
			return sendAsJSON(Status.ERROR, "Cannot load ad. " +
					"Have not created AdBlock library yet.");
		}
		
		Log.d(LOG, "creating AdRequest object");
		AdRequest request = new AdRequest();
		
		Log.d(LOG, "AdRequest object created");
		
		if(params != null)
		{
			Log.d(LOG, "adrequest params are not null, setting them now.");
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
				
			Log.d(LOG, "Done setting adrequest params");
		}
		
		Log.d(LOG, "Actually loading adrequest");
		
		_currAdRequest = request;
		_lib.loadAd(request);
		
		Log.d(LOG, "Loading ads.");
		
		return sendAsJSON(Status.OK, "Loading ads.");
	}
	
	private PluginResult pause(String callbackID)
	{
		if(_lib == null)
		{
			return sendAsJSON(Status.ERROR, "AdConnect Library is not created.");
		}
		
		_lib.stopLoading();
		
		return sendAsJSON(Status.OK, "Stopped loading ads.");
	}
	
	private PluginResult resume(String callbackID)
	{
		if(_lib == null)
		{
			return sendAsJSON(Status.ERROR, "AdConnect Library is not created.");
		}
		
		if(_currAdRequest == null)
		{
			return sendAsJSON(Status.ERROR, "No previous AdRequest found to resume.");
		}
		
		_lib.loadAd(_currAdRequest);
		
		return sendAsJSON(Status.OK, "Resumed loading ads.");
	}
	
	@Override
	public PluginResult execute(String action, JSONArray data, String callbackID)
	{
		
		JSONObject params = data.optJSONObject(0);
			
		if(action.equalsIgnoreCase("create"))
		{
			return createAdBlock(params, callbackID);
		}
		
		else if(action.equalsIgnoreCase("loadAd"))
		{
			return loadAd(params, callbackID);
		}
		
		else if(action.equalsIgnoreCase("pause"))
		{
			return pause(callbackID);
		}
		
		else if(action.equalsIgnoreCase("resume"))
		{
			return resume(callbackID);
		}
		
		else if(action.equalsIgnoreCase("hide"))
		{
			return hide(callbackID);
		}
		
		else if(action.equalsIgnoreCase("show"))
		{
			return show(callbackID);
		}
		
		else if(action.equalsIgnoreCase("isCreated"))
		{
			return isCreated(callbackID);
		}
		
		else if(action.equalsIgnoreCase("addListener"))
		{
			return addListener(params, callbackID);
		}
		
		return sendAsJSON(Status.INVALID_ACTION, "Unsupported Operation: "+action);
	}
	
	private synchronized PluginResult addListener(JSONObject params, String callbackID)
	{
		if(_lib == null)
		{
			return sendAsJSON(Status.ERROR, "Library not created.");
		}
		
		if(params == null)
		{
			return sendAsJSON(Status.ERROR, "No params have been passed");
		}
		
		String listener = params.optString("listener");
		
		if(listener.equalsIgnoreCase(""))
		{
			return sendAsJSON(Status.ERROR, "No listener has been passed.");
		}
		
		String oldCallbackID = _listeners.remove(listener); 
		
		if(oldCallbackID == null)
		{
			_listeners.put(listener, callbackID);
			
			PluginResult result = sendAsJSON(Status.NO_RESULT, listener+" added successfully.");
			result.setKeepCallback(true);
			
			return result;
		}
		
		else
		{
			if(params.optBoolean("doForce"))
			{
				
				_listeners.put(listener,  callbackID);
				PluginResult result = sendAsJSON(Status.NO_RESULT, listener+"added successfully.");
				
				result.setKeepCallback(true);
				
				return result;
			}
			
			return sendAsJSON(Status.ERROR, listener+" previously added. Cannot add again unless sent the doForce param");
		}
	}

	private PluginResult isCreated(String callbackID)
	{
		JSONObject response = new JSONObject();
		
		boolean created = _lib != null;
		try
		{
			response.put("isCreated", created);
			response.put("msg", "The library has already been created");
			
			return new PluginResult(Status.OK, response);
		}
		catch (JSONException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new PluginResult(Status.JSON_EXCEPTION, "Something went wrong.");
		}
	}

	private void updateShowStatus(String callbackID)
	{
		PluginResult result = sendAsJSON(Status.OK, "Shown");
		result.setKeepCallback(false);
		
		this.success(result, callbackID);
	}

	private PluginResult show(final String callbackID)
	{
		Log.d(LOG, "Show function called.");
		if(_lib == null)
		{
			return sendAsJSON(Status.ERROR, "AdConnect Library not created.");
		}
		
		Handler h = new Handler(ctx.getContext().getMainLooper());
		
		h.post(new Runnable(){
			
			public void run()
			{
				_lib.setVisibility(View.VISIBLE);
				
				if(_currAdRequest != null)
				{
					_lib.loadAd(_currAdRequest);
				}
				updateShowStatus(callbackID);
			}
		});
		
		PluginResult result = sendAsJSON(Status.NO_RESULT, "Showing...");
		result.setKeepCallback(true);
		
		return result;
	}
	
	private void updateHideStatus(String callbackID)
	{
		PluginResult result = sendAsJSON(Status.OK, "Successfully hidden view.");
		result.setKeepCallback(false);
		
		this.success(result, callbackID);
	}
	
	private PluginResult hide(final String callbackID)
	{
		Log.d(LOG, "Hide function called.");
		if(_lib == null)
		{
			return sendAsJSON(Status.ERROR, "AdConnect Library not created.");
		}
		
		Handler h = new Handler(ctx.getContext().getMainLooper());
		h.post(new Runnable(){

			public void run()
			{
				// TODO Auto-generated method stub
				_lib.stopLoading();
				_lib.setVisibility(View.GONE);
				
				updateHideStatus(callbackID);
			}
			
		});
		
		PluginResult result = sendAsJSON(Status.NO_RESULT, "Hiding...");
		result.setKeepCallback(true);
		
		return result;
	}
	
	public void OnFailedToReceiveAd(Ad ad, ErrorCode code)
	{
		//TODO
		String callbackID = _listeners.get("failedToReceiveAdListener");
		
		if(callbackID != null)
		{
			JSONObject message = new JSONObject();
			
			try
			{
				message.put("msg", "Failed to receive ad: "+ad.toString());
				message.put("error", code.name());
				
				this.success(message, callbackID);
			}
			catch (JSONException e)
			{
				//e.printStackTrace();
				PluginResult result = new PluginResult(Status.OK, message);
				result.setKeepCallback(true);
				
				this.success("Failed to receive ad: "+ad.toString()+"\n" +
						"error: "+code.name(), callbackID);
			}
		}
	}
	
	public void onDismissScreen(Ad ad)
	{
		// TODO Auto-generated method stub
		String callbackID = _listeners.get("dismissScreenListener");
		
		defaultListenerUpdate("dismissed screen", callbackID);
	}

	public void onLeaveApplication(Ad ad)
	{
		String callbackID = _listeners.get("leaveApplicationListener");
		
		defaultListenerUpdate("left application.", callbackID);
	}

	public void onPresentScreen(Ad ad)
	{
		String callbackID = _listeners.get("presentScreenListener");
		defaultListenerUpdate("presented screen", callbackID);
	}

	public void onReceiveAd(Ad ad)
	{
		String callbackID = _listeners.get("receiveAdListener");
		defaultListenerUpdate("received ad.", callbackID);
	}
	
	public void defaultListenerUpdate(String msg, String callbackID)
	{
		if(callbackID != null)
		{
			PluginResult result = sendAsJSON(Status.OK, msg);
			result.setKeepCallback(true);
			
			this.success(result, callbackID);
		}
	}

}
