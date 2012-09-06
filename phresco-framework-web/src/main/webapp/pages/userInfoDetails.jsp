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
<%@ page import="com.photon.phresco.commons.FrameworkConstants" %>
<%@ page import="com.photon.phresco.commons.model.User" %>
<%@ page import="com.photon.phresco.commons.model.Role" %>

<%
	User userInfo = (User)session.getAttribute(FrameworkConstants.REQ_USER_INFO);
	boolean disableCI = false;
	if (userInfo == null || userInfo.getDisplayName() == null) {
	request.setAttribute(FrameworkConstants.REQ_LOGIN_ERROR, "Session expired");

	} else {
		if (userInfo.getRoles() != null) {
			for (Role role : userInfo.getRoles()) {
				if (role.getName().equals(FrameworkConstants.ENGINEER)) {
				 	disableCI = true;	// Restrict CI
%>
				 <script type="text/javascript">
				 	disableCreateProject(); // Restrict creating project
				 </script>
<%
				}
				if (role.equals(FrameworkConstants.RELEASE_ENGINEER)) {
%>
				 <script type="text/javascript">
				 	disableCreateProject(); // Restrict creating project
				 </script>
<%
				}
			}
		}
	}
%>