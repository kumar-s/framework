/*
 * ###
 * Framework Web Archive
 * 
 * Copyright (C) 1999 - 2012 Photon Infotech Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ###
 */
package com.photon.phresco.framework.actions.applications;

import java.io.BufferedReader;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.photon.phresco.framework.PhrescoFrameworkFactory;
import com.photon.phresco.framework.actions.FrameworkBaseAction;
import com.photon.phresco.framework.api.ActionType;
import com.photon.phresco.framework.api.Project;
import com.photon.phresco.framework.api.ProjectAdministrator;
import com.photon.phresco.framework.api.ProjectRuntimeManager;
import com.photon.phresco.framework.commons.FrameworkUtil;
import com.photon.phresco.framework.commons.LogErrorReport;
import com.photon.phresco.model.ProjectInfo;
import com.photon.phresco.util.Utility;
import com.phresco.pom.site.ReportCategories;
import com.phresco.pom.site.Reports;

public class SiteReport extends FrameworkBaseAction {
	private static final long serialVersionUID = 1L;
	private static final Logger S_LOGGER = Logger.getLogger(SiteReport.class);
	
	private String projectCode = null;
    
	public String viewSiteReport() {
		S_LOGGER.debug("Entering Method  SiteReport.viewSiteReport()");
		
		try {
		    ProjectAdministrator administrator = PhrescoFrameworkFactory.getProjectAdministrator();
		    ProjectInfo projectInfo = administrator.getProject(projectCode).getProjectInfo();
		    List<Reports> selectedReports = administrator.getPomReports(projectInfo);
            getHttpRequest().setAttribute(REQ_SITE_SLECTD_REPORTS, selectedReports);
			getHttpRequest().setAttribute(REQ_PROJECT_CODE, projectCode);
		} catch (Exception e) {
			S_LOGGER.error("Entered into catch block of SiteReport.viewSiteReport()"
					+ FrameworkUtil.getStackTraceAsString(e));
			new LogErrorReport(e, "View site report");
		}
		
		return APP_SITE_REPORT_VIEW;
	}
	
	public String checkForSiteReport() {
		S_LOGGER.debug("Entering Method  SiteReport.checkForSiteReport()");
		
		try {
			Properties sysProps = System.getProperties();
	        S_LOGGER.debug( "Phresco FileServer Value of " + PHRESCO_FILE_SERVER_PORT_NO + " is " + sysProps.getProperty(PHRESCO_FILE_SERVER_PORT_NO) );
	        String phrescoFileServerNumber = sysProps.getProperty(PHRESCO_FILE_SERVER_PORT_NO);
			StringBuilder sb = new StringBuilder();
        	StringBuilder siteReportPath = new StringBuilder(Utility.getProjectHome());
        	siteReportPath.append(projectCode);
        	siteReportPath.append(File.separatorChar);
        	siteReportPath.append(SITE_TARGET);
        	siteReportPath.append(File.separatorChar);
        	siteReportPath.append(INDEX_HTML);
            File indexPath = new File(siteReportPath.toString());
         	if (indexPath.isFile() && StringUtils.isNotEmpty(phrescoFileServerNumber)) {
            	sb.append(HTTP_PROTOCOL);
            	sb.append(PROTOCOL_POSTFIX);
            	sb.append(LOCALHOST);
            	sb.append(COLON);
            	sb.append(phrescoFileServerNumber);
            	sb.append(FORWARD_SLASH);
            	sb.append(projectCode);
            	sb.append(FORWARD_SLASH);
            	sb.append(SITE_TARGET);
            	sb.append(FORWARD_SLASH);
            	sb.append(INDEX_HTML);
            	getHttpRequest().setAttribute(REQ_SITE_REPORT_PATH, sb.toString());
         	} else {
         		getHttpRequest().setAttribute(REQ_ERROR, false);
         	}
		} catch (Exception e) {
			S_LOGGER.error("Entered into catch block of SiteReport.checkForSiteReport()"
					+ FrameworkUtil.getStackTraceAsString(e));
			new LogErrorReport(e, "Getting site report");
		}
		
		return APP_SITE_REPORT_VIEW;
	}
	
	public String generateSiteReport() {
		S_LOGGER.debug("Entering Method  SiteReport.generateReport()");
		
		try {
			ActionType actionType = null;
			ProjectAdministrator administrator = PhrescoFrameworkFactory.getProjectAdministrator();
			Project project = administrator.getProject(projectCode);
			ProjectRuntimeManager runtimeManager = PhrescoFrameworkFactory.getProjectRuntimeManager();
			actionType = ActionType.SITE_REPORT;
			actionType.setWorkingDirectory(null);
			BufferedReader reader = runtimeManager.performAction(project, actionType, null, null);
			getHttpSession().setAttribute(projectCode + REQ_SITE_REPORT, reader);
			getHttpRequest().setAttribute(REQ_PROJECT_CODE, projectCode);
			getHttpRequest().setAttribute(REQ_TEST_TYPE, REQ_SITE_REPORT);
		} catch (Exception e) {
			S_LOGGER.error("Entered into catch block of SiteReport.generateSiteReport()"
					+ FrameworkUtil.getStackTraceAsString(e));
			new LogErrorReport(e, "Generating site report");
		}
		
		return APP_ENVIRONMENT_READER;
	}
	
	public String configure() {
		S_LOGGER.debug("Entering Method  SiteReport.configure()");
		
		try {
			ProjectAdministrator administrator = PhrescoFrameworkFactory.getProjectAdministrator();
			ProjectInfo projectInfo = administrator.getProject(projectCode).getProjectInfo();
			List<Reports> reports = administrator.getReports(projectInfo);
			List<Reports> selectedReports = administrator.getPomReports(projectInfo);
			getHttpRequest().setAttribute(REQ_SITE_REPORTS, reports);
			getHttpRequest().setAttribute(REQ_SITE_SLECTD_REPORTS, selectedReports);
			getHttpRequest().setAttribute(REQ_PROJECT_CODE, projectCode);
		} catch (Exception e) {
			S_LOGGER.error("Entered into catch block of SiteReport.configure()"
					+ FrameworkUtil.getStackTraceAsString(e));
			new LogErrorReport(e, "Getting type of reports");
		}
		
		return APP_SITE_REPORT_CONFIGURE;
	}
	
	public String createReportConfig() {
		S_LOGGER.debug("Entering Method  SiteReport.createReportConfig()");
		
		try {
			ProjectAdministrator administrator = PhrescoFrameworkFactory.getProjectAdministrator();
			ProjectInfo projectInfo = administrator.getProject(projectCode).getProjectInfo();
			
			//To get the selected reports from the UI
			String[] arraySelectedReports = getHttpRequest().getParameterValues(REQ_SITE_REPORTS);
			List<String> selectedReports = null; 
			if (!ArrayUtils.isEmpty(arraySelectedReports)) {
				selectedReports = Arrays.asList(arraySelectedReports);
			}
			
			//To get the selected ReportCategories from the UI
			String[] arraySelectedRptCategories = getHttpRequest().getParameterValues(REQ_SITE_SLECTD_REPORTSCATEGORIES);
			List<ReportCategories> selectedReportCategories = new ArrayList<ReportCategories>();
			if (!ArrayUtils.isEmpty(arraySelectedRptCategories)) {
				for (String arraySelectedRptCategory : arraySelectedRptCategories) {
					ReportCategories cat = new ReportCategories();
					cat.setName(arraySelectedRptCategory);
					selectedReportCategories.add(cat);
				}
			}
			
			// To get the list of Reports to be added
			List<Reports> allReports = administrator.getReports(projectInfo);
			List<Reports> reportsToBeAdded = new ArrayList<Reports>();
			if (CollectionUtils.isNotEmpty(selectedReports) && CollectionUtils.isNotEmpty(allReports)) {
				for (Reports report : allReports) {
					if (selectedReports.contains(report.getArtifactId())) {
						reportsToBeAdded.add(report);
					}
				}
			}
			
			administrator.updateRptPluginInPOM(projectInfo, reportsToBeAdded, selectedReportCategories);
			addActionMessage(getText(SUCCESS_SITE_CONFIGURE));
		} catch (Exception e) {
			S_LOGGER.error("Entered into catch block of SiteReport.createReportConfig()"
					+ FrameworkUtil.getStackTraceAsString(e));
			new LogErrorReport(e, "Configuring site report");
		}
		
		return viewSiteReport();
	}
	
	public String getProjectCode() {
		return projectCode;
	}

	public void setProjectCode(String projectCode) {
		this.projectCode = projectCode;
	}
}