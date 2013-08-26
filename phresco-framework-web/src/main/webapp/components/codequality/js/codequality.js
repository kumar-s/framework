define(["codequality/listener/codequalityListener"], function() {
	Clazz.createPackage("com.components.codequality.js");

	Clazz.com.components.codequality.js.CodeQuality = Clazz.extend(Clazz.WidgetWithTemplate, {
		// template URL, used to indicate where to get the template
		templateUrl: commonVariables.contexturl + "components/codequality/template/codequality.tmp",
		configUrl: "components/projects/config/config.json",
		name : commonVariables.codequality,
		codequalityListener: null,
		dynamicpage : null,
		dynamicPageListener : null,
		renderedData : {},
		onProgressEvent : null,
	
		/***
		 * Called in initialization time of this class 
		 *
		 * @globalConfig: global configurations for this class
		 */
		initialize : function(globalConfig){
			var self = this;
			
			if(self.codequalityListener === null){
				self.codequalityListener = new Clazz.com.components.codequality.js.listener.CodequalityListener();
			}
				if(self.dynamicpage === null){
					commonVariables.navListener.getMyObj(commonVariables.dynamicPage, function(retVal){
					self.dynamicpage = retVal;
					self.dynamicPageListener = self.dynamicpage.dynamicPageListener;
					self.registerEvents();
				});
			}else{
				self.registerEvents();
			}
		},
		
		registerEvents : function() {
			var self = this;
			if(self.onProgressEvent === null){
				self.onProgressEvent = new signals.Signal();
			}	
			self.readLogEvent = new signals.Signal();
			self.readLogEvent.add(self.codequalityListener.codeValidate, self.codequalityListener);
			self.onProgressEvent.add(self.codequalityListener.onPrgoress, self.codequalityListener);
		},
		/***
		 * Called in once the login is success
		 *
		 */
		loadPage :function(){
			Clazz.navigationController.push(this);
		},


		/***
		 * Called after the preRender() and bindUI() completes. 
		 * Override and add any preRender functionality here
		 *
		 * @element: Element as the result of the template + data binding
		 */
		 
		postRender : function(element) {
			var self = this; 
			var appDirName = commonVariables.api.localVal.getSession('appDirName');
			var goal = "validate-code";
			commonVariables.goal = goal;
			
			setTimeout(function() {
				self.codequalityListener.getReportTypes(self.codequalityListener.getRequestHeader(self.appDirName , "reporttypes"), function(response) {
					var projectlist = {};
					projectlist.projectlist = response;	
					self.renderedData = response;
					self.codequalityListener.constructHtml(self.renderedData);
				});
			}, 200);
			
			//RBAC
			var userPermissions = JSON.parse(commonVariables.api.localVal.getSession('userPermissions'));
			if (userPermissions && !userPermissions.manageCodeValidation) {
				$("#codeAnalysis").prop("disabled", true);
			} else {
				$("#codeAnalysis").prop("disabled", false);
			}
			
			//To set the height of the report result section  
			var windowHeight = $(document).height();
			var marginTop = '';
			marginTop = $('.testSuiteTable').css("margin-top");
			if(marginTop !== undefined){
				marginTop = marginTop.substring(0, marginTop.length - 2);
			}	
			var footerHeight = $('#footer').height();
			var deductionHeight = Number(marginTop) + Number(footerHeight);
			var finalHeight = windowHeight - deductionHeight - 5;
			$('.testSuiteTable').height(finalHeight);
		
		},
		
		/***
		 * Bind the action listeners. The bindUI() is called automatically after the render is complete 
		 *
		 */
		bindUI : function(){
			var self = this;
			$(".tooltiptop").tooltip();
			$(".dyn_popup").hide();
			
			$("#codeAnalysis").click(function() {
				var whereToRender = $('#code_popup ul');
                commonVariables.goal = "validate-code";
                commonVariables.phase = "validate-code";
                self.dynamicpage.getHtml(whereToRender, this, 'code_popup');
			});
			
			$("#validate").click(function() {
				$('.progress_loading').css('display','block');
				$(".dyn_popup").hide();
				self.readLogEvent.dispatch(function(){
					$('.progress_loading').css('display','none');
				});
			});
			
			$("#codeValidateConsole").click(function() {
				self.onProgressEvent.dispatch(this);
			});
			
			//To copy the console log content to the clip-board
			$('#codeLog').unbind("click");
			$('#codeLog').click(function() {
				commonVariables.navListener.copyToClipboard($('#iframePart'));
			});
			
			/* $(window).resize(function() {
			
				$(".dyn_popup").hide();

				var height = $(this).height();
				var twowidth = window.innerWidth/1.6;
				var onewidth = window.innerWidth - (twowidth+70);
				
				$('.features_content_main').height(height - 230);
				$('.unit_progress').height(height - 230);
				
				$('.unit_info').css("width",twowidth);
				$('.unit_progress').css("width",onewidth);
				$('.unit_close').css("right",onewidth+10);
				
			}); */
			
			$(window).resize();
		
			$("#content_div").mCustomScrollbar({
				autoHideScrollbar:true,
				theme:"light-thin"
			});			
			
			$(".scrollContent").mCustomScrollbar({
				autoHideScrollbar:true,
				theme:"light-thin",
				advanced:{ updateOnContentResize: true}
			});

			self.customScroll($(".consolescrolldiv"));
						
		}
	});

	return Clazz.com.components.codequality.js.CodeQuality;
});