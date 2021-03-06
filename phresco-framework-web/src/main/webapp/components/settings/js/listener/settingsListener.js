define([], function() {

	Clazz.createPackage("com.components.settings.js.listener");

	Clazz.com.components.settings.js.listener.SettingsListener = Clazz.extend(Clazz.WidgetWithTemplate, {
		
		basePlaceholder :  window.commonVariables.basePlaceholder,
		jobTemplates : null,
		mavenServiceListener : null,
		/***
		 * Called in initialization time of this class 
		 *
		 * @config: configurations for this listener
		 */
		initialize : function(config) {
			var self = this;
			
			if (self.mavenServiceListener === null)	{
				commonVariables.navListener.getMyObj(commonVariables.mavenService, function(retVal){
					self.mavenServiceListener = retVal;
				});
			}
			
		},
		
		getHeaderResponse : function (header, callback) {
			var self = this;
			try {
				commonVariables.api.ajaxRequest(header, function(response) {
						if (response !== null) {
							callback(response);
						} else {
							callback({ "status" : "service failure"});
						}

					},

					function(textStatus) {						
						callback({ "status" : "Connection failure"});
					}
				);
			} catch(exception) {
				callback({ "status" : "service exception"});
			}
		},
		
		getRequestHeader : function(ciRequestBody, action, params) {
			var self = this, header;
	
			header = {
				contentType: "application/json",
				dataType: "json",
				requestMethod : "",
				webserviceurl : '',
			};
			if (action === "jenkinsUrl") {
				header.requestMethod = "GET";
				header.webserviceurl = commonVariables.webserviceurl + commonVariables.ci + "/jenkinsUrl";
			} else if (action === "getMail") {
				header.requestMethod = "GET";
				header.webserviceurl = commonVariables.webserviceurl + commonVariables.ci + "/mail";
			} else if (action === "getTfs") {
				header.requestMethod = "GET";
				header.webserviceurl = commonVariables.webserviceurl + commonVariables.ci + "/tfs";
			} else if (action === "getConfluence") {
				header.requestMethod = "GET";
				header.webserviceurl = commonVariables.webserviceurl + commonVariables.ci + "/confluence";
			} else if(action === "getTestFlight") {
				header.requestMethod = "GET";
				header.webserviceurl = commonVariables.webserviceurl + commonVariables.ci + "/testFlight";
			} else if (action === "setup") {
				header.requestMethod = "POST";
				header.webserviceurl = commonVariables.webserviceurl + commonVariables.mvnCiSetup;
			} else if (action === "start") {
				header.requestMethod = "POST";
				header.webserviceurl = commonVariables.webserviceurl + commonVariables.mvnCiStart;
			} else if (action === "stop") {
				header.requestMethod = "POST";
				header.webserviceurl = commonVariables.webserviceurl + commonVariables.mvnCiStop;
			} else if (action === "save") {
				header.requestMethod = "POST";
				var eUser = $('input[name=username][temp=email]').val();
				var ePass = $('input[name=password][temp=email]').val();
				var jUrl = $('input[name=jenkinsUrl][temp=jenkins]').val();
				var jName = $('input[name=juserName][temp=jenkins]').val();
				var jPass = $('input[name=jpassword][temp=jenkins]').val();
				var tfcle = $('input[name=tfcle][temp=jenkins]').val();	
				header.requestPostBody = JSON.stringify(ciRequestBody);
				header.webserviceurl = commonVariables.webserviceurl + commonVariables.ci + "/global?emailAddress=" + eUser + "&emailPassword=" + ePass + "&url=" + jUrl + "&username=" + jName + "&password=" + jPass + "&tfsUrl=" + tfcle;
			} 
			return header;
		},
		
		getSettings : function() {
			var self = this;
			
			self.getHeaderResponse(self.getRequestHeader(self.ciRequestBody, 'jenkinsUrl'), function(response) {
				$('input[name=jenkinsUrl]').val(response.data.repoUrl);
				$('input[name=juserName]').val(response.data.userName);
				$('input[name=jpassword]').val(response.data.password);
			});
			
			self.getHeaderResponse(self.getRequestHeader(self.ciRequestBody, 'getMail'), function(response) {
				$('input[name=username][temp=email]').val(response.data[0]);
				$('input[name=password][temp=email]').val(response.data[1]);
			});
			
			self.getHeaderResponse(self.getRequestHeader(self.ciRequestBody, 'getTfs'), function(tfs) {
				$('input[name=tfcle][temp=jenkins]').val(tfs.data);
			});
			
			self.getHeaderResponse(self.getRequestHeader(self.ciRequestBody, 'getConfluence'), function(response) {
				for (i in response.data) {
					var data = response.data[i];
					var tds = $('#prototype').html();
					var tr = $('<tr class=confluence>');
					tr.append(tds);
					tr.find('[name=url]').val(data.repoUrl);
					tr.find('[name=username]').val(data.userName);
					tr.find('[name=password]').val(data.password);
					
					tr.insertBefore($('#save'));
				}
				self.switchStatus();
//				self.iconsmanipulator();
				
				setTimeout(function() {
					
					
					$('#conflTable').on('click', 'a.delete', function() {
						$(this).closest('.confluence').remove();
					});
					
				}, 500);
			});
			
			self.getHeaderResponse(self.getRequestHeader(self.ciRequestBody, 'getTestFlight'), function(response) {
				for (i in response.data) {
					var data = response.data[i];
					var tds = $('#testFlightPrototype').html();
					var tr = $('<tr class=testFlight>');
					tr.append(tds);
					tr.find('[name=tokenPairName]').val(data.tokenPairName);
					tr.find('[name=apiToken]').val(data.apiToken);
					tr.find('[name=teamToken]').val(data.teamToken);
					
					tr.insertBefore($('#testFlightSave'));
				}
				self.switchStatus();
//				self.iconsmanipulator();
				
				setTimeout(function() {
					
					
					$('#testFlightTable').on('click', 'a.deleteTestFlight', function() {
						$(this).closest('.testFlight').remove();
					});
					
				}, 500);
			});
			
		},
		
		switchStatus : function() {
			commonVariables.navListener.getMyObj(commonVariables.jobTemplates, function(jobTemplatesObject) {
				jobTemplatesObject.ciListener.jenkinsStatus(function(response) {
					if (response.data === "200") {
						$('input[name=switch]').attr('value', "Stop");
						$('input[name=setup]').attr('disabled', "disabled");
					} else if (response.data === "503") {
						$('input[name=switch]').attr('value', "Starting...");
						$('input[name=start]').attr('disabled', "disabled");
					} else {
						$('input[name=switch]').attr('value', "Start");
					}
				});
			});
		}, 
		
		iconsmanipulator : function() {
			var confluence = $('.confluence');
			if (confluence.length < 2) {
				$('.delete').hide();
			} else {
				$('.delete').show();
			}
		},
		
		setup : function() {
			var self = this;
			var queryString = '';
						
			$('#testConsole').html('');
			self.openConsole(true);//To open the console
			if (self.mavenServiceListener === null) {
				commonVariables.navListener.getMyObj(commonVariables.mavenService, function(retVal){
					self.mavenServiceListener = retVal;
					self.mavenServiceListener.mvnCiSetup(queryString, '#testConsole', function(response) {
						self.closeConsole();
						if (response !== undefined && response.status === "COMPLETED") {
							$('input[name=setup]').attr('disabled', "disabled");	
						}					
						$('.progress_loading').hide();
					});
				});
			} else {
				self.mavenServiceListener.mvnCiSetup(queryString, '#testConsole', function(response) {
					self.closeConsole();
					if (response !== undefined && response.status === "COMPLETED") {
						$('input[name=setup]').attr('disabled', "disabled");	
					}					
					$('.progress_loading').hide();
				});
			}
		},

		start : function() {
			var self = this;
			var queryString = '';
						
			$('#testConsole').html('');
			self.openConsole(true);//To open the console
			if (self.mavenServiceListener === null) {
				commonVariables.navListener.getMyObj(commonVariables.mavenService, function(retVal){
					self.mavenServiceListener = retVal;
					self.mavenServiceListener.mvnCiStart(queryString, '#testConsole', function(response) {
						self.closeConsole();
						$('.progress_loading').hide();
					});
				});
			} else {
				self.mavenServiceListener.mvnCiStart(queryString, '#testConsole', function(response) {
					self.closeConsole();
					$('.progress_loading').hide();
				});
			}
		},
		
		stop : function() {
			var self = this;
			var queryString = '';
						
			$('#testConsole').html('');
			self.openConsole(true);//To open the console
			if (self.mavenServiceListener === null) {
				commonVariables.navListener.getMyObj(commonVariables.mavenService, function(retVal){
					self.mavenServiceListener = retVal;
					self.mavenServiceListener.mvnCiStop(queryString, '#testConsole', function(response) {
						self.closeConsole();
						$('.progress_loading').hide();
					});
				});
			} else {
				self.mavenServiceListener.mvnCiStop(queryString, '#testConsole', function(response) {
					self.closeConsole();
					$('.progress_loading').hide();
				});
			}
		},
		
		validate : function (callback) {
			var self=this;
			var status = true;
			var username = $('.confluence').find("input[name=username]");	
			username.each(function(i, selected) {
				if($(this).val() === "") {
					$(this).focus();
					$(this).attr('placeholder','Enter Username');
					$(this).addClass("errormessage");
					$(this).bind('keypress', function() {
						$(this).removeClass("errormessage");
					});
					status = false;				
				}
			});
			var password = $('.confluence').find("input[name=password]");	
			password.each(function(i, selected) {
				if($(this).val() === "") {
					$(this).focus();
					$(this).attr('placeholder','Enter Password');
					$(this).addClass("errormessage");
					$(this).bind('keypress', function() {
						$(this).removeClass("errormessage");
					});
					status = false;				
				}
			});
			var url = $('.confluence').find("input[name=url]");	
			url.each(function(i, selected) {
				if(!self.isValidUrl($(this).val())) {	
					$(this).focus();
					$(this).val('');
					$(this).attr('placeholder','Enter ValidUrl');
					$(this).addClass("errormessage");
					$(this).bind('keypress', function() {
						$(this).removeClass("errormessage");
					});
					status = false;				
				}
			});
			
			var username = $("input[name=username][temp=email]");	
			username.each(function(i, selected) {
				if($(this).val() === "") {
					$(this).focus();
					$(this).attr('placeholder','Enter EmailId');
					$(this).addClass("errormessage");
					$(this).bind('keypress', function() {
						$(this).removeClass("errormessage");
					});
					status = false;				
				}
			});
			var password = $("input[name=password][temp=email]");	
			password.each(function(i, selected) {
				if($(this).val() === "") {
					$(this).focus();
					$(this).attr('placeholder','Enter Password');
					$(this).addClass("errormessage");
					$(this).bind('keypress', function() {
						$(this).removeClass("errormessage");
					});
					status = false;				
				}
			});
			
			return status
		},
		
		save : function() { 
			var self = this;
			var status = self.validate();
			if (status === true) {
				var ciRequestBody = [];
				var postdata = {};
				var confluence = $('.confluence');
				var confluenceRequestBody = [];
				var testFlight = $('.testFlight');
				var testFlightRequestBody = [];
				
				confluence.each(function(i, selected) {
					var confluenceObj = {};
					confluenceObj.repoUrl = $(this).find('input[name=url]').val();
					confluenceObj.userName = $(this).find('input[name=username]').val();
					confluenceObj.password = $(this).find('input[name=password]').val();
					confluenceRequestBody.push(confluenceObj);
				});
				
				postdata.repoDetails = confluenceRequestBody;
				
				testFlight.each(function(i, selected) {
					var testFlightObj = {};
					testFlightObj.tokenPairName = $(this).find('input[name=tokenPairName]').val();
					testFlightObj.apiToken = $(this).find('input[name=apiToken]').val();
					testFlightObj.teamToken = $(this).find('input[name=teamToken]').val();
					testFlightRequestBody.push(testFlightObj);
				});
				
				postdata.testFlight = testFlightRequestBody
				ciRequestBody.push(postdata);
				
				self.getHeaderResponse(self.getRequestHeader(postdata, 'save'), function(response) {
					commonVariables.api.showError(response.responseCode ,"success", true, false, true);
				});
			}
			
		},
		
		showHideConsole : function() {
			var self = this;
			var check = $('#consoleImg').attr('data-flag');
			if (check === "true") {
				self.openConsole(true);
			} else {
				self.closeConsole();
			}
		},
				
	});

	
	return Clazz.com.components.settings.js.listener.SettingsListener;
});