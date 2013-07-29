define(["manualTest/listener/manualTestListener", "testResult/listener/testResultListener"], function() {
	Clazz.createPackage("com.components.manualTest.js");

	Clazz.com.components.manualTest.js.ManualTest = Clazz.extend(Clazz.WidgetWithTemplate, {
		
		// template URL, used to indicate where to get the template
		templateUrl: commonVariables.contexturl + "components/manualTest/template/manualTest.tmp",
		configUrl: "components/manualTest/config/config.json",
		name : commonVariables.manualTest,
		manualTestListener : null,
		testResult : null,
		testResultListener : null,
		onTabularViewEvent : null,
		onGraphicalViewEvent : null,
		onDynamicPageEvent : null,
		onRunManualTestEvent : null,
		addManualTestSuite : null,
		addManualTestcase  : null,
		/***
		 * Called in initialization time of this class 
		 *
		 * @globalConfig: global configurations for this class
		 */
		initialize : function(globalConfig) {
			var self = this;
			commonVariables.testType = commonVariables.component;
		
			if (self.manualTestListener === null ) {
				self.manualTestListener = new Clazz.com.components.manualTest.js.listener.manualTestListener();
				self.registerEvents(self.manualTestListener);
			}
			
			if (self.testResultListener === null) {
				self.testResultListener = new Clazz.com.components.testResult.js.listener.TestResultListener();
			}
			
			if (self.onTabularViewEvent === null) {
				self.onTabularViewEvent = new signals.Signal();
			}
			self.onTabularViewEvent.add(self.manualTestListener.onTabularView, self.manualTestListener);
			
			if (self.onGraphicalViewEvent === null) {
				self.onGraphicalViewEvent = new signals.Signal();
			}
			self.onGraphicalViewEvent.add(self.manualTestListener.onGraphicalView, self.manualTestListener);
		},

		registerEvents : function(manualTestListener) {
			var self=this;
			self.addManualTestcase = new signals.Signal();
			self.addManualTestcase.add(manualTestListener.addManualTestcase, manualTestListener);
//			
			self.addManualTestSuite = new signals.Signal();
			self.addManualTestSuite.add(manualTestListener.addManualTestSuite, manualTestListener);
			
			self.updateManualTestcase = new signals.Signal();
			self.updateManualTestcase.add(manualTestListener.updateManualTestcase, manualTestListener);
		},
		/***
		 * Called in once the login is success
		 *
		 */
		loadPage : function(needAnimation) {
			Clazz.navigationController.mainContainer = commonVariables.contentPlaceholder;
			Clazz.navigationController.push(this, needAnimation);
		},
		
		/***
		 * Called after the preRender() and bindUI() completes. 
		 * Override and add any preRender functionality here
		 *
		 * @element: Element as the result of the template + data binding
		 */
		postRender : function(element) {
			var self = this;
			commonVariables.navListener.getMyObj(commonVariables.testResult, function(retVal) {
				self.testResult = retVal;
				Clazz.navigationController.jQueryContainer = $(commonVariables.contentPlaceholder).find('#testResult');
				Clazz.navigationController.push(self.testResult, false);
			});
			
		},
		
		preRender: function(whereToRender, renderFunction) {
			var self = this;
			var data = {};
			var userPermissions = JSON.parse(commonVariables.api.localVal.getSession('userPermissions'));
			data.userPermissions = userPermissions;
			renderFunction(data, whereToRender);
		},
		
		/***
		 * Bind the action listeners. The bindUI() is called automatically after the render is complete 
		 *
		 */
		bindUI : function() {
			var self = this;
			$(".tooltiptop").tooltip();
			self.windowResize();
			
			$(".scrollContent").mCustomScrollbar({
				autoHideScrollbar:true,
				theme:"light-thin",
				advanced:{ updateOnContentResize: true}
			});
			
			$("#addTestSuite").click(function() {
				self.openccpl(this, 'show_manualTestSuite_popup','');
				$('#testSuiteId').focus();
				var currentTestsuiteName = commonVariables.testSuiteName;
				if (currentTestsuiteName !== null) {
					$('#testSuiteName').attr('value', currentTestsuiteName);
				}
			});
			
			$("#addTestCase").click(function() {
				self.openccpl(this, 'show_manualTestCase_popup','');
				var currentTestsuiteName = commonVariables.testSuiteName;
				$('input[name=testSuiteName]').attr('value', currentTestsuiteName);
			});
			
			$("a[class=edit]").click(function() {
				self.openPopUpToEdit(this);
			});
			
			$("input[name=saveTestSuite]").click(function() {
				var testSuiteId = $('#testSuiteId').val();
				self.addManualTestSuite.dispatch(testSuiteId);
			});
			
			$("input[name=saveTestCase]").click(function() {
				var testSuiteName = $('input[name=testSuiteName]').val();
				self.addManualTestcase.dispatch(testSuiteName);
			});
			
			$("#testSuites").css("display", "none");
			$("#testCases").css("display", "none");
			$("#unitTestTab").css("display", "block");
			$(".unit_view").css("display", "none");
			$("#graphView").css("display", "none");
			
			//Shows the tabular view of the test result
			$("#tabularView").unbind("click");
			$("#tabularView").click(function() {
				self.onTabularViewEvent.dispatch();
			});
			
			//Shows the graphical view of the test result
			$("#graphicalView").unbind("click");
			$("#graphicalView").click(function() {
				self.onGraphicalViewEvent.dispatch();
			});
			
			Clazz.navigationController.mainContainer = commonVariables.contentPlaceholder;
		}
	});

	return Clazz.com.components.manualTest.js.manualTest;
});