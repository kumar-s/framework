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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import org.xml.sax.SAXParseException;

import com.google.gson.Gson;
import com.photon.phresco.commons.FrameworkConstants;
import com.photon.phresco.configuration.Environment;
import com.photon.phresco.exception.PhrescoException;
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
import com.photon.phresco.framework.commons.QualityUtil;
import com.photon.phresco.framework.commons.filter.FileListFilter;
import com.photon.phresco.framework.model.BuildInfo;
import com.photon.phresco.framework.model.PerformanceDetails;
import com.photon.phresco.framework.model.PerformanceTestResult;
import com.photon.phresco.framework.model.PropertyInfo;
import com.photon.phresco.framework.model.SettingsInfo;
import com.photon.phresco.framework.model.TestCaseError;
import com.photon.phresco.framework.model.TestCaseFailure;
import com.photon.phresco.framework.model.TestCaseResult;
import com.photon.phresco.framework.model.TestResult;
import com.photon.phresco.framework.model.TestSuiteResult;
import com.photon.phresco.util.Constants;
import com.photon.phresco.util.IosSdkUtil;
import com.photon.phresco.util.IosSdkUtil.MacSdkType;
import com.photon.phresco.util.TechnologyTypes;
import com.photon.phresco.util.Utility;
import com.phresco.pom.exception.PhrescoPomException;
import com.phresco.pom.model.Model.Modules;
import com.phresco.pom.util.PomProcessor;

public class Quality extends FrameworkBaseAction implements FrameworkConstants {

    private static final long serialVersionUID = -2040671011555139339L;
    private static final Logger S_LOGGER = Logger.getLogger(Quality.class);
    private static Boolean debugEnabled  =S_LOGGER.isDebugEnabled();
    private List<SettingsInfo> serverSettings = null;
    private String showSettings = null;
    private String testSuite = null;
    private String failures = null;
    private String errs = null;
    private String tests = null;
    private String setting = null;
    private String projectCode = null;
    private String testType = null;
    private String testResultFile = null;
    private String projectModule = "";
	private String techReport = "";
    private String testModule = null;
	private String showError = null;
    private String hideLog = null;
	private String showDebug = null;
	private String jarLocation = null;
    private String testAgainst = null;
    private String jarName = null;
    private File systemPath = null;
    
	private List<String> configName = null;
	private List<String> buildInfoEnvs = null;
    
	private String settingType = null;
    private String settingName = null;
    private String caption = null;
    private List<String> testResultFiles = null;
    private List<TestSuiteResult> testSuites = null;
    private List<String> testSuiteNames = null;
    private boolean validated = false;
	private String testResultsType = null;

	//Below variables gets the value of performance test Url, Context and TestName
	private PerformanceDetails performanceDetails = null;
	private List<String> name = null;
    private List<String> context = null;
    private List<String> contextType = null;
    private List<String> contextPostData = null;
    private List<String> encodingType = null;
    private String testName = null;
    
    //Below variables get the value of PerformanceTest for Db
    private List<String> dbPerName = null;
	private String Database = null;
	private List<String> queryType = null;
    private List<String> query = null;

    //Thread group details
    private String noOfUsers = null;
    private String rampUpPeriod = null;
    private String loopCount = null;

    //jmeterTestAgainst radio button value
    private String jmeterTestAgainst = null;
    private String technologyId=null;

    private boolean isAtleastOneFileAvail = false;

    // android performance tag name
    private String testResultDeviceId = null;
    private Map<String, String> deviceNames = null;
    private String serialNumber = null;
    
    // iphone unit test
    private String sdk = null;
	private String target = "";
	private String fromPage = "";
	
	//perfromance DB
	private String hostValue = null;
    private String portNo = null;
    private String pwd = null;
    private String dbType = null;
    private String schema = null;
    private String uname = null;
    private String dbUrl = null;
    private String driver = null;
	
    // report generation 
    private String reportName = null;
    private String reoportLocation = null;
    private String reportDataType = null;
    
    // download report
	private InputStream fileInputStream;
	private String fileName = "";
	private String reportFileName = null;
	
	private static Map<String, Map<String, NodeList>> testSuiteMap = Collections.synchronizedMap(new HashMap<String, Map<String, NodeList>>(8));
    
    public String unit() {
    	S_LOGGER.debug("Entering Method Quality.unit()");

        try {
        	ActionType actionType = null;
            ProjectAdministrator administrator = PhrescoFrameworkFactory.getProjectAdministrator();
            Project project = administrator.getProject(projectCode);
            String technology = project.getApplicationInfo().getTechInfo().getVersion();
            Map<String, String> settingsInfoMap = new HashMap<String, String>(2);
            if (TechnologyTypes.ANDROIDS.contains(technology)) {
                String device = getHttpRequest().getParameter(REQ_ANDROID_DEVICE);
				if (device.equals(SERIAL_NUMBER)) {
					device = serialNumber;
				}
				S_LOGGER.debug("Android device name " + device);
                settingsInfoMap.put(DEPLOY_ANDROID_DEVICE_MODE, device); //TODO: Need to be changed
                settingsInfoMap.put(DEPLOY_ANDROID_EMULATOR_AVD, REQ_ANDROID_DEFAULT);
                actionType = ActionType.ANDROID_TEST_COMMAND;
              
            } else if (TechnologyTypes.IPHONE_NATIVE.equals(technology)) {
            	      actionType = ActionType.IPHONE_BUILD_UNIT_TEST;
            	      settingsInfoMap.put(UNIT_TEST, TRUE);
            	      settingsInfoMap.put(IPHONE_TARGET_NAME, target);
            	      settingsInfoMap.put(IPHONE_SDK, sdk);
            } else {
                settingsInfoMap.put(TEST_PARAM, TEST_PARAM_VALUE);
                if (TechnologyTypes.SHAREPOINT.equals(technology) || TechnologyTypes.DOT_NET.equals(technology)) {
                	actionType = ActionType.SHAREPOINT_NUNIT_TEST;
                } else {
                	actionType = ActionType.TEST;
                }
            }
            FrameworkUtil frameworkUtil = FrameworkUtil.getInstance();
            StringBuilder builder = new StringBuilder(Utility.getProjectHome());
            builder.append(project.getApplicationInfo().getCode());
            List<String> projectModules = getProjectModules(projectCode);
            if (CollectionUtils.isEmpty(projectModules)) {
            	String unitTestDir = frameworkUtil.getUnitTestDir(technology);
            	builder.append(unitTestDir);
            } else {
            	builder.append(File.separatorChar);
            	builder.append(testModule);
            }
            actionType.setWorkingDirectory(builder.toString());
            
            S_LOGGER.debug("Unit test directory " + builder.toString());
            S_LOGGER.debug("Unit test Setting Info map value " + settingsInfoMap);
            
            ProjectRuntimeManager runtimeManager = PhrescoFrameworkFactory.getProjectRuntimeManager();
            BufferedReader reader = runtimeManager.performAction(project, actionType, settingsInfoMap, null);
            getHttpSession().setAttribute(projectCode + UNIT, reader);
            getHttpRequest().setAttribute(REQ_PROJECT_CODE, projectCode);
            getHttpRequest().setAttribute(REQ_TEST_TYPE, UNIT);
            getHttpRequest().setAttribute(REQ_SELECTED_MENU, APPLICATIONS);
            getHttpRequest().setAttribute(REQ_TEST_TYPE_SELECTED, REQ_TEST_UNIT);
        } catch (Exception e){
            if (e instanceof FileNotFoundException) {
                getHttpRequest().setAttribute(REQ_ERROR_TESTSUITE, getText(ERROR_UNIT_TEST));
            }
            S_LOGGER.error("Entered into catch block of Quality.unit()"+ e);
            new LogErrorReport(e, "Quality Unit test");
            
            return LOG_ERROR;
        }
        
        return APP_ENVIRONMENT_READER;
    }

    public String functional() {
    	S_LOGGER.debug("Entering Method Quality.functional()");
        
        try {
        	ActionType actionType = null;
            String envs = getHttpRequest().getParameter(ENVIRONMENT_VALUES);
            String browser = getHttpRequest().getParameter(REQ_TEST_BROWSER);
            ProjectAdministrator administrator = PhrescoFrameworkFactory.getProjectAdministrator();
            Project project = administrator.getProject(projectCode);
            String technology = project.getApplicationInfo().getTechInfo().getVersion();
            getHttpRequest().setAttribute(REQ_APPINFO, project.getApplicationInfo());  
            Map<String, String> settingsInfoMap = new HashMap<String, String>(2);
            if (TechnologyTypes.ANDROIDS.contains(technology)) {
                String device = getHttpRequest().getParameter(REQ_ANDROID_DEVICE);
				if (device.equals(SERIAL_NUMBER)) {
					device = serialNumber;
				}
                   S_LOGGER.debug("Android device name " + device);
                settingsInfoMap.put(DEPLOY_ANDROID_DEVICE_MODE, device); //TODO: Need to be changed
                settingsInfoMap.put(DEPLOY_ANDROID_EMULATOR_AVD, REQ_ANDROID_DEFAULT);
                actionType = ActionType.ANDROID_TEST_COMMAND;
            } else if (TechnologyTypes.IPHONE_NATIVE.equals(technology)) {
            	String buildNumber = getHttpRequest().getParameter(REQ_TEST_BUILD_ID);
            	settingsInfoMap.put(BUILD_NUMBER, buildNumber);
                actionType = ActionType.IPHONE_FUNCTIONAL_COMMAND;
               
	        } else if (TechnologyTypes.IPHONE_HYBRID.equals(technology)) {
	        	settingsInfoMap = null;
	            actionType = ActionType.TEST;
	        } else {
                settingsInfoMap.put(TEST_PARAM, TEST_PARAM_VALUE);
                if (TechnologyTypes.SHAREPOINT.equals(technology) || TechnologyTypes.DOT_NET.equals(technology)) {
                	actionType = ActionType.SHAREPOINT_NUNIT_TEST;
                } else {
                	actionType = ActionType.TEST;
                }
            }
            
            if (StringUtils.isEmpty(testModule) && !TechnologyTypes.ANDROIDS.contains(technology) && !TechnologyTypes.IPHONES.contains(technology) 
                    && !TechnologyTypes.BLACKBERRY.equals(technology) && !TechnologyTypes.SHAREPOINT.equals(technology) 
            		&& !TechnologyTypes.DOT_NET.equals(technology) && !TechnologyTypes.JAVA_STANDALONE.equals(technology)) {
                FunctionalUtil.adaptTestConfig(project, envs, browser);
            }
            FrameworkUtil frameworkUtil = FrameworkUtil.getInstance();
            StringBuilder builder = new StringBuilder(Utility.getProjectHome());
            builder.append(project.getApplicationInfo().getCode());
            
            if (StringUtils.isNotEmpty(testModule)) {
            	builder.append(File.separatorChar);
            	builder.append(testModule);
            }
            String funcitonalTestDir = frameworkUtil.getFuncitonalTestDir(technology);
            builder.append(funcitonalTestDir);
            actionType.setWorkingDirectory(builder.toString());
            
            S_LOGGER.debug("Functional test directory " + builder.toString());
            S_LOGGER.debug("Functional test Setting Info map value " + settingsInfoMap);
            
            //java stand alone - run against jar
            if (TechnologyTypes.JAVA_STANDALONE.equals(technology) && (testAgainst.trim().equalsIgnoreCase("jar"))) {
            	systemPath = new File(builder.toString() + File.separator + POM_FILE);
	        	PomProcessor pomprocessor = new PomProcessor(systemPath);
	        	pomprocessor.addDependency(JAVA_STANDALONE, JAVA_STANDALONE, DEPENDENCY_VERSION, SYSTEM, null, jarLocation);
	        	pomprocessor.save();
            }
            
            // java stand alone - run against build
            if (TechnologyTypes.JAVA_STANDALONE.equals(technology) && (testAgainst.trim().equalsIgnoreCase("build"))) {
            	builder = new StringBuilder(Utility.getProjectHome());             // Getting Name of Jar From POM Processor
        		builder.append(project.getApplicationInfo().getCode());
        		systemPath = new File(builder.toString() + File.separator + POM_FILE);
        		PomProcessor pomprocessor = new PomProcessor(systemPath);
        		jarName = pomprocessor.getFinalName();
        		builder.append(File.separator);
        		builder.append(DO_NOT_CHECKIN_DIR);
        		builder.append(File.separator);
        		builder.append(TARGET_DIR);
        		builder.append(File.separator);
        		builder.append(jarName);
        		builder.append(".jar");
        		jarLocation = builder.toString();
        		builder = new StringBuilder(Utility.getProjectHome());          // Adding Location of JAR as Dependency in pom.xml
        		builder.append(project.getApplicationInfo().getCode());
        		funcitonalTestDir = frameworkUtil.getFuncitonalTestDir(technology);
                builder.append(funcitonalTestDir);
            	systemPath = new File(builder.toString() + File.separator + POM_FILE);
	        	pomprocessor = new PomProcessor(systemPath);
	        	pomprocessor.addDependency(JAVA_STANDALONE, JAVA_STANDALONE, DEPENDENCY_VERSION, SYSTEM, null, jarLocation);
	        	pomprocessor.save();
            }
            
            ProjectRuntimeManager runtimeManager = PhrescoFrameworkFactory.getProjectRuntimeManager();
            BufferedReader reader = runtimeManager.performAction(project, actionType, settingsInfoMap, null);
            
            getHttpSession().setAttribute(projectCode + FUNCTIONAL, reader);
            getHttpRequest().setAttribute(REQ_PROJECT_CODE, projectCode);
            getHttpRequest().setAttribute(REQ_TEST_TYPE, FUNCTIONAL);
            getHttpRequest().setAttribute(REQ_TEST_TYPE_SELECTED, REQ_TEST_FUNCTIONAL);
            getHttpRequest().setAttribute(REQ_SELECTED_MENU, APPLICATIONS);
        } catch (Exception e) {
        	if (e instanceof FileNotFoundException) {
                getHttpRequest().setAttribute(REQ_ERROR_TESTSUITE, getText(ERROR_FUNCTIONAL_TEST));
            }
               S_LOGGER.error("Entered into catch block of Quality.functional()"+ e);
            new LogErrorReport(e, "Quality Functional test");
            
            return LOG_ERROR;
        }
        
        return APP_ENVIRONMENT_READER;
    }

    private String getTestResultPath(Project project, String testResultFile) throws ParserConfigurationException, 
            SAXException, IOException, TransformerException, PhrescoException {
    	S_LOGGER.debug("Entering Method Quality.getTestDocument(Project project, String testResultFile)");
    	S_LOGGER.debug("getTestDocument() ProjectInfo = "+project.getApplicationInfo());
    	S_LOGGER.debug("getTestDocument() TestResultFile = "+testResultFile);

    	String technology = project.getApplicationInfo().getTechInfo().getVersion();
        FrameworkUtil frameworkUtil = FrameworkUtil.getInstance();
        StringBuilder sb = new StringBuilder();
        sb.append(Utility.getProjectHome());
        sb.append(project.getApplicationInfo().getCode());

        if (FUNCTIONAL.equals(testType)) {
        	if (StringUtils.isNotEmpty(projectModule)) {
                sb.append(File.separatorChar);
                sb.append(projectModule);
    		}
            sb.append(frameworkUtil.getFunctionalReportDir(technology));
        } else if (UNIT.equals(testType)) {
        	if (StringUtils.isNotEmpty(projectModule)) {
                sb.append(File.separatorChar);
                sb.append(projectModule);
    		}
        	
        	StringBuilder tempsb = new StringBuilder(sb);
        	if ("javascript".equals(techReport)) {
        		tempsb.append(UNIT_TEST_QUNIT_REPORT_DIR);
        		File file = new File(tempsb.toString());
                if (file.isDirectory() && file.list().length > 0) {
                	sb.append(UNIT_TEST_QUNIT_REPORT_DIR);
                } else {
                	sb.append(UNIT_TEST_JASMINE_REPORT_DIR);
                }
        	} else {
        		sb.append(frameworkUtil.getUnitReportDir(technology));
        	}
        } else if (LOAD.equals(testType)) {
            sb.append(frameworkUtil.getLoadReportDir(technology));
            sb.append(File.separator);
            sb.append(testResultFile);
        } else if (PERFORMACE.equals(testType)) {
            String performanceReportDir = frameworkUtil.getPerformanceReportDir(technology);
            Pattern p = Pattern.compile(TEST_DIRECTORY);
            Matcher matcher = p.matcher(performanceReportDir);
            if (StringUtils.isNotEmpty(performanceReportDir) && matcher.find()) {
                performanceReportDir = matcher.replaceAll(testResultsType);
            }
            sb.append(performanceReportDir);
            sb.append(File.separator);
            sb.append(testResultFile);
        }

        return sb.toString();
    }

	private Document getDocument(File resultFile) throws FileNotFoundException, ParserConfigurationException, SAXException, IOException {
		InputStream fis = null;
        DocumentBuilder builder = null;
        try {
        	S_LOGGER.debug("Report path" + resultFile.getAbsolutePath());
        	fis = new FileInputStream(resultFile);
            DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
            domFactory.setNamespaceAware(false);
            builder = domFactory.newDocumentBuilder();
            Document doc = builder.parse(fis);
            return doc;

        } finally {
            if(fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                	S_LOGGER.error("Entered into catch block of Quality.getTestDocument()"+ e);
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

	public String fillTestResultFiles() throws ParserConfigurationException, SAXException, IOException, TransformerException {
    	S_LOGGER.debug("Entering Method Quality.getTestSuites(Project project)");

        try {
        	
        	ProjectAdministrator administrator = PhrescoFrameworkFactory.getProjectAdministrator();
        	Project project = administrator.getProject(projectCode);
        	Map<String, NodeList> mapTestResultName = null;
        	mapTestResultName = testSuiteMap.get(projectCode + testType + projectModule + techReport);
    		String testResultPath = getTestResultPath(project, null);
        	if (MapUtils.isEmpty(mapTestResultName) || StringUtils.isNotEmpty(fromPage)) {
        		File[] resultFiles = getTestResultFiles(testResultPath);
        		if (resultFiles != null) {
        			QualityUtil.sortResultFile(resultFiles);
        			updateCache(resultFiles);
        		} else {
        			setValidated(true);
        			if (UNIT.equals(testType)) {
        				setShowError(getText(ERROR_UNIT_TEST));
        			} else {
        				setShowError(getText(ERROR_FUNCTIONAL_TEST));
        			}
        			
        			return SUCCESS;
        		}
            	String testSuitesMapKey = projectCode + testType + projectModule + techReport;
            	mapTestResultName = testSuiteMap.get(testSuitesMapKey);
        	} 
        	
        	List<String> resultFileNames = new ArrayList<String>(mapTestResultName.keySet());
        	if (CollectionUtils.isEmpty(resultFileNames)) {
        		setValidated(true);
    			setShowError(getText(ERROR_UNIT_TEST));
    			
    			return SUCCESS;
        	}
        	setTestType(testType);
        	setTestResultFiles(resultFileNames);
        	
        	return SUCCESS;
        } catch (PhrescoException e) {
        	S_LOGGER.error("Entered into catch block of Quality.getTestSuites()"+ e);
        }
        
		return null;
    }
	
	public String fillTestSuites() {
		S_LOGGER.debug("Entering Method Quality.fillTestSuites");
		
		try {
			ProjectAdministrator administrator = PhrescoFrameworkFactory.getProjectAdministrator();
	    	Project project = administrator.getProject(projectCode);
	    	Map<String, NodeList> mapTestResultName = null;
	    	mapTestResultName = testSuiteMap.get(projectCode + testType + projectModule + techReport);
	    	
			String testResultPath = getTestResultPath(project, null);
	    	if (MapUtils.isEmpty(mapTestResultName) || StringUtils.isNotEmpty(fromPage)) {
	    		File[] resultFiles = getTestResultFiles(testResultPath);
	    		if (resultFiles != null) {
	    			QualityUtil.sortResultFile(resultFiles);
	    			updateCache(resultFiles);
	    		} else {
	    			setValidated(true);
	    			if (UNIT.equals(testType)) {
	    				setShowError(getText(ERROR_UNIT_TEST));
	    			} else {
	    				setShowError(getText(ERROR_FUNCTIONAL_TEST));
	    			}

	    			return SUCCESS;
	    		}
	        	String testSuitesMapKey = projectCode + testType + projectModule + techReport;
	        	mapTestResultName = testSuiteMap.get(testSuitesMapKey);
	    	} 
	    	
	    	
	    	List<String> resultTestSuiteNames = new ArrayList<String>(mapTestResultName.keySet());
	    	if (CollectionUtils.isEmpty(resultTestSuiteNames)) {
	    		setValidated(true);
	    		if (UNIT.equals(testType)){
	    			setShowError(getText(ERROR_UNIT_TEST));
	    		} else {
	    			setShowError(getText(ERROR_FUNCTIONAL_TEST));
	    		}
	    		
				return SUCCESS;
	    	}
	    	setTestType(testType);
	    	setTestSuiteNames(resultTestSuiteNames);
		} catch (SAXParseException e) {
    		setValidated(true);
			setShowError(getText(ERROR_PARSE_EXCEPTION));
		} catch (Exception e) {
			S_LOGGER.error("Entered into catch block of Quality.fillTestSuites()");
		}
		
		return SUCCESS;
	}
    
    private void updateCache(File[] resultFiles) throws FileNotFoundException, ParserConfigurationException, SAXException, IOException, TransformerException {
		Map<String, NodeList> mapTestSuites = new HashMap<String, NodeList>(10);
    	for (File resultFile : resultFiles) {
			try {
				Document doc = getDocument(resultFile);
				NodeList testSuiteNodeList = evaluateTestSuite(doc);
				if (testSuiteNodeList.getLength() > 0) {
					List<TestSuiteResult> testSuites = getTestSuite(testSuiteNodeList);
					for (TestSuiteResult testSuite : testSuites) {
						mapTestSuites.put(testSuite.getName(), testSuiteNodeList);
					}
				}
			} catch (PhrescoException e) {
				// continue the loop to filter the testResultFile
			} catch (XPathExpressionException e) {
				// continue the loop to filter the testResultFile
			
			}
		}
	    String testSuitesKey = projectCode + testType + projectModule + techReport;
		testSuiteMap.put(testSuitesKey, mapTestSuites);
    }
    
    private NodeList evaluateTestSuite(Document doc) throws PhrescoException, XPathExpressionException {
        ProjectAdministrator administrator = PhrescoFrameworkFactory.getProjectAdministrator();
        Project project = administrator.getProject(projectCode);
        String technology = project.getApplicationInfo().getTechInfo().getVersion();
        String testSuitePath = null;
        FrameworkUtil frameworkUtil = FrameworkUtil.getInstance();
        if (APP_UNIT_TEST.equals(testType)) {
        	testSuitePath = frameworkUtil.getUnitTestSuitePath(technology);
        } else if (APP_FUNCTIONAL_TEST.equals(testType)) {
        	testSuitePath = frameworkUtil.getFunctionalTestSuitePath(technology);
        }
        
        S_LOGGER.debug("Project info " + project.getApplicationInfo().getName() +" Test suite path " + testSuitePath);
        
    	XPath xpath = XPathFactory.newInstance().newXPath();
		XPathExpression xPathExpression = xpath.compile(testSuitePath);
		NodeList testSuiteNode = (NodeList) xPathExpression.evaluate(doc, XPathConstants.NODESET);
		
		return testSuiteNode;
    }

    private List<TestSuiteResult> getTestSuite(NodeList nodelist) throws TransformerException, PhrescoException {
    	S_LOGGER.debug("Entering Method Quality.getTestSuite(NodeList nodelist)");

    	List<TestSuiteResult> testSuites = new ArrayList<TestSuiteResult>(2);
		TestSuiteResult testSuite = null;
		for (int i = 0; i < nodelist.getLength(); i++) {
		    testSuite =  new TestSuiteResult();
		    Node node = nodelist.item(i);
		    NamedNodeMap nameNodeMap = node.getAttributes();
		    for (int k = 0; k < nameNodeMap.getLength(); k++){
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

    private List<TestCaseResult> getTestCases(NodeList testSuites) throws TransformerException, PhrescoException {
    	S_LOGGER.debug("Entering Method Quality.getTestCases(Document doc, String testSuiteName)");
        
    	try {
            String testCasePath = null;
            String testSuitePath = null;
            FrameworkUtil frameworkUtil = FrameworkUtil.getInstance();
            ProjectAdministrator administrator = PhrescoFrameworkFactory.getProjectAdministrator();
            Project project = administrator.getProject(projectCode);
            getHttpRequest().setAttribute(REQ_PROJECT_CODE, projectCode);
            String technology = project.getApplicationInfo().getTechInfo().getVersion();
           
            if (APP_UNIT_TEST.equals(testType)) {
            	testSuitePath = frameworkUtil.getUnitTestSuitePath(technology);
            } else if (APP_FUNCTIONAL_TEST.equals(testType)) {
            	testSuitePath = frameworkUtil.getFunctionalTestSuitePath(technology);
            }
            testCasePath = frameworkUtil.getTestCasePath(technology);
            
            S_LOGGER.debug("Test suite path " + testSuitePath);
            S_LOGGER.debug("Test suite path " + testCasePath);
            
            StringBuilder sb = new StringBuilder(); //testsuites/testsuite[@name='yyy']/testcase
            sb.append(testSuitePath);
            sb.append(NAME_FILTER_PREFIX);
            sb.append(testSuite);
            sb.append(NAME_FILTER_SUFIX);
            sb.append(testCasePath);

            XPath xpath = XPathFactory.newInstance().newXPath();
            NodeList nodelist = (NodeList) xpath.evaluate(sb.toString(), testSuites.item(0).getParentNode(), XPathConstants.NODESET);
            
            // For tehnologies like php and drupal duoe to plugin change xml testcase path modified
            if (nodelist.getLength() == 0) {
                StringBuilder sbMulti = new StringBuilder();
                sbMulti.append(testSuitePath);
                sbMulti.append(NAME_FILTER_PREFIX);
                sbMulti.append(testSuite);
                sbMulti.append(NAME_FILTER_SUFIX);
                sbMulti.append(XPATH_TESTSUTE_TESTCASE);
                nodelist = (NodeList) xpath.evaluate(sbMulti.toString(), testSuites.item(0).getParentNode(), XPathConstants.NODESET);
            }
            
            // For technology sharepoint
            if (nodelist.getLength() == 0) {
                StringBuilder sbMulti = new StringBuilder(); //testsuites/testsuite[@name='yyy']/testcase
                sbMulti.append(XPATH_MULTIPLE_TESTSUITE);
                sbMulti.append(NAME_FILTER_PREFIX);
                sbMulti.append(testSuite);
                sbMulti.append(NAME_FILTER_SUFIX);
                sbMulti.append(testCasePath);
                nodelist = (NodeList) xpath.evaluate(sbMulti.toString(), testSuites.item(0).getParentNode(), XPathConstants.NODESET);
            }

            List<TestCaseResult> testCases = new ArrayList<TestCaseResult>();

            int failureTestCases = 0;
            int errorTestCases = 0;

        	StringBuilder screenShotDir = new StringBuilder();
        	screenShotDir.append(Utility.getProjectHome());
        	screenShotDir.append(project.getApplicationInfo().getCode());
        	screenShotDir.append(frameworkUtil.getFunctionalReportDir(technology));
        	screenShotDir.append(File.separator);
        	screenShotDir.append(SCREENSHOT_DIR);
        	screenShotDir.append(File.separator);
        	
            for (int i = 0; i < nodelist.getLength(); i++) {
                Node node = nodelist.item(i);
                NodeList childNodes = node.getChildNodes();
                NamedNodeMap nameNodeMap = node.getAttributes();
                TestCaseResult testCase = new TestCaseResult();

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
            getHttpRequest().setAttribute(REQ_TESTSUITE_FAILURES, failureTestCases + "");
            getHttpRequest().setAttribute(REQ_TESTSUITE_ERRORS, errorTestCases + "");
            getHttpRequest().setAttribute(REQ_TESTSUITE_TESTS, nodelist.getLength() + "");
			getHttpRequest().setAttribute(REQ_PROJECT_CODE, projectCode);
			
            return testCases;
        } catch (PhrescoException e) {
        	S_LOGGER.error("Entered into catch block of Quality.getTestCases()"+ e);
            throw e;
        } catch (XPathExpressionException e) {
        	S_LOGGER.error("Entered into XPathExpressionException catch block of Quality.getTestCases()"+ e);
            throw new PhrescoException(e);
		}
    }
    
    private static TestCaseFailure getFailure(Node failureNode) throws TransformerException {
    	S_LOGGER.debug("Entering Method Quality.getFailure(Node failureNode)");
    	S_LOGGER.debug("getFailure() NodeName = "+failureNode.getNodeName());
        
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
               S_LOGGER.error("Entered into catch block of Quality.getFailure()"+ e);
        }
        
        return failure;
    }

    private static TestCaseError getError(Node errorNode) throws TransformerException {
    	S_LOGGER.debug("Entering Method Quality.getError(Node errorNode)");
		S_LOGGER.debug("getError() Node = "+errorNode.getNodeName());
        
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
               S_LOGGER.error("Entered into catch block of Quality.getError()"+ e);
        }
        
        return tcError;
    }

    private List<TestResult> getLoadTestResult(Project project, String testResultFile) throws TransformerException, PhrescoException, ParserConfigurationException, SAXException, IOException {
    	S_LOGGER.debug("Entering Method Quality.getLoadTestResult(Project project, String testResultFile)");
    	S_LOGGER.debug("getTestResult() ProjectInfo = " + project.getApplicationInfo());
    	S_LOGGER.debug("getTestResult() TestResultFile = " + testResultFile);
        
    	List<TestResult> testResults = new ArrayList<TestResult>(2);
        try {
        	String testResultPath = getTestResultPath(project, testResultFile);
            Document doc = getDocument(new File(testResultPath)); 
            NodeList nodeList = org.apache.xpath.XPathAPI.selectNodeList(doc, XPATH_TEST_RESULT);

            TestResult testResult = null;

            for (int i = 0; i < nodeList.getLength(); i++) {
                testResult =  new TestResult();
                Node node = nodeList.item(i);
                //	            NodeList childNodes = node.getChildNodes();
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
               S_LOGGER.error("Entered into catch block of Quality.getLoadTestResult()"+ e);
        }
        
        return testResults;
    }

    public String testType() {
    	S_LOGGER.debug("Entering Method Quality.testType()");
        
    	try {
            ProjectAdministrator administrator = PhrescoFrameworkFactory.getProjectAdministrator();
            Project project = administrator.getProject(projectCode);
            String testType = getHttpRequest().getParameter(REQ_TEST_TYPE);
            String technology = project.getApplicationInfo().getTechInfo().getVersion();
            
            // Show warning message for Android technology in quality page when build is not available
            if (TechnologyTypes.ANDROIDS.contains(technology)) {
            	int buildSize = administrator.getBuildInfos(project).size();
                getHttpRequest().setAttribute(REQ_BUILD_WARNING, buildSize == 0);
            } else {
            	getHttpRequest().setAttribute(REQ_BUILD_WARNING, false);
            }
            
            S_LOGGER.debug("Test type() test type " + testType);
            if (testType != null && (APP_UNIT_TEST.equals(testType) || APP_FUNCTIONAL_TEST.equals(testType))) {
                try {
                	S_LOGGER.debug("Test type() test type unit  and Functional test");
                    FrameworkUtil frameworkUtil = FrameworkUtil.getInstance();
                    if (APP_UNIT_TEST.equals(testType)) {
                        getHttpRequest().setAttribute(PATH, frameworkUtil.getUnitTestDir(technology));
                    } else if (APP_FUNCTIONAL_TEST.equals(testType) && !TechnologyTypes.IPHONE_HYBRID.equals(technology)) {
                        ActionType actionType = ActionType.STOP_SELENIUM_SERVER;
                        StringBuilder builder = new StringBuilder(Utility.getProjectHome());
                        builder.append(project.getApplicationInfo().getCode());
                        String funcitonalTestDir = frameworkUtil.getFuncitonalTestDir(technology);
                        builder.append(funcitonalTestDir);
                        actionType.setWorkingDirectory(builder.toString());
                        ProjectRuntimeManager runtimeManager = PhrescoFrameworkFactory.getProjectRuntimeManager();
                        runtimeManager.performAction(project, actionType, null, null);
                        getHttpRequest().setAttribute(PATH, 
                                frameworkUtil.getFuncitonalTestDir(technology));
                    }
                    getHttpRequest().setAttribute(REQ_FROM_PAGE, fromPage);
                    List<String> projectModules = getProjectModules(projectCode);
                    if (CollectionUtils.isNotEmpty(projectModules)) {
                    	getHttpRequest().setAttribute(REQ_PROJECT_MODULES, projectModules);
                    }
                    
                } catch (Exception e) {
                    getHttpRequest().setAttribute(REQ_ERROR_TESTSUITE, getText(ERROR_FUNCTIONAL_TEST));
                }
                getHttpRequest().setAttribute(REQ_APPINFO, project.getApplicationInfo());
                getHttpRequest().setAttribute(REQ_PROJECT_CODE, projectCode);
                getHttpRequest().setAttribute(REQ_TEST_TYPE, testType);
                List<String> projectModules = getProjectModules();
                getHttpRequest().setAttribute(REQ_PROJECT_MODULES, projectModules);
                
                return testType;
            }

            if (testType != null && APP_LOAD_TEST.equals(testType)) {
                   S_LOGGER.debug("Test type() test type load test");
                FrameworkUtil frameworkUtil = FrameworkUtil.getInstance();
                StringBuilder sb = new StringBuilder();
                sb.append(Utility.getProjectHome());
                sb.append(project.getApplicationInfo().getCode());
                getHttpRequest().setAttribute(PATH,	frameworkUtil.getLoadTestDir(technology));
                sb.append(frameworkUtil.getLoadReportDir(technology));
                   S_LOGGER.debug("test type load  test Report directory " + sb.toString());
                File file = new File(sb.toString());
                File[] children = file.listFiles(new XmlNameFileFilter(FILE_EXTENSION_XML));
                
                if (children != null) {
                	QualityUtil.sortResultFile(children);
                    getHttpRequest().setAttribute(REQ_JMETER_REPORT_FILES, children);
                } else {
                    getHttpRequest().setAttribute(REQ_ERROR_TESTSUITE, getText(ERROR_LOAD_TEST));
                }
                getHttpRequest().setAttribute(REQ_TEST_TYPE, testType);
                getHttpRequest().setAttribute(REQ_APPINFO, project.getApplicationInfo());
                getHttpRequest().setAttribute(REQ_PROJECT_CODE, projectCode);
                
                return testType;
            }

            if (testType != null && APP_PERFORMANCE_TEST.equals(testType)) {
                   S_LOGGER.debug("Test type() test type performance test");
                FrameworkUtil frameworkUtil = FrameworkUtil.getInstance();
                getHttpRequest().setAttribute(PATH,	frameworkUtil.getPerformanceTestDir(technology));
                getHttpRequest().setAttribute(REQ_TEST_TYPE, testType);
                getHttpRequest().setAttribute(REQ_APPINFO, project.getApplicationInfo());
                getHttpRequest().setAttribute(REQ_PROJECT_CODE, projectCode);
                
                return testType;
            }
        } catch (Exception e) {
               S_LOGGER.error("Entered into catch block of Quality.testType()"+ e);
            getHttpRequest().setAttribute(REQ_ERROR_TESTSUITE, ERROR_TEST_SUITE);
        }

        return APP_UNIT_TEST;
    }

    public String performanceTest() {
    	S_LOGGER.debug("Entering Method Quality.performance()");
        
        String environment = getHttpRequest().getParameter(REQ_ENVIRONMENT);
        getHttpRequest().setAttribute(REQ_TEST_TYPE_SELECTED, REQ_TEST_PERFORMANCE);
        BufferedReader reader = null;
        Writer writer = null;
        try {
        	ActionType actionType = null;
            ProjectAdministrator administrator = PhrescoFrameworkFactory.getProjectAdministrator();
            Project project = administrator.getProject(projectCode);
            String technology = project.getApplicationInfo().getTechInfo().getVersion();
            SettingsInfo selectedSettings = null;
            String requestHeaders = getHttpRequest().getParameter("requestHeaders");
            //To add the request header to the jmx
            Map<String, String> headersMap = new HashMap<String, String>(2);
            if (StringUtils.isNotEmpty(requestHeaders)) {
            	String[] headers = requestHeaders.split("#SEP#");
            	if (!ArrayUtils.isEmpty(headers)) {
            		for (String header : headers) {
						String[] nameAndValue = header.split("#VSEP#");
						headersMap.put(nameAndValue[0], nameAndValue[1]);
					}
            	}
            }
            
            Map<String, String> settingsInfoMap = new HashMap<String, String>(2);
            if (TechnologyTypes.ANDROIDS.contains(technology)) {
                String[] connectedDevices = getHttpRequest().getParameterValues(ANDROID_DEVICE);
                String devices = FrameworkUtil.convertToCommaDelimited(connectedDevices);
                settingsInfoMap.put(ANDROID_DEVICE_LIST, devices);
                actionType = ActionType.ANDROID_TEST_COMMAND;
                if (SHOW_ERROR.equals(showError)) {
                	actionType.setShowError(true);
            	} else {
            		actionType.setShowError(false);
            	}
                
                if (HIDE_LOG.equals(hideLog)) {
                	actionType.setHideLog(true);
            	} else {
            		actionType.setHideLog(false);
            	}
                
                if (SHOW_DEBUG.equals(showDebug)) {
                	actionType.setShowDebug(true);
            	} else {
            		actionType.setShowDebug(false);
            	}
                
                S_LOGGER.debug("Load method ANDROIDS type settingsInfoMap value " + settingsInfoMap);
                S_LOGGER.debug("Performance test method ANDROIDS type settingsInfoMap value " + settingsInfoMap);
            } else {
            	actionType = ActionType.TEST;
                if (Constants.SETTINGS_TEMPLATE_SERVER.equals(jmeterTestAgainst)) {
                    String serverSetting = getHttpRequest().getParameter(Constants.SETTINGS_TEMPLATE_SERVER);
                    selectedSettings = administrator.getSettingsInfo(serverSetting, Constants.SETTINGS_TEMPLATE_SERVER, project.getApplicationInfo().getCode(), environment);
                }

                if (Constants.SETTINGS_TEMPLATE_WEBSERVICE.equals(jmeterTestAgainst)) {
                    String webServiceSetting = getHttpRequest().getParameter(Constants.SETTINGS_TEMPLATE_WEBSERVICE);
                    selectedSettings = administrator.getSettingsInfo(webServiceSetting, Constants.SETTINGS_TEMPLATE_WEBSERVICE, project.getApplicationInfo().getCode(), environment);
                }
                
                if (Constants.SETTINGS_TEMPLATE_DB.equals(jmeterTestAgainst)) {
                    String dbSetting = getHttpRequest().getParameter(Constants.SETTINGS_TEMPLATE_DB);
                    selectedSettings = administrator.getSettingsInfo(dbSetting, Constants.SETTINGS_TEMPLATE_DB, project.getApplicationInfo().getCode(), environment);
                   
                   PropertyInfo host = selectedSettings.getPropertyInfo(Constants.DB_HOST);
                   PropertyInfo port = selectedSettings.getPropertyInfo(Constants.DB_PORT);
                   PropertyInfo password = selectedSettings.getPropertyInfo(Constants.DB_PASSWORD);
                   PropertyInfo type = selectedSettings.getPropertyInfo(Constants.DB_TYPE);
                   PropertyInfo dbname = selectedSettings.getPropertyInfo(Constants.DB_NAME);
                   PropertyInfo username = selectedSettings.getPropertyInfo(Constants.DB_USERNAME);
                   
                   hostValue = host.getValue();
                   portNo = port.getValue();
                   pwd = password.getValue();
                   dbType = type.getValue();
                   schema = dbname.getValue();
                   uname = username.getValue();

                   if (dbType.contains("mysql")) {
                	   dbUrl = "jdbc:mysql://" + hostValue + ":" + portNo + "/" + schema;
                	   driver = "com.mysql.jdbc.Driver";
                   }
                   if (dbType.contains("oracle")) {
                	   dbUrl = "jdbc:oracle:thin:@" + hostValue + ":" + portNo + ":" + schema;
                	   driver = "oracle.jdbc.driver.OracleDriver";
                   }
                   if (dbType.contains("mssql")) {
                	   dbUrl = "jdbc:sqlserver://" + hostValue + ":" + portNo + ";databaseName=" + schema;
                	   driver = "com.microsoft.jdbc.sqlserver.SQLServerDriver";
                   }
                   if (dbType.contains("db2")) {
                	   dbUrl = "jdbc:db2://" + hostValue + ":" + portNo + "/" + schema;
                	   driver = "com.ibm.db2.jcc.DB2Driver";
                   }
                }
                settingsInfoMap.put(TEST_PARAM, TEST_PARAM_VALUE);
                S_LOGGER.debug("Performance test method settingsInfoMap value " + settingsInfoMap);
            }

            FrameworkUtil frameworkUtil = FrameworkUtil.getInstance();
            StringBuilder builder = new StringBuilder(Utility.getProjectHome());
            builder.append(project.getApplicationInfo().getCode());
            String performanceTestDir = frameworkUtil.getPerformanceTestDir(technology);
            
            S_LOGGER.debug("Performance test directory path from framework util " + frameworkUtil.getPerformanceTestDir(technology));
            builder.append(performanceTestDir);
            S_LOGGER.debug("Performance test directory path " + builder.toString());
            if (!TechnologyTypes.ANDROIDS.contains(technology)) {
            	if ("WebService".equals(jmeterTestAgainst)) {
            		jmeterTestAgainst = "webservices";
            	}
	            builder.append(jmeterTestAgainst.toLowerCase());
	            QualityUtil.changeTestName(builder.toString(), testName);
	            QualityUtil.adaptTestConfig(builder.toString(), selectedSettings);
	            if (Constants.SETTINGS_TEMPLATE_DB.equals(jmeterTestAgainst)) {
	            	QualityUtil.adaptDBPerformanceJmx(builder.toString(), dbPerName, Database, queryType, query, Integer.parseInt(noOfUsers), Integer.parseInt(rampUpPeriod), Integer.parseInt(loopCount), dbUrl, driver, uname, pwd);
	            } else {
	            	QualityUtil.adaptPerformanceJmx(builder.toString(), name, context, contextType, contextPostData, encodingType, Integer.parseInt(noOfUsers), Integer.parseInt(rampUpPeriod), Integer.parseInt(loopCount), headersMap);
	            }
	            
	            String filepath = builder.toString() + File.separator + testName + ".json";
	            PerformanceDetails perform = new PerformanceDetails(jmeterTestAgainst, showSettings, setting, testName, name, context, contextType, contextPostData,  encodingType,dbPerName,queryType, query,Integer.parseInt(noOfUsers),Integer.parseInt(rampUpPeriod), Integer.parseInt(loopCount));
	            Gson gson = new Gson();
	            writer = new OutputStreamWriter(new FileOutputStream(filepath));
	            gson.toJson(perform,writer);
	            writer.close();
            }
            actionType.setWorkingDirectory(builder.toString());
            ProjectRuntimeManager runtimeManager = PhrescoFrameworkFactory.getProjectRuntimeManager();
            reader = runtimeManager.performAction(project, actionType, settingsInfoMap, null);
            getHttpSession().setAttribute(projectCode + PERFORMACE, reader);
            getHttpRequest().setAttribute(REQ_PROJECT_CODE, projectCode);
            getHttpRequest().setAttribute(REQ_TEST_TYPE,PERFORMACE );
        } catch(Exception e) {
            if (e instanceof FileNotFoundException) {
                getHttpRequest().setAttribute(REQ_ERROR_TESTSUITE, getText(ERROR_PERFORMANCE_TEST));
    			StringReader sb = new StringReader("Test is not available for this project");
    			reader = new BufferedReader(sb);
                getHttpSession().setAttribute(projectCode + PERFORMACE, reader);
                getHttpRequest().setAttribute(REQ_PROJECT_CODE, projectCode);
                getHttpRequest().setAttribute(REQ_TEST_TYPE,PERFORMACE );
            }
            S_LOGGER.error("Entered into catch block of Quality.performance()"+ e);
            if (writer != null) {
            	try {
					writer.close();
				} catch (IOException e1) {
					S_LOGGER.error("Entered into catch block of Quality.performance() Finally "+ e);
				}
            }
            new LogErrorReport(e, "Quality Performance test");
            
            return LOG_ERROR;
        }
        getHttpRequest().setAttribute(REQ_SELECTED_MENU, APPLICATIONS);
        
        return APP_ENVIRONMENT_READER;
    }

    public String load() {
    	S_LOGGER.debug("Entering Method Quality.load()");
        
        getHttpRequest().setAttribute(REQ_TEST_TYPE_SELECTED, REQ_TEST_LOAD);
        try {
        	ActionType actionType = null;
            ProjectAdministrator administrator = PhrescoFrameworkFactory.getProjectAdministrator();
            Project project = administrator.getProject(projectCode);
            String technology = project.getApplicationInfo().getTechInfo().getVersion();
            List<SettingsInfo> serverSettings = null;
            String requestHeaders = getHttpRequest().getParameter("requestHeaders");
            //To add the request header to the jmx
            Map<String, String> headersMap = new HashMap<String, String>(2);
            if (StringUtils.isNotEmpty(requestHeaders)) {
            	String[] headers = requestHeaders.split("#SEP#");
            	if (!ArrayUtils.isEmpty(headers)) {
            		for (String header : headers) {
						String[] nameAndValue = header.split("#VSEP#");
						headersMap.put(nameAndValue[0], nameAndValue[1]);
					}
            	}
            }
            
            Map<String, String> settingsInfoMap = new HashMap<String, String>(2);
            if (TechnologyTypes.ANDROIDS.contains(technology)) {
                String device = getHttpRequest().getParameter(REQ_ANDROID_DEVICE);
                settingsInfoMap.put(DEPLOY_ANDROID_DEVICE_MODE, device); //TODO: Need to be changed
                settingsInfoMap.put(DEPLOY_ANDROID_EMULATOR_AVD, REQ_ANDROID_DEFAULT);
                actionType = ActionType.ANDROID_TEST_COMMAND;
                S_LOGGER.debug("Load method ANDROIDS type settingsInfoMap value " + settingsInfoMap);
            } else {
            	actionType = ActionType.TEST;
            	String environment = getHttpRequest().getParameter(REQ_ENVIRONMENT);
            	String type = getHttpRequest().getParameter(REQ_JMETER_TEST_AGAINST);
                if (serverSettings == null) {
                    serverSettings = administrator.getSettingsInfos(type, projectCode, environment);
                }
                settingsInfoMap.put(TEST_PARAM, TEST_PARAM_VALUE);
                S_LOGGER.debug("Load method settingsInfoMap value " + settingsInfoMap);
            }

            FrameworkUtil frameworkUtil = FrameworkUtil.getInstance();
            StringBuilder builder = new StringBuilder(Utility.getProjectHome());
            builder.append(project.getApplicationInfo().getCode());
            String loadTestDirPath = frameworkUtil.getLoadTestDir(technology);
            S_LOGGER.debug("Load test directory path " + loadTestDirPath + "Test Name " + testName);
            builder.append(loadTestDirPath);
            QualityUtil.changeTestName(builder.toString(), testName);
            for (SettingsInfo serverSetting : serverSettings) {
            	QualityUtil.adaptTestConfig(builder.toString(), serverSetting);
			}
            QualityUtil.adaptLoadJmx(builder.toString(), Integer.parseInt(noOfUsers), Integer.parseInt(rampUpPeriod), Integer.parseInt(loopCount), headersMap);
            actionType.setWorkingDirectory(builder.toString());
            ProjectRuntimeManager runtimeManager = PhrescoFrameworkFactory.getProjectRuntimeManager();
            BufferedReader reader = runtimeManager.performAction(project, actionType, settingsInfoMap, null);
            
            getHttpSession().setAttribute(projectCode + LOAD, reader);
            getHttpRequest().setAttribute(REQ_PROJECT_CODE, projectCode);
            getHttpRequest().setAttribute(REQ_TEST_TYPE, LOAD);
        } catch(Exception e) {
            if (e instanceof FileNotFoundException) {
                getHttpRequest().setAttribute(REQ_ERROR_TESTSUITE, getText(ERROR_LOAD_TEST));
            }
            S_LOGGER.error("Entered into catch block of Quality.load()"+ e);
            new LogErrorReport(e, "Quality Load test");
            
            return LOG_ERROR;
        }
        getHttpRequest().setAttribute(REQ_SELECTED_MENU, APPLICATIONS);
        
        return APP_ENVIRONMENT_READER;
    }

    public String loadTestResult() {
    	S_LOGGER.debug("Entering Method Quality.loadTestResult()");
        
        try {
            String testResultFile = getHttpRequest().getParameter(REQ_TEST_RESULT_FILE);
            ProjectAdministrator administrator = PhrescoFrameworkFactory.getProjectAdministrator();
            Project project = administrator.getProject(projectCode);
            List<TestResult> testResults = getLoadTestResult(project, testResultFile);
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
                if (testResults.indexOf(testResult) < testResults.size() - 1) {
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

	public String performanceTestResultFiles() {
		S_LOGGER.debug("Entering Method Quality.performanceTestResultFiles()");
        
        try {
            ProjectAdministrator administrator = PhrescoFrameworkFactory.getProjectAdministrator();
            Project project = administrator.getProject(projectCode);
            FrameworkUtil frameworkUtil = FrameworkUtil.getInstance();
            StringBuilder sb = new StringBuilder();
            sb.append(Utility.getProjectHome());
            sb.append(project.getApplicationInfo().getCode());
            String performanceReportDir = frameworkUtil.getPerformanceReportDir(project.getApplicationInfo().getTechInfo().getVersion());
            S_LOGGER.debug("test type performance test Report directory " + performanceReportDir);
            if (StringUtils.isNotEmpty(performanceReportDir) && StringUtils.isNotEmpty(testResultsType)) {
                Pattern p = Pattern.compile("dir_type");
                Matcher matcher = p.matcher(performanceReportDir);
                performanceReportDir = matcher.replaceAll(testResultsType);
                sb.append(performanceReportDir);
            }
            S_LOGGER.debug("test type performance test Report directory & Type " + sb.toString() + " Type " + testResultsType);
            File file = new File(sb.toString());
            File[] children = file.listFiles(new XmlNameFileFilter(FILE_EXTENSION_XML));
            QualityUtil util = new QualityUtil();
            if (children != null) {
            	util.sortResultFile(children);
                getHttpRequest().setAttribute(REQ_JMETER_REPORT_FILES, children);
            } else {
                getHttpRequest().setAttribute(REQ_ERROR_TESTSUITE, ERROR_TEST_SUITE);
            }
            testResultFiles = new ArrayList<String>();
            for (File resultFile : children) {
                if (resultFile.isFile()) {
                    testResultFiles.add(resultFile.getName());
                }
            }
        } catch(Exception e) {
        	S_LOGGER.error("Entered into catch block of Quality.performanceTestResultFiles()"+ FrameworkUtil.getStackTraceAsString(e));
        }
        
        return SUCCESS;
    }
    
	public String performanceTestResultAvail() {
		S_LOGGER.debug("Entering Method Quality.performanceTestResultAvail()");
           
        try {
            ProjectAdministrator administrator = PhrescoFrameworkFactory.getProjectAdministrator();
            Project project = administrator.getProject(projectCode);
            FrameworkUtil frameworkUtil = FrameworkUtil.getInstance();
            List<String> testResultsTypes = new ArrayList<String>();
            testResultsTypes.add("server");
            testResultsTypes.add("database");
            testResultsTypes.add("webservices");
            for(String perType: testResultsTypes) {
                StringBuilder sb = new StringBuilder();
                sb.append(Utility.getProjectHome());
                sb.append(project.getApplicationInfo().getCode());
                String performanceReportDir = frameworkUtil.getPerformanceReportDir(project.getApplicationInfo().getTechInfo().getVersion());
                
	            if (StringUtils.isNotEmpty(performanceReportDir) && StringUtils.isNotEmpty(perType)) {
	                Pattern p = Pattern.compile("dir_type");
	                Matcher matcher = p.matcher(performanceReportDir);
	                performanceReportDir = matcher.replaceAll(perType);
	                sb.append(performanceReportDir); 
	            }
	            File file = new File(sb.toString());
	            File[] children = file.listFiles(new XmlNameFileFilter(FILE_EXTENSION_XML));
	            if (children != null && children.length > 0) {
	            	isAtleastOneFileAvail = true;
	            	break;
	            }
            }
        } catch(Exception e) {
               S_LOGGER.error("Entered into catch block of Quality.performanceTestResultAvail()"+ FrameworkUtil.getStackTraceAsString(e));
        }
        
        return SUCCESS;
    }
	
    public String performanceTestResult() {
    	S_LOGGER.debug("Entering Method Quality.performanceTestResult()");
        
        try {
            String testResultFile = getHttpRequest().getParameter(REQ_TEST_RESULT_FILE);
            String showGraphFor = getHttpRequest().getParameter("showGraphFor");
            ProjectAdministrator administrator = PhrescoFrameworkFactory.getProjectAdministrator();
            Project project = administrator.getProject(projectCode);
            String technology = project.getApplicationInfo().getTechInfo().getVersion();
            S_LOGGER.debug("Performance test file name " + testResultFile);
            if (!testResultFile.equals("null")) {
            	String testResultPath = getTestResultPath(project, testResultFile);
                Document document = getDocument(new File(testResultPath)); 
                Map<String, PerformanceTestResult> performanceReport = QualityUtil.getPerformanceReport(document, getHttpRequest(), technology, testResultDeviceId); // need to pass tech id and tag name
                getHttpRequest().setAttribute(REQ_TEST_RESULT, performanceReport);

                Set<String> keySet = performanceReport.keySet();
                StringBuilder data = new StringBuilder("[");
                StringBuilder label = new StringBuilder("[");
                
                List<Float> allMin = new ArrayList<Float>();
                List<Float> allMax = new ArrayList<Float>();
                List<Float> allAvg = new ArrayList<Float>();
                
                int index = 0;
                for (String key : keySet) {
                    PerformanceTestResult performanceTestResult = performanceReport.get(key);
                    if (REQ_TEST_SHOW_THROUGHPUT_GRAPH.equals(showGraphFor)) {
                        data.append(performanceTestResult.getThroughtPut());	//for ThroughtPut
                    } else if (REQ_TEST_SHOW_MIN_RESPONSE_GRAPH.equals(showGraphFor)) {
                        data.append(performanceTestResult.getMin());	//for min response time
                    } else if (REQ_TEST_SHOW_MAX_RESPONSE_GRAPH.equals(showGraphFor)) {
                        data.append(performanceTestResult.getMax());	//for max response time
                    } else if (REQ_TEST_SHOW_RESPONSE_TIME_GRAPH.equals(showGraphFor)) {
                   	 	data.append(performanceTestResult.getAvg());	//for responseTime
                    } else if (REQ_TEST_SHOW_ALL_GRAPH.equals(showGraphFor)) {
                    	data.append(performanceTestResult.getThroughtPut());	//for ThroughtPut
                    	allMin.add((float)performanceTestResult.getMin()/1000);
                    	allMax.add((float)performanceTestResult.getMax()/1000);
                    	allAvg.add((float) (performanceTestResult.getAvg())/1000);
                    }
                    
                    label.append("'");
                    label.append(performanceTestResult.getLabel());
                    label.append("'");
                    if (index < performanceReport.size() - 1) {
                        data.append(",");
                        label.append(",");
                    }
                    index++;
                }
                label.append("]");
                data.append("]");
                getHttpRequest().setAttribute(FrameworkConstants.REQ_GRAPH_DATA, data.toString());
                getHttpRequest().setAttribute(FrameworkConstants.REQ_GRAPH_LABEL, label.toString());
                getHttpRequest().setAttribute(FrameworkConstants.REQ_GRAPH_ALL_DATA, allMin +", "+ allAvg +", "+ allMax);
                getHttpRequest().setAttribute(FrameworkConstants.REQ_SHOW_GRAPH, showGraphFor);
                getHttpRequest().setAttribute(REQ_APPINFO, project.getApplicationInfo());
            } else {
                getHttpRequest().setAttribute(REQ_ERROR_TESTSUITE, ERROR_TEST_SUITE);
            }
        } catch (Exception e) {
        	getHttpRequest().setAttribute(REQ_ERROR_DATA, ERROR_ANDROID_DATA);
        	S_LOGGER.error("Entered into catch block of Quality.performanceTestResult()"+ e);
        }
        getHttpRequest().setAttribute(REQ_SELECTED_MENU, APPLICATIONS);
        
        return "performanceTestResult";
    }

    public String quality() {
        try {
            ProjectAdministrator administrator = PhrescoFrameworkFactory.getProjectAdministrator();
            Project project = administrator.getProject(projectCode);
            getHttpRequest().setAttribute(REQ_APPINFO, project.getApplicationInfo());
        } catch (Exception e) {
               S_LOGGER.error("Entered into catch block of Quality.quality()"+ e);
            new LogErrorReport(e, "Quality");
            
            return LOG_ERROR;
        }
        getHttpRequest().setAttribute(REQ_SELECTED_MENU, APPLICATIONS);
        
        return APP_QUALITY;
    }

    public String generateFunctionalTest() {
    	S_LOGGER.debug("Entering Method Quality.generateTest()");
        
        try {
            ProjectAdministrator administrator = PhrescoFrameworkFactory.getProjectAdministrator();
            Project project = administrator.getProject(projectCode);
            String technology = project.getApplicationInfo().getTechInfo().getVersion();
            getHttpRequest().setAttribute(REQ_APPINFO, project.getApplicationInfo());
            List<BuildInfo> buildInfos = administrator.getBuildInfos(project);
            List<Environment> environments = administrator.getEnvironments(project);

            getFunctionalTestBrowsers(technology);

            getHttpRequest().setAttribute(REQ_TEST_BUILD_INFOS, buildInfos);
            List<String> projectModules = getProjectModules(projectCode);
            getHttpRequest().setAttribute(REQ_PROJECT_MODULES, projectModules);
            getHttpRequest().setAttribute(REQ_ENVIRONMENTS, environments);
            getHttpRequest().setAttribute(REQ_SELECTED_MENU, APPLICATIONS);
        } catch (Exception e) {
        	S_LOGGER.error("Entered into catch block of Quality.generateTest()"+ e);
        }
        
        return APP_GENERATE_TEST;
    }

	public void getFunctionalTestBrowsers(String technology) {
		String osType = getOsName();
		if (WINDOWS.equals(osType)) {
		    Map<String, String> windowsBrowsersMap = new HashMap<String, String>();
		    if (TechnologyTypes.PHP.equals(technology) || TechnologyTypes.PHP_DRUPAL6.equals(technology) || TechnologyTypes.PHP_DRUPAL7.equals(technology) || TechnologyTypes.WORDPRESS.equals(technology)) {
		    	windowsBrowsersMap.put(WIN_BROWSER_FIREFOX_KEY, BROWSER_FIREFOX_VALUE);
		    	windowsBrowsersMap.put(WIN_BROWSER_OPERA_KEY, BROWSER_OPERA_VALUE);
		        windowsBrowsersMap.put(WIN_BROWSER_WEB_DRIVER_INTERNET_EXPLORER_KEY, BROWSER_INTERNET_EXPLORER_VALUE);
		        windowsBrowsersMap.put(WIN_BROWSER_SAFARI_KEY, BROWSER_SAFARI_VALUE);
		    } else if (!TechnologyTypes.SHAREPOINT.equals(technology) && !TechnologyTypes.DOT_NET.equals(technology)) {
		        windowsBrowsersMap.put(WIN_BROWSER_FIREFOX_KEY, BROWSER_FIREFOX_VALUE);
		        windowsBrowsersMap.put(WIN_BROWSER_CHROME_KEY, BROWSER_CHROME_VALUE);
		        windowsBrowsersMap.put(WIN_BROWSER_OPERA_KEY, BROWSER_OPERA_VALUE);
		        windowsBrowsersMap.put(WIN_BROWSER_INTERNET_EXPLORER_KEY, BROWSER_INTERNET_EXPLORER_VALUE);
		        windowsBrowsersMap.put(WIN_BROWSER_SAFARI_KEY, BROWSER_SAFARI_VALUE);
		    } else {
		    	windowsBrowsersMap.put(WIN_BROWSER_OPERA_KEY, BROWSER_OPERA_VALUE);
		        windowsBrowsersMap.put(WIN_BROWSER_INTERNET_EXPLORER_KEY, BROWSER_INTERNET_EXPLORER_VALUE);
		        windowsBrowsersMap.put(WIN_BROWSER_SAFARI_KEY, BROWSER_SAFARI_VALUE);
		    }
		    S_LOGGER.debug("Windows machine browsers list " + windowsBrowsersMap);
		    getHttpRequest().setAttribute(REQ_TEST_BROWSERS, windowsBrowsersMap);
		}

		if (MAC.equals(osType)) {
		    Map<String, String> macBrowsersMap = new HashMap<String, String>();
		    if (TechnologyTypes.PHP.equals(technology) || TechnologyTypes.PHP_DRUPAL6.equals(technology) || TechnologyTypes.PHP_DRUPAL7.equals(technology) || TechnologyTypes.WORDPRESS.equals(technology)) {
		    	macBrowsersMap.put(MAC_BROWSER_FIREFOX_KEY, BROWSER_FIREFOX_VALUE);
		    	macBrowsersMap.put(MAC_BROWSER_OPERA_KEY, BROWSER_OPERA_VALUE);
		    	macBrowsersMap.put(MAC_BROWSER_SAFARI_KEY, BROWSER_SAFARI_VALUE);
		    } else if (!TechnologyTypes.SHAREPOINT.equals(technology) && !TechnologyTypes.DOT_NET.equals(technology)) {
		        macBrowsersMap.put(MAC_BROWSER_FIREFOX_KEY, BROWSER_FIREFOX_VALUE);
		        macBrowsersMap.put(MAC_BROWSER_CHROME_KEY, BROWSER_CHROME_VALUE);
		        macBrowsersMap.put(MAC_BROWSER_OPERA_KEY, BROWSER_OPERA_VALUE);
		        macBrowsersMap.put(MAC_BROWSER_SAFARI_KEY, BROWSER_SAFARI_VALUE);
		    } else {
		        macBrowsersMap.put(WIN_BROWSER_INTERNET_EXPLORER_KEY, BROWSER_INTERNET_EXPLORER_VALUE);
		        macBrowsersMap.put(MAC_BROWSER_SAFARI_KEY, BROWSER_SAFARI_VALUE);
		    }
		    S_LOGGER.debug("Mac machine browsers list " + macBrowsersMap);
		    getHttpRequest().setAttribute(REQ_TEST_BROWSERS, macBrowsersMap);
		}

		if (LINUX.equals(osType)) {
		    Map<String, String> linuxBrowsersMap = new HashMap<String, String>();
		    if (TechnologyTypes.PHP.equals(technology) || TechnologyTypes.PHP_DRUPAL6.equals(technology) || TechnologyTypes.PHP_DRUPAL7.equals(technology) || TechnologyTypes.WORDPRESS.equals(technology)) {
		    	linuxBrowsersMap.put(LINUX_BROWSER_FIREFOX_KEY, BROWSER_FIREFOX_VALUE);
		    	linuxBrowsersMap.put(LINUX_BROWSER_OPERA_KEY, BROWSER_OPERA_VALUE);
		    	linuxBrowsersMap.put(LINUX_BROWSER_SAFARI_KEY, BROWSER_SAFARI_VALUE);
		    } else if (!TechnologyTypes.SHAREPOINT.equals(technology) && !TechnologyTypes.DOT_NET.equals(technology)) {
		        linuxBrowsersMap.put(LINUX_BROWSER_FIREFOX_KEY, BROWSER_FIREFOX_VALUE);
		        linuxBrowsersMap.put(LINUX_BROWSER_CHROME_KEY,BROWSER_CHROME_VALUE);
		        linuxBrowsersMap.put(WIN_BROWSER_OPERA_KEY, BROWSER_OPERA_VALUE);
		        linuxBrowsersMap.put(LINUX_BROWSER_SAFARI_KEY, BROWSER_SAFARI_VALUE);
		    } else {
		        linuxBrowsersMap.put(WIN_BROWSER_INTERNET_EXPLORER_KEY, BROWSER_INTERNET_EXPLORER_VALUE);
		        linuxBrowsersMap.put(LINUX_BROWSER_SAFARI_KEY, BROWSER_SAFARI_VALUE);
		    }
		    
		    S_LOGGER.debug("Linux machine browsers list " + linuxBrowsersMap);
		    getHttpRequest().setAttribute(REQ_TEST_BROWSERS, linuxBrowsersMap);
		}
	}
    
    public String generateUnitTest() {
    	S_LOGGER.debug("Entering Method Quality.generateUnitTest()");
        
        try {
            ProjectAdministrator administrator = PhrescoFrameworkFactory.getProjectAdministrator();
            Project project = administrator.getProject(projectCode);
            getHttpRequest().setAttribute(REQ_APPINFO, project.getApplicationInfo());
            List<String> projectModules = getProjectModules();
            getHttpRequest().setAttribute(REQ_PROJECT_MODULES, projectModules);
            getHttpRequest().setAttribute(REQ_SELECTED_MENU, APPLICATIONS);
        } catch (Exception e) {
        	S_LOGGER.error("Entered into catch block of Quality.generateTest()"+ e);
        }
        
        return APP_GENERATE_UNIT_TEST;
    }
    
    private List<String> getProjectModules() {
    	try {
            StringBuilder builder = new StringBuilder(Utility.getProjectHome());
            builder.append(projectCode);
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

    public String testReport() {
    	S_LOGGER.debug("Entering Method Quality.testReport()");
    	
    	try {
    		String testSuitesMapKey = projectCode + testType + projectModule + techReport;
        	Map<String, NodeList> testResultNameMap = testSuiteMap.get(testSuitesMapKey);
            NodeList testSuites = testResultNameMap.get(testSuite);
    		if (ALL_TEST_SUITES.equals(testSuite)) {
    			Map<String, String> testSuitesResultMap = new HashMap<String, String>();
    			float totalTestSuites = 0;
    			float successTestSuites = 0;
    			float failureTestSuites = 0;
    			float errorTestSuites = 0;
    			// get all nodelist of testType of a project
    			Collection<NodeList> allTestResultNodeLists = testResultNameMap.values();
    			for (NodeList allTestResultNodeList : allTestResultNodeLists) {
        			if (allTestResultNodeList.getLength() > 0 ) {
    	    			List<TestSuiteResult> allTestSuites = getTestSuite(allTestResultNodeList);
    	    			if (CollectionUtils.isNotEmpty(allTestSuites)) {
    		    			for (TestSuiteResult tstSuite : allTestSuites) {
    		    				//testsuite values are set before calling getTestCases value
    							testSuite = tstSuite.getName();
    				            getTestCases(allTestResultNodeList);
    				            float tests = 0;
    				            float failures = 0;
    				            float errors = 0;
    				            tests = Float.parseFloat((String) getHttpRequest().getAttribute(REQ_TESTSUITE_TESTS));
    				            failures = Float.parseFloat((String) getHttpRequest().getAttribute(REQ_TESTSUITE_FAILURES));
    				            errors = Float.parseFloat((String) getHttpRequest().getAttribute(REQ_TESTSUITE_ERRORS));
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
    			getHttpRequest().setAttribute(REQ_ALL_TESTSUITE_MAP, testSuitesResultMap);
				getHttpRequest().setAttribute(REQ_PROJECT_CODE, projectCode);
				
    			return APP_ALL_TEST_REPORT; 
    		} else {
	            if (testSuites.getLength() > 0 ) {
	            	List<TestCaseResult> testCases = getTestCases(testSuites);
	            	if (CollectionUtils.isEmpty(testCases)) {
	            		getHttpRequest().setAttribute(REQ_ERROR_TESTSUITE, ERROR_TEST_CASE);
	            	} else {
	            		getHttpRequest().setAttribute(REQ_TESTCASES, testCases);
	            	}
	            }
    		}
        } catch (Exception e) {
        	S_LOGGER.error("Entered into catch block of Quality.testSuite()"+ e);
        }
        getHttpRequest().setAttribute(REQ_SELECTED_MENU, APPLICATIONS);

        return APP_TEST_REPORT;
    }
    
    public String testAndroid(){
        getHttpRequest().setAttribute(REQ_FROM_TAB, REQ_FROM_TAB_TEST);
        getHttpRequest().setAttribute(REQ_TEST_TYPE, testType);
        
        return APP_TEST_ANDROID;
    }

    public String generateJmeter() {
    	S_LOGGER.debug("Entering Method Quality.generateJmeter()");

        try {
            ProjectAdministrator administrator = PhrescoFrameworkFactory.getProjectAdministrator();
            Project project = administrator.getProject(projectCode);
            String technology = project.getApplicationInfo().getTechInfo().getVersion();
            List<Environment> environments = administrator.getEnvironments(project);
            getHttpRequest().setAttribute(REQ_ENVIRONMENTS, environments);
	        getHttpRequest().setAttribute(REQ_SELECTED_MENU, APPLICATIONS);
	        getHttpRequest().setAttribute(REQ_TEST_TYPE_SELECTED, testType);
	        if (TechnologyTypes.ANDROIDS.contains(technology) && testType.equals(REQ_TEST_PERFORMANCE)) {
	        	QualityUtil util = new QualityUtil();
	        	try {
					ArrayList<String> connAndroidDevices = util.getConnAndroidDevices("adb devices");
					getHttpRequest().setAttribute(REQ_ANDROID_CONN_DEVICES, connAndroidDevices);
					testType = ANDROID_PERFORMACE;
				} catch (Exception e) {
					e.printStackTrace();
				}
	        }
        } catch (PhrescoException e) {
               S_LOGGER.error("Entered into catch block of Quality.generateJmeter()"+ e);
        }
        
        return testType;
    }
    
    public String fetchPerfTestJSONData() {
    	if (debugEnabled) {
    		S_LOGGER.debug("Entering Method Quality.fetchPerfTestJSONData()");
    	}
		 
    	Reader read = null;
    	try {
    		String selectedTestName = getHttpRequest().getParameter(REQ_SELECTED_TEST_NAME);
    		ProjectAdministrator administrator = PhrescoFrameworkFactory.getProjectAdministrator();
    		Project project = administrator.getProject(projectCode);
    		FrameworkUtil frameworkUtil = FrameworkUtil.getInstance();
    		StringBuilder builder = new StringBuilder(Utility.getProjectHome());
    		builder.append(project.getApplicationInfo().getCode());
    		String performanceTestDir = frameworkUtil.getPerformanceTestDir(project.getApplicationInfo().getTechInfo().getVersion());
    		builder.append(performanceTestDir);
    		if (WEBSERVICE.equals(jmeterTestAgainst)) {
    			builder.append(WEBSERVICES_DIR);
    		} else {
    			builder.append(jmeterTestAgainst);
    		}
    		builder.append(File.separator);
    		builder.append(selectedTestName);
    		builder.append(".json");
    		Gson gson = new Gson();
    		read = new InputStreamReader(new FileInputStream(builder.toString()));
    		performanceDetails = gson.fromJson(read, PerformanceDetails.class);
    	} catch(Exception e) {
    		S_LOGGER.error("Entered into catch block of Quality.fetchPerfTestJSONData()"+ e);
    	} finally {
    		if (read != null) {
    			try {
					read.close();
				} catch (IOException e) {
					S_LOGGER.error("Entered into catch block of Quality.fetchPerfTestJSONData() finally"+ e);
				}
    		}
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

    public String getSettingCaption() {
    	S_LOGGER.debug("Entering Method Quality.getSettingCaption()");
        
    	try {
            ProjectAdministrator administrator = PhrescoFrameworkFactory.getProjectAdministrator();
            Project project = administrator.getProject(projectCode);
            String envs = getHttpRequest().getParameter(REQ_ENVIRONMENTS);
            getHttpRequest().setAttribute(REQ_APPINFO, project.getApplicationInfo()); 
            SettingsInfo Settings = null;
            List<PropertyInfo> propertyInfos = null;
            String protocol = "";
            String host = "";
            String port = "";
            String context = "";
            if (StringUtils.isNotEmpty(settingType) && StringUtils.isNotEmpty(settingName)) {
	            if (settingType.equals("server")) {
	            	Settings = administrator.getSettingsInfo(settingName, Constants.SETTINGS_TEMPLATE_SERVER, projectCode, envs);
	                propertyInfos = Settings.getPropertyInfos();
	                for (PropertyInfo propertyInfo : propertyInfos) { 
	                    if (propertyInfo.getKey().equals(Constants.SERVER_PROTOCOL)) {
	                        protocol = propertyInfo.getValue();
	                    }
	                    if (propertyInfo.getKey().equals(Constants.SERVER_HOST)) {
	                        host = propertyInfo.getValue();
	                    }
	                    if (propertyInfo.getKey().equals(Constants.SERVER_PORT)) {
	                        port = propertyInfo.getValue();
	                    } 
	                    if (propertyInfo.getKey().equals(Constants.SERVER_CONTEXT)) {
	                        context = propertyInfo.getValue();
	                    }
	                }
	            }
	            if (settingType.equals("webservices")) {
	            	Settings = administrator.getSettingsInfo(settingName, Constants.SETTINGS_TEMPLATE_WEBSERVICE, projectCode, envs);
	                propertyInfos = Settings.getPropertyInfos();
	                for (PropertyInfo propertyInfo : propertyInfos) { 
	                    if (propertyInfo.getKey().equals(Constants.WEB_SERVICE_PROTOCOL)) {
	                        protocol =propertyInfo.getValue();
	                    }
	                    if (propertyInfo.getKey().equals(Constants.WEB_SERVICE_HOST)) {
	                        host = propertyInfo.getValue();
	                    }
	                    if (propertyInfo.getKey().equals(Constants.WEB_SERVICE_PORT)) {
	                        port = propertyInfo.getValue();
	                    }
	                    if (propertyInfo.getKey().equals(Constants.WEB_SERVICE_CONTEXT)) {
	                        context = propertyInfo.getValue();
	                    }
	                }
	            }
	            if (settingType.equals("database")) {
	            	Settings = administrator.getSettingsInfo(settingName, Constants.SETTINGS_TEMPLATE_DB, projectCode, envs);
	            	propertyInfos = Settings.getPropertyInfos();
	            	for (PropertyInfo propertyInfo : propertyInfos) { 
	            		if (propertyInfo.getKey().equals(Constants.DB_PROTOCOL)) {
	            			protocol = Constants.DB_PROTOCOL;
	            		}
	            		if (propertyInfo.getKey().equals(Constants.DB_HOST)) {
	            			host = propertyInfo.getValue();
	            		}
	            		if (propertyInfo.getKey().equals(Constants.DB_PORT)) {
	            			port = propertyInfo.getValue();
	            		}
	            		if (propertyInfo.getKey().equals(Constants.DB_NAME)) {
	            			context = propertyInfo.getValue();
	            		}
	            	}
	            }
            }

            if (StringUtils.isNotEmpty(port)) {
                port = ":" +  port;
            }

            if (StringUtils.isNotEmpty(context)) {
                context = "/" + context;
            }
            caption = protocol + "://" + host + port + "" + context + "/";
            
        } catch (Exception e) {
        	S_LOGGER.error("Entered into catch block of Quality.getSettingCaption()"+ FrameworkUtil.getStackTraceAsString(e));
        }
        
        return SUCCESS;
    }
   
	public String tstResultFiles() {
		S_LOGGER.debug("Entering Method Quality.perTstResultFiles()");
        
        try {
        	String testDirPath = "";
        	testResultFiles =  new ArrayList<String>();
            ProjectAdministrator administrator = PhrescoFrameworkFactory.getProjectAdministrator();
            Project project = administrator.getProject(projectCode);
            String technology = project.getApplicationInfo().getTechInfo().getVersion();
            FrameworkUtil frameworkUtil = FrameworkUtil.getInstance();
            StringBuilder sb = new StringBuilder();
            sb.append(Utility.getProjectHome());
            sb.append(project.getApplicationInfo().getCode());
            if (StringUtils.isEmpty(jmeterTestAgainst)) {
            	testDirPath = frameworkUtil.getLoadReportDir(technology);
            	sb.append(testDirPath);
            } else {
                testDirPath = frameworkUtil.getPerformanceReportDir(technology);
                if (StringUtils.isNotEmpty(testDirPath) && StringUtils.isNotEmpty(jmeterTestAgainst)) {
                    Pattern p = Pattern.compile("dir_type");
                    Matcher matcher = p.matcher(testDirPath);
                    if (WEBSERVICE.equals(jmeterTestAgainst)) {
                    	testDirPath = matcher.replaceAll(WEBSERVICES_DIR);
                    } else {
                    	testDirPath = matcher.replaceAll(jmeterTestAgainst);
                    }
                    sb.append(testDirPath);
                }
            }

            File file = new File(sb.toString());
            File[] children = file.listFiles(new XmlNameFileFilter(FILE_EXTENSION_XML));
            if (children != null && children.length > 0) {
                for (File resultFile : children) {
                    if (resultFile.isFile()) {
                        testResultFiles.add(resultFile.getName());
                    }
                }
            }
        } catch(Exception e) {
        	S_LOGGER.error("Entered into catch block of Quality.perTstResultFiles"+ FrameworkUtil.getStackTraceAsString(e));
        }
        
        return SUCCESS;
    }
	
	public String devices() {
		S_LOGGER.debug("Entering Method Quality.devices()");
		
        try {
            ProjectAdministrator administrator = PhrescoFrameworkFactory.getProjectAdministrator();
            Project project = administrator.getProject(projectCode);
            String testResultFile = getHttpRequest().getParameter(REQ_TEST_RESULT_FILE);
            if (!testResultFile.equals("null")) {
            	String testResultPath = getTestResultPath(project, testResultFile);
                Document document = getDocument(new File(testResultPath)); 
        		deviceNames = QualityUtil.getDeviceNames(document);
            }
        } catch(Exception e) {
        	S_LOGGER.error("Entered into catch block of Quality.devices()"+ FrameworkUtil.getStackTraceAsString(e));
        }
        
        return SUCCESS;
	}
	
    public String testIphone() {
           S_LOGGER.debug("Entering Method Quality.testIPhone()");
           
        try {
            ProjectAdministrator administrator = PhrescoFrameworkFactory.getProjectAdministrator();
            Project project = administrator.getProject(projectCode);
            getHttpRequest().setAttribute(REQ_APPINFO, project.getApplicationInfo());
            getHttpRequest().setAttribute(REQ_FROM_TAB, REQ_FROM_TAB_TEST); // test
            getHttpRequest().setAttribute(REQ_TEST_TYPE, testType);
            if (FUNCTIONAL.equals(testType)) {
                List<BuildInfo> buildInfos = administrator.getBuildInfos(project);
                getHttpRequest().setAttribute(REQ_TEST_BUILD_INFOS, buildInfos);
            }
            if (UNIT.equals(testType)) {
				// Get xcode targets
				List<PBXNativeTarget> xcodeConfigs = ApplicationsUtil.getXcodeConfiguration(projectCode);
				getHttpRequest().setAttribute(REQ_XCODE_CONFIGS, xcodeConfigs);
				// get list of sdks
				List<String> iphoneSdks = IosSdkUtil.getMacSdks(MacSdkType.iphoneos);
				iphoneSdks.addAll(IosSdkUtil.getMacSdks(MacSdkType.iphonesimulator));
				iphoneSdks.addAll(IosSdkUtil.getMacSdks(MacSdkType.macosx));
				getHttpRequest().setAttribute(REQ_IPHONE_SDKS, iphoneSdks);
            }
        } catch (Exception e) {
               S_LOGGER.error("Entered into catch block of Quality.testIPhone()"+ e);
        }
        
        return SUCCESS;
    }
    
    public String configNames() throws PhrescoException {
    	try {
	    	String envName = getHttpRequest().getParameter("envName");
	    	String type = getHttpRequest().getParameter("type");
	    	ProjectAdministrator administrator = PhrescoFrameworkFactory.getProjectAdministrator();
	    	List<SettingsInfo> settingsInfos = administrator.getSettingsInfos(type, projectCode, envName);
	    	List<String> settingsInfoNames = new ArrayList<String>();
	    	for (SettingsInfo settingsInfo : settingsInfos) {
	    		settingsInfoNames.add(settingsInfo.getName());
			}
	    	configName = settingsInfoNames;
    	} catch(Exception e) {
    		throw new PhrescoException(e);
    	}
    	
    	return SUCCESS;
    }
    
    public String fetchBuildInfoEnvs() throws PhrescoException {
    	try {
	    	ProjectAdministrator administrator = PhrescoFrameworkFactory.getProjectAdministrator();
	    	Project project = administrator.getProject(projectCode);
	    	String buildNumber = getHttpRequest().getParameter(REQ_TEST_BUILD_ID);
	    	buildInfoEnvs = administrator.getBuildInfo(project, Integer.parseInt(buildNumber)).getEnvironments();
    	} catch (PhrescoException e) {
			throw new PhrescoException();
		}
    	
    	return SUCCESS;
    }
    
    public String printAsPdfPopup () {
        S_LOGGER.debug("Entering Method Quality.printAsPdfPopup()");
        
        try {
        	boolean XmlResultsAvailable = false;
        	ProjectAdministrator administrator = PhrescoFrameworkFactory.getProjectAdministrator();
            Project project = administrator.getProject(projectCode);
            FrameworkUtil frameworkUtil = FrameworkUtil.getInstance();
            StringBuilder sb = new StringBuilder();
            sb.append(Utility.getProjectHome());
            sb.append(project.getApplicationInfo().getCode());
            String technology = project.getApplicationInfo().getTechInfo().getVersion();
            //check unit and functional are executed already or not
            if (!XmlResultsAvailable) {
	            File file = new File(sb.toString() + frameworkUtil.getUnitReportDir(technology));
	            File[] children = file.listFiles(new XmlNameFileFilter(FILE_EXTENSION_XML));
	            if (children != null) {
	            	XmlResultsAvailable = true;
	            }
            }
            
            if (!XmlResultsAvailable) {
	            File file = new File(sb.toString() + frameworkUtil.getFunctionalReportDir(technology));
	            File[] children = file.listFiles(new XmlNameFileFilter(FILE_EXTENSION_XML));
	            if (children != null) {
	            	XmlResultsAvailable = true;
	            }
            }
            
            if (!XmlResultsAvailable) {
	            performanceTestResultAvail();
	        	if (isAtleastOneFileAvail()) {
	        		XmlResultsAvailable = true;
	        	}
            }
            
            if (!XmlResultsAvailable) {
	            File file = new File(sb.toString() + frameworkUtil.getLoadReportDir(technology));
	            File[] children = file.listFiles(new XmlNameFileFilter(FILE_EXTENSION_XML));
	            if (children != null) {
	            	XmlResultsAvailable = true;
	            }
            }
            
        	getHttpRequest().setAttribute(REQ_TEST_EXE, XmlResultsAvailable);
        	List<String> pdfFiles = new ArrayList<String>();
            // popup showing list of pdf's already created
        	String pdfDirLoc = "";
        	String fileFilterName = "";
        	if (StringUtils.isEmpty(testType)) {
        		pdfDirLoc = Utility.getProjectHome() + projectCode + File.separator + DO_NOT_CHECKIN_DIR + File.separator + ARCHIVES + File.separator + CUMULATIVE;
        		fileFilterName = projectCode;
        	} else {
        		pdfDirLoc = Utility.getProjectHome() + projectCode + File.separator + DO_NOT_CHECKIN_DIR + File.separator + ARCHIVES + File.separator + testType;
        		fileFilterName = testType;
        	}
            File pdfFileDir = new File(pdfDirLoc);
            if (pdfFileDir.isDirectory()) {
                File[] children = pdfFileDir.listFiles(new FileNameFileFilter(DOT + PDF, fileFilterName));
                QualityUtil util = new QualityUtil();
                if (children != null) {
                	util.sortResultFile(children);
                }
            	for (File child : children) {
            		String fileNameWithType = child.getName().replace(DOT + PDF, "").replace(fileFilterName + UNDERSCORE, "");
            		String[] fileWithType = fileNameWithType.split(UNDERSCORE);
    				pdfFiles.add(fileWithType[0] + UNDERSCORE + fileWithType[1]);
    			}
            }
            
            if (pdfFiles != null) {
                getHttpRequest().setAttribute(REQ_PDF_REPORT_FILES, pdfFiles);
            }
        } catch (Exception e) {
            S_LOGGER.error("Entered into catch block of Quality.printAsPdfPopup()"+ e);
        }
        getHttpRequest().setAttribute(REQ_TEST_TYPE, testType);
        getHttpRequest().setAttribute(REQ_PROJECT_CODE, projectCode);
        
        return SUCCESS;
    }
    
    public String printAsPdf () {
        S_LOGGER.debug("Entering Method Quality.printAsPdf()");
        
        try {
            ProjectAdministrator administrator = PhrescoFrameworkFactory.getProjectAdministrator();
            Project project = administrator.getProject(projectCode);
            String testType = getHttpRequest().getParameter(REQ_TEST_TYPE);
            reportGeneration(project, testType);
            getHttpRequest().setAttribute(REQ_REPORT_STATUS, getText(SUCCESS_REPORT_STATUS));
        } catch (Exception e) {
        	getHttpRequest().setAttribute(REQ_REPORT_STATUS, getText(ERROR_REPORT_STATUS));
            S_LOGGER.error("Entered into catch block of Quality.printAsPdf()"+ e);
        }
        
        return printAsPdfPopup();
    }

    public void reportGeneration(Project project, String testType) {
    	S_LOGGER.debug("Entering Method Quality.reportGeneration()");
    	
    	try {
    		PhrescoReportGeneration prg = new PhrescoReportGeneration();
            if (StringUtils.isEmpty(testType)) { 
            	prg.cumulativePdfReport(project, testType, reportDataType);
            } else {
            	prg.generatePdfReport(project, testType, reportDataType);
            }
		} catch (Exception e) {
			S_LOGGER.error("Entered into catch block of Quality.reportGeneration()" + e);
		}
    }
    
    public String downloadReport() {
        S_LOGGER.debug("Entering Method Quality.downloadReport()");
        
        try {
        	String testType = getHttpRequest().getParameter(REQ_TEST_TYPE);
        	String pdfLOC = "";
        	if (StringUtils.isEmpty(testType)) { 
        		pdfLOC = Utility.getProjectHome() + projectCode + File.separator + DO_NOT_CHECKIN_DIR + File.separator + ARCHIVES + File.separator + CUMULATIVE + File.separator + projectCode + UNDERSCORE + reportFileName + DOT + PDF;
        	} else {
        		pdfLOC = Utility.getProjectHome() + projectCode + File.separator + DO_NOT_CHECKIN_DIR + File.separator + ARCHIVES + File.separator + testType + File.separator + testType + UNDERSCORE + reportFileName + DOT + PDF;
        	}
            File pdfFile = new File(pdfLOC);
            if (pdfFile.isFile()) {
    			fileInputStream = new FileInputStream(pdfFile);
    			fileName = reportFileName.split(UNDERSCORE)[1];
            }
        } catch (Exception e) {
            S_LOGGER.error("Entered into catch block of Quality.downloadReport()" + e);
        }
        
        return SUCCESS;
    }
    
    public String deleteReport() {
        S_LOGGER.debug("Entering Method Quality.deleteReport()");
        
        try {
        	String testType = getHttpRequest().getParameter(REQ_TEST_TYPE);
        	String pdfLOC = "";
        	if (StringUtils.isEmpty(testType)) { 
        		pdfLOC = Utility.getProjectHome() + projectCode + File.separator + DO_NOT_CHECKIN_DIR + File.separator + ARCHIVES + File.separator + CUMULATIVE + File.separator + projectCode + UNDERSCORE + reportFileName + DOT + PDF;
        	} else {
        		pdfLOC = Utility.getProjectHome() + projectCode + File.separator + DO_NOT_CHECKIN_DIR + File.separator + ARCHIVES + File.separator + testType + File.separator + testType + UNDERSCORE + reportFileName + DOT + PDF;
        	}
            File pdfFile = new File(pdfLOC);
            if (pdfFile.isFile()) {
            	boolean reportDeleted = pdfFile.delete();
            	S_LOGGER.info("Report deleted " + reportDeleted);
            	if (reportDeleted) {
            		getHttpRequest().setAttribute(REQ_REPORT_DELETE_STATUS, getText(SUCCESS_REPORT_DELETE_STATUS));
            	} else {
            		getHttpRequest().setAttribute(REQ_REPORT_DELETE_STATUS, getText(ERROR_REPORT_DELETE_STATUS));
            	}
            }
        } catch (Exception e) {
            S_LOGGER.error("Entered into catch block of Quality.downloadReport()" + e);
        }
        return printAsPdfPopup();
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

    public List<String> getName() {
        return name;
    }

    public void setName(List<String> name) {
        this.name = name;
    }

    public List<String> getContext() {
        return context;
    }

    public void setContext(List<String> context) {
        this.context = context;
    }

    public String getTestResultsType() {
        return testResultsType;
    }

    public void setTestResultsType(String testResultsType) {
        this.testResultsType = testResultsType;
    }

    public String getNoOfUsers() {
        return noOfUsers;
    }

    public void setNoOfUsers(String noOfUsers) {
        this.noOfUsers = noOfUsers;
    }

    public String getRampUpPeriod() {
        return rampUpPeriod;
    }

    public void setRampUpPeriod(String rampUpPeriod) {
        this.rampUpPeriod = rampUpPeriod;
    }

    public String getLoopCount() {
        return loopCount;
    }

    public void setLoopCount(String loopCount) {
        this.loopCount = loopCount;
    }

    public String getJmeterTestAgainst() {
        return jmeterTestAgainst;
    }

    public void setJmeterTestAgainst(String jmeterTestAgainst) {
        this.jmeterTestAgainst = jmeterTestAgainst;
    }

	public String getTestName() {
		return testName;
	}

	public void setTestName(String testName) {
		this.testName = testName;
	}

	public boolean isAtleastOneFileAvail() {
		return isAtleastOneFileAvail;
	}

	public void setAtleastOneFileAvail(boolean isAtleastOneFileAvail) {
		this.isAtleastOneFileAvail = isAtleastOneFileAvail;
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

	public List<String> getContextType() {
		return contextType;
	}

	public void setContextType(List<String> contextType) {
		this.contextType = contextType;
	}

	public List<String> getContextPostData() {
		return contextPostData;
	}

	public void setContextPostData(List<String> contextPostData) {
		this.contextPostData = contextPostData;
	}

	public List<String> getEncodingType() {
		return encodingType;
	}

	public void setEncodingType(List<String> encodingType) {
		this.encodingType = encodingType;
	}

	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	public List<String> getConfigName() {
		return configName;
	}

	public void setConfigName(List<String> configName) {
		this.configName = configName;
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
	
	public String getDatabase() {
		return Database;
	}

	public void setDatabase(String database) {
		Database = database;
	}
	
	public List<String> getDbPerName() {
		return dbPerName;
	}

	public void setDbPerName(List<String> dbPerName) {
		this.dbPerName = dbPerName;
	}
	
	public List<String> getQueryType() {
		return queryType;
	}

	public void setQueryType(List<String> queryType) {
		this.queryType = queryType;
	}

	public List<String> getQuery() {
		return query;
	}

	public void setQuery(List<String> query) {
		this.query = query;
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
	
	public List<TestSuiteResult> getTestSuites() {
		return testSuites;
	}

	public void setTestSuites(List<TestSuiteResult> testSuites) {
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

	public String getJarName() {
		return jarName;
	}

	public void setJarName(String jarName) {
		this.jarName = jarName;
	}

	public File getSystemPath() {
		return systemPath;
	}

	public void setSystemPath(File systemPath) {
		this.systemPath = systemPath;
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

	public String getReportDataType() {
		return reportDataType;
	}

	public void setReportDataType(String reportDataType) {
		this.reportDataType = reportDataType;
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
	
}