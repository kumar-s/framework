/*
 * ###
 * Framework Web Archive
 * %%
 * Copyright (C) 1999 - 2012 Photon Infotech Inc.
 * %%
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
package com.photon.phresco.framework.actions.settings;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.opensymphony.xwork2.Action;
import com.photon.phresco.commons.model.DownloadInfo;
import com.photon.phresco.commons.model.PropertyTemplate;
import com.photon.phresco.commons.model.SettingsTemplate;
import com.photon.phresco.configuration.Environment;
import com.photon.phresco.exception.PhrescoException;
import com.photon.phresco.framework.PhrescoFrameworkFactory;
import com.photon.phresco.framework.actions.FrameworkBaseAction;
import com.photon.phresco.framework.api.Project;
import com.photon.phresco.framework.api.ProjectAdministrator;
import com.photon.phresco.framework.commons.DiagnoseUtil;
import com.photon.phresco.framework.commons.FrameworkUtil;
import com.photon.phresco.framework.commons.LogErrorReport;
import com.photon.phresco.framework.impl.EnvironmentComparator;
import com.photon.phresco.framework.model.CertificateInfo;
import com.photon.phresco.framework.model.PropertyInfo;
import com.photon.phresco.framework.model.SettingsInfo;
import com.photon.phresco.util.Constants;
import com.photon.phresco.util.Utility;

public class Settings extends FrameworkBaseAction {
	private static final long serialVersionUID = -1748241910381035152L;
	
	private Map<String, String> dbDriverMap = new HashMap<String, String>(8);

	private static final Logger S_LOGGER = Logger.getLogger(Settings.class);
	private static Boolean debugEnabled = S_LOGGER.isDebugEnabled();
	private String settingsName = null;
	private String description = null;
	private String settingsType = null;
	private String oldName = null;
	private String oldSettingType = null;
	private String[] appliesto = null;
	private String connectionAlive = "false";
	private String nameError = null;
	private String typeError = null;
	private String envError = null;
	private String dynamicError = null;
	private String portError = null;
	private String appliesToError = null;
	private boolean isValidated = false;
	private List<String> projectInfoVersions = null;
	private String remoteDeploymentChk = null;

	private String envName = null;
    private String emailError = null;
	
	Document dom;
    public Element rootEle;
    // Environemnt delete
    private boolean isEnvDeleteSuceess = true;
    private String envDeleteMsg = null;
    
    //For IIS server
    private String appName = "";
	private String nameOfSite = "";
    private String appNameError = null;
    private String siteNameError = null;
    
	public String list() {
		if (debugEnabled) {
			S_LOGGER.debug("Entering Method  Settings.list()");
		}
		try {
			ProjectAdministrator administrator = PhrescoFrameworkFactory.getProjectAdministrator();
			
			List<SettingsInfo> settings = administrator.getSettingsInfos();
			getHttpRequest().setAttribute(REQ_SETTINGS, settings);
			getHttpRequest().setAttribute(REQ_SELECTED_MENU, SETTINGS);
			ProjectAdministrator administratorEnvs = getProjectAdministrator();
			List<Environment> envs = administratorEnvs.getEnvironments();
            getHttpRequest().setAttribute(REQ_ENVIRONMENTS, envs);
		} catch (Exception e) {
        	if (debugEnabled) {
                S_LOGGER.error("Entered into catch block of Settings.list()"+ FrameworkUtil.getStackTraceAsString(e));
    		}
        	new LogErrorReport(e, "Settings list");
		}
		return SETTINGS_LIST;
	}

	public String add() {
		if (debugEnabled) {
			S_LOGGER.debug("Entering Method  Settings.add()");
		}
		try {
			ProjectAdministrator administrator = getProjectAdministrator();
			List<SettingsTemplate> settingsTemplates = administrator.getSettingsTemplates();
			getHttpRequest().setAttribute(SESSION_SETTINGS_TEMPLATES, settingsTemplates);
			getHttpRequest().setAttribute(REQ_SELECTED_MENU, SETTINGS);
			getHttpSession().removeAttribute(REQ_CONFIG_INFO);
			getHttpSession().removeAttribute(ERROR_SETTINGS);
			getHttpSession().removeAttribute(SETTINGS_PARAMS);
			
			List<Environment> envs = administrator.getEnvironments();
            getHttpRequest().setAttribute(REQ_ENVIRONMENTS, envs);
            Collections.sort(envs, new EnvironmentComparator());
//            getHttpRequest().setAttribute(REQ_ALL_TECHNOLOGIES, administrator.getAllTechnologies());//TODO:Need to handle
		} catch (PhrescoException e) {
        	if (debugEnabled) {
                S_LOGGER.error("Entered into catch block of Settings.list()"+ FrameworkUtil.getStackTraceAsString(e));
    		}
        	new LogErrorReport(e, "Settings add");
		}

		return SETTINGS_ADD;
	}

	public String save() {
		if (debugEnabled) {
			S_LOGGER.debug("Entering Method  Settings.save()");
		}

		try {
			initDriverMap();
			ProjectAdministrator administrator = PhrescoFrameworkFactory.getProjectAdministrator();
			List<PropertyInfo> propertyInfoList = new ArrayList<PropertyInfo>();
			String key = null;
            String value = null;
            if (REQ_CONFIG_TYPE_OTHER.equals(settingsType)) {
            	String[] keys = getHttpRequest().getParameterValues(REQ_CONFIG_PROP_KEY);
            	if (!ArrayUtils.isEmpty(keys)) {
            		for (String propertyKey : keys) {
						value = getHttpRequest().getParameter(propertyKey);
						propertyInfoList.add(new PropertyInfo(propertyKey, value));
					}
            	}
            } else {
            	SettingsTemplate selectedSettingTemplate = administrator.getSettingsTemplate(settingsType);
            	List<PropertyTemplate> propertyTemplates = selectedSettingTemplate.getProperties();
            	boolean isIISServer = false;
	            for (PropertyTemplate propertyTemplate : propertyTemplates) {
	            	if (propertyTemplate.getKey().equals(Constants.SETTINGS_TEMPLATE_SERVER) || propertyTemplate.getKey().equals(Constants.SETTINGS_TEMPLATE_DB)) {
	            		List<PropertyTemplate> compPropertyTemplates = propertyTemplate.getPropertyTemplates();
	            		for (PropertyTemplate compPropertyTemplate : compPropertyTemplates) {
	            			key = compPropertyTemplate.getKey();
	            			value = getHttpRequest().getParameter(key);
	            			if (propertyTemplate.getKey().equals(Constants.SETTINGS_TEMPLATE_DB) && key.equals("type")) {
	            				value = value.trim().toLowerCase();
	            				propertyInfoList.add(new PropertyInfo(key, value.trim()));
	            				String dbDriver = getDbDriver(value);
	            				propertyInfoList.add(new PropertyInfo(Constants.DB_DRIVER, dbDriver.trim()));
	            			} else {
	            				propertyInfoList.add(new PropertyInfo(key, value.trim()));
	            			}
	            			if ("type".equals(key) && "IIS".equals(value)) {
	                        	isIISServer = true;
	                        }
						}
	            	} else {
	            		key = propertyTemplate.getKey();
	            		value = getHttpRequest().getParameter(key);
	            		if(key.equals("remoteDeployment")){
                      		value = remoteDeploymentChk;
	                    }
	                    value = value.trim();
						if(key.equals(ADDITIONAL_CONTEXT_PATH)){
	                    	String addcontext = value;
	                    	if(!addcontext.startsWith("/")) {
	                    		value = "/" + value;
	                    	}
	                    }
						if ("certificate".equals(key)) {
							String env = getHttpRequest().getParameter(REQ_ENVIRONMENTS);
							if (StringUtils.isNotEmpty(value)) {
								File file = new File(value); 
								value = "certificates" + FILE_SEPARATOR + env + "-" + settingsName + ".crt";
								if (file.exists()) {
									File dstFile = new File(Utility.getProjectHome() + value);
									FrameworkUtil.copyFile(file, dstFile);
								} else {
									saveCertificateFile(value);
								}
							}
						}
	                    propertyInfoList.add(new PropertyInfo(propertyTemplate.getKey(), value));
	            	}
	                if (S_LOGGER.isDebugEnabled()) {
	                	S_LOGGER.debug("Configuration.save() key " + propertyTemplate.getKey() + "and Value is " + value);
	                }
	            }
	            if (isIISServer) {
	            	propertyInfoList.add(new PropertyInfo(SETTINGS_TEMP_KEY_APP_NAME, appName));
	                propertyInfoList.add(new PropertyInfo(SETTINGS_TEMP_KEY_SITE_NAME, nameOfSite));
                }
            }
			SettingsInfo settingsInfo = new SettingsInfo(settingsName, description, settingsType);
			settingsInfo.setAppliesTo(appliesto != null ? Arrays.asList(appliesto) : null);
			settingsInfo.setPropertyInfos(propertyInfoList);
            if (S_LOGGER.isDebugEnabled()) {
            	S_LOGGER.debug("Settings Info object value " + settingsInfo);
            }
            
            if (!validate(administrator, null)) {
            	isValidated = true;
				return Action.SUCCESS;
			}
            
            String[] selectedEnvs = getHttpRequest().getParameterValues(REQ_ENVIRONMENTS);
            StringBuilder environments = new StringBuilder();
            for (String envs : selectedEnvs) { 
                if (environments.length() > 0) environments.append(',');
                environments.append(envs);
            }
            
	        List<SettingsTemplate> settingsTemplates = administrator.getSettingsTemplates();
			administrator.createSetting(settingsInfo, environments.toString());
			if (SERVER.equals(settingsType)) {
				addActionMessage(getText(SUCCESS_SERVER, Collections.singletonList(settingsName)));
			} else if (DATABASE.equals(settingsType)) {
				addActionMessage(getText(SUCCESS_DATABASE, Collections.singletonList(settingsName)));
			} else if (WEBSERVICE.equals(settingsType)) {
				addActionMessage(getText(SUCCESS_WEBSERVICE, Collections.singletonList(settingsName)));
			} else if (EMAIL.equals(settingsType)) {
				addActionMessage(getText(SUCCESS_EMAIL, Collections.singletonList(settingsName)));
			} else {
				addActionMessage(getText(SUCCESS_SETTING_CREATE, Collections.singletonList(settingsName)));
			}
			getHttpRequest().setAttribute(SESSION_SETTINGS_TEMPLATES, settingsTemplates);
			getHttpRequest().setAttribute(REQ_CONFIG_INFO, settingsInfo);
			getHttpRequest().setAttribute(REQ_SELECTED_MENU, SETTINGS);
		} catch (Exception e) {
        	if (debugEnabled) {
                S_LOGGER.error("Entered into catch block of Settings.save()"+ FrameworkUtil.getStackTraceAsString(e));
    		}
        	new LogErrorReport(e, "Settings save");
		}
		return list();
	}
	
	  private String getDbDriver(String dbtype) {
			return dbDriverMap.get(dbtype);
		}
	    
	    private void initDriverMap() {
			dbDriverMap.put("mysql", "com.mysql.jdbc.Driver");
			dbDriverMap.put("oracle", "oracle.jdbc.OracleDriver");
			dbDriverMap.put("hsql", "org.hsql.jdbcDriver");
			dbDriverMap.put("mssql", "com.microsoft.sqlserver.jdbc.SQLServerDriver");
			dbDriverMap.put("db2", "com.ibm.db2.jcc.DB2Driver");
			dbDriverMap.put("mongodb", "com.mongodb.jdbc.MongoDriver");
		}
	
	private void saveCertificateFile(String path) throws PhrescoException {
    	try {
    		String host = (String)getHttpRequest().getParameter(SERVER_HOST);
			int port = Integer.parseInt(getHttpRequest().getParameter(SERVER_PORT));
			String certificateName = (String)getHttpRequest().getParameter("certificate");
			ProjectAdministrator administrator = PhrescoFrameworkFactory.getProjectAdministrator();
			List<CertificateInfo> certificates = administrator.getCertificate(host, port);
			if (CollectionUtils.isNotEmpty(certificates)) {
				for (CertificateInfo certificate : certificates) {
					if (certificate.getDisplayName().equals(certificateName)) {
						administrator.addCertificate(certificate, new File(Utility.getProjectHome() + path));
					}
				}
			}
		} catch (Exception e) {
			throw new PhrescoException(e);
		}
    }

	private boolean validate(ProjectAdministrator administrator, String fromPage)
			throws PhrescoException {
		if (debugEnabled) {
			S_LOGGER.debug("Entering Method  Settings.validate(ProjectAdministrator administrator, String fromPage)");
		}
		if (debugEnabled) {
			S_LOGGER.debug("validate() Frompage = "+fromPage);
		}
		
		String[] environments = getHttpRequest().getParameterValues(REQ_ENVIRONMENTS);
		boolean validate = true;
		
		if (StringUtils.isEmpty(settingsName.trim())) {
	   		setNameError(ERROR_NAME);
	   		validate = false;
	   	}
		
		if (ArrayUtils.isEmpty(environments)) {
	   		setEnvError(ERROR_ENV);
	   		return false;
	   	}
		
		
		if(StringUtils.isNotEmpty(settingsName) && !settingsName.equals(oldName)) {
			for (String environment : environments) {
				List<SettingsInfo> settingsInfos = administrator.getSettingsInfos(environment);
				for (SettingsInfo settingsInfo : settingsInfos) {
					if(settingsName.equalsIgnoreCase(settingsInfo.getName())) {
						setNameError(ERROR_DUPLICATE_NAME);
						validate = false;
					}
				}
			}
		}
		
		if (appliesto == null && !settingsType.equals("Browser")) {
	   		setAppliesToError("Applies to not selected");
	   		validate = false;
		}
		
		if (StringUtils.isEmpty(fromPage) || (StringUtils.isNotEmpty(fromPage) && !settingsType.equals(oldSettingType))) {
			if (settingsType.equals(Constants.SETTINGS_TEMPLATE_SERVER) || settingsType.equals(Constants.SETTINGS_TEMPLATE_EMAIL)) {
		        for (String env : environments) {
		        	if (appliesto != null) {
		                List<SettingsInfo> settingsinfos = administrator.getSettingsInfos(env, settingsType, Arrays.asList(appliesto), settingsName );
		                if(CollectionUtils.isNotEmpty( settingsinfos)) {
		                    setTypeError(SETTINGS_ALREADY_EXIST);
		                    validate = false;
		                }
		        	}
		        }
			}
		}
		
		if (!REQ_CONFIG_TYPE_OTHER.equals(settingsType)) {
			boolean serverTypeValidation = false;
			boolean isIISServer = false;
			SettingsTemplate selectedSettingTemplate = administrator.getSettingsTemplate(settingsType);
			for (PropertyTemplate propertyTemplate : selectedSettingTemplate.getProperties()) {
				String key = null;
				String value = null;
				boolean isRequired = propertyTemplate.isRequired();
	    		if (propertyTemplate.getKey().equals("Server") || propertyTemplate.getKey().equals("Database")) {
	        		List<PropertyTemplate> compPropertyTemplates = propertyTemplate.getPropertyTemplates();
	        		for (PropertyTemplate compPropertyTemplate : compPropertyTemplates) {
	        			key = compPropertyTemplate.getKey();
	        			value = getHttpRequest().getParameter(key);
	        			//If nodeJs server selected , there should not be validation for deploy dir.
	                    if ("type".equals(key) && "NodeJS".equals(value)) {
	                    	serverTypeValidation = true;
	                    }
	                    if ("type".equals(key) && "IIS".equals(value)) {
                        	isIISServer = true;
                        }
					}
	        	} else {
	        		key = propertyTemplate.getKey();
	        	}
	    		
				value = getHttpRequest().getParameter(key);
				
				if (serverTypeValidation && "deploy_dir".equals(key)) {
	            	isRequired = false;
	            }
				if (isIISServer && ("context".equals(key))) {
                	isRequired = false;
                }
				
	            // check Remotedeployment username and password mandatory
				boolean remoteDeplyVal = Boolean.parseBoolean(remoteDeploymentChk);
				if(remoteDeplyVal) {
					if ("admin_username".equals(key) || "admin_password".equals(key)) {
						isRequired = true;
					}
					if("deploy_dir".equals(key)) {
						isRequired = false;
					}
				}
				
				if (isRequired == true && StringUtils.isEmpty(value.trim())) {
					String field = propertyTemplate.getName();
					dynamicError += propertyTemplate.getKey() + ":" + field + " is empty" + ",";
				}
			}
			if(StringUtils.isNotEmpty(dynamicError)){
		        dynamicError = dynamicError.substring(0, dynamicError.length() - 1);
		        dynamicError = dynamicError.substring(4);
		        setDynamicError(dynamicError);
		        validate = false;
		   	}
			
			if (isIISServer) {
            	if (StringUtils.isEmpty(appName)) {
            		setAppNameError("App Name is missing");
            		validate = false;
            	}
            	if (StringUtils.isEmpty(nameOfSite)) {
            		setSiteNameError("Site Name is missing");
            		validate = false;
            	}
            }
			
			if (StringUtils.isNotEmpty(getHttpRequest().getParameter("port"))) {
			   	int value = Integer.parseInt(getHttpRequest().getParameter("port"));
			   	if (validate && (value < 1 || value > 65535)) {
				   	setPortError(ERROR_PORT);
				   	validate = false;
			   	}
		   	}
			
			if (StringUtils.isNotEmpty(getHttpRequest().getParameter("emailid"))) {
		   		String value = getHttpRequest().getParameter("emailid");
		   		Pattern p = Pattern.compile("^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
		   		Matcher m = p.matcher(value);
		   		boolean b = m.matches();
		   		if(!b) {
		   			setEmailError(ERROR_EMAIL);
		   			validate = false;
		   		}
			}
		}
	   	return validate;
	}
	
	
	public String edit() {
		if (debugEnabled) {
			S_LOGGER.debug("Entering Method  Settings.edit()");
		}
		try {
			ProjectAdministrator administrator = getProjectAdministrator();
			SettingsInfo settingsInfo = administrator.getSettingsInfo(oldName, getEnvName());
            getHttpSession().setAttribute(oldName, settingsInfo);
			getHttpRequest().setAttribute(SESSION_OLD_NAME, oldName);
            List<SettingsTemplate> settingsTemplates = administrator.getSettingsTemplates();
            getHttpRequest().setAttribute(SESSION_SETTINGS_TEMPLATES, settingsTemplates);
            List<Environment> environments = administrator.getEnvironments();
            getHttpRequest().setAttribute(REQ_ENVIRONMENTS, environments);
//            getHttpRequest().setAttribute(REQ_ALL_TECHNOLOGIES, administrator.getAllTechnologies());//TODO:Need to handle
			getHttpRequest().setAttribute(REQ_FROM_PAGE, FROM_PAGE_EDIT);
			getHttpRequest().setAttribute(REQ_SELECTED_MENU, SETTINGS);
			getHttpSession().removeAttribute(ERROR_SETTINGS);
			getHttpSession().removeAttribute(SETTINGS_PARAMS);
			getHttpRequest().setAttribute("currentEnv", envName);
		} catch (Exception e) {
        	if (debugEnabled) {
                S_LOGGER.error("Entered into catch block of Settings.edit()"+ FrameworkUtil.getStackTraceAsString(e));
    		}
        	new LogErrorReport(e, "Settings edit");
		}

		return SETTINGS_EDIT;
	}

	public String update() {
		S_LOGGER.debug("Entering Method  Settings.update()");

		try {
			initDriverMap();
			HttpServletRequest request = getHttpRequest();
			ProjectAdministrator administrator = PhrescoFrameworkFactory
					.getProjectAdministrator();
			List<PropertyInfo> propertyInfoList = new ArrayList<PropertyInfo>();
			SettingsInfo newSettingsInfo = new SettingsInfo(settingsName, description, settingsType);
			newSettingsInfo.setPropertyInfos(propertyInfoList);
			newSettingsInfo.setAppliesTo(appliesto != null ? Arrays.asList(appliesto) : null);
			String key = null;
            String value = null;
            if (REQ_CONFIG_TYPE_OTHER.equals(settingsType)) {
            	String[] keys = getHttpRequest().getParameterValues(REQ_CONFIG_PROP_KEY);
            	if (!ArrayUtils.isEmpty(keys)) {
            		for (String propertyKey : keys) {
						value = getHttpRequest().getParameter(propertyKey);
						propertyInfoList.add(new PropertyInfo(propertyKey, value));
					}
            	}
            } else {
            	SettingsTemplate selectedSettingTemplate = administrator.getSettingsTemplate(settingsType);
            	List<PropertyTemplate> propertyTemplates = selectedSettingTemplate.getProperties();
            	boolean isIISServer = false;
            	for (PropertyTemplate propertyTemplate : propertyTemplates) {
	            	if (propertyTemplate.getKey().equals(Constants.SETTINGS_TEMPLATE_SERVER) || propertyTemplate.getKey().equals(Constants.SETTINGS_TEMPLATE_DB)) {
	            		List<PropertyTemplate> compPropertyTemplates = propertyTemplate.getPropertyTemplates();
		            		for (PropertyTemplate compPropertyTemplate : compPropertyTemplates) {
		            			key = compPropertyTemplate.getKey();
		            			value = getHttpRequest().getParameter(key);
		            			if (propertyTemplate.getKey().equals(Constants.SETTINGS_TEMPLATE_DB) && key.equals("type")) {
		            				value = value.trim().toLowerCase();
		            				propertyInfoList.add(new PropertyInfo(key, value.trim()));
		            				String dbDriver = getDbDriver(value);
		            				propertyInfoList.add(new PropertyInfo(Constants.DB_DRIVER, dbDriver.trim()));
		            			} else {
		            				propertyInfoList.add(new PropertyInfo(key, value.trim()));
		            			}
		            			if ("type".equals(key) && "IIS".equals(value)) {
		                        	isIISServer = true;
		                        }
							}
	            	} else {
	            		key = propertyTemplate.getKey();
	            		value = getHttpRequest().getParameter(key);
	            		if(key.equals("remoteDeployment")){
            				value = remoteDeploymentChk;
	            		}
	            		if ("certificate".equals(key)) {
							String env = getHttpRequest().getParameter(REQ_ENVIRONMENTS);
							if (StringUtils.isNotEmpty(value)) {
								File file = new File(value); 
								value = "certificates" + FILE_SEPARATOR + env + "-" + settingsName + ".crt";
								if (file.exists()) {
									File dstFile = new File(Utility.getProjectHome() + value);
									FrameworkUtil.copyFile(file, dstFile);
								} else {
									saveCertificateFile(value);
								}
							}
						}
	                    value = value.trim();
	                    propertyInfoList.add(new PropertyInfo(propertyTemplate.getKey(), value));
	            	}
	                if (S_LOGGER.isDebugEnabled()) {
	                	S_LOGGER.debug("Configuration.save() key " + propertyTemplate.getKey() + "and Value is " + value);
	                }
	            }
            	if (isIISServer) {
	            	propertyInfoList.add(new PropertyInfo(SETTINGS_TEMP_KEY_APP_NAME, appName));
	                propertyInfoList.add(new PropertyInfo(SETTINGS_TEMP_KEY_SITE_NAME, nameOfSite));
                }
            }
			
			getHttpSession().setAttribute(oldName, newSettingsInfo);
			if (!validate(administrator, FROM_PAGE_EDIT)) {
				isValidated = true;
			    List<SettingsTemplate> settingsTemplates = administrator.getSettingsTemplates();
	            getHttpRequest().setAttribute(SESSION_SETTINGS_TEMPLATES, settingsTemplates);
				request.setAttribute(REQ_FROM_PAGE, FROM_PAGE_EDIT);
				request.setAttribute(REQ_OLD_NAME, oldName);
				return Action.SUCCESS;
			}
			getHttpRequest().setAttribute(REQ_SELECTED_MENU, SETTINGS);
			S_LOGGER.debug("Settings Info object value which going to be updated" + newSettingsInfo);
            String environment = getHttpRequest().getParameter(REQ_ENVIRONMENTS);
            administrator.updateSetting(environment, oldName, newSettingsInfo);
            if (SERVER.equals(settingsType)){
				addActionMessage(getText(SERVER_UPDATE_SUCCESS, Collections.singletonList(settingsName)));
			} else if (DATABASE.equals(settingsType)) {
				addActionMessage(getText(DATABASE_UPDATE_SUCCESS, Collections.singletonList(settingsName)));
			} else if (WEBSERVICE.equals(settingsType)) {
				addActionMessage(getText(WEBSERVICE_UPDATE_SUCCESS, Collections.singletonList(settingsName)));
			} else if (EMAIL.equals(settingsType)) {
				addActionMessage(getText(EMAIL_UPDATE_SUCCESS, Collections.singletonList(settingsName)));
			} else {
				addActionMessage(getText(SUCCESS_SETTING_UPDATE, Collections.singletonList(settingsName)));
			}
					
		} catch (Exception e) {
			S_LOGGER.error("Entered into catch block of Settings.update()"+ FrameworkUtil.getStackTraceAsString(e));
        	new LogErrorReport(e, "Settings update");
		}

		return list();
	}

	public String delete() {
		if (debugEnabled) {
			S_LOGGER.debug("Entering Method  Settings.delete()");
		}
		try {
			ProjectAdministrator administrator = getProjectAdministrator();
			HttpServletRequest request = getHttpRequest();
			List<String> settingNames = new ArrayList<String>();
			String[] selectedNames = request.getParameterValues(REQ_SELECTED_ITEMS);
			Map<String, List<String>> deleteConfigs = new HashMap<String , List<String>>();
			
			for(int i = 0; i < selectedNames.length; i++){
				String[] split = selectedNames[i].split(",");
				String envName = split[0];
				List<String> configNames = deleteConfigs.get(envName);
				if (configNames == null) {
					configNames = new ArrayList<String>(3);
				}
				configNames.add(split[1]);
				deleteConfigs.put(envName, configNames);
				}
			
			administrator.deleteSettingsInfos(deleteConfigs);
			for (String name : selectedNames) {
				if (debugEnabled) {
					S_LOGGER.debug("To be deleted settings name " + name);
				}
				settingNames.add(name);
			}
			addActionMessage(SUCCESS_SETTING_DELETE);
			getHttpRequest().setAttribute(REQ_SELECTED_MENU, SETTINGS);
		} catch (Exception e) {
        	if (debugEnabled) {
                S_LOGGER.error("Entered into catch block of Settings.delete()"+ FrameworkUtil.getStackTraceAsString(e));
    		}
        	new LogErrorReport(e, "Settings delete");
		}

		return list();
	}

	public String settingsType() {
		if (debugEnabled) {
			S_LOGGER.debug("Entering Method  Settings.settingsType()");
		}

		try {
			ProjectAdministrator administrator = getProjectAdministrator();
			SettingsInfo settingsInfo = (SettingsInfo) getHttpSession().getAttribute(REQ_CONFIG_INFO);
			if (settingsInfo == null && StringUtils.isNotEmpty(oldName)) {
			    settingsInfo = administrator.getSettingsInfo(oldName, getEnvName());
			    getHttpRequest().setAttribute(REQ_OLD_NAME, oldName);
			}
			SettingsTemplate settingsTemplate = administrator.getSettingsTemplate(settingsType);
			if (debugEnabled) {
				S_LOGGER.debug("Setting Template object value " + settingsTemplate.toString());
			}
			if(SERVER.equals(settingsType)) {
			    //TODO:Need to handle
//				List<DownloadInfo> servers = administrator.getServers();
//				getHttpRequest().setAttribute(REQ_TEST_SERVERS, servers);
			}
			
			if(Constants.SETTINGS_TEMPLATE_DB.equals(settingsType)) {
	            //TODO:Need to handle
//				List<DownloadInfo> databases = administrator.getDatabases();
//				getHttpRequest().setAttribute(REQ_PROJECT_INFO_DATABASES, databases);
			}
			getHttpRequest().setAttribute(REQ_CURRENT_SETTINGS_TEMPLATE, settingsTemplate);
//			getHttpRequest().setAttribute(REQ_ALL_TECHNOLOGIES, administrator.getAlsslTechnologies());//TODO:Need to handle
		} catch (Exception e) {
        	if (debugEnabled) {
                S_LOGGER.error("Entered into catch block of Settings.settingsType()"+ FrameworkUtil.getStackTraceAsString(e));
    		}
        	new LogErrorReport(e, "Settings type");
		}
		return SETTINGS_TYPE;
	}

	public String connectionAliveCheck() {
		if (debugEnabled) {
			S_LOGGER.debug("Entering Method  Settings.connectionAliveCheck()");
		}
		try {
			connectionAlive = "false";
			HttpServletRequest request = getHttpRequest();
			String url = request.getParameter(REQ_SERVER_STATUS_URL);
			String[] results = url.split(",");
			String lprotocol = results[0];
			String lhost = results[1];
			int lport = Integer.parseInt(results[2]);
			boolean tempConnectionAlive = DiagnoseUtil.isConnectionAlive(lprotocol, lhost, lport);
			connectionAlive = tempConnectionAlive == true ? "true" : "false";
		} catch (Exception e) {
        	if (debugEnabled) {
                S_LOGGER.error("Entered into catch block of Settings.connectionAliveCheck()" + FrameworkUtil.getStackTraceAsString(e));
    		}
			addActionError(e.getLocalizedMessage());
			e.printStackTrace();
		}
		return SUCCESS;
	}
	
	public String openSettingsEnvPopup() {
	    
        try {
            ProjectAdministrator administrator = PhrescoFrameworkFactory.getProjectAdministrator();
            getHttpRequest().setAttribute(SETTINGS_FROM_TAB, getHttpRequest().getParameter(SETTINGS_FROM_TAB));
            List<Environment> envs = administrator.getEnvironments();
            getHttpRequest().setAttribute(REQ_ENVIRONMENTS, envs);
        } catch (PhrescoException e) {
            if (debugEnabled) {
                S_LOGGER.error("Entered into catch block of Configurations.createEnvironment()" + FrameworkUtil.getStackTraceAsString(e));
            }
            new LogErrorReport(e, "open popup Settings"); 
        }
		
    	return APP_ENVIRONMENT;
    }
	
	public String createSettingsEnvironment() {
    	try {
    	    String[] split = null;
    	    String envs = getHttpRequest().getParameter("envs");
    	    String selectedItems = getHttpRequest().getParameter("deletableEnvs");
            if(StringUtils.isNotEmpty(selectedItems)){
    	    	deleteSettingsEnvironment(selectedItems);
    	    }

            List<Environment> environments = new ArrayList<Environment>();
            if(StringUtils.isNotEmpty(envs)) {
                List<String> listSelectedEnvs = new ArrayList<String>(Arrays.asList(envs.split("#SEP#")));
                for (String listSelectedEnv : listSelectedEnvs) {
                    try {  
                          split = listSelectedEnv.split("#DSEP#");
                          environments.add(new Environment(split[0], split[1], false));
                    } catch(ArrayIndexOutOfBoundsException e){
                           environments.add(new Environment(split[0], "", false));
                    }
                }
            }
	    	ProjectAdministrator administrator = PhrescoFrameworkFactory.getProjectAdministrator();
	    	administrator.createEnvironments(environments);
	    	if(StringUtils.isNotEmpty(selectedItems) && CollectionUtils.isNotEmpty(environments)) {
	    		addActionMessage(getText(UPDATE_ENVIRONMENT));
	    	} else if(StringUtils.isNotEmpty(selectedItems) && CollectionUtils.isEmpty(environments)){
	    		addActionMessage(getText(DELETE_ENVIRONMENT));
	    	} else if(CollectionUtils.isNotEmpty(environments) && StringUtils.isEmpty(selectedItems)) {
	    		addActionMessage(getText(CREATE_SUCCESS_ENVIRONMENT));
		    }
	    	
    	} catch(PhrescoException e) {
    		if (debugEnabled) {
                S_LOGGER.error("Entered into catch block of Configurations.createEnvironment()" + FrameworkUtil.getStackTraceAsString(e));
     		}
    		addActionMessage(getText(CREATE_FAILURE_ENVIRONMENT));
    	}
    	return list();
    }
	
	
	public String deleteSettingsEnvironment(String selectedItems) {
    	try {
    		ProjectAdministrator administrator = PhrescoFrameworkFactory.getProjectAdministrator();
	    	List<String> envNames = Arrays.asList(selectedItems.split(","));
	    	List<String> deletableEnvs = new ArrayList<String>();
	    	for (String envName : envNames) {
				// Check if configurations are already exist
				List<SettingsInfo> configurations = administrator.configurationsByEnvName(envName);
                if (CollectionUtils.isEmpty(configurations)) {
                	deletableEnvs.add(envName);
                }
			}
	    	if (isEnvDeleteSuceess == true) {
	    		// Delete Environment
	    		administrator.deleteEnvironments(deletableEnvs);
	    	}
    	} catch(Exception e) {
    		if (debugEnabled) {
                S_LOGGER.error("Entered into catch block of Configurations.deleteEnvironment()" + FrameworkUtil.getStackTraceAsString(e));
     		}
    	}
    	return SUCCESS;
    }
	
	public String checkForRemoveSettings(){
		try {
    		ProjectAdministrator administrator = PhrescoFrameworkFactory.getProjectAdministrator();
    		String removeItems = getHttpRequest().getParameter(DELETABLE_ENVS);
	    	List<String> envs = Arrays.asList(removeItems.split(","));
	    	List<String> unDeletableEnvs = new ArrayList<String>();
	    	for (String env : envs) {
				// Check if configurations are already exist
				List<SettingsInfo> configurations = administrator.configurationsByEnvName(env);
                if (CollectionUtils.isNotEmpty(configurations)) {
                	unDeletableEnvs.add(env);
                	if(unDeletableEnvs.size() > 1){
                		setEnvError(getText(ERROR_ENVS_REMOVE, Collections.singletonList(unDeletableEnvs)));
                	} else {
                		setEnvError(getText(ERROR_ENV_REMOVE, Collections.singletonList(unDeletableEnvs)));
                	}
                }
			}
    	} catch(Exception e) {
    		if (debugEnabled) {
                S_LOGGER.error("Entered into catch block of Configurations.checkForRemoveSettings()" + FrameworkUtil.getStackTraceAsString(e));
     		}
    	}
		return SUCCESS;
	}
	
	public String checkDuplicateEnvSettings() {
	    try {
	        String envs = getHttpRequest().getParameter(ENVIRONMENT_VALUES);
	        ProjectAdministrator administrator = PhrescoFrameworkFactory.getProjectAdministrator();
	        List<Project> projects = administrator.discover(Collections.singletonList(new File(Utility.getProjectHome())));
	        for (Project project : projects) {
	            Collection<String> envNames = administrator.getEnvNames(project);
	            for (String envName : envNames) {
	            	if (envName.equalsIgnoreCase(envs)) {
		                setEnvError(getText(ERROR_DUPLICATE_NAME_IN_CONFIGURATIONS, Collections.singletonList(project.getApplicationInfo().getName())));
		            }
				}
	        }
	        Collection<String> envNames = administrator.getEnvNames();
	        for (String envName : envNames) {
	        	if (envName.equalsIgnoreCase(envs)) {
	                setEnvError(getText(ERROR_DUPLICATE_NAME_IN_SETTINGS));
	            }	
			}
	        
	    } catch(PhrescoException e) {
	        if (debugEnabled) {
	            S_LOGGER.error("Entered into catch block of Configurations.createEnvironment()" + FrameworkUtil.getStackTraceAsString(e));
	        }
	        new LogErrorReport(e, "create environment");
	    }
	    return SUCCESS;
	}
	
	public String fetchSettingProjectInfoVersions() {
	    //TODO:Need to handle this
    	/*try {
	    	String settingType = getHttpRequest().getParameter("settingsType");
	    	String name = getHttpRequest().getParameter("name");
	    	ProjectAdministrator administrator = PhrescoFrameworkFactory.getProjectAdministrator();
	    	//Technology technology = project.getProjectInfo().getTechnology();
	    	if ("Server".equals(settingType)) {
	    		List<DownloadInfo> servers = administrator.getServers();
	    		if (CollectionUtils.isNotEmpty(servers)) {
	    			for (DownloadInfo server : servers) {
						if (server.getName().equals(name)) {
							setProjectInfoVersions(server.getVersions());
						}
					}
	    		}
	    	}
	    	if ("Database".equals(settingType)) {
	    		List<DownloadInfo> databases = administrator.getDatabases();
	    		if (CollectionUtils.isNotEmpty(databases)) {
	    			for (DownloadInfo database : databases) {
						if (database.getName().equals(name)) {
							setProjectInfoVersions(database.getVersions());
						}
					}
	    		}
	    	}
    	} catch (Exception e) {
    		
    	}*/
    	return SUCCESS;
    }

	public String getSettingsName() {
		return settingsName;
	}

	public void setSettingsName(String settingsName) {
		this.settingsName = settingsName;
	}
	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getSettingsType() {
		return settingsType;
	}

	public void setSettingsType(String settingsType) {
		this.settingsType = settingsType;
	}

	public String getOldName() {
		return oldName;
	}

	public void setOldName(String oldName) {
		this.oldName = oldName;
	}

	public String[] getAppliesto() {
		return appliesto;
	}

	public void setAppliesto(String[] appliesto) {
		this.appliesto = appliesto;
	}

	public String getConnectionAlive() {
		return connectionAlive;
	}
	
	public void setConnectionAlive(String connectionAlive) {
		this.connectionAlive = connectionAlive;
	}
	
	public String getNameError() {
		return nameError;
	}

	public void setNameError(String nameError) {
		this.nameError = nameError;
	}
	
	public String getTypeError() {
        return typeError;
    }

    public void setTypeError(String typeError) {
        this.typeError = typeError;
    }
    
	
	public String getDynamicError() {
		return dynamicError;
	}

	public void setDynamicError(String dynamicError) {
		this.dynamicError = dynamicError;
	}
	
	public String getAppliesToError() {
		return appliesToError;
	}

	public void setAppliesToError(String appliesToError) {
		this.appliesToError = appliesToError;
	}
	public boolean isValidated() {
		return isValidated;
	}

	public void setValidated(boolean isValidated) {
		this.isValidated = isValidated;
	}
	
	public String getEnvName() {
	    return envName;
	}

	public void setEnvName(String envName) {
	    this.envName = envName;
	}

	public String getEnvError() {
		return envError;
	}

	public void setEnvError(String envError) {
		this.envError = envError;
	}

	public boolean isEnvDeleteSuceess() {
		return isEnvDeleteSuceess;
	}

	public void setEnvDeleteSuceess(boolean isEnvDeleteSuceess) {
		this.isEnvDeleteSuceess = isEnvDeleteSuceess;
	}

	public String getEnvDeleteMsg() {
		return envDeleteMsg;
	}

	public void setEnvDeleteMsg(String envDeleteMsg) {
		this.envDeleteMsg = envDeleteMsg;
	}
	
	public String getOldSettingType() {
		return oldSettingType;
	}

	public void setOldSettingType(String oldSettingType) {
		this.oldSettingType = oldSettingType;
	}
	
	public List<String> getProjectInfoVersions() {
		return projectInfoVersions;
	}

	public void setProjectInfoVersions(List<String> projectInfoVersions) {
		this.projectInfoVersions = projectInfoVersions;
	}
	
	
	public String getPortError() {
		return portError;
	}
	
	public void setPortError(String portError) {
		this.portError = portError;
	}
	
	public String getRemoteDeploymentChk() {
		return remoteDeploymentChk;
	}

	public void setRemoteDeploymentChk(String remoteDeploymentChk) {
		this.remoteDeploymentChk = remoteDeploymentChk;
	}
	
	public String getEmailError() {
		return emailError;
	}

	public void setEmailError(String emailError) {
		this.emailError = emailError;
	}
	
	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public String getAppNameError() {
		return appNameError;
	}

	public void setAppNameError(String appNameError) {
		this.appNameError = appNameError;
	}

	public String getSiteNameError() {
		return siteNameError;
	}

	public void setSiteNameError(String siteNameError) {
		this.siteNameError = siteNameError;
	}

	public String getNameOfSite() {
		return nameOfSite;
	}

	public void setNameOfSite(String nameOfSite) {
		this.nameOfSite = nameOfSite;
	}
}