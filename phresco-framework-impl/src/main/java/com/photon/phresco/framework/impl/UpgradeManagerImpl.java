/**
 * Phresco Framework Implementation
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
package com.photon.phresco.framework.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.log4j.Logger;
import org.codehaus.plexus.util.FileUtils;

import com.photon.phresco.commons.FrameworkConstants;
import com.photon.phresco.commons.model.VersionInfo;
import com.photon.phresco.exception.PhrescoException;
import com.photon.phresco.framework.api.UpgradeManager;
import com.photon.phresco.service.client.api.ServiceManager;
import com.photon.phresco.util.ArchiveUtil;
import com.photon.phresco.util.ArchiveUtil.ArchiveType;
import com.photon.phresco.util.FileUtil;
import com.photon.phresco.util.Utility;
import com.phresco.pom.exception.PhrescoPomException;
import com.phresco.pom.util.PomProcessor;

public class UpgradeManagerImpl implements UpgradeManager, FrameworkConstants   {
	private VersionInfo version = null;
	private static final Logger S_LOGGER = Logger.getLogger(UpgradeManagerImpl.class);
	private static Boolean DebugEnabled = S_LOGGER.isDebugEnabled();

	public VersionInfo checkForUpdate(ServiceManager serviceManager, String versionNo) throws PhrescoException {
		if (DebugEnabled) {
			S_LOGGER.debug("Entering Method UpdateManagerImpl.checkForUpdate(String versionNo)");
			S_LOGGER.debug("checkForUpdate() Version Number = " + versionNo);
		}
		return serviceManager.getVersionInfo(versionNo);
	}

	public String getCurrentVersion() throws PhrescoException {
		if (DebugEnabled) {
			S_LOGGER.debug("Entering Method UpdateManagerImpl.getCurrentVersion()");
		}
		
		try {
			File pomFile = new File (Utility.getPhrescoHome() + File.separator + 
					FrameworkConstants.BIN_DIR +  File.separator + FrameworkConstants.POM_FILE);
			PomProcessor processor = new PomProcessor(pomFile);
			return processor.getProperty(FrameworkConstants.PROPERTY_VERSION);
		} catch (PhrescoPomException e) {
			throw new PhrescoException(e);
		}
	}

	public void doUpdate(ServiceManager serviceManager, String newVersion, String customerId) throws PhrescoException {
		if (DebugEnabled) {
			S_LOGGER.debug("Entering Method UpdateManagerImpl.doUpdate(String newVersion)");
		}
		InputStream latestVersionZip = null;
		OutputStream outPutFile = null;
		File tempFile = null;
		try {
			createBackUp();
			latestVersionZip = serviceManager.getUpdateVersionContent(customerId).getEntityInputStream();
			tempFile = new File(Utility.getPhrescoTemp(), FrameworkConstants.TEMP_ZIP_FILE);
			outPutFile = new FileOutputStream(tempFile);
			 
			int read = 0;
			byte[] bytes = new byte[1024];
		 
			while ((read = latestVersionZip.read(bytes)) != -1) {
				outPutFile.write(bytes, 0, read);
			}
			
			extractUpdate(tempFile);
			markVersionUpdated(newVersion);
		} catch (IOException e) {
			throw new PhrescoException(e);
		} finally {
			Utility.closeStream(latestVersionZip);
			Utility.closeStream(outPutFile);
		}
	}

	private void extractUpdate(File tempFile) throws PhrescoException {
		ArchiveUtil.extractArchive(tempFile.getPath(), FrameworkConstants.PREV_DIR, ArchiveType.ZIP);
		FileUtil.delete(tempFile);
	}


	private void createBackUp() throws IOException, PhrescoException {
		File tempFile = new File(Utility.getPhrescoTemp(), FrameworkConstants.TEMP_FOLDER);
		tempFile.mkdir();
		File settingsFile = new File(FrameworkConstants.MAVEN_SETTINGS_FILE);
		File binFile = new File(FrameworkConstants.PREV_DIR + FrameworkConstants.BIN_DIR);
		if (binFile.exists()) {
			File[] binFilesList = binFile.listFiles();
			for (File file : binFilesList) {
				if (!file.isDirectory()) {
					FileUtils.copyFileToDirectory(file, new File(tempFile, FrameworkConstants.BIN_DIR));
				}
			}
		}
		if(settingsFile.exists()) {
			FileUtils.copyFileToDirectory(settingsFile, new File(tempFile, FrameworkConstants.OUTPUT_SETTINGS_DIR));
		}
		File fileBackups = new File(FrameworkConstants.BACKUP_DIRNAME);
		if (!fileBackups.exists()) {
			fileBackups.mkdir();
		}
		ArchiveUtil.createArchive(tempFile, 
				new File(FrameworkConstants.BACKUP_DIRNAME + File.separator + getCurrentVersion() + FrameworkConstants.ARCHIVE_EXTENSION), ArchiveType.ZIP);
		FileUtil.delete(tempFile);
	}

	private void markVersionUpdated(String newVersion) throws PhrescoException {
		FileWriter writer = null;
		
		File updateMarkkerFile = new File(Utility.getPhrescoTemp() + "/markers/upgrade-temp.marker");
		if(updateMarkkerFile.exists()) {
			FileUtil.delete(updateMarkkerFile);
		}
		
		updateMarkkerFile = new File(Utility.getPhrescoTemp() + "/markers/upgrade-temp1.marker");
		if(updateMarkkerFile.exists()) {
			FileUtil.delete(updateMarkkerFile);
		}
		
		try {
			String fileName = Utility.getPhrescoTemp() + FrameworkConstants.UPGRADE_PROP_NAME;
			writer = new FileWriter(fileName);
			writer.write(newVersion);
		} catch (IOException e) {
			throw new PhrescoException(e);
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
					throw new PhrescoException(e);
				}
			}
		}
	}
}