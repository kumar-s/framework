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
<%@ page import="org.apache.commons.lang.StringUtils"%>
<%@ page import="org.apache.commons.collections.CollectionUtils"%>
<%@ page import="java.util.HashMap"%>
<%@ page import="java.util.Map"%>
<%@ page import="java.util.Iterator"%>
<%@ page import="java.util.regex.*"%>

<%@ page import="com.photon.phresco.framework.model.SettingsInfo"%>
<%@ page import="com.photon.phresco.framework.model.PropertyInfo"%>
<%@ page import="com.photon.phresco.commons.FrameworkConstants"%>
<%@ page import="com.photon.phresco.util.Constants"%>
<%@ page import="com.photon.phresco.configuration.Environment" %>

<%@ include file="../../userInfoDetails.jsp" %>

<script type="text/javascript" src="js/delete.js" ></script>
<script type="text/javascript" src="js/confirm-dialog.js" ></script>
<script type="text/javascript" src="js/loading.js" ></script>
<script type="text/javascript" src="js/home-header.js" ></script>

<style type="text/css">
   	table th {
		padding: 0 0 0 9px;  
	}
	   	
	td {
	 	padding: 5px;
	 	text-align: left;
	}
	  
	th {
	 	padding: 0 5px;
	 	text-align: left;
	}
</style>

<% 
    String projectCode = (String)request.getAttribute(FrameworkConstants.REQ_PROJECT_CODE);
	List<Environment> envInfoValues = (List<Environment>) request.getAttribute(FrameworkConstants.ENVIRONMENTS);
%>    

<form class="configurations_list_form" id="formConfigList" name="listForm">
    <div class="operation">
        <a href="#" class="btn primary" id="addConfiguration"><s:text name="label.add"/></a>
        <input id="deleteButton" type="button" value="Delete" class="btn disabled" disabled="disabled"/>
        <input id="environmentButton" type="button" value="Environments" class="btn primary" />
    </div>
    
    <s:if test="hasActionMessages()">
        <div class="alert-message success"  id="successmsg">
            <s:actionmessage />
        </div>
    </s:if>
     
    <%
        List<SettingsInfo> configurations = (List<SettingsInfo>)request.getAttribute("configuration");
    	Map<String, String> urls = new HashMap<String, String>();
        if (CollectionUtils.isEmpty(configurations)) {
    %>
        <div class="alert-message block-message warning">
            <center><s:label key="configuration.error.message" cssClass="errorMsgLabel"/></center>
        </div>
    <%
        } else {
    %>
   		<div class="settingsList_table_div">
    		<div class="fixed-table-container">
	      		<div class="header-background"> </div>
	      		<div class="fixed-table-container-inner">
			        <table cellspacing="0" class="zebra-striped">
			          	<thead>
				            <tr>
								<th class="first">
				                	<div class="th-inner-head ">
				                		<input type="checkbox" value="" id="checkAllAuto" name="checkAllAuto">
				                	</div>
				              	</th>
				              	<th class="second">
				                	<div class="th-inner-head "><s:text name="label.name"/></div>
				              	</th>
				              	<th class="third">
				                	<div class="th-inner-head "><s:text name="label.description"/></div>
				              	</th>
				              	<th class="third">
				                	<div class="th-inner-head "><s:text name="label.type"/></div>
				              	</th>
				              	<th class="third">
                                       <div class="th-inner-head "><s:text name="label.environment"/></div>
                                   </th>
				              	<th class="third">
				                	<div class="th-inner-head "><s:text name="label.status"/></div>
				              	</th>
				            </tr>
			          	</thead>
			
			          	<tbody>
			          	
			          	<%
							for (SettingsInfo configuration : configurations) {
								for (Environment enves : envInfoValues) { 
									if (configuration.getEnvName().equals(enves.getName())) {
						%>
									<tr>
					              		<td class="checkbox_list">
					              			<input type="checkbox" class="check" name="check" value="<%=configuration.getEnvName() + "," + configuration.getName() %>">
					              		</td>
					              		<td>
					              			<a href="#" class="editConfiguration" id="<%= configuration.getName() %>"
					              				onclick="editConfiguration('<%= configuration.getName()%>', '<%= configuration.getEnvName() %>')"><%= configuration.getName() %></a>
					              		</td>
					              		<td style="width: 25%;"><%= configuration.getDescription() %></td>
					              		<td><%= configuration.getType() %></td>
					              		<td style="width: 25%;" title="<%= enves.getDesc() %>"><%= configuration.getEnvName() %></td>
					              		<td>
						              		<%	
				                            	String protocol = null;
				                            	String host = null;
				                            	int port = 0;
				                   				List<PropertyInfo> propertyInfos = configuration.getPropertyInfos();
				                           		String url = "";
				                           		for (PropertyInfo propertyInfo : propertyInfos) {
				                   					if (propertyInfo.getKey().equals(Constants.SERVER_PROTOCOL)) {
				                   						protocol = propertyInfo.getValue();
				                   					}
				                   					if (propertyInfo.getKey().equals(Constants.SERVER_HOST)) {
				                   						host = propertyInfo.getValue();
				                   					}
				                   					if (propertyInfo.getKey().equals(Constants.SERVER_PORT)) {
				                   						port = Integer.parseInt(propertyInfo.getValue());
				                   					}
				                           		}
				                           		if (StringUtils.isEmpty(protocol)) {
				                           			protocol = Constants.DB_PROTOCOL;
				                           		}
				                           		String configName = configuration.getName() + configuration.getEnvName();
				                           		Pattern pattern = Pattern.compile("\\s+");
				                           		Matcher matcher = pattern.matcher(configName);
				                           		boolean check = matcher.find();
				                           		String configNameForId = matcher.replaceAll("");
				                           		urls.put(configNameForId, protocol +","+ host + "," + port);
				                           	%>
			                           		<img src="images/icons/inprogress.png" alt="status-up" title="Loading" id="isAlive<%= configNameForId %>">
		       	  						</td>
					            	</tr>
						<%
									}
								}
							}
						%>	
			          	</tbody>
			        </table>
	      		</div>
    		</div>
   		</div>
    <%
        }
    %>
    
    <!-- Hidden Fields -->
	<input type="hidden" name="projectCode" value="<%= projectCode %>">
</form>

<script type="text/javascript">
	//To check whether the device is ipad or not and then apply jquery scrollbar
	if (!isiPad()) {
		$(".fixed-table-container-inner").scrollbars();
	}

    $(document).ready(function() {
    	enableScreen();
    	
    	<% 
			if (urls != null) {
				Iterator iterator = urls.keySet().iterator();  
		    	while (iterator.hasNext()) {
					String id = iterator.next().toString();  
					String url = urls.get(id).toString();  
	 	%>
					isConnectionAlive('<%= url %>', '<%= id %>');
		<%
		    	}
			}
		%>
		
		$("#addConfiguration").click(function() {
			showLoadingIcon();
			performAction("addConfiguration", $("#formConfigList"), $('#tabDiv'));
		});
		
		$('#deleteButton').click(function() {
			$("#confirmationText").html("Do you want to delete the selected configuration(s)?");
		    dialog('block');
		    escBlockPopup();
		});
		
		$('#environmentButton').click(function() {
			openEnvironmentPopup();
		});
		    
		$('form[name=listForm]').submit(function() {
			showProgessBar("Deleting Configuration (s)", 100);
            performAction("deleteConfigurations", $("#formConfigList"), $('#tabDiv'));
	        return false;
	    });
    });
    
    function isConnectionAlive(url, id) {
        $.ajax({
        	url : 'connectionAliveCheck',
        	data : {
        		'url' : url,
        	},
        	type : "get",
        	datatype : "json",
        	success : function(data) {
        		if ($.trim(data) == 'true') {
        			$('#isAlive' + id).attr("src","images/icons/status-up.png");
        			$('#isAlive' + id).attr("title","Alive");
        		}
				if ($.trim(data) == 'false') {
					$('#isAlive' + id).attr("src","images/icons/status-down.png");
					$('#isAlive' + id).attr("title","Down");
        		}
        	}
        });
    }
    
    function openEnvironmentPopup() {
    	$("#popup_div").empty();
        showPopup();
		popup('openEnvironmentPopup', $("#formConfigList"), $('#popup_div'));
     }
    
    function editConfiguration(configName, envName) {
    	showLoadingIcon();
    	
    	var params = "oldName=";
    	params = params.concat(configName);
    	params = params.concat("&envName=");
    	params = params.concat(envName);
    	performAction("editConfiguration", $("#formConfigList"), $('#tabDiv'), '', params);
    }
</script>