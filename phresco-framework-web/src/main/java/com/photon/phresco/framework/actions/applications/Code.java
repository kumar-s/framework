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
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.photon.phresco.commons.model.ApplicationInfo;
import com.photon.phresco.exception.PhrescoException;
import com.photon.phresco.framework.FrameworkConfiguration;
import com.photon.phresco.framework.PhrescoFrameworkFactory;
import com.photon.phresco.framework.actions.FrameworkBaseAction;
import com.photon.phresco.framework.api.ActionType;
import com.photon.phresco.framework.api.Project;
import com.photon.phresco.framework.api.ProjectAdministrator;
import com.photon.phresco.framework.api.ProjectRuntimeManager;
import com.photon.phresco.framework.commons.ApplicationsUtil;
import com.photon.phresco.framework.commons.FrameworkUtil;
import com.photon.phresco.framework.commons.LogErrorReport;
import com.photon.phresco.framework.commons.PBXNativeTarget;
import com.photon.phresco.util.TechnologyTypes;
import com.photon.phresco.util.Utility;
import com.phresco.pom.util.PomProcessor;

public class Code extends FrameworkBaseAction {
    private static final long serialVersionUID = 8217209827121703596L;
    private static final Logger S_LOGGER = Logger.getLogger(Code.class);
    
    private String projectCode = null;
	private String skipTest = null;
    private String codeTechnology = null;
    private String report = null;
    private String validateAgainst = null;
	private String target = null;
	private static String FUNCTIONALTEST = "functional";

	public String view() {
    	S_LOGGER.debug("Entering Method Code.view()");
		String serverUrl = "";
    	try {
        	getHttpRequest().setAttribute(REQ_SELECTED_MENU, APPLICATIONS);
        	ProjectAdministrator administrator = PhrescoFrameworkFactory.getProjectAdministrator();
        	ApplicationInfo appInfo = administrator.getProject(projectCode).getApplicationInfo();
            getHttpRequest().setAttribute(REQ_PROJECT_CODE, projectCode);
            getHttpRequest().setAttribute(REQ_APPINFO, appInfo);
      		FrameworkUtil frameworkUtil = FrameworkUtil.getInstance();
			serverUrl = frameworkUtil.getSonarURL();
    	    
			URL sonarURL = new URL(serverUrl);
			HttpURLConnection connection = null;
    	    try {
    	    	connection = (HttpURLConnection) sonarURL.openConnection();
    	    	int responseCode = connection.getResponseCode();
    	    	if (responseCode != 200) {
    	    		getHttpRequest().setAttribute(REQ_ERROR, getText(SONAR_NOT_STARTED));
                }
    	    } catch(Exception e) {
    	    	getHttpRequest().setAttribute(REQ_ERROR, getText(SONAR_NOT_STARTED));
    	    }
    	} catch (Exception e) {
    		S_LOGGER.error("Entered into catch block of Code.view()"+ FrameworkUtil.getStackTraceAsString(e));
    		new LogErrorReport(e, "Code view");
    		
    		return LOG_ERROR;
        }
    	return APP_CODE;
    }
    
    public String check() {
    	S_LOGGER.debug("Entering Method Code.check()");
    	StringBuilder sb = new StringBuilder();
    	try {
	        Properties sysProps = System.getProperties();
	        S_LOGGER.debug( "Phresco FileServer Value of " + PHRESCO_FILE_SERVER_PORT_NO + " is " + sysProps.getProperty(PHRESCO_FILE_SERVER_PORT_NO) );
	        String phrescoFileServerNumber = sysProps.getProperty(PHRESCO_FILE_SERVER_PORT_NO);
	        
            FrameworkConfiguration frameworkConfig = PhrescoFrameworkFactory.getFrameworkConfig();
            ProjectAdministrator administrator = PhrescoFrameworkFactory.getProjectAdministrator();
            ApplicationInfo appInfo = administrator.getProject(projectCode).getApplicationInfo();
        	String technology = appInfo.getTechInfo().getVersion();
            if (TechnologyTypes.IPHONES.contains(technology)) {
            	StringBuilder codeValidatePath = new StringBuilder(Utility.getProjectHome());
            	codeValidatePath.append(projectCode);
            	codeValidatePath.append(File.separatorChar);
            	codeValidatePath.append(DO_NOT_CHECKIN_DIR);
            	codeValidatePath.append(File.separatorChar);
            	codeValidatePath.append(STATIC_ANALYSIS_REPORT);
            	codeValidatePath.append(File.separatorChar);
            	codeValidatePath.append(INDEX_HTML);
                File indexPath = new File(codeValidatePath.toString());
             	if (indexPath.isFile() && StringUtils.isNotEmpty(phrescoFileServerNumber)) {
                	sb.append(HTTP_PROTOCOL);
                	sb.append(PROTOCOL_POSTFIX);
                	InetAddress thisIp =InetAddress.getLocalHost();
                	sb.append(thisIp.getHostAddress());
                	sb.append(COLON);
                	sb.append(phrescoFileServerNumber);
                	sb.append(FORWARD_SLASH);
                	sb.append(projectCode);
                	sb.append(FORWARD_SLASH);
                	sb.append(DO_NOT_CHECKIN_DIR);
                	sb.append(FORWARD_SLASH);
                	sb.append(STATIC_ANALYSIS_REPORT);
                	sb.append(FORWARD_SLASH);
                	sb.append(INDEX_HTML);
             	} else {
             		getHttpRequest().setAttribute(REQ_ERROR, getText(FAILURE_CODE_REVIEW));
             	}
        	} else {
	        	String serverUrl = "";
	    	    if (StringUtils.isNotEmpty(frameworkConfig.getSonarUrl())) {
	    	    	serverUrl = frameworkConfig.getSonarUrl();
	    	    } else {
	    	    	serverUrl = getHttpRequest().getRequestURL().toString();
	    	    	StringBuilder tobeRemoved = new StringBuilder();
	    	    	tobeRemoved.append(getHttpRequest().getContextPath());
	    	    	tobeRemoved.append(getHttpRequest().getServletPath());
	
	    	    	Pattern pattern = Pattern.compile(tobeRemoved.toString());
	    	    	Matcher matcher = pattern.matcher(serverUrl);
	    	    	serverUrl = matcher.replaceAll("");
	    	    }
	        	StringBuilder builder = new StringBuilder(Utility.getProjectHome());
	        	builder.append(projectCode);
                if (StringUtils.isNotEmpty(report) && FUNCTIONALTEST.equals(report)) {
                    FrameworkUtil frameworkUtil = FrameworkUtil.getInstance();
                    builder.append(frameworkUtil.getFuncitonalTestDir(technology));
                }
                builder.append(File.separatorChar);
	        	builder.append(POM_XML);
	        	File pomPath = new File(builder.toString());
	        	PomProcessor processor = new PomProcessor(pomPath);
	        	String groupId = processor.getModel().getGroupId();
	        	String artifactId = processor.getModel().getArtifactId();
	
	        	sb.append(serverUrl);
	        	sb.append(frameworkConfig.getSonarReportPath());
	        	sb.append(groupId);
	        	sb.append(COLON);
	        	sb.append(artifactId);
	        	
	        	S_LOGGER.debug("groupid : artif ====> " + sb.toString());
	        	if (StringUtils.isNotEmpty(report) && !SOURCE_DIR.equals(report)) {
	        		sb.append(COLON);
	        		sb.append(report);
	        	}
	        	S_LOGGER.debug("Url to access API ====> " + sb.toString());

	    		try {
					URL sonarURL = new URL(sb.toString());
					HttpURLConnection connection = (HttpURLConnection) sonarURL.openConnection();
					int responseCode = connection.getResponseCode();
					S_LOGGER.info("responseCode === " + responseCode);  
					S_LOGGER.debug("Response code value " + responseCode);
					if (responseCode != 200) {
					    getHttpRequest().setAttribute(REQ_ERROR, getText(FAILURE_CODE_REVIEW));
					    S_LOGGER.debug("try APP_CODE....... " + APP_CODE);
					    return APP_CODE;
		            }
				} catch (Exception e) {
					S_LOGGER.error("Entered into catch block of Code.check()"+ FrameworkUtil.getStackTraceAsString(e));
					new LogErrorReport(e, "Code review");
					getHttpRequest().setAttribute(REQ_ERROR, getText(FAILURE_CODE_REVIEW));
					return APP_CODE;
				}
        	}
            getHttpRequest().setAttribute(REQ_PROJECT_CODE, projectCode);
            getHttpRequest().setAttribute(REQ_TECHNOLOGY, technology);
            getHttpRequest().setAttribute(REQ_SONAR_PATH, sb.toString());
    	} catch (Exception e) {
			S_LOGGER.error("Entered into catch block of Code.check()"+ FrameworkUtil.getStackTraceAsString(e));
    	}
        
        return APP_CODE;
    }
    
    public String progressValidate() {
    	S_LOGGER.debug("Entering Method  Code.progressValidate()");
    	try {
        	ProjectRuntimeManager runtimeManager;
        	ProjectAdministrator administrator = PhrescoFrameworkFactory.getProjectAdministrator();
        	Project project = administrator.getProject(projectCode);
        	String technology = project.getApplicationInfo().getTechInfo().getVersion();
            runtimeManager = PhrescoFrameworkFactory.getProjectRuntimeManager();
            Map<String, String> codeValidateMap = new HashMap<String, String>(1);
            ActionType actionType = null;
            if (TechnologyTypes.IPHONES.contains(technology)) {
            	codeValidateMap.put(IPHONE_SCHEMA_PARAM, target);
            	actionType = ActionType.IPHONE_CODE_VALIDATE;
            } else {
            	actionType = ActionType.SONAR;
            }
            
            if (FUNCTIONALTEST.equals(validateAgainst)) {
            	File projectPath = new File(Utility.getProjectHome()+ File.separator + projectCode + File.separator + TEST_DIR + File.separator + FUNCTIONAL);
            	actionType.setWorkingDirectory(projectPath.toString());
            	actionType.setProfileId(null);
            	codeValidateMap.put(CODE_VALIDATE_PARAM, FUNCTIONAL);
            	validateAgainst(validateAgainst, project, projectCode);
            } else {
            	actionType.setWorkingDirectory(null);
            	actionType.setProfileId(codeTechnology);
            }
            
            actionType.setSkipTest(Boolean.parseBoolean(skipTest));
            BufferedReader reader = runtimeManager.performAction(project, actionType, codeValidateMap, null);
            getHttpSession().setAttribute(projectCode + REQ_SONAR_PATH, reader);
            getHttpRequest().setAttribute(REQ_PROJECT_CODE, projectCode);
            getHttpRequest().setAttribute(REQ_TEST_TYPE, REQ_SONAR_PATH);
        } catch (Exception e) {
        	S_LOGGER.error("Entered into catch block of Code.progressValidate()"+ FrameworkUtil.getStackTraceAsString(e));
        	new LogErrorReport(e, "Code progressValidate");
        	
        	return LOG_ERROR;
        }
        
        return APP_ENVIRONMENT_READER;
    }
    

	private void validateAgainst(String validateAgainst, Project project, String projectCode) throws PhrescoException {
		File projectPath = new File(Utility.getProjectHome()+ File.separator + projectCode + File.separator + "test" + File.separator+"functional" + File.separator + POM_FILE);
		editSonarIncludes(projectPath, projectCode);
	}
	
	private static void editSonarIncludes(File projectPath, String projectCode)
			throws PhrescoException {
		try {
			PomProcessor pomprocessor = new PomProcessor(projectPath);
			pomprocessor.setName(projectCode);
			pomprocessor.save();
		} catch (Exception e) {
			throw new PhrescoException(e);
		}
	}
	
	public String showCodeValidatePopUp(){
		S_LOGGER.debug("Entering Method  Code.progressValidate()");
        try {
        	ProjectAdministrator administrator = PhrescoFrameworkFactory.getProjectAdministrator();
        	Project project = administrator.getProject(projectCode);
			String technology = project.getApplicationInfo().getTechInfo().getVersion();
            getHttpRequest().setAttribute(REQ_PROJECT_CODE, projectCode);
            getHttpRequest().setAttribute(REQ_APPINFO, project.getApplicationInfo());
            if (TechnologyTypes.IPHONES.contains(technology)) {
				List<PBXNativeTarget> xcodeConfigs = ApplicationsUtil.getXcodeConfiguration(projectCode);
				getHttpRequest().setAttribute(REQ_XCODE_CONFIGS, xcodeConfigs);
			}
        } catch (Exception e) {
        	S_LOGGER.error("Entered into catch block of Code.progressValidate()"+ FrameworkUtil.getStackTraceAsString(e));
        }
    	return APP_SHOW_CODE_VALIDATE_POPUP;
    }
	
    public String getProjectCode() {
        return projectCode;
    }

    public void setProjectCode(String projectCode) {
        this.projectCode = projectCode;
    }

	public String getCodeTechnology() {
		return codeTechnology;
	}

	public void setCodeTechnology(String codeTechnology) {
		this.codeTechnology = codeTechnology;
	}

	public String getReport() {
		return report;
	}

	public void setReport(String report) {
		this.report = report;
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}
	
	public String getSkipTest() {
		return skipTest;
	}

	public void setSkipTest(String skipTest) {
		this.skipTest = skipTest;
	}
	
	public String getValidateAgainst() {
		return validateAgainst;
	}

	public void setValidateAgainst(String validateAgainst) {
		this.validateAgainst = validateAgainst;
	}
}