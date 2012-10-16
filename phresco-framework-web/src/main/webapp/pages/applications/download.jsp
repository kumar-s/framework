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

<%@ page import="java.util.Arrays"%>
<%@ page import="java.util.List"%>

<%@ page import="org.apache.commons.collections.CollectionUtils"%>
<%@ page import="com.photon.phresco.commons.FrameworkConstants" %>
<%@ page import="com.photon.phresco.commons.model.ArtifactInfo"%>
<%@ page import="com.photon.phresco.commons.model.DownloadInfo"%>


<%
	List<DownloadInfo> serverDownloadInfos = (List<DownloadInfo>) request.getAttribute(FrameworkConstants.REQ_SERVER_DOWNLOAD_INFO);
	List<DownloadInfo> dbDownloadInfos = (List<DownloadInfo>) request.getAttribute(FrameworkConstants.REQ_DB_DOWNLOAD_INFO);
	List<DownloadInfo> editorDownloadInfos = (List<DownloadInfo>) request.getAttribute(FrameworkConstants.REQ_EDITOR_DOWNLOAD_INFO);
	List<DownloadInfo> toolsDownloadInfos = (List<DownloadInfo>) request.getAttribute(FrameworkConstants.REQ_TOOLS_DOWNLOAD_INFO);
	List<DownloadInfo> othersDownloadInfos = (List<DownloadInfo>) request.getAttribute(FrameworkConstants.REQ_OTHERS_DOWNLOAD_INFO);
%>

<div class="theme_accordion_container">
    <section class="accordion_panel_wid">
        <div class="accordion_panel_inner">
            <section class="lft_menus_container">
            	<% 
            	if (CollectionUtils.isNotEmpty(serverDownloadInfos)) {
            	%>
                <span class="siteaccordion closereg"><span><s:text name="label.servers"/></span></span>
                <div class="mfbox siteinnertooltiptxt downloadContent">
                    <div class="scrollpanel">
                        <section class="scrollpanel_inner">
                        	<table class="download_tbl">
	                        	<thead>
	                            	<tr class="download_tbl_header">
                            			<th><s:text name="lbl.name"/></th>
                            			<th><s:text name="label.version"/></th>
                            			<th><s:text name="label.size"/></th>
                            			<th class="label_center"><s:text name="label.download"/></th>
                            		</tr>
	                            </thead>
	                            
	                        	<tbody>
		                    	<%
		                    	for (DownloadInfo serverDownloadInfo : serverDownloadInfos) {
	                    			 List<ArtifactInfo> infos = serverDownloadInfo.getArtifactGroup().getVersions();
	                    			 if (CollectionUtils.isNotEmpty(infos)) {
	                    				 for (ArtifactInfo info : infos) {
	                    		%> 
		                    		<tr>
		                    			<td><%= serverDownloadInfo.getName() %></td>
		                    			<td><%= info.getVersion() %></td>
		                    			<td><%= info.getFileSize() %></td>
		                    			<td class="label_center">
		                    				<a href="#"> 
		                    					<img src="images/icons/download.png" title="<%=serverDownloadInfo.getName() %>" />
		                    				</a>
		                    			</td> 
		                    		</tr>
	                    		<%	}
	                    		}
	                    	} %> 
                        	</table>
                        </section>
                    </div>
                </div>
                <% 
                	} if (CollectionUtils.isNotEmpty(dbDownloadInfos)) { 
                %>
                <span class="siteaccordion closereg"><span><s:text name="label.database"/></span></span>
                <div class="mfbox siteinnertooltiptxt downloadContent">
                    <div class="scrollpanel">
                        <section class="scrollpanel_inner">
                        	<table class="download_tbl">
	                        	<thead>
	                            	<tr class="download_tbl_header">
                            			<th><s:text name="lbl.name"/></th>
                            			<th><s:text name="label.version"/></th>
                            			<th><s:text name="label.size"/></th>
                            			<th class="label_center"><s:text name="label.download"/></th>
                            		</tr>	
	                            </thead>
	                            
	                        	<tbody>
		                    	<%
		                    		for (DownloadInfo dbDownloadInfo : dbDownloadInfos) {
		                    			List<ArtifactInfo> infos = dbDownloadInfo.getArtifactGroup().getVersions();
		                    			 if (CollectionUtils.isNotEmpty(infos)) {
		                    				 for (ArtifactInfo info : infos) { 
		                    	%>
		                    	
		                    		<tr>
		                    			<!-- TODO:Need to handle -->
		                    			 <td><%= dbDownloadInfo.getName() %></td>
		                    			<td><%= info.getVersion() %></td>
		                    			<td><%= info.getFileSize() %></td>
		                    			<td class="label_center">
		                    				<a href="#">
		                    					<img src="images/icons/download.png" title="<%=dbDownloadInfo.getName()%>"/>
		                    				</a>
		                    			</td>
		                    		</tr>
	                    		<%	}
		                    	}
		                    }
		                  %>
	                    		</tbody>
                        	</table>
                        </section>
                    </div>
                </div>
                <% 
                	}  if(CollectionUtils.isNotEmpty(editorDownloadInfos)) { 
                %>
                <span class="siteaccordion closereg"><span><s:text name="label.editors"/></span></span>
                <div class="mfbox siteinnertooltiptxt downloadContent">
                    <div class="scrollpanel">
                        <section class="scrollpanel_inner">
                        	<table class="download_tbl">
	                        	<thead>
	                            	<tr class="download_tbl_header">
                            			<th><s:text name="lbl.name"/></th>
                            			<th><s:text name="label.version"/></th>
                            			<th><s:text name="label.size"/></th>
                            			<th class="label_center"><s:text name="label.download"/></th>
                            		</tr>
	                            </thead>
	                        	
	                        	<tbody>
		                    	<%
		                    		for (DownloadInfo editorDownloadInfo : editorDownloadInfos) {
		                    			List<ArtifactInfo> infos = editorDownloadInfo.getArtifactGroup().getVersions();
		                    			 if (CollectionUtils.isNotEmpty(infos)) {
		                    				 for (ArtifactInfo info : infos) {
		                    	%> 
		                    		<tr>
		                    			<!-- TODO:Need to handle -->
		                    			<td><%= editorDownloadInfo.getName() %></td>
		                    			<td><%= info.getVersion() %></td>
		                    			<td><%= info.getFileSize() %></td>
		                    			<td class="label_center">
		                    				<a href="#">
		                    					<img src="images/icons/download.png" title="<%= editorDownloadInfo.getName()%>"/>
		                    				</a>
		                    			</td> 
		                    		</tr>
	                    		<%	}
		                    	}
		                    } %>
	                    		</tbody>
                        	</table>
                        </section>
                    </div>
                </div>
                
                <% 
                	} if(CollectionUtils.isNotEmpty(toolsDownloadInfos)) { 
                %>
                <span class="siteaccordion closereg"><span><s:text name="label.tools"/></span></span>
                <div class="mfbox siteinnertooltiptxt downloadContent">
                    <div class="scrollpanel">
                        <section class="scrollpanel_inner">
                        	<table class="download_tbl">
	                        	<thead>
	                            	<tr class="download_tbl_header">
                            			<th><s:text name="lbl.name"/></th>
                            			<th><s:text name="label.version"/></th>
                            			<th><s:text name="label.size"/></th>
                            			<th class="label_center"><s:text name="label.download"/></th>
                            		</tr>	
	                            </thead>
	                            
	                        	<tbody>
		                    	<%
		                    		for (DownloadInfo toolsDownloadInfo : toolsDownloadInfos) {
		                    			List<ArtifactInfo> infos = toolsDownloadInfo.getArtifactGroup().getVersions();
		                    			 if (CollectionUtils.isNotEmpty(infos)) {
		                    				 for (ArtifactInfo info : infos) {
		                    	%> 
		                    		<tr>
		                    			<!-- TODO:Need to handle -->
		                    			 <td><%= toolsDownloadInfo.getName() %></td>
		                    			<td><%= info.getVersion() %></td>
		                    			<td><%= info.getFileSize() %></td>
		                    			<td class="label_center">
		                    				<a href="#">
		                    					<img src="images/icons/download.png" title="<%=toolsDownloadInfo.getName() %>"/>
		                    				</a>
		                    			</td> 
		                    		</tr>
	                    		<%	}
		                    	}
		                    }
		                   %>
	                    		</tbody>
                        	</table>
                        </section>
                    </div>
                </div>
                
                <% 
                	} if(CollectionUtils.isNotEmpty(othersDownloadInfos)) { 
                %>
                <span class="siteaccordion closereg"><span><s:text name="label.others"/></span></span>
                <div class="mfbox siteinnertooltiptxt downloadContent">
                    <div class="scrollpanel">
                        <section class="scrollpanel_inner">
                        	<table class="download_tbl">
	                        	<thead>
	                            	<tr class="download_tbl_header">
                            			<th><s:text name="lbl.name"/></th>
                            			<th><s:text name="label.version"/></th>
                            			<th><s:text name="label.size"/></th>
                            			<th class="label_center"><s:text name="label.download"/></th>
                            		</tr>	
	                            </thead>
	                            
	                        	<tbody>
		                    	<%
		                    		for (DownloadInfo otherDownloadInfo : othersDownloadInfos) {
		                    			List<ArtifactInfo> infos = otherDownloadInfo.getArtifactGroup().getVersions();
	                    				 if (CollectionUtils.isNotEmpty(infos)) {
	                    				 for (ArtifactInfo info : infos) {
	                    		%> 
		                    		<tr>
		                    			<!-- TODO:Need to handle -->
		                    			<td><%= otherDownloadInfo.getName() %></td>
		                    			<td><%= info.getVersion() %></td>
		                    			<td><%= info.getFileSize() %></td>
		                    			<td class="label_center">
		                    				<a href="#">
		                    					<img src="images/icons/download.png" title="<%= otherDownloadInfo.getName() %>"/>
		                    				</a>
		                    			</td> 
		                    		</tr>
	                    		<%	}
	                    		}
	                    	} %>
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
	$(".mfbox").scrollbars();
	
	$(document).ready(function(){
		//accordion();
		hideLoadingIcon();
	});
</script>
