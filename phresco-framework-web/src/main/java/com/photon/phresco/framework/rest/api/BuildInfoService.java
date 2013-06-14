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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;

import com.photon.phresco.commons.FrameworkConstants;
import com.photon.phresco.commons.model.BuildInfo;
import com.photon.phresco.commons.model.ProjectInfo;
import com.photon.phresco.exception.PhrescoException;
import com.photon.phresco.framework.PhrescoFrameworkFactory;
import com.photon.phresco.framework.api.ApplicationManager;
import com.photon.phresco.framework.api.ProjectManager;
import com.photon.phresco.util.ServiceConstants;
import com.photon.phresco.util.Utility;
import com.sun.jersey.api.client.ClientResponse.Status;

/**
 * The Class BuildInfoService.
 */
@Path("/buildinfo")
public class BuildInfoService extends RestBase implements FrameworkConstants, ServiceConstants {
	
	/**
	 * List of buildinfos.
	 *
	 * @param appDirName the app dir name
	 * @return the response
	 */
	@GET
	@Path("/list")
	@Produces(MediaType.APPLICATION_JSON)
	public Response list(@QueryParam(REST_QUERY_APPDIR_NAME) String appDirName) {
		ResponseInfo<List<BuildInfo>> responseData = new ResponseInfo<List<BuildInfo>>();
		try {
			File buildInfoFile = new File(Utility.getProjectHome() + appDirName + File.separator + BUILD_DIR
					+ File.separator + BUILD_INFO_FILE_NAME);
			ApplicationManager applicationManager = PhrescoFrameworkFactory.getApplicationManager();
			List<BuildInfo> builds = applicationManager.getBuildInfos(buildInfoFile);
			ResponseInfo<List<BuildInfo>> finalOutput = responseDataEvaluation(responseData, null,
					"Buildinfo listed Successfully", builds);
			return Response.status(Status.OK).entity(finalOutput).header("Access-Control-Allow-Origin", "*").build();
		} catch (PhrescoException e) {
			ResponseInfo<List<BuildInfo>> finalOutput = responseDataEvaluation(responseData, e,
					"Buildinfo list Failed", null);
			return Response.status(Status.NOT_FOUND).entity(finalOutput).header("Access-Control-Allow-Origin", "*")
					.build();
		}
	}

	/**
	 * Builds the info zip.
	 *
	 * @param appDirName the app dir name
	 * @param buildNumber the build number
	 * @return the response
	 */
	@POST
	@Path("/buildfile")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response buildInfoZip(@QueryParam(REST_QUERY_APPDIR_NAME) String appDirName,
			@QueryParam(REST_QUERY_BUILD_NUMBER) int buildNumber) {
		InputStream fileInputStream = null;
		ResponseInfo responseData = new ResponseInfo();
		try {
			File buildInfoFile = new File(Utility.getProjectHome() + appDirName + File.separator + BUILD_DIR
					+ File.separator + BUILD_INFO_FILE_NAME);
			ApplicationManager applicationManager = PhrescoFrameworkFactory.getApplicationManager();
			BuildInfo buildInfo = applicationManager.getBuildInfo(buildNumber, buildInfoFile.toString());
			if (buildInfo.getBuildNo() == buildNumber) {
				String deliverables = buildInfo.getDeliverables();
				StringBuilder builder = new StringBuilder();
				String fileName = buildInfo.getBuildName();
				if (StringUtils.isEmpty(deliverables)) {
					builder.append(Utility.getProjectHome() + appDirName);
					builder.append(File.separator);
					String moduleName = buildInfo.getModuleName();
					if (StringUtils.isNotEmpty(moduleName)) {
						builder.append(moduleName);
						builder.append(File.separator);
					}
					builder.append(BUILD_DIR);
					builder.append(File.separator);
					builder.append(buildInfo.getBuildName());
				} else {
					builder.append(buildInfo.getDeliverables());
					fileName = fileName.substring(fileName.lastIndexOf(FrameworkConstants.FORWARD_SLASH) + 1);
					boolean status = fileName.endsWith(APKLIB) || fileName.endsWith(APK);
					if (status) {
						fileName = fileName.substring(0, fileName.lastIndexOf(".")) + ARCHIVE_FORMAT;
					} else {
						fileName = FilenameUtils.removeExtension(fileName) + ARCHIVE_FORMAT;
					}
				}
				fileInputStream = new FileInputStream(new File(builder.toString()));
				return Response.status(Status.OK).entity(fileInputStream).header("Access-Control-Allow-Origin", "*")
						.build();
			}
		} catch (FileNotFoundException e) {
			ResponseInfo finalOutput = responseDataEvaluation(responseData, e, "Zip Download Failed", null);
			return Response.status(Status.NOT_FOUND).entity(finalOutput).header("Access-Control-Allow-Origin", "*")
					.build();
		} catch (PhrescoException e) {
			ResponseInfo finalOutput = responseDataEvaluation(responseData, e, "Zip Download Failed", null);
			return Response.status(Status.NOT_FOUND).entity(finalOutput).header("Access-Control-Allow-Origin", "*")
					.build();
		}
		return null;
	}

	/**
	 * Delete build.
	 *
	 * @param buildNumbers the build numbers
	 * @param projectId the project id
	 * @param customerId the customer id
	 * @param appId the app id
	 * @return the response
	 */
	@DELETE
	@Path("/deletebuild")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteBuild(String[] buildNumbers, @QueryParam(REST_QUERY_PROJECTID) String projectId,
			@QueryParam(REST_QUERY_CUSTOMERID) String customerId, @QueryParam(REST_QUERY_APPID) String appId) {
		ResponseInfo responseData = new ResponseInfo();
		try {
			int[] buildInts = new int[buildNumbers.length];
			for (int i = 0; i < buildNumbers.length; i++) {
				buildInts[i] = Integer.parseInt(buildNumbers[i]);
			}
			ProjectManager projectManager = PhrescoFrameworkFactory.getProjectManager();
			ProjectInfo project = projectManager.getProject(projectId, customerId, appId);
			ApplicationManager applicationManager = PhrescoFrameworkFactory.getApplicationManager();
			applicationManager.deleteBuildInfos(project, buildInts);
		} catch (PhrescoException e) {
			ResponseInfo finalOutput = responseDataEvaluation(responseData, e, "Deletion of build Failed", null);
			return Response.status(Status.NOT_FOUND).entity(finalOutput).header("Access-Control-Allow-Origin", "*")
					.build();
		}
		ResponseInfo finalOutput = responseDataEvaluation(responseData, null, "Build deleted Successfully", null);
		return Response.status(Status.OK).entity(finalOutput).header("Access-Control-Allow-Origin", "*").build();
	}
}