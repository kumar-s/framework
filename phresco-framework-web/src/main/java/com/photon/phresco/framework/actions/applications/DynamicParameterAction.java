package com.photon.phresco.framework.actions.applications;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.antlr.stringtemplate.StringTemplate;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.photon.phresco.api.DynamicPageParameter;
import com.photon.phresco.api.DynamicParameter;
import com.photon.phresco.commons.model.ApplicationInfo;
import com.photon.phresco.commons.model.ArtifactGroup;
import com.photon.phresco.commons.model.ArtifactInfo;
import com.photon.phresco.commons.model.Customer;
import com.photon.phresco.commons.model.RepoInfo;
import com.photon.phresco.exception.PhrescoException;
import com.photon.phresco.framework.actions.FrameworkBaseAction;
import com.photon.phresco.framework.commons.FrameworkUtil;
import com.photon.phresco.framework.commons.ParameterModel;
import com.photon.phresco.framework.model.DependantParameters;
import com.photon.phresco.plugins.model.Mojos.Mojo.Configuration.Parameters.Parameter;
import com.photon.phresco.plugins.model.Mojos.Mojo.Configuration.Parameters.Parameter.Childs.Child;
import com.photon.phresco.plugins.model.Mojos.Mojo.Configuration.Parameters.Parameter.MavenCommands.MavenCommand;
import com.photon.phresco.plugins.model.Mojos.Mojo.Configuration.Parameters.Parameter.PossibleValues;
import com.photon.phresco.plugins.model.Mojos.Mojo.Configuration.Parameters.Parameter.PossibleValues.Value;
import com.photon.phresco.plugins.util.MojoProcessor;
import com.photon.phresco.util.Constants;
import com.photon.phresco.util.PhrescoDynamicLoader;
import com.photon.phresco.util.Utility;

public class DynamicParameterAction extends FrameworkBaseAction implements Constants {

	private static final long serialVersionUID = 1L;
	
	private static final Logger S_LOGGER = Logger.getLogger(Quality.class);
    private static Boolean s_debugEnabled  =S_LOGGER.isDebugEnabled();
    
    //Dynamic parameter
    private List<Value> dependentValues = null; //Value for dependancy parameters
    private String dynamicPageParameterDesign = "";
    private String currentParamKey = ""; 
    private String goal = "";
    private String selectedOption = "";
    private String dependency = "";
    private String fileType = ""; //for file browse 
    private String fileOrFolder = ""; //for file browse
    	
	private boolean paramaterAvailable;
    
    private String availableParams = "";
    private final String PHASE_FUNCTIONAL_TEST_WEBDRIVER = "functional-test-webdriver";
    private final String PHASE_FUNCTIONAL_TEST_GRID = "functional-test-grid";
    private final String PHASE_FUNCTIONAL_TEST = "functional-test";
    private final String PHASE_RUNAGAINST_SOURCE = "run-against-source";
    
    
    private static Map<String, PhrescoDynamicLoader> pdlMap = new HashMap<String, PhrescoDynamicLoader>();
    
   
	/**
	 * To the phresco plugin info file path based on the goal
	 * @param goal
	 * @return
	 * @throws PhrescoException 
	 */
	public String getPhrescoPluginInfoFilePath(String goal) throws PhrescoException {
		StringBuilder sb = new StringBuilder(getApplicationHome());
		sb.append(File.separator);
		sb.append(FOLDER_DOT_PHRESCO);
		sb.append(File.separator);
		sb.append(PHRESCO_HYPEN);
		if (PHASE_FUNCTIONAL_TEST_WEBDRIVER.equals(goal) || PHASE_FUNCTIONAL_TEST_GRID.equals(goal)) {
			sb.append(PHASE_FUNCTIONAL_TEST);
		} else if (PHASE_RUNGAINST_SRC_START.equals(goal)|| PHASE_RUNGAINST_SRC_STOP.equals(goal) ) {
			sb.append(PHASE_RUNAGAINST_SOURCE);
		}
		else {
			sb.append(goal);
		}
		sb.append(INFO_XML);
		return sb.toString();
	}
    
	public String getPhrescoPluginInfoXmlFilePath(String goal, ApplicationInfo appInfo) throws PhrescoException {
		StringBuilder sb = new StringBuilder(Utility.getProjectHome());
		sb.append(appInfo.getAppDirName());
		sb.append(File.separator);
		sb.append(FOLDER_DOT_PHRESCO);
		sb.append(File.separator);
		sb.append(PHRESCO_HYPEN);
		if (PHASE_FUNCTIONAL_TEST_WEBDRIVER.equals(goal) || PHASE_FUNCTIONAL_TEST_GRID.equals(goal)) {
			sb.append(PHASE_FUNCTIONAL_TEST);
		}else if (PHASE_RUNGAINST_SRC_START.equals(goal)|| PHASE_RUNGAINST_SRC_STOP.equals(goal) ) {
			sb.append(PHASE_RUNAGAINST_SOURCE);
		} else {
			sb.append(goal);
		}
		sb.append(INFO_XML);
		return sb.toString();
	}
	
	/**
     * To get list of parameters from phresco-plugin-info.xml
     * @param applicationInfo
     * @param goal
     * @return List<Parameter>
     * @throws PhrescoException
     */
    protected List<Parameter> getMojoParameters(MojoProcessor mojo, String goal) throws PhrescoException {
		com.photon.phresco.plugins.model.Mojos.Mojo.Configuration mojoConfiguration = mojo.getConfiguration(goal);
		if (mojoConfiguration != null) {
		    return mojoConfiguration.getParameters().getParameter();
		}
		
		return null;
	}
    
    protected List<Parameter> getDynamicParameters(ApplicationInfo appInfo, String goal) throws PhrescoException {
        MojoProcessor mojo = new MojoProcessor(new File(getPhrescoPluginInfoFilePath(goal)));
        com.photon.phresco.plugins.model.Mojos.Mojo.Configuration mojoConfiguration = mojo.getConfiguration(goal);
        List<Parameter> parameters = null;
        if (mojoConfiguration != null) {
            parameters = mojoConfiguration.getParameters().getParameter();
        }
        
        return parameters;
    }
    
    protected List<Parameter> getDynamicParameters(String goal) throws PhrescoException {
        MojoProcessor mojo = new MojoProcessor(new File(getPhrescoPluginInfoFilePath(goal)));
        com.photon.phresco.plugins.model.Mojos.Mojo.Configuration mojoConfiguration = mojo.getConfiguration(goal);
        List<Parameter> parameters = null;
        if (mojoConfiguration != null) {
            parameters = mojoConfiguration.getParameters().getParameter();
        }
        
        return parameters;
    }
    
    /**
     * To set List of parameters in request
     * @param mojo TODO
     * @param appInfo
     * @param goal
     * @throws PhrescoException
     */
    protected void setPossibleValuesInReq(MojoProcessor mojo, ApplicationInfo appInfo, List<Parameter> parameters, Map<String, DependantParameters> watcherMap) throws PhrescoException {
        try {
            if (CollectionUtils.isNotEmpty(parameters)) {
                StringBuilder paramBuilder = new StringBuilder();
                for (Parameter parameter : parameters) {
                    String parameterKey = parameter.getKey();
                    if (parameter.getDynamicParameter() != null) { //Dynamic parameter
                        Map<String, Object> constructMapForDynVals = constructMapForDynVals(appInfo, watcherMap, parameterKey);
                        
                        // Get the values from the dynamic parameter class
                        List<Value> dynParamPossibleValues = getDynamicPossibleValues(constructMapForDynVals, parameter);
                        addValueDependToWatcher(watcherMap, parameterKey, dynParamPossibleValues);
                        if (watcherMap.containsKey(parameterKey)) {
                            DependantParameters dependantParameters = (DependantParameters) watcherMap.get(parameterKey);
                            if (CollectionUtils.isNotEmpty(dynParamPossibleValues)) {
                                dependantParameters.setValue(dynParamPossibleValues.get(0).getValue());
                            }
                        }
                        
                        setReqAttribute(REQ_DYNAMIC_POSSIBLE_VALUES + parameter.getKey(), dynParamPossibleValues);
                        if (CollectionUtils.isNotEmpty(dynParamPossibleValues)) {
                        	addWatcher(watcherMap, parameter.getDependency(), parameterKey, dynParamPossibleValues.get(0).getValue());
                        }
                        if (StringUtils.isNotEmpty(paramBuilder.toString())) {
                            paramBuilder.append("&");
                        }
                        paramBuilder.append(parameterKey);
                        paramBuilder.append("=");
                        if (CollectionUtils.isNotEmpty(dynParamPossibleValues)) {
                            paramBuilder.append(dynParamPossibleValues.get(0).getValue());
                        }
                    } else if (parameter.getPossibleValues() != null) { //Possible values
                        List<Value> values = parameter.getPossibleValues().getValue();
                        
                        if (watcherMap.containsKey(parameterKey)) {
                            DependantParameters dependantParameters = (DependantParameters) watcherMap.get(parameterKey);
                            dependantParameters.setValue(values.get(0).getValue());
                        }
                        
                        addValueDependToWatcher(watcherMap, parameterKey, values);
                        if (CollectionUtils.isNotEmpty(values)) {
                            addWatcher(watcherMap, parameter.getDependency(), parameterKey, values.get(0).getValue());
                        }
                        if (StringUtils.isNotEmpty(paramBuilder.toString())) {
                            paramBuilder.append("&");
                        }
                        paramBuilder.append(parameterKey);
                        paramBuilder.append("=");
                        paramBuilder.append("");
                    } else if (parameter.getType().equalsIgnoreCase(TYPE_BOOLEAN) && StringUtils.isNotEmpty(parameter.getDependency())) { //Checkbox
                        addWatcher(watcherMap, parameter.getDependency(), parameterKey, parameter.getValue());
                        if (StringUtils.isNotEmpty(paramBuilder.toString())) {
                            paramBuilder.append("&");
                        }
                        paramBuilder.append(parameterKey);
                        paramBuilder.append("=");
                        paramBuilder.append("");
                    }
                }
                setAvailableParams(paramBuilder.toString());
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

	private void addValueDependToWatcher(Map<String, DependantParameters> watcherMap, String parameterKey, List<Value> values) {
		for (Value value : values) {
		    if (StringUtils.isNotEmpty(value.getDependency())) {
		        addWatcher(watcherMap, value.getDependency(), parameterKey, value.getKey());
		    }
		}
	}

    private void addWatcher(Map<String, DependantParameters> watcherMap, String dependency, String parameterKey, String parameterValue) {
    	
        if (StringUtils.isNotEmpty(dependency)) {
            List<String> dependencyKeys = Arrays.asList(dependency.split(CSV_PATTERN));
            for (String dependentKey : dependencyKeys) {
                DependantParameters dependantParameters;
                if (watcherMap.containsKey(dependentKey)) {
                    dependantParameters = (DependantParameters) watcherMap.get(dependentKey);
                } else {
                    dependantParameters = new DependantParameters();
                }
                dependantParameters.getParentMap().put(parameterKey, parameterValue);
                addParentToWatcher(watcherMap, parameterKey, parameterValue);
                watcherMap.put(dependentKey, dependantParameters);
            }
        }
    }
    
    private void addParentToWatcher(Map<String, DependantParameters> watcherMap, String parameterKey, String parameterValue) {
    	
    	DependantParameters dependantParameters;
        if (watcherMap.containsKey(parameterKey)) {
            dependantParameters = (DependantParameters) watcherMap.get(parameterKey);
        } else {
            dependantParameters = new DependantParameters();
        }
    	dependantParameters.setValue(parameterValue);
    	watcherMap.put(parameterKey, dependantParameters);
    }
    
    protected Map<String, Object> constructMapForDynVals(ApplicationInfo appInfo, Map<String, DependantParameters> watcherMap, String parameterKey) {
        Map<String, Object> paramMap = new HashMap<String, Object>(8);
        DependantParameters dependantParameters = watcherMap.get(parameterKey);
        if (dependantParameters != null) {
            paramMap.putAll(getDependantParameters(dependantParameters.getParentMap(), watcherMap));
        }
        paramMap.put(DynamicParameter.KEY_APP_INFO, appInfo);
        paramMap.put(REQ_CUSTOMER_ID, getCustomerId());
        if (StringUtils.isNotEmpty(getReqParameter(BUILD_NUMBER))) {
        	paramMap.put(DynamicParameter.KEY_BUILD_NO, getReqParameter(BUILD_NUMBER));
        }

        return paramMap;
    }
    
    private Map<String, Object> getDependantParameters(Map<String, String> parentMap, Map<String, DependantParameters> watcherMap) {
        Map<String, Object> paramMap = new HashMap<String, Object>(8);
        Set<String> keySet = parentMap.keySet();
        for (String key : keySet) {
            if (watcherMap.get(key) != null) {
                String value = ((DependantParameters) watcherMap.get(key)).getValue();
                paramMap.put(key, value);
            }
        }

        return paramMap;
    }

   /* protected void setDependencyToWatcher(Map<String, DependantParameters> watcherMap, List<String> dependencyKeys,  
            String parentParamKey, String parentParamValue) {
        for (String dependentKey : dependencyKeys) {
            DependantParameters dependantParameters;
            if (watcherMap.containsKey(dependentKey)) {
                dependantParameters = (DependantParameters) watcherMap.get(dependentKey);
            } else {
                dependantParameters = new DependantParameters();
            }
            dependantParameters.getParentMap().put(parentParamKey, parentParamValue);
            System.out.println("adding watcher " + dependentKey);
            watcherMap.put(dependentKey, dependantParameters);
        }
    }*/

    /**
     * To set List of Possible values as Dynamic parameter in request
     * @param watcherMap
     * @param parameter
     * @return
     * @throws PhrescoException
     */
    protected List<Value> getDynamicPossibleValues(Map<String, Object> watcherMap, Parameter parameter) throws PhrescoException {
        PossibleValues possibleValue = getDynamicValues(watcherMap, parameter);
        List<Value> possibleValues = (List<Value>) possibleValue.getValue();
        return possibleValues;
    }
	
	private PossibleValues getDynamicValues(Map<String, Object> watcherMap, Parameter parameter) throws PhrescoException {
		try {
			String className = parameter.getDynamicParameter().getClazz();
			DynamicParameter dynamicParameter;
			PhrescoDynamicLoader phrescoDynamicLoader = pdlMap.get(getCustomerId());
			if (MapUtils.isNotEmpty(pdlMap) && phrescoDynamicLoader != null) {
				dynamicParameter = phrescoDynamicLoader.getDynamicParameter(className);
			} else {
				//To get repo info from Customer object
				Customer customer = getServiceManager().getCustomer(getCustomerId());
				RepoInfo repoInfo = customer.getRepoInfo();
				//To set groupid,artfid,type infos to List<ArtifactGroup>
				List<ArtifactGroup> artifactGroups = new ArrayList<ArtifactGroup>();
				ArtifactGroup artifactGroup = new ArtifactGroup();
				artifactGroup.setGroupId(parameter.getDynamicParameter().getDependencies().getDependency().getGroupId());
				artifactGroup.setArtifactId(parameter.getDynamicParameter().getDependencies().getDependency().getArtifactId());
				artifactGroup.setPackaging(parameter.getDynamicParameter().getDependencies().getDependency().getType());
				//to set version
				List<ArtifactInfo> artifactInfos = new ArrayList<ArtifactInfo>();
		        ArtifactInfo artifactInfo = new ArtifactInfo();
		        artifactInfo.setVersion(parameter.getDynamicParameter().getDependencies().getDependency().getVersion());
				artifactInfos.add(artifactInfo);
		        artifactGroup.setVersions(artifactInfos);
				artifactGroups.add(artifactGroup);
				
				//dynamically loads specified Class
				phrescoDynamicLoader = new PhrescoDynamicLoader(repoInfo, artifactGroups);
				dynamicParameter = phrescoDynamicLoader.getDynamicParameter(className);
				pdlMap.put(getCustomerId(), phrescoDynamicLoader);
			}
			
			return dynamicParameter.getValues(watcherMap);
		} catch (Exception e) {
			throw new PhrescoException(e);
		}
	}
	
	protected List<? extends Object> getDynamicPageParameter(ApplicationInfo appInfo, Map<String, DependantParameters> watcherMap, Parameter parameter) throws PhrescoException {
		String parameterKey = parameter.getKey();
		Map<String, Object> paramsMap = constructMapForDynVals(appInfo, watcherMap, parameterKey);
		String className = parameter.getDynamicParameter().getClazz();
		DynamicPageParameter dynamicPageParameter;
		PhrescoDynamicLoader phrescoDynamicLoader = pdlMap.get(getCustomerId());
		if (MapUtils.isNotEmpty(pdlMap) && phrescoDynamicLoader != null) {
			dynamicPageParameter = phrescoDynamicLoader.getDynamicPageParameter(className);
		} else {
			//To get repo info from Customer object
			Customer customer = getServiceManager().getCustomer(getCustomerId());
			RepoInfo repoInfo = customer.getRepoInfo();
			//To set groupid,artfid,type infos to List<ArtifactGroup>
			List<ArtifactGroup> artifactGroups = new ArrayList<ArtifactGroup>();
			ArtifactGroup artifactGroup = new ArtifactGroup();
			artifactGroup.setGroupId(parameter.getDynamicParameter().getDependencies().getDependency().getGroupId());
			artifactGroup.setArtifactId(parameter.getDynamicParameter().getDependencies().getDependency().getArtifactId());
			artifactGroup.setPackaging(parameter.getDynamicParameter().getDependencies().getDependency().getType());
			//to set version
			List<ArtifactInfo> artifactInfos = new ArrayList<ArtifactInfo>();
	        ArtifactInfo artifactInfo = new ArtifactInfo();
	        artifactInfo.setVersion(parameter.getDynamicParameter().getDependencies().getDependency().getVersion());
			artifactInfos.add(artifactInfo);
	        artifactGroup.setVersions(artifactInfos);
			artifactGroups.add(artifactGroup);
			
			//dynamically loads specified Class
			phrescoDynamicLoader = new PhrescoDynamicLoader(repoInfo, artifactGroups);
			dynamicPageParameter = phrescoDynamicLoader.getDynamicPageParameter(className);
			pdlMap.put(getCustomerId(), phrescoDynamicLoader);
		}
		
		return dynamicPageParameter.getObjects(paramsMap);
	}
	
	/**
	 * To persist entered values into phresco-plugin-info.xml
	 * @throws IOException 
	 */
	protected void persistValuesToXml(MojoProcessor mojo, String goal) throws PhrescoException {
	    try {
	        List<Parameter> parameters = getMojoParameters(mojo, goal);
	        StringBuilder csParamVal = new StringBuilder();
	        String sep = "";
	        if (CollectionUtils.isNotEmpty(parameters)) {
	            for (Parameter parameter : parameters) {
	                if (Boolean.parseBoolean(parameter.getMultiple())) {
	                    String[] parameterValues = getReqParameterValues(parameter.getKey());
	                    for (String parameterValue : parameterValues) {
	                        csParamVal.append(sep);
	                        csParamVal.append(parameterValue);
	                        sep = ",";
	                    }
	                    parameter.setValue(csParamVal.toString());
	                } else if (TYPE_BOOLEAN.equalsIgnoreCase(parameter.getType())) {
	                    if (getReqParameter(parameter.getKey()) != null) {
	                        parameter.setValue(getReqParameter(parameter.getKey()));
	                    } else {
	                        parameter.setValue(Boolean.FALSE.toString());
	                    }
	                } else if (parameter.getType().equalsIgnoreCase(TYPE_MAP)) {
	                    List<Child> childs = parameter.getChilds().getChild();
	                    String[] keys = getReqParameterValues(childs.get(0).getKey());
	                    String[] values = getReqParameterValues(childs.get(1).getKey());
	                    Properties properties = new Properties();
	                    for (int i = 0; i < keys.length; i++) {
	                        properties.put(keys[i], values[i]);
	                    }
	                    StringWriter writer = new StringWriter();
	                    properties.store(writer, "");
	                    String value = writer.getBuffer().toString();
	                    parameter.setValue(value);
	                } else {
	                    parameter.setValue(getReqParameter(parameter.getKey()));
	                }
	            }
	        }
	        mojo.save();
	    } catch (IOException e) {
	        throw new PhrescoException(e);
	    }
	}

	/**
	 * To get list of maven command arguments
	 * @param parameters
	 * @return
	 */
	protected List<String> getMavenArgCommands(List<Parameter> parameters) {
		List<String> buildArgCmds = new ArrayList<String>();			
		for (Parameter parameter : parameters) {
			if (parameter.getPluginParameter()!= null && PLUGIN_PARAMETER_FRAMEWORK.equalsIgnoreCase(parameter.getPluginParameter())) {
				List<MavenCommand> mavenCommand = parameter.getMavenCommands().getMavenCommand();
				for (MavenCommand mavenCmd : mavenCommand) {
					if (StringUtils.isNotEmpty(parameter.getValue()) && parameter.getValue().equalsIgnoreCase(mavenCmd.getKey())) {
						buildArgCmds.add(mavenCmd.getValue());
					}
				}
			}
		}
		return buildArgCmds;
	}
	
	public String changeEveDependancyListener() {
        if (s_debugEnabled) {
            S_LOGGER.debug("Entering Method Quality.changeEveDependancyListener()");
        }
        
        Map<String, DependantParameters> watcherMap = (Map<String, DependantParameters>) 
                getSessionAttribute(getAppId() + getGoal() + SESSION_WATCHER_MAP);
        
        DependantParameters currentParameters = watcherMap.get(getCurrentParamKey());
        if (currentParameters == null) {
            currentParameters = new DependantParameters();
        }
        currentParameters.setValue(getSelectedOption());
        watcherMap.put(getCurrentParamKey(), currentParameters);

        return SUCCESS;
    }
    
    public String updateDependancy() throws IOException {
        if (s_debugEnabled) {
            S_LOGGER.debug("Entering Method Quality.updateDependancy()");
        }
        
        try {
            ApplicationInfo applicationInfo = getApplicationInfo();
            MojoProcessor mojo = new MojoProcessor(new File(getPhrescoPluginInfoFilePath(getGoal())));
            Map<String, DependantParameters> watcherMap = (Map<String, DependantParameters>) getSessionAttribute(getAppId() + getGoal() + SESSION_WATCHER_MAP);

            if (StringUtils.isNotEmpty(getDependency())) {
                // Get the values from the dynamic parameter class
                Parameter dependentParameter = mojo.getParameter(getGoal(), getDependency());
                if (dependentParameter.getDynamicParameter() != null) {
                	ApplicationInfo applicationInfo2 = getApplicationInfo();
                    Map<String, Object> constructMapForDynVals = constructMapForDynVals(applicationInfo, watcherMap, getDependency());
                    if (TYPE_DYNAMIC_PAGE_PARAMETER.equalsIgnoreCase(dependentParameter.getType())) {
                    	List<? extends Object> dynamicPageParameter = getDynamicPageParameter(applicationInfo2, watcherMap, dependentParameter);
                    	FrameworkUtil frameworkUtil = new FrameworkUtil();
                    	ParameterModel parameterModel = new ParameterModel();
                    	parameterModel.setName(dependentParameter.getKey());
                    	StringTemplate constructDynamicTemplate = frameworkUtil.constructDynamicTemplate(getCustomerId(), dependentParameter, parameterModel, dynamicPageParameter);
                    	setDynamicPageParameterDesign(constructDynamicTemplate.toString());
                    } else {
	                    List<Value> dependentPossibleValues = getDynamicPossibleValues(constructMapForDynVals, dependentParameter);
	                    setDependentValues(dependentPossibleValues);
	                    if (watcherMap.containsKey(getDependency())) {
	                        DependantParameters dependantParameters = (DependantParameters) watcherMap.get(getDependency());
	                        dependantParameters.setValue(dependentPossibleValues.get(0).getValue());
	                    }
                    }    
                }
            }
            return SUCCESS;
        } catch (PhrescoException e) {
            S_LOGGER.error("Entered into catch block of Quality.updateDependancy()"+ e);
            return showErrorPopup(e, getText(EXCEPTION_QUALITY_FUNCTIONAL_RUN));
        }
    }
    
    public String validateDynamicParam() {
        try {
        	File infoFile = new File(getPhrescoPluginInfoFilePath(getGoal()));
        	if (!infoFile.exists()) {
        		setParamaterAvailable(false);
                return SUCCESS;
        	}
        	
            MojoProcessor mojo = new MojoProcessor(infoFile);
            List<Parameter> parameters = getMojoParameters(mojo, getGoal()); //getDynamicParameters(getGoal());
            if (CollectionUtils.isEmpty(parameters)) {
                setParamaterAvailable(false);
                return SUCCESS;
            }
            
            boolean hasShow = false;
            for (Parameter parameter : parameters) {
                if (parameter.isShow()) {
                    hasShow = true;
                    setParamaterAvailable(true);
                    return SUCCESS;
                }
            }
            if (!hasShow) {
                Map<String, DependantParameters> watcherMap = new HashMap<String, DependantParameters>(8);
                setPossibleValuesInReq(mojo, getApplicationInfo(), parameters, watcherMap);
                if (watcherMap != null && !watcherMap.isEmpty()) {
                    setParamaterAvailable(true);
                }
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
        
        return SUCCESS;
    }
    
    public String openBrowseFileTree() throws PhrescoException {
		setReqAttribute(FILE_TYPES, getFileType());
		setReqAttribute(FILE_BROWSE, getFileOrFolder());
		ApplicationInfo applicationInfo = getApplicationInfo();
		setReqAttribute(REQ_PROJECT_LOCATION, getAppDirectoryPath(applicationInfo).replace(File.separator, FORWARD_SLASH));
		
		return SUCCESS;
	}
    
    public List<Value> getDependentValues() {
        return dependentValues;
    }

    public void setDependentValues(List<Value> dependentValues) {
        this.dependentValues = dependentValues;
    }
    
    public String getCurrentParamKey() {
        return currentParamKey;
    }

    public void setCurrentParamKey(String currentParamKey) {
        this.currentParamKey = currentParamKey;
    }

    public String getGoal() {
        return goal;
    }

    public void setGoal(String goal) {
        this.goal = goal;
    }
    
    public String getSelectedOption() {
        return selectedOption;
    }

    public void setSelectedOption(String selectedOption) {
        this.selectedOption = selectedOption;
    }

    public String getDependency() {
        return dependency;
    }

    public void setDependency(String dependency) {
        this.dependency = dependency;
    }
    
    public boolean isParamaterAvailable() {
        return paramaterAvailable;
    }

    public void setParamaterAvailable(boolean paramaterAvailable) {
        this.paramaterAvailable = paramaterAvailable;
    }

    public String getAvailableParams() {
        return availableParams;
    }

    public void setAvailableParams(String availableParams) {
        this.availableParams = availableParams;
    }
    
    public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	public String getFileOrFolder() {
		return fileOrFolder;
	}

	public void setFileOrFolder(String fileOrFolder) {
		this.fileOrFolder = fileOrFolder;
	}

	public void setDynamicPageParameterDesign(String dynamicPageParameterDesign) {
		this.dynamicPageParameterDesign = dynamicPageParameterDesign;
	}

	public String getDynamicPageParameterDesign() {
		return dynamicPageParameterDesign;
	}
}