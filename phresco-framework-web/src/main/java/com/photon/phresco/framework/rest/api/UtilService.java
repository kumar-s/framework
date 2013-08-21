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
package com.photon.phresco.framework.rest.api;

import java.awt.Desktop;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.photon.phresco.commons.FrameworkConstants;
import com.photon.phresco.commons.ResponseCodes;
import com.photon.phresco.commons.model.ApplicationInfo;
import com.photon.phresco.commons.model.ProjectInfo;
import com.photon.phresco.commons.model.Technology;
import com.photon.phresco.exception.PhrescoException;
import com.photon.phresco.framework.commons.FrameworkUtil;
import com.photon.phresco.framework.rest.api.util.ActionResponse;
import com.photon.phresco.framework.rest.api.util.ActionServiceConstant;
import com.photon.phresco.framework.rest.api.util.FrameworkServiceUtil;
import com.photon.phresco.service.client.api.ServiceManager;
import com.photon.phresco.util.ServiceConstants;
import com.photon.phresco.util.Utility;
import com.sun.jersey.api.client.ClientResponse.Status;

import fr.opensagres.xdocreport.utils.StringUtils;

/**
 * The Class UtilService.
 */
@Path("/util")
public class UtilService extends RestBase implements FrameworkConstants, ServiceConstants, ResponseCodes {

	/**
	 * Open folder.
	 *
	 * @param appDirName the app dir name
	 * @param type the type
	 * @return the response
	 */
	@GET
	@Path("/openFolder")
	@Produces(MediaType.APPLICATION_JSON)
	public Response openFolder(@QueryParam(REST_QUERY_APPDIR_NAME) String appDirName,
			@QueryParam(REST_QUERY_TYPE) String type) {
		ResponseInfo<String> responseData = new ResponseInfo<String>();
		try {
			if (Desktop.isDesktopSupported()) {
				File file = new File(getPath(appDirName, type));
				if (file.exists()) {
					Desktop.getDesktop().open(file);
				} else {
					StringBuilder sbOpenPath = new StringBuilder(Utility.getProjectHome());
					sbOpenPath.append(appDirName);
					Desktop.getDesktop().open(new File(sbOpenPath.toString()));
				}
			}
			ResponseInfo<String> finalOutput = responseDataEvaluation(responseData, null,
					null, RESPONSE_STATUS_SUCCESS, PHR3C00001);
			return Response.status(Status.OK).entity(finalOutput).header("Access-Control-Allow-Origin", "*").build();
		} catch (Exception e) {
			ResponseInfo<ProjectInfo> finalOutput = responseDataEvaluation(responseData, e, null, RESPONSE_STATUS_ERROR, PHR3C10001);
			return Response.status(Status.OK).entity(finalOutput).header("Access-Control-Allow-Origin", "*")
					.build();
		}
	}

	/**
	 * Copy path.
	 *
	 * @param appDirName the app dir name
	 * @param type the type
	 * @return the response
	 */
	@GET
	@Path("/copyPath")
	@Produces(MediaType.APPLICATION_JSON)
	public Response copyPath(@QueryParam(REST_QUERY_APPDIR_NAME) String appDirName,
			@QueryParam(REST_QUERY_TYPE) String type) {
		ResponseInfo<String> responseData = new ResponseInfo<String>();
		try {
			File file = new File(getPath(appDirName, type));
			String pathToCopy = "";
			if (file.exists()) {
				pathToCopy = file.getPath();
			} else {
				StringBuilder sbCopyPath = new StringBuilder(Utility.getProjectHome());
				sbCopyPath.append(appDirName);
				pathToCopy = Utility.getProjectHome() + sbCopyPath.toString();
			}
			copyToClipboard(pathToCopy);
			ResponseInfo<String> finalOutput = responseDataEvaluation(responseData, null,
					null, RESPONSE_STATUS_SUCCESS, PHR4C00001);
			return Response.status(Status.OK).entity(finalOutput).header("Access-Control-Allow-Origin", "*").build();
		} catch (Exception e) {
			ResponseInfo<ProjectInfo> finalOutput = responseDataEvaluation(responseData, e, null, RESPONSE_STATUS_ERROR, PHR4C10001);
			return Response.status(Status.OK).entity(finalOutput).header("Access-Control-Allow-Origin", "*")
					.build();
		}
	}
	
	/**
	 * To copy the console log to the clipboard
	 * @param log
	 * @return
	 */
	@POST
	@Path("/copyToClipboard")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response copyLogToClipboard(String log) {
		ResponseInfo<String> responseData = new ResponseInfo<String>();
		try {
			copyToClipboard(log);
			ResponseInfo<String> finalOutput = responseDataEvaluation(responseData, null, null, RESPONSE_STATUS_SUCCESS, PHR2C00001);
			return Response.status(Status.OK).entity(finalOutput).header("Access-Control-Allow-Origin", "*").build();
		} catch (Exception e) {
			ResponseInfo<ProjectInfo> finalOutput = responseDataEvaluation(responseData, e, null, RESPONSE_STATUS_ERROR, PHR2C10001);
			return Response.status(Status.OK).entity(finalOutput).header("Access-Control-Allow-Origin", "*").build();
		}
	}

	/**
	 * Gets the tecnology options.
	 *
	 * @param userId the user id
	 * @param techId the tech id
	 * @return the tecnology options
	 */
	@GET
	@Path("/techOptions")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getTecnologyOptions(@QueryParam(REST_QUERY_USERID) String userId,
			@QueryParam(REST_QUERY_TECHID) String techId) {
		ResponseInfo<List<String>> responseData = new ResponseInfo<List<String>>();
		try {
			ServiceManager serviceManager = CONTEXT_MANAGER_MAP.get(userId);
			if (serviceManager == null) {
				ResponseInfo<List<ApplicationInfo>> finalOutput = responseDataEvaluation(responseData, null,
						null, RESPONSE_STATUS_FAILURE, PHR310001);
				return Response.status(Status.OK).entity(finalOutput).header("Access-Control-Allow-Origin",
						"*").build();
			}
			Technology technology = serviceManager.getTechnology(techId);
			List<String> archetypeFeatures = technology.getOptions();
			ResponseInfo<List<String>> finalOutput = responseDataEvaluation(responseData, null,
					archetypeFeatures, RESPONSE_STATUS_SUCCESS, PHR300002);
			return Response.status(Status.OK).entity(finalOutput).header("Access-Control-Allow-Origin", "*").build();
		} catch (PhrescoException e) {
			ResponseInfo<ProjectInfo> finalOutput = responseDataEvaluation(responseData, e,
					null, RESPONSE_STATUS_ERROR, PHR310004);
			return Response.status(Status.OK).entity(finalOutput).header("Access-Control-Allow-Origin", "*")
					.build();
		}
	}

	/**
	 * Gets the path of test directory.
	 *
	 * @param appDirName the app dir name
	 * @param type the type
	 * @return the path
	 * @throws PhrescoException the phresco exception
	 */
	private String getPath(String appDirName, String type) throws PhrescoException {
		StringBuilder sb = new StringBuilder(Utility.getProjectHome()).append(appDirName).append(File.separator);
		try {
			if (TEST_UNIT.equals(type)) {
				sb.append(FrameworkServiceUtil.getUnitTestDir(appDirName));
			} else if (TEST_FUNCTIONAL.equals(type)) {
				sb.append(FrameworkServiceUtil.getFunctionalTestDir(appDirName));
			} else if (TEST_COMPONENT.equals(type)) {
				sb.append(FrameworkServiceUtil.getComponentTestDir(appDirName));
			} else if (TEST_LOAD.equals(type)) {
				sb.append(FrameworkServiceUtil.getLoadTestDir(appDirName));
			} else if (TEST_MANUAL.equals(type)) {
				sb.append(FrameworkServiceUtil.getManualTestDir(appDirName));
			} else if (TEST_PERFORMANCE.equals(type)) {
				sb.append(FrameworkServiceUtil.getPerformanceTestDir(appDirName));
			} else if (BUILD.equals(type)) {
				sb = new StringBuilder(FrameworkServiceUtil.getBuildDir(appDirName));
			}
		} catch (Exception e) {
			throw new PhrescoException(e);
		}

		return sb.toString();
	}

	/**
	 * Copy to clipboard.
	 *
	 * @param content the content
	 * @throws UnsupportedEncodingException 
	 */
	private void copyToClipboard(String content) throws PhrescoException {
		try {
			Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
			String decodedContent = java.net.URLDecoder.decode(content, "UTF-8");
			clipboard.setContents(new StringSelection(decodedContent.replaceAll("(?m)^[ \t]*\r?\n", "")), null);
		} catch (Exception e) {
			throw new PhrescoException(e);
		}
	}
	
	@GET
	@Path("/validation")
	@Produces(MediaType.APPLICATION_JSON)
	public Response checkMandatoryValidation(@Context HttpServletRequest request,
			@QueryParam("appDirName") String appDirName, @QueryParam("phase") String phase,
			@QueryParam("customerId") String customerId) {
		FrameworkServiceUtil futil = new FrameworkServiceUtil();
		ActionResponse response = null;
		String envNames = request.getParameter("environmentName");
		try {
			response = futil.mandatoryValidation(request, phase, appDirName);
			if (response.isErrorFound()) {	
				return Response.status(Status.OK).entity(response).header("Access-Control-Allow-Origin", "*").build();
			}
			
			if ((PHASE_LOAD.equals(phase) || PERFORMANCE_TEST.equals(phase)) && REQ_PARAMETERS.equals(request.getParameter(REQ_TEST_BASIS))) {
				response = FrameworkServiceUtil.checkForConfigurations(appDirName, envNames, customerId);
				if (response.isErrorFound()) {
					return Response.status(Status.OK).entity(response).header("Access-Control-Allow-Origin", "*").build();
				}
			} else if (!PHASE_LOAD.equals(phase) && !PERFORMANCE_TEST.equals(phase)) {
				response = FrameworkServiceUtil.checkForConfigurations(appDirName, envNames, customerId);
				if (response.isErrorFound()) {
					return Response.status(Status.OK).entity(response).header("Access-Control-Allow-Origin", "*").build();
				}
			}
			response.setStatus(RESPONSE_STATUS_SUCCESS);
			response.setResponseCode(PHR9C00001);
		} catch (PhrescoException e) {
			response.setStatus(RESPONSE_STATUS_ERROR);
			response.setService_exception(FrameworkUtil.getStackTraceAsString(e));
			response.setResponseCode(PHR9C10001);
		}
		return Response.status(Status.OK).entity(response).header("Access-Control-Allow-Origin", "*").build();
	}

	
	
}