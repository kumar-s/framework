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

<%@ page import="com.photon.phresco.framework.model.FrameworkConstants" %>
<%@ page import="com.photon.phresco.util.TechnologyTypes" %>
<%@ page import="com.photon.phresco.commons.model.ProjectInfo" %>
<%@ page import="com.photon.phresco.framework.model.BuildInfo" %>

<script type="text/javascript" src="js/delete.js" ></script>
<script type="text/javascript" src="js/confirm-dialog.js" ></script>
<script type="text/javascript" src="js/loading.js" ></script>
<script type="text/javascript" src="js/home-header.js" ></script>
<script type="text/javascript" src="js/reader.js" ></script>

<style type="text/css">
    .btn.success, .alert-message.success {
        margin-top: 5px;
        left: 21%; 
    }
    
    .table_div {
    	margin-top: 5px;
    } 
    
    .build_table_div {
	    margin-top: 5px;
	    border-radius: 6px 6px 6px 6px;
	    margin-left: 6px;
	    margin-right: 6px;
		height:98%
	}
</style>

<%
	ProjectInfo projectInfo = (ProjectInfo) request.getAttribute(FrameworkConstants.REQ_PROJECT_INFO);
	String projectCode = projectInfo.getCode();
	String technology = projectInfo.getTechnology().getId();
	
	List<BuildInfo> buildInfos = (List<BuildInfo>) request.getAttribute(FrameworkConstants.REQ_BUILD);
    String testType = (String) request.getAttribute(FrameworkConstants.REQ_TEST_TYPE);
    
  	StringBuilder sbBuildPath = new StringBuilder();
  	sbBuildPath.append(projectCode);
  	sbBuildPath.append("/");    //File separator is not working for this function.
  	sbBuildPath.append(FrameworkConstants.CHECKIN_DIR);
  	sbBuildPath.append("/");
  	sbBuildPath.append(FrameworkConstants.BUILD_PATH);
    
    String serverLog = "";
	Boolean nodeServerStatus = (Boolean) session.getAttribute(projectCode + FrameworkConstants.SESSION_NODEJS_SERVER_STATUS);
	Boolean javaServerStatus = (Boolean) session.getAttribute(projectCode + FrameworkConstants.SESSION_JAVA_SERVER_STATUS);
	
	if (TechnologyTypes.NODE_JS_WEBSERVICE.equals(technology)) {
		if (session.getAttribute(projectCode + FrameworkConstants.SESSION_NODEJS_SERVER_STATUS) == null) {
			nodeServerStatus = false;
 		} else {
 			nodeServerStatus = session.getAttribute(projectCode + FrameworkConstants.SESSION_NODEJS_SERVER_STATUS).toString().equals("true") ? true : false;
 		}
		serverLog = (String) request.getAttribute(FrameworkConstants.REQ_SERVER_LOG);
	}
	if (TechnologyTypes.HTML5_MOBILE_WIDGET.equals(technology) || TechnologyTypes.HTML5_WIDGET.equals(technology) 
			|| TechnologyTypes.JAVA_WEBSERVICE.equals(technology) || TechnologyTypes.HTML5_MULTICHANNEL_JQUERY_WIDGET.equals(technology)) {
		if (session.getAttribute(projectCode + FrameworkConstants.SESSION_JAVA_SERVER_STATUS) == null) {
			javaServerStatus = false;
 		} else {
 			javaServerStatus = session.getAttribute(projectCode + FrameworkConstants.SESSION_JAVA_SERVER_STATUS).toString().equals("true") ? true : false;
 		}
		serverLog = (String) request.getAttribute(FrameworkConstants.REQ_SERVER_LOG);
	}
%>

<s:if test="hasActionMessages()">
    <div class="alert-message success"  id="successmsg">
        <s:actionmessage />
    </div>
</s:if>

<div class="alert-message warning"  id="warningmsg" style="display:none;">
	<s:label cssClass="labelWarn" key="build.warn.message" />
</div>

<form id="formBuild" class="build_form" name="listForm" autocomplete="off">
    <div class="operation">
		<div class="build_delete_btn_div">
		    <input id="generatebtn" type="button" value="<s:text name="label.generatebuild"/>" class="primary btn env_btn">
		    <input id="deleteButton" type="button" value="<s:text name="label.delete"/>" class="btn disabled" disabled="disabled"/>
		</div> 
		
		<div class="runagint_source";>
			<% if (TechnologyTypes.NODE_JS_WEBSERVICE.equals(technology)) { %>
				<div id="nodeJS_btndiv" class="nodeJS_div">
					<input id="nodeJS_runAgnSrc" type="button" value="<s:text name="label.runagainsrc"/>" class="primary btn">
					<input id="stopbtn" type="button" value="<s:text name="label.stop"/>" class="btn disabled" onclick="stopNodeJS();">
					<input id="restartbtn" type="button" value="<s:text name="label.restart"/>" class="btn disabled" onclick="restartNodeJS();"/>
				</div>		
			<% } %>
		
			<%
				if (TechnologyTypes.JAVA_WEBSERVICE.equals(technology) || TechnologyTypes.HTML5_WIDGET.equals(technology) 
						|| TechnologyTypes.HTML5_MOBILE_WIDGET.equals(technology) || TechnologyTypes.HTML5_MULTICHANNEL_JQUERY_WIDGET.equals(technology)) {
			%>
				<div id="nodeJS_btndiv" class="nodeJS_div">
					<input id="runAgnSrc" type="button" value="<s:text name="label.runagainsrc"/>" class="primary btn">
					<input id="stopbtn" type="button" value="<s:text name="label.stop"/>" class="btn disabled" onclick="stopServer();">
					<input id="restartbtn" type="button" value="<s:text name="label.restart"/>" class="btn disabled" onclick="restartServer();"/>
				</div>		
			<% } %>
		
			<div class="icon_div">
				<a href="#" id="openFolder"><img id="folderIcon" src="images/icons/open-folder.png" title="Open folder" /></a>
				<a href="#" id="copyPath"><img src="images/icons/copy-path.png" title="Copy path"/></a>
			</div>
		</div>
	</div>

	<div class="buildDiv">
	    <div class="build_detail_div" id="build-body-container">
	        <!-- Data Display starts -->
	
			<!-- Data Display ends -->            
		</div>

		<div class="build_progress_div">
			<div class="build_table_div">
    			<!-- Command Display Heading starts -->
				<div class="tblheader" style="height: 29px;">
					<table class="zebra-striped" style="height: 29px;"> 
						<tr class="tr_color">
		    				<th><s:text name="label.progress"/></th>
		    				<th><img src="images/icons/clipboard-copy.png" alt="clipboard" id="clipboard" title="Copy to clipboard"/></th>
						</tr>
		           </table>
          		</div>
         		<!-- Command Display Heading starts -->

				<!-- Command Display starts -->
				<div class="build_cmd_div" id="build-output">
			    	<%= serverLog %>
				</div>
				<!-- Command Display starts -->
            </div>
        </div>
	</div>
	
	<!-- Hidden Fields -->
	<input type="hidden" name="projectCode" value="<%= projectCode %>">
</form>

<!-- <div class="popup_div" id="generateBuild"> </div>-->

<script type="text/javascript">
	<% if (TechnologyTypes.NODE_JS_WEBSERVICE.equals(technology)) { %>
			if (<%= nodeServerStatus %>) {
				runAgainstSrcServerRunning();
			} else {
				runAgainstSrcServerDown();
			}
	<% } %>
	
	<%
		if (TechnologyTypes.JAVA_WEBSERVICE.equals(technology) || TechnologyTypes.HTML5_WIDGET.equals(technology) 
			|| TechnologyTypes.HTML5_MOBILE_WIDGET.equals(technology) || TechnologyTypes.HTML5_MULTICHANNEL_JQUERY_WIDGET.equals(technology)) {
	%>
			if (<%= javaServerStatus %>) {
				runAgainstSrcRunning();
			} else {
				runAgainstSrcSDown();
			}
	<% } %>
	
    $(document).ready(function() {
    	enableScreen();
    	
    	if ($.browser.safari && $.browser.version == 530.17) {
    		$(".buildDiv").show().css("float","left");
    	}
    	
    	refreshTable('<%= projectCode %>');
    	
        if (($('#database option').length == 0) && ($('#server option').length == 0)) {
			$('#buildbtn').prop("disabled", true);
        }
        
        $('#deleteButton').removeClass("btn primary");
        $('#deleteButton').addClass("btn disabled");
        $('#deleteButton').attr("disabled", true);
        
        $('#showSettings').click(function() {
            $('.build_form').attr("action", "buildView");
            $('.build_form').submit();
        });
        
        $('#generatebtn').click(function() {
            generateBuild('<%= projectCode %>', "generateBuild", this);
        });
        
        $('#nodeJS_runAgnSrc').click(function() {
        	generateBuild('<%= projectCode %>', "nodeJS_runAgnSrc", this);
        });
        
        $('#runAgnSrc').click(function() {
        	generateBuild('<%= projectCode %>', "runAgnSrc", this);
        });
        
        $('#deleteButton').click(function() {
			$("#confirmationText").html("Do you want to delete the selected build(s)");
		    dialog('block');
		    escBlockPopup();
        });
        
        $('form[name=listForm]').submit(function() {
			showProgessBar("Deleting Build (s)", 100);
			performAction('deleteBuild', $('#formBuild'), $("#tabDiv"));
	        return false;
	    });
        
        $('#openFolder').click(function() {
            openFolder('<%= sbBuildPath %>');
        });
        
        $('#copyPath').click(function() {
           copyPath('<%= sbBuildPath %>');
        });
        
        $('#clipboard').click(function(){
    		copyToClipboard($('#build-output').text());
    	});
    });
    
 	// Its used by iphone alone
    function deploy(obj) {
    	$('#popup_div').empty();
        $("#build-output").empty();
        $("#build-output").html('<%= FrameworkConstants.MSG_IPHONE_DEPLOY %>');
        var currentId = obj.id;
        var idArray = currentId.split('#');
        var buildNumber = idArray[1];
        var params = "buildNumber=";
        params = params.concat(buildNumber);
		readerHandlerSubmit('deploy', '<%= projectCode %>', 'Deploy', $('#formBuild'), '', params);
    }
 	
    function copyToClipboard(data) {
        var params = "copyToClipboard=";
        params = params.concat(data);
        performAction('copyToClipboard', '', '', '', params);
	}
    
    function generateBuild(projectCode, from, obj) {
    	$('#popup_div').empty();
		showPopup();
       	var currentId = obj.id;
       	var idArray = currentId.split('#');
       	var buildNumber = idArray[1];
        var params = "from=";
		params = params.concat(from);
		params = params.concat("&buildNumber=");
		params = params.concat(buildNumber);
		params = params.concat("&projectCode=");
		params = params.concat('<%= projectCode %>');
       	popup('generateBuild', '', $('#popup_div'), '', '', params);
        escPopup();
    }
    
    function deployAndroid(obj) {
    	$('#popup_div').empty();
    	var currentId = obj.id;
        var idArray = currentId.split('#');
        var buildNumber = idArray[1];
		showPopup();
       	var params = "buildNumber=";
		params = params.concat(buildNumber);
       	popup('deployAndroid', $('#formBuild'), $('#popup_div'), '', '', params);
    }
   
    function deployIphone(obj) {
    	$('#popup_div').empty();
        var currentId = obj.id;
        var idArray = currentId.split('#');
        var buildNumber = idArray[1];
		showPopup();
       	var params = "buildNumber=";
		params = params.concat(buildNumber);
       	popup('deployIphone', $('#formBuild'), $('#popup_div'), '', '', params);
    }
    
    // When Node js is  running disable run against source button
    function runAgainstSrcServerRunning() {
	    disableControl($("#nodeJS_runAgnSrc"), "btn disabled");
	   	enableControl($("#stopbtn"), "btn nodejs_stopbtn");
	   	enableControl($("#restartbtn"), "primary btn");
	}
    	
 	// When Node js is not running enable run against source button
    function runAgainstSrcServerDown() {
		disableControl($("#stopbtn"), "btn disabled");
	    disableControl($("#restartbtn"), "btn disabled");
	    enableControl($("#nodeJS_runAgnSrc"), "primary btn");
    }
 	
 // When Java WS is  running enable run against source button
    function runAgainstSrcRunning() {
		disableControl($("#runAgnSrc"), "btn disabled");
	   	enableControl($("#stopbtn"), "btn nodejs_stopbtn");
	   	enableControl($("#restartbtn"), "primary btn");
    }
    
 	// When Java WS is not running enable run against source button
    function runAgainstSrcSDown() {
		disableControl($("#stopbtn"), "btn disabled");
	    disableControl($("#restartbtn"), "btn disabled");
	    enableControl($("#runAgnSrc"), "primary btn");
    }
 	
 	function restartServer() {
 		$("#build-output").empty();
 		$("#build-output").html("Server is restarting...");
 		$("#stopbtn").attr("class", "btn disabled");
       	$("#restartbtn").attr("class", "btn disabled");
 		readerHandlerSubmit('restartServer', '<%= projectCode %>', '<%= FrameworkConstants.REQ_JAVA_START %>', $('#formBuild'), true);
 	}
 	
	function stopServer() {
		$("#build-output").empty();
		$("#build-output").html("Server is stopping...");
		$("#stopbtn").attr("class", "btn disabled");
       	$("#restartbtn").attr("class", "btn disabled");
		readerHandlerSubmit('stopServer', '<%= projectCode %>', '<%= FrameworkConstants.REQ_JAVA_STOP %>', $('#formBuild'), true);
 	}
	
	function successEvent(pageUrl, data) {
		if (pageUrl == "restartNodeJSServer") {
    		runAgainstSrcServerRunning();
    	} else if (pageUrl == "stopNodeJSServer") {
    		runAgainstSrcServerDown();
    	} else if (pageUrl == "startNodeJSServer") {
    		runAgainstSrcServerRunning();
     	} else if (pageUrl == "restartServer" || pageUrl == "runAgainstSource") {
     		runAgainstSrcRunning();
     	} else if (pageUrl == "stopServer") {
     		runAgainstSrcSDown();
     	} else if (pageUrl == "NodeJSRunAgainstSource" && $.trim(data) == "Server startup failed") {
     		runAgainstSrcServerDown();
     	} else if (pageUrl == "NodeJSRunAgainstSource" && $.trim(data) != "Server startup failed") {
     		runAgainstSrcServerRunning();
     	} else if (pageUrl == "checkForConfiguration") {
    		successEnvValidation(data);
    	} else if (pageUrl == "checkForConfigType") {
    		successEnvValidation(data);
    	} else if (pageUrl == "fetchBuildInfoEnvs") {
    		fillVersions("environments", data.buildInfoEnvs);
    	} else if (pageUrl == "createProfile") {
			successProfileCreation(data);
		} else if (pageUrl == "getSqlDatabases") {
			fillVersions("databases", data.databases , "");
			//getting sql files to be executed
			getSQLFiles();
		} else if (pageUrl == "fetchSQLFiles ") {
			fillVersions("avaliableSourceScript", data.sqlFiles , "getSQLFiles");
			// after sql files are loaded, already selected value should be hidden in available list
			hideDbWithVersions();
		} else if (pageUrl == "jsToMinify") {
			updateHiddenField(data.jsFinalName, data.selectedJs, data.browseLocation);
    	}
    }
	
</script>