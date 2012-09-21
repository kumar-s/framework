/*
 * ###
 * Phresco Framework Implementation
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
package com.photon.phresco.framework.impl;

import hudson.cli.CLI;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Logger;
import org.jdom.JDOMException;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.photon.phresco.commons.FrameworkConstants;
import com.photon.phresco.exception.PhrescoException;
import com.photon.phresco.framework.PhrescoFrameworkFactory;
import com.photon.phresco.framework.api.CIManager;
import com.photon.phresco.framework.api.ProjectAdministrator;
import com.photon.phresco.framework.model.BuildInfo;
import com.photon.phresco.framework.model.CIBuild;
import com.photon.phresco.framework.model.CIJob;
import com.photon.phresco.framework.model.CIJobStatus;
import com.photon.phresco.service.client.api.ServiceManager;
import com.photon.phresco.util.Utility;
import com.sun.jersey.api.client.ClientResponse;

public class CIManagerImpl implements CIManager, FrameworkConstants {
    private static final Logger S_LOGGER = Logger.getLogger(CIManagerImpl.class);
	private static Boolean debugEnabled = S_LOGGER.isDebugEnabled();
    
    private CLI cli = null;

    @Override
    public CIJobStatus createJob(CIJob job, String customerId) throws PhrescoException {
    	if (debugEnabled) {
    		S_LOGGER.debug("Entering Method CIManagerImpl.createJob(CIJob job)");
    	}
    	
    	return doJob(job, FrameworkConstants.CI_CREATE_JOB_COMMAND, customerId);
    }
    
    @Override
    public CIJobStatus updateJob(CIJob job, String customerId) throws PhrescoException {
    	if (debugEnabled) {
    		S_LOGGER.debug("Entering Method CIManagerImpl.createJob(CIJob job)");
		}
    	
    	return doJob(job, FrameworkConstants.CI_UPDATE_JOB_COMMAND, customerId);
    }
    
    private CIJobStatus doJob(CIJob job, String jobType, String customerId) throws PhrescoException {
    	S_LOGGER.debug("Entering Method CIManagerImpl.createJob(CIJob job)");
    	
    	try {
            cli = getCLI(job);
            List<String> argList = new ArrayList<String>();
            argList.add(jobType);
            argList.add(job.getName());
            ServiceManager serviceManager = PhrescoFrameworkFactory.getProjectAdministrator().getServiceManager();
            String configPath = serviceManager.getCiConfigPath(job.getRepoType(), customerId);
            ConfigProcessor processor = new ConfigProcessor(new URL(configPath));
            customizeNodes(processor, job);
            
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            S_LOGGER.debug("argList " + argList.toString());
            int result = cli.execute(argList, processor.getConfigAsStream(), System.out, baos);
            
            String message = "Job created successfully";
            if (result == -1) { 
            	byte[] byteArray = baos.toByteArray();
            	message = new String(byteArray);
            }
            
            S_LOGGER.debug("message " + message);
            //when svn is selected credential value has to set
            if(SVN.equals(job.getRepoType())) {
            	setSvnCredential(job, customerId);
            }
            
            setMailCredential(job, customerId);
            return new CIJobStatus(result, message);
        } catch (IOException e) {
            throw new PhrescoException(e);
        } catch (JDOMException e) {
            throw new PhrescoException(e);
        } finally {
            if (cli != null) {
                try {
                    cli.close();
                } catch (IOException e) {
                	if (debugEnabled) {
                		S_LOGGER.error(e.getLocalizedMessage());
            		}
                } catch (InterruptedException e) {
                	if (debugEnabled) {
                		S_LOGGER.error(e.getLocalizedMessage());
            		}
                }
            }
        }
    }
    
    private void setSvnCredential(CIJob job, String customerId) throws JDOMException, IOException {
       	S_LOGGER.debug("Entering Method CIManagerImpl.setSvnCredential");
       	
        try {
        	ServiceManager serviceManager = PhrescoFrameworkFactory.getProjectAdministrator().getServiceManager();
        	InputStream credentialXml = serviceManager.getCredentialXml(customerId);
            SvnProcessor processor = new SvnProcessor(credentialXml);
			DataInputStream in = new DataInputStream(credentialXml);
			while (in.available() != 0) {
				System.out.println(in.readLine());
			}

			in.close();
            processor.changeNodeValue("credentials/entry//userName", job.getUserName());
            processor.changeNodeValue("credentials/entry//password", job.getPassword());
            
            processor.writeStream(new File(Utility.getJenkinsHome() + File.separator + job.getName()));
            
            //jenkins home location
			String jenkinsJobHome = System.getenv(JENKINS_HOME);
            StringBuilder builder = new StringBuilder(jenkinsJobHome);
            builder.append(File.separator);
            
            processor.writeStream(new File(builder.toString() + CI_CREDENTIAL_XML));
		} catch (Exception e) {
       		S_LOGGER.error("Entered into the catch block of CIManagerImpl.setSvnCredential " + e.getLocalizedMessage());
		}
    }
    
    @Override
    public void setMailCredential(CIJob job, String customerId) {
       	S_LOGGER.debug("Entering Method CIManagerImpl.setMailCredential");
       	
        try {
        	ServiceManager serviceManager = PhrescoFrameworkFactory.getProjectAdministrator().getServiceManager();
        	InputStream credentialXml = serviceManager.getMailerXml(customerId);
            SvnProcessor processor = new SvnProcessor(credentialXml);
			DataInputStream in = new DataInputStream(credentialXml);
			while (in.available() != 0) {
				System.out.println(in.readLine());
			}

			in.close();
			// Mail have to with jenkins running email address
			InetAddress ownIP = InetAddress.getLocalHost();
			processor.changeNodeValue(CI_HUDSONURL, HTTP_PROTOCOL + PROTOCOL_POSTFIX + ownIP.getHostAddress() + COLON + job.getJenkinsPort() + FORWARD_SLASH + CI + FORWARD_SLASH);
            processor.changeNodeValue("smtpAuthUsername", job.getSenderEmailId());
            processor.changeNodeValue("smtpAuthPassword", job.getSenderEmailPassword());
            processor.changeNodeValue("adminAddress", job.getSenderEmailId());
            
            //jenkins home location
			String jenkinsJobHome = System.getenv(JENKINS_HOME);
            StringBuilder builder = new StringBuilder(jenkinsJobHome);
            builder.append(File.separator);
            
            processor.writeStream(new File(builder.toString() + CI_MAILER_XML));
		} catch (Exception e) {
       		S_LOGGER.error("Entered into the catch block of CIManagerImpl.setMailCredential " + e.getLocalizedMessage());
		}
    }
    
    @Override
    public void getJdkHomeXml(String customerId) throws PhrescoException {
       	S_LOGGER.debug("Entering Method CIManagerImpl.getJdkHomeXml");
       	
        try {
        	ServiceManager serviceManager = PhrescoFrameworkFactory.getProjectAdministrator().getServiceManager();
        	InputStream JDKHomeXmlStream = serviceManager.getJdkHomeXml(customerId);
			String jenkinsJobHome = System.getenv(JENKINS_HOME);
            StringBuilder builder = new StringBuilder(jenkinsJobHome);
            builder.append(File.separator);
            
			File f = new File(builder.toString() + CI_JDK_HOME_XML);
			streamToFile(f, JDKHomeXmlStream);
		} catch (Exception e) {
       		S_LOGGER.error("Entered into the catch block of CIManagerImpl.getJdkHomeXml" + e.getLocalizedMessage());
		}
    }
    
    private void streamToFile(File dest, InputStream ip) {
		OutputStream out;
		try {
			out = new FileOutputStream(dest);
			byte buf[] = new byte[5024];
			int len;
			while ((len = ip.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			out.close();
			ip.close();
		} catch (Exception e) {
       		S_LOGGER.error("Entered into the catch block of CIManagerImpl.streamToFile" + e.getLocalizedMessage());
		}
    }
    
    @Override
    public void getMavenHomeXml(String customerId) throws PhrescoException {
        if (debugEnabled) {
        	S_LOGGER.debug("Entering Method CIManagerImpl.getMavenHomeXml");
        }

        try {
        	ServiceManager serviceManager = PhrescoFrameworkFactory.getProjectAdministrator().getServiceManager();
        	InputStream MAVENHomeXmlStream = serviceManager.getMavenHomeXml(customerId);
            String jenkinsJobHome = System.getenv(JENKINS_HOME);
            StringBuilder builder = new StringBuilder(jenkinsJobHome);
            builder.append(File.separator);
            File f = new File(builder.toString() + CI_MAVEN_HOME_XML);
            streamToFile(f, MAVENHomeXmlStream);
		} catch (Exception e) {
        	if (debugEnabled) {
        		S_LOGGER.error("Entered into the catch block of CIManagerImpl.getMavenHomeXml" + e.getLocalizedMessage());
    		}
		}
    }
    
    @Override
    public CIJobStatus buildJob(CIJob job) throws PhrescoException {
   		S_LOGGER.debug("Entering Method CIManagerImpl.buildJob(CIJob job)");
    	cli = getCLI(job);
        
        List<String> argList = new ArrayList<String>();
        argList.add(FrameworkConstants.CI_BUILD_JOB_COMMAND);
        argList.add(job.getName());
        try {
            int status = cli.execute(argList);
            String message = FrameworkConstants.CI_BUILD_STARTED;
            if (status == FrameworkConstants.JOB_STATUS_NOTOK) {
                message = FrameworkConstants.CI_BUILD_STARTING_ERROR;
            }
            return new CIJobStatus(status, message);
        } finally {
            if (cli != null) {
                try {
                    cli.close();
                } catch (IOException e) {
                	if (debugEnabled) {
                		S_LOGGER.error(e.getLocalizedMessage());
            		}
                } catch (InterruptedException e) {
                	if (debugEnabled) {
                		S_LOGGER.error(e.getLocalizedMessage());
            		}
                }
            }
        }
    }
    
    private JsonArray getBuildsArray(CIJob job) throws PhrescoException {
    	try {
        	String jenkinsUrl = "http://" + job.getJenkinsUrl() + ":" + job.getJenkinsPort() + "/ci/";
        	String jobNameUtf8 = job.getName().replace(" ", "%20");
        	String buildsJsonUrl = jenkinsUrl + "job/" + jobNameUtf8 + "/api/json";
            String jsonResponse = getJsonResponse(buildsJsonUrl);
            
            JsonParser parser = new JsonParser();
            JsonElement jsonElement = parser.parse(jsonResponse);
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            JsonElement element = jsonObject.get(FrameworkConstants.CI_JOB_JSON_BUILDS);
            
            JsonArray jsonArray = element.getAsJsonArray();
            
            return jsonArray;
		} catch (Exception e) {
			throw new PhrescoException(e);

		}
    }
    
    @Override
    public List<CIBuild> getCIBuilds(CIJob job) throws PhrescoException {
    	S_LOGGER.debug("Entering Method CIManagerImpl.getCIBuilds(CIJob job)");
    	List<CIBuild> ciBuilds = null;
        try{
        	S_LOGGER.debug("getCIBuilds()  JobName = "+ job.getName());
            JsonArray jsonArray = getBuildsArray(job);
            ciBuilds = new ArrayList<CIBuild>(jsonArray.size());
            Gson gson = new Gson();
            CIBuild ciBuild = null;
            for (int i = 0; i < jsonArray.size(); i++) {
                ciBuild = gson.fromJson(jsonArray.get(i), CIBuild.class);
                setBuildStatus(ciBuild, job);
        		String buildUrl = ciBuild.getUrl();
        		String jenkinUrl = job.getJenkinsUrl() + ":" + job.getJenkinsPort();
        		buildUrl = buildUrl.replaceAll("localhost:" + job.getJenkinsPort(), jenkinUrl); // when displaying url it should display setup machine ip
        		ciBuild.setUrl(buildUrl);
                ciBuilds.add(ciBuild);
            }
        } catch(Exception e) {
        	S_LOGGER.debug("Entering Method CIManagerImpl.getCIBuilds(CIJob job)");
        }
        return ciBuilds;
    }
    
    private void setBuildStatus(CIBuild ciBuild, CIJob job) throws PhrescoException {
   		S_LOGGER.debug("Entering Method CIManagerImpl.setBuildStatus(CIBuild ciBuild)");
   		S_LOGGER.debug("setBuildStatus()  url = "+ ciBuild.getUrl());
		String buildUrl = ciBuild.getUrl();
		String jenkinsUrl = job.getJenkinsUrl() + ":" + job.getJenkinsPort();
		buildUrl = buildUrl.replaceAll("localhost:" + job.getJenkinsPort(), jenkinsUrl); // display the jenkins running url in ci list
    	String response = getJsonResponse(buildUrl + "api/json");
        JsonParser parser = new JsonParser();
        JsonElement jsonElement = parser.parse(response);
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        
        JsonElement resultJson = jsonObject.get(FrameworkConstants.CI_JOB_BUILD_RESULT);
        JsonElement idJson = jsonObject.get(FrameworkConstants.CI_JOB_BUILD_ID);
        JsonElement timeJson = jsonObject.get(FrameworkConstants.CI_JOB_BUILD_TIME_STAMP);
        JsonArray asJsonArray = jsonObject.getAsJsonArray(FrameworkConstants.CI_JOB_BUILD_ARTIFACTS);
        
        if(jsonObject.get(FrameworkConstants.CI_JOB_BUILD_RESULT).toString().equals("null")) { // when build is result is not known
        	ciBuild.setStatus("INPROGRESS");
        } else if(resultJson.getAsString().equals("SUCCESS") && asJsonArray.size() < 1) { // when build is success and zip relative path is not added in json
            ciBuild.setStatus("INPROGRESS");
        } else {
        	ciBuild.setStatus(resultJson.getAsString());
        	//download path
        	for (JsonElement jsonArtElement : asJsonArray) {
        		String buildDownloadZip = jsonArtElement.getAsJsonObject().get(FrameworkConstants.CI_JOB_BUILD_DOWNLOAD_PATH).toString();
        		if (buildDownloadZip.endsWith(FrameworkConstants.CI_ZIP)) {
        			S_LOGGER.debug("download artifact " + buildDownloadZip);
        			ciBuild.setDownload(buildDownloadZip);
        		}
    		}
        }

        ciBuild.setId(idJson.getAsString());
        String dispFormat = "dd/MM/yyyy hh:mm:ss";
        ciBuild.setTimeStamp(getDate(timeJson.getAsString(), dispFormat));
    }

    private String getDate(String timeStampStr, String format) {
        DateFormat formatter = new SimpleDateFormat(format);
        long timeStamp = Long.parseLong(timeStampStr);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeStamp);
        return formatter.format(calendar.getTime());
    }
    
    private String getJsonResponse(String jsonUrl) throws PhrescoException {
   		S_LOGGER.debug("Entering Method CIManagerImpl.getJsonResponse(String jsonUrl)");
   		S_LOGGER.debug("getJsonResponse() JSonUrl = "+jsonUrl);
   		try {
	        HttpClient httpClient = new DefaultHttpClient();
	        HttpGet httpget = new HttpGet(jsonUrl);
	        ResponseHandler<String> responseHandler = new BasicResponseHandler();
            return httpClient.execute(httpget, responseHandler);
        } catch (IOException e) {
            throw new PhrescoException(e);
        }
    }
    
    private CLI getCLI(CIJob job) throws PhrescoException {
   		S_LOGGER.debug("Entering Method CIManagerImpl.getCLI()");
   		
        String jenkinsUrl = "http://" + job.getJenkinsUrl() + ":" + job.getJenkinsPort() + "/ci/";
        try {
            return new CLI(new URL(jenkinsUrl));
        } catch (MalformedURLException e) {
            throw new PhrescoException(e);
        } catch (IOException e) {
            throw new PhrescoException(e);
        } catch (InterruptedException e) {
            throw new PhrescoException(e);
        }
    }
    
    private void customizeNodes(ConfigProcessor processor, CIJob job) throws JDOMException, PhrescoException {
        //SVN url customization
    	if (SVN.equals(job.getRepoType())) {
            processor.changeNodeValue("scm/locations//remote", job.getSvnUrl());
    	} else {
    		processor.changeNodeValue("scm/userRemoteConfigs//url", job.getSvnUrl());
    		processor.changeNodeValue("scm/branches//name", job.getBranch());
    	}
        
        //Schedule expression customization
        processor.changeNodeValue("triggers//spec", job.getScheduleExpression());
        
        //Triggers Implementation
        List<String> triggers = job.getTriggers();
        
        processor.createTriggers("triggers", triggers, job.getScheduleExpression());
        
        //Maven command customization
        processor.changeNodeValue("goals", job.getMvnCommand());
        
        //Recipients customization
        Map<String, String> email = job.getEmail();
        
        //Failure Reception list
        processor.changeNodeValue("publishers//hudson.plugins.emailext.ExtendedEmailPublisher//configuredTriggers//hudson.plugins.emailext.plugins.trigger.FailureTrigger//email//recipientList", (String)email.get("failureEmailssuccessEmails"));
        
        //Success Reception list
        processor.changeNodeValue("publishers//hudson.plugins.emailext.ExtendedEmailPublisher//configuredTriggers//hudson.plugins.emailext.plugins.trigger.SuccessTrigger//email//recipientList", (String)email.get("successEmails"));
        
        //enable collabnet file release plugin integration
        if (job.isEnableBuildRelease()) {
        	processor.enableCollabNetBuildReleasePlugin(job);
        }
    }
    
    public static void main(String[] args)  {
//    	try {
//    		CIManagerImpl managerImpl = new CIManagerImpl();
//            managerImpl.validate();
//    	} catch(Exception e) {
//    		e.printStackTrace();
//    	}
    }

    private void validate() throws IOException, InterruptedException {
   		S_LOGGER.debug("Entering Method CIManagerImpl.validate()");
        CLI validateCLI = new CLI(new URL("http://localhost:3579/ci/"));
        
        List<String> argList = new ArrayList<String>();
        argList.add("build");
        argList.add("NewJob");
        
        Gson gson = new Gson();
        JsonParser parser = new JsonParser();
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        
        byte[] data = new byte[4];
        ByteArrayInputStream inputStream = new ByteArrayInputStream(data);
        
        BufferedInputStream stream = new BufferedInputStream(System.in);
        
        int execute = validateCLI.execute(argList, inputStream, baos, baos);
        validateCLI.close();
        
        
        byte[] outData = new byte[2];
        
        int read;
        while ((read = inputStream.read(outData)) != -1) {
            System.out.print(new String(outData));
        }
        inputStream.close();
//        byte[] byteArray = baos.toByteArray();
//        baos.flush();
//        baos.close();
    }
    
    @Override
    public int getTotalBuilds(CIJob job) throws PhrescoException {
		S_LOGGER.debug("Entering Method CIManagerImpl.getTotalBuilds(CIJob job)");
		S_LOGGER.debug("getCIBuilds()  JobName = " + job.getName());
        JsonArray jsonArray = getBuildsArray(job);
        Gson gson = new Gson();
        CIBuild ciBuild = null;
        if(jsonArray.size() > 0) {
            ciBuild = gson.fromJson(jsonArray.get(0), CIBuild.class);
    		String buildUrl = ciBuild.getUrl();
    		String jenkinsUrl = job.getJenkinsUrl() + ":" + job.getJenkinsPort();
    		buildUrl = buildUrl.replaceAll("localhost:" + job.getJenkinsPort(), jenkinsUrl); // display the jenkins running url in ci list
        	String response = getJsonResponse(buildUrl + "api/json");
            JsonParser parser = new JsonParser();
            JsonElement jsonElement = parser.parse(response);
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            JsonElement resultJson = jsonObject.get(FrameworkConstants.CI_JOB_BUILD_RESULT);
            JsonArray asJsonArray = jsonObject.getAsJsonArray(FrameworkConstants.CI_JOB_BUILD_ARTIFACTS);
            if(jsonObject.get(FrameworkConstants.CI_JOB_BUILD_RESULT).toString().equals("null")) { // when build result is not known
            	return -1; // it indicates the job is in progress and not yet completed
            } else if(resultJson.getAsString().equals("SUCCESS") && asJsonArray.size() < 1) { // when build is success and build zip relative path is unknown
                return -1;
            } else {
            	return jsonArray.size();
            }
        } else {
        	return -1;	// When the project is build first time,
        }
    }
    
    @Override
    public CIJobStatus deleteCI(CIJob job, List<String> builds) throws PhrescoException {
   		S_LOGGER.debug("Entering Method CIManagerImpl.deleteCI(CIJob job)");
   		S_LOGGER.debug("Job name " + job.getName());
    	cli = getCLI(job);
        String deleteType = null;
        List<String> argList = new ArrayList<String>();
        S_LOGGER.debug("job name " + job.getName());
        S_LOGGER.debug("Builds " + builds);
        if(CollectionUtils.isEmpty(builds)) {	// delete job
           	S_LOGGER.debug("Job deletion started");
       		S_LOGGER.debug("Command " + FrameworkConstants.CI_JOB_DELETE_COMMAND);
        	deleteType = "Job";
        	argList.add(FrameworkConstants.CI_JOB_DELETE_COMMAND);
            argList.add(job.getName());
        } else {								// delete Build
        	S_LOGGER.debug("Build deletion started");
        	deleteType = "Build";
        	argList.add(FrameworkConstants.CI_BUILD_DELETE_COMMAND);
            argList.add(job.getName());
    	    StringBuilder result = new StringBuilder();
    	    for(String string : builds) {
    	        result.append(string);
    	        result.append(",");
    	    }
    	    String buildNos = result.substring(0, result.length() - 1);
        	argList.add(buildNos);
       		S_LOGGER.debug("Command " + FrameworkConstants.CI_BUILD_DELETE_COMMAND);
       		S_LOGGER.debug("Build numbers " + buildNos);
        }
        try {
            int status = cli.execute(argList);
            String message = deleteType + " deletion started in jenkins";
            if (status == FrameworkConstants.JOB_STATUS_NOTOK) {
            	deleteType = deleteType.substring(0, 1).toLowerCase() + deleteType.substring(1);
                message = "Error while deleting " + deleteType +" in jenkins";
            }
            if (debugEnabled) {
            	S_LOGGER.debug("Delete CI Status " + status);
            	S_LOGGER.debug("Delete CI Message " + message);
    		}
            return new CIJobStatus(status, message);
        } finally {
            if (cli != null) {
                try {
                    cli.close();
                } catch (IOException e) {
                	if (debugEnabled) {
                		S_LOGGER.error("Entered into catch block of CIManagerImpl.deleteCI(CIJob job) " + e.getLocalizedMessage());
            		}
                } catch (InterruptedException e) {
                	if (debugEnabled) {
                		S_LOGGER.error("Entered into catch block of CIManagerImpl.deleteCI(CIJob job) " + e.getLocalizedMessage());
            		}
                }
            }
        }
    }
    
    @Override
    public int getProgressInBuild(CIJob job) throws PhrescoException {
   		S_LOGGER.debug("Entering Method CIManagerImpl.isBuilding(CIJob job)");
   		
    	String jenkinsUrl = "http://" + job.getJenkinsUrl() + ":" + job.getJenkinsPort() + "/ci/";
    	String isBuildingUrlUrl = "computer/api/xml?xpath=/computerSet/busyExecutors/text()";
        String jsonResponse = getJsonResponse(jenkinsUrl + isBuildingUrlUrl);
        int buidInProgress = Integer.parseInt(jsonResponse);
       	S_LOGGER.debug("buidInProgress " + buidInProgress);
    	return buidInProgress;
    }
    
    @Override
    public void getEmailExtPlugin(String customerId) throws PhrescoException {
        S_LOGGER.debug("Entering Method CIManagerImpl.getEmailExtPlugin");
        
        try {
        	ServiceManager serviceManager = PhrescoFrameworkFactory.getProjectAdministrator().getServiceManager();
        	ClientResponse response = serviceManager.getEmailExtPlugin(customerId);
   			S_LOGGER.debug("createProject response code " + response.getStatus());
            InputStream pluginStream = response.getEntityInputStream();
            String pluginDir = Utility.getJenkinsHomePluginDir();
            File pluginFile = new File(pluginDir + CI_MAIL_EXT_PLUGIN);
			streamToFile(pluginFile, pluginStream);
		} catch (Exception e) {
   			S_LOGGER.error("Entered into the catch block of CIManagerImpl.getEmailExtPlugin" + e.getLocalizedMessage());
		}
    }
    
    @Override
    public void deleteDoNotCheckin(CIJob job) throws PhrescoException {
        S_LOGGER.debug("Entering Method CIManagerImpl.deleteBuilds");
        
        try {
        	String jenkinsDataHome = System.getenv(JENKINS_HOME);
            StringBuilder builder = new StringBuilder(jenkinsDataHome);
            builder.append(File.separator);
            builder.append(WORKSPACE_DIR);
            builder.append(File.separator);
            builder.append(job.getName());
            builder.append(File.separator);
            builder.append(CHECKIN_DIR);
            builder.append(File.separator);
            builder.append(BUILD_PATH);
            deleteDir(new File(builder.toString()));
		} catch (Exception e) {
   			S_LOGGER.error("Entered into the catch block of CIManagerImpl.deleteBuilds" + e.getLocalizedMessage());
		}
    }
    
    @Override
    public List<BuildInfo> getBuildInfos(CIJob job) throws PhrescoException {
        S_LOGGER.debug("Entering Method CIManagerImpl.getBuildInfos");
        
        List<BuildInfo> buildInfo = null;
        try {
        	ProjectAdministrator administrator = PhrescoFrameworkFactory.getProjectAdministrator();
        	String jenkinsDataHome = System.getenv(JENKINS_HOME);
            StringBuilder builder = new StringBuilder(jenkinsDataHome);
            builder.append(File.separator);
            builder.append(WORKSPACE_DIR);
            builder.append(File.separator);
            builder.append(job.getName());
            builder.append(File.separator);
            builder.append(BUILD_DIR);
            builder.append(File.separator);
            builder.append(BUILD_INFO_FILE_NAME);
        	buildInfo = administrator.readBuildInfo(new File(builder.toString()));
		} catch (Exception e) {
   			S_LOGGER.error("Entered into the catch block of CIManagerImpl.getBuildInfos" + e.getLocalizedMessage());
		}
		
        return buildInfo;
    }
    
	@Override
	public BuildInfo getBuildInfo(CIJob job, int buildNumber)throws PhrescoException {
		List<BuildInfo> buildInfos = getBuildInfos(job);
		if (CollectionUtils.isEmpty(buildInfos)) {
			return null;
		}

		for (BuildInfo buildInfo : buildInfos) {
			if (buildInfo.getBuildNo() == buildNumber) {
				return buildInfo;
			}
		}

		return null;
	}

    private boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i=0; i<children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        // The directory is now empty so delete it
        return dir.delete();
    } 
}