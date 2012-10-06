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

<%@ page import="java.util.List" %>
<%@ page import="org.apache.commons.lang.StringUtils" %>

<%@ page import="com.photon.phresco.commons.FrameworkConstants" %>
<%@ page import="com.photon.phresco.util.TechnologyTypes"%>
<%@ page import="com.photon.phresco.commons.model.ApplicationType"%>
<%@ page import="com.photon.phresco.commons.model.Technology"%>
<%@ page import="com.photon.phresco.commons.model.ApplicationInfo"%>

<%
    ApplicationInfo selectedInfo = (ApplicationInfo) request.getAttribute(FrameworkConstants.REQ_PROJECT_INFO);
    ApplicationType selectedAppType = (ApplicationType) request.getAttribute(FrameworkConstants.SESSION_APPLICATION_TYPE);
    String fromPage = (String) request.getAttribute(FrameworkConstants.REQ_FROM_PAGE);
    String disabled = "disabled";
    if (StringUtils.isEmpty(fromPage)) {
        disabled = "";
        fromPage = "";
    } 
    String projectCode = "";
    if (selectedInfo != null) {
        projectCode = selectedInfo.getCode();
    } else {
        projectCode = (String) request.getAttribute(FrameworkConstants.REQ_PROJECT_CODE);
        selectedInfo = (ApplicationInfo) session.getAttribute(projectCode);
    }
    
    List<String> selectedVersions = null;
	if(selectedInfo != null) {
	    //TODO:Need to handle
// 		selectedVersions = selectedInfo.getTechnology().getVersions();
	}
%>

<div class="clearfix">

    <label for="xlInput" Class="new-xlInput"><span class="red">*</span> <s:text name="label.technology"/></label>
    
    <!--  Technologies are loaded here starts-->
    <div class="input new-input">
        <%
        //TODO:Need to handle
        List<Technology> technologies = null;
//             List<Technology> technologies = selectedAppType.getTechnologies();
        %>
		<div class="app_type_float_left">
			<select id="technology" name="technology" class="xlarge" <%= disabled %> >
				<%
					Technology selectedTech = null;
					if(selectedInfo != null) {
					    //TODO:Need to handle
// 						selectedTech = selectedInfo.getTechnology();
					}
					
					String selectedStr = "";
					if (technologies != null && technologies.size() > 0) {
						
						for(Technology technology : technologies) {
							String id = technology.getId();
							String name = technology.getName();
							
							if(selectedTech != null && selectedTech.getId().equals(id)) {
								selectedStr = "selected";
							} else {
								selectedStr = "";
							}
				%>
					<option value="<%= id %>" <%= selectedStr %> > <%= name %> </option>
				<%
						}
					}
				%>
			</select>
		</div>
		
		<div id="technologyVersionDiv" style="display: none;">
			<div class="app_type_version">
				<s:text name="label.versions"/>
			</div>
			
			<div class="app_type_version_select_div" id="techVersionDiv">
				<select id="techVersion" name="techVersion" class="app_type_version_select" <%= disabled %>>
				
				</select>
			</div>
		</div>
    </div>
    <!--  Technologies are loaded here ends -->
    
</div>

<!--  Technology dependency are loaded here starts-->
<div id="techDependency"></div>
<!--  Technology dependency are loaded here ends-->
                    
<script type="text/javascript">
	$(document).ready(function() {
	    techDependencies();
	    
	    $('#technology').change(function() {
	        techDependencies();
	    });
	});
	    
	function techDependencies() {
		$("#alreadyConstructed").val("");
	    var technology = $("#technology").val();
	    var params = '<%= FrameworkConstants.REQ_APPLICATION_TYPE %>';
	    params = params.concat("=");
	    params = params.concat('<%= selectedAppType.getName() %>');
	    params = params.concat("&technology=");
	    params = params.concat(technology);
	    params = params.concat("&" + '<%= FrameworkConstants.REQ_FROM_PAGE %>' + "=");
	    params = params.concat('<%= fromPage %>');
	    /* jQuery.ajaxSetup({async:false}); */
	    popup('technology', params, $('#techDependency'), true);
	}
	
	function techVersions() {
		var technology = $("#technology").val();
		var params = '<%= FrameworkConstants.REQ_APPLICATION_TYPE %>';
	    params = params.concat("=");
	    params = params.concat('<%= selectedAppType.getName() %>');
		params = params.concat("&technology=");
		params = params.concat(technology);
		/* jQuery.ajaxSetup({async:true}); */
		performAction("techVersions", params, '', true);
	}
	
	function showPrjtInfoTechVersion() {
		<% if (selectedVersions != null) { %>
				$("#techVersion option").each(function() {
					<% for (String selectedVersion : selectedVersions) { %>
							if ($(this).val().trim() == '<%= selectedVersion %>') {
								$(this).prop("selected", "selected");
							}
					<% } %>
				});
		<% } %>
		
		var selectedTechnology = $("#technology").val();
		if ('<%= TechnologyTypes.ANDROID_NATIVE %>' == selectedTechnology) {
			if($("#pilotProjects").val() != "") {
				removeLowerTechVersions();
			}
		}
	}
</script>