(function(){
	
	document.addEventListener("deviceready", run, false);
	
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
		
		var lib = window.plugins.adConnectLibrary;
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
						console.log(resp.msg);
					
					else
						console.log(resp);
					
					var adRequest = 
					{
						isInTestMode: true,
						bday: lib.BDay(1988, 09, 28),
						gender: lib.Gender.MALE,
						lang: lib.Language.ENGLISH
					};
					
					lib.loadAd(adRequest, success, fail);
				}
		, fail);
	}
	
})();

