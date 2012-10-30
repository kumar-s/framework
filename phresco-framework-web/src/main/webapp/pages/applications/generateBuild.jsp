<%--
  ###
  Framework Web Archive
  
  Copyright (C) 1999 - 2012 Photon Infotech Inc.
  
  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at
  
       http://www.apache.org/licenses/LICENSE-2.0
  
  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
  ###
  --%>
<%@ taglib uri="/struts-tags" prefix="s"%>

<%@ page import="java.util.List"%>
<%@ page import="java.util.Map"%>
<%@ page import="java.util.Arrays"%>

<%@ page import="org.apache.commons.lang.StringUtils" %>
<%@ page import="org.apache.commons.collections.CollectionUtils" %>
<%@ page import="org.antlr.stringtemplate.StringTemplate" %>

<%@ page import="com.photon.phresco.configuration.Environment"%>
<%@ page import="com.photon.phresco.commons.FrameworkConstants"%>
<%@ page import="com.photon.phresco.framework.commons.FrameworkUtil" %>
<%@ page import="com.photon.phresco.plugins.model.Mojos.Mojo.Configuration.Parameters.Parameter"%>
<%@ page import="com.photon.phresco.plugins.model.Mojos.Mojo.Configuration.Parameters.Parameter.Name.Value"%>
<%@ page import="com.photon.phresco.commons.model.ApplicationInfo"%>

<script src="js/reader.js" ></script>
<script src="js/select-envs.js"></script>

<%
    /* String defaultEnv = "";
   	String testType = (String) request.getAttribute(FrameworkConstants.REQ_TEST_TYPE);
   	String importSqlPro  = (String) request.getAttribute(FrameworkConstants.REQ_IMPORT_SQL);
   	String finalName = (String) request.getAttribute(FrameworkConstants.FINAL_NAME);
   	String mainClassValue = (String) request.getAttribute(FrameworkConstants.MAIN_CLASS_VALUE);
   	String checkImportSql = "";
   	if (importSqlPro != null && Boolean.parseBoolean(importSqlPro)) {
   	    checkImportSql = "checked";
   	} */
   	
   	//xcode targets
   	/* List<PBXNativeTarget> xcodeConfigs = (List<PBXNativeTarget>) request.getAttribute(FrameworkConstants.REQ_XCODE_CONFIGS);
   	List<String> buildInfoEnvs = (List<String>) request.getAttribute(FrameworkConstants.BUILD_INFO_ENVS);
   	List<Environment> environments = (List<Environment>) request.getAttribute(FrameworkConstants.REQ_ENVIRONMENTS); */
   	// mac sdks
   /* 	List<String> macSdks = (List<String>) request.getAttribute(FrameworkConstants.REQ_IPHONE_SDKS);
   	
   	Map<String, String> jsMap = (Map<String, String>) request.getAttribute(FrameworkConstants.REQ_MINIFY_MAP);
   	String fileLoc = (String) request.getAttribute("fileLocation"); */
   	
   	String from = (String) request.getAttribute(FrameworkConstants.REQ_BUILD_FROM);
   	String buildNumber = "";
   	if (FrameworkConstants.REQ_DEPLOY.equals(from)) {
    	buildNumber = (String) request.getAttribute(FrameworkConstants.REQ_DEPLOY_BUILD_NUMBER);
    }
   	
   	//To read parameter list from phresco-plugin-info.xml
   	List<Parameter> parameters = (List<Parameter>) request.getAttribute(FrameworkConstants.REQ_DYNAMIC_PARAMETERS);
   	
   	List<String> projectModules = (List<String>) request.getAttribute(FrameworkConstants.REQ_PROJECT_MODULES);
    
    ApplicationInfo applicationInfo = (ApplicationInfo) request.getAttribute(FrameworkConstants.REQ_APP_INFO);
    String goal = (String) request.getAttribute(FrameworkConstants.REQ_GOAL);
    String appId  = applicationInfo.getId();
%>

<form autocomplete="off" class="build_form form-horizontal" id="generateBuildForm">
<div id="generateBuild_Modal">
	<%
	    if (CollectionUtils.isNotEmpty(projectModules)) {
	%>
		<div class="control-group">
			<label class="control-label labelbold popupLbl">
				<s:text name='lbl.name' />
			</label>
			<div class="controls">
				<select name="projectModule" class="xlarge" >
					<%
						for(String projectModule : projectModules) {
					%>
						<option value="<%= projectModule %>"> <%= projectModule %></option>
					<%
						}
					%>
				</select>
			</div>
		</div>
	<%
		}
	%>

	<!-- dynamic parameters starts -->
	<%	
		if (CollectionUtils.isNotEmpty(parameters)) {
			for (Parameter parameter: parameters) {
	   			Boolean mandatory = false;
	   			String lableTxt = "";
	   			String labelClass = "";
				if (Boolean.parseBoolean(parameter.getRequired())) {
					mandatory = true;
	 			}
				if (!FrameworkConstants.TYPE_HIDDEN.equalsIgnoreCase(parameter.getType())) {
					List<Value> values = parameter.getName().getValue();						
					for(Value value : values) {
						if (value.getLang().equals("en")) {	//to get label of parameter
							labelClass = "popupLbl";
							lableTxt = value.getValue();
				   		    break;
						}
					}
				}
					
				// load input text box
				if (FrameworkConstants.TYPE_STRING.equalsIgnoreCase(parameter.getType()) || FrameworkConstants.TYPE_NUMBER.equalsIgnoreCase(parameter.getType()) || 
					FrameworkConstants.TYPE_PASSWORD.equalsIgnoreCase(parameter.getType()) || FrameworkConstants.TYPE_HIDDEN.equalsIgnoreCase(parameter.getType())) { 
				StringTemplate txtInputElement = FrameworkUtil.constructInputElement(mandatory, lableTxt, labelClass, parameter.getType(), "", parameter.getKey(), parameter.getKey(), "", StringUtils.isNotEmpty(parameter.getValue()) ? parameter.getValue():"");
	%> 	
				<%= txtInputElement %>
	<% 			
				} else if (FrameworkConstants.TYPE_BOOLEAN.equalsIgnoreCase(parameter.getType())) {
					String cssClass = "chckBxAlign";
					String onClickFunction = "";
					if (parameter.getDependency() != null) {
						//If current control has dependancy value 
						onClickFunction = "dependancyChckBoxEvent(this, '"+parameter.getDependency()+"');";
				} else {
					onClickFunction = "changeChckBoxValue(this);";
				}
				StringTemplate chckBoxElement = FrameworkUtil.constructCheckBoxElement(mandatory, lableTxt, labelClass, cssClass, parameter.getKey(), parameter.getKey(), parameter.getValue(), onClickFunction);
	%>			
					<%= chckBoxElement%>	
	<%			
				} else if (FrameworkConstants.TYPE_LIST.equalsIgnoreCase(parameter.getType()) && parameter.getPossibleValues() != null) { //load select list box
					//To construct select box element if type is list and if possible value exists
			    	List<com.photon.phresco.plugins.model.Mojos.Mojo.Configuration.Parameters.Parameter.PossibleValues.Value> psblValues = parameter.getPossibleValues().getValue();
					List<String> selectedValList = Arrays.asList(parameter.getValue().split(FrameworkConstants.CSV_PATTERN));
					StringTemplate selectElmnt = FrameworkUtil.constructSelectElement(mandatory, lableTxt, labelClass, "", parameter.getKey(), parameter.getKey(), psblValues, selectedValList, parameter.getMultiple());
	%>				
					<%= selectElmnt %>
						
	<% 			
				} else if (FrameworkConstants.TYPE_DYNAMIC_PARAMETER.equalsIgnoreCase(parameter.getType())) {
					//To dynamically load values into select box for environmet
					List<com.photon.phresco.plugins.model.Mojos.Mojo.Configuration.Parameters.Parameter.PossibleValues.Value> dynamicEnvNames = (List<com.photon.phresco.plugins.model.Mojos.Mojo.Configuration.Parameters.Parameter.PossibleValues.Value>) request.getAttribute(FrameworkConstants.REQ_DYNAMIC_POSSIBLE_VALUES);
					List<String> selectedValList = Arrays.asList(parameter.getValue().split(FrameworkConstants.CSV_PATTERN));
					StringTemplate selectDynamicElmnt = FrameworkUtil.constructSelectElement(mandatory, lableTxt, labelClass, "", parameter.getKey(), parameter.getKey(), dynamicEnvNames, selectedValList, parameter.getMultiple());
	%>				
		    		<%= selectDynamicElmnt %>
	<%
				} 
	%>
			<script type="text/javascript">
				<%-- $('input[name="<%= parameter.getKey() %>"]').live('input propertychange',function(e) {
					var name = $(this).val();
					var type = '<%= parameter.getType() %>';
					var txtBoxName = '<%= parameter.getKey() %>';
					validateInput(name, type, txtBoxName);
				}); --%>
			</script>
	<% 
			}
		}
	%>
	<!-- dynamic parameters ends -->
</div>
</form>

<script type="text/javascript">
	if(!isiPad()){
	    /* JQuery scroll bar */
		$(".generate_build").scrollbars();
	}

	var url = "";
	var readerSession = "";
	$(document).ready(function() {
		// accodion for advanced issue
// 		accordion();
		
		/** NodeJS run against source **/
		$('#runAgainstSrc').click(function() {
			var isChecked = $('#importSql').is(":checked");
			if ($('#importSql').is(":checked") && $('#selectedSourceScript option').length == 0) {
				$("#errMsg").html('<%= FrameworkConstants.SELECT_DB %>');
				return false;
			}
			buildValidateSuccess('NodeJSRunAgainstSource', '<%= FrameworkConstants.REQ_READ_LOG_FILE %>');
		});
		
		/** Java run against source **/
		$('#javaRunAgainstSrc').click(function() {
			var isChecked = $('#importSql').is(":checked");
			if ($('#importSql').is(":checked") && $('#selectedSourceScript option').length == 0) {
				$("#errMsg").html('<%= FrameworkConstants.SELECT_DB %>');
				return false;
			}
			buildValidateSuccess('runAgainstSource', '<%= FrameworkConstants.REQ_JAVA_START %>');
		});
		
		$('#importSql').click(function() {
			var isChecked = $('#importSql').is(":checked");
			if (isChecked) {
<%-- 				$("#errMsg").html('<%= FrameworkConstants.EXEC_SQL_MSG %>'); --%>
				// getting database list based on environment and and execute sqk checkbox
		    } else {
		    	$('#DbWithSqlFiles').val("");
		    	$("#errMsg").empty();
		    }
			// show hide sql execution fieldset
			executeSqlShowHide();
		});
		
		$('#hideLog').change(function() {
			var isChecked = $('#hideLog').is(":checked");
			if (isChecked) {
				$("#errMsg").html('<%= FrameworkConstants.HIDE_LOG_MSG %>');
		    } else {
		    	$("#errMsg").empty();
		    }
		});
		
		$('#androidSigning').click(function() {
// 			if ($(this).is(':checked')) {
				// remove existing duplicate div
				$('#advancedSettingsBuildForm').remove();
				showAdvSettingsConfigure();
// 			} else {
// 				removeAdvSettings();
// 			}
		})
		
		//execute sql codes
		$('#btnAdd').click(function(e) {
			addDbWithVersions();
			$('#avaliableSourceScript > option:selected').appendTo('#selectedSourceScript');
		});
			
		$('#btnAddAll').click(function(e) {
			var sqlFiles = "";
			$('#avaliableSourceScript option').each(function(i, available) {
				sqlFiles = $(available).val();
				$('#DbWithSqlFiles').val($('#DbWithSqlFiles').val() + $('#databases').val()+ "#VSEP#" + sqlFiles + "#NAME#" + $(available).text() + "#SEP#");
			});		
			$('#avaliableSourceScript > option').appendTo('#selectedSourceScript');
		});

		$('#btnRemove').click(function() {
			updateDbWithVersionsForRemove();
   			$('#selectedSourceScript > option:selected').appendTo('#avaliableSourceScript');
		});

		$('#btnRemoveAll').click(function() {
			updateDbWithVersionsForRemoveAll();
   			$('#selectedSourceScript > option').appendTo('#avaliableSourceScript');
		});
		
		//To move up the values
		$('#up').bind('click', function() {
			$('#selectedSourceScript option:selected').each( function() {
				var newPos = $('#selectedSourceScript  option').index(this) - 1;
				if (newPos > -1) {
					$('#selectedSourceScript  option').eq(newPos).before("<option value='"+$(this).val()+"' selected='selected'>"+$(this).text()+"</option>");
					$(this).remove();
				}
			});
		});
		
		//To move down the values
		$('#down').bind('click', function() {
			var countOptions = $('#selectedSourceScript option').size();
			$('#selectedSourceScript option:selected').each( function() {
				var newPos = $('#selectedSourceScript  option').index(this) + 1;
				if (newPos < countOptions) {
					$('#selectedSourceScript  option').eq(newPos).after("<option value='"+$(this).val()+"' selected='selected'>"+$(this).text()+"</option>");
					$(this).remove();
				}
			});
		});
		
		$('#environments').change(function() {
			if ($("#from").val() != "generateBuild") {
				
				$('#DbWithSqlFiles').val("");
				executeSqlShowHide();
			}
		});
		
		//execute Sql script
		executeSqlShowHide();
// 		showHideMinusIcon();
	});
	
	function addDbWithVersions() {
		//creating new data list
		var sqlFiles = "";
		$("#avaliableSourceScript :selected").each(function(i, available) {
			sqlFiles = $(available).val();
			$('#DbWithSqlFiles').val($('#DbWithSqlFiles').val() + $('#databases').val()+ "#VSEP#" + sqlFiles + "#NAME#" + $(available).text() + "#SEP#");
		});
	}
	
	function hideDbWithVersions() {
		// getting existing data list
		var nameSep = new Array();
		nameSep = $('#DbWithSqlFiles').val().split("#SEP#");
		for (var i=0; i < nameSep.length - 1; i++) {
			var addedDbs = nameSep[i].split("#VSEP#");
			var addedSqlName = addedDbs[1].split("#NAME#");
			if($('#databases').val() == addedDbs[0]) {
				$("#avaliableSourceScript option[value='" + addedSqlName[0] + "']").remove();
			}
		}
		// show corresponding DB sql files
		showSelectedDBWithVersions();
	}
	
	function showSelectedDBWithVersions() {
		$('#selectedSourceScript').empty();
		var nameSep = new Array();
		nameSep = $('#DbWithSqlFiles').val().split("#SEP#");
		for (var i=0; i < nameSep.length - 1; i++) {
			var addedDbs = nameSep[i].split("#VSEP#");
			var addedSqlName = addedDbs[1].split("#NAME#");
			if($('#databases').val() == addedDbs[0]) {
				$('#selectedSourceScript').append($("<option></option>").attr("value",addedSqlName[0]).text(addedSqlName[1])); 
			}
		}
		
		//hiding loading icon..
		hideLoadingIcon();
	}
	
	function updateDbWithVersionsForRemove() {
		var toBeUpdatedDbwithVersions = "";
		$("#selectedSourceScript option:selected").each(function(i, alreadySelected) {
			var nameSep = new Array();
			nameSep = $('#DbWithSqlFiles').val().split("#SEP#");
			for (var i=0; i < nameSep.length - 1; i++) {
				var addedDbs = nameSep[i].split("#VSEP#");
				var addedSqlName = addedDbs[1].split("#NAME#");
				if(($('#databases').val() == addedDbs[0]) && $(alreadySelected).val() != addedSqlName[0]) {
					toBeUpdatedDbwithVersions = toBeUpdatedDbwithVersions + nameSep[i] + "#SEP#";
				} else if(($('#databases').val() != addedDbs[0]) && $(alreadySelected).val() != addedSqlName[0]) {
					toBeUpdatedDbwithVersions = toBeUpdatedDbwithVersions + nameSep[i] + "#SEP#";
				}
			}
			$('#DbWithSqlFiles').val(toBeUpdatedDbwithVersions);
		});
	}
	
	function updateDbWithVersionsForRemoveAll() {
		var toBeUpdatedDbwithVersions = "";
		$("#selectedSourceScript option").each(function(i, alreadySelected) {
			var nameSep = new Array();
			nameSep = $('#DbWithSqlFiles').val().split("#SEP#");
			for (var i=0; i < nameSep.length - 1; i++) {
				var addedDbs = nameSep[i].split("#VSEP#");
				var addedSqlName = addedDbs[1].split("#NAME#");
				if(($('#databases').val() != addedDbs[0]) && $(alreadySelected).val() != addedSqlName[0]) {
					toBeUpdatedDbwithVersions = toBeUpdatedDbwithVersions + nameSep[i] + "#SEP#";
				} else if(($('#databases').val() == addedDbs[0]) && $(alreadySelected).val() == addedSqlName[0]) {
					toBeUpdatedDbwithVersions = toBeUpdatedDbwithVersions + nameSep[i] + "#SEP#";
				}
			}
			$('#DbWithSqlFiles').val(toBeUpdatedDbwithVersions);
		});
	}
	
	function executeSqlShowHide() {
		if($('#importSql').is(":checked")) {
			loadingIconShow();
			$('#sqlExecutionContain').show();
			// after fieldset completed, we have to load db and sql files
		getDatabases();
		} else {
			$('#sqlExecutionContain').hide();
		}
	}
	
	function getDatabases() {
		if(!isBlank($("#environments").val())) { 
			var params = 'environments=';
		    params = params.concat($("#environments").val());
		    <%-- params = params.concat("&projectCode=");
		    params = params.concat('<%= projectCode %>'); --%>
			performAction("getSqlDatabases", params, '', true);
		}
	}
	
	$("#databases").change(function() {
		loadingIconShow();
		getSQLFiles();
	});
	
	function getSQLFiles() {
		if(!isBlank($("#databases").val())) {
			var params = 'selectedDb=';
		    params = params.concat($("#databases").val());
		    params =  params.concat('&environments=');
		    params = params.concat($("#environments").val());
		   <%--  params = params.concat("&projectCode=");
		    params = params.concat('<%= projectCode %>'); --%>
			performAction("fetchSQLFiles", params, '', true);
		}
	}

	function loadingIconShow() {
		$('.popupLoadingIcon').show();
		getCurrentCSS();
	}
	
	function hideLoadingIcon() {
		$('.popupLoadingIcon').hide();
	}
	
	function checkObj(obj) {
		if(obj == "null" || obj == undefined) {
			return "";
		} else {
			return obj;
		}
	}
	
	function buildValidateSuccess(lclURL, lclReaderSession) {
		url = lclURL;
		readerSession = lclReaderSession;
		checkForConfig();
	}
	
	function checkForConfig() {
		loadContent('checkForConfiguration', $("#generateBuildForm"), '', getBasicParams(), true);
	}
	
	function successEnvValidation(data) {
		if(data.hasError == true) {
			showError(data);
		} else {
			$('.build_cmd_div').css("display", "block");
			$("#console_div").empty();
			//showParentPage();
			if(url == "build") {
				$("#warningmsg").show();
				$("#console_div").html("Generating build...");
			} else if(url == "deploy") {
				$("#console_div").html("Deploying project...");
			} else {
				$("#console_div").html("Server is starting...");
				disableControl($("#nodeJS_runAgnSrc"), "btn disabled");
				disableControl($("#runAgnSrc"), "btn disabled");
			}
			performUrlActions(url, readerSession);
		}
	}
	
	function performUrlActions(url, actionType) {
		<%-- var params = "";
		params = params.concat("&environments=");
		params = params.concat(getSelectedEnvs());
		params = params.concat("&DbWithSqlFiles=");
		params = params.concat($('#DbWithSqlFiles').val()); --%>
		readerHandlerSubmit(url, '<%= appId %>', actionType, $("#generateBuildForm"), true, getBasicParams());
	}
	
	function showAdvSettingsConfigure() {
		showPopup();
		popup('advancedBuildSettings', '', $('#popup_div'), '', true);
	}
	
	function changeChckBoxValue(obj) {
		if ($(obj).is(':checked')) {
			$(obj).val("true");
		} else {
			$(obj).val("false");
		}
	}
	
	//to update build number in hidden field for deploy popup
	<% if (FrameworkConstants.REQ_DEPLOY.equals(from)) { %>
		$("input[name=buildNumber]").val('<%= buildNumber %>');
	<% } %>
	
	var pushToElement = "";
	var isMultiple = "";
	var controlType = "";
	function dependancyChckBoxEvent(obj, dependantKey) {
		changeChckBoxValue(obj);
		pushToElement = dependantKey;
		isMultiple = $("#"+pushToElement).attr("isMultiple");
		controlType = $("#"+pushToElement).attr("type");
		var dependantValue = $(obj).is(':checked');
		var params = "";
		params = params.concat("dependantKey=");
		params = params.concat(dependantKey);
		params = params.concat("&dependantValue=");
		params = params.concat(dependantValue);
		params = params.concat("&goal=");
		params = params.concat('<%= goal%>');
		params = params.concat("&");
		params = params.concat(getBasicParams());
		loadContent('dependancyListener', '', '', params, true);
	}
	
	function updateDependantValue(data) {
		constructElements(data, pushToElement, isMultiple, controlType);
	}
</script>