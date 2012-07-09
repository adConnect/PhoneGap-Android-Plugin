(function(window, PhoneGap){
	
	var AdConnectLibrary = function(){};

	AdConnectLibrary.prototype = 
	{
			_pluginName: "AdConnectLibrary",
			
			Language: {
				ENGLISH: "EN",
				CHINESE: "CN"
			},
			
			Gender: {
				MALE: "MALE",
				FEMALE: "FEMALE",
				ALL: "ALL"
			},
			
			MAX_AGE: 122,
			
			BDay: function(year, month, day)
			{
				var currDate = new Date();
				var currYear = currDate.getFullYear();
				
				if(!year || isNaN(year)
						|| year > currYear 
						|| year < currYear - this.MAX_AGE)
				{
					return "";
				}	
				
				
				if(!month || isNaN(month) 
						|| month < 1 
						|| month > 12)
				{	
					month = 1;
					day = 1;
				}
				
				if(!day || isNaN(day)
						|| day < 1
						|| day > 31)
				{
					day = 1;
				}
				
				var daysInMonth = [31,28,31,30,31,30,31,31,30,31,30,31];
				
				// If evenly divisible by 4 and not evenly divisible by 100,
				// or is evenly divisible by 400, then a leap year
				if ( (!(year % 4) && year % 100) || !(year % 400)) 
				{
					daysInMonth[1] = 29;
				}
				
				if(day > daysInMonth[month-1])
				{
					day = 1;	
				}
				
				if(month < 10)
				{
					month = "0"+month;
				}
				
				if(day < 10)
				{
					day = "0"+day		
				}
				
				return year+month+day+"";
			},
			
			AdSize: 
			{
				BANNER:
				{
					width: 320,
					height: 50
				},
				
				TABLET_BANNER:
				{
					width: 468,
					height: 60
				},
				
				TABLET_LEADERBOARD: 
				{
					width: 728,
					height: 90
				},
				
				TABLET_RECT: 
				{
					width: 300,
					height: 250
				}
			},
			
			exec: function(action, args, success, fail)
			{
				return PhoneGap.exec(success,
						fail,
						this._pluginName,
						action,
						args);
			},
			
			/**
			 * @param args JSON Object of the form
			 * 				{
			 * 					publisherID: "STRING_PUBLISHER_ID",
			 * 					
			 * 					size:{
			 * 						width: INT_PIXELS,
			 * 						height: INT_PIXELS
			 * 					}
			 * 				}
			 */
			create: function(args, success, fail)
			{
				return this.exec("create", [args], success, fail);
			},
			
			loadAd: function(args, success, fail)
			{
				return this.exec("loadAd", [args], success, fail);
			},
			
			pause: function(success, fail)
			{
				return this.exec("pause", [], success, fail);
			},
			
			resume: function(success, fail)
			{
				return this.exec("resume", [], success, fail);
			},
			
			hide: function(success, fail)
			{
				return this.exec("hide", [], success, fail);
			},
			
			show: function(success, fail)
			{
				return this.exec("show", [], success, fail);
			},
			
			isCreated: function(success, fail)
			{
				return this.exec("isCreated", [], success, fail);
			},
			
			addListener: function(listener, doForce, success, fail)
			{
				var msg = {
						listener: listener,
						doForce: doForce
				};
				
				return this.exec("addListener", [msg], success, fail);
			},
			
			addOnFailedToReceiveAdListener: function(success, fail, doForce)
			{
				return this.addListener("failedToReceiveAdListener", doForce, success, fail);
			},
			
			addOnDismissScreenListener: function(success, fail, doForce)
			{
				return this.addListener("dismissScreenListener", doForce, success, fail);
			},
			
			addOnLeaveApplicationListener: function(success, fail, doForce)
			{
				return this.addListener("leaveApplicationListener", doForce, success, fail);
			},
			
			addOnPresentScreenListener: function(success, fail, doForce)
			{
				return this.addListener("presentScreenListener", doForce, success, fail);
			},
			
			addOnReceiveAdListener: function(success, fail, doForce)
			{
				return this.addListener("receiveAdListener", doForce, success, fail);
			}
	};
		
	PhoneGap.addConstructor(function(){
		
		PhoneGap.addPlugin("adConnectLibrary", new AdConnectLibrary());
	});
})(window, PhoneGap);


	

