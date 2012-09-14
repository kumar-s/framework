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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.photon.phresco.exception.PhrescoException;
import com.photon.phresco.framework.FrameworkConfiguration;
import com.photon.phresco.framework.PhrescoFrameworkFactory;
import com.photon.phresco.framework.actions.FrameworkBaseAction;
import com.photon.phresco.framework.api.ProjectAdministrator;
import com.photon.phresco.framework.commons.ApplicationsUtil;
import com.photon.phresco.framework.commons.FrameworkUtil;
import com.photon.phresco.framework.commons.LogErrorReport;
import com.photon.phresco.model.ApplicationType;
import com.photon.phresco.model.Database;
import com.photon.phresco.model.Module;
import com.photon.phresco.model.ModuleGroup;
import com.photon.phresco.model.ProjectInfo;
import com.photon.phresco.model.Server;
import com.photon.phresco.model.Technology;
import com.photon.phresco.model.WebService;

public class Features extends FrameworkBaseAction {
	private static final long serialVersionUID = 6608382760989903186L;
	private static final Logger S_LOGGER = Logger.getLogger(Features.class);
	private static Boolean debugEnabled = S_LOGGER.isDebugEnabled();
	private String projectCode = null;
	private String externalCode = null;
	private String fromPage = null;
	private String name = null;
	private String code = null;
	private String groupId = null;
	private String projectVersion = null;
	private String artifactId = null;
	private String description = null;
	private String application = null;
	private String technology = null;
	private List<String> techVersion = null;
	private String nameError = null;
	private String moduleId = null;
	private String version = null;
	private String moduleType = null;
	private String techId = null;
	private String preVersion = null;
	private Collection<String> dependentIds = null;
	private Collection<String> dependentVersions = null;
	private Collection<String> preDependentIds = null;
	private Collection<String> preDependentVersions = null;
	private List<String> pilotModules = null;
	private List<String> pilotJSLibs = null;
	private String configServerNames = null;
	private String configDbNames = null;
	private String fromTab = null;
	private List<String> defaultModules =  null;
	private String customerId = null;
	private boolean validated = false;

	public String features() {
		if (debugEnabled) {
			S_LOGGER.debug("Entering Method  Features.features()");
		}
		
		String returnPage = APP_FEATURES_ONE_CLM;
		boolean left = false;
		boolean rightBottom = false;
		boolean right = false;

		try {
			ProjectInfo projectInfo = null;
			ProjectAdministrator administrator = PhrescoFrameworkFactory.getProjectAdministrator();
			if (StringUtils.isEmpty(fromPage) && validate(administrator)) {
				setValidated(true);
				return SUCCESS;
			}
			if (StringUtils.isEmpty(fromPage)
					&& StringUtils.isNotEmpty(projectCode)) { // previous button clicked
				projectInfo = (ProjectInfo) getHttpSession().getAttribute(projectCode);
			} else if (StringUtils.isNotEmpty(fromPage)) { // For edit project
				projectInfo = administrator.getProject(projectCode)
						.getProjectInfo();
				if (description != null) {
					projectInfo.setDescription(description);
				}
				if (externalCode != null) {
					projectInfo.setProjectCode(externalCode);
				}
				if (projectVersion != null) {
					projectInfo.setVersion(projectVersion);
				}
				if (groupId != null) {
					projectInfo.setGroupId(groupId);
				}
				if (artifactId != null) {
					projectInfo.setArtifactId(artifactId);
				}
				application = projectInfo.getApplication();
				technology = projectInfo.getTechnology().getId();
				setTechnology(projectInfo, administrator);
			} else { // For creating new project
				projectInfo = new ProjectInfo();
			}
			if (StringUtils.isEmpty(fromPage)) {
				setAppInfos(projectInfo, administrator);
			}

			getHttpRequest().setAttribute(REQ_TEMP_SELECTED_PILOT_PROJ, getHttpRequest().getParameter(REQ_TEMP_SELECTED_PILOT_PROJ));

			String selectedFeatures = getHttpRequest().getParameter(REQ_TEMP_SELECTEDMODULES);
			if (StringUtils.isNotEmpty(selectedFeatures)) {
				Map<String, String> mapFeatures = ApplicationsUtil.stringToMap(selectedFeatures);
				getHttpRequest().setAttribute(REQ_TEMP_SELECTEDMODULES, mapFeatures);
			}

			String selectedJsLibs = getHttpRequest().getParameter(REQ_SELECTED_JSLIBS);
			if (StringUtils.isNotEmpty(selectedJsLibs)) {
				Map<String, String> mapJsLibs = ApplicationsUtil.stringToMap(selectedJsLibs);
				getHttpRequest().setAttribute(REQ_TEMP_SELECTED_JSLIBS, mapJsLibs);
			}

			setFeaturesInRequest(administrator, projectInfo);
			getHttpRequest().setAttribute(REQ_PROJECT_INFO, projectInfo);

			List<ModuleGroup> coreModules = (List<ModuleGroup>) getHttpRequest().getAttribute(REQ_CORE_MODULES);
			List<ModuleGroup> customModules = (List<ModuleGroup>) getHttpRequest().getAttribute(REQ_CUSTOM_MODULES);
			List<ModuleGroup> allJsLibs = (List<ModuleGroup>) getHttpRequest().getAttribute(REQ_ALL_JS_LIBS);

			// Assigning the position of the coreModule
			if (CollectionUtils.isNotEmpty(coreModules)) { // Assigning coreModule to the left position
				left = true;
				getHttpRequest().setAttribute(REQ_FEATURES_FIRST_MDL_CAT, REQ_EXTERNAL_FEATURES);
				getHttpRequest().setAttribute(REQ_FEATURES_LEFT_MODULES, coreModules);
			}

			// Assigning the position of the customModule
			if (!left && CollectionUtils.isNotEmpty(customModules)) { // Assigning customModule to the left position
				left = true;
				getHttpRequest().setAttribute(REQ_FEATURES_FIRST_MDL_CAT, REQ_CUSTOM_FEATURES);
				getHttpRequest().setAttribute(REQ_FEATURES_LEFT_MODULES, customModules);
			} else if (left && CollectionUtils.isNotEmpty(customModules)) { // Assigning customModule to the right bottom position
				right = true;
				getHttpRequest().setAttribute(REQ_FEATURES_SECOND_MDL_CAT, REQ_CUSTOM_FEATURES);
				getHttpRequest().setAttribute(REQ_FEATURES_RIGHT_MODULES, customModules);
			}

			// Assigning the position of the JSLibraries
			if (left && right && CollectionUtils.isNotEmpty(allJsLibs)) { // Assigning JSLibraries to the right bottom position
				rightBottom = true;
			} else if (left && !right && CollectionUtils.isNotEmpty(allJsLibs)) { // Assigning JSLibraries to the right position
				right = true;
				getHttpRequest().setAttribute(REQ_FEATURES_SECOND_MDL_CAT, REQ_JS_LIBS);
				getHttpRequest().setAttribute(REQ_FEATURES_RIGHT_MODULES, allJsLibs);
			} else if (!left && !right && CollectionUtils.isNotEmpty(allJsLibs)) { // Assigning JSLibraries to the left position
				left = true;
				getHttpRequest().setAttribute(REQ_FEATURES_FIRST_MDL_CAT, REQ_JS_LIBS);
				getHttpRequest().setAttribute(REQ_FEATURES_LEFT_MODULES, allJsLibs);
			}

			if (left && right && rightBottom) {
				returnPage = APP_FEATURES_THREE_CLM;
			} else if (left && right && !rightBottom) {
				returnPage = APP_FEATURES_TWO_CLM;
			}
			getHttpRequest().setAttribute(REQ_CONFIG_SERVER_NAMES, configServerNames);
			getHttpRequest().setAttribute(REQ_CONFIG_DB_NAMES, configDbNames);
			FrameworkConfiguration configuration = PhrescoFrameworkFactory.getFrameworkConfig();
			getHttpRequest().setAttribute(REQ_SERVER_URL, configuration.getServerPath());
			getHttpRequest().setAttribute(REQ_CUSTOMER_ID, customerId);
		} catch (PhrescoException e) {
			if (debugEnabled) {
				S_LOGGER.error("Entered into catch block of Features.list()"
						+ FrameworkUtil.getStackTraceAsString(e));
			}
			new LogErrorReport(e, "Feature list");
			
			return LOG_ERROR;
		}
		
		return returnPage;
	}

	private void setAppInfos(ProjectInfo projectInfo, ProjectAdministrator administrator) throws PhrescoException {
		projectInfo.setName(name);
		projectInfo.setCode(code);
		if (StringUtils.isNotEmpty(externalCode)) {
			projectInfo.setProjectCode(externalCode);
		}
		if (StringUtils.isNotEmpty(groupId)) {
			projectInfo.setGroupId(groupId);
		}
		if (StringUtils.isNotEmpty(artifactId)) {
			projectInfo.setArtifactId(artifactId);
		}
		projectInfo.setVersion(projectVersion);
		projectInfo.setDescription(description);
		projectInfo.setApplication(application);
		projectInfo.setTechId(technology);
		projectInfo.setCustomerId(customerId);
		String pilotProjectName = getHttpRequest().getParameter(REQ_SELECTED_PILOT_PROJ);
		projectInfo.setPilotProjectName(pilotProjectName);
		setTechnology(projectInfo, administrator);
		FrameworkUtil.setAppInfoDependents(getHttpRequest(), customerId);
	}

	private void setTechnology(ProjectInfo projectInfo, ProjectAdministrator administrator) throws PhrescoException {
		try {
			Technology newTechnology = new Technology();
			if (StringUtils.isEmpty(fromPage)) {
				ApplicationType applicationType = administrator.getApplicationType(application, customerId);
				Technology selectedTechnology = applicationType.getTechonology(technology);
				newTechnology.setId(selectedTechnology.getId());
				newTechnology.setName(selectedTechnology.getName());
				newTechnology.setVersions(techVersion);
			} else {
				newTechnology.setId(projectInfo.getTechId());
				newTechnology.setName(projectInfo.getTechnology().getName());
				newTechnology.setVersions(projectInfo.getTechnology().getVersions());
				newTechnology.setJsLibraries(projectInfo.getTechnology().getJsLibraries());
				newTechnology.setModules(projectInfo.getTechnology().getModules());
			}
			
			List<Server> servers = administrator.getServersByTech(getTechnology(), customerId);
			List<Database> databases = administrator.getDatabasesByTech(getTechnology(), customerId);
			List<WebService> webservices = administrator.getWebServices(getTechnology(), customerId);
			
			String selectedServers = getHttpRequest().getParameter(REQ_SELECTED_SERVERS);
			String selectedDatabases = getHttpRequest().getParameter(REQ_SELECTED_DBS);
			String[] selectedWebservices = getHttpRequest().getParameterValues(REQ_WEBSERVICES);
			boolean isEmailSupported = false;
			if (APP_INFO.equals(fromTab)) {
				if (StringUtils.isNotEmpty(selectedServers)) {
					List<String> listTempSelectedServers = null;
					if (StringUtils.isNotEmpty(selectedServers)) {
						listTempSelectedServers = new ArrayList<String>(
								Arrays.asList(selectedServers.split("#SEP#")));
					}
					newTechnology.setServers(ApplicationsUtil.getSelectedServers(servers,
							listTempSelectedServers));
				}
				
				if (StringUtils.isNotEmpty(selectedDatabases)) {
					List<String> listTempSelectedDatabases = null;
					if (StringUtils.isNotEmpty(selectedDatabases)) {
						listTempSelectedDatabases = new ArrayList<String>(
								Arrays.asList(selectedDatabases.split("#SEP#")));
					}
					newTechnology.setDatabases(ApplicationsUtil.getSelectedDatabases(
							databases, listTempSelectedDatabases));
				}
				
				if (!ArrayUtils.isEmpty(selectedWebservices)) {
					newTechnology.setWebservices(ApplicationsUtil.getSelectedWebservices(
							webservices, ApplicationsUtil.getArrayListFromStrArr(selectedWebservices)));
				}
				
				if (getHttpRequest().getParameter(REQ_EMAIL_SUPPORTED) != null) {
					isEmailSupported = Boolean.parseBoolean(getHttpRequest().getParameter(REQ_EMAIL_SUPPORTED));
				}
				newTechnology.setEmailSupported(isEmailSupported);
			} else {
				if (projectInfo != null && projectInfo.getTechnology() != null) {
					newTechnology.setServers(projectInfo.getTechnology().getServers());
					newTechnology.setDatabases(projectInfo.getTechnology().getDatabases());
					newTechnology.setWebservices(projectInfo.getTechnology().getWebservices());
					newTechnology.setEmailSupported(projectInfo.getTechnology().isEmailSupported());
				}
			}
			projectInfo.setTechnology(newTechnology);
		} catch (Exception e) {
			e.printStackTrace();
			throw new PhrescoException(e);
		}
	}

    private boolean validate(ProjectAdministrator administrator) throws PhrescoException {
    	try {
        	if (StringUtils.isEmpty(name.trim())) {
        		setNameError(ERROR_NAME);
                return true;
            }
            if (administrator.getProject(code) != null) {
            	setNameError(ERROR_DUPLICATE_NAME);
                return true;
            }
		} catch (Exception e) {
			throw new PhrescoException(e);
		}
    	
        return false;
    }
    
	public void setFeaturesInRequest(ProjectAdministrator administrator,
			ProjectInfo projectInfo) throws PhrescoException {
		if (debugEnabled) {
			S_LOGGER.debug("Entering Method  Features.setFeaturesInRequest()");
		}
		
		try {
			Technology selectedTechnology = projectInfo.getTechnology();

			List<ModuleGroup> coreModule = administrator.getCoreModules(selectedTechnology.getId(), customerId);
			if (CollectionUtils.isNotEmpty(coreModule)) {
				getHttpRequest().setAttribute(REQ_CORE_MODULES, coreModule);
			}

			List<ModuleGroup> customModule = (List<ModuleGroup>) administrator
					.getCustomModules(selectedTechnology.getId(), customerId);
			if (CollectionUtils.isNotEmpty(customModule)) {
				getHttpRequest().setAttribute(REQ_CUSTOM_MODULES, customModule);
			}

			List<ModuleGroup> jsLibs = administrator.getJSLibs(selectedTechnology.getId(), customerId);
			if (CollectionUtils.isNotEmpty(jsLibs)) {
				getHttpRequest().setAttribute(REQ_ALL_JS_LIBS, jsLibs);
			}
			
			if (CollectionUtils.isNotEmpty(selectedTechnology.getModules())) {
	            getHttpRequest().setAttribute(REQ_PROJECT_INFO_MODULES,
	                    ApplicationsUtil.getMapFromModuleGroups(selectedTechnology.getModules()));
	        }

	        if (CollectionUtils.isNotEmpty(selectedTechnology.getJsLibraries())) {
	            getHttpRequest().setAttribute(REQ_PROJECT_INFO_JSLIBS,
	                    ApplicationsUtil.getMapFromModuleGroups(selectedTechnology.getJsLibraries()));
	        }

			// This attribute for Pilot Project combo box
			getHttpRequest().setAttribute(REQ_PILOTS_NAMES,
					ApplicationsUtil.getPilotNames(selectedTechnology.getId(), customerId));

			getHttpRequest().setAttribute(REQ_FROM_PAGE, fromPage);
			getHttpRequest().setAttribute(REQ_SELECTED_MENU, APPLICATIONS);
		} catch (Exception e) {
			throw new PhrescoException(e);
		}
	}

	public String fetchPilotProjectModules() throws PhrescoException {
		try {
			String techId = getHttpRequest().getParameter(REQ_TECHNOLOGY);
			pilotModules = new ArrayList<String>();
			pilotModules.addAll(ApplicationsUtil.getPilotModuleIds(techId, customerId));
			pilotModules.addAll(ApplicationsUtil.getPilotJsLibIds(techId, customerId));
		} catch (Exception e) {
			throw new PhrescoException(e);
		}
		
		return SUCCESS;
	}
	
	public String fetchDefaultModules() {
		String techId = getHttpRequest().getParameter(REQ_TECHNOLOGY);
		try {
			defaultModules = new ArrayList<String>();
			ProjectAdministrator administrator = PhrescoFrameworkFactory.getProjectAdministrator();
			Technology technology = administrator.getTechnology(techId);
			List<ModuleGroup> coreModules = (List<ModuleGroup>) administrator.getCoreModules(techId, customerId);
			if (CollectionUtils.isNotEmpty(coreModules) && coreModules != null) {
				for (ModuleGroup coreModule : coreModules) {
					List<Module> modules = coreModule.getVersions();
					if (CollectionUtils.isNotEmpty(modules)) {
						for (Module module : modules) {
							if (module.getRequired()) {
								defaultModules.add(module.getId());
							}
						}
					}
				}
			}
			List<ModuleGroup> customModules = (List<ModuleGroup>) administrator.getCustomModules(techId, customerId);
			if (CollectionUtils.isNotEmpty(customModules) && customModules != null) {
				for (ModuleGroup customModule : customModules) {
					List<Module> modules = customModule.getVersions();
					if (CollectionUtils.isNotEmpty(modules)) {
						for (Module module : modules) {
							if (module.getRequired()) {
								defaultModules.add(module.getId());
							}
						}
					}
				}
			}
			
			List<ModuleGroup> jsLibraries = technology.getJsLibraries();
			if (CollectionUtils.isNotEmpty(jsLibraries) && jsLibraries != null) {
				for (ModuleGroup jsLibrary : jsLibraries) {
					List<Module> jsLibs = jsLibrary.getVersions();
					if (CollectionUtils.isNotEmpty(jsLibs)) {
						for (Module jsLib : jsLibs) {
							if (jsLib.getRequired()) {
								defaultModules.add(jsLib.getId());
							}
						}
					}
				}
			}
		} catch (Exception e) {
			if (debugEnabled) {
				S_LOGGER.error("Entered into catch block of fetchDefaultModules()"
						+ FrameworkUtil.getStackTraceAsString(e));
			}
			new LogErrorReport(e, "Feature fetchDefaultModules");
			
			return LOG_ERROR;
		}
		
		return SUCCESS;
	}

	public String checkDependencyModules() {
		try {
			List<ModuleGroup> allModules = getAllModule();
			for (ModuleGroup module : allModules) {
				if (module.getId().equals(moduleId)) {
					Module checkedVersion = module.getVersion(version);
					if (StringUtils.isNotEmpty(preVersion)) {
						Module preVerModule = module.getVersion(preVersion);
						preDependentIds = ApplicationsUtil.getIds(preVerModule
								.getDependentModules());
						preDependentVersions = ApplicationsUtil
								.getDependentVersions();
					}
					if (checkedVersion != null) {
						dependentIds = ApplicationsUtil.getIds(checkedVersion
								.getDependentModules());
						dependentVersions = ApplicationsUtil
								.getDependentVersions();
					}
				}
			}

		} catch (PhrescoException e) {
			if (debugEnabled) {
				S_LOGGER.error("Entered into catch block of Features.checkDependency()"
						+ FrameworkUtil.getStackTraceAsString(e));
			}
			new LogErrorReport(e, "Feature Select Dependency");
			
			return LOG_ERROR;
		}
		
		return SUCCESS;
	}

	private List<ModuleGroup> getAllModule() throws PhrescoException {
		try {
			ProjectAdministrator administrator = PhrescoFrameworkFactory.getProjectAdministrator();
			Technology technology = administrator.getTechnology(techId);
			if (REQ_CORE_MODULE.equals(moduleType)) {
				return administrator.getCoreModules(techId, customerId);
			}
		
			if (REQ_CUSTOM_MODULE.equals(moduleType)) {
				return administrator.getCustomModules(techId, customerId);
			}
		
			if (REQ_JSLIB_MODULE.equals(moduleType)) {
				return technology.getJsLibraries();
			}
		} catch (Exception e) {
			throw new PhrescoException(e);
		}
		
		return null;
	}

	public Collection<String> getDependentIds() {
		return dependentIds;
	}

	public void setDependentIds(Collection<String> dependentIds) {
		this.dependentIds = dependentIds;
	}

	public Collection<String> getDependentVersions() {
		return dependentVersions;
	}

	public void setDependentVersions(Collection<String> dependentVersions) {
		this.dependentVersions = dependentVersions;
	}

	public Collection<String> getPreDependentIds() {
		return preDependentIds;
	}

	public void setPreDependentIds(Collection<String> dependentIds) {
		this.preDependentIds = dependentIds;
	}

	public Collection<String> getPreDependentVersions() {
		return preDependentVersions;
	}

	public void setPreDependentVersions(Collection<String> dependentVersions) {
		this.preDependentVersions = dependentVersions;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getPreVersion() {
		return preVersion;
	}

	public void setPreVersion(String preVersion) {
		this.preVersion = preVersion;
	}

	public String getModuleId() {
		return moduleId;
	}

	public void setModuleId(String moduleId) {
		this.moduleId = moduleId;
	}

	public String getModuleType() {
		return moduleType;
	}

	public void setModuleType(String moduleType) {
		this.moduleType = moduleType;
	}

	public String getTechId() {
		return techId;
	}

	public void setTechId(String techId) {
		this.techId = techId;
	}

	public String getProjectCode() {
		return projectCode;
	}

	public void setProjectCode(String projectCode) {
		this.projectCode = projectCode;
	}

	public String getFromPage() {
		return fromPage;
	}

	public void setFromPage(String fromPage) {
		this.fromPage = fromPage;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getApplication() {
		return application;
	}

	public void setApplication(String application) {
		this.application = application;
	}

	public String getTechnology() {
		return technology;
	}

	public void setTechnology(String technology) {
		this.technology = technology;
	}

	public String getNameError() {
		return nameError;
	}

	public void setNameError(String nameError) {
		this.nameError = nameError;
	}

	public List<String> getPilotModules() {
		return pilotModules;
	}

	public void setPilotModules(List<String> pilotModules) {
		this.pilotModules = pilotModules;
	}

	public List<String> getPilotJSLibs() {
		return pilotJSLibs;
	}

	public void setPilotJSLibs(List<String> pilotJSLibs) {
		this.pilotJSLibs = pilotJSLibs;
	}

	public List<String> getTechVersion() {
		return techVersion;
	}

	public void setTechVersion(List<String> techVersion) {
		this.techVersion = techVersion;
	}

	public String getConfigServerNames() {
		return configServerNames;
	}

	public void setConfigServerNames(String configServerNames) {
		this.configServerNames = configServerNames;
	}

	public String getConfigDbNames() {
		return configDbNames;
	}

	public void setConfigDbNames(String configDbNames) {
		this.configDbNames = configDbNames;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getArtifactId() {
		return artifactId;
	}

	public void setArtifactId(String artifactId) {
		this.artifactId = artifactId;
	}

	public String getExternalCode() {
		return externalCode;
	}

	public void setExternalCode(String externalCode) {
		this.externalCode = externalCode;
	}
	
	public String getProjectVersion() {
		return projectVersion;
	}

	public void setProjectVersion(String projectVersion) {
		this.projectVersion = projectVersion;
	}
	
	public String getFromTab() {
		return fromTab;
	}

	public void setFromTab(String fromTab) {
		this.fromTab = fromTab;
	}
	
	public List<String> getDefaultModules() {
		return defaultModules;
	}

	public void setDefaultModules(List<String> defaultModules) {
		this.defaultModules = defaultModules;
	}
	
	public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }
    
    public boolean isValidated() {
		return validated;
	}

	public void setValidated(boolean validated) {
		this.validated = validated;
	}
}