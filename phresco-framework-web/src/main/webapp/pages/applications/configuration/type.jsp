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
<%@ page import="org.apache.commons.collections.CollectionUtils"%>
<%@ page import="org.apache.commons.lang.StringUtils" %>

<%@ page import="com.photon.phresco.model.SettingsInfo"%>
<%@ page import="com.photon.phresco.model.SettingsTemplate"%>
<%@ page import="com.photon.phresco.model.PropertyTemplate"%>
<%@ page import="com.photon.phresco.commons.FrameworkConstants"%>
<%@ page import="com.photon.phresco.model.I18NString"%>
<%@ page import="com.photon.phresco.model.Server" %>
<%@ page import="com.photon.phresco.model.Database"%>
<%@ page import="com.photon.phresco.util.TechnologyTypes"%>
<%@ page import="com.photon.phresco.model.ProjectInfo"%>
<%@ page import="com.photon.phresco.model.Technology"%>
<%@ page import="com.photon.phresco.util.Constants"%>

<%
    String value = "";
	String selectedValue = "";
	String selectedVersion = "";
	String disableStr = "";
    String oldName = (String) request.getParameter(FrameworkConstants.REQ_OLD_NAME);
    SettingsInfo settingsInfo = (SettingsInfo) request.getAttribute(FrameworkConstants.REQ_CONFIG_INFO);
    
    ProjectInfo projectInfo = (ProjectInfo)request.getAttribute(FrameworkConstants.REQ_PROJECT_INFO);
    String projectCode = "";
    List<Server> projectInfoServers = null;
    List<Database> projectInfoDatabases = null;
    if (projectInfo != null) {
    	projectCode = projectInfo.getCode();
    	Technology technology = projectInfo.getTechnology();
		projectInfoServers = technology.getServers();
		projectInfoDatabases = technology.getDatabases();
    }
    
    SettingsTemplate settingsTemplate = (SettingsTemplate)request.getAttribute(FrameworkConstants.REQ_CURRENT_SETTINGS_TEMPLATE);
    if (settingsTemplate != null) {
	    List<PropertyTemplate> propertyTemplates = settingsTemplate.getProperties();
	    for (PropertyTemplate propertyTemplate : propertyTemplates) {
	    	String masterKey = "";
	        String key = propertyTemplate.getKey();
	        String propType = propertyTemplate.getType();
	        List<String> possibleValues = propertyTemplate.getPossibleValues();
	        boolean isRequired = propertyTemplate.isRequired();
	        
			I18NString i18NString = propertyTemplate.getName();
	        String label = i18NString.get("en-US").getValue();
			
			I18NString descStr = propertyTemplate.getDescription();
			String desc = "";
			if (descStr != null) {
	        	desc = descStr.get("en-US").getValue();
			}
	        
	        if (key.equals(Constants.SETTINGS_TEMPLATE_SERVER) || key.equals(Constants.SETTINGS_TEMPLATE_DB)) {
        		List<PropertyTemplate> comPropertyTemplates = propertyTemplate.getPropertyTemplates();
        		for (PropertyTemplate comPropertyTemplate : comPropertyTemplates) {
        			if (comPropertyTemplate.getKey().equals("type") && settingsInfo != null) {
        				selectedValue = settingsInfo.getPropertyInfo(comPropertyTemplate.getKey()).getValue();
        			}
        			if (comPropertyTemplate.getKey().equals("version") && settingsInfo != null) {
        				selectedVersion = settingsInfo.getPropertyInfo(comPropertyTemplate.getKey()).getValue();
        			}
        		}
        	}
	        
	        if (settingsInfo != null && settingsInfo.getPropertyInfo(key) != null) {
	        	value = settingsInfo.getPropertyInfo(key).getValue();
	        }
%>
	
    <div class="clearfix" id="<%= key %>">
    	
        <label for="<%= key %>" class="new-xlInput">
        	<% if (isRequired == true) { %>
        		<span class="red">*</span> 
        	<% } %>
        	
        	<%= label %>
        </label>
       
        <div class="input new-input">
	        <div class="typeFields">
		        <% 
		        	if (key.equals(FrameworkConstants.ADMIN_FIELD_PASSWORD) || key.equals(FrameworkConstants.PASSWORD)) {
		        %>
						<input class="xlarge" id="<%= label %>" name="<%= key %>" type="password"  value ="<%= value %>" placeholder="<%= desc %>"/>
		        <%  
		        	} else if ((key.equals(Constants.SETTINGS_TEMPLATE_SERVER) || key.equals(Constants.SETTINGS_TEMPLATE_DB)) && possibleValues == null) {
		        		masterKey = key;
		        		List<PropertyTemplate> compPropertyTemplates = propertyTemplate.getPropertyTemplates();
		        		for (PropertyTemplate compPropertyTemplate : compPropertyTemplates) {
		        			i18NString = propertyTemplate.getName();
		        	        label = i18NString.get("en-US").getValue();
		        			key = compPropertyTemplate.getKey();
		        		}
		        %>
	        			<select id="type" name="type" class="selectEqualWidth  server_db_Width">
	                	
	                	</select>
	        			    
	        			<div class="versionDiv">
							<label id="versionsLbl" class="versionsLbl"><s:text name="label.version"/></label>&nbsp;&nbsp;&nbsp;
	                		<select id="version" name="version" class="config_version_select">
	                		
	                		</select>		                		
                		</div>
                <%
		        	} else if ("remoteDeployment".equals(key)) {
		        		 boolean remoteDeply = Boolean.parseBoolean(value);
		        		 String checkedStr = "";
		        		 if (remoteDeply) {
		        			 checkedStr = "checked";
		        		 }
		        %>		
		        		<input type="checkbox" id="<%= label %>" name="<%= key %>" value="true" <%= checkedStr %> style = "margin-top: 8px;">
		        		&nbsp;&nbsp;&nbsp;&nbsp;
		        		<input type="button" value="<s:text name="label.authenticate"/>" id="authenticate" class="primary btn hideContent"/>
		        <%	
		        	} else if (possibleValues == null) {
		        %>
		        		<input class="xlarge" id="<%= label %>" name="<%= key %>" type="text"  value ="<%= value %>" placeholder="<%= desc %>"/>
		        <% 	} else { %>
	        			<select id="<%= label %>" name="<%= key %>" class="selectEqualWidth">
	        				<option disabled="disabled" selected="selected" value="" style="color: #BFBFBF;"><%= desc %></option>
	        				<% 
	        					String selectedStr = "";
        						List<String> selectedType = null;
	        					for(String possibleValue : possibleValues) {
	        						if (possibleValue.equals(value) ) {
    									selectedStr = "selected";
    								} else {
    									selectedStr = "";
    								}
	        				%>
	        					<option value="<%= possibleValue %>" <%= selectedStr %> > <%= possibleValue %></option>
	        				<%	
	        					}
	        				%>
	                	</select>
		        <% } %>
	        </div>
	        
			<div>		
				<div class="lblDesc configSettingHelp-block" id="<%= key %>ErrorDiv">
					
				</div>
       		</div>
       		
        </div>
    </div>
<%
    	}
    }
%>

<div id="IISServerDiv" class="hideContent">
	 <div class="clearfix" id="siteNameErrDiv">
		<label class="new-xlInput"><span class="red">*</span> <s:text name="label.site.name"/> </label>
		<div class="input new-input">
			<div class="typeFields">
				<%
					String siteName = "";
					if (settingsInfo != null && settingsInfo.getPropertyInfo(FrameworkConstants.SETTINGS_TEMP_KEY_SITE_NAME) != null) {
						siteName = settingsInfo.getPropertyInfo(FrameworkConstants.SETTINGS_TEMP_KEY_SITE_NAME).getValue();							
					}
				%>
				<input class="xlarge settings_text" id="nameOfSite" name="nameOfSite" type="text" placeholder="<s:text name="placeholder.site.name"/>" 
					value="<%= siteName %>"/>
			</div>
			<div>
				<div class="lblDesc configSettingHelp-block" id="siteNameErrMsg">
				    
				</div>
			</div>
		</div>
    </div>
	<div class="clearfix" id="appNameErrDiv">
		<label class="new-xlInput"><span class="red">*</span> <s:text name="label.app.name"/> </label>
		<div class="input new-input">
			<div class="typeFields">
				<%
					String appName = "";
					if (settingsInfo != null && settingsInfo.getPropertyInfo(FrameworkConstants.SETTINGS_TEMP_KEY_APP_NAME) != null) {
						appName = settingsInfo.getPropertyInfo(FrameworkConstants.SETTINGS_TEMP_KEY_APP_NAME).getValue();							
					}
				%>
				<input class="xlarge settings_text" id="appName" name="appName" type="text" placeholder="<s:text name="placeholder.app.name"/>"
					value="<%= appName %>"/>
			</div>
			<div>
				<div class="lblDesc configSettingHelp-block" id="appNameErrMsg">
				    
				</div>
			</div>
		</div>
    </div>
</div>

<script type="text/javascript">
	$("div#certificate").hide();
	
	enableOrDisabAuthBtn();
	
	$(document).ready(function() {
		enableScreen();
		
		/** To display projectInfo servers starts **/
		<% if (CollectionUtils.isNotEmpty(projectInfoServers)) { %>
				if ($('#configType').val() == "Server") {
					$('#type').find('option').remove();
					<%
						for (Server projectInfoServer : projectInfoServers) {
							String serverName = projectInfoServer.getName();
					%>
							$('#type').append($("<option></option>").attr("value", '<%= serverName %>').text('<%= serverName %>'));
					<% } %>
				}
		<% } %>
		/** To display projectInfo servers ends **/
		
		/** To display projectInfo databases starts **/
		<% if (CollectionUtils.isNotEmpty(projectInfoDatabases)) { %>
				if ($('#configType').val() == "Database") {
					$('#type').find('option').remove();
					<%
						for (Database projectInfoDatabase : projectInfoDatabases) {
							String databaseName = projectInfoDatabase.getName();
					%>
							$('#type').append($("<option></option>").attr("value", '<%= databaseName %>').text('<%= databaseName %>'));
					<% } %>
				}
		<% } %>
		/** To display projectInfo databases ends **/
		
		getCurrentVersions(''); // To get the projectInfo servers/databases versions of the selected server/database
		
		var server = $('#type').val();
		if ((server != undefined && !isBlank(server)) &&
				(server.trim() == "Apache Tomcat" || server.trim() == "JBoss" || server.trim() == "WebLogic")) {
			$('#remoteDeployment').show(); 
		} else {
			hideRemoteDeply(); 
		}
		if (server.trim() == "IIS") {
			configForIIS("none");
			$('#IISServerDiv').css("display", "block");
		}
		
		$("#type").change(function() {
			var server = $('#type').val();
			if ((server != undefined && !isBlank(server)) &&
					(server.trim() == "Apache Tomcat" || server.trim() == "JBoss" || server.trim() == "WebLogic")) {
				$('#remoteDeployment').show();	 
			} else {
				hideRemoteDeply(); 
			}
			
			remoteDeplyChecked();
			
			if ($(this).val() != "Apache Tomcat" || $(this).val() != "JBoss" || $(this).val() != "WebLogic") {	
				$("input[name='remoteDeployment']").attr("checked",false);
				$("#admin_username label").html('Admin Username');
				$("#admin_password label").html('Admin Password'); 
			}
			 if ($(this).val() == "IIS") {
				 configForIIS("none");
				 $('#IISServerDiv').css("display", "block");
			 } else {
				 configForIIS("block");
				 $('#IISServerDiv').css("display", "none");
			 }
			
			// based on technology hide remote deployment
			technologyBasedRemoteDeploy();
		});
		
		// based on technology hide remote deploy directory
		technologyBasedRemoteDeploy();
		
		// Hide deploy dir if NodeJs server is created
		if ($("#type option:selected").val() == "NodeJS") {
			hideDeployDir();
		}
		
		// hide deploy dir if remote Deployment selected
		$("input[name='remoteDeployment']").change(function() {
			var isChecked = $("input[name='remoteDeployment']").is(":checked");
			enableOrDisabAuthBtn();
			if (isChecked) {
				hideDeployDir();
				$("#admin_username label").html('<span class="red">* </span>Admin Username');
				$("#admin_password label").html('<span class="red">* </span>Admin Password');  
			} else {
			    $("#admin_username label").html('Admin Username');
				$("#admin_password label").html('Admin Password');  
			    $('#deploy_dir').show();
			} 
		});
		 
		/** to display corressponding versions **/
		$("#type").change(function() {
			$('#deploy_dir').show();
			if ($(this).val() == "NodeJS") {
				hideDeployDir();
			}
			
			getCurrentVersions('onChange');
			
			technologyBasedRemoteDeploy();
		});
        
		$("input[name='name']").prop({"maxLength":"20", "title":"20 Characters only"});
		$("input[name='context']").prop({"maxLength":"60", "title":"60 Characters only"});
		$("input[name='port']").prop({"maxLength":"5", "title":"Port number must be between 1 and 65535"});
		$("input[name='sapSvcPort']").prop({"maxLength":"5", "title":"Port number must be between 1 and 65535"});
		
		$("input[name='port']").live('input propertychange', function (e) { 	//Port validation
        	var portNo = $(this).val();
        	portNo = checkForNumber(portNo);
        	$(this).val(portNo);
        	enableOrDisabAuthBtn();
		});
        
        $("#xlInput").live('input propertychange',function(e) { 	//Name validation
        	var name = $(this).val();
        	name = checkForSplChr(name);
        	$(this).val(name);
        });
        
        $("input[name='dbname']").live('input propertychange',function(e) { //Database Name validation
        	var name = $(this).val();
        	name = checkForSplChr(name);
        	name = removeSpace(name);
        	$(this).val(name);
        });
        
        $("input[name='sapSvcPort']").live('input propertychange', function (e) { //sapSvcPort validation
        	var portNo = $(this).val();
        	portNo = checkForNumber(portNo);
        	$(this).val(portNo);
        });
        
        $("input[name='context']").live('input propertychange',function(e) {	//Root Context validation
        	var name = $(this).val();
        	name = checkForContext(name);
        	$(this).val(name);
        });
        
        $("input[name='host']").live('input propertychange',function(e) {
			enableOrDisabAuthBtn();
		});
		
		$("select[name='protocol']").change(function() {
			enableOrDisabAuthBtn();
		});
		
		$("#authenticate").click(function() {
			showPopup();
			$('.popup_div').empty();
			var params = "host=";
	    	params = params.concat($("input[name='host']").val());
			params = params.concat("&port=");
			params = params.concat($("input[name='port']").val());
			params = params.concat("&projectCode=");
			params = params.concat('<%= projectCode %>');
			performAction('authenticateServer', '', $('#popup_div'), '', params);
		});
	});
	
	function showSetttingsInfoServer() {
		$("#type option").each(function() {
			if ($(this).val().trim().toLowerCase() == '<%= selectedValue.toLowerCase() %>') {
				$(this).prop("selected", "selected");
			}
			// When editing nodejs server config, hide deploy directory field
			if ('<%= selectedValue %>' == "NodeJS") {
				 $('#deploy_dir').hide(); 
			}
			if ('<%= selectedValue %>' == "Apache Tomcat" || '<%= selectedValue %>' == "JBoss" || '<%= selectedValue %>' == "WebLogic") {
				 $('#remoteDeployment').show(); 
		    } else {
		    	 hideRemoteDeply(); 
		    }
			remoteDeplyChecked();
		});
	}
	
	function showSetttingsInfoVersion() {
		$("#version option").each(function() {
			if ($(this).val().trim() == '<%= selectedVersion %>') {
				$(this).prop("selected", "selected");
			}
		});
	}
	
	function hideDeployDir() {
		$("input[name='deploy_dir']").val("");
		$('#deploy_dir').hide();
	}

	function hideRemoteDeply() {
		$('#remoteDeployment').hide();
	}

	function remoteDeplyChecked() {
		var isRemoteChecked = $("input[name='remoteDeployment']").is(":checked");
		if (isRemoteChecked) {
			hideDeployDir();
			$("#admin_username label").html('<span class="red">* </span>Admin Username');
			$("#admin_password label").html('<span class="red">* </span>Admin Password');  
		} else {
			$('#deploy_dir').show();
		}
	}
	
	function technologyBasedRemoteDeploy() {
		<% 
			if (TechnologyTypes.ANDROIDS.contains(projectInfo.getTechnology().getId())) {
		%>
				hideDeployDir();
				hideRemoteDeply();
		<%
			}
		%>
	}
	
	function enableOrDisabAuthBtn() {
		var protocol = $("select[name='protocol']").val();
		var host = $("input[name='host']").val();
		var port = $("input[name='port']").val();
		var isChecked = $("input[name='remoteDeployment']").is(":checked");
		if (protocol == "https" && !isBlank(host) && host != undefined && !isBlank(port) && port != undefined && isChecked) {
			$("#authenticate").removeClass("hideContent");
		} else {
			$("#authenticate").addClass("hideContent");
		}
	}
	function configForIIS(prop) {
		$('#context').css("display", prop);
		$('#additional_context').css("display", prop);
	}
</script>