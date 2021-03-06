(function (window, document) {
	"use strict";

	var lib;

	function run()
	{
		function success(resp)
		{
			console.log("success with:" + resp.msg);
		}
		
		function fail(err)
		{
			console.log("failed with: " + err.msg);
		}
		
		lib = window.plugins.adConnectLibrary;
		console.log("Testing the plugin");
		
		var config = {
				id: "12345",
				size: lib.AdSize.BANNER,
				layout: {
					gravity: "bottom"
				}
		}
		
		lib.create(config, 
				function(resp)
				{
					if(resp.msg)
					{	
						console.log(resp.msg);
					}
					
					else
					{
						console.log(resp);
					}
					
					console.log("building adRequest");
					var adRequest = 
					{
						isInTestMode: true,
						bday: lib.BDay(1988, 9, 28),
						gender: lib.Gender.MALE,
						lang: lib.Language.ENGLISH
					};
					
					console.log("Loading ad using adRequest");
					lib.loadAd(adRequest, function(resp){
						
						if(resp.msg)
						{
							console.log(resp.msg);
						}
						
						else
						{
							console.log(resp);
						}
						
					}, fail);
					
					lib.addOnReceiveAdListener(function(resp){
						console.log("first receiveAdListener.");
						if(resp.msg)
							console.log(resp.msg);
						
						lib.addOnReceiveAdListener(success, function(err){
							
							console.log("second receiveAdListener");
							
							if(err.msg)
								console.log(err.msg);
							
							lib.addOnReceiveAdListener(function(resp){
								
								console.log("third receiveAdListener.");
								
								if(resp.msg)
									console.log(resp.msg);
							}, fail, true);
						}, false)
					}, fail, false);
				
				}, fail);
	}
	
	function onPause()
	{
		if(lib)
		{
			lib.pause(success, fail);
		}
	}
	
	function onResume()
	{
		if(lib)
		{
			lib.resume(success, fail);
		}	
	}
	
	document.addEventListener("deviceready", run, false);
	document.addEventListener("pause", onPause, false);
	document.addEventListener("resume", onResume, false);
	
})(window, document);

