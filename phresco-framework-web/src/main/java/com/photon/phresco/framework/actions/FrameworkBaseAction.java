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
package com.photon.phresco.framework.actions;

import java.awt.Desktop;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.bind.JAXBException;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import com.photon.phresco.commons.FrameworkConstants;
import com.photon.phresco.commons.model.ApplicationInfo;
import com.photon.phresco.commons.model.LogInfo;
import com.photon.phresco.commons.model.TechnologyInfo;
import com.photon.phresco.commons.model.User;
import com.photon.phresco.exception.PhrescoException;
import com.photon.phresco.framework.FrameworkConfiguration;
import com.photon.phresco.framework.PhrescoFrameworkFactory;
import com.photon.phresco.framework.api.ApplicationManager;
import com.photon.phresco.framework.api.ProjectAdministrator;
import com.photon.phresco.framework.commons.FrameworkActions;
import com.photon.phresco.framework.commons.FrameworkUtil;
import com.photon.phresco.framework.commons.LogErrorReport;
import com.photon.phresco.service.client.api.ServiceClientConstant;
import com.photon.phresco.service.client.api.ServiceContext;
import com.photon.phresco.service.client.api.ServiceManager;
import com.photon.phresco.service.client.factory.ServiceClientFactory;
import com.photon.phresco.util.Credentials;
import com.photon.phresco.util.Utility;
import com.phresco.pom.exception.PhrescoPomException;
import com.phresco.pom.model.Model.Modules;
import com.phresco.pom.util.PomProcessor;

public class FrameworkBaseAction extends ActionSupport implements FrameworkConstants, FrameworkActions, ServiceClientConstant {
	private static final Logger S_LOGGER = Logger.getLogger(FrameworkBaseAction.class);
	private static Boolean debugEnabled = S_LOGGER.isDebugEnabled();

    private static final long serialVersionUID = 1L;
    private ProjectAdministrator projectAdministrator = null;
    private String path = null;
    private String copyToClipboard = null;
    private String customerId = "";
    private String projectId = "";
    private String appId = "";
    
    private static ServiceManager serviceManager = null;
    
    protected ServiceManager getServiceManager() {
		return serviceManager;
	}
    
    public ProjectAdministrator getProjectAdministrator() throws PhrescoException {
        if(projectAdministrator == null) {
            projectAdministrator = PhrescoFrameworkFactory.getProjectAdministrator();
        }
        
        return projectAdministrator;
    }
    
    public HttpServletRequest getHttpRequest() {
        return (HttpServletRequest) ActionContext.getContext().get(ServletActionContext.HTTP_REQUEST);
    }
    
    public HttpSession getHttpSession() {
        return getHttpRequest().getSession();
    }
    
    public void openFolder() {
		if (debugEnabled) {
			S_LOGGER.debug("Entered FrameworkBaseAction.openFolder()");
		}
		try {
			if (Desktop.isDesktopSupported()) {
				File dir = new File(Utility.getProjectHome() + path);
				if(dir.exists()){
	    		Desktop.getDesktop().open(new File(Utility.getProjectHome() + path));
				}else{
					Desktop.getDesktop().open(new File(Utility.getProjectHome()));
				}
	    	}
		} catch (Exception e) {
			if (debugEnabled) {
				S_LOGGER.error("Unable to open the Path, " + FrameworkUtil.getStackTraceAsString(e));
				new LogErrorReport(e, "Open Folder");
			}
		}
	}
    
    public void copyPath() {
    	if (debugEnabled) {
    		S_LOGGER.debug("Entered FrameworkBaseAction.copyPath");
    	}
    	File dir = new File(Utility.getProjectHome() + path);
    	if(dir.exists()){
    		copyToClipboard = Utility.getProjectHome() + path;
    	}else{
    		copyToClipboard = Utility.getProjectHome();
    	}
    	copyToClipboard ();
    }
    
    public void copyToClipboard () {
    	S_LOGGER.debug("Entered FrameworkBaseAction.copyToClipboard");
    	Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
    	clipboard.setContents(new StringSelection(copyToClipboard.replaceAll(" ", "").replaceAll("(?m)^[ \t]*\r?\n", "")), null);
    	
    }
    
    protected List<String> getProjectModules(String projectCode) {
    	try {
            StringBuilder builder = getProjectHome(projectCode);
            builder.append(File.separatorChar);
            builder.append(POM_XML);
    		File pomPath = new File(builder.toString());
    		PomProcessor processor = new PomProcessor(pomPath);
    		Modules pomModule = processor.getPomModule();
    		if (pomModule != null) {
    			return pomModule.getModule();
    		}
    	} catch (JAXBException e) {
    		e.printStackTrace();
    	} catch (IOException e) {
    		e.printStackTrace();
    	} catch (PhrescoPomException e) {
    		e.printStackTrace();
    	}
    	return null;
    }

	private StringBuilder getProjectHome(String projectCode) {
		StringBuilder builder = new StringBuilder(Utility.getProjectHome());
		builder.append(projectCode);
		return builder;
	}
    
    protected List<String> getWarProjectModules(String projectCode) throws JAXBException, IOException, ArrayIndexOutOfBoundsException, PhrescoPomException {
    	List<String> projectModules = getProjectModules(projectCode);
    	List<String> warModules = new ArrayList<String>(5);
    	if (CollectionUtils.isNotEmpty(projectModules)) {
	    	for (String projectModule : projectModules) {
	    		StringBuilder pathBuilder = getProjectHome(projectCode);
	    		pathBuilder.append(File.separatorChar);
	    		pathBuilder.append(projectModule);
	    		pathBuilder.append(File.separatorChar);
	    		pathBuilder.append(POM_XML);
	    		PomProcessor processor = new PomProcessor(new File(pathBuilder.toString()));
	    		String packaging = processor.getModel().getPackaging();
				if (StringUtils.isNotEmpty(packaging) && WAR.equalsIgnoreCase(packaging)) {
	    			warModules.add(projectModule);
	    		}
			}
    	}
    	return warModules;
    }
    
    protected String showErrorPopup(PhrescoException e, String action) {
        StringWriter sw = null;
        PrintWriter pw = null;
        try {
            sw = new StringWriter();
            pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            String stacktrace = sw.toString();
            User userInfo = (User) getHttpSession().getAttribute(SESSION_USER_INFO);
            LogInfo log = new LogInfo();
            log.setMessage(e.getLocalizedMessage());
            log.setTrace(stacktrace);
            log.setAction(action);
            log.setUserId(userInfo.getLoginId());
            setReqAttribute(REQ_LOG_REPORT, log);
        } finally {
            if (pw != null) {
                pw.close();
            }
            if (sw != null) {
                try {
                    sw.close();
                } catch (IOException e1) {
                    //Do nothing due to error popup
                }
            }
        }
        return LOG_ERROR;
    }
    
    protected User doLogin(Credentials credentials) throws PhrescoException {
        try {
            String userName = credentials.getUsername();
            String password = credentials.getPassword();
            ServiceContext context = new ServiceContext();
            FrameworkConfiguration configuration = PhrescoFrameworkFactory.getFrameworkConfig();
            context.put(SERVICE_URL, configuration.getServerPath());
            context.put(SERVICE_USERNAME, userName);
            context.put(SERVICE_PASSWORD, password);
            serviceManager = ServiceClientFactory.getServiceManager(context);
        } catch (Exception ex) {
            S_LOGGER.error(ex.getLocalizedMessage());
            throw new PhrescoException(ex);
        }
        return serviceManager.getUserInfo();
    }
    
    public ApplicationInfo getApplicationInfo() throws PhrescoException {
        ApplicationManager applicationManager = PhrescoFrameworkFactory.getApplicationManager();
        return applicationManager.getApplicationInfo(getCustomerId(), getProjectId(), getAppId());
    }
    
    public String getTechId() throws PhrescoException {
    	TechnologyInfo techInfo = getApplicationInfo().getTechInfo();
    	if (techInfo != null) {
    		return techInfo.getId();
    	}
    	throw new PhrescoException("Technology not Found");
    }
    
    public String getAppHome() throws PhrescoException {
        StringBuilder builder = new StringBuilder(Utility.getProjectHome());
        builder.append(getApplicationInfo().getAppDirName());
        builder.append(File.separator);
        builder.append(FOLDER_DOT_PHRESCO);
        builder.append(File.separator);
        builder.append(CONFIGURATION_INFO_FILE_NAME);
        return builder.toString();
    }
    
    protected void setReqAttribute(String key, Object value) {
        getHttpRequest().setAttribute(key, value);
    }
    
    protected Object getReqAttribute(String key) {
        return getHttpRequest().getAttribute(key);
    }
    
    protected void setSessionAttribute(String key, Object value) {
        getHttpSession().setAttribute(key, value);
    }
    
    protected void removeSessionAttribute(String key) {
        getHttpSession().removeAttribute(key);
    }
    
    protected Object getSessionAttribute(String key) {
        return getHttpSession().getAttribute(key);
    }

    public String getPath() {
    	return path;
    }

    public void setPath(String path) {
    	this.path = path;
    }

	public String getCopyToClipboard() {
		return copyToClipboard;
	}

	public void setCopyToClipboard(String copyToClipboard) {
		this.copyToClipboard = copyToClipboard;
	}

	public String getCustomerId() {
	    return customerId;
	}

	public void setCustomerId(String customerId) {
	    this.customerId = customerId;
	}

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

}