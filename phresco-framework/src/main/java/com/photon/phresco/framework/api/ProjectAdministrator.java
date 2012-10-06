/*
 * ###
 * Phresco Framework
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
package com.photon.phresco.framework.api;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.photon.phresco.commons.model.ApplicationInfo;
import com.photon.phresco.commons.model.ApplicationType;
import com.photon.phresco.commons.model.ArtifactGroup;
import com.photon.phresco.commons.model.DownloadInfo;
import com.photon.phresco.commons.model.LogInfo;
import com.photon.phresco.commons.model.SettingsTemplate;
import com.photon.phresco.commons.model.Technology;
import com.photon.phresco.commons.model.User;
import com.photon.phresco.commons.model.VideoInfo;
import com.photon.phresco.commons.model.VideoType;
import com.photon.phresco.configuration.Environment;
import com.photon.phresco.exception.PhrescoException;
import com.photon.phresco.framework.model.BuildInfo;
import com.photon.phresco.framework.model.CIBuild;
import com.photon.phresco.framework.model.CIJob;
import com.photon.phresco.framework.model.CIJobStatus;
import com.photon.phresco.framework.model.CertificateInfo;
import com.photon.phresco.framework.model.SettingsInfo;
import com.photon.phresco.service.client.api.ServiceManager;
import com.photon.phresco.util.Credentials;
import com.phresco.pom.site.ReportCategories;
import com.phresco.pom.site.Reports;

public interface ProjectAdministrator {

    ServiceManager getServiceManager();
    
    /**
     * Create a project with the given project information
     * and the project will be downloaded in the given path
     * @param info
     * @param path
     * @return
     * @throws PhrescoException
     */
    Project createProject(ApplicationInfo info, File path) throws PhrescoException;

    /**
     * Update the project with the given project information
     * and the project will be download to the given path
     * @param delta
     * @param appInfo
     * @param path
     * @return
     * @throws PhrescoException
     * 
     */
    Project updateProject(ApplicationInfo delta, ApplicationInfo appInfo, File path, User userInfo) throws PhrescoException;

    /**
     * Returns the project for the specified project code
     * @param projectCode
     * @return
     * @throws PhrescoException
     */
    Project getProject(String projectCode) throws PhrescoException;
    
    /**
     * Returns the project for the specified project code
     * @param baseDir
     * @return
     * @throws PhrescoException
     */
    Project getProjectByWorkspace(File baseDir) throws PhrescoException;

    /**
     * Returns all application types
     * @return
     * @throws PhrescoException
     */
    List<ApplicationType> getApplicationTypes(String customerId) throws PhrescoException;

    /**
     * Returns Application type object for the given name
     * @param name of the application type [Web, Mobile or Webservice]
     * @return Application Type
     * @throws PhrescoException
     */
    ApplicationType getApplicationType(String appTyeId, String customerId) throws PhrescoException;

    /**
     * Returns all application types
     * @return
     * @throws PhrescoException
     */
    List<Technology> getAppTypeTechnologies(String appTyeId, String customerId) throws PhrescoException;

    /**
     * Deletes list of projects for the given project codes
     * @param projectCodes
     * @throws PhrescoException
     */
    void deleteProject(List<String> projectCodes) throws PhrescoException;

    /**
     * Returns list of project for the given paths
     * @param paths
     * @return
     * @throws PhrescoException
     */
    List<Project> discover(List<File> paths) throws PhrescoException;

    /**
     * Returns all the settings template available in the service repository
     * @return
     * @throws PhrescoException
     */
    List<SettingsTemplate> getSettingsTemplates() throws PhrescoException;

    /**
     * Returns the settings template which matched to the given type
     * @param type
     * @return
     * @throws PhrescoException
     */
    SettingsTemplate getSettingsTemplate(String type) throws PhrescoException;

    /**
     * Creates settings.info object under the projects location
     * @param info
     * @param envName
     * @throws PhrescoException
     */
    void createSetting(SettingsInfo info, String envName) throws PhrescoException;

    /**
     * Update the existing settings info with latest information
     * @param envName
     * @param oldConfigName
     * @param info
     * @throws PhrescoException
     */
    void updateSetting(String envName, String oldConfigName, SettingsInfo info) throws PhrescoException;

    /**
     * Returns the setting information based on the given name defined in the settings.info file
     * @param name
     * @param envName
     * @return
     * @throws PhrescoException
     */
    SettingsInfo getSettingsInfo(String name, String envName) throws PhrescoException;

    /**
     * Returns all the setting informations defined in the settings.xml file
     * @return
     * @throws PhrescoException
     */
    List<SettingsInfo> getSettingsInfos() throws PhrescoException;
    
    /**
     * Returns all the setting information for the given environment name
     * @param envName
     * @return
     * @throws PhrescoException
     */
    List<SettingsInfo> getSettingsInfos(String envName) throws PhrescoException;


    /**
     * Returns list of setting informations based on the technology id and type specified for each setting
     * For example, Server, Database or Email related settings will be returned
     * @param type, technologyId
     * @param projectCode
     * @param envName environment Name
     * @return list of SettingsInfo object
     * @throws PhrescoException
     */
    List<SettingsInfo> getSettingsInfos(String type, String projectCode, String envName) throws PhrescoException;
    

    /**
     * Returns list of setting informations based on the type selected
     * @param envName environment Name
     * @param type settings type
     * @param appliesTo technologyId
     * @return list of SettingsInfo object
     * @throws PhrescoException
     */
    List<SettingsInfo> getSettingsInfos(String envName, String type, List<String> appliesTo, String settingsName) throws PhrescoException;

    /**
     * Returns the settings information based on the specified name, type and project code
     * @param name
     * @param type
     * @param projectCode
     * @param envName
     * @return
     * @throws PhrescoException
     */
    SettingsInfo getSettingsInfo(String name, String type, String projectCode, String envName) throws PhrescoException;
    
    

    /**
     * Deletes all the settings information for the given names
     * @param names
     * @param envName
     * @throws PhrescoException
     */
    void deleteSettingsInfos(Map<String, List<String>> selectedConfigs) throws PhrescoException;

    /**
     * Returns list of configurations for the given project
     * @param project
     * @return
     * @throws PhrescoException
     */
    List<SettingsInfo> configurations(Project project) throws PhrescoException;
   
    /**
     * Returns list of configurations of specify type for the given project
     * @param project
     * @param envName
     * @param type
     * @return
     * @throws PhrescoException
     */
    List<SettingsInfo> configurations(Project project, String envName, String type, String configName) throws PhrescoException;

    /**
     * Returns configuration information for the given name and project
     * @param name
     * @param envName
     * @param project
     * @return
     * @throws PhrescoException
     */
    SettingsInfo configuration(String name, String envName, Project project) throws PhrescoException;

    /**
     * Returns list of configurations for the specified type and project
     * @param type
     * @param project
     * @return
     * @throws PhrescoException
     */
    List<SettingsInfo> configurations(String type, Project project) throws PhrescoException;

    /**
     * Create configuration information for the given project
     * @param info
     * @param envName
     * @param project
     * @throws PhrescoException
     */
    void createConfiguration(SettingsInfo info, String envName, Project project) throws PhrescoException;

    /**
     * Update configuration information for the given project
     * @param envName
     * @param oldName
     * @param info
     * @param project
     * @throws PhrescoException
     */
    void updateConfiguration(String envName, String oldConfigName, SettingsInfo info, Project project) throws PhrescoException;

    /**
     * Deletes list of configurations for the given names under the mentioned project
     * @param names
     * @param project
     * @throws PhrescoException
     */
    void deleteConfigurations(Map<String, List<String>> selectedConfigs, Project project) throws PhrescoException;

    /**
     * TODO: DO we need this?
     * @param path
     * @return
     * @throws IOException
     */
    List<SettingsInfo> readSettingsInfo(File path) throws IOException;

    /**
     * Returns all the setting informations defined in the build.info file
     * @return
     * @throws PhrescoException
     */
    List<BuildInfo> getBuildInfos(Project project) throws PhrescoException;

    /**
     * Returns all the build info details from build.info file
     * @return
     * @throws PhrescoException
     */
    List<BuildInfo> readBuildInfo(File path) throws IOException;
    
    BuildInfo getBuildInfo(Project project, int buildNumber) throws PhrescoException;

    List<BuildInfo> getBuildInfos(Project project, int[] buildNumbers) throws PhrescoException;

    void deleteBuildInfos(Project project, int[] buildNumbers) throws PhrescoException;

    List<VideoInfo> getVideoInfos() throws PhrescoException;

    /**
     * Returns all the videos for the given Thumbnail image name
     * @return
     * @throws PhrescoException
     */
    List<VideoType> getVideoTypes(String name) throws PhrescoException;

    /**
     * Returns the core modules specified under the given technology
     * @param technology
     * @return list of core modules
     */
    List<ArtifactGroup> getCoreModules(Technology technology);

    /**
     * Returns the custom modules specified under the given technology
     * @param technology
     * @return list of custom modules
     */
    List<ArtifactGroup> getCustomModules(Technology technology);

    /**
     * Authenticates the user credentials and returns the user information
     * @param credentials
     * @return
     * @throws PhrescoException
     */
    User doLogin(Credentials credentials) throws PhrescoException;

    /**
     * Returns Server DownloadInfo from the service
     * @return
     * @throws PhrescoException
     */
    List<DownloadInfo> getServerDownloadInfo() throws PhrescoException;

    /**
     * Returns Database DownloadInfo from the service
     * @return
     * @throws PhrescoException
     */
    List<DownloadInfo> getDbDownloadInfo() throws PhrescoException;

    /**
     * Returns editor DownloadInfo from the service
     * @return
     * @throws PhrescoException
     */
    List<DownloadInfo> getEditorDownloadInfo() throws PhrescoException;
    
    /**
     * Returns tools DownloadInfo from the service
     * @return
     * @throws PhrescoException
     */
    List<DownloadInfo> getToolsDownloadInfo() throws PhrescoException;
    
    /**
     * Returns others DownloadInfo from the service
     * @return
     * @throws PhrescoException
     */
    List<DownloadInfo> getOtherDownloadInfo() throws PhrescoException;
    
    /**
     * Returns the jForum path from the service
     * @return
     * @throws PhrescoException
     */
    String getJforumPath() throws PhrescoException;

    void createJob(Project project, CIJob job) throws PhrescoException;

    void updateJob(Project project, CIJob job) throws PhrescoException;

    CIJob getJob(Project project) throws PhrescoException;

    /**
     * Returns the list of user created jobs
     * @return
     * @throws PhrescoException
     */
    List<CIJob> getJobs(Project project) throws PhrescoException;
    
    /**
     * Returns particular job object
     * @return
     * @throws PhrescoException
     */
    CIJob getJob(Project project, String jobName) throws PhrescoException;
    
    /**
     * Writes particular jobs object in .phresco folder
     * @return
     * @throws PhrescoException
     */
    void writeJsonJobs(Project project, List<CIJob> job, String status) throws PhrescoException;
    
    /**
     * Delete jobs object in .phresco folder
     * @return
     * @throws PhrescoException
     */
    void deleteJsonJobs(Project project, List<CIJob> job) throws PhrescoException;
    
    /**
     * Delete job object in .phresco folder
     * @return
     * @throws PhrescoException
     */
    void updateJsonJob(Project project, CIJob job) throws PhrescoException;
    
    /**
     * Returns build of jobs
     * @return
     * @throws PhrescoException
     */
    CIJobStatus buildJobs(Project project, List<String> jobs) throws PhrescoException;

    /**
     * Returns build of a particular job
     * @return
     * @throws PhrescoException
     */
    List<CIBuild> getBuilds(CIJob ciJob) throws PhrescoException;

	String sendReport(LogInfo loginfo) throws PhrescoException ;

	/**
	 * Validates the project and returns the result
	 * @param project
	 * @param action
	 * @return
	 * @throws PhrescoException
	 */
	List<ValidationResult> validate(Project project) throws PhrescoException;
	
    /**
     * Stores jdk home spcified xml file in jenkins home location
     * @throws PhrescoException
     */
	void getJdkHomeXml() throws PhrescoException;
	
    /**
     * Stores maven home spcified xml file in jenkins home location
     * @throws PhrescoException
     */
	void getMavenHomeXml() throws PhrescoException;
	

	List<ValidationResult> validate() throws PhrescoException;
	
    /**
     * Get total number of builds
     * @throws PhrescoException
     */
	int getTotalBuilds(Project project) throws PhrescoException;
	
    /**
     * Delete job. If build is null job ll be deleted
     * @throws PhrescoException
     */
	CIJobStatus deleteCIBuild(Project project, Map<String, List<String>> builds) throws PhrescoException;

    /**
     * Delete job. If build is null job ll be deleted
     * @throws PhrescoException
     */
	CIJobStatus deleteCIJobs(Project project, List<String> jobs) throws PhrescoException;
	
    /**
     * Checks whether job is in progress
     * @throws PhrescoException
     */
	int getProgressInBuild(Project project) throws PhrescoException;
	
    /**
     * Checks whether job is in progress
     * @throws PhrescoException
     */
	boolean isJobCreatingBuild(CIJob ciJob) throws PhrescoException;
	
    /**
     * gets exmail ext plugin from nexus and stores it in jenkins plugin dir
     * @throws PhrescoException
     */
	void getEmailExtPlugin() throws PhrescoException;
	
    /**
     * Delete existing builds in do_not_checkin folder
     * @throws PhrescoException
     */
	void deleteDoNotCheckin(Project project) throws PhrescoException;
	
	/**
	 * Create the environments in the configuration.xml file under the specified project
	 * @param project
	 * @param envNames
	 * @param isNewFile
	 * @throws PhrescoException
	 */
	void createEnvironments(Project project, List<Environment> envNames, boolean isNewFile) throws PhrescoException;
	
	/**
	 * Create the environments in the settings.xml
	 * @param envNames
	 * @throws PhrescoException
	 */
	void createEnvironments(List<Environment> envNames) throws PhrescoException;
	
	/**
	 * Delete the list of environments from the gobal settings
	 * @param envNames
	 * @throws PhrescoException
	 */
	void deleteEnvironments(List<String> envNames) throws PhrescoException;
	
	/**
	 * Delete the list of environments from the specified project
	 * @param envNames
	 * @param project
	 * @throws PhrescoException
	 */
	void deleteEnvironments(List<String> envNames, Project project) throws PhrescoException;
	
	/**
	* Returns the environment
	* @param project
	* @throws PhrescoException
	*/

	/**
	 * Get the default environment from the service
	 * @return
	 * @throws PhrescoException
	 */
	List<Environment> getEnvFromService() throws PhrescoException;
	
	/**
	 * Get the environments from the settings.xml
	 * @return
	 * @throws PhrescoException
	 */
	List<Environment> getEnvironments() throws PhrescoException;
	
	/**
	 * Get the environments from the configurtaion.xml by the specified project 
	 * @param project
	 * @return
	 * @throws PhrescoException
	 */
	List<Environment> getEnvironments(Project project) throws PhrescoException;
	
	/**
	 * Get the environment names from the settings.xml
	 * @return
	 * @throws PhrescoException
	 */
	Collection<String> getEnvNames() throws PhrescoException;
	
	/**
	 * Get the environment names from the configurtaion.xml by the specified project 
	 * @param project
	 * @return
	 * @throws PhrescoException
	 */
	Collection<String> getEnvNames(Project project) throws PhrescoException;

	/**
	 * Copy the environments from the configuration.xml by the specified project and write those to the specified file 
	 * @param project
	 * @param selectedEnvs
	 * @param resultConfigXml
	 * @throws PhrescoException
	 */
	
	
	/**
	 * add server details for functional test configuration.xml file
	 * @param resolution 
	 */
	void updateTestConfiguration(Project project, String selectedEnvs, String browser, String resultConfigXml, String resolution) throws PhrescoException;
	
	/**
	 * Get the default environment from the specified project
	 * @param projectCode
	 * @return
	 */
	String getDefaultEnvName(String projectCode);
	
	/**
	 * get configurations by environment name
	 * @param envName
	 * @param project
	 * @return
	 * @throws PhrescoException
	 */
	List<SettingsInfo> configurationsByEnvName(String envName, Project project) throws PhrescoException;
	
	/**
	 * get configurations by environment name
	 * @param envName
	 * @return
	 * @throws PhrescoException
	 */
	List<SettingsInfo> configurationsByEnvName(String envName) throws PhrescoException;
	
	/**
	 * setAsDefaultEnvironment
	 * @param envName
	 * @return
	 * @throws PhrescoException
	 */
	void setAsDefaultEnv(String env, Project project) throws PhrescoException;
	
	//TODO: Remove the below method once the plugins are adapted for configuration.xml
	SettingsInfo getSettingsInfo(String name, String type, String projectCode) throws PhrescoException;
	
	/**
	 * get servers from service
	 * @param 
	 * @return
	 * @throws PhrescoException
	 */
	List<DownloadInfo> getServers() throws PhrescoException;
	
	List<DownloadInfo> getDatabases() throws PhrescoException;
	
	/**
	 * Delete the sql Folder
	 * @param dbList
	 * @return 
	 * @throws PhrescoException
	 */
	 void deleteSqlFolder(List<String> dbList, ApplicationInfo appInfo) throws PhrescoException;

	 List<Reports> getReports(ApplicationInfo appInfo) throws PhrescoException;
	 
	 List<Reports> getPomReports(ApplicationInfo appInfo) throws PhrescoException;
	 
	 void updateRptPluginInPOM(ApplicationInfo appInfo, List<Reports> reports, List<ReportCategories> reportCategories) throws PhrescoException;
	 
	/**
	 * Returns CI build object for build number
	 * @param job, buildNumber
	 * @return BuildInfo
	 * @throws PhrescoException
	 */
	 BuildInfo getCIBuildInfo(CIJob job, int buildNumber) throws PhrescoException;
	 
	 List<CertificateInfo> getCertificate(String host, int port) throws PhrescoException;
	 
	 void addCertificate(CertificateInfo info, File file) throws PhrescoException;
}
