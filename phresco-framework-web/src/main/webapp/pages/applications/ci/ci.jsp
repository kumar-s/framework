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

<%@ page import="org.apache.commons.collections.CollectionUtils"%>
<%@ page import="org.apache.commons.collections.MapUtils"%>
<%@ page import="java.util.List"%>
<%@ page import="java.util.Map"%>
<%@ page import="java.util.Iterator"%>

<%@ page import="com.photon.phresco.commons.FrameworkConstants" %>
<%@ page import="com.photon.phresco.model.ProjectInfo"%>
<%@ page import="com.photon.phresco.commons.CIBuild"%>

<%@ include file="../progress.jsp" %>
<%@ include file="../errorReport.jsp" %>
<%@ include file="../../userInfoDetails.jsp" %>

<script type="text/javascript" src="js/reader.js" ></script>
<script type="text/javascript" src="js/delete.js" ></script>
<script type="text/javascript" src="js/home-header.js" ></script>

<style type="text/css">
    #warningmsg {
     	display: none;
     	width: auto; 
		left: 521px;
     	position: absolute;
     	text-align: center;
     	padding: 4px 14px;
     	margin-top: 5px;
     	float: right;
     	font-weight: bold;
    }
</style>

<%
    ProjectInfo projectInfo = (ProjectInfo)request.getAttribute(FrameworkConstants.REQ_PROJECT_INFO);
    String projectCode = projectInfo.getCode();
    String jenkinsAlive = request.getAttribute(FrameworkConstants.CI_JENKINS_ALIVE).toString();
    boolean isAtleastOneJobIsInProgress = false;
    String isBuildTriggeredFromUI = request.getAttribute(FrameworkConstants.CI_BUILD_TRIGGERED_FROM_UI).toString();
%>

<s:if test="hasActionMessages()">
    <div class="alert-message success"  id="successmsg" style="margin-top: 2px; margin-left: 165px;">
        <s:actionmessage />
    </div>
</s:if>

<div class="alert-message ciAlertMsg"  id="warningmsg">
	<s:label cssClass="labelWarn ciLabelWarn" key="ci.warn.message" />
</div>

<form name="listForm" id="formCi" class="configurations_list_form ciFormJob">
    <div class="operation ciOperationDiv">
    	<div class="ciOperationEleme">
	        <input id="setup" type="button" value="<s:text name="label.setup"/>" class="primary btn">
	        <input id="startJenkins" type="button" value="<s:text name="label.start"/>" class="primary btn">
	        <input id="stopJenkins" type="button" value="<s:text name="label.stop"/>" class="btn disabled" disabled="disabled">
	        <input id="configure" type="button" value="<s:text name="label.configure"/>" class="primary btn">
	        <input id="build" type="button" value="<s:text name="label.build"/>" class="btn disabled" disabled="disabled" onclick="buildCI();">
	        <input id="deleteButton" type="button" value="<s:text name="label.deletebuild"/>" class="btn disabled" disabled="disabled">
	        <input id="deleteJob" type="button" value="<s:text name="label.deletejob"/>" class="btn disabled" disabled="disabled" onclick="deleteCIJob();">
        </div>
    </div>
    
    <% 
	    boolean isExistJob = true;
        int noOfJobsIsinProgress = Integer.parseInt(request.getAttribute(FrameworkConstants.CI_NO_OF_JOBS_IN_PROGRESS).toString());
        Map<String, List<CIBuild>> existingJobs = (Map<String, List<CIBuild>>)request.getAttribute(FrameworkConstants.REQ_EXISTING_JOBS);
        // when no job's jenkins is alive , have to display no jobs available
        if (MapUtils.isEmpty(existingJobs) ) {
        	isExistJob = false;
    %>
		<!-- jobs not available message -->
        <div class="alert-message block-message warning" >
            <center><s:label key="ci.jobs.error.message" cssClass="errorMsgLabel"/></center>
        </div>
    <% } else { %>
		<div id="ciList" class="ciListElem" >
			<div class="scrollCiList ciScrollListElem">
			    <section class="accordion_panel_wid">
			        <div id="CiBuildsList" class="accordion_panel_inner" style="height: 365px">
			        	<%
				        	Iterator iterator = existingJobs.keySet().iterator();  
					    	while (iterator.hasNext()) {
					    	   String jobName = iterator.next().toString();  
					    	   List<CIBuild> builds = existingJobs.get(jobName);
					    	   if(new Boolean(request.getAttribute(FrameworkConstants.CI_BUILD_IS_IN_PROGRESS + jobName).toString()).booleanValue()) {
					    		   isAtleastOneJobIsInProgress = true;
					    	   }
			        	%>
			            <section class="lft_menus_container">
			                <span class="siteaccordion" id="siteaccordion_active">
			                	<span>
	                				<input type="checkbox" class="<%= jobName %>Job" name="Jobs" id="checkBox" value="<%= jobName %>" 
	                					<%= new Boolean(request.getAttribute(FrameworkConstants.CI_BUILD_JENKINS_ALIVE + jobName).toString()).booleanValue() ? "" : "disabled" %>>
			                		&nbsp;&nbsp;<%= jobName %> &nbsp;&nbsp;
								</span>
			                </span>
			                <div class="mfbox siteinnertooltiptxt">
			                    <div class="scrollpanel">
			                        <section class="scrollpanel_inner">
			                        	<% if (CollectionUtils.isNotEmpty(builds)) { %>
			                        	<table class="download_tbl">
				                        	<thead>
				                            	<tr class="download_tbl_header">
				                            		<th>
														<input type="checkbox" value="<%= jobName %>" class="<%= jobName %>AllBuild" name="allBuilds">
				                            		</th>
				                            		<th>#</th>
			                            			<th><s:text name="label.url"/></th>
			                            			<th><s:text name="label.download"/></th>
			                            			<th><s:text name="label.time"/></th>
			                            			<th><s:text name="label.status"/></th>
			                            		</tr>
				                            </thead>
				                            
				                        	<tbody id="<%= jobName %>" class="jobBuildsList">
				                        		<% for (CIBuild build : builds) { %>
						                    		<tr>
						                    			<td>
						                    				<input type="checkbox" value="<%= jobName %>,<%= build.getNumber() %>" class="<%= jobName %>" name="builds">
						                    			</td>
						                    			<td><%= build.getNumber() %></td>
						                    			<td><a href="<%= build.getUrl() %>" target="_blank"><%= build.getUrl().replace("%20", " ") %></a></td>
						                    			<td style="padding-left: 3%;">
						                    				<% if (build.getStatus().equals("INPROGRESS")) { %>
								                                <img src="images/icons/inprogress.png" title="In progress"/>
										              		<% 
								                                } else if (build.getStatus().equals("SUCCESS")) {
								                                	String downloadUrl = build.getUrl() + FrameworkConstants.CI_JOB_BUILD_ARTIFACT + FrameworkConstants.FORWARD_SLASH + build.getDownload().replaceAll("\"",""); 
								                            %>
										                		<a href="<s:url action='CIBuildDownload'>
												          		     <s:param name="buildDownloadUrl"><%= downloadUrl %></s:param>
												          		     <s:param name="projectCode"><%= projectCode %></s:param>
												          		     <s:param name="buildNumber"><%= build.getNumber() %></s:param>
												          		     <s:param name="downloadJobName"><%= jobName %></s:param>
												          		     </s:url>"><img src="images/icons/download.png" title="Download"/>
									                            </a>
								                            <% }  else { %>
								                                <img src="images/icons/wrong_icon.png" title="Not available"/>
								                            <% } %>
														</td>
						                    			<td><%= build.getTimeStamp() %></td>
						                    			<td>
						                    				<% if (build.getStatus().equals("SUCCESS")) { %>
								                                <img src="images/icons/success.png" title="Success">
								                           	<% } else if (build.getStatus().equals("INPROGRESS")) { %>
								                                <img src="images/icons/inprogress.png" title="In progress"/>
								                            <% } else { %>
								                                <img src="images/icons/failure.png" title="Failure">
								                            <% } %>
						                    			</td>
						                    		</tr>
					                    		<% } %>
				                    		</tbody>
			                        	</table>
			                        	<% } else { %>
			                        	    <div class="alert-message block-message warning" >
			                        	    	<%  if (!new Boolean(request.getAttribute(FrameworkConstants.CI_BUILD_JENKINS_ALIVE + jobName).toString()).booleanValue()) { %>
			                        	    		<center><s:label key="ci.server.down.message" cssClass="errorMsgLabel"/></center>
			                        	    	<% } else { %>
													<center><s:label key="ci.error.message" cssClass="errorMsgLabel"/></center>			                        	    	
			                        	    	<% } %>
									        </div>
			                        	<% } %>
			                        </section>
			                    </div>
			                </div>
			            </section>  
			            <% } %>
			        </div>
			    </section>
		    </div>
		</div>
    <% } %>
    
    <!-- Hidden Fields -->
	<input type="hidden" name="projectCode" value="<%= projectCode %>" />
</form>

<script type="text/javascript">
	//To check whether the device is ipad or not and then apply jquery scrollbar
	if (!isiPad()) {
		$("#CiBuildsList").css("height", ($('#tabDiv').height() - $('.ciOperationDiv').height()));
		$(".accordion_panel_inner").scrollbars();
	}
	
	// buildSize to refresh ci after build completed
	var refreshCi = false;
	var isJenkinsAlive = false;
	var isJenkinsReady = false;
	
	$(document).ready(function() {
		accordion();
		
		$("#popup_div").css("display","none");
		$("#popup_div").empty();
		enableScreen();
		
		hideProgessBar();
		
		$("input:checkbox[name='allBuilds']").click(function() {
			$("input:checkbox[class='" + $(this).val() +"']").attr('checked', $(this).is(':checked'));
			$("input:checkbox[value='" + $(this).val() +"']").attr('checked', $(this).is(':checked'));
			enableDisableDeleteButton($(this).val());
		});
		
		$("input:checkbox[name='builds']").click(function() {
			var isAllChecked = isAllCheckBoxCheked($(this).attr("class"));
			$("input:checkbox[value='" + $(this).attr("class") +"']").attr('checked', isAllChecked);
			enableDisableDeleteButton($(this).attr("class"));
		});
		
		$("input:checkbox[name='Jobs']").click(function() {
			$("input:checkbox[class='" + $(this).val() +"']").attr('checked', $(this).is(':checked'));
			$("input:checkbox[value='" + $(this).val() +"']").attr('checked', $(this).is(':checked'));
			enableDisableDeleteButton($(this).val());
		});
		
	    $('#configure').click(function() {
			if (isMoreThanOneJobSelected()) {
				showHidePopupMsg($(".ciAlertMsg"), '<%= FrameworkConstants.CI_ONE_JOB_REQUIRED%>');
				return false;			
			}
	        showCI();
	    });
	    
	    $('#setup').click(function() {
	       	ProgressShow("block");
	       	$("#build-output").empty();
	       	getCurrentCSS();
	       	setupProgress();
	    });
	    
	    $('#closeGenerateTest, #closeGenTest').click(function() {
	    	ProgressShow("none");
	    	refreshAfterServerUp();
	    });
	    
	    $('#startJenkins').click(function() {
	    	isCiRefresh = true; //after stratup , when closing popup, page should refreshed after some time
	       	ProgressShow("block");
	       	$("#build-output").empty();
	       	getCurrentCSS();
	        startJenkins();
	    });
	    
	    $('#stopJenkins').click(function() {
	       	ProgressShow("block");
	       	$("#build-output").empty();
	       	getCurrentCSS();
	        stopJenkins();
	    });
	    
	    if (<%= jenkinsAlive %>) {
	    	console.log("jenkins alive , enable configure button ");
	    	enableStart();
	    	enableControl($("#configure"), "primary btn");
	    	disableControl($("#setup"), "btn disabled"); // when CI running setup should not work
	    } else {
	    	console.log("Jenkins down , disabled configure button ");
	    	enableStop();
	    	disableControl($("#configure"), "btn disabled");
	    }
	    
	//     RBACK implemented
	    if ('<%= disableCI %>' == 'true') {
	    	disableCI();	//Restrict CI
	    }
	    
	    // delete ci builds
	    $('#deleteButton').click(function() {
			$("#confirmationText").html("Do you want to delete the selected build(s)");
			dialog('block');
			escBlockPopup();
	    });
	    
	    $('form[name=listForm]').submit(function() {
	        showProgessBar("Deleting Build (s)", 100);
	        performAction('CIBuildDelete', $('#formCi'), $("#tabDiv"));
	        return false;
	    });
		
		// if build is in progress disable configure button
	    if (<%= isAtleastOneJobIsInProgress %> || <%= isBuildTriggeredFromUI %>) {
	    	console.log("build is in progress, disable configure button ");
	    	disableControl($("#configure"), "btn disabled"); // when building , user should not configure which leads to restart of jenkins
	    	disableControl($("#build"), "btn disabled");
	    	refreshCi = true;
	    	console.log("at least one job is in progres...");
	    	refreshBuild();
	    } 
		
		if (isCiRefresh) {
			refreshAfterServerUp(); // after server restarted , it ll reload builds and ll refresh page (reload page after 10 sec)	
		}
	});
	
	function buildCI() {
		popup('buildCI', $('#formCi'), $('#tabDiv'));
	}
	
	function showCI() {
		$("#popup_div").empty();
	    $("#showConfigure").empty();
	    showPopup();
		popup('configure', $('#formCi'), $('#popup_div'));
	    escPopup();
	}
	
	function setupProgress() {
		$('#loadingDiv').css("display","block");
		readerHandlerSubmit('setup', '<%= projectCode %>', '<%= FrameworkConstants.CI_SETUP %>', $('#formCi'));
	}
	
	function ProgressShow(prop) {
	    $(".wel_come").show().css("display",prop);
	    $('#build-outputOuter').show().css("display",prop);
	}
	
	function startJenkins() {
		$('#loadingDiv').css("display","block");
		readerHandlerSubmit('startJenkins', '<%= projectCode %>', '<%= FrameworkConstants.CI_START %>', $('#formCi'));
	}
	
	function stopJenkins() {
		$('#loadingDiv').css("display","block");
		readerHandlerSubmit('stopJenkins', '<%= projectCode %>', '<%= FrameworkConstants.CI_STOP %>', $('#formCi'));
	}
	
	function deleteCIJob() {
	    showProgessBar("Deleting job (s)", 100);
		performAction('CIJobDelete', $('#formCi'), $("#tabDiv"));
	}
	
	function enableStart() {
	    disableControl($("#startJenkins"), "btn disabled");
	    enableControl($("#stopJenkins"), "primary btn");
	}
	
	function enableStop() {
	    enableControl($("#startJenkins"), "primary btn");
	    disableControl($("#stopJenkins"), "btn disabled");
	}
	
	//when background build is in progress, have to refresh ci page
	function refreshBuild() {
		if (refreshCi) {
			console.log("Going to get no of jobs in progress " + refreshCi);	
			performAction('getNoOfJobsIsInProgress', $('#formCi'), '', true);
		}
	}
	
	function successRefreshBuild(data) {
		console.log("noOfJobsIsinProgress....." + <%= noOfJobsIsinProgress %>);
		console.log("successRefreshBuild....." + data);
		//data can be zero when no build is in progress, can be int value for each running job
		// noOfJobsIsinProgress also can be zero  when no jobs in in progress
		if (data < <%= noOfJobsIsinProgress %> || data > <%= noOfJobsIsinProgress %>) { // When build is increased or decreased on a job refresh the page , refresh the page
	    	showLoadingIcon($("#tabDiv")); // Loading Icon
	    	console.log("build succeeded going to load builds.....");
			performAction('ci', $('#formCi'), $("#tabDiv"));
		} else {
			window.setTimeout(refreshBuild, 15000); // wait for 15 sec
		}
	}
	
	//after configured , it ll take some time to start server , so we ll get no builds available , in that case have to refresh ci
	function refreshAfterServerUp() {
		console.log("Server startup Refreshed...." + isCiRefresh);
		// after configured job , jenkins will take some time to load. In that case after jenkins started(fully up and running), we have to enable this
	   	localJenkinsAliveCheck ();
		
		console.log("Local jenkins alive called ===> jenkins alive ===> " + isJenkinsAlive);
		console.log("Local jenkins alive called ===> jenkins ready ===> " + isJenkinsReady);
		
	   	//default : isCiRefresh = true
		if (!isCiRefresh) { //when stop is clicked it will comme here
			console.log("Refreshing it!!!!!!");
			reloadCI();
		} else if (isCiRefresh && (!isJenkinsAlive || !isJenkinsReady)) { // when start is clicked it will come here
			//till page is reloaded disable these buttons.
		   	disableControl($("#configure"), "btn disabled");
		   	disableControl($("#build"), "btn disabled");
		   	disableControl($("#deleteJob"), "btn disabled");
		   	$(".errorMsgLabel").text('<%= FrameworkConstants.CI_BUILD_LOADED_SHORTLY%>');
		   	
			console.log("I ll wait till jenkins gets ready!!!");
			window.setTimeout(refreshAfterServerUp, 15000); // wait for 15 sec
		} else {
			isCiRefresh = false;
			console.log("Server started successfully!");
			reloadCI();
		}
	}
	
	function reloadCI() {
		if ($("a[name='appTabs'][class='selected']").attr("id") == "ci" && $(".wel_come").css("display") == "none"){
	    	showLoadingIcon($("#tabDiv")); // Loading Icon
	    	console.log("Server startup completed ..." + isCiRefresh);
	    	var params = "projectCode=";
	    	params = params.concat('<%= projectCode %>');
			performAction('ci', '', $("#tabDiv"), '', params);
		} else {
			$(".errorMsgLabel").text('<%= FrameworkConstants.CI_NO_JOBS_AVAILABLE%>');
		}
	}
	
	function localJenkinsAliveCheck () {
	    $.ajax({
	        url : "localJenkinsAliveCheck",
	        data: { },
	        type: "POST",
	        success : function(data) {
	        	if ($.trim(data) == '200') {
	        		console.log("200");
	        		isJenkinsAlive = true;
	        		isJenkinsReady = true;
	        	}
	        	if ($.trim(data) == '503') {
	        		console.log("503");
	        		isJenkinsAlive = true;
	        		isJenkinsReady = false;
	        	}
	        	if ($.trim(data) == '404') {
	        		console.log("404");
	        		isJenkinsAlive = false;
	        		isJenkinsReady = false;
	        	}
	        },
	        async:false
	    });
	}
	
	function successEvent(pageUrl, data) {
		if (pageUrl == "getNoOfJobsIsInProgress") {
			successRefreshBuild(data);
		} else if (pageUrl == "getBuildsSize") {
			successRefreshCI(data);
		} else if (pageUrl == "checkForConfiguration") {
			successEnvValidation(data);
		}
	}
	
	function enableDisableDeleteButton(atleastOneCheckBoxVal) {
		if (isAtleastOneCheckBoxCheked('jobBuildsList')) {
			enableControl($("#deleteButton"), "primary btn");
		} else {
			disableControl($("#deleteButton"), "btn disabled");
		}
		
		if ($("input[type=checkbox][name='Jobs']:checked").length > 0) {
			enableControl($("#deleteJob"), "primary btn");
			enableControl($("#build"), "primary btn");
		} else {
			disableControl($("#deleteJob"), "btn disabled");
			disableControl($("#build"), "btn disabled");
		}
	}
	
	function isMoreThanOneJobSelected() {
		if ($("input[type=checkbox][name='Jobs']:checked").length > 1) {
			return true;
		} else {
			return false;
		}
	}
</script>