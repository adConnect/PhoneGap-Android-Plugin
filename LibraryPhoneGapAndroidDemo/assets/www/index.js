(function(){
	
	document.addEventListener("deviceready", run, false);
	
	function run()
	{
		
		function success(r)
		{
			console.log("got back: ", r);
		}
		
		function fail(e)
		{
			console.log("failed with: "+e);
		}
		
		console.log("Testing the plugin");
		console.log(window.plugins.adConnectLibrary);
		window.plugins.adConnectLibrary.test("msg", success, fail);
			
	}
	
})();

