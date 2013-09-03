define(["croneExpression/croneExpression"], function() {

	Clazz.createPackage("com.components.configuration.js.listener");

	Clazz.com.components.configuration.js.listener.ConfigurationListener = Clazz.extend(Clazz.WidgetWithTemplate, {
		basePlaceholder :  window.commonVariables.basePlaceholder,
		editConfigurations : null,
		configList : [],
		configListPage : null,
		cancelEditConfigurations : null,
		configRequestBody : {},
		envJson : [],
		configTemName : [],
		bcheck : null,
		count : 0,
		serverTypeVersion : null,
		databaseTypeVersion : null,
		croneExp : null,
		nonEnvEditConfigurations : null,
	
		/***
		 * Called in initialization time of this class 
		 *
		 * @config: configurations for this listener
		 */
		initialize : function(config) {
			var self = this;
		},
		
		editConfiguration : function(envName, envSpecificVal) {
			var self=this, envSpecific;
			commonVariables.environmentName = envName;
			envSpecific = (envSpecificVal === "true") ? true : false;
			Clazz.navigationController.jQueryContainer = commonVariables.contentPlaceholder;
			if(self.editConfigurations  === null) {
				commonVariables.navListener.getMyObj(commonVariables.editConfiguration, function(retVal) {
					self.editConfigurations = retVal;
					self.editConfigurations.envSpecificVal = envSpecific;
					self.editConfigurations.configClick = "EditConfiguration";
					Clazz.navigationController.push(self.editConfigurations, commonVariables.animation);
				});
			} else {
				self.editConfigurations.envSpecificVal = envSpecific;
				self.editConfigurations.configClick = "EditConfiguration";
				Clazz.navigationController.push(self.editConfigurations, commonVariables.animation);
			}
		},

		windowResize : function(){
			var resultvalue = 0;
			$('.features_content_main').prevAll().each(function() {
				resultvalue = resultvalue + $(this).height(); 
			});
			
			resultvalue = resultvalue + $('.footer_section').height() + 65;
			$('.features_content_main').height($(window).height() - (resultvalue + 155));
		},
		
		cancelEditConfiguration : function() {
			var self=this;
			Clazz.navigationController.jQueryContainer = commonVariables.contentPlaceholder;
			if(self.cancelEditConfigurations  === null) {
				commonVariables.navListener.getMyObj(commonVariables.configuration, function(retVal) {
					self.cancelEditConfigurations = retVal;
					Clazz.navigationController.push(self.cancelEditConfigurations, commonVariables.animation, commonVariables.animation);
				});
			} else {
				Clazz.navigationController.push(self.cancelEditConfigurations, commonVariables.animation, commonVariables.animation);
			}
		},
		
		getConfigurationList : function(header, callback) {
			var self = this;
			try {
				self.showpopupLoad();
				if (self.bcheck === false) {
				}
				commonVariables.api.ajaxRequest(header,
					function(response) {
						self.hidePopupLoad();
						if (response !== null && (response.status !== "error" || response.status !== "failure")) {
							if(response.responseCode === "PHR600006" || response.responseCode === "PHR600015") {
								$(".msgdisplay").removeClass("error").addClass("success");
								$(".success").attr('data-i18n', 'successCodes.' + response.responseCode);
								self.renderlocales(commonVariables.contentPlaceholder);	
								$(".success").show();
								$(".success").fadeIn(500).fadeOut(500).fadeIn(500).fadeOut(500).fadeIn(500).fadeOut(5);
								setTimeout(function() {
									$(".success").hide();
									callback(response);
								},1200);
							}
							else {
								callback(response);
							}
						} else {
							self.hidePopupLoad();
							//self.loadingScreen.removeLoading();
							$(".msgdisplay").removeClass("success").addClass("error");
							$(".error").attr('data-i18n', 'errorCodes.' + response.responseCode);
							self.renderlocales(commonVariables.contentPlaceholder);	
							$(".error").show();
							$(".error").fadeIn(500).fadeOut(500).fadeIn(500).fadeOut(500).fadeIn(500).fadeOut(5);
							setTimeout(function() {
								$(".error").hide();
							},2500);
						}

					},

					function(textStatus, xhr, e) {
						self.hidePopupLoad();
						if (self.bcheck === false) {
						}
						$(".msgdisplay").removeClass("success").addClass("error");
						$(".error").attr('data-i18n', 'errorCodes.' + response.responseCode);
						self.renderlocales(commonVariables.contentPlaceholder);	
						$(".error").show();
						$(".error").fadeIn(500).fadeOut(500).fadeIn(500).fadeOut(500).fadeIn(500).fadeOut(5);
						setTimeout(function() {
							$(".error").hide();
						},2500);
					}
				);
			} catch(exception) {
				self.hidePopupLoad();
				if (self.bcheck === false) {
				}
			}

		},
		
		showpopupLoad : function(){
			$('.popuploading').show();
		},
		
		hidePopupLoad : function(){
			$('.popuploading').hide();
		},
		
		getRequestHeader : function(configRequestBody, action, deleteEnv) {
			var self = this, header, appDirName;
			var customerId = self.getCustomer();
			customerId = (customerId === "") ? "photon" : customerId;
			appDirName = commonVariables.api.localVal.getSession("appDirName");
			data = JSON.parse(commonVariables.api.localVal.getSession('userInfo'));
			if(data !== null) {
				var userId = data.id;
			}
			var techId = commonVariables.techId;
			self.bcheck = false;
			header = {
				contentType: "application/json",
				dataType: "json",
				requestMethod : "GET",
				webserviceurl : ''
			};
			if (action === 'list') {
				if (configRequestBody.envSpecific === false && configRequestBody.configType !== "") {
					header.webserviceurl = commonVariables.webserviceurl+commonVariables.configuration+"/allEnvironments?appDirName="+appDirName+"&isEnvSpecific="+configRequestBody.envSpecific+"&configType="+configRequestBody.configType;
				} else {				
					header.webserviceurl = commonVariables.webserviceurl+commonVariables.configuration+"/allEnvironments?appDirName="+appDirName;
				}
			} else if (action === 'edit') {
				if (configRequestBody.envSpecific === false) {
					header.webserviceurl = commonVariables.webserviceurl+commonVariables.configuration+"?appDirName="+appDirName+"&configName="+commonVariables.environmentName+"&isEnvSpecific="+configRequestBody.envSpecific;
				} else {
					header.webserviceurl = commonVariables.webserviceurl+commonVariables.configuration+"?appDirName="+appDirName+"&envName="+commonVariables.environmentName;
				}
			}else if (action === 'configTypes') {
				self.bcheck = true;
				header.webserviceurl = commonVariables.webserviceurl+commonVariables.configuration+"/types?customerId="+customerId+"&userId="+userId+"&techId="+techId;
			} else if (action === 'delete') {
				self.bcheck = true;
				header.requestMethod = "DELETE";
				header.webserviceurl = commonVariables.webserviceurl+commonVariables.configuration+"/deleteEnv?appDirName="+appDirName+"&envName="+deleteEnv;
			} else if (action === "deleteConfig") {
				header.requestMethod = "DELETE";
				header.webserviceurl = commonVariables.webserviceurl+commonVariables.configuration+"/deleteConfig?appDirName="+appDirName+"&configName="+deleteEnv;
			} else if (action === "template") {
					self.bcheck = true;
					header.webserviceurl = commonVariables.webserviceurl+commonVariables.configuration+"/settingsTemplate?appDirName="+appDirName+"&customerId="+customerId+"&userId="+userId+"&type="+deleteEnv+"&techId="+techId;
			} else if (action === "saveEnv") {
					header.requestMethod = "POST";
					header.requestPostBody = JSON.stringify(configRequestBody);
					header.webserviceurl = commonVariables.webserviceurl+commonVariables.configuration+"?appDirName="+appDirName;
			} else if (action === "saveConfig") {
					header.requestMethod = "POST";
					header.requestPostBody = JSON.stringify(configRequestBody);
					if (deleteEnv === "false") {
						header.webserviceurl = commonVariables.webserviceurl+commonVariables.configuration+"/updateConfig?appDirName="+appDirName+"&isEnvSpecific="+deleteEnv+"&configName="+commonVariables.updateConfigName+"&customerId="+customerId+"&userId="+userId;
					} else {
						header.webserviceurl = commonVariables.webserviceurl+commonVariables.configuration+"/updateConfig?appDirName="+appDirName+"&envName="+deleteEnv+"&customerId="+customerId+"&userId="+userId;
					}
			} else if (action === "cloneEnv") {
					header.requestMethod = "POST";
					header.requestPostBody = JSON.stringify(configRequestBody);
					header.webserviceurl = commonVariables.webserviceurl+commonVariables.configuration+"/cloneEnvironment?appDirName="+appDirName+"&envName="+deleteEnv;
			} else if (action === "isAliveCheck") {
					header.requestMethod = "GET";
					header.webserviceurl = commonVariables.webserviceurl+commonVariables.configuration+"/connectionAliveCheck?url="+configRequestBody.protocol+","+configRequestBody.host+","+configRequestBody.port;
			} else if (action === "fileBrowse") {
					header.requestMethod = "GET";
					header.dataType = "xml";
					header.contentType = "application/xml",
					header.webserviceurl = commonVariables.webserviceurl+commonVariables.configuration+"/fileBrowse?&appDirName="+appDirName;
			} else if (action === "certificate") {
					header.requestMethod = "GET";
					header.webserviceurl = commonVariables.webserviceurl+commonVariables.configuration+"/returnCertificate?host="+configRequestBody.host+"&port="+configRequestBody.port+"&appDirName="+appDirName;
			} else if (action === "addCertificate") {
					header.requestMethod = "POST";
					header.requestPostBody = JSON.stringify(configRequestBody);
					header.webserviceurl = commonVariables.webserviceurl+commonVariables.configuration+"/addCertificate";
			}
			return header;
		},
		
		constructHtml : function(configTemplates, configuration, currentConfig, envSpecificVal){
			var flag = 0;
			var htmlTag = "";
			var key = "";
			var self=this;
			var show = "";
			var required = "";
			var editable = "";
			var multiple = "";
			var sort = "";
			var multiple = "";
			var checked = "";
			var configProperties = "";
			var bCheck = false;
			var fCheck = false;
			var configTemplate = configTemplates.data.settingsTemplate;
			if(currentConfig === 'Server') {
				self.serverTypeVersion = configTemplates.data.downloadInfo;
			}
			
			if(currentConfig === 'Database') {
				self.databaseTypeVersion = configTemplates.data.downloadInfo;
			}
			
			if (configTemplate.length !== 0) {
				var content = "";
				var configName = "";
				var configDesc = "";
				if (configuration !== null && configuration !== undefined && configuration !== "") {
					configName = configuration.name;
					configDesc = configuration.desc;
				}
				if ($.isEmptyObject(self.configTemName)) {
					self.configTemName.push(configTemplate.name);
				} else {
					var found = $.inArray(configTemplate.name, self.configTemName) > -1;
					if (found === false) {
						self.configTemName.push(configTemplate.name);
					} else {
						self.count++;
						$("tbody[name=ConfigurationLists]").children('tr.row_bg').each(function(){
							if(currentConfig === 'Server') {
								if($(this).attr('configType') === currentConfig) {
									bCheck = true;									
									$(".msgdisplay").removeClass("error").addClass("success");
									$(".success").text("Server Already Added");
									$(".success").show();
									$(".success").fadeIn(500).fadeOut(500).fadeIn(500).fadeOut(500).fadeIn(500).fadeOut(5);
									setTimeout(function() {
										$(".success").hide();
									},2500);
									flag = 1;
								}
							}
							
							if(currentConfig === 'Email') {
								if($(this).attr('configType') === currentConfig) {
									bCheck = true;		
									$(".msgdisplay").removeClass("error").addClass("success");
									$(".success").text("Email Already Added");
									$(".success").show();
									$(".success").fadeIn(500).fadeOut(500).fadeIn(500).fadeOut(500).fadeIn(500).fadeOut(5);
									setTimeout(function() {
										$(".success").hide();
									},2500);
								}
							}
						});
					}
				}
				
				if (self.count === 0) {
					self.count = "";
				}
				
				var removeIcon = "";
				if(envSpecificVal === false) { 
					removeIcon = ''; 
				} else {
					removeIcon = '<a href="javascript:;" name="removeConfig"><img src="themes/default/images/helios/close_red.png" border="0" alt="" class="flt_right"/></a>';
				}
				
				var headerTr = '<tr type="'+configTemplate.name+self.count+'" class="row_bg" envSpecificVal="'+envSpecificVal+'" configType="'+configTemplate.name+'"><td colspan="3">' + configTemplate.name + '</td><td colspan="3">'+removeIcon+'</td></tr>';
				content = content.concat(headerTr);
				var defaultTd = '<tr name="'+configTemplate.name+'" class="'+configTemplate.name+self.count+'"><td class="labelTd">Name <sup>*</sup></td><td><input type="text" id="Config'+configTemplate.name+self.count+'" mandatory="true" maxlength="30" title="30 Characters only" class="configName" value="'+configName+'" placeholder= "Configuration Name"/></td><td class="labelTd">Description</td><td><input type="text" class="configDesc" value="'+configDesc+'" maxlength="150" title="150 Characters only" placeholder= "Configuration Description"/></td>';
				content = content.concat(defaultTd);
				var count = 2;
				var i = 2;
				var configPropertiesType = "";
				if (configuration.properties !== undefined) {
					configProperties = configuration.properties;
					configPropertiesType = configProperties.type;
				}
				$.each(configTemplate.properties, function(index, value) {
					var key = value.key;
					var label = value.name;
					var type = value.type;
					
					var configValue = "";
					
					if (configProperties[key] !== undefined) {
						configValue = configProperties[key];
					}
					
					var mandatoryCtrl = "";
					var required = false;
					if (value.required) {
						mandatoryCtrl = ' <sup>*</sup>';
						required = true;
					}
					var control = "";
					if  (count % 3 === 0) {
						control = '<tr name="'+configTemplate.name+'" class="'+configTemplate.name+self.count+'"><td class="labelTd">' + label + mandatoryCtrl + '</td><td>';
					} else {
						control = '<td class="labelTd">' + label + mandatoryCtrl + '</td><td>';
					}
					var inputCtrl = "";
					if (value.possibleValues !== null &&  value.possibleValues.length !== 0) {
						inputCtrl = '<select mandatory="'+required+'" class="'+configTemplate.name+self.count+'Configuration selectpicker" configKey ="configKey'+configTemplate.name+key+'" temp="'+configTemplate.name+key+self.count+'" name="' + value.key + '">';
						var possibleValues = value.possibleValues;
						var options = "";
						for (j in possibleValues) {
							var selectedAttr = "";
							if (possibleValues[j] === configValue) {
								selectedAttr = "selected";
							}
							options = options.concat('<option value="' + possibleValues[j] + '" '+ selectedAttr +'>' + possibleValues[j] + '</option>');
						}
						inputCtrl = inputCtrl.concat(options);
						inputCtrl = inputCtrl.concat("</select></td>");
						
					} else if (type === "Password") {
						inputCtrl = '<input value="'+ configValue +'" class="'+configTemplate.name+self.count+'Configuration" name="'+key+'" mandatory="'+required+'" type="password" temp="'+configTemplate.name+key+self.count+'" placeholder=""/>';
					} else if (type === "FileType") {
						inputCtrl = '<div id="file-uploader'+currentConfig+'" class="file-uploader" propTempName="'+key+'"><noscript><p>Please enable JavaScript to use file uploader.</p><!-- or put a simple form for upload here --></noscript>  </div>';
						fCheck = true;
					} else if (type === "Actions") {
						inputCtrl = '<input value="'+ label +'" class="'+configTemplate.name+self.count+'Configuration btn btn_style" name="'+key+'" mandatory="'+required+'" type="button" />';
						fCheck = true;
					} else if (key === "scheduler") {
						inputCtrl = '<input value="'+ configValue +'" class="'+configTemplate.name+self.count+'Configuration" temp="'+configTemplate.name+key+self.count+'" name="'+key+'" mandatory="'+required+'" type="text" placeholder=""/><a name="cron_expression"><img src="themes/default/images/helios/settings_icon.png" width="23" height="22" border="0" alt=""></a><div id="cron_expression" class="dyn_popup" style="display:none"></div>';
					} else if (type === "Boolean") {
						var checked = "";
						if(configValue === "true") {
							checked = "checked";
						} else if(configValue === "" || configValue === null) {
							configValue = "false";
						}
						inputCtrl = '<input value="'+ configValue +'" '+checked+' class="'+configTemplate.name+self.count+'Configuration" name="'+key+'" mandatory="'+required+'" type="checkbox"/>';
					} else {
						if (key === 'type') {
							inputCtrl = '<select mandatory="'+required+'" class="'+configTemplate.name+self.count+'Configuration selectpicker" name="' + value.key + '">';
							
							var options1 = "";
							if(currentConfig === 'Server') {
								$.each(self.serverTypeVersion, function(key, value){
									var selectedAttr = "";
									if (key === configValue) {
										selectedAttr = "selected";
									}
									options1 = options1.concat('<option value="' + key + '" '+selectedAttr+'>' + key + '</option>');
								});
							
								 if ((options1 === "") && (flag === 0)){ 
									$(".msgdisplay").removeClass("error").addClass("success");
									$(".success").text("Choose the Server Type from App Info");
									$(".success").show();
									$(".success").fadeIn(500).fadeOut(500).fadeIn(500).fadeOut(500).fadeIn(500).fadeOut(5);
									setTimeout(function() {
										$(".success").hide();
									},2500);
								} 
							} else if(currentConfig === 'Database') {
								$.each(self.databaseTypeVersion, function(key, value){
									var selectedAttr = "";
									if (key === configValue) {
										selectedAttr = "selected";
									}
									options1 = options1.concat('<option value="' + key + '" '+selectedAttr+'>' + key + '</option>');
								});
							
								 if (options1 === ""){
									$(".msgdisplay").removeClass("error").addClass("success");
									$(".success").text("Choose the DB Type from App Info");
									$(".success").show();
									$(".success").fadeIn(500).fadeOut(500).fadeIn(500).fadeOut(500).fadeIn(500).fadeOut(5);
									setTimeout(function() {
										$(".success").hide();
									},2500);
									
								}
							}
							inputCtrl = inputCtrl.concat(options1);
							inputCtrl = inputCtrl.concat("</select></td>");
						} else if (key === 'version') {
							inputCtrl = '<select mandatory="'+required+'" class="'+configTemplate.name+self.count+'Configuration selectpicker" currentConfig="'+configTemplate.name+self.count+'" name="' + value.key + '">';
							var options1 = "";
							var i=0;
							if(currentConfig === 'Server') {
								if (configPropertiesType !== "") {
									$.each(self.serverTypeVersion, function(key, value){
										if (configPropertiesType === key) {
											for(var k=0; k<value.length; k++) {
												var selectedAttr = "";
												if (value[k] === configValue) {
													selectedAttr = "selected";
												}
												options1 = options1.concat('<option value="' + value[k] + '" '+selectedAttr+'>' + value[k] + '</option>');
											}
										} 
									});
								} else {
									$.each(self.serverTypeVersion, function(key, value){
										for(var k=0; k<value.length; k++) {
											options1 = options1.concat('<option value="' + value[k] + '">' + value[k] + '</option>');
										}
										i++;
										if (i === 1) {
											return false;
										}
									});
								}
							} else if(currentConfig === 'Database') {
								if (configPropertiesType !== "") {
									$.each(self.databaseTypeVersion, function(key, value){
										if (configPropertiesType === key) {
											for(var k=0; k<value.length; k++) {
												var selectedAttr = "";
												if (value[k] === configValue) {
													selectedAttr = "selected";
												}
												options1 = options1.concat('<option value="' + value[k] + '" '+selectedAttr+'>' + value[k] + '</option>');
											}
										} 
									});
								} else {
									$.each(self.databaseTypeVersion, function(key, value){
										for(var k=0; k<value.length; k++) {
											options1 = options1.concat('<option value="' + value[k] + '">' + value[k] + '</option>');
										}
										i++;
										if (i === 1) {
											return false;
										}
									});
								}
							}
							inputCtrl = inputCtrl.concat(options1);
							inputCtrl = inputCtrl.concat("</select></td>"); 
						} else if (key === 'certificate') { 
							inputCtrl = '<input mandatory="'+required+'" value="'+ configValue +'" class="'+configTemplate.name+self.count+'Configuration" name="'+key+'" temp="'+configTemplate.name+key+self.count+'" type="text" placeholder=""/><a href="#" name="remote_deploy"><img src="themes/default/images/helios/settings_icon.png" width="23" height="22" border="0" alt=""></a><div id="remote_deploy" class="dyn_popup" style="display:none"><div id="certificateValue"></div><div class="flt_right"><input type="button" name="selectFilePath" class="btn btn_style" value="Ok">&nbsp;&nbsp;<input type="button" value="Close" name="treePopupClose" class="btn btn_style dyn_popup_close"></div></div>';
						} else {
							inputCtrl = '<input mandatory="'+required+'" value="'+ configValue +'" configKey ="configKey'+configTemplate.name+key+'" class="'+configTemplate.name+self.count+'Configuration" name="'+key+'" temp="'+configTemplate.name+key+self.count+'" type="text" placeholder=""/>';
						}
					}
					control = control.concat(inputCtrl);
					content = content.concat(control);
					i = count++;
				});
				if (bCheck === false) {
					$("tbody[name=ConfigurationLists]").append(content);
					if (currentConfig === 'Server') {
						self.remoteDeploy();
					}
					self.multiselect();
					$("a[name=removeConfig]").unbind("change");
					$('select[name=type]').unbind("change");
					self.severDbOnChangeEvent();
					
					var host = $('input[temp='+configTemplate.name+key+'host'+self.count+']').val();
					var port = $('input[temp='+configTemplate.name+key+'port'+self.count+']').val();
					var protocol = $('select[temp='+configTemplate.name+key+'protocol'+self.count+'] option:selected').val();
					var type = configTemplate.name+self.count;
					if (protocol === undefined) {
						protocol = "http";
					}
					self.isAliveCheck(host, port, type, protocol);
					if (currentConfig === 'Scheduler') {
						self.CroneExpression();
					}
					
				}
								
				$("a[name=removeConfig]").unbind("click");
				self.removeConfiguration();
				self.spclCharValidation();
				if (fCheck === true) {
					self.createUploader("file-uploader"+currentConfig);
				}
			}
		},
		
		createUploader : function(id) {     
			var self = this, appDirName;
			appDirName = commonVariables.api.localVal.getSession("appDirName");
            var uploader = new qq.FileUploader({
                element: document.getElementById(id),
                action: commonVariables.webserviceurl+commonVariables.configuration+'/upload',
				actionType : "configuration",
				appDirName : appDirName,
				buttonLabel: "Upload File",
				multiple: false,
				debug: true,	
				onComplete: function (id, fileName, responseJSON) {
					$("img[name=removeFile]").click(function(){
						$(this).parent().remove();
					});
				}

            });  
        },
		
		severDbOnChangeEvent : function () {
			var self=this;	
			$('select[name=type]').change(function() {
				var configTypeClass = $(this).parent().parent().attr('class');
				var configtype = $(this).parent().parent().attr('name');
				var configValue = $(this).val();
				var options = "";
				if(configtype === 'Server') {
					$.each(self.serverTypeVersion, function(key, value) {
						if (configValue === key) {
							$('select[currentConfig='+configTypeClass+']').html('');
							for(var k=0; k<value.length; k++) {
								options += '<option value="' + value[k] + '">' + value[k] + '</option>';
							}
							$('select[currentConfig='+configTypeClass+']').append(options);
							$('select[currentConfig='+configTypeClass+']').selectpicker('refresh');
						}
					});
				} 
				if (configtype === 'Database') {
					$.each(self.databaseTypeVersion, function(key, value) {
						if (configValue === key) {
							$('select[currentConfig='+configTypeClass+']').html('');
							for(var k=0; k<value.length; k++) {
								options += '<option value="' + value[k] + '">' + value[k] + '</option>';
							}
							$('select[currentConfig='+configTypeClass+']').append(options);
							$('select[currentConfig='+configTypeClass+']').selectpicker('refresh');
						}
					});
				}
			});
			
		},
		
		remoteDeploy : function() {
			var self=this;
			$("input[name=certificate]").parent().prev('td').hide();
			$("a[name=remote_deploy]").html('');
			$("input[name=certificate]").hide();
			if ($("input[name=remoteDeployment]").val() === '' && $("input[name=remoteDeployment]").val() === null) {
				$("input[name=remoteDeployment]").val(false);
			} else if ($("input[name=remoteDeployment]").val() === "true") {
				self.showCertificate();
			}
			
			$("input[name=remoteDeployment]").click(function() {
				$("input[name=deploy_dir]").val('');
				if ($(this).is(':checked')) { 
					$(this).val(true);
					self.showCertificate();
				} else {
					$(this).val(false);
					self.hideCertificateTab();
					$("input[name=deploy_dir]").attr("mandatory", true);
					$("input[name=deploy_dir]").parent().prev('td').show();
					$("input[name=deploy_dir]").show();
					$("input[name=admin_username]").attr("mandatory", false);
					$("input[name=admin_password]").attr("mandatory", false);
					$("input[name=admin_username]").parent().prev('td').find('sup').html('');
					$("input[name=admin_password]").parent().prev('td').find('sup').html('');
				}
			});
			
			$('a[name=remote_deploy]').click(function(){
				var current = this;
				var val = {}, disName = [];
				val.host = $("input[configKey=configKeyServerhost]").val();
				val.port = $("input[configKey=configKeyServerport]").val();
				self.configRequestBody = val;
				self.getConfigurationList(self.getRequestHeader(self.configRequestBody, "certificate", ''), function(response) {
					$("#certificateValue").css("height", "250px");
					$("#certificateValue").html('');
					if(response.data !== null) {
						disName = response.data.certificates[0].displayName.split(",");
						$("#certificateValue").append('<select name="certificateValue">"'+self.getDisName(disName)+'"</select>');
						self.popupforTree(current, $(current).attr('name'));
						var cerValue = $('select[name=certificateValue]').val();
						self.saveCertificate(cerValue);
					} else {
						self.configRequestBody = {};
						self.getConfigurationList(self.getRequestHeader(self.configRequestBody, "fileBrowse", ''), function(response) {
							self.fileTree(response, function(res){
								var temptree = $('<div class=""></div>');
								temptree.append('<ul id="filetree" class="filetree"><li><span><strong>Click here to select file path </strong></span>' + res + '</li></ul>');
								setTimeout(function(){
									$(temptree).jstree({
									"themes": {
										"theme": "default",
										"dots": false,
										"icons": false,
										"url": "themes/default/css/Helios/style.css"
									}
									}).bind("init.jstree", function(event, data){ 
									}).bind("loaded.jstree", function (event, data) {
										$("#certificateValue").append(temptree);
										self.treeclickEvent();	
										self.popupforTree(current, $(current).attr('name'));
										$("#certificateValue").mCustomScrollbar({
											autoHideScrollbar:true,
											theme:"light-thin",
											advanced:{ updateOnContentResize: true}
										});	
									}); 
								}, 100);
							});
						});
					}
				});
				
			});
			
			$('input[name=treePopupClose]').click(function(){
				$("#remote_deploy").hide();
			});
			
			$('select[configKey=configKeyServerprotocol]').change(function() {
				if($(this).val() === 'https' && $("input[configKey=configKeyServerport]").val() !== "" && $("input[configKey=configKeyServerhost]").val() !== "" && $("input[name=remoteDeployment]").is(':checked')) {
					self.showCertificateTab();
				} else {
					self.hideCertificateTab();
				}
			});
			
		},
		
		treeclickEvent : function() {
			var self=this;
			$('span.folder, span.file').click(function(e){
				$("span.folder a, span.file a").removeClass("selected");
				$(this).find("a").attr("class", "selected");
				var path = $(this).parent().attr('value');
				path = path.replace(/\+/g,' ');
				$("input[name=selectFilePath]").unbind('click');
				self.saveCertificate(path);
			});
		},
		
		saveCertificate : function(value) {
			var self=this;
			$("input[name=selectFilePath]").click(function() {
				var cerficate = value;
				$("input[name=certificate]").val(cerficate);
				var customerId = self.getCustomer();
				customerId = (customerId === "") ? "photon" : customerId;
				var appDirName = commonVariables.api.localVal.getSession("appDirName");
				var certificateJson = {};
				certificateJson.customerId = customerId;
				certificateJson.host = $("input[configKey=configKeyServerhost]").val();
				certificateJson.port = $("input[configKey=configKeyServerport]").val();
				certificateJson.appDirName = appDirName;
				certificateJson.certificateName = value;
				certificateJson.fromPage = 'configuration';
				certificateJson.environmentName = $('input[name=EnvName]').val();
				certificateJson.configName = $(this).parent().parent().parent().parent().attr('name');
				certificateJson.propValue = value;
				self.configRequestBody = certificateJson;
				self.getConfigurationList(self.getRequestHeader(self.configRequestBody, "addCertificate", ''), function(response) {
					$(".msgdisplay").removeClass("error").addClass("success");
					$(".success").attr('data-i18n', 'successCodes.' + response.responseCode);
					self.renderlocales(commonVariables.contentPlaceholder);	
					$(".success").show();
					$(".success").fadeIn(500).fadeOut(500).fadeIn(500).fadeOut(500).fadeIn(500).fadeOut(5);
					setTimeout(function() {
						$(".success").hide();
					},2500);
					$("#remote_deploy").hide();
					self.closeTreePopup();
				});
			});
		},
		
		showCertificate : function() {
			var self = this;
			$("input[name=deploy_dir]").attr("mandatory", false);
			$("input[name=deploy_dir]").parent().prev('td').hide();
			$("input[name=deploy_dir]").hide();
			$("input[name=admin_username]").attr("mandatory", true);
			$("input[name=admin_password]").attr("mandatory", true);
			$("input[name=admin_username]").parent().prev('td').append(' <sup>*</sup>');
			$("input[name=admin_password]").parent().prev('td').append(' <sup>*</sup>');
			if ($("select[configKey=configKeyServerprotocol]").val() === 'https' && $("input[configKey=configKeyServerport]").val() !== "" && $("input[configKey=configKeyServerhost]").val() !== "") {
				self.showCertificateTab();
			}
		},
		
		showCertificateTab : function () {
			var self = this, remoteValue = '<img src="themes/default/images/helios/settings_icon.png" width="23" height="22" border="0" alt="">';;
			$("input[name=certificate]").parent().prev('td').show();
			$("input[name=certificate]").show();
			$("a[name=remote_deploy]").html(remoteValue);
		},
		
		hideCertificateTab : function() {
			var self = this;
			$("input[name=certificate]").parent().prev('td').hide();
			$("input[name=certificate]").hide();
			$("input[name=certificate]").val('');
			$("a[name=remote_deploy]").html('');
		},
		
		getDisName : function(disName) {
			var self=this, option='';
			
			for(i=0; i<disName.length; i++) {
				var val = (i === 0) ? "*" : i;
				option += '<option value='+disName[i]+'>'+disName[i]+'</option>';
			}
			return option;
		},
		
		isAliveCheck : function(host, port, type, protocol) {
			var self=this, aliveJson = {};
			if (host !== '' && host !== undefined && port !== '' && port !== undefined) {
				aliveJson.host = host;
				aliveJson.port = port;
				aliveJson.type = type;
				aliveJson.protocol = protocol;
				self.configRequestBody = aliveJson;
				self.getConfigurationList(self.getRequestHeader(self.configRequestBody, "isAliveCheck", ''), function(response) {
					var typeMatch = aliveJson.type;
					if(response.data === true) {
						$(".row_bg").each(function() {
							var type = $(this).attr("type");
							if (type === typeMatch) {
								$(this).find("td:first-child").after("<td class=active>Active</td>");
							}
						});
					} else {
						$(".row_bg").each(function() {
							var type = $(this).attr("type");
							if (type === typeMatch) {
								$(this).find("td:first-child").after("<td class=inactive>In Active</td>");
							}
						});
					}
				});
			}
			
		},
		
		CroneExpression : function() {
			var self=this;
			$('a[name=cron_expression]').click(function(){
				self.opencc(this, $(this).attr('name'));
				self.croneExp = new Clazz.com.components.croneExpression.js.croneExpression();
				self.croneExp.croneExpressionlistener.rendercrontemp($("#cron_expression"));
				
			});
		},	
		
		htmlForOther : function(value) {
			var self = this, headerTr, content = '', textBox, apiKey = "", keyValue = "", type="Other", name="", desc="", addIcon = '<img src="themes/default/images/helios/plus_icon.png" border="0" alt="">';
				if (value !== null && value !== '') {
					type = value.type;
					name = value.name;
					desc = value.desc;
				}
				
				headerTr = '<tr class="row_bg" type="otherConfig" configType="'+type+'"><div class="row"><td colspan="3">' + type + '</td><td colspan="3">'+
				'<a href="javascript:;" name="removeConfig"><img src="themes/default/images/helios/close_red.png" border="0" alt="" class="flt_right"/></a></td></div></tr>';
				content = content.concat(headerTr);
				
				var defaultTd = '<tr name="configName" class="otherConfig" name="'+type+'"><td class="labelTd">Name <sup>*</sup></td><td><input type="text" id="ConfigOther" maxlength="30" title="30 Characters only" mandatory="true" class="configName" value="'+name+'" placeholder= "Configuration Name"/></td><td class="labelTd">Description</td><td><input type="text" id="ConfigOther" class="configDesc" maxlength="150" title="150 Characters only"  value="'+desc+'" placeholder= "Configuration Description"/></td>';
				content = content.concat(defaultTd);
				
				if (value !== null && value !== '') {
					$.each(value.properties, function(key, value){
						textBox = '<tr class="otherConfig" name="'+type+'"><td></td><td><input type="text" placeholder= "key" class="otherKey" value="'+key+'"/><td><input type="text" placeholder= "value" class="otherKeyValue" value="'+value+'"/></td><td><div class="flt_right icon_center"><a href="javascript:;" name="addOther"></a> <a href="javascript:;" name="removeOther"><img src="themes/default/images/helios/minus_icon.png" border="0" alt=""></a></div></td></tr>';
						content = content.concat(textBox);
					});
				} else {
					textBox = '<tr class="otherConfig" name="'+type+'"><td></td><td><input type="text" placeholder= "key" class="otherKey"/><td><input type="text" placeholder= "value" class="otherKeyValue" /></td><td><div class="flt_right icon_center"><a href="javascript:;" name="addOther"><img src="themes/default/images/helios/plus_icon.png" border="0" alt=""></a> <a href="javascript:;" name="removeOther"></a></div></td></tr>';
					content = content.concat(textBox);
				}

				$("tbody[name=ConfigurationLists]").append(content);
				if (value !== null && value !== '') {
					$("tr.otherConfig:last").find("a[name=addOther]").html(addIcon);
					if ($("tr.otherConfig").length === 1) {
						$("a[name=removeOther]").html('');
					}
				}
				$("a[name=removeConfig]").unbind("click");
				self.removeConfiguration();
				self.addClick();
		},
		
		addOtherConfig : function(toAppend) {
			var self = this, dynamicValue, textBox = '<tr class="otherConfig" name="Other"><td></td><td><input type="text" class="otherKey" placeholder= "key"/><td><input type="text" placeholder= "value" class="otherKeyValue"/></td><td><div class="flt_right icon_center"><a href="javascript:;" name="addOther"><img src="themes/default/images/helios/plus_icon.png" border="0" alt=""></a> <a href="javascript:;" name="removeOther"><img src="themes/default/images/helios/minus_icon.png" border="0" alt=""></a></div></td></tr>', minusIcon = '<img src="themes/default/images/helios/minus_icon.png" border="0" alt="">';
			dynamicValue = $(textBox).insertAfter(toAppend);
			dynamicValue.prev('tr').find('a[name="addOther"]').html('');
			dynamicValue.prev('tr').find('a[name="removeOther"]').html(minusIcon);
			$("a[name=addOther]").unbind("click");
			$("a[name=removeOther]").unbind("click");
			self.addClick();
		},
		
		addClick : function() {
			var self = this;
			$("a[name=addOther]").click(function() {
				var toAppend = $(this).parents(".otherConfig:last");
				self.addOtherConfig(toAppend);
			});
			
			$("a[name=removeOther]").click(function(){
				self.removeClick($(this));
			});
		},
		
		removeClick : function(removeValue) {
			var self=this, addIcon = '<img src="themes/default/images/helios/plus_icon.png" border="0" alt="">';
				$("a[name=addOther]").html('');
				removeValue.parent().parent().parent().remove();
				$("a[name=removeOther]").parents('tr:last').find('a[name="addOther"]').html(addIcon);
				if (($("a[name=removeOther]").parents('tr.otherConfig').length) === 1) {
					$('tr.otherConfig').find('a[name="addOther"]').html(addIcon);
					$("a[name=removeOther]").html('');
				}
		},
		
		addEnvEvent : function(envName, envDesc) {
			var self = this;
			$("ul[name=envList]").append('<li draggable="true" name="'+envName+'"><div><input type="radio" name="optionsRadiosfd"></div><div  class="envlistname" name="envListName">'+envName+'</div><input type="hidden" name="envListDesc" value="'+envDesc+'"></li>');
			$('.connected').sortable({
				connectWith: '.connected'
			});
			$("input[name=envName]").val('');
			$("input[name=envDesc]").val('');
		},
		
		saveEnvEvent : function(envWithConfig, callback) {
			var self = this, toAppend;
			self.envJson = [];
			$.each($("ul[name=envList]").children(), function(index, value) {
				var envNameDesc = {};
				envNameDesc.name = $(value).find("div[name=envListName]").html();
				envNameDesc.desc = $(value).find("input[name=envListDesc]").val();
				envNameDesc.configurations = [];
				for (i=0; i<envWithConfig.length; i++) {
					if(envWithConfig[i].name === envNameDesc.name) {
						if (envWithConfig[i].configurations !== null) {
							envNameDesc.configurations = envWithConfig[i].configurations;
						}
					}
				}
				envNameDesc.appliesTo = [""];
				envNameDesc.defaultEnv = $(value).find("input[name=optionsRadiosfd]").is(':checked');
				if (envNameDesc.name !== undefined && envNameDesc.name !== null) {
					self.envJson.push(envNameDesc);
				}
			});
			callback(self.envJson);
		},
		
		addConfiguration : function(config) {
			var self=this;
			if (config !== "Other") {
				self.getConfigurationList(self.getRequestHeader(self.configRequestBody, "template", config), function(response) {
					self.constructHtml(response, '', config, '', true);
				});
			} else {
				self.htmlForOther('');
			}
		},
		
		removeConfiguration : function() {
			var self = this;
			$("a[name=removeConfig]").click(function() {
				self.removeConfigFunction($(this));
			});
		},
		
		removeConfigFunction : function(removeValue) {
			var currentRow = removeValue.parent().parent().next();
			removeValue.parent().parent().addClass('remove');				
			while(currentRow !== null && currentRow.length > 0) {
			   if (currentRow.attr('class') !== "row_bg") {
				   currentRow.addClass('remove');
				   currentRow = currentRow.next('tr');
			   } else {
				currentRow = null;
			   }
			}
			$('.remove').remove();
		},
		
		updateConfig : function() {
			var self=this, configJson = {}, properties = {};
			self.configList = [];
			$.each($(".row_bg"), function(index, value) {
				properties = {};
				configJson = {};
				var type = $(this).attr("type");
				$("." + type).each(function() {
					if ($(this).children().find('.configName').val() !== undefined) {
						configJson.name = $(this).children().find('.configName').val();
					}
					if ($(this).children().find('.configDesc').val() !== undefined) {
						configJson.desc = $(this).children().find('.configDesc').val();
					}
					$(this).find("." + type + "Configuration").each(function() {
						var proValue = $(this).attr("name");
						if(proValue !== undefined) {
							properties[proValue] = $(this).val();
						}
					});
					
					var otherKey = $(this).children().find('.otherKey').val();
					var otherKeyValue = $(this).children().find('.otherKeyValue').val();
					if (otherKey !== undefined && otherKeyValue !== undefined) {
						properties[otherKey] = otherKeyValue;
						configJson.properties = properties;
					} else {
						configJson.properties = properties;
					}
					configJson.type = $(this).attr("name");
				});
				self.configList.push(configJson);
			});
			
			var envrName = "";
			if ($('input[name=EnvName]').val() !== undefined) {
				envrName = $('input[name=EnvName]').val();
			} else {
				envrName = $(".row_bg").attr('envspecificval');
			}
			
			self.configRequestBody = self.configList;
			if(self.validation()) {
				self.getConfigurationList(self.getRequestHeader(self.configRequestBody, "saveConfig", envrName), function(response) {
					Clazz.navigationController.jQueryContainer = commonVariables.contentPlaceholder;
					if(self.configListPage === null) {
						commonVariables.navListener.getMyObj(commonVariables.configuration, function(retVal) {
							self.configListPage = retVal;
							Clazz.navigationController.push(self.configListPage, true);
						});
					} else {
						Clazz.navigationController.push(self.configListPage, true);
					}
				});
			}
		},
		
		validation : function() {
			var self = this, bCheck = true;
			$.each($(".row_bg"), function(index, value) {
				bCheck = false;
				var type = $(this).attr("type");
				if (type !== "otherConfig") {
					$("." + type).each(function() {
						$(this).find("." + type + "Configuration").each(function() {
							$(".configName").each(function() {
								var id = $(this).attr("id");
								var val = $("#"+id).val();
								if(val === undefined || val === null || $.trim(val) === ""){
									bCheck = false;
									$("#"+id).attr('placeholder','Enter Configuration Name');
									$("#"+id).addClass("errormessage");
									$("#"+id).focus();
									$("#"+id).val('');
									$("#"+id).bind('keypress', function() {
									$(this).removeClass("errormessage");
									});
									return bCheck;
								} else {
									bCheck = true;
								}
							});
							if(bCheck === false){
								return bCheck;
							}
							var mandatory = $(this).attr("mandatory");
							var val = $(this).attr("name");
							if(mandatory === 'true') {
								if($(this).val() !== undefined && $(this).val() !== null && $.trim($(this).val()) !== ""){
									if(val === "emailid") {
										var email = $("input[name=emailid]").val();
										var emailMatcher = /^([a-zA-Z0-9_\.\-])+\@(([a-zA-Z0-9\-])+\.)+([a-zA-Z0-9]{2,4})+$/;
										if (!emailMatcher.test(email)) {
											$("input[name=emailid]").val("");
											$("input[name=emailid]").attr('placeholder','Please Enter valid email address');
											$("input[name=emailid]").addClass("errormessage");
											$("input[name=emailid]").bind('keypress', function() {
												$(this).removeClass("errormessage");
											});
											bCheck = false;
										} else {
											bCheck = true;
										}
							
									} else {
										bCheck = true;
									}
								} else{
									bCheck = false;
									var temp = $(this).attr("temp");
									$("input[temp='"+temp+"']").attr('placeholder','Enter Value');
									$("input[temp='"+temp+"']").addClass("errormessage");
									$("input[temp='"+temp+"']").focus();
									$("input[temp='"+temp+"']").bind('keypress', function() {
										$(this).removeClass("errormessage");
									});
									return bCheck;
								}
							}
						});
						if(bCheck === false){
							return bCheck;
						}
					});
				} else {
					$(".configName").each(function() {
						var id = $(this).attr("id");
						var val = $("#"+id).val();
						if(val === undefined || val === null || $.trim(val) === ""){
							bCheck = false;
							$("#"+id).attr('placeholder','Enter Configuration Name');
							$("#"+id).addClass("errormessage");
							$("#"+id).focus();
							$("#"+id).val('');
							$("#"+id).bind('keypress', function() {
							$(this).removeClass("errormessage");
							});
							return bCheck;
						} else {
							bCheck = true;
						}
					});
					if(bCheck === false){
						return bCheck;
					}
					
					$('.otherKey').each(function() {
						if($(this).val() !== undefined && $(this).val() !== null && $.trim($(this).val()) !== "") {
							bCheck = true;
						} else {
							bCheck = false;
							$(this).attr('placeholder','Enter Key');
							$(this).addClass("errormessage");
							$(this).focus();
							$(this).bind('keypress', function() {
								$(this).removeClass("errormessage");
							});
							return bCheck;
						}
					});
		
					$('.otherKeyValue').each(function() {
						if($(this).val() !== undefined && $(this).val() !== null && $.trim($(this).val()) !== "") {
							bCheck = true;
						} else {
							bCheck = false;
							$(this).attr('placeholder','Enter Value');
							$(this).addClass("errormessage");
							$(this).focus();
							$(this).bind('keypress', function() {
								$(this).removeClass("errormessage");
							});
							return bCheck;
						}
		
					});
				}
				if(bCheck === false) {
					return bCheck;
				}
			});

			return bCheck;
		},

		
		spclCharValidation : function() {
			var self = this;
			$("input[name=port]").focusout(function (e) {
				var str = $(this).val();
				str = str.replace(/[^0-9]+/g, '');
				$(this).val(str);
			});
			
			$("input[name=host]").focusout(function (e) {
				var str = $(this).val();
				str = str.replace(/[^a-zA-Z 0-9\.\-\_]+/g, '');
				$(this).val(str);
			});
		},
		
		cloneEnv : function(val, envName, callback) {
			var self = this;
			var ename = $(val).closest('tr').prev('tr').find('td:first').find('input').val();
			var count = 0;
			var flag = 1;
			var arr = [];
			var array = [];
			if(ename === "") {	
				$("input[name='envrName']").focus();
				$("input[name='envrName']").attr('placeholder','Enter Environment Name');
			} else {
				$('.envlist').each(function() {
					arr[count]=$(this).find('td:first').text();
					array[count] = (arr[count].replace(/\s*\(.*?\)\s*/g, ''));
					count++;
				});
				for (var i=0; i<array.length; i++) {
					if($.trim(ename) === $.trim(array[i])) {
						flag = 0;
					}		
				};
				if (flag === 0) {
					$(".content_end").show();
					$(".msgdisplay").removeClass("success").addClass("error");
					$(".error").text('Enter Unique Name.');
					$(".error").fadeIn(500).fadeOut(500).fadeIn(500).fadeOut(500).fadeIn(500).fadeOut(5);
					setTimeout(function() {
						$(".content_end").hide();
					},2500);
					$(val).closest('tr').prev('tr').find('td:first').find('input').focus(); 
				} else {
					var desc = $(".cloneEnvDesc"+envName).val();
					var defaultEnv = $('input[name=cloneName'+envName+']').is(':checked');
					self.constructJson(ename, desc, defaultEnv, function(response){
						callback(response); 
					});
				};
			}
		},
		
		constructJson : function (ename, desc, defaultEnv, callback) {
			var self = this, envJson = {};
			envJson.name = ename;
			envJson.desc = desc;
			envJson.defaultEnv = defaultEnv;
			callback(envJson);
		},
		
		nonEnvConfig : function (configName, envSpecificVal) {
			var self=this;
			Clazz.navigationController.jQueryContainer = commonVariables.contentPlaceholder;
			commonVariables.navListener.getMyObj(commonVariables.editConfiguration, function(retVal) {
				self.nonEnvEditConfigurations = retVal;
				self.nonEnvEditConfigurations.configName = configName;
				self.nonEnvEditConfigurations.configClick = "AddConfiguration";
				self.nonEnvEditConfigurations.envSpecificVal = (envSpecificVal === "false") ? false : true;
				Clazz.navigationController.push(self.nonEnvEditConfigurations, true);
			});
		}
		
	});

	return Clazz.com.components.configuration.js.listener.ConfigurationListener;
});