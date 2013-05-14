
var commonVariables = {
	globalconfig : "",
	webserviceurl : "",
	contexturl : "src",
	
	login : "login",
	loginContext : "login",

	header : "header",
	headerContext : "header",
	
	footer : "footer",
	footerContext : "footer",
	
	navigation : "navigation",
	navigationContext : "",

	basePlaceholder : "basepage\\:widget",
	headerPlaceholder : "header\\:widget",
	contentPlaceholder : "content\\:widget",
	footerPlaceholder : "footer\\:widget"
};

define(["jquery"], function($) {
	$(document).ready(function(){

		$.get('src/components/login/test/config.json', function(data) {
			commonVariables.globalconfig = data;
			commonVariables.webserviceurl = "framework/";
			configJson = {
				// comment out the below line for production, this one is so require doesn't cache the result
				urlArgs: "time=" +  (new Date()).getTime(),
				baseUrl: "src/",
				
				paths : {
					framework : "js/framework",
					listener : "js/commonComponents/listener",
					fastclick : "lib/fastclick",
					api : "js/api",
					lib : "lib",
					signals : "lib/signals",
					common : "js/commonComponents/common",
					modules: "js/commonComponents/modules",
					Clazz : "js/framework/class",
					components: "components",
					configData: data,
					jshamcrest: "lib/jshamcrest-0.5.2-min",
					jsmockito: "lib/jsmockito-1.0.3-min"
				}
			};

			$.each(commonVariables.globalconfig.components, function(index, value){
				configJson.paths[index] = value.path;
			});
			// setup require.js
			var requireConfig = requirejs.config(configJson);
			
			require(["lib/Signal-1.0.0", "lib/SignalBinding-1.0.0", "lib/i18next-1.6.0", "jquery_mCustomScrollbar_concat_min-2.8.1", "loginTest", "projectlistTest", "headerTest", "footerTest", "navigationTest", "projectTest", "applicationTest", "featuresTest", "codequalityTest", "configurationTest", "buildTest", "jshamcrest", "jsmockito"],	function (Signal, SignalBinding, next, mCustomScrollbar, loginTest, projectlistTest, headerTest, footerTest, navigationTest, projectTest,applicationTest, featuresTest, codequalityTest, configurationTest, buildTest){
				JsHamcrest.Integration.JsTestDriver();
				JsMockito.Integration.JsTestDriver();
				loginTest.runTests(data);
				navigationTest.runTests(data);
				headerTest.runTests(data);
				applicationTest.runTests(data);		
				footerTest.runTests(data);
				featuresTest.runTests(data);
				projectlistTest.runTests(data);
				projectTest.runTests(data);
				codequalityTest.runTests(data);	
				configurationTest.runTests(data);
				buildTest.runTests(data);
			});
		}, "json");
	});
});