define([], function() {

	Clazz.createPackage("com.components.componentTest.js.listener");

	Clazz.com.components.componentTest.js.listener.ComponentTestListener = Clazz.extend(Clazz.WidgetWithTemplate, {
		
		testResultListener : null,
		dynamicpage : null,
		
		/***
		 * Called in initialization time of this class 
		 *
		 * @config: configurations for this listener
		 */
		initialize : function(config) {
			var self = this;
			if (self.testResultListener === null) {
				self.testResultListener = new Clazz.com.components.testResult.js.listener.TestResultListener();
			}
		},
		
		getDynamicParams : function(thisObj, callback) {
			var self = this;
			commonVariables.goal = commonVariables.componentTestGoal;
			commonVariables.phase = commonVariables.componentTestGoal;
			commonVariables.navListener.getMyObj(commonVariables.dynamicPage, function(dynamicPageObject) {
				self.dynamicPageListener = new Clazz.com.components.dynamicPage.js.listener.DynamicPageListener();
				dynamicPageObject.getHtml($('.dynamicControls'), thisObj, 'componentTest_popup', function(response) {;
					if ("No parameters available" === response) {
						self.runComponentTest(function(responseData) {
							callback(responseData);
						});
					}
				});
			});
		},
		
		runComponentTest : function(callback) {
			var self = this;
			var testData = $("#componentTestForm").serialize();
			var appdetails = commonVariables.api.localVal.getProjectInfo();
			var userInfo = JSON.parse(commonVariables.api.localVal.getSession('userInfo'));
			var queryString = '';
			appId = appdetails.data.projectInfo.appInfos[0].id;
			projectId = appdetails.data.projectInfo.id;
			customerId = appdetails.data.projectInfo.customerIds[0];
			username = commonVariables.api.localVal.getSession('username');
						
			if (appdetails !== null && userInfo !== null) {
				queryString ="username="+username+"&appId="+appId+"&customerId="+customerId+"&goal=component-test&phase=component-test&projectId="+projectId+"&"+testData+'&displayName='+userInfo.displayName;
			}
			queryString += self.isBlank($('.moduleName').val()) ? "" : '&moduleName='+$('.moduleName').val();
			$('#testConsole').html('');
			self.testResultListener.openConsoleDiv();//To open the console
			$('.progress_loading').show();
			commonVariables.navListener.getMyObj(commonVariables.mavenService, function(retVal) {
				retVal.mvnComponentTest(queryString, '#testConsole', function(response) {
					self.testResultListener.closeConsole();
					commonVariables.consoleError = false;
					callback(response);
				});
			});
		}
	});

	return Clazz.com.components.componentTest.js.listener.ComponentTestListener;
});