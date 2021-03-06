define(["framework/base"], function(){

	Clazz.createPackage("com.js.widget.common");

	Clazz.com.js.widget.common.Loading = Clazz.extend(Clazz.Base, {
		mainContent : null,

		/**
		 * This method is used to get customize loading layout.
		 *
		 * @returns mainContent is main layout that contain loading screen and image.
		 */
		getWidgetContent : function() {
			this.mainContent = $('<div class="widget-loading-main"></div>');
			var loadingImage = $('<span class="widget-loading-image"></span>');
			this.mainContent.append(loadingImage);

			return this.mainContent;
		},

		/**
		 * This method is used to get partial loading.
		 *
		 */
		getPartialLoading : function() {
			this.mainContent = $('<div class="widget-loading-partial"></div>');
			var loadingImage = $('<span class="widget-loading-image"></span>');
			this.mainContent.append(loadingImage);
			return this.mainContent;
		},

		/**
		 * This method is used to remove customize loading layout.
		 *
		 */
		removeLoading : function() {
			$('.widget-mask-main').remove();
			$('.widget-mask-mid-content').remove();
		},

		/**
		 * This method is used to show customize loading layout.
		 *
		 */
		showLoading: function(isPartialLoading) {
			var maskMain = $('div.widget-mask-main'), maskContent = $('div.widget-mask-mid-content');
			if(maskMain.length < 1 && maskContent.length < 1){
				if (isPartialLoading !== undefined && isPartialLoading !== null) {
					var maskMain = $('<div class="widget-mask-mid-content" style="margin-top:130px;	margin-bottom:27px;"></div>');
					maskMain.append(this.getPartialLoading());
					$(isPartialLoading).append(maskMain);
				} else {
					var maskMain = $('<div class="widget-mask-main"></div>');
					maskMain.append(this.getWidgetContent());
					$("basepage\\:widget").append(maskMain);
				}
				maskMain.show();
			}
		}
	});

	return Clazz.com.js.widget.common.Loading;
});