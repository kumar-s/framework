/**
 * Framework Web Archive
 *
 * Copyright (C) 1999-2013 Photon Infotech Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.photon.phresco.framework.actions.applications;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.google.gson.Gson;
import com.photon.phresco.commons.FileListFilter;
import com.photon.phresco.commons.FrameworkConstants;
import com.photon.phresco.commons.model.ApplicationInfo;
import com.photon.phresco.commons.model.ProjectInfo;
import com.photon.phresco.exception.PhrescoException;
import com.photon.phresco.framework.FrameworkConfiguration;
import com.photon.phresco.framework.PhrescoFrameworkFactory;
import com.photon.phresco.framework.api.ActionType;
import com.photon.phresco.framework.api.ApplicationManager;
import com.photon.phresco.framework.commons.FrameworkUtil;
import com.photon.phresco.framework.commons.QualityUtil;
import com.photon.phresco.framework.model.DependantParameters;
import com.photon.phresco.framework.model.PerformanceDetails;
import com.photon.phresco.framework.model.PerformanceTestResult;
import com.photon.phresco.framework.model.SettingsInfo;
import com.photon.phresco.framework.model.TestCase;
import com.photon.phresco.framework.model.TestCaseError;
import com.photon.phresco.framework.model.TestCaseFailure;
import com.photon.phresco.framework.model.TestResult;
import com.photon.phresco.framework.model.TestSuite;
import com.photon.phresco.plugins.model.Mojos.Mojo.Configuration.Parameters.Parameter;
import com.photon.phresco.plugins.util.MojoProcessor;
import com.photon.phresco.service.client.impl.CacheKey;
import com.photon.phresco.service.client.impl.EhCacheManager;
import com.photon.phresco.util.Constants;
import com.photon.phresco.util.HubConfiguration;
import com.photon.phresco.util.NodeConfig;
import com.photon.phresco.util.NodeConfiguration;
import com.photon.phresco.util.Utility;
import com.phresco.pom.exception.PhrescoPomException;
import com.phresco.pom.model.Model.Modules;
import com.phresco.pom.util.PomProcessor;

public class Quality extends DynamicParameterAction implements Constants {

    private static final String IS_CLASS_EMPTY = "isClassEmpty";
	private static final long serialVersionUID = -2040671011555139339L;
    private static final Logger S_LOGGER = Logger.getLogger(Quality.class);
    private static Boolean s_debugEnabled  =S_LOGGER.isDebugEnabled();
    
    private List<SettingsInfo> serverSettings = null;
    private String testSuite = "";
    private String failures = "";
    private String errs = "";
    private String tests = "";
    private String setting = "";
    private String projectCode = "";
    private String testType = "";
    private String testResultFile = "";
	private String techReport = "";
    private String testModule = "";
	private String showError = "";
    private String hideLog = "";
    private String showDebug = "";
    private String jarLocation = "";
    private String testAgainst = "";
    private String resolution = ""; 
    private String filePath = "";
    private String type = "";
    private String testId = "";
    private String fromTab = "";
    private String actionType = "";
    
	private List<String> buildInfoEnvs = null;
    
	private String settingType = "";
    private String settingName = "";
    private String caption = "";
    private List<String> testResultFiles = new ArrayList<String>();
    private List<TestSuite> testSuites = null;
    private List<String> testSuiteNames = null;
    private boolean validated = false;
	private String testResultsType = "";
	private List<TestSuite> allTestSuite = null;
	private List<com.photon.phresco.commons.model.TestCase> allTestCases = null;
	
	//Below variables gets the value of performance test Url, Context and TestName
	private String resultJson = "";
	private PerformanceDetails performanceDetails = null;
    private String testName = "";
    
    private boolean resultFileAvailable = false;

    // android performance tag name
    private String testResultDeviceId = "";
    private Map<String, String> deviceNames = null;
    private String serialNumber = "";
    
    // iphone unit test
    private String sdk = "";
	private String target = "";
	private String fromPage = "";
	
    private String showGraphFor = "";
	
    // report generation 
    private String reportName = "";
    private String reoportLocation = "";
    private String reportDataType = "";
    private String sonarUrl = "";
    
    // download report
	private InputStream fileInputStream;
	private String fileName = "";
	private String reportFileName = "";
	
	//ios test type iosTestType
	private String iosTestType = "";
	private String testSuitName = "";
	
	private String projectModule = "";
	private String testScenarioName = "";
	private String totalSuccess = "";
	private String totalFailures = "";
	private String totalTestCases = "";
	private String testCoverage = "";
	
	private String featureId = "";
	private String testCaseId = "";
	private String testDescription = "";
	private String testSteps = "";
	private String expectedResult = "";
	private String actualResult = "";
	private String status = "";
	private String bugComment = "";
	private String testCaseIdError = "";
	private String featureIdError = "";
	private String nameError = "";
	private boolean errorFound;
	
    boolean connectionAlive = false;
    boolean updateCache;
    
    private String isFromCI = "";
	
	private static Map<String, Map<String, NodeList>> testSuiteMap = Collections.synchronizedMap(new HashMap<String, Map<String, NodeList>>(8));
	
	private EhCacheManager cacheManager = new EhCacheManager();
	
	public String unit() {
	    if (s_debugEnabled) {
	        S_LOGGER.debug("Entering Method Quality.unit()");
	    }
	    
	    try {
        	//To get ip of request machine
	    	String requestIp = getHttpRequest().getRemoteAddr();
			setReqAttribute(REQ_REQUEST_IP, requestIp);
			
	        ApplicationInfo appInfo = getApplicationInfo();
	        FrameworkUtil frameworkUtil = FrameworkUtil.getInstance();
	        setReqAttribute(PATH, frameworkUtil.getUnitTestDir(appInfo));
            setReqAttribute(REQ_APPINFO, appInfo);
            setProjModulesInReq();
            // get unit test report options
            setUnitReportOptions();
	    } catch (Exception e) {
            if (s_debugEnabled) {
                S_LOGGER.error("Entered into catch block of Quality.unit()" + FrameworkUtil.getStackTraceAsString(e));
            }
            return showErrorPopup(new PhrescoException(e), getText(EXCEPTION_QUALITY_UNIT_LOAD));
        }
	    
	    return APP_UNIT_TEST;
	}
	
	public String component() {
	    if (s_debugEnabled) {
	        S_LOGGER.debug("Entering Method Quality.component()");
	    }
	    
	    try {
	    	String requestIp = getHttpRequest().getRemoteAddr();
			setReqAttribute(REQ_REQUEST_IP, requestIp);
	        ApplicationInfo appInfo = getApplicationInfo();
	        FrameworkUtil frameworkUtil = FrameworkUtil.getInstance();
	        setReqAttribute(PATH, frameworkUtil.getComponentTestDir(appInfo));
            setReqAttribute(REQ_APPINFO, appInfo);
            setProjModulesInReq();
	    } catch (Exception e) {
            if (s_debugEnabled) {
                S_LOGGER.error("Entered into catch block of Quality.component()" + FrameworkUtil.getStackTraceAsString(e));
            }
            return showErrorPopup(new PhrescoException(e), getText(EXCEPTION_QUALITY_COMPONENT_LOAD));
        }
	    
	    return APP_COMPONENT_TEST;
	}
	
	private void setUnitReportOptions() throws PhrescoException {
        if (s_debugEnabled) {
            S_LOGGER.debug("Entering Method Quality.setUnitReportOptions");
        }
		try {
	        ApplicationInfo appInfo = getApplicationInfo();
	        FrameworkUtil frameworkUtil = FrameworkUtil.getInstance();
	        String unitTestReportOptions = frameworkUtil.getUnitTestReportOptions(appInfo);
	        if (StringUtils.isNotEmpty(unitTestReportOptions)) {
	        	List<String> asList = Arrays.asList(unitTestReportOptions.split(","));
	        	setReqAttribute(REQ_UNIT_TEST_REPORT_OPTIONS, asList);
	        }
		} catch (Exception e) {
            if (s_debugEnabled) {
                S_LOGGER.error("Entered into catch block of Quality.setUnitReportOptions()" + FrameworkUtil.getStackTraceAsString(e));
            }
			throw new PhrescoException(e);
		}
	}
	
	public String fetchUnitTestSuites() {
        if (s_debugEnabled) {
            S_LOGGER.debug("Entering Method Quality.fetchUnitTestSuites");
        }
        
        try {
            ApplicationInfo appInfo = getApplicationInfo();
            // TO kill the Process
            String baseDir = Utility.getProjectHome()+ appInfo.getAppDirName();
            Utility.killProcess(baseDir, getTestType());
            String testResultPath = getUnitTestResultPath(appInfo, null);
            FrameworkUtil frameworkUtil = FrameworkUtil.getInstance();
            String testSuitePath = "";
            if (StringUtils.isNotEmpty(getTechReport())) {
            	testSuitePath = frameworkUtil.getUnitTestSuitePath(appInfo, getTechReport());
		    } else {
		    	testSuitePath = frameworkUtil.getUnitTestSuitePath(appInfo);
		    }
            List<String> resultTestSuiteNames = getTestSuiteNames(testResultPath, testSuitePath);
            if (CollectionUtils.isEmpty(resultTestSuiteNames)) {
                setValidated(true);
                setShowError(getText(ERROR_UNIT_TEST));
                return SUCCESS;
            }
            setTestSuiteNames(resultTestSuiteNames);
        } catch (Exception e) {
            if (s_debugEnabled) {
                S_LOGGER.error("Entered into catch block of Quality.fetchUnitTestSuites()" + FrameworkUtil.getStackTraceAsString(e));
            }
            return showErrorPopup(new PhrescoException(e), getText(EXCEPTION_QUALITY_UNIT_TESTSUITES));
        }
        
        return SUCCESS;
    }
	
	public String fetchComponentTestSuites() {
        if (s_debugEnabled) {
            S_LOGGER.debug("Entering Method Quality.fetchComponentTestSuites()");
        }
        
        try {
            ApplicationInfo appInfo = getApplicationInfo();
            // TO kill the Process
            String baseDir = Utility.getProjectHome()+ appInfo.getAppDirName();
            Utility.killProcess(baseDir, getTestType());
            String testResultPath = getComponentTestResultPath(appInfo, null);
            FrameworkUtil frameworkUtil = FrameworkUtil.getInstance();
            String testSuitePath = frameworkUtil.getComponentTestSuitePath(appInfo);
            List<String> resultTestSuiteNames = getTestSuiteNames(testResultPath, testSuitePath);
            if (CollectionUtils.isEmpty(resultTestSuiteNames)) {
                setValidated(true);
                setShowError(getText(ERROR_COMPONENT_TEST));
                return SUCCESS;
            }
            setTestSuiteNames(resultTestSuiteNames);
        } catch (Exception e) {
            if (s_debugEnabled) {
                S_LOGGER.error("Entered into catch block of Quality.fetchComponentTestSuites()" + FrameworkUtil.getStackTraceAsString(e));
            }
            return showErrorPopup(new PhrescoException(e), getText(EXCEPTION_QUALITY_COMPONENT_TESTSUITES));
        }
        
        return SUCCESS;
    }
	
	private String getUnitTestResultPath(ApplicationInfo appInfo, String testResultFile) throws PhrescoException, JAXBException, IOException, PhrescoPomException {
        if (s_debugEnabled) {
            S_LOGGER.debug("Entering Method Quality.getUnitTestResultPath()");
        }
        
        StringBuilder sb = new StringBuilder(getApplicationHome());
        if (StringUtils.isNotEmpty(getProjectModule())) {
            sb.append(File.separatorChar);
            sb.append(getProjectModule());
        }
        // TODO Need to change this
        StringBuilder tempsb = new StringBuilder(sb);
        if (JAVASCRIPT.equals(getTechReport())) {
            tempsb.append(UNIT_TEST_QUNIT_REPORT_DIR);
            File file = new File(tempsb.toString());
            if (file.isDirectory() && file.list().length > 0) {
                sb.append(UNIT_TEST_QUNIT_REPORT_DIR);
            } else {
                sb.append(UNIT_TEST_JASMINE_REPORT_DIR);
            }
        } else {
            FrameworkUtil frameworkUtil = FrameworkUtil.getInstance();
            if (StringUtils.isNotEmpty(getTechReport())) {
            	sb.append(frameworkUtil.getUnitTestReportDir(appInfo, getTechReport()));
            } else {
            	sb.append(frameworkUtil.getUnitTestReportDir(appInfo));
            }
        }
        return sb.toString();
    }
	
	private String getComponentTestResultPath(ApplicationInfo appInfo, String testResultFile) throws PhrescoException, JAXBException, IOException, PhrescoPomException {
        if (s_debugEnabled) {
            S_LOGGER.debug("Entering Method Quality.getUnitTestResultPath()");
        }
        
        StringBuilder sb = new StringBuilder(getApplicationHome());
        FrameworkUtil frameworkUtil = FrameworkUtil.getInstance();
    	sb.append(frameworkUtil.getComponentTestReportDir(appInfo));
        return sb.toString();
    }
    
	public String showUnitTestPopUp() {
	    if (s_debugEnabled) {
            S_LOGGER.debug("Entering Method Quality.showUnitTestPopUp()");
        }
	    
	    try {
	    	ApplicationInfo appInfo = getApplicationInfo();
            removeSessionAttribute(appInfo.getId() + PHASE_UNIT_TEST + SESSION_WATCHER_MAP);
            Map<String, DependantParameters> watcherMap = new HashMap<String, DependantParameters>(8);

            MojoProcessor mojo = new MojoProcessor(new File(getPhrescoPluginInfoFilePath(PHASE_UNIT_TEST)));
            List<Parameter> parameters = getMojoParameters(mojo, PHASE_UNIT_TEST);

            setPossibleValuesInReq(mojo, appInfo, parameters, watcherMap, PHASE_UNIT_TEST);
            setSessionAttribute(appInfo.getId() + PHASE_UNIT_TEST + SESSION_WATCHER_MAP, watcherMap);
            setReqAttribute(REQ_DYNAMIC_PARAMETERS, parameters);
            setReqAttribute(REQ_GOAL, PHASE_UNIT_TEST);
            setReqAttribute(REQ_PHASE, PHASE_UNIT_TEST);
    	    setProjModulesInReq();
	    } catch (PhrescoException e) {
	        if (s_debugEnabled) {
                S_LOGGER.error("Entered into catch block of Quality.fetchUnitTestSuites()" + FrameworkUtil.getStackTraceAsString(e));
            }
            return showErrorPopup(new PhrescoException(e), getText(EXCEPTION_QUALITY_UNIT_PARAMS));
	    }
	    
	    return SUCCESS;
	}
	
	public String showComponentTestPopUp() {
	    if (s_debugEnabled) {
            S_LOGGER.debug("Entering Method Quality.showComponentTestPopUp()");
        }
	    
	    try {
	    	ApplicationInfo appInfo = getApplicationInfo();
            removeSessionAttribute(appInfo.getId() + PHASE_COMPONENT_TEST + SESSION_WATCHER_MAP);
            Map<String, DependantParameters> watcherMap = new HashMap<String, DependantParameters>(8);

            MojoProcessor mojo = new MojoProcessor(new File(getPhrescoPluginInfoFilePath(PHASE_COMPONENT_TEST)));
            List<Parameter> parameters = getMojoParameters(mojo, PHASE_COMPONENT_TEST);

            setPossibleValuesInReq(mojo, appInfo, parameters, watcherMap, PHASE_COMPONENT_TEST);
            setSessionAttribute(appInfo.getId() + PHASE_COMPONENT_TEST + SESSION_WATCHER_MAP, watcherMap);
            setReqAttribute(REQ_DYNAMIC_PARAMETERS, parameters);
            setReqAttribute(REQ_GOAL, PHASE_COMPONENT_TEST);
            setReqAttribute(REQ_PHASE, PHASE_COMPONENT_TEST);
    	    setProjModulesInReq();
	    } catch (PhrescoException e) {
	        if (s_debugEnabled) {
                S_LOGGER.error("Entered into catch block of Quality.showComponentTestPopUp()" + FrameworkUtil.getStackTraceAsString(e));
            }
            return showErrorPopup(new PhrescoException(e), getText(EXCEPTION_QUALITY_COMPONENT_PARAMS));
	    }
	    
	    return SUCCESS;
	}

    private void setProjModulesInReq() throws PhrescoException {
        List<String> projectModules = getProjectModules(getApplicationInfo().getAppDirName());
        setReqAttribute(REQ_PROJECT_MODULES, projectModules);
    }

	public String runUnitTest() {
        if (s_debugEnabled) {
            S_LOGGER.debug("Entering Method Quality.runUnitTest()");
        }
        
        try {
            ApplicationInfo appInfo = getApplicationInfo();
            StringBuilder workingDirectory = new StringBuilder(getAppDirectoryPath(appInfo));
            MojoProcessor mojo = new MojoProcessor(new File(getPhrescoPluginInfoFilePath(PHASE_UNIT_TEST)));
            persistValuesToXml(mojo, PHASE_UNIT_TEST);
            List<Parameter> parameters = getMojoParameters(mojo, PHASE_UNIT_TEST);
            List<String> buildArgCmds = getMavenArgCommands(parameters);
            buildArgCmds.add(HYPHEN_N);
            String pomFileName = Utility.getPomFileName(appInfo);
            if(!Constants.POM_NAME.equals(pomFileName)) {
				buildArgCmds.add(Constants.HYPHEN_F);
				buildArgCmds.add(pomFileName);
			}
            ApplicationManager applicationManager = PhrescoFrameworkFactory.getApplicationManager();
            BufferedReader reader = applicationManager.performAction(getProjectInfo(), ActionType.UNIT_TEST, buildArgCmds, workingDirectory.toString());
            setSessionAttribute(getAppId() + UNIT, reader);
            setReqAttribute(REQ_APP_ID, getAppId());
            setReqAttribute(REQ_ACTION_TYPE, UNIT);
        } catch (PhrescoException e) {
            if (s_debugEnabled) {
                S_LOGGER.error("Entered into catch block of Quality.runUnitTest()" + FrameworkUtil.getStackTraceAsString(e));
            }
            return showErrorPopup(e, getText(EXCEPTION_QUALITY_UNIT_RUN));
        }
        
        return APP_ENVIRONMENT_READER;
    }
	
	public String runComponentTest() {
        if (s_debugEnabled) {
            S_LOGGER.debug("Entering Method Quality.runComponentTest()");
        }
        
        try {
            ApplicationInfo appInfo = getApplicationInfo();
            StringBuilder workingDirectory = new StringBuilder(getAppDirectoryPath(appInfo));
            MojoProcessor mojo = new MojoProcessor(new File(getPhrescoPluginInfoFilePath(PHASE_COMPONENT_TEST)));
            persistValuesToXml(mojo, PHASE_COMPONENT_TEST);
            List<Parameter> parameters = getMojoParameters(mojo, PHASE_COMPONENT_TEST);
            List<String> buildArgCmds = getMavenArgCommands(parameters);
            buildArgCmds.add(HYPHEN_N);
            String pomFileName = Utility.getPomFileName(appInfo);
            if(!Constants.POM_NAME.equals(pomFileName)) {
				buildArgCmds.add(Constants.HYPHEN_F);
				buildArgCmds.add(pomFileName);
			}
            ApplicationManager applicationManager = PhrescoFrameworkFactory.getApplicationManager();
            BufferedReader reader = applicationManager.performAction(getProjectInfo(), ActionType.COMPONENT_TEST, buildArgCmds, workingDirectory.toString());
            setSessionAttribute(getAppId() + COMPONENT, reader);
            setReqAttribute(REQ_APP_ID, getAppId());
            setReqAttribute(REQ_ACTION_TYPE, COMPONENT);
        } catch (PhrescoException e) {
            if (s_debugEnabled) {
                S_LOGGER.error("Entered into catch block of Quality.runComponentTest()" + FrameworkUtil.getStackTraceAsString(e));
            }
            return showErrorPopup(e, getText(EXCEPTION_QUALITY_COMPONENT_RUN));
        }
        
        return APP_ENVIRONMENT_READER;
    }
	
	public String functional() {
        if (s_debugEnabled) {
            S_LOGGER.debug("Entering Method Quality.functional()");
        }
        
        try {
        	//To get ip of request machine
        	String requestIp = getHttpRequest().getRemoteAddr();
			setReqAttribute(REQ_REQUEST_IP, requestIp);
			
            ApplicationInfo appInfo = getApplicationInfo();
            FrameworkUtil frameworkUtil = FrameworkUtil.getInstance();
            String seleniumToolType = frameworkUtil.getSeleniumToolType(appInfo);
            setReqAttribute(PATH, frameworkUtil.getFunctionalTestDir(appInfo));
            setReqAttribute(REQ_FUNCTEST_SELENIUM_TOOL, seleniumToolType);
            setReqAttribute(REQ_APPINFO, appInfo);
            if (SELENIUM_GRID.equalsIgnoreCase(seleniumToolType)) {
                HubConfiguration hubConfig = getHubConfig();
                if (hubConfig != null) {
                    String host = hubConfig.getHost();
                    int port = hubConfig.getPort();
                    boolean isConnectionAlive = Utility.isConnectionAlive(HTTP_PROTOCOL, host, port);
                    setReqAttribute(REQ_HUB_STATUS, isConnectionAlive);
                }
            }
            
            boolean hasFunctionalLogFile = false;
	        String logFilePath = frameworkUtil.getLogFilePath(appInfo);
            if (StringUtils.isNotEmpty(logFilePath) && new File(getFunctionalLogFilePath()).exists()) {
                hasFunctionalLogFile = true;
            }
            setReqAttribute(REQ_HAS_FUNCTIONAL_LOG_FILE, hasFunctionalLogFile);
            setReqAttribute(REQ_PROJECT_ID, getProjectId());
            setReqAttribute(REQ_CUSTOMER_ID, getCustomerId());
        } catch (PhrescoException e) {
            if (s_debugEnabled) {
                S_LOGGER.error("Entered into catch block of Quality.functional()" + FrameworkUtil.getStackTraceAsString(e));
            }
            return showErrorPopup(e, getText(EXCEPTION_QUALITY_FUNCTIONAL_LOAD));
        } catch (PhrescoPomException e) {
            if (s_debugEnabled) {
                S_LOGGER.error("Entered into catch block of Quality.functional()" + FrameworkUtil.getStackTraceAsString(e));
            }
            return showErrorPopup(new PhrescoException(e), getText(EXCEPTION_QUALITY_FUNCTIONAL_LOAD));
        }

        return APP_FUNCTIONAL_TEST;
    }
	
	public String downloadFunctionalLogFile() throws PhrescoException {
        if (s_debugEnabled) {
            S_LOGGER.debug("Entering Method Quality.downloadFuncLogFile()");
        }
        
        try {
            String logFilePath = getFunctionalLogFilePath();
            fileInputStream = new FileInputStream(new File(logFilePath));
            fileName = logFilePath.substring(logFilePath.lastIndexOf("/") + 1, logFilePath.length());
        } catch (Exception e) {
            if (s_debugEnabled) {
                S_LOGGER.error("Entered into catch block of Quality.downloadFunctionalLogFile()"+ FrameworkUtil.getStackTraceAsString(e));
            }
            return showErrorPopup(new PhrescoException(e), getText(EXCEPTION_LOG_FILE_DOWNLOAD_NOT_AVAILABLE));
        }
        
        return SUCCESS;
    }
	
	private String getFunctionalLogFilePath() throws PhrescoException {
	    StringBuilder builder = new StringBuilder();
	    try {
	        FrameworkUtil frameworkUtil = FrameworkUtil.getInstance();
	        String logFilePath = frameworkUtil.getLogFilePath(getApplicationInfo());
	        builder.append(getApplicationHome());
	        builder.append(logFilePath);
	    } catch (PhrescoException e) {
	        throw new PhrescoException(e);
	    } catch (PhrescoPomException e) {
	        throw new PhrescoException(e);
	    }
	    return builder.toString();
	}
	
	public String fetchFunctionalTestSuites() {
        if (s_debugEnabled) {
            S_LOGGER.debug("Entering Method Quality.fetchFunctionalTestSuites()");
        }
        
        try {
            ApplicationInfo appInfo = getApplicationInfo();
         // TO kill the Process
            String baseDir = Utility.getProjectHome()+ appInfo.getAppDirName();
            Utility.killProcess(baseDir, getTestType());
            String testResultPath = getFunctionalTestResultPath(appInfo, null);
            FrameworkUtil frameworkUtil = FrameworkUtil.getInstance();
            String testSuitePath = frameworkUtil.getFunctionalTestSuitePath(appInfo);
            List<String> resultTestSuiteNames = getTestSuiteNames(testResultPath, testSuitePath);
            if (CollectionUtils.isEmpty(resultTestSuiteNames)) {
                setValidated(true);
                setShowError(getText(ERROR_FUNCTIONAL_TEST));
                return SUCCESS;
            }
            setTestSuiteNames(resultTestSuiteNames);
        } catch (Exception e) {
            if (s_debugEnabled) {
                S_LOGGER.error("Entered into catch block of Quality.fetchFunctionalTestSuites()" + FrameworkUtil.getStackTraceAsString(e));
            }
            return showErrorPopup(new PhrescoException(e), getText(EXCEPTION_QUALITY_FUNCTIONAL_TESTSUITES));
        }
        
        return SUCCESS;
    }
	
	private String getFunctionalTestResultPath(ApplicationInfo appInfo, String testResultFile) throws PhrescoException {
        if (s_debugEnabled) {
            S_LOGGER.debug("Entering Method Quality.getFunctionalTestResultPath()");
        }
        
        StringBuilder sb = new StringBuilder();
        try {
            sb.append(getApplicationHome());
            if (StringUtils.isNotEmpty(getProjectModule())) {
                sb.append(File.separatorChar);
                sb.append(getProjectModule());
            }
            FrameworkUtil frameworkUtil = FrameworkUtil.getInstance();
            sb.append(frameworkUtil.getFunctionalTestReportDir(appInfo));
        } catch (PhrescoException e) {
            throw new PhrescoException(e);
        } catch (PhrescoPomException e) {
            throw new PhrescoException(e);
        }
        
        return sb.toString();
    }
	
	public String showFunctionalTestPopUp() {
        if (s_debugEnabled) {
            S_LOGGER.debug("Entering Method Quality.showFunctionalTestPopUp()");
        }
        try {
            ApplicationInfo appInfo = getApplicationInfo();
            FrameworkUtil frameworkUtil = FrameworkUtil.getInstance();
            String seleniumToolType = frameworkUtil.getSeleniumToolType(appInfo);
            removeSessionAttribute(appInfo.getId() + PHASE_FUNCTIONAL_TEST + SESSION_WATCHER_MAP);
            Map<String, DependantParameters> watcherMap = new HashMap<String, DependantParameters>(8);

            MojoProcessor mojo = new MojoProcessor(new File(getPhrescoPluginInfoFilePath(PHASE_FUNCTIONAL_TEST)));
            List<Parameter> parameters = getMojoParameters(mojo, PHASE_FUNCTIONAL_TEST + HYPHEN + seleniumToolType);
            
            setPossibleValuesInReq(mojo, appInfo, parameters, watcherMap, PHASE_FUNCTIONAL_TEST + HYPHEN + seleniumToolType);
            setSessionAttribute(appInfo.getId() + PHASE_FUNCTIONAL_TEST + HYPHEN + seleniumToolType + SESSION_WATCHER_MAP, watcherMap);
            setReqAttribute(REQ_DYNAMIC_PARAMETERS, parameters);
            setReqAttribute(REQ_PHASE, PHASE_FUNCTIONAL_TEST);
            setReqAttribute(REQ_GOAL, PHASE_FUNCTIONAL_TEST + HYPHEN + seleniumToolType);
        } catch (PhrescoException e) {
            if (s_debugEnabled) {
                S_LOGGER.error("Entered into catch block of Quality.showFunctionalTestPopUp()" + FrameworkUtil.getStackTraceAsString(e));
            }
            return showErrorPopup(e, getText(EXCEPTION_QUALITY_FUNCTIONAL_PARAMS));
        } catch (PhrescoPomException e) {
            if (s_debugEnabled) {
                S_LOGGER.error("Entered into catch block of Quality.showFunctionalTestPopUp()" + FrameworkUtil.getStackTraceAsString(e));
            }
            return showErrorPopup(new PhrescoException(e), getText(EXCEPTION_QUALITY_FUNCTIONAL_PARAMS));
        }
        
        return SUCCESS;
    }
	
	public String runFunctionalTest() {
	    if (s_debugEnabled) {
	        S_LOGGER.debug("Entering Method Quality.runFunctionalTest()");
	    }
	    
	    try {
	        ApplicationInfo appInfo = getApplicationInfo();
	        StringBuilder workingDirectory = new StringBuilder(getAppDirectoryPath(appInfo));
	        if (StringUtils.isNotEmpty(getProjectModule())) {
	            workingDirectory.append(File.separator);
	            workingDirectory.append(getProjectModule());
	        }
	        MojoProcessor mojo = new MojoProcessor(new File(getPhrescoPluginInfoFilePath(PHASE_FUNCTIONAL_TEST)));
	        FrameworkUtil frameworkUtil = FrameworkUtil.getInstance();
            String seleniumToolType = frameworkUtil.getSeleniumToolType(appInfo);
	        persistValuesToXml(mojo, PHASE_FUNCTIONAL_TEST + HYPHEN + seleniumToolType);
	        List<Parameter> parameters = getMojoParameters(mojo, PHASE_FUNCTIONAL_TEST + HYPHEN + seleniumToolType);
            List<String> buildArgCmds = getMavenArgCommands(parameters);
            buildArgCmds.add(HYPHEN_N);
            String pomFileName = Utility.getPomFileName(appInfo);
            if(!Constants.POM_NAME.equals(pomFileName)) {
				buildArgCmds.add(Constants.HYPHEN_F);
				buildArgCmds.add(pomFileName);
			}
	        ApplicationManager applicationManager = PhrescoFrameworkFactory.getApplicationManager();
	        BufferedReader reader = applicationManager.performAction(getProjectInfo(), ActionType.FUNCTIONAL_TEST, buildArgCmds, workingDirectory.toString());
	        setSessionAttribute(getAppId() + FUNCTIONAL, reader);
	        setReqAttribute(REQ_APP_ID, getAppId());
	        setReqAttribute(REQ_ACTION_TYPE, FUNCTIONAL);
	    } catch (PhrescoException e) {
	    	if (s_debugEnabled) {
	    		S_LOGGER.error("Entered into catch block of Quality.runFunctionalTest()"+ FrameworkUtil.getStackTraceAsString(e));
	    	}
	        return showErrorPopup(e, getText(EXCEPTION_QUALITY_FUNCTIONAL_RUN));
	    } catch (PhrescoPomException e) {
	        if (s_debugEnabled) {
                S_LOGGER.error("Entered into catch block of Quality.runFunctionalTest()"+ FrameworkUtil.getStackTraceAsString(e));
            }
            return showErrorPopup(new PhrescoException(e), getText(EXCEPTION_QUALITY_FUNCTIONAL_RUN));
        }

	    return APP_ENVIRONMENT_READER;
	}
	
	public String checkForHub() {
	    if (s_debugEnabled) {
            S_LOGGER.debug("Entering Method Quality.checkForHub()");
        }
	    
	    try {
	        HubConfiguration hubConfig = getHubConfig();
            if (hubConfig != null) {
                String host = hubConfig.getHost();
                int port = hubConfig.getPort();
                setConnectionAlive(Utility.isConnectionAlive(HTTP_PROTOCOL, host, port));
            }
        } catch (Exception e) {
            if (s_debugEnabled) {
                S_LOGGER.error("Entered into catch block of Quality.checkForHub()"+ FrameworkUtil.getStackTraceAsString(e));
            }
            return showErrorPopup(new PhrescoException(e), getText(EXCEPTION_QUALITY_FUNCTIONAL_HUB_CONNECTION));
        }
	    
	    return SUCCESS;
	}
	
	private HubConfiguration getHubConfig() throws PhrescoException {
	    BufferedReader reader = null;
	    HubConfiguration hubConfig = null;
	    try {
	        FrameworkUtil frameworkUtil = FrameworkUtil.getInstance();
	        String functionalTestDir = frameworkUtil.getFunctionalTestDir(getApplicationInfo());
	        StringBuilder sb = new StringBuilder(getApplicationHome());
	        sb.append(functionalTestDir)
	        .append(File.separator)
	        .append(Constants.HUB_CONFIG_JSON);
	        File hubConfigFile = new File(sb.toString());
	        Gson gson = new Gson();
	        reader = new BufferedReader(new FileReader(hubConfigFile));
	        hubConfig = gson.fromJson(reader, HubConfiguration.class);
	    } catch (PhrescoException e) {
	        throw new PhrescoException(e);
	    } catch (PhrescoPomException e) {
	        throw new PhrescoException(e);
	    } catch (FileNotFoundException e) {
	        throw new PhrescoException(e);
	    } finally {
	        Utility.closeReader(reader);
	    }

	    return hubConfig;
	}
	
	private NodeConfiguration getNodeConfig() throws PhrescoException {
        BufferedReader reader = null;
        NodeConfiguration nodeConfig = null;
        try {
            FrameworkUtil frameworkUtil = FrameworkUtil.getInstance();
            String functionalTestDir = frameworkUtil.getFunctionalTestDir(getApplicationInfo());
            StringBuilder sb = new StringBuilder(getApplicationHome());
            sb.append(functionalTestDir)
            .append(File.separator)
            .append(Constants.NODE_CONFIG_JSON);
            File hubConfigFile = new File(sb.toString());
            Gson gson = new Gson();
            reader = new BufferedReader(new FileReader(hubConfigFile));
            nodeConfig = gson.fromJson(reader, NodeConfiguration.class);
        } catch (PhrescoException e) {
            throw new PhrescoException(e);
        } catch (PhrescoPomException e) {
            throw new PhrescoException(e);
        } catch (FileNotFoundException e) {
            throw new PhrescoException(e);
        } finally {
            Utility.closeReader(reader);
        }

        return nodeConfig;
    }
	
	public String showStartedHubLog() {
	    if (s_debugEnabled) {
            S_LOGGER.debug("Entering Method Quality.showStartedHubLog()");
        }
	    
	    try {
	        HubConfiguration hubConfig = getHubConfig();
	        String host = hubConfig.getHost();
	        int port = hubConfig.getPort();
	        String str = "Hub is already running in " + host + ":" + port;
	        InputStream is = new ByteArrayInputStream(str.getBytes());
	        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
	        setSessionAttribute(getAppId() + START_HUB, reader);
	        setReqAttribute(REQ_APP_ID, getAppId());
	        setReqAttribute(REQ_ACTION_TYPE, START_HUB);
        } catch (PhrescoException e) {
            if (s_debugEnabled) {
                S_LOGGER.error("Entered into catch block of Quality.showStartedHubLog()"+ FrameworkUtil.getStackTraceAsString(e));
            }
            return showErrorPopup(e, getText(EXCEPTION_QUALITY_FUNCTIONAL_HUB_LOG));
        }
	    
	    return APP_ENVIRONMENT_READER;
	}
	
	public String showStartHubPopUp() throws PhrescoPomException, FileNotFoundException {
        if (s_debugEnabled) {
            S_LOGGER.debug("Entering Method Quality.showStartHubPopUp()");
        }
        
        try {
        	MojoProcessor mojo = new MojoProcessor(new File(getPhrescoPluginInfoFilePath(PHASE_START_HUB)));
            List<Parameter> parameters = getMojoParameters(mojo, PHASE_START_HUB);
            setReqAttribute(REQ_DYNAMIC_PARAMETERS, parameters);
            setReqAttribute(REQ_GOAL, PHASE_START_HUB);
            setReqAttribute(REQ_PHASE, PHASE_START_HUB);
        } catch (PhrescoException e) {
        	if (s_debugEnabled) {
	    		S_LOGGER.error("Entered into catch block of Quality.showStartHubPopUp()"+ FrameworkUtil.getStackTraceAsString(e));
	    	}
            return showErrorPopup(e, getText(EXCEPTION_QUALITY_FUNCTIONAL_START_HUB_PARAMS));
        }
        
        return SUCCESS;
    }

	public String startHub() {
		if (s_debugEnabled) {
			S_LOGGER.debug("Entering Method Quality.startHub()");
		}

		try {
			ApplicationInfo appInfo = getApplicationInfo();
			MojoProcessor mojo = new MojoProcessor(new File(getPhrescoPluginInfoFilePath(PHASE_START_HUB)));
			persistValuesToXml(mojo, PHASE_START_HUB);
			ApplicationManager applicationManager = PhrescoFrameworkFactory.getApplicationManager();
			ProjectInfo projectInfo = getProjectInfo();
			String workingDirectory = getAppDirectoryPath(appInfo);
			List<Parameter> parameters = getMojoParameters(mojo, PHASE_START_HUB);
            List<String> buildArgCmds = getMavenArgCommands(parameters);
            buildArgCmds.add(HYPHEN_N);
            String pomFileName = Utility.getPomFileName(appInfo);
			if(!Constants.POM_NAME.equals(pomFileName)) {
				buildArgCmds.add(Constants.HYPHEN_F);
				buildArgCmds.add(pomFileName);
			}
			BufferedReader reader = applicationManager.performAction(projectInfo, ActionType.START_HUB, buildArgCmds, workingDirectory);
			setSessionAttribute(getAppId() + START_HUB, reader);
			setReqAttribute(REQ_APP_ID, getAppId());
			setReqAttribute(REQ_ACTION_TYPE, START_HUB);
		} catch (PhrescoException e) {
			if (s_debugEnabled) {
	    		S_LOGGER.error("Entered into catch block of Quality.startHub()"+ FrameworkUtil.getStackTraceAsString(e));
	    	}
			return showErrorPopup(e, getText(EXCEPTION_QUALITY_FUNCTIONAL_START_HUB));
		}

		return APP_ENVIRONMENT_READER;
	}
	
	public String stopHub() {
        if (s_debugEnabled) {
            S_LOGGER.debug("Entering Method Quality.stopHub()");
        }

        try {
            ApplicationInfo appInfo = getApplicationInfo();
            ApplicationManager applicationManager = PhrescoFrameworkFactory.getApplicationManager();
            ProjectInfo projectInfo = getProjectInfo();
            String workingDirectory = getAppDirectoryPath(appInfo);
            List<String> buildArgCmds = new ArrayList<String>();
            buildArgCmds.add(HYPHEN_N);
            String pomFileName = Utility.getPomFileName(getApplicationInfo());
			if(!Constants.POM_NAME.equals(pomFileName)) {
				buildArgCmds.add(Constants.HYPHEN_F);
				buildArgCmds.add(pomFileName);
			}
            BufferedReader reader = applicationManager.performAction(projectInfo, ActionType.STOP_HUB, buildArgCmds, workingDirectory);
            setSessionAttribute(getAppId() + STOP_HUB, reader);
            setReqAttribute(REQ_APP_ID, getAppId());
            setReqAttribute(REQ_ACTION_TYPE, STOP_HUB);
        } catch (PhrescoException e) {
            if (s_debugEnabled) {
                S_LOGGER.error("Entered into catch block of Quality.stopHub()"+ FrameworkUtil.getStackTraceAsString(e));
            }
            return showErrorPopup(e, getText(EXCEPTION_QUALITY_FUNCTIONAL_STOP_HUB));
        }

        return APP_ENVIRONMENT_READER;
    }
	
	public String checkForNode() {
        if (s_debugEnabled) {
            S_LOGGER.debug("Entering Method Quality.checkForNode()");
        }
        
        BufferedReader reader = null;
        try {
            FrameworkUtil frameworkUtil = FrameworkUtil.getInstance();
            String functionalTestDir = frameworkUtil.getFunctionalTestDir(getApplicationInfo());
            StringBuilder sb = new StringBuilder(getApplicationHome());
            sb.append(functionalTestDir)
			.append(File.separator)
			.append(Constants.NODE_CONFIG_JSON);
            File hubConfigFile = new File(sb.toString());
            Gson gson = new Gson();
            reader = new BufferedReader(new FileReader(hubConfigFile));
            NodeConfiguration nodeConfiguration = gson.fromJson(reader, NodeConfiguration.class);
            if (nodeConfiguration != null) {
                String host = nodeConfiguration.getConfiguration().getHost();
                int port = nodeConfiguration.getConfiguration().getPort();
                setConnectionAlive(Utility.isConnectionAlive(HTTP_PROTOCOL, host, port));
            }
        } catch (PhrescoException e) {
            if (s_debugEnabled) {
                S_LOGGER.error("Entered into catch block of Quality.checkForNode()"+ FrameworkUtil.getStackTraceAsString(e));
            }
            return showErrorPopup(e, getText(EXCEPTION_QUALITY_FUNCTIONAL_NODE_CONNECTION));
        } catch (PhrescoPomException e) {
            if (s_debugEnabled) {
                S_LOGGER.error("Entered into catch block of Quality.checkForNode()"+ FrameworkUtil.getStackTraceAsString(e));
            }
            return showErrorPopup(new PhrescoException(e), getText(EXCEPTION_QUALITY_FUNCTIONAL_NODE_CONNECTION));
        } catch (FileNotFoundException e) {
            if (s_debugEnabled) {
                S_LOGGER.error("Entered into catch block of Quality.checkForNode()"+ FrameworkUtil.getStackTraceAsString(e));
            }
            return showErrorPopup(new PhrescoException(e), getText(EXCEPTION_QUALITY_FUNCTIONAL_NODE_CONNECTION));
        } finally {
            Utility.closeStream(reader);
        }
        
        return SUCCESS;
    }
	
	public String showStartedNodeLog() {
        if (s_debugEnabled) {
            S_LOGGER.debug("Entering Method Quality.showStartedNodeLog()");
        }
        
        try {
            NodeConfiguration nodeConfig = getNodeConfig();
            NodeConfig configuration = nodeConfig.getConfiguration();
            String host = configuration.getHost();
            int port = configuration.getPort();
            String str = "Node is already running in " + host + COLON + port;
            InputStream is = new ByteArrayInputStream(str.getBytes());
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            setSessionAttribute(getAppId() + START_NODE, reader);
            setReqAttribute(REQ_APP_ID, getAppId());
            setReqAttribute(REQ_ACTION_TYPE, START_NODE);
        } catch (PhrescoException e) {
            if (s_debugEnabled) {
                S_LOGGER.error("Entered into catch block of Quality.showStartedNodeLog()"+ FrameworkUtil.getStackTraceAsString(e));
            }
            return showErrorPopup(e, getText(EXCEPTION_QUALITY_FUNCTIONAL_NODE_LOG));
        }
        
        return APP_ENVIRONMENT_READER;
    }
	
	public String showStartNodePopUp() {
        if (s_debugEnabled) {
            S_LOGGER.debug("Entering Method Quality.showStartNodePopUp()");
        }
        
        try {
        	MojoProcessor mojo = new MojoProcessor(new File(getPhrescoPluginInfoFilePath(PHASE_START_NODE)));
            List<Parameter> parameters = getMojoParameters(mojo, PHASE_START_NODE);
            setReqAttribute(REQ_DYNAMIC_PARAMETERS, parameters);
            setReqAttribute(REQ_GOAL, PHASE_START_NODE);
            setReqAttribute(REQ_PHASE, PHASE_START_NODE);
        } catch (PhrescoException e) {
        	if (s_debugEnabled) {
	    		S_LOGGER.error("Entered into catch block of Quality.showStartNodePopUp()"+ FrameworkUtil.getStackTraceAsString(e));
	    	}
            return showErrorPopup(e, getText(EXCEPTION_QUALITY_FUNCTIONAL_START_NODE_PARAMS));
        }
        
        return SUCCESS;
    }
	
	public String startNode() {
		if (s_debugEnabled) {
            S_LOGGER.debug("Entering Method Quality.startNode()");
        }
		
        try {
        	ApplicationInfo appInfo = getApplicationInfo();
			MojoProcessor mojo = new MojoProcessor(new File(getPhrescoPluginInfoFilePath(PHASE_START_NODE)));
			persistValuesToXml(mojo, PHASE_START_NODE);
			List<Parameter> parameters = getMojoParameters(mojo, PHASE_START_NODE);
            List<String> buildArgCmds = getMavenArgCommands(parameters);
            buildArgCmds.add(HYPHEN_N);
            String pomFileName = Utility.getPomFileName(appInfo);
			if(!Constants.POM_NAME.equals(pomFileName)) {
				buildArgCmds.add(Constants.HYPHEN_F);
				buildArgCmds.add(pomFileName);
			}
			ApplicationManager applicationManager = PhrescoFrameworkFactory.getApplicationManager();
			ProjectInfo projectInfo = getProjectInfo();
			String workingDirectory = getAppDirectoryPath(appInfo);
			BufferedReader reader = applicationManager.performAction(projectInfo, ActionType.START_NODE, buildArgCmds, workingDirectory);
			setSessionAttribute(getAppId() + START_NODE, reader);
			setReqAttribute(REQ_APP_ID, getAppId());
			setReqAttribute(REQ_ACTION_TYPE, START_NODE);
        } catch (PhrescoException e) {
        	if (s_debugEnabled) {
	    		S_LOGGER.error("Entered into catch block of Quality.startNode()"+ FrameworkUtil.getStackTraceAsString(e));
	    	}
        	return showErrorPopup(e, getText(EXCEPTION_QUALITY_FUNCTIONAL_START_NODE));
        }
        
        return APP_ENVIRONMENT_READER;
    }
	
	public String stopNode() {
        if (s_debugEnabled) {
            S_LOGGER.debug("Entering Method Quality.stopNode()");
        }

        try {
            ApplicationInfo appInfo = getApplicationInfo();
            ApplicationManager applicationManager = PhrescoFrameworkFactory.getApplicationManager();
            ProjectInfo projectInfo = getProjectInfo();
            String workingDirectory = getAppDirectoryPath(appInfo);
            List<String> buildArgCmds = new ArrayList<String>();
            buildArgCmds.add(HYPHEN_N);
            String pomFileName = Utility.getPomFileName(appInfo);
			if(!Constants.POM_NAME.equals(pomFileName)) {
				buildArgCmds.add(Constants.HYPHEN_F);
				buildArgCmds.add(pomFileName);
			}
            BufferedReader reader = applicationManager.performAction(projectInfo, ActionType.STOP_NODE, buildArgCmds, workingDirectory);
            setSessionAttribute(getAppId() + STOP_NODE, reader);
            setReqAttribute(REQ_APP_ID, getAppId());
            setReqAttribute(REQ_ACTION_TYPE, STOP_NODE);
        } catch (PhrescoException e) {
            if (s_debugEnabled) {
                S_LOGGER.error("Entered into catch block of Quality.stopNode()"+ FrameworkUtil.getStackTraceAsString(e));
            }
            return showErrorPopup(e, getText(EXCEPTION_QUALITY_FUNCTIONAL_STOP_NODE));
        }

        return APP_ENVIRONMENT_READER;
    }
	
    public String performance() {
        if (s_debugEnabled) {
            S_LOGGER.debug("Entering Method Quality.performance()");
        }

        try {
        	//To get ip of request machine
        	String requestIp = getHttpRequest().getRemoteAddr();
			setReqAttribute(REQ_REQUEST_IP, requestIp);
			
            ApplicationInfo appInfo = getApplicationInfo();
            FrameworkUtil frameworkUtil = FrameworkUtil.getInstance();
            MojoProcessor mojo = new MojoProcessor(new File(getPhrescoPluginInfoFilePath(PHASE_PERFORMANCE_TEST)));
            Parameter testAgainstParameter = mojo.getParameter(PHASE_PERFORMANCE_TEST, REQ_TEST_AGAINST);
            if (testAgainstParameter != null && TYPE_LIST.equalsIgnoreCase(testAgainstParameter.getType())) {
            	setReqAttribute(REQ_TEST_AGAINST_VALUES, testAgainstParameter.getPossibleValues().getValue());
            }
            String performanceTestShowDevice = frameworkUtil.getPerformanceTestShowDevice(appInfo);
            setReqAttribute(SHOW_ANDROID_DEVICE, performanceTestShowDevice);
            setReqAttribute(PATH, frameworkUtil.getPerformanceTestDir(appInfo));
            setReqAttribute(REQ_APPINFO, appInfo);
        } catch (PhrescoException e) {
            if (s_debugEnabled) {
                S_LOGGER.error("Entered into catch block of Quality.performance()"+ FrameworkUtil.getStackTraceAsString(e));
            }
            return showErrorPopup(e, getText(EXCEPTION_QUALITY_FUNCTIONAL_STOP_NODE));
        } catch (PhrescoPomException e) {
        	if (s_debugEnabled) {
                S_LOGGER.error("Entered into catch block of Quality.performance()"+ FrameworkUtil.getStackTraceAsString(e));
            }
        	return showErrorPopup(new PhrescoException(e), getText(EXCEPTION_QUALITY_FUNCTIONAL_STOP_NODE));
        }

        return APP_PERFORMANCE_TEST;
    }
    
    public String showPerformanceTestPopUp() {
        if (s_debugEnabled) {
            S_LOGGER.debug("Entering Method Quality.showPerformanceTestPopUp()");
        }
        
        try {
        	ApplicationInfo appInfo = getApplicationInfo();
        	removeSessionAttribute(appInfo.getId() + PHASE_PERFORMANCE_TEST + SESSION_WATCHER_MAP);
        	Map<String, DependantParameters> watcherMap = new HashMap<String, DependantParameters>(8);

        	MojoProcessor mojo = new MojoProcessor(new File(getPhrescoPluginInfoFilePath(PHASE_PERFORMANCE_TEST)));
        	List<Parameter> parameters = getMojoParameters(mojo, PHASE_PERFORMANCE_TEST);
        	setPossibleValuesInReq(mojo, appInfo, parameters, watcherMap, PHASE_PERFORMANCE_TEST);
        	setSessionAttribute(appInfo.getId() + PHASE_PERFORMANCE_TEST + SESSION_WATCHER_MAP, watcherMap);
        	setReqAttribute(REQ_DYNAMIC_PARAMETERS, parameters);
        	setReqAttribute(REQ_GOAL, PHASE_PERFORMANCE_TEST);
        	setReqAttribute(REQ_PHASE, PHASE_PERFORMANCE_TEST);
        } catch (PhrescoException e) {
        	if (s_debugEnabled) {
        		S_LOGGER.error("Entered into catch block of Quality.showFunctionalTestPopUp()" + FrameworkUtil.getStackTraceAsString(e));
        	}
        	return showErrorPopup(e, getText(EXCEPTION_QUALITY_FUNCTIONAL_PARAMS));
        }

        return SUCCESS;
    }
	
	private List<String> getTestSuiteNames(String testResultPath, String testSuitePath) throws FileNotFoundException, ParserConfigurationException,
            SAXException, IOException, TransformerException, PhrescoException, PhrescoPomException {
        String testSuitesMapKey = getAppId() + getTestType() + getProjectModule() + getTechReport();
        Map<String, NodeList> testResultNameMap = testSuiteMap.get(testSuitesMapKey);
        List<String> resultTestSuiteNames = null;
        if (MapUtils.isEmpty(testResultNameMap) || updateCache) { //  || StringUtils.isNotEmpty(fromPage) when the user clicks on close button, newly generated report shoud be displayed
            File[] resultFiles = getTestResultFiles(testResultPath);
            if (!ArrayUtils.isEmpty(resultFiles)) {
                QualityUtil.sortResultFile(resultFiles);
                updateCache(resultFiles, testSuitePath);
            }
            testResultNameMap = testSuiteMap.get(testSuitesMapKey);
        }
        if (testResultNameMap != null) {
        	resultTestSuiteNames = new ArrayList<String>(testResultNameMap.keySet());
        }
        return resultTestSuiteNames;
    }
	
    private String getTestResultPath(ApplicationInfo appInfo, String testResultFile) throws ParserConfigurationException, 
            SAXException, IOException, TransformerException, PhrescoException, JAXBException, PhrescoPomException {
    	if (s_debugEnabled) { 
	    	S_LOGGER.debug("Entering Method Quality.getTestDocument(Project project, String testResultFile)");
	    	S_LOGGER.debug("getTestDocument() ProjectInfo = "+appInfo);
	    	S_LOGGER.debug("getTestDocument() TestResultFile = "+testResultFile);
    	}	
    	
        FrameworkUtil frameworkUtil = FrameworkUtil.getInstance();
        StringBuilder sb = new StringBuilder(getApplicationHome());

        if (LOAD.equals(getTestType())) {
        	 String loadTestReportDir = frameworkUtil.getLoadTestReportDir(appInfo);
             Pattern p = Pattern.compile(TEST_DIRECTORY);
             Matcher matcher = p.matcher(loadTestReportDir);
             if (StringUtils.isNotEmpty(loadTestReportDir) && matcher.find()) {
            	 loadTestReportDir = matcher.replaceAll(getTestResultsType());
             }
             sb.append(loadTestReportDir);
             sb.append(File.separator);
             sb.append(testResultFile);
        } else if (PERFORMACE.equals(getTestType())) {
            String performanceTestReportDir = frameworkUtil.getPerformanceTestReportDir(appInfo);
            Pattern p = Pattern.compile(TEST_DIRECTORY);
            Matcher matcher = p.matcher(performanceTestReportDir);
            if (StringUtils.isNotEmpty(performanceTestReportDir) && matcher.find()) {
                performanceTestReportDir = matcher.replaceAll(getTestResultsType());
            }
            sb.append(performanceTestReportDir);
            sb.append(File.separator);
            sb.append(testResultFile);
        }
        return sb.toString();
    }

	private Document getDocument(File resultFile) throws FileNotFoundException, ParserConfigurationException, SAXException, IOException {
		InputStream fis = null;
        DocumentBuilder builder = null;
        try {
        	if (s_debugEnabled) { 
        		S_LOGGER.debug("Report path" + resultFile.getAbsolutePath());
        	}	
        	fis = new FileInputStream(resultFile);
            DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
            domFactory.setValidating(false);
            domFactory.setNamespaceAware(false);
            builder = domFactory.newDocumentBuilder();
            Document doc = builder.parse(fis);
            return doc;
        } finally {
            if(fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                	if (s_debugEnabled) { 
                		S_LOGGER.error("Entered into catch block of Quality.getTestDocument()"+ e);
                	}	
                }
            }
        }
	}
	
	private File[] getTestResultFiles(String path) {
		File testDir = new File(path);
        if (testDir.isDirectory()) {
            FilenameFilter filter = new FileListFilter("", "xml");
            return testDir.listFiles(filter);
        }
        return null;
	}

    private void updateCache(File[] resultFiles, String testSuitePath) throws FileNotFoundException, ParserConfigurationException, SAXException, IOException, TransformerException, PhrescoException, PhrescoPomException {
		Map<String, NodeList> mapTestSuites = new HashMap<String, NodeList>(10);
    	for (File resultFile : resultFiles) {
			try {
				Document doc = getDocument(resultFile);
				NodeList testSuiteNodeList = evaluateTestSuite(doc, testSuitePath);
				if (testSuiteNodeList.getLength() > 0) {
					List<TestSuite> testSuites = getTestSuite(testSuiteNodeList);
					for (TestSuite testSuite : testSuites) {
						mapTestSuites.put(testSuite.getName(), testSuiteNodeList);
					}
				}
			} catch (PhrescoException e) {
				// continue the loop to filter the testResultFile
				if (s_debugEnabled) { 
					S_LOGGER.error("Entered into catch block of Quality.updateCache()"+ e);
				}	
			} catch (XPathExpressionException e) {
				// continue the loop to filter the testResultFile
				if (s_debugEnabled) { 
					S_LOGGER.error("Entered into catch block of Quality.updateCache()"+ e);
				}	
			} catch (SAXException e) {
				// continue the loop to filter the testResultFile
				if (s_debugEnabled) { 
					S_LOGGER.error("Entered into catch block of Quality.updateCache()"+ e);
				}	
			}
		}
	    String testSuitesKey = getAppId() + getTestType() + getProjectModule() + getTechReport();
	    
		testSuiteMap.put(testSuitesKey, mapTestSuites);
    }
    
    private NodeList evaluateTestSuite(Document doc, String testSuitePath) throws PhrescoException, XPathExpressionException, PhrescoPomException {
    	XPath xpath = XPathFactory.newInstance().newXPath();
		XPathExpression xPathExpression = xpath.compile(testSuitePath);
		NodeList testSuiteNode = (NodeList) xPathExpression.evaluate(doc, XPathConstants.NODESET);
		return testSuiteNode;
    }

    private List<TestSuite> getTestSuite(NodeList nodelist) throws TransformerException, PhrescoException {
    	if (s_debugEnabled) { 
    		S_LOGGER.debug("Entering Method Quality.getTestSuite(NodeList nodelist)");
    	}	

    	List<TestSuite> testSuites = new ArrayList<TestSuite>(2);
		TestSuite testSuite = null;
		for (int i = 0; i < nodelist.getLength(); i++) {
		    testSuite =  new TestSuite();
		    Node node = nodelist.item(i);
		    NamedNodeMap nameNodeMap = node.getAttributes();

		    for (int k = 0; k < nameNodeMap.getLength(); k++) {
		        Node attribute = nameNodeMap.item(k);
		        String attributeName = attribute.getNodeName();
		        String attributeValue = attribute.getNodeValue();
		        if (ATTR_ASSERTIONS.equals(attributeName)) {
		            testSuite.setAssertions(attributeValue);
		        } else if (ATTR_ERRORS.equals(attributeName)) {
		            testSuite.setErrors(Float.parseFloat(attributeValue));
		        } else if (ATTR_FAILURES.equals(attributeName)) {
		            testSuite.setFailures(Float.parseFloat(attributeValue));
		        } else if (ATTR_FILE.equals(attributeName)) {
		            testSuite.setFile(attributeValue);
		        } else if (ATTR_NAME.equals(attributeName)) {
		            testSuite.setName(attributeValue);
		        } else if (ATTR_TESTS.equals(attributeName)) {
		            testSuite.setTests(Float.parseFloat(attributeValue));
		        } else if (ATTR_TIME.equals(attributeName)) {
		            testSuite.setTime(attributeValue);
		        }
		    }
		    testSuites.add(testSuite);
		}
		
		return testSuites;
    }

    private List<TestCase> getTestCases(NodeList testSuites, String testSuitePath, String testCasePath) throws TransformerException, PhrescoException, PhrescoPomException {
    	if (s_debugEnabled) { 
    		S_LOGGER.debug("Entering Method Quality.getTestCases(Document doc, String testSuiteName)");
    	}	
        
    	try {
    		if (s_debugEnabled) { 
    			S_LOGGER.debug("Test suite path " + testSuitePath);
    			S_LOGGER.debug("Test suite path " + testCasePath);
    		}
            StringBuilder sb = new StringBuilder(); //testsuites/testsuite[@name='yyy']/testcase
            sb.append(testSuitePath);
            sb.append(NAME_FILTER_PREFIX);
            sb.append(getTestSuite());
            sb.append(NAME_FILTER_SUFIX);
            sb.append(testCasePath);

            XPath xpath = XPathFactory.newInstance().newXPath();
            NodeList nodeList = (NodeList) xpath.evaluate(sb.toString(), testSuites.item(0).getParentNode(), XPathConstants.NODESET);
            
            // For tehnologies like php and drupal duoe to plugin change xml testcase path modified
            if (nodeList.getLength() == 0) {
                StringBuilder sbMulti = new StringBuilder();
                sbMulti.append(testSuitePath);
                sbMulti.append(NAME_FILTER_PREFIX);
                sbMulti.append(getTestSuite());
                sbMulti.append(NAME_FILTER_SUFIX);
                sbMulti.append(XPATH_TESTSUTE_TESTCASE);
                nodeList = (NodeList) xpath.evaluate(sbMulti.toString(), testSuites.item(0).getParentNode(), XPathConstants.NODESET);
            }
            
            // For technology sharepoint
            if (nodeList.getLength() == 0) {
                StringBuilder sbMulti = new StringBuilder(); //testsuites/testsuite[@name='yyy']/testcase
                sbMulti.append(XPATH_MULTIPLE_TESTSUITE);
                sbMulti.append(NAME_FILTER_PREFIX);
                sbMulti.append(getTestSuite());
                sbMulti.append(NAME_FILTER_SUFIX);
                sbMulti.append(testCasePath);
                nodeList = (NodeList) xpath.evaluate(sbMulti.toString(), testSuites.item(0).getParentNode(), XPathConstants.NODESET);
            }

            List<TestCase> testCases = new ArrayList<TestCase>();
            
        	StringBuilder screenShotDir = new StringBuilder(getApplicationHome());
        	screenShotDir.append(File.separator);
        	FrameworkUtil frameworkUtil = FrameworkUtil.getInstance();
        	screenShotDir.append(frameworkUtil.getFunctionalTestReportDir(getApplicationInfo()));
        	screenShotDir.append(File.separator);
        	screenShotDir.append(SCREENSHOT_DIR);
        	screenShotDir.append(File.separator);
        	
        	int failureTestCases = 0;
            int errorTestCases = 0;
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                NodeList childNodes = node.getChildNodes();
                NamedNodeMap nameNodeMap = node.getAttributes();
                TestCase testCase = new TestCase();
                for (int k = 0; k < nameNodeMap.getLength(); k++) {
                    Node attribute = nameNodeMap.item(k);
                    String attributeName = attribute.getNodeName();
                    String attributeValue = attribute.getNodeValue();
                    if (ATTR_NAME.equals(attributeName)) {
                        testCase.setName(attributeValue);
                    } else if (ATTR_CLASS.equals(attributeName) || ATTR_CLASSNAME.equals(attributeName)) {
                        testCase.setTestClass(attributeValue);
                    } else if (ATTR_FILE.equals(attributeName)) {
                        testCase.setFile(attributeValue);
                    } else if (ATTR_LINE.equals(attributeName)) {
                        testCase.setLine(Float.parseFloat(attributeValue));
                    } else if (ATTR_ASSERTIONS.equals(attributeName)) {
                        testCase.setAssertions(Float.parseFloat(attributeValue));
                    } else if (ATTR_TIME.equals(attributeName)) {
                        testCase.setTime(attributeValue);
                    }
                }
                
                if (childNodes != null && childNodes.getLength() > 0) {
                    for (int j = 0; j < childNodes.getLength(); j++) {
                        Node childNode = childNodes.item(j);
                        if (ELEMENT_FAILURE.equals(childNode.getNodeName())) {
                        	failureTestCases++;
                            TestCaseFailure failure = getFailure(childNode);
                            if (failure != null) {
                            	File file = new File(screenShotDir.toString() + testCase.getName() + DOT + IMG_PNG_TYPE);
                            	if (file.exists()) {
                            		failure.setHasFailureImg(true);
                            	}
                                testCase.setTestCaseFailure(failure);
                            } 
                        }

                        if (ELEMENT_ERROR.equals(childNode.getNodeName())) {
                        	errorTestCases++;
                            TestCaseError error = getError(childNode);
                            if (error != null) {
                            	File file = new File(screenShotDir.toString() + testCase.getName() + DOT + IMG_PNG_TYPE);
                            	if (file.exists()) {
                            		error.setHasErrorImg(true);
                            	}
                                testCase.setTestCaseError(error);
                            }
                        }
                    }
                }
                testCases.add(testCase);
            }
            setReqAttribute(REQ_TESTSUITE_FAILURES, failureTestCases + "");
            setReqAttribute(REQ_TESTSUITE_ERRORS, errorTestCases + "");
            setReqAttribute(REQ_TESTSUITE_TESTS, nodeList.getLength() + "");
			
            return testCases;
        } catch (PhrescoException e) {
        	if (s_debugEnabled) { 
        		S_LOGGER.error("Entered into catch block of Quality.getTestCases()"+ e);
        	}	
            throw e;
        } catch (XPathExpressionException e) {
        	if (s_debugEnabled) { 
        		S_LOGGER.error("Entered into XPathExpressionException catch block of Quality.getTestCases()"+ e);
        	}
            throw new PhrescoException(e);
		}
    }
    
    private static TestCaseFailure getFailure(Node failureNode) throws TransformerException {
    	if (s_debugEnabled) { 
           S_LOGGER.debug("Entering Method Quality.getFailure(Node failureNode)");
           S_LOGGER.debug("getFailure() NodeName = "+failureNode.getNodeName());
    	}   
        TestCaseFailure failure = new TestCaseFailure();
        try {
            failure.setDescription(failureNode.getTextContent());
            failure.setFailureType(REQ_TITLE_EXCEPTION);
            NamedNodeMap nameNodeMap = failureNode.getAttributes();

            if (nameNodeMap != null && nameNodeMap.getLength() > 0) {
                for (int k = 0; k < nameNodeMap.getLength(); k++){
                    Node attribute = nameNodeMap.item(k);
                    String attributeName = attribute.getNodeName();
                    String attributeValue = attribute.getNodeValue();

                    if (ATTR_TYPE.equals(attributeName)) {
                        failure.setFailureType(attributeValue);
                    }
                }
            }
        } catch (Exception e) {
        	if (s_debugEnabled) { 
               S_LOGGER.error("Entered into catch block of Quality.getFailure()"+ e);
        	}   
        }
        return failure;
    }

    private static TestCaseError getError(Node errorNode) throws TransformerException {
    	if (s_debugEnabled) { 
           S_LOGGER.debug("Entering Method Quality.getError(Node errorNode)");
           S_LOGGER.debug("getError() Node = "+errorNode.getNodeName());
    	}   
        TestCaseError tcError = new TestCaseError();
        try {
            tcError.setDescription(errorNode.getTextContent());
            tcError.setErrorType(REQ_TITLE_ERROR);
            NamedNodeMap nameNodeMap = errorNode.getAttributes();
            if (nameNodeMap != null && nameNodeMap.getLength() > 0) {
                for (int k = 0; k < nameNodeMap.getLength(); k++){
                    Node attribute = nameNodeMap.item(k);
                    String attributeName = attribute.getNodeName();
                    String attributeValue = attribute.getNodeValue();

                    if (ATTR_TYPE.equals(attributeName)) {
                        tcError.setErrorType(attributeValue);
                    }
                }
            }
        } catch (Exception e) {
        	if (s_debugEnabled) { 
               S_LOGGER.error("Entered into catch block of Quality.getError()"+ e);
        	}   
        }
        return tcError;
    }

    private List<TestResult> getLoadTestResult(ApplicationInfo appInfo, String testResultFile) throws TransformerException, PhrescoException, ParserConfigurationException, SAXException, IOException {
    	if (s_debugEnabled) { 
           S_LOGGER.debug("Entering Method Quality.getLoadTestResult(Project project, String testResultFile)");
           S_LOGGER.debug("getTestResult() ProjectInfo = " + appInfo);
           S_LOGGER.debug("getTestResult() TestResultFile = " + testResultFile);
    	}   
        List<TestResult> testResults = new ArrayList<TestResult>(2);
        try {
        	String testResultPath = getTestResultPath(appInfo, testResultFile);
            Document doc = getDocument(new File(testResultPath)); 
            NodeList nodeList = org.apache.xpath.XPathAPI.selectNodeList(doc, XPATH_TEST_RESULT);

            TestResult testResult = null;

            for (int i = 0; i < nodeList.getLength(); i++) {
                testResult =  new TestResult();
                Node node = nodeList.item(i);
                NamedNodeMap nameNodeMap = node.getAttributes();

                for (int k = 0; k < nameNodeMap.getLength(); k++) {
                    Node attribute = nameNodeMap.item(k);
                    String attributeName = attribute.getNodeName();
                    String attributeValue = attribute.getNodeValue();

                    if (ATTR_JM_TIME.equals(attributeName)) {
                        testResult.setTime(Integer.parseInt(attributeValue));
                    } else if (ATTR_JM_LATENCY_TIME.equals(attributeName)) {
                        testResult.setLatencyTime(Integer.parseInt(attributeValue));
                    } else if (ATTR_JM_TIMESTAMP.equals(attributeName)) {
                        Date date = new Date(Long.parseLong(attributeValue));
                        DateFormat format = new SimpleDateFormat(DATE_TIME_FORMAT);
                        String strDate = format.format(date);
                        testResult.setTimeStamp(strDate);
                    } else if (ATTR_JM_SUCCESS_FLAG.equals(attributeName)) {
                        testResult.setSuccess(Boolean.parseBoolean(attributeValue));
                    } else if (ATTR_JM_LABEL.equals(attributeName)) {
                        testResult.setLabel(attributeValue);
                    } else if (ATTR_JM_THREAD_NAME.equals(attributeName)) {
                        testResult.setThreadName(attributeValue);
                    }
                }
                testResults.add(testResult);
            }
        } catch (Exception e) {
        	if (s_debugEnabled) { 
               S_LOGGER.error("Entered into catch block of Quality.getLoadTestResult()"+ e);
        	}   
        }
        return testResults;
    }

    public String performanceTest() throws PhrescoException {
    	try {
    		FileOutputStream fop = null;
    		ApplicationManager applicationManager = PhrescoFrameworkFactory.getApplicationManager();
    		ProjectInfo projectInfo = getProjectInfo();
    		ApplicationInfo applicationInfo = getApplicationInfo();
    		MojoProcessor mojo = new MojoProcessor(new File(getPhrescoPluginInfoFilePath(PHASE_PERFORMANCE_TEST)));
    		persistValuesToXml(mojo, PHASE_PERFORMANCE_TEST);

    		//To get maven build arguments
    		List<Parameter> parameters = getMojoParameters(mojo, PHASE_PERFORMANCE_TEST);
    		List<String> buildArgCmds = getMavenArgCommands(parameters);
    		buildArgCmds.add(HYPHEN_N);
    		String pomFileName = Utility.getPomFileName(applicationInfo);
    		if(!Constants.POM_NAME.equals(pomFileName)) {
				buildArgCmds.add(Constants.HYPHEN_F);
				buildArgCmds.add(pomFileName);
			}
    		String workingDirectory = getAppDirectoryPath(applicationInfo);  
    		
    		//
            performanceJsonWriter();    			
    		BufferedReader reader = applicationManager.performAction(projectInfo, ActionType.PERFORMANCE_TEST, buildArgCmds, workingDirectory);
    		setSessionAttribute(getAppId() + PERFORMANCE_TEST, reader);
    		setReqAttribute(REQ_APP_ID, getAppId());
    		setReqAttribute(REQ_ACTION_TYPE, PERFORMANCE_TEST);
    	} catch (PhrescoException e) {
    		throw new PhrescoException(e);
    	}

    	return SUCCESS;
    }

	public String performanceJsonWriter() throws PhrescoException {
		
		FileWriter fw = null;
		
		try {
			if(StringUtils.isNotEmpty(getTestAgainst())) {
				ApplicationInfo applicationInfo = getApplicationInfo();
				PomProcessor processor = new PomProcessor(getPOMFile(applicationInfo));					
		        String performTestDir = processor.getProperty(POM_PROP_KEY_PERFORMANCETEST_DIR);	        
				FileOutputStream fop;
				boolean success = false;
				if(getIsFromCI().equalsIgnoreCase("true")) {				
					StringBuilder filepath = new StringBuilder(Utility.getProjectHome())
					.append(applicationInfo.getAppDirName())
					.append(performTestDir)
					.append(File.separator)
					.append(getTestAgainst())
					.append(File.separator)
					.append(Constants.FOLDER_JSON);
//					.append(File.separator)
//					.append("CITemp");					
					success = new File(filepath.toString()).mkdirs();
					
					filepath.append(File.separator)
					.append(getTestName())
					.append(DOT_JSON);
					File file = new File(filepath.toString());
					fop = new FileOutputStream(file);
					if (!file.exists()) {
						file.createNewFile();
					}
					byte[] contentInBytes = getResultJson().getBytes();				 
					fop.write(contentInBytes);
					fop.flush();
					fop.close();				
										
					StringBuilder infofilepath = new StringBuilder(Utility.getProjectHome())
					.append(applicationInfo.getAppDirName())
					.append(performTestDir)
					.append(File.separator)
					.append(getTestAgainst())
					.append(File.separator)
					.append(Constants.FOLDER_JSON)				
//					.append(File.separator)
//					.append("CITemp")
					.append(File.separator)
					.append("ci")
					.append(".info");					
					file = new File(infofilepath.toString());					
					if (!file.exists()) {
						file.createNewFile();
						fw = new FileWriter(file);
						fw.write(getTestName()+DOT_JSON);
					} else {
						fw = new FileWriter(infofilepath.toString(),true);
						StringBuilder jsonFileName = new StringBuilder()
						.append(",")
						.append(getTestName());
						fw.write(jsonFileName.toString()+DOT_JSON);						
					}
					
				} else {					
					StringBuilder filepath = new StringBuilder(Utility.getProjectHome())
					.append(applicationInfo.getAppDirName())
					.append(performTestDir)
					.append(File.separator)
					.append(getTestAgainst())
					.append(File.separator)
					.append(Constants.FOLDER_JSON);			
					if (new File(filepath.toString()).exists()) {
						filepath.append(File.separator)
						.append(getTestName())
						.append(DOT_JSON);				
						File file = new File(filepath.toString());
						fop = new FileOutputStream(file);
						if (!file.exists()) {
							file.createNewFile();
						}
						byte[] contentInBytes = getResultJson().getBytes();				 
						fop.write(contentInBytes);
						fop.flush();
						fop.close();				
					}
				}				
			}			
		} catch (Exception e) {			
			throw new PhrescoException(e);
		} finally {
			Utility.closeStream(fw);
		}
		return SUCCESS;
	}
    
    private File getPOMFile(ApplicationInfo appInfo) {
        StringBuilder builder = new StringBuilder(Utility.getProjectHome())
        .append(appInfo.getAppDirName())
        .append(File.separatorChar)
        .append(Utility.getPomFileName(appInfo));
        return new File(builder.toString());
    }
    
    public String load() throws JAXBException, IOException, PhrescoPomException {
    	if (s_debugEnabled) {
	        S_LOGGER.debug("Entering Method Quality.load()");
	    } 
	    
    	try {
        	//To get ip of request machine
    		String requestIp = getHttpRequest().getRemoteAddr();
    		setReqAttribute(REQ_REQUEST_IP, requestIp);
    		
    		ApplicationInfo appInfo = getApplicationInfo();	
    		FrameworkUtil frameworkUtil = FrameworkUtil.getInstance();
    		setReqAttribute(PATH, frameworkUtil.getLoadTestDir(appInfo));
    		setReqAttribute(REQ_APP_INFO, appInfo);
    	} catch(Exception e){
        }
    	
    	return APP_LOAD_TEST;
    }
    
    public String loadTestResultAvail() {
        if (s_debugEnabled) {
            S_LOGGER.debug("Entering Method Quality.loadTestResultAvail()");
        }

        try {
        	ApplicationInfo appInfo = getApplicationInfo();
	       	 // TO kill the Process
        	String baseDir = Utility.getProjectHome()+ appInfo.getAppDirName();
        	Utility.killProcess(baseDir, getTestType());
            FrameworkUtil frameworkUtil = FrameworkUtil.getInstance();
            List<String> testResultsTypes = new ArrayList<String>();
            testResultsTypes.add("server");
            testResultsTypes.add("webservice");
            for (String testResultsType: testResultsTypes) {
                StringBuilder sb = new StringBuilder(getApplicationHome());
                String loadReportDir = frameworkUtil.getLoadTestReportDir(getApplicationInfo());
                if (StringUtils.isNotEmpty(loadReportDir) && StringUtils.isNotEmpty(testResultsType)) {
                    Pattern p = Pattern.compile("dir_type");
                    Matcher matcher = p.matcher(loadReportDir);
                    loadReportDir = matcher.replaceAll(testResultsType);
                    sb.append(loadReportDir); 
                }
                File file = new File(sb.toString());
                File[] children = file.listFiles(new XmlNameFileFilter(FILE_EXTENSION_XML));
                if (!ArrayUtils.isEmpty(children)) {
                    setResultFileAvailable(true);
                    break;
                }
            }
        } catch(Exception e) {
            if (s_debugEnabled) {
                S_LOGGER.error("Entered into catch block of Quality.loadTestResultAvail()"+ FrameworkUtil.getStackTraceAsString(e));
            }
        }

        return SUCCESS;
    }
    
    public String fetchLoadTestResultFiles() {
        if (s_debugEnabled) {
            S_LOGGER.debug("Entering Method Quality.fetchLoadTestResultFiles()");
        }

        try {
            StringBuilder sb = new StringBuilder(getApplicationHome());
            FrameworkUtil frameworkUtil = FrameworkUtil.getInstance();
            String loadReportDir = frameworkUtil.getLoadTestReportDir(getApplicationInfo());

            if (s_debugEnabled) {
                S_LOGGER.debug("test type performance test Report directory " + loadReportDir);
            }

            if (StringUtils.isNotEmpty(loadReportDir) && StringUtils.isNotEmpty(getTestResultsType())) {
                Pattern p = Pattern.compile(TEST_DIRECTORY);
                Matcher matcher = p.matcher(loadReportDir);
                loadReportDir = matcher.replaceAll(getTestResultsType());
                sb.append(loadReportDir);
            }

            if (s_debugEnabled) {
                S_LOGGER.debug("test type performance test Report directory & Type " + sb.toString() + " Type " + getTestResultsType());
            }

            File file = new File(sb.toString());
            File[] resultFiles = file.listFiles(new XmlNameFileFilter(FILE_EXTENSION_XML));
            if (!ArrayUtils.isEmpty(resultFiles)) {
                QualityUtil.sortResultFile(resultFiles);
                for (File resultFile : resultFiles) {
                    if (resultFile.isFile()) {
                        testResultFiles.add(resultFile.getName());
                    }
                }
            }
        } catch(PhrescoException e) {
            if (s_debugEnabled) {
                S_LOGGER.error("Entered into catch block of Quality.fetchPerformanceTestResultFiles()"+ FrameworkUtil.getStackTraceAsString(e));
            }
        } catch (PhrescoPomException e) {
            if (s_debugEnabled) {
                S_LOGGER.error("Entered into catch block of Quality.fetchPerformanceTestResultFiles()"+ FrameworkUtil.getStackTraceAsString(e));
            }
        }

        return SUCCESS;
    }
    
    public String showLoadTestPopup() throws PhrescoException{
    	
    	try {
    		ApplicationInfo appInfo = getApplicationInfo();
            removeSessionAttribute(appInfo.getId() + PHASE_LOAD_TEST + SESSION_WATCHER_MAP);
            Map<String, DependantParameters> watcherMap = new HashMap<String, DependantParameters>(8);

            MojoProcessor mojo = new MojoProcessor(new File(getPhrescoPluginInfoFilePath(PHASE_LOAD_TEST)));
            List<Parameter> parameters = getMojoParameters(mojo, PHASE_LOAD_TEST);

            setPossibleValuesInReq(mojo, appInfo, parameters, watcherMap, PHASE_LOAD_TEST);
            setSessionAttribute(appInfo.getId() + PHASE_LOAD_TEST + SESSION_WATCHER_MAP, watcherMap);
            setReqAttribute(REQ_DYNAMIC_PARAMETERS, parameters);
            setReqAttribute(REQ_GOAL, PHASE_LOAD_TEST);
            setReqAttribute(REQ_PHASE, PHASE_LOAD_TEST);
    	} catch(PhrescoException e) {
    		if (s_debugEnabled) {
	    		S_LOGGER.error("Entered into catch block of Quality.showLoadTestPopup()"+ FrameworkUtil.getStackTraceAsString(e));
	    	}
			return showErrorPopup(e, getText(EXCEPTION_QUALITY_LOAD_PARAMS));
    	}
    	
    	return SUCCESS;
   }
    
    public String runLoadTest() {
    	if (s_debugEnabled) {
	        S_LOGGER.debug("Entering Method Quality.runLoadTest()");
	    } 
    	try {
    		ApplicationInfo appInfo = getApplicationInfo();
	        StringBuilder workingDirectory = new StringBuilder(getAppDirectoryPath(appInfo));
	        MojoProcessor mojo = new MojoProcessor(new File(getPhrescoPluginInfoFilePath(PHASE_LOAD_TEST)));
            persistValuesToXml(mojo, PHASE_LOAD_TEST);
            List<Parameter> parameters = getMojoParameters(mojo, PHASE_LOAD_TEST);
            List<String> buildArgCmds = getMavenArgCommands(parameters);
            buildArgCmds.add(HYPHEN_N);
            String pomFileName = Utility.getPomFileName(appInfo);
            if(!Constants.POM_NAME.equals(pomFileName)) {
				buildArgCmds.add(Constants.HYPHEN_F);
				buildArgCmds.add(pomFileName);
			}
            ApplicationManager applicationManager = PhrescoFrameworkFactory.getApplicationManager();
            BufferedReader reader = applicationManager.performAction(getProjectInfo(), ActionType.LOAD_TEST, buildArgCmds, workingDirectory.toString());
            setSessionAttribute(getAppId() + LOAD, reader);
            setReqAttribute(REQ_APP_ID, getAppId());
            setReqAttribute(REQ_ACTION_TYPE, LOAD);
    	} catch(PhrescoException e) {
    		if (s_debugEnabled) {
	    		S_LOGGER.error("Entered into catch block of Quality.runLoadTest()"+ FrameworkUtil.getStackTraceAsString(e));
	    	}
			return showErrorPopup(e, getText(EXCEPTION_QUALITY_LOAD_RUN));
    	}
    	return APP_ENVIRONMENT_READER;
    }
    
    public String loadTestResult() {
        
           S_LOGGER.debug("Entering Method Quality.loadTestResult()");
        
        try {
            String testResultFile = getHttpRequest().getParameter(REQ_TEST_RESULT_FILE);
            List<TestResult> testResults = getLoadTestResult(getApplicationInfo(), testResultFile);
            getHttpRequest().setAttribute(REQ_TEST_RESULT, testResults);
            Gson gson = new Gson();
            StringBuilder jSon = new StringBuilder();
            StringBuilder data = new StringBuilder();
            jSon.append(GRAPH_JSON);
            data.append(SQUARE_OPEN);
            for (TestResult testResult : testResults) {
                jSon.append(gson.toJson(testResult));
                data.append(SQUARE_OPEN);
                data.append(testResults.indexOf(testResult));
                data.append(COMMA);
                data.append(testResult.getTime());
                data.append(SQUARE_CLOSE);
                if(testResults.indexOf(testResult) < testResults.size() - 1) {
                    jSon.append(COMMA);
                    data.append(COMMA);
                }
            }
            jSon.append(SQUARE_CLOSE);
            jSon.append(SEMI_COLON);
            data.append(SQUARE_CLOSE);
            data.append(SEMI_COLON);
            StringBuilder script = new StringBuilder();
            script.append(SCRIPT_START);
            script.append(jSon.toString());
            script.append(GRAPH_DATA);
            script.append(data.toString());
            script.append(GRAPH_VOLUME_DATA);
            script.append(data.toString());
            script.append(GRAPH_SUMMARY_DATA);
            script.append(data.toString());
            //script.append("var flagData = [[3, 'Login as u1']];");
            script.append("var flagData = '';");
            script.append(SCRIPT_END);
            
               S_LOGGER.debug("Test result java script constructed for load test" + script.toString());
            
            getHttpSession().setAttribute(SESSION_GRAPH_SCRIPT, script.toString());

        } catch (Exception e) {
               S_LOGGER.error("Entered into catch block of Quality.loadTestResult()"+ e);
        }

        getHttpRequest().setAttribute(REQ_SELECTED_MENU, APPLICATIONS);
        return "loadTestResult";
    }

    public String performanceTestResultAvail() {
        if (s_debugEnabled) {
            S_LOGGER.debug("Entering Method Quality.performanceTestResultAvail()");
        }

        try {
        	ApplicationInfo appInfo = getApplicationInfo();
       	 	// TO kill the Process
            String baseDir = Utility.getProjectHome()+ appInfo.getAppDirName();
            Utility.killProcess(baseDir, getTestType());
            FrameworkUtil frameworkUtil = FrameworkUtil.getInstance();
            List<String> testResultsTypes = new ArrayList<String>();
            testResultsTypes.add("server");
            testResultsTypes.add("database");
            testResultsTypes.add("webservice");
            for (String testResultsType: testResultsTypes) {
                StringBuilder sb = new StringBuilder(getApplicationHome());
                String performanceReportDir = frameworkUtil.getPerformanceTestReportDir(getApplicationInfo());
                if (StringUtils.isNotEmpty(performanceReportDir) && StringUtils.isNotEmpty(testResultsType)) {
                    Pattern p = Pattern.compile("dir_type");
                    Matcher matcher = p.matcher(performanceReportDir);
                    performanceReportDir = matcher.replaceAll(testResultsType);
                    sb.append(performanceReportDir); 
                }
                File file = new File(sb.toString());
                File[] children = file.listFiles(new XmlNameFileFilter(FILE_EXTENSION_XML));
                if (!ArrayUtils.isEmpty(children)) {
                    setResultFileAvailable(true);
                    break;
                }
            }
        } catch(Exception e) {
            if (s_debugEnabled) {
                S_LOGGER.error("Entered into catch block of Quality.performanceTestResultAvail()"+ FrameworkUtil.getStackTraceAsString(e));
            }
        }

        return SUCCESS;
    }
	
    public String fetchPerformanceTestResultFiles() {
        if (s_debugEnabled) {
            S_LOGGER.debug("Entering Method Quality.fetchPerformanceTestResultFiles()");
        }

        try {
            StringBuilder sb = new StringBuilder(getApplicationHome());
            FrameworkUtil frameworkUtil = FrameworkUtil.getInstance();
            String performanceReportDir = frameworkUtil.getPerformanceTestReportDir(getApplicationInfo());

            if (s_debugEnabled) {
                S_LOGGER.debug("test type performance test Report directory " + performanceReportDir);
            }
            
          //test type will be available 
            if (StringUtils.isNotEmpty(performanceReportDir) && StringUtils.isNotEmpty(getTestResultsType())) {
                Pattern p = Pattern.compile(TEST_DIRECTORY);
                Matcher matcher = p.matcher(performanceReportDir);
                performanceReportDir = matcher.replaceAll(getTestResultsType());
                sb.append(performanceReportDir);
            }
            
            //for android - test type will not be available --- to get device id from result xml
            String performanceTestShowDevice = frameworkUtil.getPerformanceTestShowDevice(getApplicationInfo());
            if (StringUtils.isNotEmpty(performanceTestShowDevice) && Boolean.parseBoolean(performanceTestShowDevice)) {
            	sb.append(performanceReportDir);
            }
            
            if (s_debugEnabled) {
                S_LOGGER.debug("test type performance test Report directory & Type " + sb.toString() + " Type " + getTestResultsType());
            }
            

            File file = new File(sb.toString());
            File[] resultFiles = file.listFiles(new XmlNameFileFilter(FILE_EXTENSION_XML));
            if (!ArrayUtils.isEmpty(resultFiles)) {
                QualityUtil.sortResultFile(resultFiles);
                for (File resultFile : resultFiles) {
                    if (resultFile.isFile()) {
                        testResultFiles.add(resultFile.getName());
                    }
                }
            }
        } catch(PhrescoException e) {
            if (s_debugEnabled) {
                S_LOGGER.error("Entered into catch block of Quality.fetchPerformanceTestResultFiles()"+ FrameworkUtil.getStackTraceAsString(e));
            }
        } catch (PhrescoPomException e) {
            if (s_debugEnabled) {
                S_LOGGER.error("Entered into catch block of Quality.fetchPerformanceTestResultFiles()"+ FrameworkUtil.getStackTraceAsString(e));
            }
        }

        return SUCCESS;
    }
	
    public String fetchPerformanceTestResult() {
        if (s_debugEnabled) {
           S_LOGGER.debug("Entering Method Quality.fetchPerformanceTestResult()");
        }
        
        try {
            ApplicationInfo appInfo = getApplicationInfo();
            String techId = appInfo.getTechInfo().getId();
            if (s_debugEnabled) {
               S_LOGGER.debug("Performance test file name " + getTestResultFile());
            }
            if (StringUtils.isNotEmpty(getTestResultFile())) {
            	String testResultPath = getPerformanceTestResultPath(appInfo, getTestResultFile());
                Document document = getDocument(new File(testResultPath)); 
                Map<String, PerformanceTestResult> performanceTestResultMap = QualityUtil.getPerformanceReport(document, getHttpRequest(), techId, testResultDeviceId); // need to pass tech id and tag name
                setReqAttribute(REQ_TEST_RESULT, performanceTestResultMap);

                Set<String> keySet = performanceTestResultMap.keySet();
                StringBuilder graphData = new StringBuilder("[");
                StringBuilder label = new StringBuilder("[");
                
                List<Float> allMin = new ArrayList<Float>();
                List<Float> allMax = new ArrayList<Float>();
                List<Float> allAvg = new ArrayList<Float>();
                int index = 0;
                for (String key : keySet) {
                    PerformanceTestResult performanceTestResult = performanceTestResultMap.get(key);
                    if (REQ_TEST_SHOW_THROUGHPUT_GRAPH.equals(getShowGraphFor())) {
                        graphData.append(performanceTestResult.getThroughtPut());	//for ThroughtPut
                    } else if (REQ_TEST_SHOW_MIN_RESPONSE_GRAPH.equals(getShowGraphFor())) {
                        graphData.append(performanceTestResult.getMin());	//for min response time
                    } else if (REQ_TEST_SHOW_MAX_RESPONSE_GRAPH.equals(getShowGraphFor())) {
                        graphData.append(performanceTestResult.getMax());	//for max response time
                    } else if (REQ_TEST_SHOW_RESPONSE_TIME_GRAPH.equals(getShowGraphFor())) {
                   	 	graphData.append(performanceTestResult.getAvg());	//for responseTime
                    } else if (REQ_TEST_SHOW_ALL_GRAPH.equals(getShowGraphFor())) {
                    	graphData.append(performanceTestResult.getThroughtPut());	//for ThroughtPut
                    	allMin.add((float)performanceTestResult.getMin()/1000);
                    	allMax.add((float)performanceTestResult.getMax()/1000);
                    	allAvg.add((float) (performanceTestResult.getAvg())/1000);
                    }
                    
                    label.append("'");
                    label.append(performanceTestResult.getLabel());
                    label.append("'");
                    if (index < performanceTestResultMap.size() - 1) {
                        graphData.append(",");
                        label.append(",");
                    }
                    index++;
                }
                label.append("]");
                graphData.append("]");
                setReqAttribute(REQ_GRAPH_DATA, graphData.toString());
                setReqAttribute(REQ_GRAPH_LABEL, label.toString());
                setReqAttribute(REQ_GRAPH_ALL_DATA, allMin +", "+ allAvg +", "+ allMax);
                setReqAttribute(REQ_SHOW_GRAPH, getShowGraphFor());
                setReqAttribute(REQ_APP_INFO, appInfo);
            } else {
                setReqAttribute(REQ_ERROR_TESTSUITE, ERROR_TEST_SUITE);
            }
        } catch (Exception e) {
            setReqAttribute(REQ_ERROR_DATA, ERROR_ANDROID_DATA);
            if (s_debugEnabled) {
               S_LOGGER.error("Entered into catch block of Quality.performanceTestResult()"+ e);
            }
        }

        return SUCCESS;
    }
    
    private String getPerformanceTestResultPath(ApplicationInfo appInfo, String testResultFile) throws PhrescoException, JAXBException, IOException, PhrescoPomException {
        if (s_debugEnabled) {
            S_LOGGER.debug("Entering Method Quality.getFunctionalTestResultPath()");
        }
        
        StringBuilder sb = new StringBuilder(getApplicationHome());
        
        //To change the dir_type based on the selected type
        FrameworkUtil frameworkUtil = FrameworkUtil.getInstance();
        String performanceTestReportDir = frameworkUtil.getPerformanceTestReportDir(appInfo);
        Pattern p = Pattern.compile(TEST_DIRECTORY);
        Matcher matcher = p.matcher(performanceTestReportDir);
        if (StringUtils.isNotEmpty(performanceTestReportDir) && matcher.find()) {
            performanceTestReportDir = matcher.replaceAll(getTestResultsType());
        }
        
        sb.append(performanceTestReportDir);
        sb.append(File.separator);
        sb.append(testResultFile);
        
        return sb.toString();
    }

    public String quality() {
    	try {
    		removeSessionAttribute(getAppId() + SESSION_APPINFO);//To remove the appInfo from the session
    	} catch (Exception e) {
    		if (s_debugEnabled) {
    			S_LOGGER.error("Entered into catch block of Quality.quality()"+ e);
    		} 
    	}

    	return APP_QUALITY;
    }

    // This method returns what type of OS we are using
    public String getOsName() {
        String OS = null;
        String osType = null;
        if(OS == null) {
            OS = System.getProperty(OS_NAME).toLowerCase(); 
        }
        if (OS.indexOf(WINDOWS_CHECK) >= 0) {
            osType = WINDOWS;
        }
        if (OS.indexOf(MAC_CHECK) >= 0) {
            osType = MAC;
        }
        if (OS.indexOf(LINUX_CHECK) >= 0) {
            osType = LINUX;
        }
        return osType;
    }
    
    public String fetchUnitTestReport() throws TransformerException, PhrescoPomException {
        try {
            ApplicationInfo appInfo = getApplicationInfo();
            FrameworkUtil frameworkUtil = FrameworkUtil.getInstance();
            String testSuitePath = "";
            if (StringUtils.isNotEmpty(getTechReport())) {
            	testSuitePath = frameworkUtil.getUnitTestSuitePath(appInfo, getTechReport());
		    } else {
		    	testSuitePath = frameworkUtil.getUnitTestSuitePath(appInfo);
		    }
            
            String testCasePath = "";
            if (StringUtils.isNotEmpty(getTechReport())) {
            	testCasePath = frameworkUtil.getUnitTestCasePath(appInfo, getTechReport());
		    } else {
		    	testCasePath = frameworkUtil.getUnitTestCasePath(appInfo);
		    }
            return testReport(testSuitePath, testCasePath);
        } catch (PhrescoException e) {
            // TODO: handle exception
        }
        
        return null;
    }
    
    public String fetchComponentTestReport() throws TransformerException, PhrescoPomException {
    	try {
    		ApplicationInfo appInfo = getApplicationInfo();
    		FrameworkUtil frameworkUtil = FrameworkUtil.getInstance();
    		String testSuitePath = frameworkUtil.getComponentTestSuitePath(appInfo);
    		String testCasePath = frameworkUtil.getComponentTestCasePath(appInfo);

    		return testReport(testSuitePath, testCasePath);
    	} catch (PhrescoException e) {
    		// TODO: handle exception
    	}

    	return null;
    }
    
    public String fetchFunctionalTestReport() throws TransformerException, PhrescoPomException {
    	try {
    		ProjectInfo projectInfo = getProjectInfo();
    		ApplicationInfo appInfo = projectInfo.getAppInfos().get(0);
    		FrameworkUtil frameworkUtil = FrameworkUtil.getInstance();
    		String testSuitePath = frameworkUtil.getFunctionalTestSuitePath(appInfo);
    		String testCasePath = frameworkUtil.getFunctionalTestCasePath(appInfo);
    		setReqAttribute(REQ_PROJECT_INFO, projectInfo);

    		return testReport(testSuitePath, testCasePath);
    	} catch (PhrescoException e) {
    		// TODO: handle exception
    	}

    	return null;
    }

    private String testReport(String testSuitePath, String testCasePath) throws TransformerException, PhrescoPomException {
    	S_LOGGER.debug("Entering Method Quality.testReport()");
    	try {
    		String testSuitesMapKey = getAppId() + getTestType() + getProjectModule() + getTechReport();
        	Map<String, NodeList> testResultNameMap = testSuiteMap.get(testSuitesMapKey);
            NodeList testSuites = testResultNameMap.get(getTestSuite());
    		if (ALL.equals(getTestSuite())) {
    			Map<String, String> testSuitesResultMap = new HashMap<String, String>();
    			float totalTestSuites = 0;
    			float successTestSuites = 0;
    			float failureTestSuites = 0;
    			float errorTestSuites = 0;
    			// get all nodelist of testType of a project
    			Collection<NodeList> allTestResultNodeLists = testResultNameMap.values();
    			for (NodeList allTestResultNodeList : allTestResultNodeLists) {
        			if (allTestResultNodeList.getLength() > 0 ) {
    	    			List<TestSuite> allTestSuites = getTestSuite(allTestResultNodeList);
    	    			if (CollectionUtils.isNotEmpty(allTestSuites)) {
    		    			for (TestSuite tstSuite : allTestSuites) {
    		    				//testsuite values are set before calling getTestCases value
    							setTestSuite(tstSuite.getName());
    							getTestCases(allTestResultNodeList, testSuitePath, testCasePath);
    				            float tests = 0;
    				            float failures = 0;
    				            float errors = 0;
    				            tests = Float.parseFloat((String) getReqAttribute(REQ_TESTSUITE_TESTS));
    				            failures = Float.parseFloat((String) getReqAttribute(REQ_TESTSUITE_FAILURES));
    				            errors = Float.parseFloat((String) getReqAttribute(REQ_TESTSUITE_ERRORS));
    				            float success = 0;
    				            
    				            if (failures != 0 && errors == 0) {
    				                if (failures > tests) {
    				                    success = failures - tests;
    				                } else {
    				                    success = tests - failures;
    				                }
    				            } else if (failures == 0 && errors != 0) {
    				                if (errors > tests) {
    				                    success = errors - tests;
    				                } else {
    				                    success = tests - errors;
    				                }
    				            } else if (failures != 0 && errors != 0) {
    				                float failTotal = (failures + errors);
    				                if (failTotal > tests) {
    				                    success = failTotal - tests;
    				                } else {
    				                    success = tests - failTotal;
    				                }
    				            } else {
    				            	success = tests;
    				            }
    				            
    				            totalTestSuites = totalTestSuites + tests;
    				            failureTestSuites = failureTestSuites + failures;
    				            errorTestSuites = errorTestSuites + errors;
    				            successTestSuites = successTestSuites + success;
    				            String rstValues = tests + "," + success + "," + failures + "," + errors;
    				            testSuitesResultMap.put(tstSuite.getName(), rstValues);
    						}
    	    			}
        			}
				}
    			setReqAttribute(REQ_ALL_TESTSUITE_MAP, testSuitesResultMap);
				setReqAttribute(REQ_APP_DIR_NAME, getApplicationInfo().getAppDirName());
    			return APP_ALL_TEST_REPORT; 
    		} else {
	            if (testSuites.getLength() > 0 ) {
	            	List<TestCase> testCases = getTestCases(testSuites, testSuitePath, testCasePath);
	            	if (CollectionUtils.isEmpty(testCases)) {
	            		setReqAttribute(REQ_ERROR_TESTSUITE, ERROR_TEST_CASE);
	            	} else {
	            		boolean isClassEmpty = false;
	            		//to check whether class attribute is there or not
	            		for (TestCase testCase : testCases) {
							if (testCase.getTestClass() == null) {
								isClassEmpty = true;
							}
						}
	            		setReqAttribute(IS_CLASS_EMPTY, isClassEmpty);
	            		setReqAttribute(REQ_TESTCASES, testCases);
	            	}
	            }
    		}
        } catch (PhrescoException e) {
        	S_LOGGER.error("Entered into catch block of Quality.testSuite()"+ e);
        }

		return APP_TEST_REPORT;
    }
    
	public String manualTestCase () throws PhrescoException, PhrescoPomException{
	 if (s_debugEnabled) {
	        S_LOGGER.debug("Entering Method Quality.manualTestCase()");
		    }
	 	cacheManager.resetCache();
    	//To get ip of request machine
	 	String requestIp = getHttpRequest().getRemoteAddr();
		setReqAttribute(REQ_REQUEST_IP, requestIp);
		
		ApplicationInfo appInfo = getApplicationInfo();
		setReqAttribute(REQ_APPINFO, appInfo);
		FrameworkUtil frameworkUtil = FrameworkUtil.getInstance();
		setReqAttribute(PATH, frameworkUtil.getManualTestDir(appInfo));
		
		return APP_MANUAL_TEST;
	   }
	 
	public String fetchManualTestSuites() throws PhrescoException, PhrescoPomException  {
		if (s_debugEnabled) {
			S_LOGGER.debug("Entering Method Quality.manualTestCase()");
		}

		Thread t = null;
		try {
			ApplicationInfo appInfo = getApplicationInfo();
			final FrameworkUtil frameworkUtil = FrameworkUtil.getInstance();
			String manualTestDir = frameworkUtil.getManualTestDir(appInfo);
			final StringBuilder sb = new StringBuilder(Utility.getProjectHome())
			.append(appInfo.getAppDirName())
			.append(manualTestDir);
			final List<TestSuite> readManualTestSuiteFile; 
			if (new File(sb.toString()).exists()) {
				readManualTestSuiteFile = frameworkUtil.readManualTestSuiteFile(sb.toString());
				if (CollectionUtils.isNotEmpty(readManualTestSuiteFile)) {
					setAllTestSuite(readManualTestSuiteFile);
					CacheKey key = new CacheKey(appInfo.getTechInfo().getId());
					cacheManager.add(key, readManualTestSuiteFile);
				}
				Runnable runnable = new Runnable() {
					public void run() {
						try {
							for (TestSuite tstSuite : readManualTestSuiteFile) {
								String testSuiteName = tstSuite.getName();
								CacheKey key = new CacheKey(testSuiteName);
								List<com.photon.phresco.commons.model.TestCase> readManualTestCaseFile = frameworkUtil.readManualTestCaseFile(sb.toString(), testSuiteName, null);
								if (CollectionUtils.isNotEmpty(readManualTestCaseFile)) {
									cacheManager.add(key, readManualTestCaseFile);
								}
							}

						} catch (PhrescoException e) {
							S_LOGGER.error("Entered into catch block of Quality.getManualTestSuites()"+ e);
						}
					}
				};

				t = new Thread(runnable);
				t.start();
			} else {
				readManualTestSuiteFile = null;
				setAllTestSuite(readManualTestSuiteFile);
			}
		} catch (Exception e) {
			S_LOGGER.error("Entered into catch block of Quality.fetchManualTestSuites()"+ e);
		} 
		
		return SUCCESS;
	}
  
	public String addManualTestSuite () throws PhrescoException {
		if (s_debugEnabled) {
			S_LOGGER.debug("Entering Method Quality.addManualTestSuite()");
		}
		
		return SUCCESS;
	}
	
	public String addManualTestCase () throws PhrescoException, PhrescoPomException {
		if (s_debugEnabled) {
			S_LOGGER.debug("Entering Method Quality.addManualTestCase()");
		}
		String testScenario = getType();
		String testScenarioId = getTestId();
		if (StringUtils.isNotEmpty(testScenarioId)) {
			CacheKey testSuitekey = new CacheKey(testScenario);
			List<com.photon.phresco.commons.model.TestCase> readManualTestCaseFile = (List<com.photon.phresco.commons.model.TestCase>) cacheManager.get(testSuitekey);
			if (CollectionUtils.isNotEmpty(readManualTestCaseFile)) {
				for (com.photon.phresco.commons.model.TestCase testCase : readManualTestCaseFile) {
					if (testCase.getFeatureId().equals(testScenarioId)) {
						setReqAttribute(REQ_TESTCASE, testCase);
					}
				}
			} 
		}
		setReqAttribute(REQ_TESTSUITE, testScenario);
		return SUCCESS;
	}
	
	public String validateform() throws PhrescoException, PhrescoPomException {
		if (s_debugEnabled) {
			S_LOGGER.debug("Entering Method Quality.validateform()");
		}
		String testSuiteName = getTestScenarioName();
		
		CacheKey testSuitekey = new CacheKey(testSuiteName);
		List<com.photon.phresco.commons.model.TestCase> readManualTestCaseFile = (List<com.photon.phresco.commons.model.TestCase>) cacheManager.get(testSuitekey);
		boolean hasError = false;
		if (!getFromTab().equals(EDIT)) {
			if(StringUtils.isNotEmpty(getTestCaseId()) && CollectionUtils.isNotEmpty(readManualTestCaseFile)) {
				for(com.photon.phresco.commons.model.TestCase testCase : readManualTestCaseFile) {
					if(testCase.getTestCaseId().equalsIgnoreCase(getTestCaseId())) {
						setTestCaseIdError(getText(ERROR_TESTCASE_ID_EXISTS));
						hasError = true;
						break;
					}
				}
			} else if(StringUtils.isEmpty(getTestCaseId())) {
				setTestCaseIdError(getText(ERROR_TESTCASE_ID_MISSING));
				hasError = true;
	        }
			
			if (StringUtils.isEmpty(getFeatureId())) {
				setFeatureIdError(getText(ERROR_FEATURE_ID_MISSING));
				hasError = true;
			}
		}
		if (hasError) {
            setErrorFound(true);
        }
		
		return SUCCESS;
	}
	
	public String validateTestSuite() throws PhrescoException {
		ApplicationInfo appInfo = getApplicationInfo();
		CacheKey key = new CacheKey(appInfo.getTechInfo().getId());
		List<TestSuite> readManualTestCaseFile = (List<TestSuite>) cacheManager.get(key);
		boolean hasError = false;
		if(StringUtils.isNotEmpty(getTestScenarioName())) {
			for (TestSuite testSuite : readManualTestCaseFile) {
				if(testSuite.getName().equalsIgnoreCase(getTestScenarioName())) {
					setNameError(getText(ERROR_TEST_SCENARIO_NAME_EXISTS));
					hasError = true;
					break;
				}
			}
		} else {
			setNameError(getText(ERROR_TEST_SCENARIO_NAME_MISSING));
			hasError = true;
		}
		
		if (hasError) {
            setErrorFound(true);
        }
		
		return SUCCESS;
	}
	public String showManualTestPopUp () throws PhrescoException {
		if (s_debugEnabled) {
			S_LOGGER.debug("Entering Method Quality.showManualTestPopUp()");
		}

		return SUCCESS;
	}
	
	public String saveTestSuites () throws PhrescoException, PhrescoPomException {
		if (s_debugEnabled) {
			S_LOGGER.debug("Entering Method Quality.saveTestSuites()");
		}
		ApplicationInfo appInfo = getApplicationInfo();
		setReqAttribute(REQ_APPINFO, appInfo);
		FrameworkUtil frameworkUtil = FrameworkUtil.getInstance();
		String path = frameworkUtil.getManualTestDir(appInfo);
		setReqAttribute(PATH, path);
		StringBuilder sb = new StringBuilder(Utility.getProjectHome())
		.append(appInfo.getAppDirName())
		.append(path);
		String cellValue[] = {"","",getTestScenarioName(),getTotalSuccess(), getTotalFailures(),"","","",getTotalTestCases(),getTestCoverage(),"","",""};
		FrameworkUtil.addNew(sb.toString(), getTestScenarioName(), cellValue);
		return MANUAL;
	}
	
	public String saveTestCases () throws PhrescoException, PhrescoPomException {
		if (s_debugEnabled) {
			S_LOGGER.debug("Entering Method Quality.saveTestCases()");
		}
		ApplicationInfo appInfo = getApplicationInfo();
		setReqAttribute(REQ_APPINFO, appInfo);
		FrameworkUtil frameworkUtil = FrameworkUtil.getInstance();
		String path = frameworkUtil.getManualTestDir(appInfo);
		setReqAttribute(PATH, path);
		StringBuilder sb = new StringBuilder(Utility.getProjectHome())
		.append(appInfo.getAppDirName())
		.append(path);
		if (!getFromTab().equals(EDIT)) {
			String cellValue[] = {"",getFeatureId(),"",getTestCaseId(),getTestDescription(),getTestSteps(),"","",getExpectedResult(),getActualResult(),getStatus(),"","",getBugComment()};
			FrameworkUtil.addNewTestCase(sb.toString(),getTestScenarioName(),cellValue, getStatus());
		} else {
			com.photon.phresco.commons.model.TestCase testCase =  new com.photon.phresco.commons.model.TestCase();
			testCase.setTestCaseId(getTestCaseId());
			testCase.setSteps(getTestSteps());
			testCase.setExpectedResult(getExpectedResult());
			testCase.setActualResult(getActualResult());
			testCase.setStatus(getStatus());
			testCase.setBugComment(getBugComment());
			frameworkUtil.readManualTestCaseFile(sb.toString(), getTestScenarioName(), testCase);
		}
		return MANUAL;
	}
	
	public String readManualTestCases () {
		if (s_debugEnabled) {
			S_LOGGER.debug("Entering Method Quality.readManualTestCasesFromSheet()");
		}
		try {
			CacheKey key = new CacheKey(getTestSuitName());
			List<com.photon.phresco.commons.model.TestCase> readManualTestCaseFile = (List<com.photon.phresco.commons.model.TestCase>) cacheManager.get(key);
			if (CollectionUtils.isNotEmpty(readManualTestCaseFile)) {
				setAllTestCases(readManualTestCaseFile);
			} else {
				ApplicationInfo appInfo = getApplicationInfo();
				FrameworkUtil frameworkUtil = FrameworkUtil.getInstance();
				String manualTestDir = frameworkUtil.getManualTestDir(appInfo);
				StringBuilder sb = new StringBuilder(Utility.getProjectHome())
				.append(appInfo.getAppDirName())
				.append(manualTestDir);
				CacheKey testSuitekey = new CacheKey(getTestSuitName());
				List<com.photon.phresco.commons.model.TestCase> readTestCase = frameworkUtil.readManualTestCaseFile(sb.toString(), getTestSuitName(), null);
				if (CollectionUtils.isNotEmpty(readTestCase)) {
					cacheManager.add(testSuitekey, readTestCase);
					setAllTestCases(readTestCase);
				}
			}
			
		} catch (Exception e) {
			S_LOGGER.error("Entered into catch block of Quality.readManualTestCases()"+ e);
		}

		return SUCCESS;
	}
 
	 public class XmlNameFileFilter implements FilenameFilter {
        private String filter_;
        public XmlNameFileFilter(String filter) {
            filter_ = filter;
        }

        public boolean accept(File dir, String name) {
            return name.endsWith(filter_);
        }
    }
    
    public class FileNameFileFilter implements FilenameFilter {
        private String filter_;
        private String startWith_;
        public FileNameFileFilter(String filter, String startWith) {
            filter_ = filter;
            startWith_ = startWith;
        }

        public boolean accept(File dir, String name) {
            return name.endsWith(filter_) && name.startsWith(startWith_);
        }
    }

	public String devices() {
		S_LOGGER.debug("Entering Method Quality.devices()");
        try {
            String testResultFile = getHttpRequest().getParameter(REQ_TEST_RESULT_FILE);
            if (!testResultFile.equals("null")) {
            	String testResultPath = getTestResultPath(getApplicationInfo(), testResultFile);
                Document document = getDocument(new File(testResultPath)); 
        		deviceNames = QualityUtil.getDeviceNames(document);
            }
        } catch(Exception e) {
        	S_LOGGER.error("Entered into catch block of Quality.devices()"+ FrameworkUtil.getStackTraceAsString(e));
        }
        return SUCCESS;
	}
	
    public String showGeneratePdfPopup() {
        S_LOGGER.debug("Entering Method Quality.printAsPdfPopup()");
        try {
        	boolean isReportAvailable = false;
			ApplicationInfo appInfo = getApplicationInfo();
			setReqAttribute(REQ_APPINFO, appInfo);
			String url = "";
			FrameworkUtil frameworkUtil = FrameworkUtil.getInstance();
			url = frameworkUtil.getSonarURL();
			// is sonar report available
			
            isReportAvailable = isSonarReportAvailable(frameworkUtil, appInfo);
            S_LOGGER.debug("code validation Report status ==>" + isReportAvailable);
            // is test report available
    	    if (!isReportAvailable) {
    	    	isReportAvailable = isTestReportAvailable(frameworkUtil, appInfo);
    	    	S_LOGGER.debug("Test Report status ==>" + isReportAvailable);
    	    }
			setReqAttribute(REQ_APP_ID, getAppId());
			setReqAttribute(REQ_PROJECT_ID, getProjectId());
			setReqAttribute(REQ_CUSTOMER_ID, getCustomerId());
			setReqAttribute(REQ_FROM_PAGE, getFromPage());
			setReqAttribute(REQ_SONAR_URL, url);
			setReqAttribute(CHECK_REPORT_AVAILABILITY, isReportAvailable);
        	List<String> existingPDFs = getExistingPDFs();
    		if (existingPDFs != null) {
    			setReqAttribute(REQ_PDF_REPORT_FILES, existingPDFs);
    		}
        } catch (Exception e) {
            S_LOGGER.error("Entered into catch block of Quality.printAsPdfPopup()"+ e);
        }
        setReqAttribute(REQ_TEST_TYPE, fromPage);
        return SUCCESS;
    }

	private boolean isSonarReportAvailable(FrameworkUtil frameworkUtil, ApplicationInfo appInfo) throws PhrescoException, MalformedURLException, JAXBException,
			IOException, PhrescoPomException {
		if (s_debugEnabled) {
            S_LOGGER.debug("Entering Method Quality.isSonarReportAvailable()");
        }
		boolean isSonarReportAvailable = false;
		
		try {
			String isIphone = frameworkUtil.isIphoneTagExists(appInfo);
			if (StringUtils.isEmpty(isIphone)) {
				FrameworkConfiguration frameworkConfig = PhrescoFrameworkFactory.getFrameworkConfig();
				String serverUrl = frameworkUtil.getSonarURL();
				String sonarReportPath = frameworkConfig.getSonarReportPath().replace(FORWARD_SLASH + SONAR, "");
				serverUrl = serverUrl + sonarReportPath;
				PomProcessor processor = frameworkUtil.getPomProcessor(appInfo);
				Modules pomModules = processor.getPomModule();
				List<String> modules = null;
				if (pomModules != null) {
					modules = pomModules.getModule();
				}
				
				// check multimodule or not
				List<String> sonarProfiles = frameworkUtil.getSonarProfiles(appInfo);
				if (CollectionUtils.isEmpty(sonarProfiles)) {
					sonarProfiles.add(SONAR_SOURCE);
				}
				sonarProfiles.add(FUNCTIONAL);
				boolean isSonarUrlAvailable = false;
				if (CollectionUtils.isNotEmpty(modules)) {
					for (String module : modules) {
						for (String sonarProfile : sonarProfiles) {
							isSonarUrlAvailable = checkSonarModuleUrl(sonarProfile, serverUrl, module, frameworkUtil, appInfo);
							
							if (isSonarUrlAvailable) {
								isSonarReportAvailable = true;
								break;
							}
						}
					}
				} else {
					for (String sonarProfile : sonarProfiles) {
						isSonarUrlAvailable = checkSonarUrl(sonarProfile, serverUrl, frameworkUtil, appInfo);
						if(isSonarUrlAvailable) {
							isSonarReportAvailable = true;
							break;
						}
					}
				}
			} else {
				StringBuilder sb = new StringBuilder(Utility.getProjectHome())
				.append(appInfo.getAppDirName())
				.append(File.separatorChar)
				.append(DO_NOT_CHECKIN_DIR)
				.append(File.separatorChar)
				.append(STATIC_ANALYSIS_REPORT);
				File indexPath = new File(sb.toString());
				if (indexPath.exists() && indexPath.isDirectory()) {
					File[] listFiles = indexPath.listFiles();
			    	for (int i = 0; i < listFiles.length; i++) {
						File file = listFiles[i];
						File htmlFileCheck = new File(file, INDEX_HTML);
						if (htmlFileCheck.exists()) {
							isSonarReportAvailable = true;
						} 
					} 
				}
			}
		} catch (Exception e) {
			S_LOGGER.error("Entered inside catch block of Quality.isSonarReportAvailable()!!!");
			e.printStackTrace();
		}
		return isSonarReportAvailable;
	}

	private boolean checkSonarModuleUrl(String sonarProfile, String serverUrl, String module, FrameworkUtil frameworkUtil, ApplicationInfo appInfo) throws PhrescoException, PhrescoPomException {
		if (s_debugEnabled) {
            S_LOGGER.debug("Entering Method Quality.checkSonarModuleUrl()");
        }
		boolean isSonarReportAvailable = false;
		try {
			if(StringUtils.isNotEmpty(module)) {
				StringBuilder builder = new StringBuilder(Utility.getProjectHome());
				builder.append(appInfo.getAppDirName());
				builder.append(File.separatorChar);
				
				if (!FUNCTIONALTEST.equals(sonarProfile)) {
					builder.append(module);
                }
				if (StringUtils.isNotEmpty(sonarProfile) && FUNCTIONALTEST.equals(sonarProfile)) {
					builder.append(frameworkUtil.getFunctionalTestDir(appInfo));
                }
				
	            builder.append(File.separatorChar);
	        	builder.append(Utility.getPomFileName(appInfo));
	        	File pomPath = new File(builder.toString());
	        	StringBuilder sbuild = new StringBuilder();
	        	if (pomPath.exists()) {
	        		PomProcessor pomProcessor = new PomProcessor(pomPath);
		        	String groupId = pomProcessor.getModel().getGroupId();
		        	String artifactId = pomProcessor.getModel().getArtifactId();
		        	
		        	sbuild.append(groupId);
		        	sbuild.append(COLON);
		        	sbuild.append(artifactId);
		        	if (!REQ_SRC.equals(sonarProfile)) {
		        		sbuild.append(COLON);
		        		sbuild.append(sonarProfile);
		        	}
	        	
					S_LOGGER.debug("serverUrl with report path " + serverUrl);
		        	String artifact = sbuild.toString();
		        	String url = serverUrl + artifact;
		        	if (isSonarAlive(url)) {
		        		isSonarReportAvailable = true;
		        	}
	        	}
			}
		} catch (PhrescoException e) {
			S_LOGGER.error("Entered inside PhrescoException catch block of Quality.checkSonarModuleUrl() !!!");
			e.printStackTrace();
		} catch (PhrescoPomException e) {
			S_LOGGER.error("Entered inside PhrescoPomException catch block of Quality.checkSonarModuleUrl() !!!");
			e.printStackTrace();
		}
		return isSonarReportAvailable;
	}
	
	private boolean checkSonarUrl(String sonarProfile, String serverUrl, FrameworkUtil frameworkUtil, ApplicationInfo appInfo) {
		if (s_debugEnabled) {
            S_LOGGER.debug("Entering Method Quality.checkSonarUrl()");
        }
		boolean isSonarReportAvailable = false;
		try {
			if (StringUtils.isNotBlank(sonarProfile)) {
				//get sonar report
				StringBuilder builder = new StringBuilder(Utility.getProjectHome());
				builder.append(appInfo.getAppDirName());
				builder.append(File.separatorChar);
				
				if (StringUtils.isNotEmpty(sonarProfile) && FUNCTIONALTEST.equals(sonarProfile)) {
					builder.append(frameworkUtil.getFunctionalTestDir(appInfo));
                }
				
	            builder.append(File.separatorChar);
	        	builder.append(Utility.getPomFileName(appInfo));
	        	File pomPath = new File(builder.toString());
	        	StringBuilder sbuild = new StringBuilder();
	        	if (pomPath.exists()) {
		        	PomProcessor pomProcessor = new PomProcessor(pomPath);
		        	String groupId = pomProcessor.getModel().getGroupId();
		        	String artifactId = pomProcessor.getModel().getArtifactId();
		        	
		        	sbuild.append(groupId);
		        	sbuild.append(COLON);
		        	sbuild.append(artifactId);
		        	
		        	if (!SOURCE_DIR.equals(sonarProfile)) {
		        		sbuild.append(COLON);
		        		sbuild.append(sonarProfile);
		        	}
	        	}
	        	
	        	String artifact = sbuild.toString();
	        	String url = serverUrl + artifact;
	        	S_LOGGER.debug("sonarUrl... " + url);
	        	if (isSonarAlive(url)) {
	        		isSonarReportAvailable = true;
	        	}
			}
		} catch (Exception e) {
			S_LOGGER.error("Entered inside check block of Quality.checkSonarUrl()!!!");
			e.printStackTrace();
		}
		return isSonarReportAvailable;
	}

	private boolean isSonarAlive(String url) {
		boolean xmlResultsAvailable = false;
	    try {
			URL sonarURL = new URL(url);
			HttpURLConnection connection = null;
	    	connection = (HttpURLConnection) sonarURL.openConnection();
	    	int responseCode = connection.getResponseCode();
	    	if (responseCode != 200) {
	    		xmlResultsAvailable = false;
	        } else {
	        	xmlResultsAvailable = true;
	        }
	    } catch(Exception e) {
	    	xmlResultsAvailable = false;
	    }
	    return xmlResultsAvailable;
	}
	
	private boolean isTestReportAvailable(FrameworkUtil frameworkUtil, ApplicationInfo appInfo) throws PhrescoPomException, PhrescoException {
		if (s_debugEnabled) {
            S_LOGGER.debug("Entering Method Quality.isTestReportAvailable");
        }
		boolean xmlResultsAvailable = false;
		File file = null;
		StringBuilder sb = new StringBuilder(Utility.getProjectHome());
		sb.append(appInfo.getAppDirName());
		try {
			String isIphone = frameworkUtil.isIphoneTagExists(appInfo);
			// unit xml check
			if(!xmlResultsAvailable) {
				List<String> moduleNames = new ArrayList<String>();
				PomProcessor processor = frameworkUtil.getPomProcessor(appInfo);
				Modules pomModules = processor.getPomModule();
				List<String> modules = null;
				// check multimodule or not
				if(pomModules != null) {
					modules = pomModules.getModule();
					for (String module : modules) {
						if (StringUtils.isNotEmpty(module)) {
							moduleNames.add(module);
						}
					}
					for (String moduleName : moduleNames) {
						String moduleXmlPath = sb.toString() + File.separator + moduleName + frameworkUtil.getUnitTestReportDir(appInfo);
						file = new File(moduleXmlPath);
						xmlResultsAvailable = xmlFileSearch(file, xmlResultsAvailable);
					}
				} else {
					if (StringUtils.isNotEmpty(isIphone)) {
						String unitIphoneTechReportDir = frameworkUtil.getUnitTestReportDir(appInfo);
						file = new File(sb.toString() + unitIphoneTechReportDir);
					} else {
						String unitTechReports = frameworkUtil.getUnitTestReportOptions(appInfo);
						if (StringUtils.isEmpty(unitTechReports)) {
							file = new File(sb.toString() + frameworkUtil.getUnitTestReportDir(appInfo));
						} else {
							List<String> unitTestTechs = Arrays.asList(unitTechReports.split(","));
							for (String unitTestTech : unitTestTechs) {
								unitTechReports = frameworkUtil.getUnitTestReportDir(appInfo, unitTestTech);
								if (StringUtils.isNotEmpty(unitTechReports)) {
									file = new File(sb.toString() + unitTechReports);
								}
							}
						}
					}
					xmlResultsAvailable = xmlFileSearch(file, xmlResultsAvailable);
				}
				S_LOGGER.debug("check unit Test xml Availablity ==>" + xmlResultsAvailable);
			}
		
			// functional xml check 
			if(!xmlResultsAvailable) {
				file = new File(sb.toString() + frameworkUtil.getFunctionalTestReportDir(appInfo));
				xmlResultsAvailable = xmlFileSearch(file, xmlResultsAvailable);
	            S_LOGGER.debug("check functional Test xml Availablity ==>" + xmlResultsAvailable);
			}
			
			// performance xml check
			if (StringUtils.isEmpty(isIphone)) {
				if(!xmlResultsAvailable) {
		            performanceTestResultAvail();
		        	if(isResultFileAvailable()) {
		        		xmlResultsAvailable = true;
		        	}
		        	S_LOGGER.debug("check performance Test xml Availablity ==>" + xmlResultsAvailable);
	            }
			}
            
			// load xml check
			if (StringUtils.isEmpty(isIphone)) {
				if(!xmlResultsAvailable) {
	            	loadTestResultAvail();
	            	if(isResultFileAvailable()) {
		        		xmlResultsAvailable = true;
		        	}
		            S_LOGGER.debug("check load Test xml Availablity ==>" + xmlResultsAvailable);
	            }
			}
		} catch (PhrescoException e) {
			e.printStackTrace();
			S_LOGGER.error("Entered inside the catch block of Quality.isTestReportAvailable() method !!!");
		}
		return xmlResultsAvailable;
	}

	private boolean xmlFileSearch(File file, boolean xmlResultsAvailable) {
		File[] children = file.listFiles(new XmlNameFileFilter(FILE_EXTENSION_XML));
        if(children != null && children.length > 0) {
        	xmlResultsAvailable = true;
        }
		return xmlResultsAvailable;
	}

	private List<String> getExistingPDFs() throws PhrescoException {
		S_LOGGER.debug("Entering Method Quality.getExistingPDFs()");
		List<String> pdfFiles = new ArrayList<String>();
		// popup showing list of pdf's already created
		String pdfDirLoc = "";
		String fileFilterName = "";
		if (StringUtils.isEmpty(fromPage) || FROMPAGE_ALL.equals(fromPage)) {
			pdfDirLoc = Utility.getProjectHome() + getApplicationInfo().getAppDirName() + File.separator + DO_NOT_CHECKIN_DIR + File.separator + ARCHIVES + File.separator + CUMULATIVE;
			fileFilterName = getApplicationInfo().getAppDirName();
		} else {
			pdfDirLoc = Utility.getProjectHome() + getApplicationInfo().getAppDirName() + File.separator + DO_NOT_CHECKIN_DIR + File.separator + ARCHIVES + File.separator + fromPage;
			fileFilterName = fromPage;
		}
		File pdfFileDir = new File(pdfDirLoc);
		if(pdfFileDir.isDirectory()) {
		    File[] children = pdfFileDir.listFiles(new FileNameFileFilter(DOT + PDF, fileFilterName));
		    QualityUtil util = new QualityUtil();
		    if(children != null) {
		    	util.sortResultFile(children);
		    }
			for (File child : children) {
				String fileNameWithType = child.getName().replace(DOT + PDF, "").replace(fileFilterName + UNDERSCORE, "");
				String[] fileWithType = fileNameWithType.split(UNDERSCORE);
				pdfFiles.add(fileWithType[0] + UNDERSCORE + fileWithType[1]);
			}
		}
		return pdfFiles;
	}
    
    public String printAsPdf () {
        S_LOGGER.debug("Entering Method Quality.printAsPdf()");
        try {
			ApplicationManager applicationManager = PhrescoFrameworkFactory.getApplicationManager();
			ProjectInfo projectInfo = getProjectInfo();
			
			ApplicationInfo applicationInfo = getApplicationInfo();
			MojoProcessor mojo = new MojoProcessor(new File(getPhrescoPluginInfoFilePath(PHASE_PDF_REPORT)));
			List<Parameter> parameters = getMojoParameters(mojo, PHASE_PDF_REPORT);
			String sonarUrl = (String) getReqAttribute(REQ_SONAR_URL);
	        if (CollectionUtils.isNotEmpty(parameters)) {
	            for (Parameter parameter : parameters) {
	            	String key = parameter.getKey();
	            	if (REQ_REPORT_TYPE.equals(key)) {
	            		parameter.setValue(reportDataType);
	            	} else if (REQ_TEST_TYPE.equals(key)) {
	            		if (StringUtils.isEmpty(fromPage)) {
	            			setFromPage(FROMPAGE_ALL);
	            		}
	            		parameter.setValue(getFromPage());
	            	} else if (REQ_SONAR_URL.equals(key)) {
	            		parameter.setValue(sonarUrl);
	            	} else if ("logo".equals(key)) {
	            		parameter.setValue(getLogoImageString());
	            	} else if ("theme".equals(key)) {
	            		parameter.setValue(getThemeColorJson());
	            	}
	            }
	        }
	        mojo.save();
	        
			List<String> buildArgCmds = getMavenArgCommands(parameters);
			buildArgCmds.add(HYPHEN_N);
			String pomFileName = Utility.getPomFileName(applicationInfo);
			if(!Constants.POM_NAME.equals(pomFileName)) {
				buildArgCmds.add(Constants.HYPHEN_F);
				buildArgCmds.add(pomFileName);
			}
			String workingDirectory = getAppDirectoryPath(applicationInfo);
			BufferedReader reader = applicationManager.performAction(projectInfo, ActionType.PDF_REPORT, buildArgCmds, workingDirectory);
			String line;
			line = reader.readLine();
			while (line != null) {
				line = reader.readLine();
				System.out.println("Restart Start Console : " + line);
			}
			setReqAttribute(REQ_APPINFO, applicationInfo);
			setReqAttribute(REQ_FROM_PAGE, getFromPage());
            setReqAttribute(REQ_REPORT_STATUS, getText(SUCCESS_REPORT_STATUS));
        } catch (Exception e) {
        	S_LOGGER.error("Entered into catch block of Quality.printAsPdf()"+ e);
        	if (e.getLocalizedMessage().contains(getText(ERROR_REPORT_MISSISNG_FONT_MSG))) {
        		setReqAttribute(REQ_REPORT_STATUS, getText(ERROR_REPORT_MISSISNG_FONT));
        	} else {
        		setReqAttribute(REQ_REPORT_STATUS, getText(ERROR_REPORT_STATUS));
        	}
        }
        return showGeneratePdfPopup();
    }

    public String downloadReport() {
    	if (s_debugEnabled) {
    		S_LOGGER.debug("Entering Method Quality.downloadReport()");
    	}
        try {
        	String fromPage = getReqParameter(REQ_FROM_PAGE);
        	String pdfLOC = "";
        	String archivePath = getApplicationHome() + File.separator + DO_NOT_CHECKIN_DIR + File.separator + ARCHIVES + File.separator;
        	if ((FrameworkConstants.ALL).equals(fromPage)) {
        		pdfLOC = archivePath + CUMULATIVE + File.separator + getApplicationInfo().getAppDirName() + UNDERSCORE + reportFileName + DOT + PDF;
        	} else {
        		pdfLOC = archivePath + fromPage + File.separator + fromPage + UNDERSCORE + reportFileName + DOT + PDF;
        	}
            File pdfFile = new File(pdfLOC);
            if (pdfFile.isFile()) {
    			fileInputStream = new FileInputStream(pdfFile);
    			fileName = reportFileName.split(UNDERSCORE)[1];
            }
        } catch (Exception e) {
        	if (s_debugEnabled) {
        		S_LOGGER.error("Entered into catch block of Quality.downloadReport()" + e);
        	}	
        }
        return SUCCESS;
    }
    
    public String deleteReport() {
    	if (s_debugEnabled) {
    		S_LOGGER.debug("Entering Method Quality.deleteReport()");
    	}	
        try {
        	String fromPage = getReqParameter(REQ_FROM_PAGE);
        	String pdfLOC = "";
        	String archivePath = getApplicationHome() + File.separator + DO_NOT_CHECKIN_DIR + File.separator + ARCHIVES + File.separator;
        	if ((FrameworkConstants.ALL).equals(fromPage)) {
        		pdfLOC = archivePath + CUMULATIVE + File.separator + getApplicationInfo().getAppDirName() + UNDERSCORE + reportFileName + DOT + PDF;
        	} else {
        		pdfLOC = archivePath + fromPage + File.separator + fromPage + UNDERSCORE + reportFileName + DOT + PDF;
        	}
            File pdfFile = new File(pdfLOC);
            if (pdfFile.isFile()) {
            	boolean reportDeleted = pdfFile.delete();
            	if (s_debugEnabled) {
            		S_LOGGER.info("Report deleted " + reportDeleted);
            	}	
            	if(reportDeleted) {
            		setReqAttribute(REQ_REPORT_DELETE_STATUS, getText(SUCCESS_REPORT_DELETE_STATUS));
            	} else {
            		setReqAttribute(REQ_REPORT_DELETE_STATUS, getText(ERROR_REPORT_DELETE_STATUS));
            	}
            }
        } catch (Exception e) {
        	if (s_debugEnabled) {
        		S_LOGGER.error("Entered into catch block of Quality.downloadReport()" + e);
        	}	
        }
        return showGeneratePdfPopup();
    }
    
    public List<SettingsInfo> getServerSettings() {
        return serverSettings;
    }

    public void setServerSettings(List<SettingsInfo> serverSettings) {
        this.serverSettings = serverSettings;
    }

    public String getTestSuite() {
        return testSuite;
    }

    public void setTestSuite(String testSuite) {
        this.testSuite = testSuite;
    }

    public String getTests() {
        return tests;
    }

    public void setTests(String tests) {
        this.tests = tests;
    }

    public String getErrs() {
        return errs;
    }

    public void setErrs(String errs) {
        this.errs = errs;
    }

    public String getFailures() {
        return failures;
    }

    public void setFailures(String failures) {
        this.failures = failures;
    }


    public String getSetting() {
        return setting;
    }

    public void setSetting(String setting) {
        this.setting = setting;
    }

    public String getProjectCode() {
        return projectCode;
    }

    public void setProjectCode(String projectCode) {
        this.projectCode = projectCode;
    }

    public String getTestType() {
        return testType;
    }

    public void setTestType(String testType) {
        this.testType = testType;
    }

    public String getTestResultFile() {
        return testResultFile;
    }

    public void setTestResultFile(String testResultFile) {
        this.testResultFile = testResultFile;
    }

    public String getSettingType() {
        return settingType;
    }

    public void setSettingType(String settingType) {
        this.settingType = settingType;
    }

    public String getSettingName() {
        return settingName;
    }

	public void setSettingName(String settingName) {
        this.settingName = settingName;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getCaption() {
        return caption;
    }

    public List<String> getTestResultFiles() {
        return testResultFiles;
    }

    public void setTestResultFiles(List<String> testResultFiles) {
        this.testResultFiles = testResultFiles;
    }

    public String getTestResultsType() {
        return testResultsType;
    }

    public void setTestResultsType(String testResultsType) {
        this.testResultsType = testResultsType;
    }

    public String getSonarUrl() {
		return sonarUrl;
	}

	public void setSonarUrl(String sonarUrl) {
		this.sonarUrl = sonarUrl;
	}

	public String getTestName() {
		return testName;
	}

	public void setTestName(String testName) {
		this.testName = testName;
	}

	public boolean isResultFileAvailable() {
		return resultFileAvailable;
	}

	public void setResultFileAvailable(boolean isAtleastOneFileAvail) {
		this.resultFileAvailable = isAtleastOneFileAvail;
	}
    
	public String getShowError() {
		return showError;
	}

	public void setShowError(String showError) {
		this.showError = showError;
	}

	public String getTestResultDeviceId() {
		return testResultDeviceId;
	}

	public void setTestResultDeviceId(String testResultDeviceId) {
		this.testResultDeviceId = testResultDeviceId;
	}

	public Map<String, String> getDeviceNames() {
		return deviceNames;
	}

	public void setDeviceNames(Map<String, String> deviceNames) {
		this.deviceNames = deviceNames;
	}

	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}
	
	public List<String> getBuildInfoEnvs() {
		return buildInfoEnvs;
	}

	public void setBuildInfoEnvs(List<String> buildInfoEnvs) {
		this.buildInfoEnvs = buildInfoEnvs;
	}
	
	public String getHideLog() {
		return hideLog;
	}

	public void setHideLog(String hideLog) {
		this.hideLog = hideLog;
	}

	public String getSdk() {
		return sdk;
	}

	public void setSdk(String sdk) {
		this.sdk = sdk;
	}
	
    public String getFromPage() {
		return fromPage;
	}

	public void setFromPage(String fromPage) {
		this.fromPage = fromPage;
	}
	
    public String getProjectModule() {
		return projectModule;
	}

	public void setProjectModule(String projectModule) {
		this.projectModule = projectModule;
	}
	
	public List<TestSuite> getTestSuites() {
		return testSuites;
	}

	public void setTestSuites(List<TestSuite> testSuites) {
		this.testSuites = testSuites;
	}
	
	public boolean getValidated() {
		return validated;
	}

	public void setValidated(boolean validated) {
		this.validated = validated;
	}
	
	public String getTestModule() {
		return testModule;
	}

	public void setTestModule(String testModule) {
		this.testModule = testModule;
	}

    public String getTechReport() {
		return techReport;
	}

	public void setTechReport(String techReport) {
		this.techReport = techReport;
	}
  
	public PerformanceDetails getPerformanceDetails() {
		return performanceDetails;
	}

	public void setPerformanceDetails(PerformanceDetails performanceDetails) {
		this.performanceDetails = performanceDetails;
	}
	
	public List<String> getTestSuiteNames() {
		return testSuiteNames;
	}

	public void setTestSuiteNames(List<String> testSuiteNames) {
		this.testSuiteNames = testSuiteNames;
	}
	
	public String getShowDebug() {
		return showDebug;
	}

	public void setShowDebug(String showDebug) {
		this.showDebug = showDebug;
	}

    public String getReportName() {
        return reportName;
    }

    public void setReportName(String reportName) {
        this.reportName = reportName;
    }

    public String getReoportLocation() {
        return reoportLocation;
    }

    public void setReoportLocation(String reoportLocation) {
        this.reoportLocation = reoportLocation;
    }

	public InputStream getFileInputStream() {
		return fileInputStream;
	}

	public void setFileInputStream(InputStream fileInputStream) {
		this.fileInputStream = fileInputStream;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getReportFileName() {
		return reportFileName;
	}

	public void setReportFileName(String reportFileName) {
		this.reportFileName = reportFileName;
	}

	public String getReportDataType() {
		return reportDataType;
	}

	public void setReportDataType(String reportDataType) {
		this.reportDataType = reportDataType;
	}
    
	public String getJarLocation() {
		return jarLocation;
	}

	public void setJarLocation(String jarLocation) {
		this.jarLocation = jarLocation;
	}

	public String getTestAgainst() {
		return testAgainst;
	}

	public void setTestAgainst(String testAgainst) {
		this.testAgainst = testAgainst;
	}

	public String getIosTestType() {
		return iosTestType;
	}

	public void setIosTestType(String iosTestType) {
		this.iosTestType = iosTestType;
	}

	public String getResolution() {
		return resolution;
	}

	public void setResolution(String resolution) {
		this.resolution = resolution;
	}

    public boolean isConnectionAlive() {
        return connectionAlive;
    }

    public void setConnectionAlive(boolean connectionAlive) {
        this.connectionAlive = connectionAlive;
    }

    public String getShowGraphFor() {
        return showGraphFor;
    }

    public void setShowGraphFor(String showGraphFor) {
        this.showGraphFor = showGraphFor;
    }

	public boolean isUpdateCache() {
		return updateCache;
	}

	public void setUpdateCache(boolean updateCache) {
		this.updateCache = updateCache;
	}

	public void setResultJson(String resultJson) {
		this.resultJson = resultJson;
	}

	public String getResultJson() {
		return resultJson;
	}
	
	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public List<TestSuite> getAllTestSuite() {
		return allTestSuite;
	}

	public void setAllTestSuite(List<TestSuite> allTestSuite) {
		this.allTestSuite = allTestSuite;
	}

	public String getTestSuitName() {
		return testSuitName;
	}

	public void setTestSuitName(String testSuitName) {
		this.testSuitName = testSuitName;
	}

	public List<com.photon.phresco.commons.model.TestCase> getAllTestCases() {
		return allTestCases;
	}

	public void setAllTestCases(
			List<com.photon.phresco.commons.model.TestCase> allTestCases) {
		this.allTestCases = allTestCases;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getTestScenarioName() {
		return testScenarioName;
	}

	public void setTestScenarioName(String testScenarioName) {
		this.testScenarioName = testScenarioName;
	}

	public String getTotalSuccess() {
		return totalSuccess;
	}
	public void setIsFromCI(String isFromCI) {
		this.isFromCI = isFromCI;
	}

	public String getIsFromCI() {
		return isFromCI;
	}

	public void setTotalSuccess(String totalSuccess) {
		this.totalSuccess = totalSuccess;
	}

	public String getTotalFailures() {
		return totalFailures;
	}

	public void setTotalFailures(String totalFailures) {
		this.totalFailures = totalFailures;
	}

	public String getTotalTestCases() {
		return totalTestCases;
	}

	public void setTotalTestCases(String totalTestCases) {
		this.totalTestCases = totalTestCases;
	}

	public String getTestCoverage() {
		return testCoverage;
	}

	public void setTestCoverage(String testCoverage) {
		this.testCoverage = testCoverage;
	}

	public String getFeatureId() {
		return featureId;
	}

	public void setFeatureId(String featureId) {
		this.featureId = featureId;
	}

	public String getTestCaseId() {
		return testCaseId;
	}

	public void setTestCaseId(String testCaseId) {
		this.testCaseId = testCaseId;
	}

	public String getTestDescription() {
		return testDescription;
	}

	public void setTestDescription(String testDescription) {
		this.testDescription = testDescription;
	}

	public String getTestSteps() {
		return testSteps;
	}

	public void setTestSteps(String testSteps) {
		this.testSteps = testSteps;
	}

	public String getExpectedResult() {
		return expectedResult;
	}

	public void setExpectedResult(String expectedResult) {
		this.expectedResult = expectedResult;
	}

	public String getActualResult() {
		return actualResult;
	}

	public void setActualResult(String actualResult) {
		this.actualResult = actualResult;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getBugComment() {
		return bugComment;
	}

	public void setBugComment(String bugComment) {
		this.bugComment = bugComment;
	}

	public String getTestId() {
		return testId;
	}

	public void setTestId(String testId) {
		this.testId = testId;
	}

	public String getFromTab() {
		return fromTab;
	}

	public void setFromTab(String fromTab) {
		this.fromTab = fromTab;
	}
	
	public String getActionType() {
		return actionType;
	}

	public void setActionType(String actionType) {
		this.actionType = actionType;
	}

	public boolean isErrorFound() {
		return errorFound;
	}

	public void setErrorFound(boolean errorFound) {
		this.errorFound = errorFound;
	}

	public String getNameError() {
		return nameError;
	}

	public void setNameError(String nameError) {
		this.nameError = nameError;
	}

	public String getTestCaseIdError() {
		return testCaseIdError;
	}

	public void setTestCaseIdError(String testCaseIdError) {
		this.testCaseIdError = testCaseIdError;
	}

	public String getFeatureIdError() {
		return featureIdError;
	}

	public void setFeatureIdError(String featureIdError) {
		this.featureIdError = featureIdError;
	}
}