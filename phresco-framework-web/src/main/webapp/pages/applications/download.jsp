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
<%@ page import="java.util.Arrays"%>

<%@ page import="com.photon.phresco.commons.FrameworkConstants" %>
<%@ page import="com.photon.phresco.commons.model.DownloadInfo"%>

<style>

#Downloadtab_div {
	height: 96%;
}

</style>

<!--[if IE]>
<script src="js/html5.js"></script>
<![endif]-->

<%
	List<DownloadInfo> serverDownloadInfos = (List<DownloadInfo>) request.getAttribute(FrameworkConstants.REQ_SERVER_DOWNLOAD_INFO);
	List<DownloadInfo> dbDownloadInfos = (List<DownloadInfo>) request.getAttribute(FrameworkConstants.REQ_DB_DOWNLOAD_INFO);
	List<DownloadInfo> editorDownloadInfos = (List<DownloadInfo>) request.getAttribute(FrameworkConstants.REQ_EDITOR_DOWNLOAD_INFO);
	List<DownloadInfo> toolsDownloadInfos = (List<DownloadInfo>) request.getAttribute(FrameworkConstants.REQ_TOOLS_DOWNLOAD_INFO);
	List<DownloadInfo> othersDownloadInfos = (List<DownloadInfo>) request.getAttribute(FrameworkConstants.REQ_OTHERS_DOWNLOAD_INFO);
%>

<div class="theme_accordion_container" id="Downloadtab_div">
    <section class="accordion_panel_wid">
        <div class="accordion_panel_inner">
            <section class="lft_menus_container">
            	<% 
            		if(serverDownloadInfos != null && serverDownloadInfos.size() > 0) {
            	%>
                <span class="siteaccordion" id="siteaccordion_active"><span><s:text name="label.servers"/></span></span>
                <div class="mfbox siteinnertooltiptxt">
                    <div class="scrollpanel">
                        <section class="scrollpanel_inner">
                        	<table class="download_tbl">
	                        	<thead>
	                            	<tr class="download_tbl_header">
                            			<th><s:text name="label.name"/></th>
                            			<th><s:text name="label.version"/></th>
                            			<th><s:text name="label.size"/></th>
                            			<th class="label_center"><s:text name="label.download"/></th>
                            		</tr>
	                            </thead>
	                            
	                        	<tbody>
		                    	<%
		                    		for (DownloadInfo serverDownloadInfo : serverDownloadInfos) {
		                    	%>
		                    		<tr>
		                    			<!-- TODO:Need to handle -->
		                    			<%-- <td><%= serverDownloadInfo.getName() %></td>
		                    			<td><%= serverDownloadInfo.getVersion() %></td>
		                    			<td><%= serverDownloadInfo.getFileSize() %></td>
		                    			<td class="label_center">
		                    				<a href="<%= serverDownloadInfo.getDownloadURL() %>">
		                    					<img src="images/icons/download.png" title="<%=serverDownloadInfo.getName() %>" />
		                    				</a>
		                    			</td> --%>
		                    		</tr>
	                    		<%	} %>
	                    		</tbody>
                        	</table>
                        </section>
                    </div>
                </div>
                <% 
                	} if(dbDownloadInfos != null && dbDownloadInfos.size() > 0) { 
                %>
                <span class="siteaccordion"><span><s:text name="label.database"/></span></span>
                <div class="mfbox siteinnertooltiptxt">
                    <div class="scrollpanel">
                        <section class="scrollpanel_inner">
                        	<table class="download_tbl">
	                        	<thead>
	                            	<tr class="download_tbl_header">
                            			<th><s:text name="label.name"/></th>
                            			<th><s:text name="label.version"/></th>
                            			<th><s:text name="label.size"/></th>
                            			<th class="label_center"><s:text name="label.download"/></th>
                            		</tr>	
	                            </thead>
	                            
	                        	<tbody>
		                    	<%
		                    		for (DownloadInfo dbDownloadInfo : dbDownloadInfos) {
		                    	%>
		                    		<tr>
		                    			<!-- TODO:Need to handle -->
		                    			<%-- <td><%= dbDownloadInfo.getName() %></td>
		                    			<td><%= dbDownloadInfo.getVersion() %></td>
		                    			<td><%= dbDownloadInfo.getFileSize() %></td>
		                    			<td class="label_center">
		                    				<a href="<%= dbDownloadInfo.getDownloadURL() %>">
		                    					<img src="images/icons/download.png" title="<%=dbDownloadInfo.getName()%>"/>
		                    				</a>
		                    			</td> --%>
		                    		</tr>
	                    		<%	} %>
	                    		</tbody>
                        	</table>
                        </section>
                    </div>
                </div>
                <% 
                	}  if(editorDownloadInfos != null && editorDownloadInfos.size() > 0) { 
                %>
                <span class="siteaccordion"><span><s:text name="label.editors"/></span></span>
                <div class="mfbox siteinnertooltiptxt">
                    <div class="scrollpanel">
                        <section class="scrollpanel_inner">
                        	<table class="download_tbl">
	                        	<thead>
	                            	<tr class="download_tbl_header">
                            			<th><s:text name="label.name"/></th>
                            			<th><s:text name="label.version"/></th>
                            			<th><s:text name="label.size"/></th>
                            			<th class="label_center"><s:text name="label.download"/></th>
                            		</tr>
	                            </thead>
	                        	
	                        	<tbody>
		                    	<%
		                    		for (DownloadInfo editorDownloadInfo : editorDownloadInfos) {
		                    	%>
		                    		<tr>
		                    			<!-- TODO:Need to handle -->
		                    			<%-- <td><%= editorDownloadInfo.getName() %></td>
		                    			<td><%= editorDownloadInfo.getVersion() %></td>
		                    			<td><%= editorDownloadInfo.getFileSize() %></td>
		                    			<td class="label_center">
		                    				<a href="<%= editorDownloadInfo.getDownloadURL() %>">
		                    					<img src="images/icons/download.png" title="<%= editorDownloadInfo.getName()%>"/>
		                    				</a>
		                    			</td> --%>
		                    		</tr>
	                    		<%	} %>
	                    		</tbody>
                        	</table>
                        </section>
                    </div>
                </div>
                
                
                <% 
                	} if(toolsDownloadInfos != null && toolsDownloadInfos.size() > 0) { 
                %>
                <span class="siteaccordion"><span><s:text name="label.tools"/></span></span>
                <div class="mfbox siteinnertooltiptxt">
                    <div class="scrollpanel">
                        <section class="scrollpanel_inner">
                        	<table class="download_tbl">
	                        	<thead>
	                            	<tr class="download_tbl_header">
                            			<th><s:text name="label.name"/></th>
                            			<th><s:text name="label.version"/></th>
                            			<th><s:text name="label.size"/></th>
                            			<th class="label_center"><s:text name="label.download"/></th>
                            		</tr>	
	                            </thead>
	                            
	                        	<tbody>
		                    	<%
		                    		for (DownloadInfo toolsDownloadInfo : toolsDownloadInfos) {
		                    	%>
		                    		<tr>
		                    			<!-- TODO:Need to handle -->
		                    			<%-- <td><%= toolsDownloadInfo.getName() %></td>
		                    			<td><%= toolsDownloadInfo.getVersion() %></td>
		                    			<td><%= toolsDownloadInfo.getFileSize() %></td>
		                    			<td class="label_center">
		                    				<a href="<%= toolsDownloadInfo.getDownloadURL() %>">
		                    					<img src="images/icons/download.png" title="<%=toolsDownloadInfo.getName() %>"/>
		                    				</a>
		                    			</td> --%>
		                    		</tr>
	                    		<%	} %>
	                    		</tbody>
                        	</table>
                        </section>
                    </div>
                </div>
                
                 <% 
                	} if(othersDownloadInfos != null && othersDownloadInfos.size() > 0) { 
                %>
                <span class="siteaccordion"><span><s:text name="label.others"/></span></span>
                <div class="mfbox siteinnertooltiptxt">
                    <div class="scrollpanel">
                        <section class="scrollpanel_inner">
                        	<table class="download_tbl">
	                        	<thead>
	                            	<tr class="download_tbl_header">
                            			<th><s:text name="label.name"/></th>
                            			<th><s:text name="label.version"/></th>
                            			<th><s:text name="label.size"/></th>
                            			<th class="label_center"><s:text name="label.download"/></th>
                            		</tr>	
	                            </thead>
	                            
	                        	<tbody>
		                    	<%
		                    		for (DownloadInfo otherDownloadInfos : othersDownloadInfos) {
		                    	%>
		                    		<tr>
		                    			<!-- TODO:Need to handle -->
		                    			<%-- <td><%= otherDownloadInfos.getName() %></td>
		                    			<td><%= otherDownloadInfos.getVersion() %></td>
		                    			<td><%= otherDownloadInfos.getFileSize() %></td>
		                    			<td class="label_center">
		                    				<a href="<%= otherDownloadInfos.getDownloadURL() %>">
		                    					<img src="images/icons/download.png" title="<%= otherDownloadInfos.getName() %>"/>
		                    				</a>
		                    			</td> --%>
		                    		</tr>
	                    		<%	} %>
	                    		</tbody>
                        	</table>
                        </section>
                    </div>
                </div>
                
                <% } %>
            </section>  
        </div>
    </section>
</div>

<script type="text/javascript">

/* To check whether the device is ipad or not */
if(!isiPad()){
	/* JQuery scroll bar */
	$(".accordion_panel_inner").scrollbars();
	
}

$(document).ready(function(){
	accordion();
	enableScreen();
});
</script>
