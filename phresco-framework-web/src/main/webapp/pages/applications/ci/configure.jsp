<%@ taglib uri="/struts-tags" prefix="s"%>

<%@	page import="org.apache.commons.lang.StringUtils"%>
<%@ page import="java.util.List"%>

<%@ page import="com.photon.phresco.framework.model.CIJob" %>
<%@ page import="com.photon.phresco.framework.model.FrameworkConstants" %>
<%@ page import="com.photon.phresco.framework.model.SettingsInfo"%>
<%@ page import="com.photon.phresco.commons.XCodeConstants"%>
<%@ page import="com.photon.phresco.commons.AndroidConstants"%>
<%@ page import="com.photon.phresco.util.TechnologyTypes" %>
<%@ page import="com.photon.phresco.framework.commons.PBXNativeTarget"%>
<%@ page import="com.photon.phresco.configuration.Environment"%>
<%@ page import="com.photon.phresco.commons.model.ProjectInfo"%>

<script src="js/select-envs.js"></script>

<%
	List<SettingsInfo> serverConfigs = (List<SettingsInfo>) request.getAttribute(FrameworkConstants.REQ_SERVER_CONFIGS);
	List<SettingsInfo> databaseConfigs = (List<SettingsInfo>) request.getAttribute(FrameworkConstants.REQ_DATABASE_CONFIGS);
	List<SettingsInfo> emailConfigs = (List<SettingsInfo>) request.getAttribute(FrameworkConstants.REQ_EMAIL_CONFIGS);
	List<SettingsInfo> webServiceConfigs = (List<SettingsInfo>) request.getAttribute(FrameworkConstants.REQ_WEBSERVICE_CONFIGS);
	
	String showSettings = (String) request.getAttribute(FrameworkConstants.REQ_SHOW_SETTINGS);
	if (showSettings != null && Boolean.valueOf(showSettings)) {
		showSettings = "checked";
	}
	
	String[] weekDays = {"", "Sunday","Monday","Tuesday","Wednesday","Thursday","Friday","Saturday"};
	String[] months = {"", "January","February","March","April","May","June","July","August","September","October","November","December"};

	CIJob existingJob =  (CIJob) request.getAttribute(FrameworkConstants.REQ_EXISTING_JOB);
	String disableStr = existingJob == null ? "" : "disabled";
	ProjectInfo projectInfo = (ProjectInfo)request.getAttribute(FrameworkConstants.REQ_PROJECT_INFO);
	String projectCode = projectInfo.getCode();
	String technology = projectInfo.getTechnology().getId();
    String actionStr = "saveJob";
    actionStr = (existingJob == null || StringUtils.isEmpty(existingJob.getSvnUrl())) ? "saveJob" : "updateJob";
    existingJob = (existingJob == null || StringUtils.isEmpty(existingJob.getSvnUrl())) ? null : existingJob; // when we setup it ll have only jenkins url and port in that case we have to check svnUrl and make null
   	//xcode targets
   	List<PBXNativeTarget> xcodeConfigs = (List<PBXNativeTarget>) request.getAttribute(FrameworkConstants.REQ_XCODE_CONFIGS);
   	List<Environment> environments = (List<Environment>) request.getAttribute(FrameworkConstants.REQ_ENVIRONMENTS);
   	// mac sdks
   	List<String> macSdks = (List<String>) request.getAttribute(FrameworkConstants.REQ_IPHONE_SDKS);
%>
<div class="popup_Modal configurePopUp" id="ciDetails">
    <form name="ciDetails" action="<%= actionStr %>" method="post" autocomplete="off" class="ci_form" id="ciForm">
        <div class="modal-header">
            <h3><s:text name="label.ci"/></h3>
            <a class="close" href="#" id="close">&times;</a>
        </div>
        
        <div class="modal-body" id="ciConfigs" style="padding-top: 2px;">
        	<div class="clearfix">
				<label class="xlInput popup-label"></label>
				<div class="input">
					<span id="missingData" class="missingData" style="color:#690A0B;"></span>
				</div>
			</div>
			
        	<div class="clearfix">
				<label class="xlInput popup-label"><span class="red">* </span><s:text name="label.name"/></label>
				<div class="input">
					<input type="text" id="name" name="name" value="<%= existingJob == null ? projectCode : existingJob.getName() %>" 
						disabled autofocus />
				</div>
			</div>
			
			<div class="clearfix">
				<label class="xlInput popup-label"><s:text name="label.svn.type"/></label>
				
				<div class="input">
					<div class="multipleFields quartsRadioWidth">
						<div>
							<input type="radio" name="svnType" value="svn" <%= existingJob == null ? "checked" : "" %> />&nbsp;
							<s:text name="label.svn"/>
						</div>
					</div>
					<div class="multipleFields quartsRadioWidth">
						<div><input type="radio" name="svnType" value="git" />&nbsp; <s:text name="label.git"/></div>
					</div>
				</div>
			</div>
			
			<div class="clearfix">
				<label class="xlInput popup-label"><span class="red">* </span><s:text name="label.svn.url"/></label>
				<div class="input">
					<input type="text" id="svnurl" class="ciSvnUrlWidth" name="svnurl" 
						value="<%= existingJob == null ? "" : existingJob.getSvnUrl() %>">
				</div>
			</div>
			
			<div class="clearfix" id="divBranch">
				<label class="xlInput popup-label"><span class="red">* </span><s:text name="label.branch"/></label>
				<div class="input">
					<input type="text" id="branch" name="branch" maxlength="63" title="63 Characters only" value="">
				</div>
			</div>
			
			<div class="clearfix" id="divUsername">
				<label class="xlInput popup-label"><span class="red">* </span><s:text name="label.username"/></label>
				<div class="input">
					<input type="text" id="username" name="username" maxlength="63" title="63 Characters only" value="">
				</div>
			</div>
			
			<div class="clearfix" id="divPassword">
				<label class="xlInput popup-label"><span class="red">* </span><s:text name="label.password"/></label>
				<div class="input">
					<input type="password" id="password" name="password" maxlength="63" title="63 Characters only" value="">
				</div>
			</div>
			
			<div class="clearfix">
				<label class="xlInput popup-label"><s:text name="label.sender.mail"/></label>
				<div class="input">
					<input type="text" name="senderEmailId" value="<%= existingJob == null ? "" : existingJob.getSenderEmailId() %>">
				</div>
			</div>
			
			<div class="clearfix">
				<label class="xlInput popup-label"><s:text name="label.sender.pwd"/></label>
				<div class="input">
					<input type="password" name="senderEmailPassword" value="<%= existingJob == null ? "" : existingJob.getSenderEmailPassword() %>">
				</div>
			</div>
			
			<div class="clearfix">
				<label class="xlInput popup-label"><s:text name="label.recipient.mail"/></label>
				
				<div class="input">
					<div class="multipleFields emaillsFieldsWidth">
						<div>
							<input id="successEmail" type="checkbox" name="emails"  value="success"/>&nbsp;
							<s:text name="label.when.success"/>
						</div>
					</div>
					<div class="multipleFields">
						<div>
							<input id="successEmailId" type="text" name="successEmailIds" disabled 
								value="<%= existingJob == null ? "" : (String)existingJob.getEmail().get("successEmails") %>">
						</div>
					</div>
				</div>
				
				<div class="input">
					<div class="multipleFields emaillsFieldsWidth">
						<div>
							<input id="failureEmail" type="checkbox" name="emails" value="failure"/> &nbsp;
							<s:text name="label.when.fail"/>
						</div>
					</div>
					<div class="multipleFields">
						<div>
							<input id="failureEmailId" type="text" name="failureEmailIds" disabled 
								value="<%= existingJob == null ? "" : (String)existingJob.getEmail().get("failureEmails") %>">
						</div>
					</div>
				</div>
					<label class="xlInput popup-label"><span class="red">* </span>Build Triggers</label>
				<div class="input">
					<div class="multipleFields emaillsFieldsWidth">
						<div>
							<input id="buildPeriodically" type="checkbox" name="triggers" value="TimerTrigger"/>&nbsp;
							Build periodically
						</div>
					</div>
				</div>
				
				<div class="input">
					<div class="multipleFields emaillsFieldsWidth">
						<div><input id="pollSCM" type="checkbox" name="triggers" value="SCMTrigger"/>&nbsp;Poll SCM</div>
					</div>
				</div>
			</div>
			<%
				String schedule = existingJob == null ? "" : (String)existingJob.getScheduleType();
				
				String dailyEvery = "";
				String dailyHour = "";
				String dailyMinute = "";
				
				String weeklyWeek = "";
				String weeklyHour = "";
				String weeklyMinute = "";
				
				String monthlyDay = "";
				String monthlyMonth = "";
				String monthlyHour = "";
				String monthlyMinute = "";
				
				String CronExpre = existingJob == null ? "" : existingJob.getScheduleExpression();
				String[] cronSplit = CronExpre.split(" ");
				if (schedule.equals("Daily")) {
					if (CronExpre.contains("/")) {
						dailyEvery = "checked";
					}
		            if (cronSplit[0].contains("/")) {
		            	dailyMinute = cronSplit[0].substring(2) + "";
		            } else {
		            	dailyMinute = cronSplit[0];
		            }
					if (cronSplit[1].contains("/")) {
						dailyHour = cronSplit[1].substring(2) + "";
		            } else {
		            	dailyHour = cronSplit[1];
		            }
				} else if (schedule.equals("Weekly")) {
					weeklyWeek = cronSplit[4];
					weeklyHour = cronSplit[1];
					weeklyMinute = cronSplit[0];
				} else if (schedule.equals("Monthly")) {
					monthlyDay = cronSplit[2];
					monthlyMonth = cronSplit[3];
					monthlyHour = cronSplit[1];
					monthlyMinute = cronSplit[0];
				}
			%>
			<div class="clearfix">
				<label class="xlInput popup-label"><s:text name="label.schedule"/></label>
				
				<div class="input">
					<div class="multipleFields quartsRadioWidth">
						<div>
							<input id="scheduleDaily" type="radio" name="schedule" value="Daily"  onChange="javascript:show('Daily');" 
								<%= ((schedule.equals("Daily")) || (schedule.equals(""))) ? "checked" : "" %> />&nbsp;
							<s:text name="label.daily"/>
						</div>
					</div>
					<div class="multipleFields quartsRadioWidth">
						<div>
							<input id="scheduleDaily" type="radio" name="schedule" value="Weekly" onChange="javascript:show('Weekly');" 
								<%= schedule.equals("Weekly") ? "checked" : "" %> />&nbsp;
							<s:text name="label.weekly"/>
						</div>
					</div>
					<div class="multipleFields quartsRadioWidth">
						<div>
							<input id="scheduleDaily" type="radio" name="schedule" value="Monthly" onChange="javascript:show('Monthly');" 
								<%= schedule.equals("Monthly") ? "checked" : "" %>/>&nbsp; 
							<s:text name="label.monthly"/>
						</div>
					</div>
				</div>
				<div  id='Daily' class="schedulerWidth">
					<div>
						<s:text name="label.every"/> &nbsp;&nbsp;&nbsp;&nbsp;	
						<s:text name="label.hours"/> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 
						<s:text name="label.minutes"/>
					</div>
					<div class="dailyInnerDiv">
						<s:text name="label.At"/> &nbsp;&nbsp;
						<input type="checkbox" id="daily_every" name="daily_every" onChange="javascript:show('Daily');" <%= dailyEvery %>>
						&nbsp;&nbsp;
	                    <select id="daily_hour" name="daily_hour" onChange="javascript:show('Daily');" class="schedulerSelectWidth">
	                         <option value="*">*</option>
	                         <% 
	                         	for (int i = 1; i < 24; i++) {
	                         		String selectedStr = "";
	                         		if (StringUtils.isNotEmpty(dailyHour) && dailyHour.equals("" + i)) {
	                         			selectedStr = "selected";
	                         		}
	                         %>
	                         		<option value="<%= i %>" <%= selectedStr %>> <%= i %> </option>
	                         <% } %>
	                   	</select>
	                     &nbsp;	&nbsp;
					    <select id="daily_minute" name="daily_minute" onChange="javascript:show('Daily');" class="schedulerSelectWidth">
							<option value="*">*</option>            
							<% 
								for (int i = 1; i < 60; i++) { 
	                         		String selectedStr = "";
	                         		if (StringUtils.isNotEmpty(dailyMinute) && dailyMinute.equals("" + i)) {
	                         			selectedStr = "selected";
	                         		}
							%>
									<option value="<%= i %>" <%= selectedStr %>><%= i %></option>
							<% } %>
						</select>
                    </div>
				</div>
				<div id='Weekly' class="schedulerWidth">
   					<select id="weekly_week" name="weekly_week" multiple onChange="javascript:show('Weekly');" class="schedulerDay alignVertical">
				   		<%
				   			String defaultSelectedStr = "";
				   			if (StringUtils.isEmpty(weeklyWeek)) {
				             	defaultSelectedStr = "selected";
				           	}
				   		%>
				   			<option value="*" <%= defaultSelectedStr %>>*</option>
				   		<%
				   			for (int i = 1; i < weekDays.length; i++){
				   				String selectedStr = "";
				   				if (StringUtils.isNotEmpty(weeklyWeek) && weeklyWeek.equals("" + i)) {
                     				selectedStr = "selected";
                     			}
						%>
							<option value="<%= i %>" <%= selectedStr %>><%= weekDays[i] %></option>
                        <% } %>
                    </select>
                    
                    &nbsp; <s:text name="label.smal.at"/> &nbsp;
                    <select id="weekly_hours" name="weekly_hours" onChange="javascript:show('Weekly');" class="schedulerSelectWidth alignVertical">
                        <option value="*">*</option>
	                    <% 
	                    	for (int i = 1; i < 24; i++) {
	                    		String selectedStr = "";
	                     		if (StringUtils.isNotEmpty(weeklyHour) && weeklyHour.equals("" + i)) {
	                     			selectedStr = "selected";
	                     		}
	                    %>
	                       		<option value="<%= i %>" <%= selectedStr %>><%= i %></option>
	                    <% } %>
                    </select>&nbsp;<s:text name="label.hour"/>&nbsp;
                    
                  	<select id="weekly_minute" name="weekly_minute" onChange="javascript:show('Weekly');" class="schedulerSelectWidth alignVertical">
						<option value="*">*</option>            
	                  	<% 
	                  		for (int i = 1; i < 60; i++) { 
	                    		String selectedStr = "";
	                     		if (StringUtils.isNotEmpty(weeklyMinute) && weeklyMinute.equals("" + i)) {
	                     			selectedStr = "selected";
	                     		}
	                  	%>
	                      		<option value="<%= i %>" <%= selectedStr %>><%= i %></option>
	                  	<% } %>
                   	</select>&nbsp;<s:text name="label.minute"/>
				</div>
				
				<div id='Monthly' class="schedulerWidth">
                   <s:text name="label.every"/>
                   <select id="monthly_day" name="monthly_day" onChange="javascript:show('Monthly');" class="schedulerSelectWidth alignVertical">
						<option value="*">*</option>
                       	<% 
                       		for (int i = 1; i <= 31; i++) { 
	                    		String selectedStr = "";
	                     		if (StringUtils.isNotEmpty(monthlyDay) && monthlyDay.equals("" + i)) {
	                     			selectedStr = "selected";
	                     		}
						%>
								<option value="<%= i %>" <%= selectedStr %>><%= i %></option>
						<% } %>
                   </select>&nbsp;<s:text name="label.of"/>&nbsp;
                   
                  	<select id="monthly_month" name="monthly_month" multiple onChange="javascript:show('Monthly');" class="schedulerDay alignVertical">
						<%
				   			defaultSelectedStr = "";
				   			if (StringUtils.isEmpty(monthlyMonth)) {
				             	defaultSelectedStr = "selected";
				           	}
				   		%>
				   			<option value="*" <%= defaultSelectedStr %>>*</option>
				   		<%
				   			for(int i = 1; i < months.length; i++){
				   				String selectedStr = "";
				   				if (StringUtils.isNotEmpty(monthlyMonth) && monthlyMonth.equals("" + i)) {
                     				selectedStr = "selected";
                     			}
						%>
								<option value="<%= i %>" <%= selectedStr %>><%= months[i] %></option>
                        <% } %>
                    </select>
                    
					&nbsp; <s:text name="label.smal.at"/> &nbsp;
                    <select id="monthly_hour" name="monthly_hour" onChange="javascript:show('Monthly');" class="schedulerSelectWidth alignVertical">
                        <option value="*">*</option>
	                    <% 
	                    	for (int i = 1; i < 24; i++) { 
	                    		String selectedStr = "";
	                     		if (StringUtils.isNotEmpty(monthlyHour) && monthlyHour.equals("" + i)) {
	                     			selectedStr = "selected";
	                     		}
	                    %>
	                        	<option value="<%= i %>" <%= selectedStr %>><%= i %></option>
	                    <% } %>
                    </select>&nbsp;<s:text name="label.hour"/>
                    
                    &nbsp; <select id="monthly_minute" name="monthly_minute" onChange="javascript:show('Monthly');" class="schedulerSelectWidth alignVertical">
                        <option value="*">*</option>
	                    <% 
	                    	for (int i = 1; i < 60; i++) { 
	                    		String selectedStr = "";
	                     		if(StringUtils.isNotEmpty(monthlyMinute) && monthlyMinute.equals("" + i)) {
	                     			selectedStr = "selected";
	                     		}
	                    %>
	                        <option value="<%= i %>" <%= selectedStr %>><%= i %></option>
	                    <% } %>
                    </select> &nbsp; <s:text name="label.minute"/>
				</div>
			</div>
			
			<div class="clearfix">
				<label class="xlInput popup-label"><s:text name="label.cron.expression"/></label>
				<div class="input" id="cronValidation">
				
				</div>
			</div>
        </div>
		
		<div class="modal-body" id="configs">
			<div class="clearfix">
			    <label class="xlInput popup-label"><span class="red">*</span> <s:text name="label.environment"/></label>
			    <div class="input">
			        <select id="environments" name="environment" class="xlarge">
				        <optgroup label="Configurations" class="optgrplbl">
							<%
								String defaultEnv = "";
								String selectedStr = "";
								if (environments != null) {
									for (Environment environment : environments) {
										if (environment.isDefaultEnv()) {
											defaultEnv = environment.getName();
											selectedStr = "selected";
										} else {
											selectedStr = "";
										}
							%>
									<option value="<%= environment.getName() %>" <%= selectedStr %> onClick="selectEnvs()"><%= environment.getName() %></option>
							<% 		
									} 
								}
							%>
						</optgroup>
					</select>
				</div>
			</div>
			
			<% if (TechnologyTypes.ANDROIDS.contains(technology)) { %>
				<!-- Android Version -->
				<div class="clearfix">
					<label class="xlInput popup-label"><s:text name="label.sdk"/></label>
					<div class="input">
						<select id="androidVersion" name="androidVersion" class="xlarge" >
							<%
								for (int i=0; i<AndroidConstants.SUPPORTED_SDKS.length; i++) {
									if (!AndroidConstants.SUPPORTED_SDKS[i].equalsIgnoreCase(FrameworkConstants.ANDROID_LOWER_VER)) {
							%>
								<option value="<%= AndroidConstants.SUPPORTED_SDKS[i] %>"><%= AndroidConstants.SUPPORTED_SDKS[i] %></option>
							<%  	
									}
								} 
							%>
						</select>
					</div>
				</div>
			<% } %>	
		
			<% if (TechnologyTypes.IPHONES.contains(technology)) { %>
				<!-- TARGET -->
                <div class="clearfix">
                    <label class="xlInput popup-label"><s:text name="label.target"/></label>
                    <div class="input">
						<select id="target" name="target" class="xlarge" >
							<% 
								if (xcodeConfigs != null) { 
									for (PBXNativeTarget xcodeConfig : xcodeConfigs) {
							%>
										<option value="<%= xcodeConfig.getName() %>"><%= xcodeConfig.getName() %></option>
							<% 
									} 
								} 
							%>	
				       </select>
                    </div>
                </div>
	                
				<!-- SDK -->
				<div class="clearfix">
					<label class="xlInput popup-label"><s:text name="label.sdk"/></label>
					<div class="input">
						<select id="sdk" name="sdk" class="xlarge" >
							<%
								if (macSdks != null) {
									for (String sdk : macSdks) {
							%>
										<option value="<%= sdk %>"><%= sdk %></option>
							<% 
									} 
								}
							%>
						</select>
					</div>
				</div>
				
				<!-- Mode -->
				<div class="clearfix">
					<label class="xlInput popup-label"><s:text name="label.mode"/></label>
					<div class="input">
						<select id="mode" name="mode" class="xlarge" >
							<option value="<%= XCodeConstants.CONFIGURATION_DEBUG %>"><%= XCodeConstants.CONFIGURATION_DEBUG %></option>
							<option value="<%= XCodeConstants.CONFIGURATION_RELEASE %>"><%= XCodeConstants.CONFIGURATION_RELEASE %></option>
						</select>
					</div>
				</div>
			<% } %>	

			<!-- Show Settings -->
			<div class="clearfix">
				<label class="xlInput popup-label"></label>
				<div class="input">
					<input type="checkbox" id="showSettings" name="showSettings" value="showsettings" <%= showSettings %>> <s:text name="label.show.setting"/>
						&nbsp;
					<% if (TechnologyTypes.ANDROIDS.contains(technology)) { %>
						<input type="checkbox" id="proguard" name="proguard" value="false" >
						<span><s:text name="label.progurad"/></span>
												
						<input type="checkbox" id="signing" name="signing" value="false">
						<span class="popup-span"><a href="#" class="popup-span" id="androidSigning" ><s:text name="label.signing"/></a></span>
					<% } %>
				</div>
			</div>
			
			<div class="clearfix">
				<label class="xlInput popup-label"><s:text name="label.enable.build.release"/></label>
				
				<div class="input" style="padding-top: 10px;">
					<div class="multipleFields quartsRadioWidth">
						<div><input type="radio" name="enableBuildRelease" value="true" checked />&nbsp; <s:text name="label.yes"/></div>
					</div>
					<div class="multipleFields quartsRadioWidth">
						<div><input type="radio" name="enableBuildRelease" value="false" />&nbsp; <s:text name="label.no"/></div>
					</div>
				</div>
			</div>
			
			<!-- build release plugin changes starts -->
			<fieldset class="popup-fieldset fieldsetBottom perFieldSet" style="text-align: left;" id="collabNetInfo">
				<legend class="fieldSetLegend"><s:text name="label.build.release"/></legend>
				
				<div id="CollabNetConfig">
					<div class="clearfix">
						<label class="xlInput popup-label ciTwoLineLbl">
							<span class="red">* </span>
							<s:text name="label.build.release.url"/>
						</label>
						<div class="input ciTwoLineTxtBox">
							<input type="text" id="collabNetURL" class="ciSvnUrlWidth" name="collabNetURL" 
								value="<%= existingJob == null ? "" : existingJob.getCollabNetURL() %>">
						</div>
					</div>
		
					<div class="clearfix">
						<label class="xlInput popup-label ciTwoLineLbl">
							<span class="red">* </span>
							<s:text name="label.build.release.username"/>
						</label>
						<div class="input ciTwoLineTxtBox">
							<input type="text" id="collabNetusername" name="collabNetusername" maxlength="63" title="63 Characters only" 
								value="<%= existingJob == null ? "" : existingJob.getCollabNetusername() %>">
						</div>
					</div>
					
					<div class="clearfix">
						<label class="xlInput popup-label ciTwoLineLbl">
							<span class="red">* </span>
							<s:text name="label.build.release.password"/>
						</label>
						<div class="input ciTwoLineTxtBox">
							<input type="password" id="collabNetpassword" name="collabNetpassword" maxlength="63" title="63 Characters only" 
								value="<%= existingJob == null ? "" : existingJob.getCollabNetpassword() %>">
						</div>
					</div>
					
					<div class="clearfix">
						<label class="xlInput popup-label">
							<span class="red">* </span>
							<s:text name="label.build.release.project"/>
						</label>
						<div class="input">
							<input type="text" id="collabNetProject" name="collabNetProject" maxlength="63" title="63 Characters only" 
								value="<%= existingJob == null ? "" : existingJob.getCollabNetProject() %>">
						</div>
					</div>
					
					<div class="clearfix">
						<label class="xlInput popup-label">
							<span class="red">* </span>
							<s:text name="label.build.release.package"/>
						</label>
						<div class="input">
							<input type="text" id="collabNetPackage" name="collabNetPackage" maxlength="63" title="63 Characters only" 
								value="<%= existingJob == null ? "" : existingJob.getCollabNetPackage() %>">
						</div>
					</div>
					
					<div class="clearfix">
						<label class="xlInput popup-label">
							<span class="red">* </span>
							<s:text name="label.build.release.release.name"/>
						</label>
						<div class="input">
							<input type="text" id="collabNetRelease" name="collabNetRelease" maxlength="63" title="63 Characters only" 
								value="<%= existingJob == null ? "" : existingJob.getCollabNetRelease() %>">
						</div>
					</div>
					
					<div class="clearfix">
						<label class="xlInput popup-label"><s:text name="label.build.release.overwrite"/></label>
						
						<div class="input" style="padding-top: 10px;">
							<div class="multipleFields quartsRadioWidth">
								<div>
									<input type="radio" name="overwriteFiles" value="true" checked />&nbsp; 
									<s:text name="label.yes"/>
								</div>
							</div>
							<div class="multipleFields quartsRadioWidth">
								<div><input type="radio" name="overwriteFiles" value="false" />&nbsp; <s:text name="label.no"/></div>
							</div>
						</div>
					</div>
				</div>
				
			</fieldset>
    	<!-- build release plugin changes ends -->
    	
		</div>
    
        <div class="modal-footer">
			<div style="float: left;" id="loadingDiv">
				<div id="errMsg"></div>
				<img src="themes/photon/images/loading_red.gif" class="popupLoadingIcon" style="display: none;"> 
	    	</div> 
 	    	<input type="hidden" name="oldJobName" value="<%= existingJob == null ? "" : existingJob.getName() %>" >
            <input type="button" class="btn primary" value="<s:text name="label.cancel"/>" id="cancel">
            <input type="button" class="btn primary" value="<s:text name="label.save"/>" id="actionBtn">
            <input type="button" class="btn primary" value="<s:text name="label.next"/>" id="nextBtn">
            <input type="button" class="btn primary" value="<s:text name="label.previous"/>" id="preBtn">
        </div>
        
        <!-- Hidden Fields -->
		<input type="hidden" name="projectCode" value="<%= projectCode %>" />
    </form>
</div>

<script type="text/javascript">
	var selectedSchedule = $("input:radio[name=schedule]:checked").val();
	loadSchedule(selectedSchedule);
	
	$(document).ready(function() {
		credentialsDisp();
		$("#name").focus();
		$("#configs").hide();
		$("#actionBtn").hide();
		$("#preBtn").hide();
		
		$("#nextBtn").click(function() {
			var name= $("#name").val();
			var svnurl= $("#svnurl").val();
			var username= $("#username").val();
			var password= $("#password").val();
			if (isValidUrl(svnurl)) {
				$("#errMsg").html("Enter the URL");
				$("#svnurl").focus();
				$("#svnurl").val("");
				return false;
			}
			
			if (name == "") {
				$("#errMsg").html("Enter the Name");
				$("#name").focus();
				$("#name").val("");
				return false;
			}
			
			if ($("input:radio[name=svnType][value='svn']").is(':checked')) {
				if (isBlank($.trim($("input[name= username]").val()))) {
					$("#errMsg").html("Enter UserName");
					$("#username").focus();
					$("#username").val("");
					return false;
				}
				if (password == "") {
					$("#errMsg").html("Enter Password");
					$("#password").focus();
					return;
				} else {
					$("#errMsg").empty();
				}
			}
			
			if ($("input:radio[name=svnType][value='git']").is(':checked')) {
				if (isBlank($.trim($("input[name=branch]").val()))) {
					$("#errMsg").html("Enter Branch Name");
					$("#branch").focus();
					$("#branch").val("");
					return false;
				} else {
					$("#errMsg").empty();
				}
			}

			if ($("input[name='name']").val().length <= 0) {
				ciConfigureError('missingData', "Name is missing");
				return;
			} else {
				$("#errMsg").empty();
			}
			
			if ($("[name=triggers]:checked").length == 0 ) {
				$("#errMsg").html("Enter Build Triggers");
				$("#buildPeriodically").focus();
				return false;
			} else {
				$("#errMsg").empty();
				$(this).hide();
				$("#ciConfigs").hide();
				$("#configs").show();
				$("#preBtn").show();
				$("#actionBtn").show();
				showSettings();
			}
        });
        
        $("#preBtn").click(function() {
			$(this).hide();
			$("#ciConfigs").show();
			$("#configs").hide();
			$("#nextBtn").show();
			$("#actionBtn").hide();
        });
        
		$('#close, #cancel').click(function() {
			showParentPage();
			$("#popup_div").empty();
		});
		
		$("#actionBtn").click(function() {
			// do the validation for collabNet info only if the user selects git radio button
			if ($("input:radio[name=enableBuildRelease][value='true']").is(':checked')) {
				if (collabNetValidation()) {
					createJob();
				}
			} else {
				createJob();
			}
        });
		
		$("#successEmail").click(function() {
			if ($(this).is(":checked")) {
				$("#successEmailId").attr("disabled", false);
			} else {
				$("#successEmailId").attr("disabled", true);
				$("#successEmailId").val('');
	   		}
		});
		
		$("#failureEmail").click(function() {
			if ($(this).is(":checked")) {
				$("#failureEmailId").attr("disabled", false);
			} else {
				$("#failureEmailId").attr("disabled", true);
				$("#failureEmailId").val('');
	   		}
		});
		
		$("input:radio[name=schedule]").click(function() {
		    var selectedSchedule = $(this).val();
		    loadSchedule(selectedSchedule);
		});
		
		$("input:radio[name=svnType][value=svn]").click(function() {
			credentialsDisp();
		});
		
		$("input:radio[name=svnType][value=git]").click(function() {
			credentialsDisp();
		});
		
		show(selectedSchedule);
		
		<% 
			if (existingJob != null) {
				for (String trigger : existingJob.getTriggers()) {
		%>
					$("input[value='<%= trigger %>']").prop("checked", true);
				<% } %>
				//based on svn type radio button have to be selected
				<% if (StringUtils.isNotEmpty(existingJob.getRepoType())) { %>
					$("input:radio[name=svnType][value=<%= existingJob.getRepoType() %>]").prop("checked", true);
				<% } else { %>
					$("input:radio[name=svnType][value=svn]").prop("checked", true);
				<% } %>
				
				<% if (StringUtils.isNotEmpty(existingJob.getBranch())) { %>
					$("input:text[name=branch]").val("<%= existingJob.getBranch() %>");
				<% } %>
				
				<% if (StringUtils.isNotEmpty(existingJob.getUserName())) { %>
					$("input:text[name=username]").val("<%= existingJob.getUserName() %>");
				<% } %>
				
				<% if (StringUtils.isNotEmpty(existingJob.getPassword())) { %>
					$("input[name=password]").val("<%= existingJob.getPassword() %>");
				<% } %>
				// when the job is not null, have to make selection in radio buttons of collabnet plugin
				$('input:radio[name=enableBuildRelease]').filter("[value='"+<%= existingJob.isEnableBuildRelease() %>+"']").attr("checked", true);
				$('input:radio[name=overwriteFiles]').filter("[value='"+<%= existingJob.isCollabNetoverWriteFiles() %>+"']").attr("checked", true);
		<% } %>
	    
		credentialsDisp();
		
		$('input:radio[name=enableBuildRelease]').click(function() {
			enableDisableCollabNet();
			ciConfigureError('errMsg', "");
		});
		// while editing a job , based on value show hide it (CollabNet build release)
		enableDisableCollabNet();
	});

	function createJob() {
		isCiRefresh = true;
		getCurrentCSS();
		$('.popupLoadingIcon').css("display","block");
		var url = $("#ciForm").attr("action");
		$('#ciForm :input').attr('disabled', false);
		popup(url, $('#ciForm'), $('#tabDiv'));
	}
	
	function enableDisableCollabNet() {
		if ($('input:radio[name=enableBuildRelease]:checked').val() == "true") {
			$('#collabNetInfo').show();
			$('input:text[name=collabNetURL]').focus();
		} else {
			$('#collabNetInfo').hide();
			//when user selects no resets all the value
			$('input:text[name=collabNetURL], input:text[name=collabNetusername], input:password[name=collabNetpassword]').val('');
			$('input:text[name=collabNetProject], input:text[name=collabNetPackage], input:text[name=collabNetRelease]').val('');
		}
	}
	
	function collabNetValidation() {
		if (isValidUrl($('input:text[name=collabNetURL]').val())) {
			ciConfigureError('errMsg', "URL is missing");
			$('input:text[name=collabNetURL]').focus();
			return false;
		} else if (isBlank($('input:text[name=collabNetusername]').val())) {
			ciConfigureError('errMsg', "Username is missing");
			$('input:text[name=collabNetusername]').focus();
			return false;
		} else if (isBlank($('input:password[name=collabNetpassword]').val())) {
			ciConfigureError('errMsg', "Password is missing");
			$('input:password[name=collabNetpassword]').focus();
			return false;
		} else if (isBlank($('input:text[name=collabNetProject]').val())) {
			ciConfigureError('errMsg', "Project is missing");
			$('input:text[name=collabNetProject]').focus();
			return false;
		} else if (isBlank($('input:text[name=collabNetPackage]').val())) {
			ciConfigureError('errMsg', "Package is missing");
			$('input:text[name=collabNetPackage]').focus();
			return false;
		} else if (isBlank($('input:text[name=collabNetRelease]').val())) {
			ciConfigureError('errMsg', "Release is missing");
			$('input:text[name=collabNetRelease]').focus();
			return false;
		} else {
			ciConfigureError('errMsg', "");
			return true;
	 	}
	}
	
	function credentialsDisp() {
		if ($("input:radio[name=svnType][value='svn']").is(':checked')) {
			$('#divUsername, #divPassword').show();
			$('#divBranch').hide();
		} else {
			$('#divUsername, #divPassword').hide();
			$('#divBranch').show();
		}
	}
	
	function ciConfigureError(id, errMsg) {
		$("#" + id ).empty();
		$("#" + id ).append(errMsg);
	}
    
	function loadSchedule(selectedSchedule) {
		hideAllSchedule();
		$("#" + selectedSchedule).show();	
	}
	
	function hideAllSchedule() {
		$("#Daily").hide();
		$("#Weekly").hide();
		$("#Monthly").hide();
	}
	
    function show(ids) {
        var buttonObj = window.document.getElementById("enableButton");
        if (ids == "Daily") {
            var dailyEveryObj = window.document.getElementById("daily_every");
            var dailyHourObj = window.document.getElementById("daily_hour");
            var hours = dailyHourObj.options[dailyHourObj.selectedIndex].value;
            var dailyMinuteObj = window.document.getElementById("daily_minute");
            var minutes = dailyMinuteObj.options[dailyMinuteObj.selectedIndex].value;
    		var params = "cronBy=";
			params = params.concat("Daily");
			params = params.concat("&hours=");
			params = params.concat(hours);
			params = params.concat("&minutes=");
			params = params.concat(minutes);
			params = params.concat("&every=");
			params = params.concat(dailyEveryObj.checked);
			cronValidationLoad(params);
        } else if (ids == "Weekly") {
            var weeklyHourObj = window.document.getElementById("weekly_hours");
            var hours = weeklyHourObj.options[weeklyHourObj.selectedIndex].value;

            var weeklyMinuteObj = window.document.getElementById("weekly_minute");
            var minutes = weeklyMinuteObj.options[weeklyMinuteObj.selectedIndex].value;

            var weekObj = window.document.getElementById("weekly_week");
            var week;
            var count = 0;
            
            if (weekObj.options.selectedIndex == -1)  {
                window.document.getElementById("cronValidation").innerHTML = '<b>Select Cron Expression</b>';
            } else {
                for (var i = 0; i < weekObj.options.length; i++){
                    if (weekObj.options[i].selected){
                        if (count == 0) {
                            week = weekObj.options[i].value;
                        } else {
                           week += "," + weekObj.options[i].value;
                        }
                        count++;
                    }
                }
	        	var params = "cronBy=";
				params = params.concat("Weekly");
				params = params.concat("&hours=");
				params = params.concat(hours);
				params = params.concat("&minutes=");
				params = params.concat(minutes);
				params = params.concat("&week=");
				params = params.concat(week);
				cronValidationLoad(params);
            }
      } else if (ids == "Monthly") {
            var monthlyDayObj = window.document.getElementById("monthly_day");
            var day = monthlyDayObj.options[monthlyDayObj.selectedIndex].value;

            var monthlyHourObj = window.document.getElementById("monthly_hour");
            var hours = monthlyHourObj.options[monthlyHourObj.selectedIndex].value;

            var monthlyMinuteObj = window.document.getElementById("monthly_minute");
            var minutes = monthlyMinuteObj.options[monthlyMinuteObj.selectedIndex].value;

            var monthObj = window.document.getElementById("monthly_month");
            var month;
            var count = 0;
            if (monthObj.options.selectedIndex == -1)  {
                window.document.getElementById("cronValidation").innerHTML = '<b>Select Cron Expression</b>';
            } else {
                for (var i = 0; i < monthObj.options.length; i++){
                    if (monthObj.options[i].selected){
                        if (count == 0) {
                            month = monthObj.options[i].value;
                        } else {
                           month += "," + monthObj.options[i].value;
                        }
                        count++;
                    }
                }
        		var params = "cronBy=";
				params = params.concat("Monthly");
				params = params.concat("&hours=");
				params = params.concat(hours);
				params = params.concat("&minutes=");
				params = params.concat(minutes);
				params = params.concat("&month=");
				params = params.concat(month);
				params = params.concat("&day=");
				params = params.concat(day);
				cronValidationLoad(params);
            }
        }
    }
    
    function cronValidationLoad(params) {
    	popup('cronValidation', $('#ciForm'), $('#cronValidation'), '', '', params);
    }
</script>