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
package com.photon.phresco.framework.commons;

import java.io.BufferedReader;
import java.io.BufferedWriter;
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
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.servlet.http.HttpServletRequest;

import org.antlr.stringtemplate.StringTemplate;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.w3c.dom.Element;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.photon.phresco.commons.FrameworkConstants;
import com.photon.phresco.commons.model.ApplicationInfo;
import com.photon.phresco.commons.model.ArtifactGroup;
import com.photon.phresco.commons.model.ArtifactInfo;
import com.photon.phresco.commons.model.Customer;
import com.photon.phresco.commons.model.RepoInfo;
import com.photon.phresco.commons.model.Role;
import com.photon.phresco.commons.model.TestCase;
import com.photon.phresco.commons.model.User;
import com.photon.phresco.commons.model.UserPermissions;
import com.photon.phresco.exception.PhrescoException;
import com.photon.phresco.framework.FrameworkConfiguration;
import com.photon.phresco.framework.PhrescoFrameworkFactory;
import com.photon.phresco.framework.model.LockDetail;
import com.photon.phresco.framework.model.TestSuite;
import com.photon.phresco.plugins.model.Mojos.Mojo.Configuration.Parameters.Parameter;
import com.photon.phresco.plugins.model.Mojos.Mojo.Configuration.Parameters.Parameter.PossibleValues.Value;
import com.photon.phresco.service.client.api.ServiceManager;
import com.photon.phresco.util.Constants;
import com.photon.phresco.util.PhrescoDynamicLoader;
import com.photon.phresco.util.Utility;
import com.phresco.pom.exception.PhrescoPomException;
import com.phresco.pom.model.Model;
import com.phresco.pom.model.Model.Profiles;
import com.phresco.pom.model.Profile;
import com.phresco.pom.util.PomProcessor;

public class FrameworkUtil implements Constants, FrameworkConstants {

	private static final String PHRESCO_SQL_PATH = "phresco.sql.path";
	private static final String PHRESCO_UNIT_TEST = "phresco.unitTest";
	private static final String PHRESCO_CODE_VALIDATE_REPORT = "phresco.code.validate.report";
	private static final long serialVersionUID = 1L;
	private static FrameworkUtil frameworkUtil = null;
    private static final Logger S_LOGGER = Logger.getLogger(FrameworkUtil.class);
    private static HttpServletRequest request;
    
	public static FrameworkUtil getInstance() throws PhrescoException {
        if (frameworkUtil == null) {
            frameworkUtil = new FrameworkUtil();
        }
        return frameworkUtil;
    }
	
	public FrameworkUtil() {
		// TODO Auto-generated constructor stub
	}
	
	public FrameworkUtil(HttpServletRequest request) {
		this.request = request;
	}
	
	public String getSqlFilePath(String oldAppDirName) throws PhrescoException, PhrescoPomException {
		return getPomProcessor(oldAppDirName).getProperty(PHRESCO_SQL_PATH);
	}

	public String getUnitTestReportOptions(ApplicationInfo appinfo) throws PhrescoException, PhrescoPomException {
		return getPomProcessor(appinfo.getAppDirName()).getProperty(PHRESCO_UNIT_TEST);
	}
	
    public String getUnitTestDir(ApplicationInfo appinfo) throws PhrescoException, PhrescoPomException {
        return getPomProcessor(appinfo.getAppDirName()).getProperty(POM_PROP_KEY_UNITTEST_DIR);
    }
    
    public String getComponentTestDir(ApplicationInfo appInfo) throws PhrescoException, PhrescoPomException {
        return getPomProcessor(appInfo.getAppDirName()).getProperty(POM_PROP_KEY_COMPONENTTEST_DIR);
    }
    
    public String getManualTestReportDir(ApplicationInfo appInfo) throws PhrescoException, PhrescoPomException {
        return getPomProcessor(appInfo.getAppDirName()).getProperty(POM_PROP_KEY_MANUALTEST_RPT_DIR);
    }
    
    public String getUnitTestReportDir(ApplicationInfo appInfo) throws PhrescoPomException, PhrescoException {
        return getPomProcessor(appInfo.getAppDirName()).getProperty(POM_PROP_KEY_UNITTEST_RPT_DIR);
    }
    
    public String getComponentTestReportDir(ApplicationInfo appInfo) throws PhrescoPomException, PhrescoException {
        return getPomProcessor(appInfo.getAppDirName()).getProperty(POM_PROP_KEY_COMPONENTTEST_RPT_DIR);
    }
    
    public String getUnitTestReportDir(ApplicationInfo appInfo, String option) throws PhrescoPomException, PhrescoException {
        return getPomProcessor(appInfo.getAppDirName()).getProperty(POM_PROP_KEY_UNITTEST_RPT_DIR_START + option + POM_PROP_KEY_UNITTEST_RPT_DIR_END);
    }

	public String getUnitTestSuitePath(ApplicationInfo appInfo) throws PhrescoException, PhrescoPomException {
        return getPomProcessor(appInfo.getAppDirName()).getProperty(POM_PROP_KEY_UNITTEST_TESTSUITE_XPATH);
    }
	
	public String getComponentTestSuitePath(ApplicationInfo appInfo) throws PhrescoException, PhrescoPomException {
        return getPomProcessor(appInfo.getAppDirName()).getProperty(POM_PROP_KEY_COMPONENTTEST_TESTSUITE_XPATH);
    }
	
	public String getUnitTestSuitePath(ApplicationInfo appInfo, String option) throws PhrescoException, PhrescoPomException {
        return getPomProcessor(appInfo.getAppDirName()).getProperty(POM_PROP_KEY_UNITTEST_TESTSUITE_XPATH_START + option + POM_PROP_KEY_UNITTEST_TESTSUITE_XPATH_END);
    }
    
    public  String getUnitTestCasePath(ApplicationInfo appInfo) throws PhrescoException, PhrescoPomException {
        return getPomProcessor(appInfo.getAppDirName()).getProperty(POM_PROP_KEY_UNITTEST_TESTCASE_PATH);
    }
    
    public  String getComponentTestCasePath(ApplicationInfo appInfo) throws PhrescoException, PhrescoPomException {
        return getPomProcessor(appInfo.getAppDirName()).getProperty(POM_PROP_KEY_COMPONENTTEST_TESTCASE_PATH);
    }
    
    public  String getUnitTestCasePath(ApplicationInfo appInfo, String option) throws PhrescoException, PhrescoPomException {
        return getPomProcessor(appInfo.getAppDirName()).getProperty(POM_PROP_KEY_UNITTEST_TESTCASE_PATH_START + option + POM_PROP_KEY_UNITTEST_TESTCASE_PATH_END);
    }
    
    public String getSeleniumToolType(ApplicationInfo appInfo) throws PhrescoException, PhrescoPomException {
        return getPomProcessor(appInfo.getAppDirName()).getProperty(POM_PROP_KEY_FUNCTEST_SELENIUM_TOOL);
    }
    
    public String getFunctionalTestDir(ApplicationInfo appInfo) throws PhrescoException, PhrescoPomException {
        return getPomProcessor(appInfo.getAppDirName()).getProperty(POM_PROP_KEY_FUNCTEST_DIR);
    }
    
    public String getFunctionalTestReportDir(ApplicationInfo appInfo) throws PhrescoPomException, PhrescoException {
        return getPomProcessor(appInfo.getAppDirName()).getProperty(POM_PROP_KEY_FUNCTEST_RPT_DIR);
    }

    public String getSceenShotDir(ApplicationInfo appInfo) throws PhrescoPomException, PhrescoException {
        return getPomProcessor(appInfo.getAppDirName()).getProperty(POM_PROP_KEY_SCREENSHOT_DIR);
    }
    
    public String getFunctionalTestSuitePath(ApplicationInfo appInfo) throws PhrescoException, PhrescoPomException {
        return getPomProcessor(appInfo.getAppDirName()).getProperty(POM_PROP_KEY_FUNCTEST_TESTSUITE_XPATH);
    }
    
    public  String getFunctionalTestCasePath(ApplicationInfo appInfo) throws PhrescoException, PhrescoPomException {
        return getPomProcessor(appInfo.getAppDirName()).getProperty(POM_PROP_KEY_FUNCTEST_TESTCASE_PATH);
    }
    
    public String getLoadTestDir(ApplicationInfo appInfo) throws PhrescoException, PhrescoPomException {
    	return getPomProcessor(appInfo.getAppDirName()).getProperty(POM_PROP_KEY_LOADTEST_DIR);
    }
    
    public String getLoadTestReportDir(ApplicationInfo appinfo) throws PhrescoPomException, PhrescoException {
    	return getPomProcessor(appinfo.getAppDirName()).getProperty(POM_PROP_KEY_LOADTEST_RPT_DIR);
    }
    
    public String getPerformanceTestDir(ApplicationInfo appinfo) throws PhrescoException, PhrescoPomException {
        return getPomProcessor(appinfo.getAppDirName()).getProperty(POM_PROP_KEY_PERFORMANCETEST_DIR);
    }
    
    public String getPerformanceTestShowDevice(ApplicationInfo appinfo) throws PhrescoException, PhrescoPomException {
        return getPomProcessor(appinfo.getAppDirName()).getProperty(POM_PROP_KEY_PERF_SHOW_DEVICE);
    }
    
	public String getLoadUploadJmxDir(ApplicationInfo appinfo) throws PhrescoException, PhrescoPomException {
        return getPomProcessor(appinfo.getAppDirName()).getProperty(POM_PROP_KEY_LOADTEST_JMX_UPLOAD_DIR);
    }
	
    public String getPerformanceTestReportDir(ApplicationInfo appinfo) throws PhrescoException, PhrescoPomException {
        return getPomProcessor(appinfo.getAppDirName()).getProperty(POM_PROP_KEY_PERFORMANCETEST_RPT_DIR);
    }
    
	public String getPerformanceUploadJmxDir(ApplicationInfo appinfo) throws PhrescoException, PhrescoPomException {
        return getPomProcessor(appinfo.getAppDirName()).getProperty(POM_PROP_KEY_PERFORMANCETEST_JMX_UPLOAD_DIR);
    }
    
    public String getPerformanceResultFileExtension(ApplicationInfo appinfo) throws PhrescoException, PhrescoPomException {
        return getPomProcessor(appinfo.getAppDirName()).getProperty(POM_PROP_KEY_PERFORMANCETEST_RESULT_EXTENSION);
    }
    
    public String getLoadResultFileExtension(ApplicationInfo appinfo) throws PhrescoException, PhrescoPomException {
        return getPomProcessor(appinfo.getAppDirName()).getProperty(POM_PROP_KEY_LOADTEST_RESULT_EXTENSION);
    }
	
    public String getEmbedAppTargetDir(ApplicationInfo appinfo) throws PhrescoException, PhrescoPomException {
        return getPomProcessor(appinfo.getAppDirName()).getProperty(POM_PROP_KEY_EMBED_APP_TARGET_DIR);
    }
    
    public String getLogFilePath(ApplicationInfo appinfo) throws PhrescoException, PhrescoPomException {
        return getPomProcessor(appinfo.getAppDirName()).getProperty(POM_PROP_KEY_LOG_FILE_PATH);
    }
    
    public String isIphoneTagExists(ApplicationInfo appinfo) throws PhrescoException, PhrescoPomException {
        return getPomProcessor(appinfo.getAppDirName()).getProperty(PHRESCO_CODE_VALIDATE_REPORT);
    }
	
	public String getThemeFileExtension(ApplicationInfo appinfo) throws PhrescoException, PhrescoPomException {
        return getPomProcessor(appinfo.getAppDirName()).getProperty(POM_PROP_KEY_THEME_EXT);
    }
	
	public String getThemeBuilderPath(ApplicationInfo appinfo) throws PhrescoException, PhrescoPomException {
        return getPomProcessor(appinfo.getAppDirName()).getProperty(POM_PROP_KEY_THEME_BUILDER);
    }
	
	public String getThemeBuilderBrowsePath(ApplicationInfo appinfo) throws PhrescoException, PhrescoPomException {
        return getPomProcessor(appinfo.getAppDirName()).getProperty(POM_PROP_KEY_THEME_BROWSE_BUILDER);
    }

	public String getHubConfigFile(ApplicationInfo appInfo) throws PhrescoException, PhrescoPomException {
        StringBuilder sb = new StringBuilder(Utility.getProjectHome());
        sb.append(appInfo.getAppDirName());
        sb.append(File.separator);
        sb.append(getFunctionalTestDir(appInfo));
        sb.append(File.separator);
        sb.append("hubconfig.json");
        return sb.toString();
    }

    public static String getStackTraceAsString(Exception exception) {
	    StringWriter sw = new StringWriter();
	    PrintWriter pw = new PrintWriter(sw);
	    pw.print(" " + SQUARE_OPEN + " ");
	    pw.print(exception.getClass().getName());
	    pw.print(" " + SQUARE_CLOSE + " ");
	    pw.print(exception.getMessage());
	    pw.print(" ");
	    exception.printStackTrace(pw);
	    return sw.toString();
    }
    
    public static String removeFileExtension(String fileName) {
    	fileName = fileName.substring(0, fileName.lastIndexOf('.'));
    	return fileName;
    }
    
    public static float roundFloat(int decimal, double value) {
		BigDecimal roundThroughPut = new BigDecimal(value);
		return roundThroughPut.setScale(decimal, BigDecimal.ROUND_HALF_EVEN).floatValue();
	}
    
    public static String convertToCommaDelimited(String[] list) {
        StringBuffer ret = new StringBuffer("");
        for (int i = 0; list != null && i < list.length; i++) {
            ret.append(list[i]);
            if (i < list.length - 1) {
                ret.append(',');
            }
        }
        return ret.toString();
    }
    
    public static void copyFile(File srcFile, File dstFile) throws PhrescoException {
    	try {
    		if (!dstFile.exists()) {
    			dstFile.getParentFile().mkdirs();
    			dstFile.createNewFile();
    		}
			FileUtils.copyFile(srcFile, dstFile);
		} catch (Exception e) {
			throw new PhrescoException();
		}
    }
    
    public PomProcessor getPomProcessor(String appDirName) throws PhrescoException {
    	try {
    		StringBuilder builder = new StringBuilder(Utility.getProjectHome());
    		builder.append(appDirName);
    		builder.append(File.separatorChar);
    		builder.append(POM_XML);
    		S_LOGGER.debug("builder.toString() " + builder.toString());
    		File pomPath = new File(builder.toString());
    		S_LOGGER.debug("file exists " + pomPath.exists());
    		return new PomProcessor(pomPath);
    	} catch (Exception e) {
    		throw new PhrescoException(e);
    	}
    }

    
	// get server Url for sonar
	public String getSonarHomeURL() throws PhrescoException {
		FrameworkConfiguration frameworkConfig = PhrescoFrameworkFactory.getFrameworkConfig();
		String serverUrl = "";
		StringBuffer sb = null;
		try {
			if (StringUtils.isNotEmpty(frameworkConfig.getSonarUrl())) {
				serverUrl = frameworkConfig.getSonarUrl();
				S_LOGGER.debug("if condition serverUrl " + serverUrl);
			} else {
				serverUrl = request.getRequestURL().toString();
				URL url = new URL(serverUrl);
				InetAddress ip = InetAddress.getLocalHost();

				sb = new StringBuffer();
				sb.append(url.getProtocol());
				sb.append(PROTOCOL_POSTFIX);
				sb.append(ip.getHostAddress());
				sb.append(COLON);
				sb.append(url.getPort());
				serverUrl = sb.toString();
			}
		} catch (Exception e) {
			S_LOGGER.debug("Url is not valid --> " + serverUrl);
		}
		return serverUrl;
	}
    
    //get server Url for sonar
    public String getSonarURL() throws PhrescoException {
    	FrameworkConfiguration frameworkConfig = PhrescoFrameworkFactory.getFrameworkConfig();
    	String serverUrl = getSonarHomeURL();
    	S_LOGGER.debug("serverUrl ... " + serverUrl);
	    String sonarReportPath = frameworkConfig.getSonarReportPath();
	    S_LOGGER.debug("sonarReportPath ... " + sonarReportPath);
	    String[] sonar = sonarReportPath.split("/");
	    S_LOGGER.debug("sonar[1] " + sonar[1]);
	    serverUrl = serverUrl.concat(FORWARD_SLASH + sonar[1]);
	    S_LOGGER.debug("Final url => " + serverUrl);
	    return serverUrl;
    }
    
	public List<String> getSonarProfiles(ApplicationInfo appInfo) throws PhrescoException {
		List<String> sonarTechReports = new ArrayList<String>(6);
		try {
			StringBuilder pomBuilder = new StringBuilder(Utility.getProjectHome());
			pomBuilder.append(File.separator);
			pomBuilder.append(appInfo.getAppDirName());
			pomBuilder.append(File.separator);
			pomBuilder.append(Utility.getPomFileName(appInfo));
			File pomPath = new File(pomBuilder.toString());
			PomProcessor pomProcessor = new PomProcessor(pomPath);
			Model model = pomProcessor.getModel();
			S_LOGGER.debug("model... " + model);
			Profiles modelProfiles = model.getProfiles();
			if (modelProfiles == null) {
				return sonarTechReports;
			}
			S_LOGGER.debug("model Profiles... " + modelProfiles);
			List<Profile> profiles = modelProfiles.getProfile();
			if (profiles == null) {
				return sonarTechReports;
			}
			S_LOGGER.debug("profiles... " + profiles);
			for (Profile profile : profiles) {
				S_LOGGER.debug("profile...  " + profile);
				if (profile.getProperties() != null) {
					List<Element> any = profile.getProperties().getAny();
					int size = any.size();
					
					for (int i = 0; i < size; ++i) {
						boolean tagExist = 	any.get(i).getTagName().equals(SONAR_LANGUAGE);
						if (tagExist){
							S_LOGGER.debug("profile.getId()... " + profile.getId());
							sonarTechReports.add(profile.getId());
						}
					}
				}
			}
			S_LOGGER.debug("return from getSonarProfiles");
		} catch (Exception e) {
			throw new PhrescoException(e);
		}
		return sonarTechReports;
	}
	
	/**
	 * To encrypt the given string
	 * @param inputString
	 * @return
	 */
	public static String encryptString(String inputString) {
        byte[] encodeBase64 = Base64.encodeBase64(inputString.getBytes());
        String encodedString = new String(encodeBase64);

        return encodedString;
    }
	
	/**
	 * To decrypt the given string
	 * @param inputString
	 * @return
	 */
	public static String decryptString(String inputString) {
        byte[] decodedBytes = org.apache.commons.codec.binary.Base64.decodeBase64(inputString.getBytes());
        String decodedString = new String(decodedBytes);

        return decodedString;
    }

   public static List<String> getCsvAsList(String csv) {
        Pattern csvPattern = Pattern.compile(CSV_PATTERN);
        Matcher match = csvPattern.matcher(csv);

        List<String> list = new ArrayList<String>(match.groupCount());
        // For each field
        while (match.find()) {
            String value = match.group();
            if (value == null) {
                break;
            }
            if (value.endsWith(",")) {  // trim trailing ,
                value = value.substring(0, value.length() - 1);
            }
            if (value.startsWith("\"")) { // assume also ends with
                value = value.substring(1, value.length() - 1);
            }
            if (value.length() == 0) {
                value = null;
            }
            list.add(value.trim());
        }
        if (CollectionUtils.isEmpty(list)) {
            list.add(csv.trim());
        }
        
        return list;
    }
    
    public static String listToCsv(List<?> list) {
        Iterator<?> iter = list.iterator();
        String csvString = "";
        String sep = "";
        while (iter.hasNext()) {
            csvString += sep + iter.next();
            sep = ",";
        }
        
        return csvString;
    }
    
    public static UserPermissions getUserPermissions(ServiceManager serviceManager, User user) throws PhrescoException {
    	UserPermissions permissions = new UserPermissions();
    	try {
    		List<String> roleIds = user.getRoleIds();
			if (CollectionUtils.isNotEmpty(roleIds)) {
				List<String> permissionIds = new ArrayList<String>();
				for (String roleId : roleIds) {
					Role role = serviceManager.getRole(roleId);
					permissionIds.addAll(role.getPermissionIds());
				}
				
				if (CollectionUtils.isNotEmpty(permissionIds)) {
					if (permissionIds.contains(FrameworkConstants.PER_MANAGE_APPLICATIONS)) {
						permissions.setManageApplication(true);
					}
					
					if (permissionIds.contains(FrameworkConstants.PER_MANAGE_REPO)) {
						permissions.setManageRepo(true);
					}
					
					if (permissionIds.contains(FrameworkConstants.PER_IMPORT_APPLICATIONS)) {
						permissions.setImportApplication(true);
					}
					
					if (permissionIds.contains(FrameworkConstants.PER_MANAGE_PDF_REPORTS)) {
						permissions.setManagePdfReports(true);
					}
					
					if (permissionIds.contains(FrameworkConstants.PER_MANAGE_CODE_VALIDATION)) {
						permissions.setManageCodeValidation(true);
					}
					
					if (permissionIds.contains(FrameworkConstants.PER_MANAGE_CONFIGURATIONS)) {
						permissions.setManageConfiguration(true);
					}
					
					if (permissionIds.contains(FrameworkConstants.PER_MANAGE_BUILDS)) {
						permissions.setManageBuilds(true);
					}
					
					if (permissionIds.contains(FrameworkConstants.PER_MANAGE_TEST)) {
						permissions.setManageTests(true);
					}
					
					if (permissionIds.contains(FrameworkConstants.PER_MANAGE_CI_JOBS)) {
						permissions.setManageCIJobs(true);
					}
					
					if (permissionIds.contains(FrameworkConstants.PER_EXECUTE_CI_JOBS)) {
						permissions.setExecuteCIJobs(true);
					}
					
					if (permissionIds.contains(FrameworkConstants.PER_MANAGE_MAVEN_REPORTS)) {
						permissions.setManageMavenReports(true);
					}
				}
			}
		} catch (PhrescoException e) {
			throw e;
		}
		
		return permissions;
    }

	public List<TestSuite> readManualTestSuiteFile(String filePath) {
		List<TestSuite> testSuites = readTestSuites(filePath);
		return testSuites;
	}

    public  List<TestSuite> readTestSuites(String filePath)  {
            List<TestSuite> excels = new ArrayList<TestSuite>();
            Iterator<Row> rowIterator = null;
            try {
            	File testDir = new File(filePath);
          		StringBuilder sb = new StringBuilder(filePath);
       	        if(testDir.isDirectory()) {
	       	        	FilenameFilter filter = new PhrescoFileFilter("", "xlsx");
	       	        	File[] listFiles = testDir.listFiles(filter);
	       	        	if (listFiles.length != 0) {
							for (File file1 : listFiles) {
								 if (file1.isFile()) {
									sb.append(File.separator);
							    	sb.append(file1.getName());
							    }
							}
							readTestSuiteFromXLSX(excels, sb);
	       	        	} else {
	   	                	FilenameFilter filter1 = new PhrescoFileFilter("", "xls");
	   	     	            File[] listFiles1 = testDir.listFiles(filter1);
	   	     	            if (listFiles1.length != 0) {
		   	     	            for(File file2 : listFiles1) {
		   	     	            	if (file2.isFile()) {
		   	     	            		sb.append(File.separator);
		   	    	                	sb.append(file2.getName());
		   	     	            	}
		   	     	            }
		   	     	            readTestSuitesFromXLS(excels, sb);
	   	     	            } /*else {
		   	     	            FilenameFilter filterOds = new PhrescoFileFilter("", "ods");
		   	     	            File[] odsListFiles = testDir.listFiles(filterOds);
		   	     	            for(File file2 : odsListFiles) {
		   	     	            	if (file2.isFile()) {
		   	     	            		sb.append(File.separator);
		   	    	                	sb.append(file2.getName());
		   	     	            	}
		   	     	            }
	   	     	            	readTestSuiteFromODS(sb, excels);
	   	     	            }*/
	   	                }
       	        	}
                    
            } catch (Exception e) {
                   // e.printStackTrace();
            }
            return excels;
    }

	private void readTestSuiteFromXLSX(List<TestSuite> excels, StringBuilder sb)
			throws FileNotFoundException, InvalidFormatException, IOException,
			UnknownHostException, PhrescoException {
		Iterator<Row> rowIterator;
		FileInputStream myInput = new FileInputStream(sb.toString());
		    
		OPCPackage opc=OPCPackage.open(myInput); 
		
		XSSFWorkbook myWorkBook = new XSSFWorkbook(opc);
		XSSFSheet mySheet = myWorkBook.getSheetAt(0);
		rowIterator = mySheet.rowIterator();
		for (int i = 0; i <=2; i++) {
			rowIterator.next();
		}
		
		while (rowIterator.hasNext()) {
			Row next = rowIterator.next();
			if (StringUtils.isNotEmpty(getValue(next.getCell(2))) && !getValue(next.getCell(2)).equalsIgnoreCase("Total")) {
				TestSuite createObject = createObject(next);
		    	excels.add(createObject);
			}
		}
	}

	private void readTestSuitesFromXLS(List<TestSuite> excels, StringBuilder sb)
			throws FileNotFoundException, IOException, UnknownHostException,
			PhrescoException {
		Iterator<Row> rowIterator;
		FileInputStream myInput = new FileInputStream(sb.toString());
		HSSFWorkbook myWorkBook = new HSSFWorkbook(myInput);

		HSSFSheet mySheet = myWorkBook.getSheetAt(0);
		rowIterator = mySheet.rowIterator();
		for (int i = 0; i <=2; i++) {
			rowIterator.next();
		}
		while (rowIterator.hasNext()) {
			Row next = rowIterator.next();
			if (StringUtils.isNotEmpty(getValue(next.getCell(2))) && !getValue(next.getCell(2)).equalsIgnoreCase("Total")) {
				TestSuite createObject = createObject(next);
		    	excels.add(createObject);
			}
		}
	}
    

	private static TestSuite createObject(Row next) throws UnknownHostException, PhrescoException{
    	TestSuite testSuite = new TestSuite();
    	if(next.getCell(2) != null) {
    		Cell cell = next.getCell(2);
    		String value = getValue(cell);
    		if(StringUtils.isNotEmpty(value)) {
    			testSuite.setName(value);
    		}
    	}
    	float success = 0;
    	if(next.getCell(3)!=null){
    		Cell cell = next.getCell(3);
    		String value=getValue(cell);
    		if(StringUtils.isNotEmpty(value)) {
    			success =Float.parseFloat(value);
	    		testSuite.setTests(success);
    		}
    	}
    	float failure = 0;
    	if(next.getCell(4)!=null){
    		Cell cell = next.getCell(4);
    		String value=getValue(cell);
    		if(StringUtils.isNotEmpty(value)) {
    			failure = Float.parseFloat(value);
	    		testSuite.setFailures(failure);
    		}
    	}
    	float notApplicable = 0;
    	if(next.getCell(5)!=null){
    		Cell cell = next.getCell(5);
    		String value=getValue(cell);
    		if(StringUtils.isNotEmpty(value)) {
    			notApplicable = Float.parseFloat(value);
	    		testSuite.setNotApplicable(notApplicable);
    		}
    	}
    	if(next.getCell(6)!=null){
    		Cell cell = next.getCell(6);
    		String value=getValue(cell);
    		if(StringUtils.isNotEmpty(value)) {
	    		float notExecuted=Float.parseFloat(value);
	    		testSuite.setErrors(notExecuted);
    		}
    	}
    	float blocked = 0;
    	if(next.getCell(7)!=null){
    		Cell cell = next.getCell(7);
    		String value=getValue(cell);
    		if(StringUtils.isNotEmpty(value)) {
    			blocked =Float.parseFloat(value);
	    		testSuite.setBlocked(blocked);
    		}
    	}
    	float total = 0;
    	if(next.getCell(8)!=null){
    		Cell cell = next.getCell(8);
    		String value=getValue(cell);
    		if(StringUtils.isNotEmpty(value)) {
	    		total=Float.parseFloat(value);
	    		testSuite.setTotal(total);
    		}
    	}
    	if(next.getCell(9)!=null){
    		Cell cell=next.getCell(9);
    		String value=getValue(cell);
    		if(StringUtils.isNotEmpty(value)) {
	    		float testCoverage=Float.parseFloat(value);
	    		testSuite.setTestCoverage(testCoverage);
    		}
    	}
    	float notExecuted = total - (success + failure + notApplicable + blocked);
    	testSuite.setNotExecuted(notExecuted);
    	return testSuite;
	}
    
    public List<TestCase> readManualTestCaseFile(String filePath, String fileName, com.photon.phresco.commons.model.TestCase testCase) throws PhrescoException {
		List<TestCase> testCases = readTestCase(filePath, fileName, testCase);
		return testCases;
	}
    
    private List<TestCase> readTestCase(String filePath,String fileName,com.photon.phresco.commons.model.TestCase tstCase) throws PhrescoException {
    	 List<TestCase> testCases = new ArrayList<TestCase>();
    	 try {
    		 File testDir = new File(filePath);
       		StringBuilder sb = new StringBuilder(filePath);
	        if(testDir.isDirectory()) {
       	        	FilenameFilter filter = new PhrescoFileFilter("", "xlsx");
       	        	File[] listFiles = testDir.listFiles(filter);
       	        	if (listFiles.length != 0) {
						for (File file1 : listFiles) {
							 if (file1.isFile()) {
								sb.append(File.separator);
						    	sb.append(file1.getName());
						    }
						}
						updateTestCaseToXLSX(fileName, tstCase, testCases, sb);
       	        	} else {
   	                	FilenameFilter filter1 = new PhrescoFileFilter("", "xls");
   	     	            File[] listFiles1 = testDir.listFiles(filter1);
	   	     	            if (listFiles1.length != 0) {
		   	     	            for(File file2 : listFiles1) {
		   	     	            	if (file2.isFile()) {
		   	     	            		sb.append(File.separator);
		   	    	                	sb.append(file2.getName());
		   	     	            	}
		   	     	            }
		   	     	            FileInputStream myInput = new FileInputStream(sb.toString());
		   	     	            HSSFWorkbook myWorkBook = new HSSFWorkbook(myInput);
			   	     	        int numberOfSheets = myWorkBook.getNumberOfSheets();
						         for (int j = 0; j < numberOfSheets; j++) {
						        	 HSSFSheet mySheet = myWorkBook.getSheetAt(j);
						        	 if(mySheet.getSheetName().equals(fileName)) {
						        		 Iterator<Row> rowIterator;
						        		 readTestFromSheet(tstCase, testCases, mySheet);
						    	         if (StringUtils.isNotEmpty(tstCase.getTestCaseId())) {
						    	        	 updateIndexPage(fileName, tstCase,
													testCases, myWorkBook);
							    	         }
						    	         if (StringUtils.isNotEmpty(tstCase.getTestCaseId())) {
							    	         	myInput.close();
				    	         			    FileOutputStream outFile =new FileOutputStream(sb.toString());
				    	         			    myWorkBook.write(outFile);
				    	         			    outFile.close();
						    	         }
						        	 }
						         }
	   	     	            }
   	                }
    	        }
    	 } catch (Exception e) {
	     }
         return testCases;
    }

	private void updateTestCaseToXLSX(String fileName,
			com.photon.phresco.commons.model.TestCase tstCase,
			List<TestCase> testCases, StringBuilder sb)
			throws FileNotFoundException, InvalidFormatException, IOException,
			UnknownHostException, PhrescoException {
		FileInputStream myInput = new FileInputStream(sb.toString());
		    
		OPCPackage opc=OPCPackage.open(myInput); 
		
		XSSFWorkbook myWorkBook = new XSSFWorkbook(opc);
		int numberOfSheets = myWorkBook.getNumberOfSheets();
		 for (int j = 0; j < numberOfSheets; j++) {
			 XSSFSheet mySheet = myWorkBook.getSheetAt(j);
			 if(mySheet.getSheetName().equals(fileName)) {
				 Iterator<Row> rowIterator = mySheet.rowIterator();
		         for (int i = 0; i <=23; i++) {
						rowIterator.next();
					}
		         while (rowIterator.hasNext()) {
		     		Row next = rowIterator.next();
		     		if (StringUtils.isNotEmpty(getValue(next.getCell(1)))) {
		     			TestCase createObject = readTest(next);
		     			testCases.add(createObject);
		     			if (tstCase != null && createObject.getTestCaseId().equals(tstCase.getTestCaseId())) {
		     				Cell stepsCell=next.getCell(5);
		     				stepsCell.setCellValue(tstCase.getSteps());
		     				
		     				Cell expectedCell=next.getCell(8);
		     				expectedCell.setCellValue(tstCase.getExpectedResult());
		     				
		     				Cell actualCell=next.getCell(9);
		     				actualCell.setCellValue(tstCase.getActualResult());
		     				
		     				Cell statusCell=next.getCell(10);
		     				statusCell.setCellValue(tstCase.getStatus());
		     				
		     				Cell commentCell=next.getCell(13);
		     				commentCell.setCellValue(tstCase.getBugComment());
		     			   
		     			}
		     		}
		     		
		         }
		         if (StringUtils.isNotEmpty(tstCase.getTestCaseId())) {
		     		float totalPass = 0;
					float totalFail = 0;
					float totalNotApplicable = 0;
					float totalBlocked = 0;
					float notExecuted = 0;
					float totalTestCases = 0;
		     		for (TestCase testCase: testCases) {
		     			String testCaseStatus = testCase.getStatus();
		     			String testId = tstCase.getTestCaseId();
						String status = tstCase.getStatus();
		     			if(testCaseStatus.equalsIgnoreCase("Pass") && !testCase.getTestCaseId().equalsIgnoreCase(testId)) {
							totalPass = totalPass + 1;
						} else if (testCaseStatus.equalsIgnoreCase("Fail") && !testCase.getTestCaseId().equalsIgnoreCase(testId)) {
							totalFail = totalFail + 1;
						} else if (testCaseStatus.equalsIgnoreCase("notApplicable") && !testCase.getTestCaseId().equalsIgnoreCase(testId)) {
							totalNotApplicable = totalNotApplicable + 1;
						} else if (testCaseStatus.equalsIgnoreCase("blocked") && !testCase.getTestCaseId().equalsIgnoreCase(testId)) {
							totalBlocked = totalBlocked + 1;
						}
						
						if (testCase.getTestCaseId().equals(testId) && !testCase.getStatus().equalsIgnoreCase("Pass") 
								&& !testCase.getStatus().equalsIgnoreCase("success")
								&& status.equalsIgnoreCase("Pass") || status.equalsIgnoreCase("success")) {
							totalPass = totalPass +1;
						} else if (testCase.getTestCaseId().equals(testId)&& !testCase.getStatus().equalsIgnoreCase("Fail") 
								&& !testCase.getStatus().equalsIgnoreCase("failure")
								&& status.equalsIgnoreCase("Fail") || status.equalsIgnoreCase("failure")) {
							totalFail = totalFail + 1;
						}  else if (testCase.getTestCaseId().equals(testId)&& !testCase.getStatus().equalsIgnoreCase("notApplicable") 
								&& status.equalsIgnoreCase("notApplicable")) {
							totalNotApplicable = totalNotApplicable + 1;
						} else if (testCase.getTestCaseId().equals(testId)&& !testCase.getStatus().equalsIgnoreCase("blocked") 
								&& status.equalsIgnoreCase("blocked")) {
							totalBlocked = totalBlocked + 1;
						}
						totalTestCases = totalPass + totalFail + notExecuted + totalNotApplicable + totalBlocked;
						XSSFSheet mySheet1 = myWorkBook.getSheetAt(0);
						rowIterator = mySheet1.rowIterator();
						 for (int i = 0; i <=2; i++) {
								rowIterator.next();
							}
		                while (rowIterator.hasNext()) {
		            		Row next1 = rowIterator.next();
		            		if (StringUtils.isNotEmpty(getValue(next1.getCell(2))) && !getValue(next1.getCell(2)).equalsIgnoreCase("Total")) {
		            			TestSuite createObject = createObject(next1);
		                    	if (StringUtils.isNotEmpty(testId) && createObject.getName().equals(fileName)) {
			         				updateIndex(totalPass,
											totalFail,
											totalNotApplicable,
											totalBlocked, next1);
			         			}
		            		}
		                }
		     		}
		         }
		         if (StringUtils.isNotEmpty(tstCase.getTestCaseId())) {
		         	myInput.close();
				    FileOutputStream outFile =new FileOutputStream(sb.toString());
				    myWorkBook.write(outFile);
				    outFile.close();
		         }
			 }
		}
	}

	private void updateIndexPage(String fileName,
			com.photon.phresco.commons.model.TestCase tstCase,
			List<TestCase> testCases, HSSFWorkbook myWorkBook)
			throws UnknownHostException, PhrescoException {
		Iterator<Row> rowIterator;
		float totalPass = 0;
			float totalFail = 0;
			float totalNotApplicable = 0;
			float totalBlocked = 0;
			float notExecuted = 0;
			float totalTestCases = 0;
			for (TestCase testCase: testCases) {
				String testCaseStatus = testCase.getStatus();
				String testId = tstCase.getTestCaseId();
				String status = tstCase.getStatus();
				if(testCaseStatus.equalsIgnoreCase("Pass") && !testCase.getTestCaseId().equalsIgnoreCase(testId)) {
					totalPass = totalPass + 1;
				} else if (testCaseStatus.equalsIgnoreCase("Fail") && !testCase.getTestCaseId().equalsIgnoreCase(testId)) {
					totalFail = totalFail + 1;
				} else if (testCaseStatus.equalsIgnoreCase("notApplicable") && !testCase.getTestCaseId().equalsIgnoreCase(testId)) {
					totalNotApplicable = totalNotApplicable + 1;
				} else if (testCaseStatus.equalsIgnoreCase("blocked") && !testCase.getTestCaseId().equalsIgnoreCase(testId)) {
					totalBlocked = totalBlocked + 1;
				}
				
				if (testCase.getTestCaseId().equals(testId) && !testCase.getStatus().equalsIgnoreCase("Pass") 
						&& !testCase.getStatus().equalsIgnoreCase("success")
						&& status.equalsIgnoreCase("Pass") || status.equalsIgnoreCase("success")) {
					totalPass = totalPass +1;
				} else if (testCase.getTestCaseId().equals(testId)&& !testCase.getStatus().equalsIgnoreCase("Fail") 
						&& !testCase.getStatus().equalsIgnoreCase("failure")
						&& status.equalsIgnoreCase("Fail") || status.equalsIgnoreCase("failure")) {
					totalFail = totalFail + 1;
				}  else if (testCase.getTestCaseId().equals(testId)&& !testCase.getStatus().equalsIgnoreCase("notApplicable") 
						&& status.equalsIgnoreCase("notApplicable")) {
					totalNotApplicable = totalNotApplicable + 1;
				} else if (testCase.getTestCaseId().equals(testId)&& !testCase.getStatus().equalsIgnoreCase("blocked") 
						&& status.equalsIgnoreCase("blocked")) {
					totalBlocked = totalBlocked + 1;
				}
				totalTestCases = totalPass + totalFail + notExecuted + totalNotApplicable + totalBlocked;
				HSSFSheet mySheet1 = myWorkBook.getSheetAt(0);
				rowIterator = mySheet1.rowIterator();
				 for (int i = 0; i <=2; i++) {
						rowIterator.next();
					}
		        while (rowIterator.hasNext()) {
		    		Row next1 = rowIterator.next();
		    		if (StringUtils.isNotEmpty(getValue(next1.getCell(2))) && !getValue(next1.getCell(2)).equalsIgnoreCase("Total")) {
		    			TestSuite createObject = createObject(next1);
		            	if (StringUtils.isNotEmpty(testId) && createObject.getName().equals(fileName)) {
		     				updateIndex(
									totalPass,
									totalFail,
									totalNotApplicable,
									totalBlocked,
									next1);
		     			}
		    		}
		        }
			}
	}

	private void updateIndex(float totalPass, float totalFail,
			float totalNotApplicable, float totalBlocked, Row next1) {
		Cell successCell=next1.getCell(3);
		int pass = (int)totalPass;
		successCell.setCellValue(pass);
		
		Cell failureCell=next1.getCell(4);
		int fail = (int)totalFail;
		failureCell.setCellValue(fail);
		
		Cell notAppCell=next1.getCell(5);
		int notApp = (int)totalNotApplicable;
		notAppCell.setCellValue(notApp);
		
		Cell blockedCell=next1.getCell(7);
		int blocked = (int)totalBlocked;
		blockedCell.setCellValue(blocked);
		
		Cell cell = next1.getCell(8);
		double numericCellValue = cell.getNumericCellValue();
		
		Cell notExeCell=next1.getCell(6);
		int notExe = (int) (numericCellValue - (pass + fail + notApp + blocked));
		notExeCell.setCellValue(notExe);
		
		Cell testCovrgeCell=next1.getCell(9);
		int total = (int) cell.getNumericCellValue();
		float notExetd= notExe;
		float testCovrge = (float)((total-notExetd)/total)*100;
		testCovrgeCell.setCellValue(Math.round(testCovrge));
	}

	private void readTestFromSheet(com.photon.phresco.commons.model.TestCase tstCase,
			List<TestCase> testCases, HSSFSheet mySheet) {
		Iterator<Row> rowIterator = mySheet.rowIterator();
		 for (int i = 0; i <=23; i++) {
				rowIterator.next();
			}
		 while (rowIterator.hasNext()) {
			Row next = rowIterator.next();
			if (StringUtils.isNotEmpty(getValue(next.getCell(1)))) {
				TestCase createObject = readTest(next);
				testCases.add(createObject);
				if (tstCase != null && createObject.getTestCaseId().equals(tstCase.getTestCaseId())) {
					Cell stepsCell=next.getCell(5);
					stepsCell.setCellValue(tstCase.getSteps());
					
					Cell expectedCell=next.getCell(8);
					expectedCell.setCellValue(tstCase.getExpectedResult());
					
					Cell actualCell=next.getCell(9);
					actualCell.setCellValue(tstCase.getActualResult());
					
					Cell statusCell=next.getCell(10);
					statusCell.setCellValue(tstCase.getStatus());
					
					Cell commentCell=next.getCell(13);
					commentCell.setCellValue(tstCase.getBugComment());
				   
				}
			}
		 }
	}
    
    private TestCase readTest(Row next){
    	TestCase testcase = new TestCase();
    	if(next.getCell(1) != null) {
    		Cell cell = next.getCell(1);
    		String value = getValue(cell);
    		if(StringUtils.isNotEmpty(value)) {
    			testcase.setFeatureId(value);
    		}
    	}
    	if(next.getCell(3)!=null){
    		Cell cell = next.getCell(3);
    		String value=getValue(cell);
    		if(StringUtils.isNotEmpty(value)) {
    			testcase.setTestCaseId(value);
    		}
    	}
    	if(next.getCell(4)!=null){
    		Cell cell = next.getCell(4);
    		String value=getValue(cell);
    		if(StringUtils.isNotEmpty(value)) {
    			testcase.setDescription(value);
    		}
    	}
    	
    	if(next.getCell(5)!=null){
    		Cell cell=next.getCell(5);
    		String value=getValue(cell);
    		if(StringUtils.isNotEmpty(value)) {
    			testcase.setSteps(value);
    		}
    	}
    	if(next.getCell(8)!=null){
    		Cell cell=next.getCell(8);
    		String value=getValue(cell);
    		if(StringUtils.isNotEmpty(value)) {
    			testcase.setExpectedResult(value);
    		}
    	}
    	if(next.getCell(9)!=null){
    		Cell cell=next.getCell(9);
    		String value=getValue(cell);
    		if(StringUtils.isNotEmpty(value)) {
    			testcase.setActualResult(value);
    		}
    	}
    	if(next.getCell(10)!=null){
    		Cell cell=next.getCell(10);
    		String value=getValue(cell);
    		if(StringUtils.isNotEmpty(value)) {
    			testcase.setStatus(value);
    		}
    	}
    	if(next.getCell(13)!=null){
    		Cell cell=next.getCell(13);
    		String value=getValue(cell);
    		if(StringUtils.isNotEmpty(value)) {
    			testcase.setBugComment(value);
    		}
    	}
    	return testcase;
	}
    
    private static String getValue(Cell cell) {
    	if (cell != null) {
    		if (Cell.CELL_TYPE_STRING == cell.getCellType()) {
    			return cell.getStringCellValue();
    		}

    		if (Cell.CELL_TYPE_NUMERIC == cell.getCellType()) {
    			return String.valueOf(cell.getNumericCellValue());
    		}
    	}
    	
		return null;
	}
	
	public static String findPlatform() {
		String osName = System.getProperty(OS_NAME);
		String osBit = System.getProperty(OS_ARCH);
		if (osName.contains(WINDOWS)) {
			osName = WINDOWS;
		} else if (osName.contains(LINUX)) {
			osName = LINUX;
		} else if (osName.contains(MAC)) {
			osName = MAC;
		} else if (osName.contains(SERVER)) {
			osName = SERVER;
		} else if (osName.contains(WINDOWS7)) {
			osName = WINDOWS7.replace(" ", "");
		}
		if (osBit.contains(OS_BIT64)) {
			osBit = OS_BIT64;
		} else {
			osBit = OS_BIT86;
		}
		return osName.concat(osBit);
	}
	
	public void addNew(String filePath, String testName,String cellValue[]) {
		try {
			//FileInputStream myInput = new FileInputStream(filePath);

			int numCol;
			int cellno = 0;
			CellStyle tryStyle[] = new CellStyle[20];
			String sheetName = testName;
			//String cellValue[] = {"","",testName,success, fail,"","","",total,testCoverage,"","",""};
			Iterator<Row> rowIterator;
			File testDir = new File(filePath);
      		StringBuilder sb = new StringBuilder(filePath);
   	        if(testDir.isDirectory()) {
       	        	FilenameFilter filter = new PhrescoFileFilter("", "xlsx");
       	        	File[] listFiles = testDir.listFiles(filter);
       	        	if (listFiles.length != 0) {
						for (File file1 : listFiles) {
							 if (file1.isFile()) {
								sb.append(File.separator);
						    	sb.append(file1.getName());
						    }
						}
						FileInputStream myInput = new FileInputStream(sb.toString());
						OPCPackage opc=OPCPackage.open(myInput); 
						
						XSSFWorkbook myWorkBook = new XSSFWorkbook(opc);
						XSSFSheet mySheet = myWorkBook.getSheetAt(0);
						rowIterator = mySheet.rowIterator();
						numCol = 13;
						Row next;
						for (Cell cell : mySheet.getRow((mySheet.getLastRowNum()) - 2)) {
							tryStyle[cellno] = cell.getCellStyle();
							cellno = cellno + 1;
						}
						do {

							int flag = 0;
							next = rowIterator.next();
							if (mySheet.getSheetName().equalsIgnoreCase("Index")
									&& ((mySheet.getLastRowNum() - next.getRowNum()) < 3)) {
								for (Cell cell : next) {
									cell.setCellType(1);
									if (cell.getStringCellValue().equalsIgnoreCase("total")) {
										mySheet.shiftRows((mySheet.getLastRowNum() - 1),
												(mySheet.getPhysicalNumberOfRows() - 1), 1);
										flag = 1;
									}
									if (flag == 1)
										break;
								}
								if (flag == 1)
									break;
							}
						} while (rowIterator.hasNext());

						Row r = null;
						if (mySheet.getSheetName().equalsIgnoreCase("Index")) {
							r = mySheet.createRow(next.getRowNum() - 1);

						} else {
							r = mySheet.createRow(next.getRowNum() + 1);
						}
						for (int i = 0; i < numCol; i++) {
							Cell cell = r.createCell(i);
							cell.setCellValue(cellValue[i]);
							// used only when sheet is 'index'
							if (i == 2)
								sheetName = cellValue[i];

							cell.setCellStyle(tryStyle[i]);
						}
						if (mySheet.getSheetName().equalsIgnoreCase("Index")) {
							Sheet fromSheet = myWorkBook.getSheetAt((myWorkBook
									.getNumberOfSheets() - 1));
							Sheet toSheet = myWorkBook.createSheet(sheetName);
							int i = 0;
							Iterator<Row> copyFrom = fromSheet.rowIterator();
							Row fromRow, toRow;
							CellStyle newSheetStyle[] = new CellStyle[20];
							Integer newSheetType[] = new Integer[100];
							String newSheetValue[] = new String[100];
							do {
								fromRow = copyFrom.next();
								if (fromRow.getRowNum() == 24) {
									break;
								}
								toRow = toSheet.createRow(i);
								int numCell = 0;
								for (Cell cell : fromRow) {
									Cell newCell = toRow.createCell(numCell);

									cell.setCellType(1);

									newSheetStyle[numCell] = cell.getCellStyle();
									newCell.setCellStyle(newSheetStyle[numCell]);

									newSheetType[numCell] = cell.getCellType();
									newCell.setCellType(newSheetType[numCell]);
									if (fromRow.getCell(0).getStringCellValue().length() != 1
											&& fromRow.getCell(0).getStringCellValue()
													.length() != 2
											&& fromRow.getCell(0).getStringCellValue()
													.length() != 3) {
										newSheetValue[numCell] = cell.getStringCellValue();
										newCell.setCellValue(newSheetValue[numCell]);
									}

									numCell = numCell + 1;
								}
								i = i + 1;
							} while (copyFrom.hasNext());
						}
						// write to file
						FileOutputStream fileOut = new FileOutputStream(sb.toString());
						myWorkBook.write(fileOut);
						myInput.close();
						fileOut.close();
       	        	} else {
   	                	FilenameFilter xlsFilter = new PhrescoFileFilter("", "xls");
   	     	            File[] xlsListFiles = testDir.listFiles(xlsFilter);
   	     	            if (xlsListFiles.length != 0) {
		   	     	            for(File file2 : xlsListFiles) {
		   	     				if (file2.isFile()) {
		   	     					sb.append(File.separator);
		   	     			    	sb.append(file2.getName());
		   	     				}
		   	     			}
		   	     			FileInputStream myInput = new FileInputStream(sb.toString());
		   	     			HSSFWorkbook myWorkBook = new HSSFWorkbook(myInput);
		
		   	     			HSSFSheet mySheet = myWorkBook.getSheetAt(0);
		   	     			rowIterator = mySheet.rowIterator();
			   	     		numCol = 13;
							Row next;
							for (Cell cell : mySheet.getRow((mySheet.getLastRowNum()) - 2)) {
								tryStyle[cellno] = cell.getCellStyle();
								cellno = cellno + 1;
							}
							do {
	
								int flag = 0;
								next = rowIterator.next();
								if (mySheet.getSheetName().equalsIgnoreCase("Index")
										&& ((mySheet.getLastRowNum() - next.getRowNum()) < 3)) {
									for (Cell cell : next) {
										cell.setCellType(1);
										if (cell.getStringCellValue().equalsIgnoreCase("total")) {
											mySheet.shiftRows((mySheet.getLastRowNum() - 1),
													(mySheet.getPhysicalNumberOfRows() - 1), 1);
											flag = 1;
										}
										if (flag == 1)
											break;
									}
									if (flag == 1)
										break;
								}
							} while (rowIterator.hasNext());
	
							Row r = null;
							if (mySheet.getSheetName().equalsIgnoreCase("Index")) {
								r = mySheet.createRow(mySheet.getLastRowNum() - 2);
							} else {
								r = mySheet.createRow(next.getRowNum() + 1);
							}
							for (int i = 0; i < numCol; i++) {
								Cell cell = r.createCell(i);
								cell.setCellValue(cellValue[i]);
								// used only when sheet is 'index'
								if (i == 2)
									sheetName = cellValue[i];
	
								cell.setCellStyle(tryStyle[i]);
							}
							if (mySheet.getSheetName().equalsIgnoreCase("Index")) {
								Sheet fromSheet = myWorkBook.getSheetAt((myWorkBook
										.getNumberOfSheets() - 1));
								Sheet toSheet = myWorkBook.createSheet(sheetName);
								int i = 0;
								Iterator<Row> copyFrom = fromSheet.rowIterator();
								Row fromRow, toRow;
								CellStyle newSheetStyle[] = new CellStyle[20];
								Integer newSheetType[] = new Integer[100];
								String newSheetValue[] = new String[100];
								do {
									fromRow = copyFrom.next();
									if (fromRow.getRowNum() == 24) {
										break;
									}
									toRow = toSheet.createRow(i);
									int numCell = 0;
									for (Cell cell : fromRow) {
										Cell newCell = toRow.createCell(numCell);
	
										cell.setCellType(1);
	
										newSheetStyle[numCell] = cell.getCellStyle();
										newCell.setCellStyle(newSheetStyle[numCell]);
	
										newSheetType[numCell] = cell.getCellType();
										newCell.setCellType(newSheetType[numCell]);
										if (fromRow.getCell(0).getStringCellValue().length() != 1
												&& fromRow.getCell(0).getStringCellValue()
														.length() != 2
												&& fromRow.getCell(0).getStringCellValue()
														.length() != 3) {
											newSheetValue[numCell] = cell.getStringCellValue();
											newCell.setCellValue(newSheetValue[numCell]);
										}
	
										numCell = numCell + 1;
										if(numCell == 15) {
											break;
										}
									}
									i = i + 1;
								} while (copyFrom.hasNext());
							}
							// write to file
							FileOutputStream fileOut = new FileOutputStream(sb.toString());
							myWorkBook.write(fileOut);
							myInput.close();
							fileOut.close();
	       	        	}
   	        	}
   	        }
		} catch (Exception e) {
		}
	}

	public void addNewTestCase(String filePath, String testSuiteName,String cellValue[], String status) {
		try {
			int numCol = 14;
			int cellno = 0;
			CellStyle tryStyle[] = new CellStyle[20];
			//String cellValue[] = {"",featureId,"",testCaseId,testDesc,testSteps,testCaseType,priority,expectedResult,actualResult,status,"","",bugComment};
			Iterator<Row> rowIterator = null;
			File testDir = new File(filePath);
      		StringBuilder sb = new StringBuilder(filePath);
   	        if(testDir.isDirectory()) {
       	        	FilenameFilter filter = new PhrescoFileFilter("", "xlsx");
       	        	File[] listFiles = testDir.listFiles(filter);
       	        	if (listFiles.length != 0) {
						for (File file1 : listFiles) {
							 if (file1.isFile()) {
								sb.append(File.separator);
						    	sb.append(file1.getName());
						    }
						}
						writeTestCasesToXLSX(testSuiteName, cellValue, status, numCol, cellno, tryStyle, sb);
						
   	        	} else {
   	        		FilenameFilter xlsFilter = new PhrescoFileFilter("", "xls");
     	            File[] xlsListFiles = testDir.listFiles(xlsFilter);
     	           if (xlsListFiles.length != 0) {
	     	            for(File file2 : xlsListFiles) {
	     	            	if (file2.isFile()) {
	     	            		sb.append(File.separator);
	    	                	sb.append(file2.getName());
	     	            	}
	     	            }
	     	            writeTestCaseToXLS(testSuiteName, cellValue, status, numCol, cellno, tryStyle, sb);
     	           }
                }
   	        }
		} catch (Exception e) {
			//e.printStackTrace();
		}
	}

	private void writeTestCasesToXLSX(String testSuiteName, String[] cellValue,
			String status, int numCol, int cellno, CellStyle[] tryStyle,
			StringBuilder sb) throws FileNotFoundException,
			InvalidFormatException, IOException, UnknownHostException,
			PhrescoException {
		Iterator<Row> rowIterator;
		FileInputStream myInput = new FileInputStream(sb.toString());
		OPCPackage opc=OPCPackage.open(myInput); 
		XSSFWorkbook myWorkBook = new XSSFWorkbook(opc);
		int numberOfSheets = myWorkBook.getNumberOfSheets();
		for (int j = 0; j < numberOfSheets; j++) {
			XSSFSheet mySheet = myWorkBook.getSheetAt(j);
			if(mySheet.getSheetName().equals(testSuiteName)) {
				rowIterator = mySheet.rowIterator();
				Row next;
				for (Cell cell : mySheet.getRow((mySheet.getLastRowNum()) - 2)) {
					tryStyle[cellno] = cell.getCellStyle();
					cellno = cellno + 1;
				}
				float totalPass = 0;
				float totalFail = 0;
				float totalNotApp = 0;
				float totalBlocked = 0;
				float notExecuted = 0;
				float totalTestCases = 0;
				do {
					next = rowIterator.next();
					if (StringUtils.isNotEmpty(getValue(next.getCell(1))) && !getValue(next.getCell(0)).equalsIgnoreCase("S.NO")) {
						String value = getValue(next.getCell(10));
						if (StringUtils.isNotEmpty(value)) {
							if (value.equalsIgnoreCase("pass") || value.equalsIgnoreCase("success")) {
								totalPass = totalPass + 1;
							} else if(value.equalsIgnoreCase("fail") || value.equalsIgnoreCase("failure")) {
								totalFail = totalFail + 1;
							} else if(value.equalsIgnoreCase("notApplicable")) {
								totalNotApp = totalNotApp + 1;
							} else if(value.equalsIgnoreCase("blocked")) {
								totalBlocked = totalBlocked + 1;
							} 
						}else {
							notExecuted = notExecuted + 1;
						}
					}
				} while (rowIterator.hasNext());
				//to update the status in the index page 
				if (status.equalsIgnoreCase("pass") || status.equalsIgnoreCase("success")) {
					totalPass = totalPass + 1;
				} else if (status.equalsIgnoreCase("fail") || status.equalsIgnoreCase("failure")) {
					totalFail = totalFail + 1;
				} else if (status.equalsIgnoreCase("notApplicable")) {
					totalNotApp = totalNotApp + 1;
				} else if (status.equalsIgnoreCase("blocked")) {
					totalBlocked = totalBlocked + 1;
				} else {
					notExecuted = notExecuted + 1;
				}
				totalTestCases = totalPass + totalFail + totalNotApp + totalBlocked + notExecuted;
				XSSFSheet mySheet1 = myWorkBook.getSheetAt(0);
				rowIterator = mySheet1.rowIterator();
				 for (int i = 0; i <=2; i++) {
						rowIterator.next();
					}
		            while (rowIterator.hasNext()) {
		        		Row next1 = rowIterator.next();
		        		if (StringUtils.isNotEmpty(getValue(next1.getCell(2))) && !getValue(next1.getCell(2)).equalsIgnoreCase("Total")) {
		        			TestSuite createObject = createObject(next1);
		                	if (createObject.getName().equals(testSuiteName)) {
		         				addCalculationsToIndex(
										totalPass, totalFail,
										totalNotApp,
										totalBlocked,
										notExecuted,
										totalTestCases, next1);
		         			}
		        		}
		            }
				
				Row r = null;
				if (mySheet.getSheetName().equalsIgnoreCase("Index")) {
					r = mySheet.createRow(next.getRowNum() - 1);
				
				} else {
					r = mySheet.createRow(next.getRowNum() + 1);
				}
				for (int i = 0; i < numCol; i++) {
					Cell cell = r.createCell(i);
					cell.setCellValue(cellValue[i]);
				
					cell.setCellStyle(tryStyle[i]);
				}
				FileOutputStream fileOut = new FileOutputStream(sb.toString());
				myWorkBook.write(fileOut);
				myInput.close();
				fileOut.close();
			}
			 	
		}
	}

	private void writeTestCaseToXLS(String testSuiteName, String[] cellValue,
			String status, int numCol, int cellno, CellStyle[] tryStyle,
			StringBuilder sb) throws FileNotFoundException, IOException,
			UnknownHostException, PhrescoException {
		Iterator<Row> rowIterator;
		FileInputStream myInput = new FileInputStream(sb.toString());
		HSSFWorkbook myWorkBook = new HSSFWorkbook(myInput);
		HSSFSheet mySheet;
		int numberOfSheets = myWorkBook.getNumberOfSheets();
		 for (int j = 0; j < numberOfSheets; j++) {
				HSSFSheet myHssfSheet = myWorkBook.getSheetAt(j);
				if(myHssfSheet.getSheetName().equals(testSuiteName)) {
					rowIterator = myHssfSheet.rowIterator();
					Row next;
					for (Cell cell : myHssfSheet.getRow((myHssfSheet.getLastRowNum()) - 2)) {
						tryStyle[cellno] = cell.getCellStyle();
						cellno = cellno + 1;
						if(cellno == 16){
							break;
						}
					}
					float totalPass = 0;
					float totalFail = 0;
					float totalNotApp = 0;
					float totalBlocked = 0;
					float notExecuted = 0;
					float totalTestCases = 0;
					do {
						next = rowIterator.next();
						if (StringUtils.isNotEmpty(getValue(next.getCell(1))) && !getValue(next.getCell(0)).equalsIgnoreCase("S.NO")) {
							String value = getValue(next.getCell(10));
							if (StringUtils.isNotEmpty(value)) {
								if (value.equalsIgnoreCase("pass") || value.equalsIgnoreCase("success")) {
									totalPass = totalPass + 1;
								} else if(value.equalsIgnoreCase("fail") || value.equalsIgnoreCase("failure")) {
									totalFail = totalFail + 1;
								}  else if(value.equalsIgnoreCase("notApplicable")) {
									totalNotApp = totalNotApp + 1;
								}  else if(value.equalsIgnoreCase("blocked")) {
									totalBlocked = totalBlocked + 1;
								} 
							}else {
								notExecuted = notExecuted + 1;
							}
						}
					} while (rowIterator.hasNext());
					//to update the status in the index page 
					if (status.equalsIgnoreCase("pass") || status.equalsIgnoreCase("success")) {
						totalPass = totalPass + 1;
					} else if (status.equalsIgnoreCase("fail") || status.equalsIgnoreCase("failure")) {
						totalFail = totalFail + 1;
					} else if (status.equalsIgnoreCase("notApplicable")) {
						totalNotApp = totalNotApp + 1;
					} else if (status.equalsIgnoreCase("blocked")) {
						totalBlocked = totalBlocked + 1;
					} else {
						notExecuted = notExecuted + 1;
					}
					totalTestCases = totalPass + totalFail + totalNotApp + totalBlocked + notExecuted;
					HSSFSheet mySheetHssf = myWorkBook.getSheetAt(0);
					rowIterator = mySheetHssf.rowIterator();
					 for (int i = 0; i <=2; i++) {
							rowIterator.next();
						}
		                while (rowIterator.hasNext()) {
		            		Row next1 = rowIterator.next();
		            		if (StringUtils.isNotEmpty(getValue(next1.getCell(2))) && !getValue(next1.getCell(2)).equalsIgnoreCase("Total")) {
		            			TestSuite createObject = createObject(next1);
		                    	if (createObject.getName().equals(testSuiteName)) {
			         				addCalculationsToIndex(
											totalPass, totalFail,
											totalNotApp,
											totalBlocked,
											notExecuted,
											totalTestCases, next1);
			         			}
		            		}
		                }
					
					Row r = null;
					if (myHssfSheet.getSheetName().equalsIgnoreCase("Index")) {
						r = myHssfSheet.createRow(next.getRowNum() - 1);
					
					} else {
						r = myHssfSheet.createRow(next.getRowNum() + 1);
					}
					for (int i = 0; i < numCol; i++) {
						Cell cell = r.createCell(i);
						cell.setCellValue(cellValue[i]);
						if (tryStyle[i] != null) {
							cell.setCellStyle(tryStyle[i]);
						}
					}
					FileOutputStream fileOut = new FileOutputStream(sb.toString());
					myWorkBook.write(fileOut);
					myInput.close();
					fileOut.close();
				}
		    	 	
			}
	}

	private static void addCalculationsToIndex(float totalPass,
			float totalFail, float totalNotApp, float totalBlocked,
			float notExecuted, float totalTestCases, Row next1) {
		Cell successCell=next1.getCell(3);
		int pass = (int)totalPass;
		successCell.setCellValue(pass);
		
		Cell failureCell=next1.getCell(4);
		int fail = (int)totalFail;
		failureCell.setCellValue(fail);
		
		Cell notAppCell=next1.getCell(5);
		int notApp = (int)totalNotApp;
		notAppCell.setCellValue(notApp);
		
		Cell notExeCell=next1.getCell(6);
		int notExe = (int)notExecuted;
		notExeCell.setCellValue(notExe);
		
		Cell blockedCell=next1.getCell(7);
		int blocked = (int)totalBlocked;
		blockedCell.setCellValue(blocked);
		
		Cell totalCell=next1.getCell(8);
		int totalTests = (int)totalTestCases;
		totalCell.setCellValue(totalTests);
       			   
		Cell testCovrgeCell=next1.getCell(9);
		float notExetd= notExe;
		float testCovrge = (float)((totalTests-notExetd)/totalTests)*100;
		testCovrgeCell.setCellValue(Math.round(testCovrge));
	}
	
	public static int getHttpsResponse(String url) throws PhrescoException {
		URL httpsUrl;
		try {
			SSLContext ssl_ctx = SSLContext.getInstance("TLS");
			TrustManager[] trust_mgr = get_trust_mgr();
			ssl_ctx.init(null, trust_mgr, new SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(ssl_ctx.getSocketFactory());
			httpsUrl = new URL(url);
			HttpsURLConnection con = (HttpsURLConnection) httpsUrl.openConnection();
			con.setHostnameVerifier(new HostnameVerifier() {
				 // Guard against "bad hostname" errors during handshake.	
				public boolean verify(String host, SSLSession sess) {
					return true;
				}
			});
			return con.getResponseCode();
		} catch (MalformedURLException e) {
			throw new PhrescoException(e);
		} catch (IOException e) {
			throw new PhrescoException(e);
		} catch (NoSuchAlgorithmException e) {
			throw new PhrescoException(e);
		} catch (KeyManagementException e) {
			throw new PhrescoException(e);
		}
	}
	
	private static TrustManager[ ] get_trust_mgr() {
	     TrustManager[ ] certs = new TrustManager[ ] {
	        new X509TrustManager() {
			@Override
			public void checkClientTrusted(
					java.security.cert.X509Certificate[] chain, String authType)
					throws CertificateException {
				
			}
			@Override
			public void checkServerTrusted(
					java.security.cert.X509Certificate[] chain, String authType)
					throws CertificateException {
				// TODO Auto-generated method stub
				
			}
			@Override
			public java.security.cert.X509Certificate[] getAcceptedIssuers() {
				// TODO Auto-generated method stub
				return null;
			}
	         }
	      };
	      return certs;
	  }

	public static boolean isCharacterExists(String string) {
		if (string.matches(".*[a-zA-Z0-9]+.*")) {
			return true;
		}
		return false;
	}
}

class PhrescoFileFilter implements FilenameFilter {
	private String name;
	private String extension;

	public PhrescoFileFilter(String name, String extension) {
		this.name = name;
		this.extension = extension;
	}

	public boolean accept(File directory, String filename) {
		boolean fileOK = true;

		if (name != null) {
			fileOK &= filename.startsWith(name);
		}

		if (extension != null) {
			fileOK &= filename.endsWith('.' + extension);
		}
		return fileOK;
	}
}