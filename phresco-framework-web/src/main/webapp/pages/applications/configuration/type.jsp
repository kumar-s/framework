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
  
<%@page import="com.itextpdf.text.log.SysoLogger"%>
<%@ taglib uri="/struts-tags" prefix="s"%>

<%@ page import="java.util.ArrayList"%>
<%@ page import="java.util.List"%>

<%@ page import="org.antlr.stringtemplate.StringTemplate" %>
<%@ page import="org.apache.commons.collections.CollectionUtils" %>

<%@ page import="com.photon.phresco.commons.FrameworkConstants"%>
<%@ page import="com.photon.phresco.commons.model.ApplicationInfo"%>
<%@ page import="com.photon.phresco.commons.model.PropertyTemplate" %>
<%@ page import="com.photon.phresco.framework.commons.FrameworkUtil" %>

<form id="configProperties">
<%
	ApplicationInfo appInfo = (ApplicationInfo) request.getAttribute(FrameworkConstants.REQ_APPINFO);
	List<PropertyTemplate> properties = (List<PropertyTemplate>) request.getAttribute(FrameworkConstants.REQ_PROPERTIES);
	StringBuilder sb = new StringBuilder();
    for (PropertyTemplate propertyTemplate : properties) {
        List<String> possibleValues = new ArrayList<String>(8);

        if (FrameworkConstants.SERVER_KEY.equals(propertyTemplate.getKey())) {
        	possibleValues = appInfo.getSelectedServers();
    	} else if (FrameworkConstants.DATABASE_KEY.equals(propertyTemplate.getKey())) {
    		possibleValues = appInfo.getSelectedDatabases();
    	} else {
    		possibleValues = propertyTemplate.getPossibleValues();
    	}
    	
        if (CollectionUtils.isNotEmpty(possibleValues)) {
             StringTemplate dropDownControl = FrameworkUtil.constructSelectElement(propertyTemplate.isRequired(), propertyTemplate.getName(), "", "", propertyTemplate.getName(), 
                     propertyTemplate.getKey(), possibleValues, null, Boolean.FALSE.toString(),"", true,  propertyTemplate.getKey() + "Control", propertyTemplate.getKey() + "Error");
             sb.append(dropDownControl);
        } else {
             StringTemplate inputControl = FrameworkUtil.constructInputElement(propertyTemplate.isRequired(), propertyTemplate.getName(), "", propertyTemplate.getType(),
                     "", propertyTemplate.getKey(), propertyTemplate.getKey(), propertyTemplate.getHelpText(), "", true,  propertyTemplate.getKey() + "Control", propertyTemplate.getKey() + "Error");
            
             sb.append(inputControl);
        }
    }
%>
	<%= sb.toString() %>
</form>