/**
 * Phresco Pom
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

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.photon.phresco.framework.commons.ErrorHandlerTest;
import com.photon.phresco.framework.rest.api.util.BufferMapTest;

@RunWith(Suite.class)
@SuiteClasses({
	LoginServiceTest.class,
	UpgradeServiceTest.class,
	ProjectServiceTest.class,
	ConfigurationServiceTest.class,
	BuildInfoTest.class,
	CIServiceTest.class,
	CodeValidationServiceTest.class,
	PilotServiceTest.class,
	ManualTestServiceTest.class,
	UnitServiceTest.class,
	PdfServiceTest.class,
	RepositoryServiceTest.class,
	ParameterServiceTest.class,
	DownloadServiceTest.class,
	TechnologyServiceTest.class,
	CustomerServiceTest.class,
	AppInfoConfigsTest.class,
	UtilServiceTest.class,
	FeatureServiceTest.class,
	CIJobTemplateServiceTest.class,
	BufferMapTest.class,
	ErrorHandlerTest.class,
	ProjectDeleteServiceTest.class,
	DashboardServiceTest.class,
	LiquibaseServiceTest.class,
	UploadBuildServiceTest.class
	}
)

public class AllTest {
	// intentionally blank. All tests were added via annotations
}
