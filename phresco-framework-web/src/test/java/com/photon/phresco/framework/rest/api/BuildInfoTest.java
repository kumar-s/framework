package com.photon.phresco.framework.rest.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;

import junit.framework.Assert;

import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import com.photon.phresco.commons.model.BuildInfo;
import com.photon.phresco.exception.PhrescoException;
import com.photon.phresco.framework.rest.api.util.ActionResponse;
import com.photon.phresco.util.Utility;

public class BuildInfoTest extends RestBaseTest {

	ParameterService parameterservice = new ParameterService();
	ActionService actionservice = new ActionService();
	BuildInfoService buildinfoservice = new BuildInfoService();
	static String uniqueKey = "";
	
	@Test
	public void getBuildParams() {
		String goal = "package";
		String phase = "package";
		
		Response response = parameterservice.getParameter(appDirName, goal, phase, userId, customerId,"");
		assertEquals(200, response.getStatus());
	}
	
	@Test
	public void generateBuild() throws PhrescoException {
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setParameter("buildName", "sample");
		request.setParameter("fromPage", "All");
		request.setParameter("environmentName", "Production");
		request.setParameter("logs", "showErrors");
		request.setParameter("skipTest", "true");
		request.setParameter("customerId", customerId);
		request.setParameter("projectId", "TestProject");
		request.setParameter("appId", "TestProject");
		request.setParameter("username", userId);
		request.setParameter("buildNumber", "1");
		HttpServletRequest httpServletRequest = (HttpServletRequest)request;
		Response build = actionservice.build(httpServletRequest);
		ActionResponse entity = (ActionResponse) build.getEntity();
		uniqueKey = entity.getUniquekey();
		assertEquals("STARTED", entity.getStatus());
	}
	
	@Test
	public void readBuildLog() throws PhrescoException {
		assertEquals(true, readLog());
	}
	
	@Test
	public void deploy() throws PhrescoException {
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setParameter("environmentName", "Production");
		request.setParameter("logs", "showErrors");
		request.setParameter("customerId", customerId);
		request.setParameter("projectId", "TestProject");
		request.setParameter("appId", "TestProject");
		request.setParameter("username", userId);
		request.setParameter("executeSql", "false");
		request.setParameter("buildName", "sample.zip");
		request.setParameter("buildNumber", "1");
		HttpServletRequest httpServletRequest = (HttpServletRequest)request;
		Response deploy = actionservice.deploy(httpServletRequest);
		ActionResponse entity = (ActionResponse) deploy.getEntity();
		uniqueKey = entity.getUniquekey();
		assertEquals("STARTED", entity.getStatus());
	}
	
	@Test
	public void readDeployLog() throws PhrescoException {
		assertEquals(true, readLog());
	}
	
	@Test
	public void getBuildList() {
		Response buildList = buildinfoservice.list(appDirName);
		ResponseInfo<List<BuildInfo>> entity = (ResponseInfo<List<BuildInfo>>) buildList.getEntity();
		List<BuildInfo> data = entity.getData();
		assertEquals(1, data.size());
	}
	
	@Test
	public void getBuildListFailure() {
		Response buildList = buildinfoservice.list("xx");
		assertEquals(404, buildList.getStatus());
	}
	
	@Test
	public void downloadBuild() {
		Response parameter = buildinfoservice.buildInfoZip("TestProject", 1);
		assertEquals(200, parameter.getStatus());
	}
	
	@Test
	public void downloadBuildFileNotFound() {
		File buildInfoFile = new File(Utility.getProjectHome() + appDirName + File.separator + "do_not_checkin/build"
				+ File.separator + "build.info");
		File tempBuildFile = new File(Utility.getProjectHome() + appDirName + File.separator + "do_not_checkin/build" + 
				 File.separator + "sample.info");
		buildInfoFile.renameTo(tempBuildFile);
		Response parameter = buildinfoservice.buildInfoZip(appDirName, 1);
		tempBuildFile.renameTo(buildInfoFile);
		assertEquals(404, parameter.getStatus());
		
	}
	
	@Test
	public void downloadBuildFailure() {
		Response parameter = buildinfoservice.buildInfoZip("xx", 1);
		assertEquals(404, parameter.getStatus());
	}
	
	@Test
	public void deleteBuild() {
		String[] buildNumbers = {"1"};
		Response parameter = buildinfoservice.deleteBuild(buildNumbers, "TestProject", customerId, "TestProject");
		assertEquals(200, parameter.getStatus());
	}

	
	@Test
	public void deleteBuildFailure() {
		String[] buildNumbers = {"3"};
		Response parameter = buildinfoservice.deleteBuild(buildNumbers, "TestProject", customerId, "TestProject");
		assertEquals(404, parameter.getStatus());
	}

	@Test
	public void  checkServerStatus() {
		Response response = buildinfoservice.checkStatus(appDirName);
		Assert.assertEquals(200, response.getStatus());
	}
	
	@Test
	public void  runServer() throws PhrescoException {
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setParameter("environmentName", "Production");
		request.setParameter("logs", "showErrors");
		request.setParameter("skipTest", "true");
		request.setParameter("customerId", customerId);
		request.setParameter("projectId", "TestProject");
		request.setParameter("appId", "TestProject");
		request.setParameter("username", userId);
		HttpServletRequest httpServletRequest = (HttpServletRequest)request;
		Response runAgainstSource = actionservice.runAgainstSource(httpServletRequest);
		Assert.assertEquals(200, runAgainstSource.getStatus());
		ActionResponse entity = (ActionResponse) runAgainstSource.getEntity();
		uniqueKey = entity.getUniquekey();
		assertEquals("STARTED", entity.getStatus());
	}
	
	@Test
	public void readRunAgainstSourceLog() throws PhrescoException {
		assertEquals(true, readLog());
	}
	
	@Test
	public void  checkStartStatus() throws InterruptedException {
		Thread.sleep(3000);
		Response response = buildinfoservice.checkStatus(appDirName);
		Assert.assertEquals(200, response.getStatus());
	}
	
	
	@Test
	public void  stopServer() throws PhrescoException {
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setParameter("environmentName", "Production");
		request.setParameter("logs", "showErrors");
		request.setParameter("skipTest", "true");
		request.setParameter("customerId", customerId);
		request.setParameter("projectId", "TestProject");
		request.setParameter("appId", "TestProject");
		request.setParameter("username", userId);
		HttpServletRequest httpServletRequest = (HttpServletRequest)request;
		Response runAgainstSource = actionservice.stopServer(httpServletRequest);
		Assert.assertEquals(200, runAgainstSource.getStatus());
		ActionResponse entity = (ActionResponse) runAgainstSource.getEntity();
		uniqueKey = entity.getUniquekey();
		assertEquals("STARTED", entity.getStatus());
	}
	
	@Test
	public void readRunAgainstSourceStopLog() throws PhrescoException {
		assertEquals(true, readLog());
	}
	
	@Test
	public void  checkStopStatus() {
		Response response = buildinfoservice.checkStatus(appDirName);
		Assert.assertEquals(200, response.getStatus());
	}
	
	public Boolean readLog() throws PhrescoException {
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setParameter("uniquekey", uniqueKey);
		HttpServletRequest httpServletRequest = (HttpServletRequest)request;
		Response build = actionservice.read(httpServletRequest);
		ActionResponse output = (ActionResponse) build.getEntity();
		System.out.println(output.getLog());
		if (output.getLog() != null) {

			if (output.getLog().contains("BUILD FAILURE")) {
				fail("Error occured ");
			}
			
			if (output.getLog().contains("Starting Coyote")) {
				return true;
			}
			
			if ("INPROGRESS".equalsIgnoreCase(output.getStatus())) {
				readLog();
				return true;
			} else if ("COMPLETED".equalsIgnoreCase(output.getStatus())) {
				System.out.println("***** Log finished ***********");
				return true;
			} else if ("ERROR".equalsIgnoreCase(output.getStatus())) {
				fail("Error occured while retrieving the logs");
				return false;
			}
		}
		return false;
	}
}