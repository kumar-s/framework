package com.photon.phresco.Screens;

import java.io.IOException;

import com.photon.phresco.uiconstants.Drupal6ConstantsXml;
import com.photon.phresco.uiconstants.Drupal7ConstantsXml;
import com.photon.phresco.uiconstants.JavaWebServConstantsXml;
import com.photon.phresco.uiconstants.MobWidgetConstantsXml;
import com.photon.phresco.uiconstants.NodeJSConstantsXml;
import com.photon.phresco.uiconstants.PhpConstantsXml;
import com.photon.phresco.uiconstants.PhrescoUiConstantsXml;
import com.photon.phresco.uiconstants.PhrescoUiConstantsXml;
import com.photon.phresco.uiconstants.SharepointConstantsXml;
import com.photon.phresco.uiconstants.WordPressConstants;
import com.photon.phresco.uiconstants.YuiConstantsXml;
import com.photon.phresco.uiconstants.iPhoneConstantsXml;

public class ConfigScreen extends WebDriverAbstractBaseScreen {
	PhrescoUiConstantsXml phrsc = new PhrescoUiConstantsXml();
	public WebDriverBaseScreen element;
	

	
public void DrupalServerConfig(Drupal7ConstantsXml drupalConst,String methodName) throws InterruptedException, IOException,
		Exception {

	element= getXpathWebElement(drupalConst.DRUPALPROJ);
	waitForElementPresent(drupalConst.DRUPALPROJ,methodName);
	element.click();
	element= getXpathWebElement(phrsc.CONFIGURATIONS);
	waitForElementPresent(phrsc.CONFIGURATIONS,methodName);
	element.click();
	waitForElementPresent(phrsc.CONFIGURATIONS_ADD,methodName);
	element= getXpathWebElement(phrsc.CONFIGURATIONS_ADD);
	element.click();
	Thread.sleep(1000);
	element= getXpathWebElement(phrsc.CONFIGURATIONS_NAME);
	element.click();
	element.type(drupalConst.CONFIGURATIONS_DRUPAL_SERVER_NAME_VALUE);
	element.click();
	element= getXpathWebElement(phrsc.CONFIGURATIONS_DESCRIPTION);
	element.type(drupalConst.CONFIGURATIONS_DRUPAL_SERVER_DESCRIPTION_VALUE);
	/*select(phrsc.CONFIGURATIONS_TYPE,
			phrsc.CONFIGURATIONS_TYPE_SERVER_VALUE);*/
	element= getXpathWebElement(phrsc.CONFIGURATIONS_TYPE);
	waitForElementPresent(phrsc.CONFIGURATIONS_TYPE,methodName);
	element.click();
	element= getXpathWebElement(phrsc.CONFIGURATIONS_TYPE_SERVER_CLICK);
	waitForElementPresent(phrsc.CONFIGURATIONS_TYPE_SERVER_CLICK,methodName);
	element.click();
	element= getXpathWebElement(phrsc.CONFIGURATIONS_TYPE_PROTOCOL_TYPE);
	waitForElementPresent(phrsc.CONFIGURATIONS_TYPE_PROTOCOL_TYPE,methodName);
	Thread.sleep(3000);
	element.click();
	Thread.sleep(1000);
	element= getXpathWebElement(phrsc.CONFIGURATIONS_TYPE_HTTP_CLICK);
	waitForElementPresent(phrsc.CONFIGURATIONS_TYPE_HTTP_CLICK,methodName);
	element.type(phrsc.CONFIGURATIONS_TYPE_HTTP_CLICK);
	element.click();
	element= getXpathWebElement(phrsc.CONFIGURATIONS_HOST);
	waitForElementPresent(phrsc.CONFIGURATIONS_HOST,methodName);
	element.click();
	element.type(drupalConst.CONFIGURATIONS_DRUPAL_SERVER_HOST_VALUE);
	element= getXpathWebElement(phrsc.CONFIGURATIONS_PORT);
	waitForElementPresent(phrsc.CONFIGURATIONS_PORT,methodName);
	element.click();
	element.type(drupalConst. CONFIGURATIONS_DRUPAL_SERVER_PORT_VALUE);
	element= getXpathWebElement(phrsc.CONFIGURATIONS_SERVER_DEPLOYDIR);
	element.click();
	element.type(drupalConst.CONFIGURATIONS_DRUPAL_SERVER_DEPLOYDIR_VALUE);
	//type(phrsc. CONFIGURATIONS_SERVER_TYPE_WAMP,phrsc.CONFIGURATIONS_DRUPAL_SERVER_TYPE_VALUE);
	element= getXpathWebElement(phrsc.CONFIGURATIONS_SERVER_CONTEXT);
	element.click();
	element.type(drupalConst.CONFIGURATIONS_DRUPAL_SERVER_CONTEXT_VALUE);
	element= getXpathWebElement(phrsc.CONFIGURATIONS_SAVE);
	element.click();
	waitForElementPresent(phrsc.CONFIGURATIONS_ADD,methodName);
}


public void DrupalDatabaseConfig(Drupal7ConstantsXml drupalConst,String methodName) throws InterruptedException,
		IOException, Exception {

	element= getXpathWebElement(drupalConst.DRUPALPROJ);
	waitForElementPresent(drupalConst.DRUPALPROJ,methodName);
	element.click();
	Thread.sleep(3000);
	element= getXpathWebElement(phrsc.CONFIGURATIONS);
	Thread.sleep(1000);
	waitForElementPresent(phrsc.CONFIGURATIONS,methodName);
	element.click();
	waitForElementPresent(phrsc.CONFIGURATIONS_ADD,methodName);
	element= getXpathWebElement(phrsc.CONFIGURATIONS_ADD);
	element.click();
	waitForElementPresent(phrsc.CONFIGURATIONS_NAME,methodName);
	element= getXpathWebElement(phrsc.CONFIGURATIONS_NAME);
	Thread.sleep(1000);
	element.type(drupalConst.CONFIGURATIONS_DRUPAL_DB_NAME_VALUE);
	element=getXpathWebElement(phrsc.CONFIGURATIONS_DESCRIPTION);
	waitForElementPresent(phrsc.CONFIGURATIONS_DESCRIPTION,methodName);
	element.type(drupalConst.CONFIGURATIONS_DRUPAL_DB_DESCRIPTION_VALUE);
	element= getXpathWebElement(phrsc.CONFIGURATIONS_TYPE);
	waitForElementPresent(phrsc.CONFIGURATIONS_TYPE,methodName);
	element.click();
	Thread.sleep(1000);
	element= getXpathWebElement(phrsc.CONFIGURATIONS_TYPE_DATABASE_CLICK);
	waitForElementPresent(phrsc.CONFIGURATIONS_TYPE_DATABASE_CLICK,methodName);
	//Thread.sleep(3000);
	element.type(phrsc.CONFIGURATIONS_TYPE_DATABASE_CLICK);
	element.click();
	Thread.sleep(2000);
	element=getXpathWebElement(phrsc.CONFIGURATIONS_HOST);
	element=getXpathWebElement(phrsc.CONFIGURATIONS_HOST);
	waitForElementPresent(phrsc.CONFIGURATIONS_HOST,methodName);
	element.click();
	element.type(drupalConst.CONFIGURATIONS_DRUPAL_DB_HOST_VALUE);
	element.click();
	//Thread.sleep(1000);
	//Thread.sleep(1000);
	element=getXpathWebElement(phrsc.CONFIGURATIONS_PORT);
	waitForElementPresent(phrsc.CONFIGURATIONS_PORT,methodName);
	element.click();
	element.type(drupalConst.CONFIGURATIONS_DRUPAL_DB_PORT_VALUE);
	Thread.sleep(1000);
	element=getXpathWebElement(phrsc.CONFIGURATIONS_DATABASE_USERNAME_CLICK);
	waitForElementPresent(phrsc.CONFIGURATIONS_DATABASE_USERNAME_CLICK,methodName);
	Thread.sleep(1000);
	element.type(drupalConst.CONFIGURATIONS_DRUPAL_DB_USERNAME_VALUE);
	element=getXpathWebElement(phrsc.CONFIGURATIONS_TYPE_DATABASE_NAME_CLICK);
	waitForElementPresent(phrsc.CONFIGURATIONS_TYPE_DATABASE_NAME_CLICK,methodName);
	Thread.sleep(1000);
	element.type(methodName);
	element=getXpathWebElement(phrsc.CONFIGURATIONS_SAVE);
	waitForElementPresent(phrsc.CONFIGURATIONS_SAVE,methodName);
	Thread.sleep(1000);
	element.click();
	waitForElementPresent(phrsc.CONFIGURATIONS_ADD,methodName);
}

public void Drupal7NoneServerConfig(Drupal7ConstantsXml drupalConst,String methodName) throws InterruptedException, IOException,
Exception {

	element= getXpathWebElement(drupalConst.DRUPALPROJ);
	waitForElementPresent(drupalConst.DRUPALPROJ,methodName);
	element.click();
	element= getXpathWebElement(phrsc.CONFIGURATIONS);
	waitForElementPresent(phrsc.CONFIGURATIONS,methodName);
	element.click();
	waitForElementPresent(phrsc.CONFIGURATIONS_ADD,methodName);
	element= getXpathWebElement(phrsc.CONFIGURATIONS_ADD);
	element.click();
	Thread.sleep(1000);
	element= getXpathWebElement(phrsc.CONFIGURATIONS_NAME);
	element.click();
	element.type(drupalConst.CONFIGURATIONS_DRUPAL_SERVER_NAME_VALUE);
	element.click();
	element= getXpathWebElement(phrsc.CONFIGURATIONS_DESCRIPTION);
	element.type(drupalConst.CONFIGURATIONS_DRUPAL_SERVER_DESCRIPTION_VALUE);
	/*select(phrsc.CONFIGURATIONS_TYPE,
			phrsc.CONFIGURATIONS_TYPE_SERVER_VALUE);*/
	element= getXpathWebElement(phrsc.CONFIGURATIONS_TYPE);
	waitForElementPresent(phrsc.CONFIGURATIONS_TYPE,methodName);
	element.click();
	element= getXpathWebElement(phrsc.CONFIGURATIONS_TYPE_SERVER_CLICK);
	waitForElementPresent(phrsc.CONFIGURATIONS_TYPE_SERVER_CLICK,methodName);
	element.click();
	element= getXpathWebElement(phrsc.CONFIGURATIONS_TYPE_PROTOCOL_TYPE);
	waitForElementPresent(phrsc.CONFIGURATIONS_TYPE_PROTOCOL_TYPE,methodName);
	Thread.sleep(3000);
	element.click();
	Thread.sleep(1000);
	element= getXpathWebElement(phrsc.CONFIGURATIONS_TYPE_HTTP_CLICK);
	waitForElementPresent(phrsc.CONFIGURATIONS_TYPE_HTTP_CLICK,methodName);
	element.type(phrsc.CONFIGURATIONS_TYPE_HTTP_CLICK);
	element.click();
	element= getXpathWebElement(phrsc.CONFIGURATIONS_HOST);
	waitForElementPresent(phrsc.CONFIGURATIONS_HOST,methodName);
	element.click();
	element.type(drupalConst.CONFIGURATIONS_DRUPAL_SERVER_HOST_VALUE);
	element= getXpathWebElement(phrsc.CONFIGURATIONS_PORT);
	waitForElementPresent(phrsc.CONFIGURATIONS_PORT,methodName);
	element.click();
	element.type(drupalConst. CONFIGURATIONS_DRUPAL_SERVER_PORT_VALUE);
	element= getXpathWebElement(phrsc.CONFIGURATIONS_SERVER_DEPLOYDIR);
	element.click();
	element.type(drupalConst.CONFIGURATIONS_DRUPAL_SERVER_DEPLOYDIR_VALUE);
	//type(phrsc. CONFIGURATIONS_SERVER_TYPE_WAMP,phrsc.CONFIGURATIONS_DRUPAL_SERVER_TYPE_VALUE);
	element= getXpathWebElement(phrsc.CONFIGURATIONS_SERVER_CONTEXT);
	element.click();
	element.type(drupalConst.CONFIGURATIONS_DRUPAL_SERVER_CONTEXT_VALUE);
	element= getXpathWebElement(phrsc.CONFIGURATIONS_SAVE);
	element.click();
	waitForElementPresent(phrsc.CONFIGURATIONS_ADD,methodName);
}



public void Drupal7NoneDatabaseConfig(Drupal7ConstantsXml drupalConst,String methodName) throws InterruptedException,
IOException, Exception {

	element= getXpathWebElement(drupalConst.DRUPAL7_NONE_PROJ);
	waitForElementPresent(drupalConst.DRUPAL7_NONE_PROJ,methodName);
	element.click();
	Thread.sleep(3000);
	element= getXpathWebElement(phrsc.CONFIGURATIONS);
	Thread.sleep(1000);
	waitForElementPresent(phrsc.CONFIGURATIONS,methodName);
	element.click();
	waitForElementPresent(phrsc.CONFIGURATIONS_ADD,methodName);
	element= getXpathWebElement(phrsc.CONFIGURATIONS_ADD);
	element.click();
	waitForElementPresent(phrsc.CONFIGURATIONS_NAME,methodName);
	element= getXpathWebElement(phrsc.CONFIGURATIONS_NAME);
	Thread.sleep(1000);
	element.type(drupalConst.CONFIGURATIONS_DRUPAL_DB_NAME_VALUE);
	element=getXpathWebElement(phrsc.CONFIGURATIONS_DESCRIPTION);
	waitForElementPresent(phrsc.CONFIGURATIONS_DESCRIPTION,methodName);
	element.type(drupalConst.CONFIGURATIONS_DRUPAL_DB_DESCRIPTION_VALUE);
	element= getXpathWebElement(phrsc.CONFIGURATIONS_TYPE);
	waitForElementPresent(phrsc.CONFIGURATIONS_TYPE,methodName);
	element.click();
	Thread.sleep(1000);
	element= getXpathWebElement(phrsc.CONFIGURATIONS_TYPE_DATABASE_CLICK);
	waitForElementPresent(phrsc.CONFIGURATIONS_TYPE_DATABASE_CLICK,methodName);
	//Thread.sleep(3000);
	element.type(phrsc.CONFIGURATIONS_TYPE_DATABASE_CLICK);
	element.click();
	Thread.sleep(2000);
	element=getXpathWebElement(phrsc.CONFIGURATIONS_HOST);
	element=getXpathWebElement(phrsc.CONFIGURATIONS_HOST);
	waitForElementPresent(phrsc.CONFIGURATIONS_HOST,methodName);
	element.click();
	element.type(drupalConst.CONFIGURATIONS_DRUPAL_DB_HOST_VALUE);
	element.click();
	//Thread.sleep(1000);
	//Thread.sleep(1000);
	element=getXpathWebElement(phrsc.CONFIGURATIONS_PORT);
	waitForElementPresent(phrsc.CONFIGURATIONS_PORT,methodName);
	element.click();
	element.type(drupalConst.CONFIGURATIONS_DRUPAL_DB_PORT_VALUE);
	Thread.sleep(1000);
	element=getXpathWebElement(phrsc.CONFIGURATIONS_DATABASE_USERNAME_CLICK);
	waitForElementPresent(phrsc.CONFIGURATIONS_DATABASE_USERNAME_CLICK,methodName);
	Thread.sleep(1000);
	element.type(drupalConst.CONFIGURATIONS_DRUPAL_DB_USERNAME_VALUE);
	element=getXpathWebElement(phrsc.CONFIGURATIONS_TYPE_DATABASE_NAME_CLICK);
	waitForElementPresent(phrsc.CONFIGURATIONS_TYPE_DATABASE_NAME_CLICK,methodName);
	Thread.sleep(1000);
	element.type(methodName);
	element=getXpathWebElement(phrsc.CONFIGURATIONS_SAVE);
	waitForElementPresent(phrsc.CONFIGURATIONS_SAVE,methodName);
	Thread.sleep(1000);
	element.click();
	waitForElementPresent(phrsc.CONFIGURATIONS_ADD,methodName);
}


public void Drupal6NoneServerConfig(Drupal6ConstantsXml drupal6Const,String methodName) throws InterruptedException, IOException,
Exception {

	element= getXpathWebElement(drupalConst.DRUPALPROJ);
	waitForElementPresent(drupalConst.DRUPALPROJ,methodName);
	element.click();
	element= getXpathWebElement(phrsc.CONFIGURATIONS);
	waitForElementPresent(phrsc.CONFIGURATIONS,methodName);
	element.click();
	waitForElementPresent(phrsc.CONFIGURATIONS_ADD,methodName);
	element= getXpathWebElement(phrsc.CONFIGURATIONS_ADD);
	element.click();
	Thread.sleep(1000);
	element= getXpathWebElement(phrsc.CONFIGURATIONS_NAME);
	element.click();
	element.type(drupal6Const.CONFIGURATIONS_DRUPAL6_SERVER_NAME_VALUE);
	element.click();
	element= getXpathWebElement(phrsc.CONFIGURATIONS_DESCRIPTION);
	element.type(drupal6Const.CONFIGURATIONS_DRUPAL6_SERVER_DESCRIPTION_VALUE);
	select(phrsc.CONFIGURATIONS_TYPE,
			phrsc.CONFIGURATIONS_TYPE_SERVER_VALUE);
	element= getXpathWebElement(phrsc.CONFIGURATIONS_TYPE);
	waitForElementPresent(phrsc.CONFIGURATIONS_TYPE,methodName);
	element.click();
	element= getXpathWebElement(phrsc.CONFIGURATIONS_TYPE_SERVER_CLICK);
	waitForElementPresent(phrsc.CONFIGURATIONS_TYPE_SERVER_CLICK,methodName);
	element.click();
	element= getXpathWebElement(phrsc.CONFIGURATIONS_TYPE_PROTOCOL_TYPE);
	waitForElementPresent(phrsc.CONFIGURATIONS_TYPE_PROTOCOL_TYPE,methodName);
	Thread.sleep(3000);
	element.click();
	Thread.sleep(1000);
	element= getXpathWebElement(phrsc.CONFIGURATIONS_TYPE_HTTP_CLICK);
	waitForElementPresent(phrsc.CONFIGURATIONS_TYPE_HTTP_CLICK,methodName);
	element.type(phrsc.CONFIGURATIONS_TYPE_HTTP_CLICK);
	element.click();
	element= getXpathWebElement(phrsc.CONFIGURATIONS_HOST);
	waitForElementPresent(phrsc.CONFIGURATIONS_HOST,methodName);
	element.click();
	element.type(drupal6Const.CONFIGURATIONS_DRUPAL6_SERVER_HOST_VALUE);
	element= getXpathWebElement(phrsc.CONFIGURATIONS_PORT);
	waitForElementPresent(phrsc.CONFIGURATIONS_PORT,methodName);
	element.click();
	element.type(drupal6Const. CONFIGURATIONS_DRUPAL6_SERVER_PORT_VALUE);
	element= getXpathWebElement(phrsc.CONFIGURATIONS_SERVER_DEPLOYDIR);
	element.click();
	element.type(drupal6Const.CONFIGURATIONS_DRUPAL6_SERVER_DEPLOYDIR_VALUE);
	//type(phrsc. CONFIGURATIONS_SERVER_TYPE_WAMP,phrsc.CONFIGURATIONS_DRUPAL_SERVER_TYPE_VALUE);
	element= getXpathWebElement(phrsc.CONFIGURATIONS_SERVER_CONTEXT);
	element.click();
	element.type(drupal6Const.CONFIGURATIONS_DRUPAL6_SERVER_CONTEXT_VALUE);
	element= getXpathWebElement(phrsc.CONFIGURATIONS_SAVE);
	element.click();
	waitForElementPresent(phrsc.CONFIGURATIONS_ADD,methodName);
}

public void Drupal6NoneDatabaseConfig(Drupal6ConstantsXml drupal6Const,String methodName) throws InterruptedException,
IOException, Exception {

element= getXpathWebElement(drupal6Const.drupal6PROJ);
waitForElementPresent(drupal6Const.drupal6PROJ,methodName);
element.click();
Thread.sleep(3000);
element= getXpathWebElement(phrsc.CONFIGURATIONS);
Thread.sleep(1000);
waitForElementPresent(phrsc.CONFIGURATIONS,methodName);
element.click();
waitForElementPresent(phrsc.CONFIGURATIONS_ADD,methodName);
element= getXpathWebElement(phrsc.CONFIGURATIONS_ADD);
element.click();
waitForElementPresent(phrsc.CONFIGURATIONS_NAME,methodName);
element= getXpathWebElement(phrsc.CONFIGURATIONS_NAME);
Thread.sleep(1000);
element.type(drupal6Const.CONFIGURATIONS_DRUPAL6_DB_NAME_VALUE);
element=getXpathWebElement(phrsc.CONFIGURATIONS_DESCRIPTION);
waitForElementPresent(phrsc.CONFIGURATIONS_DESCRIPTION,methodName);
element.type(drupal6Const.CONFIGURATIONS_DRUPAL6_DB_DESCRIPTION_VALUE);
element= getXpathWebElement(phrsc.CONFIGURATIONS_TYPE);
waitForElementPresent(phrsc.CONFIGURATIONS_TYPE,methodName);
element.click();
Thread.sleep(1000);
element= getXpathWebElement(phrsc.CONFIGURATIONS_TYPE_DATABASE_CLICK);
waitForElementPresent(phrsc.CONFIGURATIONS_TYPE_DATABASE_CLICK,methodName);
element.type(phrsc.CONFIGURATIONS_TYPE_DATABASE_CLICK);
//Thread.sleep(3000);
element.click();
Thread.sleep(1000);
element=getXpathWebElement(phrsc.CONFIGURATIONS_HOST);
waitForElementPresent(phrsc.CONFIGURATIONS_HOST,methodName);
element.click();
element.type(drupal6Const.CONFIGURATIONS_DRUPAL6_DB_HOST_VALUE);
Thread.sleep(1000);
Thread.sleep(1000);
element=getXpathWebElement(phrsc.CONFIGURATIONS_PORT);
waitForElementPresent(phrsc.CONFIGURATIONS_PORT,methodName);
element.click();
element.type(drupal6Const.CONFIGURATIONS_DRUPAL6_DB_PORT_VALUE);
Thread.sleep(1000);
element=getXpathWebElement(phrsc.CONFIGURATIONS_DATABASE_USERNAME_CLICK);
waitForElementPresent(phrsc.CONFIGURATIONS_DATABASE_USERNAME_CLICK,methodName);
Thread.sleep(1000);
element.type(drupal6Const.CONFIGURATIONS_DRUPAL6_DB_USERNAME_VALUE);
element=getXpathWebElement(phrsc.CONFIGURATIONS_TYPE_DATABASE_NAME_CLICK);
waitForElementPresent(phrsc.CONFIGURATIONS_TYPE_DATABASE_NAME_CLICK,methodName);
Thread.sleep(1000);
element.type(methodName);
element=getXpathWebElement(phrsc.CONFIGURATIONS_SAVE);
waitForElementPresent(phrsc.CONFIGURATIONS_SAVE,methodName);
Thread.sleep(1000);
element.click();
waitForElementPresent(phrsc.CONFIGURATIONS_ADD,methodName);
}



public void SharepointServerConfig(SharepointConstantsXml spconst) throws InterruptedException,
		IOException, Exception {
		
	    element=getXpathWebElement(spconst.CREATEDPROJECT_SHAREPOINT);
	    element.click();
	    element=getXpathWebElement(phrsc.CONFIGURATIONS);
	    element.click();
	    element=getXpathWebElement(phrsc.CONFIGURATIONS_ADD);
		waitForElementPresent(phrsc.CONFIGURATIONS_ADD);
		element.click();
		element=getXpathWebElement(phrsc.CONFIGURATIONS_NAME);
		waitForElementPresent(phrsc.CONFIGURATIONS_NAME);
		element.click();
		element.type(spconst.SHAREPOINT_SERVER_CONFIGURATION_NAME_VALUE);
		element=getXpathWebElement(phrsc.CONFIGURATIONS_DESCRIPTION);
		element.type(spconst.SHAREPOINT_SERVER_CONFIGURATION_DESCRIPTION_VALUE);
		element=getXpathWebElement(phrsc.CONFIGURATIONS_TYPE);
		element.click();
		element=getXpathWebElement(phrsc.CONFIGURATIONS_TYPE_SERVER_CLICK);
		element.click();
		element=getXpathWebElement(phrsc.CONFIGURATIONS_HOST);
		element.type(spconst.SHAREPOINT_SETTINGS_HOST_VALUE);
		element=getXpathWebElement(phrsc.CONFIGURATIONS_PORT);
		element.type(spconst.SHAREPOINT_SETTINGS_PORT_VALUE);
		element=getXpathWebElement(phrsc.CONFIGURATIONS_SERVER_DEPLOYDIR);
		element.type(spconst.SHAREPOINT_SETTINGS_SERVER_DEPLOYDIR_VALUE);
		element=getXpathWebElement(phrsc.CONFIGURATIONS_SERVER_TYPE);
		element.type(spconst.SHAREPOINT_SETTINGS_SERVER_TYPE_VALUE);
		element=getXpathWebElement(phrsc.CONFIGURATIONS_SERVER_CONTEXT);
		element.type(spconst.SHAREPOINT_SETTINGS_SERVER_CONTEXT_VALUE);
		element=getXpathWebElement(phrsc.CONFIGURATIONS_SAVE);
		element.click();
		waitForElementPresent(phrsc.CONFIGURATIONS_ADD);
	}*/


	
public void PHPServerConfig(PhpConstantsXml phpconst,String methodName) throws InterruptedException,IOException, Exception {
  
	/*element= getXpathWebElement(drupalConst.DRUPALPROJ);
	waitForElementPresent(drupalConst.DRUPALPROJ,methodName);
	element.click();
	element= getXpathWebElement(phrsc.CONFIGURATIONS);
	waitForElementPresent(phrsc.CONFIGURATIONS,methodName);
	element.click();*/
	waitForElementPresent(phrsc.CONFIGURATIONS_ADD,methodName);
	element= getXpathWebElement(phrsc.CONFIGURATIONS_ADD);
	element.click();
	Thread.sleep(1000);
	element= getXpathWebElement(phrsc.CONFIGURATIONS_NAME);
	element.click();
	element.type(phpconst.PHP_CONFIGURATION_NAME_SERVER);
	element.click();
	element= getXpathWebElement(phrsc.CONFIGURATIONS_DESCRIPTION);
	element.type(phpconst.PHP_CONFIGURATION_DESCRIPTION_SERVER);
	/*select(phrsc.CONFIGURATIONS_TYPE,
			phrsc.CONFIGURATIONS_TYPE_SERVER_VALUE);*/
	element= getXpathWebElement(phrsc.CONFIGURATIONS_TYPE);
	waitForElementPresent(phrsc.CONFIGURATIONS_TYPE,methodName);
	element.click();
	element= getXpathWebElement(phrsc.CONFIGURATIONS_TYPE_SERVER_CLICK);
	waitForElementPresent(phrsc.CONFIGURATIONS_TYPE_SERVER_CLICK,methodName);
	element.click();
	element= getXpathWebElement(phrsc.CONFIGURATIONS_TYPE_PROTOCOL_TYPE);
	waitForElementPresent(phrsc.CONFIGURATIONS_TYPE_PROTOCOL_TYPE,methodName);
	Thread.sleep(3000);
	element.click();
	Thread.sleep(1000);
	element= getXpathWebElement(phrsc.CONFIGURATIONS_TYPE_HTTP_CLICK);
	waitForElementPresent(phrsc.CONFIGURATIONS_TYPE_HTTP_CLICK,methodName);
	element.type(phrsc.CONFIGURATIONS_TYPE_HTTP_CLICK);
	element.click();
	element= getXpathWebElement(phrsc.CONFIGURATIONS_HOST);
	waitForElementPresent(phrsc.CONFIGURATIONS_HOST,methodName);
	element.click();
	element.type(phpconst.SERVER_HOST_DESCRIPTIONS);
	element= getXpathWebElement(phrsc.CONFIGURATIONS_PORT);
	waitForElementPresent(phrsc.CONFIGURATIONS_PORT,methodName);
	element.click();
	element.type(phpconst. PHP_SERVER_PORT);
	element= getXpathWebElement(phrsc.CONFIGURATIONS_SERVER_DEPLOYDIR);
	element.click();
	element.type(phpconst.PHP_CONFIGURATION_SERVER_DEPLOYDIR);
	//type(phrsc. CONFIGURATIONS_SERVER_TYPE_WAMP,phrsc.CONFIGURATIONS_DRUPAL_SERVER_TYPE_VALUE);
	element= getXpathWebElement(phrsc.CONFIGURATIONS_SERVER_CONTEXT);
	element.click();
	element.type(phpconst.PHP_CONFIGURATION_SERVER_CONTEXT);
	element= getXpathWebElement(phrsc.CONFIGURATIONS_SAVE);
	element.click();
	waitForElementPresent(phrsc.CONFIGURATIONS_ADD,methodName);
}


public void PhpDatabaseConfig(PhpConstantsXml phpconst,String methodName) throws InterruptedException,IOException, Exception {
	
	element= getXpathWebElement(phpconst.PHPPROJECT);
	waitForElementPresent(phpconst.PHPPROJECT,methodName);
	element.click();
	Thread.sleep(3000);
	element= getXpathWebElement(phrsc.CONFIGURATIONS);
	Thread.sleep(1000);
	waitForElementPresent(phrsc.CONFIGURATIONS,methodName);
	element.click();
	waitForElementPresent(phrsc.CONFIGURATIONS_ADD,methodName);
	element= getXpathWebElement(phrsc.CONFIGURATIONS_ADD);
	element.click();
	waitForElementPresent(phrsc.CONFIGURATIONS_NAME,methodName);
	element= getXpathWebElement(phrsc.CONFIGURATIONS_NAME);
	Thread.sleep(1000);
	element.type(phpconst.PHP_CONFIGURATION_DB_NAME);
	element=getXpathWebElement(phrsc.CONFIGURATIONS_DESCRIPTION);
	waitForElementPresent(phrsc.CONFIGURATIONS_DESCRIPTION,methodName);
	element.type(phpconst.PHP_CONFIGURATION_DB_DESCRIPTION);
	element= getXpathWebElement(phrsc.CONFIGURATIONS_TYPE);
	waitForElementPresent(phrsc.CONFIGURATIONS_TYPE,methodName);
	element.click();
	Thread.sleep(1000);
	element= getXpathWebElement(phrsc.CONFIGURATIONS_TYPE_DATABASE_CLICK);
	waitForElementPresent(phrsc.CONFIGURATIONS_TYPE_DATABASE_CLICK,methodName);
	//Thread.sleep(3000);
	element.type(phrsc.CONFIGURATIONS_TYPE_DATABASE_CLICK);
	element.click();
	Thread.sleep(2000);
	element=getXpathWebElement(phrsc.CONFIGURATIONS_HOST);
	element=getXpathWebElement(phrsc.CONFIGURATIONS_HOST);
	waitForElementPresent(phrsc.CONFIGURATIONS_HOST,methodName);
	element.click();
	element.type(phpconst.SERVER_HOST_DESCRIPTIONS);
	element.click();
	//Thread.sleep(1000);
	//Thread.sleep(1000);
	element=getXpathWebElement(phrsc.CONFIGURATIONS_PORT);
	waitForElementPresent(phrsc.CONFIGURATIONS_PORT,methodName);
	element.click();
	element.type(phpconst.CONFIGURATION_PHP_PORT);
	Thread.sleep(1000);
	element=getXpathWebElement(phrsc.CONFIGURATIONS_DATABASE_USERNAME_CLICK);
	waitForElementPresent(phrsc.CONFIGURATIONS_DATABASE_USERNAME_CLICK,methodName);
	Thread.sleep(1000);
	element.type(phpconst.PHP_SETTINGS_DB_USERNAME);
	element=getXpathWebElement(phrsc.CONFIGURATIONS_TYPE_DATABASE_NAME_CLICK);
	waitForElementPresent(phrsc.CONFIGURATIONS_TYPE_DATABASE_NAME_CLICK,methodName);
	Thread.sleep(1000);
	element.type(methodName);
	element=getXpathWebElement(phrsc.CONFIGURATIONS_SAVE);
	waitForElementPresent(phrsc.CONFIGURATIONS_SAVE,methodName);
	Thread.sleep(1000);
	element.click();
	waitForElementPresent(phrsc.CONFIGURATIONS_ADD,methodName);
}

public void PHPNoneServerConfig(PhpConstantsXml phpconst,String methodName) throws InterruptedException,IOException, Exception {
	  
	/*element= getXpathWebElement(drupalConst.DRUPALPROJ);
	waitForElementPresent(drupalConst.DRUPALPROJ,methodName);
	element.click();
	element= getXpathWebElement(phrsc.CONFIGURATIONS);
	waitForElementPresent(phrsc.CONFIGURATIONS,methodName);
	element.click();*/
	waitForElementPresent(phrsc.CONFIGURATIONS_ADD,methodName);
	element= getXpathWebElement(phrsc.CONFIGURATIONS_ADD);
	element.click();
	Thread.sleep(1000);
	element= getXpathWebElement(phrsc.CONFIGURATIONS_NAME);
	element.click();
	element.type(phpconst.PHP_CONFIGURATION_NAME_SERVER);
	element.click();
	element= getXpathWebElement(phrsc.CONFIGURATIONS_DESCRIPTION);
	element.type(phpconst.PHP_CONFIGURATION_DESCRIPTION_SERVER);
	/*select(phrsc.CONFIGURATIONS_TYPE,
			phrsc.CONFIGURATIONS_TYPE_SERVER_VALUE);*/
	element= getXpathWebElement(phrsc.CONFIGURATIONS_TYPE);
	waitForElementPresent(phrsc.CONFIGURATIONS_TYPE,methodName);
	element.click();
	element= getXpathWebElement(phrsc.CONFIGURATIONS_TYPE_SERVER_CLICK);
	waitForElementPresent(phrsc.CONFIGURATIONS_TYPE_SERVER_CLICK,methodName);
	element.click();
	element= getXpathWebElement(phrsc.CONFIGURATIONS_TYPE_PROTOCOL_TYPE);
	waitForElementPresent(phrsc.CONFIGURATIONS_TYPE_PROTOCOL_TYPE,methodName);
	Thread.sleep(3000);
	element.click();
	Thread.sleep(1000);
	element= getXpathWebElement(phrsc.CONFIGURATIONS_TYPE_HTTP_CLICK);
	waitForElementPresent(phrsc.CONFIGURATIONS_TYPE_HTTP_CLICK,methodName);
	element.type(phrsc.CONFIGURATIONS_TYPE_HTTP_CLICK);
	element.click();
	element= getXpathWebElement(phrsc.CONFIGURATIONS_HOST);
	waitForElementPresent(phrsc.CONFIGURATIONS_HOST,methodName);
	element.click();
	element.type(phpconst.SERVER_HOST_DESCRIPTIONS);
	element= getXpathWebElement(phrsc.CONFIGURATIONS_PORT);
	waitForElementPresent(phrsc.CONFIGURATIONS_PORT,methodName);
	element.click();
	element.type(phpconst. PHP_SERVER_PORT);
	element= getXpathWebElement(phrsc.CONFIGURATIONS_SERVER_DEPLOYDIR);
	element.click();
	element.type(phpconst.PHP_CONFIGURATION_SERVER_DEPLOYDIR);
	//type(phrsc. CONFIGURATIONS_SERVER_TYPE_WAMP,phrsc.CONFIGURATIONS_DRUPAL_SERVER_TYPE_VALUE);
	element= getXpathWebElement(phrsc.CONFIGURATIONS_SERVER_CONTEXT);
	element.click();
	element.type(phpconst.PHP_CONFIGURATION_SERVER_CONTEXT);
	element= getXpathWebElement(phrsc.CONFIGURATIONS_SAVE);
	element.click();
	waitForElementPresent(phrsc.CONFIGURATIONS_ADD,methodName);
}


public void PhpNoneDatabaseConfig(PhpConstantsXml phpconst,String methodName) throws InterruptedException,IOException, Exception {
	
	element= getXpathWebElement(phpconst.PHP_NONE_PROJECT);
	waitForElementPresent(phpconst.PHP_NONE_PROJECT,methodName);
	element.click();
	Thread.sleep(3000);
	element= getXpathWebElement(phrsc.CONFIGURATIONS);
	Thread.sleep(1000);
	waitForElementPresent(phrsc.CONFIGURATIONS,methodName);
	element.click();
	waitForElementPresent(phrsc.CONFIGURATIONS_ADD,methodName);
	element= getXpathWebElement(phrsc.CONFIGURATIONS_ADD);
	element.click();
	waitForElementPresent(phrsc.CONFIGURATIONS_NAME,methodName);
	element= getXpathWebElement(phrsc.CONFIGURATIONS_NAME);
	Thread.sleep(1000);
	element.type(phpconst.PHP_CONFIGURATION_DB_NAME);
	element=getXpathWebElement(phrsc.CONFIGURATIONS_DESCRIPTION);
	waitForElementPresent(phrsc.CONFIGURATIONS_DESCRIPTION,methodName);
	element.type(phpconst.PHP_CONFIGURATION_DB_DESCRIPTION);
	element= getXpathWebElement(phrsc.CONFIGURATIONS_TYPE);
	waitForElementPresent(phrsc.CONFIGURATIONS_TYPE,methodName);
	element.click();
	Thread.sleep(1000);
	element= getXpathWebElement(phrsc.CONFIGURATIONS_TYPE_DATABASE_CLICK);
	waitForElementPresent(phrsc.CONFIGURATIONS_TYPE_DATABASE_CLICK,methodName);
	//Thread.sleep(3000);
	element.type(phrsc.CONFIGURATIONS_TYPE_DATABASE_CLICK);
	element.click();
	Thread.sleep(2000);
	element=getXpathWebElement(phrsc.CONFIGURATIONS_HOST);
	element=getXpathWebElement(phrsc.CONFIGURATIONS_HOST);
	waitForElementPresent(phrsc.CONFIGURATIONS_HOST,methodName);
	element.click();
	element.type(phpconst.SERVER_HOST_DESCRIPTIONS);
	element.click();
	//Thread.sleep(1000);
	//Thread.sleep(1000);
	element=getXpathWebElement(phrsc.CONFIGURATIONS_PORT);
	waitForElementPresent(phrsc.CONFIGURATIONS_PORT,methodName);
	element.click();
	element.type(phpconst.CONFIGURATION_PHP_PORT);
	Thread.sleep(1000);
	element=getXpathWebElement(phrsc.CONFIGURATIONS_DATABASE_USERNAME_CLICK);
	waitForElementPresent(phrsc.CONFIGURATIONS_DATABASE_USERNAME_CLICK,methodName);
	Thread.sleep(1000);
	element.type(phpconst.PHP_SETTINGS_DB_USERNAME);
	element=getXpathWebElement(phrsc.CONFIGURATIONS_TYPE_DATABASE_NAME_CLICK);
	waitForElementPresent(phrsc.CONFIGURATIONS_TYPE_DATABASE_NAME_CLICK,methodName);
	Thread.sleep(1000);
	element.type(methodName);
	element=getXpathWebElement(phrsc.CONFIGURATIONS_SAVE);
	waitForElementPresent(phrsc.CONFIGURATIONS_SAVE,methodName);
	Thread.sleep(1000);
	element.click();
	waitForElementPresent(phrsc.CONFIGURATIONS_ADD,methodName);
}

public void WordpressNoneServerConfig(WordPressConstants wordpressConst,String methodName) throws InterruptedException,IOException, Exception {
	  
	/*element= getXpathWebElement(drupalConst.DRUPALPROJ);
	waitForElementPresent(drupalConst.DRUPALPROJ,methodName);
	element.click();
	element= getXpathWebElement(phrsc.CONFIGURATIONS);
	waitForElementPresent(phrsc.CONFIGURATIONS,methodName);
	element.click();*/
	waitForElementPresent(phrsc.CONFIGURATIONS_ADD,methodName);
	element= getXpathWebElement(phrsc.CONFIGURATIONS_ADD);
	element.click();
	Thread.sleep(1000);
	element= getXpathWebElement(phrsc.CONFIGURATIONS_NAME);
	element.click();
	element.type(wordpressConst.WORDPRESS_SERVER_CONFIGURATION_NAME_VALUE);
	element.click();
	element= getXpathWebElement(phrsc.CONFIGURATIONS_DESCRIPTION);
	element.type(wordpressConst.WORDPRESS_SERVER_CONFIGURATION_DESCRIPTION_VALUE);
	/*select(phrsc.CONFIGURATIONS_TYPE,
			phrsc.CONFIGURATIONS_TYPE_SERVER_VALUE);*/
	element= getXpathWebElement(phrsc.CONFIGURATIONS_TYPE);
	waitForElementPresent(phrsc.CONFIGURATIONS_TYPE,methodName);
	element.click();
	element= getXpathWebElement(phrsc.CONFIGURATIONS_TYPE_SERVER_CLICK);
	waitForElementPresent(phrsc.CONFIGURATIONS_TYPE_SERVER_CLICK,methodName);
	element.click();
	element= getXpathWebElement(phrsc.CONFIGURATIONS_TYPE_PROTOCOL_TYPE);
	waitForElementPresent(phrsc.CONFIGURATIONS_TYPE_PROTOCOL_TYPE,methodName);
	Thread.sleep(3000);
	element.click();
	Thread.sleep(1000);
	element= getXpathWebElement(phrsc.CONFIGURATIONS_TYPE_HTTP_CLICK);
	waitForElementPresent(phrsc.CONFIGURATIONS_TYPE_HTTP_CLICK,methodName);
	element.type(phrsc.CONFIGURATIONS_TYPE_HTTP_CLICK);
	element.click();
	element= getXpathWebElement(phrsc.CONFIGURATIONS_HOST);
	waitForElementPresent(phrsc.CONFIGURATIONS_HOST,methodName);
	element.click();
	element.type(wordpressConst.WORDPRESS_SERVER_CONFIGURATION_HOST_NAME_VALUE);
	element= getXpathWebElement(phrsc.CONFIGURATIONS_PORT);
	waitForElementPresent(phrsc.CONFIGURATIONS_PORT,methodName);
	element.click();
	element.type(wordpressConst. WORDPRESS_SERVER_CONFIGURATION_PORT_VALUE);
	element= getXpathWebElement(phrsc.CONFIGURATIONS_SERVER_DEPLOYDIR);
	element.click();
	element.type(wordpressConst.CONFIGURATIONS_WORDPRESS_SERVER_DEPLOYDIR_VALUE);
	//type(phrsc. CONFIGURATIONS_SERVER_TYPE_WAMP,phrsc.CONFIGURATIONS_DRUPAL_SERVER_TYPE_VALUE);
	element= getXpathWebElement(phrsc.CONFIGURATIONS_SERVER_CONTEXT);
	element.click();
	element.type(wordpressConst.WORDPRESS_SERVER_CONFIGURATION_CONTEXT_VALUE);
	element= getXpathWebElement(phrsc.CONFIGURATIONS_SAVE);
	element.click();
	waitForElementPresent(phrsc.CONFIGURATIONS_ADD,methodName);
}


public void WordpressNoneDatabaseConfig(WordPressConstants wordpressConst,String methodName) throws InterruptedException,IOException, Exception {
	
	element= getXpathWebElement(wordpressConst.CREATEDPROJECT_WORDPRESS);
	waitForElementPresent(wordpressConst.CREATEDPROJECT_WORDPRESS,methodName);
	element.click();
	Thread.sleep(3000);
	element= getXpathWebElement(phrsc.CONFIGURATIONS);
	Thread.sleep(1000);
	waitForElementPresent(phrsc.CONFIGURATIONS,methodName);
	element.click();
	waitForElementPresent(phrsc.CONFIGURATIONS_ADD,methodName);
	element= getXpathWebElement(phrsc.CONFIGURATIONS_ADD);
	element.click();
	waitForElementPresent(phrsc.CONFIGURATIONS_NAME,methodName);
	element= getXpathWebElement(phrsc.CONFIGURATIONS_NAME);
	Thread.sleep(1000);
	element.type(wordpressConst.CONFIGURATIONS_WORDPRESS_DB_NAME_VALUE);
	element=getXpathWebElement(phrsc.CONFIGURATIONS_DESCRIPTION);
	waitForElementPresent(phrsc.CONFIGURATIONS_DESCRIPTION,methodName);
	element.type(wordpressConst.CONFIGURATIONS_WORDPRESS_DB_DESCRIPTION_VALUE);
	element= getXpathWebElement(phrsc.CONFIGURATIONS_TYPE);
	waitForElementPresent(phrsc.CONFIGURATIONS_TYPE,methodName);
	element.click();
	Thread.sleep(1000);
	element= getXpathWebElement(phrsc.CONFIGURATIONS_TYPE_DATABASE_CLICK);
	waitForElementPresent(phrsc.CONFIGURATIONS_TYPE_DATABASE_CLICK,methodName);
	//Thread.sleep(3000);
	element.type(phrsc.CONFIGURATIONS_TYPE_DATABASE_CLICK);
	element.click();
	Thread.sleep(2000);
	element=getXpathWebElement(phrsc.CONFIGURATIONS_HOST);
	element=getXpathWebElement(phrsc.CONFIGURATIONS_HOST);
	waitForElementPresent(phrsc.CONFIGURATIONS_HOST,methodName);
	element.click();
	element.type(wordpressConst.CONFIGURATIONS_WORDPRESS_DB_HOST_VALUE);
	element.click();
	//Thread.sleep(1000);
	//Thread.sleep(1000);
	element=getXpathWebElement(phrsc.CONFIGURATIONS_PORT);
	waitForElementPresent(phrsc.CONFIGURATIONS_PORT,methodName);
	element.click();
	element.type(wordpressConst.CONFIGURATIONS_WORDPRESS_DB_PORT_VALUE);
	Thread.sleep(1000);
	element=getXpathWebElement(phrsc.CONFIGURATIONS_DATABASE_USERNAME_CLICK);
	waitForElementPresent(phrsc.CONFIGURATIONS_DATABASE_USERNAME_CLICK,methodName);
	Thread.sleep(1000);
	element.type(wordpressConst.CONFIGURATIONS_WORDPRESS_DB_USERNAME_VALUE);
	element=getXpathWebElement(phrsc.CONFIGURATIONS_TYPE_DATABASE_NAME_CLICK);
	waitForElementPresent(phrsc.CONFIGURATIONS_TYPE_DATABASE_NAME_CLICK,methodName);
	Thread.sleep(1000);
	element.type(methodName);
	element=getXpathWebElement(phrsc.CONFIGURATIONS_SAVE);
	waitForElementPresent(phrsc.CONFIGURATIONS_SAVE,methodName);
	Thread.sleep(1000);
	element.click();
	waitForElementPresent(phrsc.CONFIGURATIONS_ADD,methodName);
}




public void JavaWebServiceServerConfig(JavaWebServConstantsXml jws) throws InterruptedException,
			IOException, Exception {
        
	    element=getXpathWebElement(jws.CREATEDPROJECT_JAVAWEBSERVICE);
	    element.click();
	    element=getXpathWebElement(phrsc.CONFIGURATIONS);
	    element.click();
	    element=getXpathWebElement(phrsc.CONFIGURATIONS_ADD);
		waitForElementPresent(phrsc.CONFIGURATIONS_ADD);
		element.click();
		element=getXpathWebElement(phrsc.CONFIGURATIONS_NAME);
		element.click();
		element=getXpathWebElement(phrsc.CONFIGURATIONS_NAME);
		element.type(jws.JAVAWEBSERVICE_SERVER_CONFIGURATION_NAME_VALUE);
		element=getXpathWebElement(phrsc.CONFIGURATIONS_DESCRIPTION);
		element.type(jws.JAVAWEBSERVICE_SERVER_CONFIGURATION_DESCRIPTION_VALUE);
		select(phrsc.CONFIGURATIONS_TYPE,
				phrsc.CONFIGURATIONS_TYPE_SERVER_VALUE);
		element=getXpathWebElement(phrsc.CONFIGURATIONS_TYPE);
		element.click();
		element=getXpathWebElement(phrsc.CONFIGURATIONS_TYPE_SERVER_VALUE);
		element.click();
		element=getXpathWebElement(phrsc.CONFIGURATIONS_TYPE_SERVER_CLICK);
		element.click();
		element=getXpathWebElement(phrsc.CONFIGURATIONS_TYPE_SERVER_CLICK);
		element.click();
		element=getXpathWebElement(phrsc.CONFIGURATIONS_HOST);
		element.type(jws.JAVAWEBSERVICE_SETTINGS_HOST_VALUE);
		element=getXpathWebElement(phrsc.CONFIGURATIONS_PORT);
		element.type(jws.JAVAWEBSERVICE_SETTINGS_PORT_VALUE);
		element=getXpathWebElement(phrsc.CONFIGURATIONS_SERVER_DEPLOYDIR);
		element.type(jws.JAVAWEBSERVICE_SETTINGS_SERVER_DEPLOYDIR_VALUE);
		element=getXpathWebElement(phrsc.CONFIGURATIONS_SERVER_TYPE);
		element.type(jws.JAVAWEBSERVICE_SETTINGS_SERVER_TYPE_VALUE);
		element=getXpathWebElement(phrsc.CONFIGURATIONS_SERVER_CONTEXT);
		element.type(jws.JAVAWEBSERVICE_SETTINGS_SERVER_CONTEXT_VALUE);
		element=getXpathWebElement(phrsc.CONFIGURATIONS_SAVE);
		element.click();
		waitForElementPresent(phrsc.CONFIGURATIONS_ADD);
	}

	public void JavaWebServiceDatabaseConfig(JavaWebServConstantsXml jws) throws InterruptedException,
			IOException, Exception {

		element=getXpathWebElement(jws.CREATEDPROJECT_JAVAWEBSERVICE);
		element.click();
		element=getXpathWebElement(phrsc.CONFIGURATIONS);
		element.click();
		element=getXpathWebElement(phrsc.CONFIGURATIONS);
		element.click();
		element=getXpathWebElement(phrsc.CONFIGURATIONS_ADD);
		waitForElementPresent(phrsc.CONFIGURATIONS_ADD);
		element.click();
		element=getXpathWebElement(phrsc.CONFIGURATIONS_NAME);
		element.click();
		element=getXpathWebElement(phrsc.CONFIGURATIONS_NAME);
		element.type(jws.JAVAWEBSERVICE_DB_CONFIGURATION_NAME_VALUE);
		element=getXpathWebElement(phrsc.CONFIGURATIONS_DESCRIPTION);
		element.type(jws.JAVAWEBSERVICE_DB_CONFIGURATION_DESCRIPTION_VALUE);
		select(phrsc.CONFIGURATIONS_TYPE, phrsc.CONFIGURATIONS_TYPE_DATABASE);
		element=getXpathWebElement(phrsc.CONFIGURATIONS_TYPE);
		element.click();
		element=getXpathWebElement(phrsc.CONFIGURATIONS_TYPE_DATABASE_CLICK);
		element.click();
		element=getXpathWebElement(phrsc.CONFIGURATIONS_HOST);
		element.type(jws.JAVAWEBSERVICE_SETTINGS_DB_HOST_VALUE);
		element=getXpathWebElement(phrsc.CONFIGURATIONS_PORT);
		element.type(jws.JAVAWEBSERVICE_SETTINGS_DB_PORT_VALUE);
		element=getXpathWebElement(phrsc.CONFIGURATIONS_DATABASE_USERNAME_CLICK);
		element.type(jws.JAVAWEBSERVICE_SETTINGS_TYPE_DB_USERNAME);
		element=getXpathWebElement(phrsc.CONFIGURATIONS_TYPE_DATABASE_NAME_CLICK);
		element.type(jws.JAVAWEBSERVICE_SETTINGS_DB_CONTEXT_VALUE);
		element=getXpathWebElement(phrsc.CONFIGURATIONS_SAVE);
		element.click();
		waitForElementPresent(phrsc.CONFIGURATIONS_ADD);
	}
	
	public void NodeJsServerConfig(NodeJSConstantsXml nodejsconst) throws InterruptedException,
	IOException, Exception {

		element=getXpathWebElement(nodejsconst.NODEJS_PROJECT_CREATION_ID);
		element.click();
		element=getXpathWebElement(phrsc.CONFIGURATIONS);
		element.click();
		element=getXpathWebElement(phrsc.CONFIGURATIONS_ADD);
		waitForElementPresent(phrsc.CONFIGURATIONS_ADD);
		element.click();
		element=getXpathWebElement(phrsc.CONFIGURATIONS_NAME);
		element.click();
		element=getXpathWebElement(phrsc.CONFIGURATIONS_NAME);
		element.type(nodejsconst. NODEJS_CONFIG_SERVER_NAME);
		element=getXpathWebElement(phrsc.CONFIGURATIONS_DESCRIPTION);
		element.type(nodejsconst.NODEJS_CONFIG_SERVER_DESCRIPTION);
		select(phrsc.CONFIGURATIONS_TYPE,
				phrsc.CONFIGURATIONS_TYPE_SERVER_VALUE);
		element= getXpathWebElement(phrsc.CONFIGURATIONS_TYPE);
		element.click();
		element= getXpathWebElement(phrsc.CONFIGURATIONS_TYPE_SERVER_CLICK);
		element.click();
		element= getXpathWebElement(phrsc.CONFIGURATIONS_HOST);
		element.type(nodejsconst.NODEJS_CONFIG_SERVER_HOST);
		element= getXpathWebElement(phrsc.CONFIGURATIONS_PORT);
		element.type(nodejsconst. NODEJS_CONFIG_SERVER_PORT);
		element=getXpathWebElement(phrsc.CONFIGURATIONS_SERVER_DEPLOYDIR);
		element.type(nodejsconst.NODEJS_CONFIG_SERVER_DEPLOY_DIR);
		element=getXpathWebElement(phrsc.CONFIGURATIONS_SERVER_CONTEXT);
		element.type(nodejsconst.NODEJS_CONFIG_SERVER_CONTEXT_NAME);
		element=getXpathWebElement(phrsc.CONFIGURATIONS_SAVE);
		element.click();
		waitForElementPresent(phrsc.CONFIGURATIONS_ADD);
		}

	public void NodeJsDatabaseConfig(NodeJSConstantsXml nodejsconst) throws InterruptedException,
	IOException, Exception {

		element=getXpathWebElement(nodejsconst.NODEJS_PROJECT_CREATION_ID);
		element.click();
		element=getXpathWebElement(phrsc.CONFIGURATIONS);
		element.click();
		element=getXpathWebElement(phrsc.CONFIGURATIONS_ADD);
		waitForElementPresent(phrsc.CONFIGURATIONS_ADD);
		element.click();
		element=getXpathWebElement(phrsc.CONFIGURATIONS_NAME);
		element.click();
		element.type(nodejsconst.NODEJS_CONFIG_DB_NAME);
		element=getXpathWebElement(phrsc.CONFIGURATIONS_DESCRIPTION);
		element.type(nodejsconst.NODEJS_CONFIG_DB_DESCRIPTION);
		select(phrsc.CONFIGURATIONS_TYPE, nodejsconst.NODEJS_CONFIG_CHOOSE_DB);
		//click(phrsc.CONFIGURATIONS_TYPE_DATABASE_CLICK);
		element=getXpathWebElement(phrsc.CONFIGURATIONS_TYPE);
		element.click();
		element=getXpathWebElement(nodejsconst.NODEJS_CONFIG_CHOOSE_DB_NAME);
		element.click();
		element=getXpathWebElement(phrsc.CONFIGURATIONS_HOST);
		element.type(nodejsconst.NODEJS_CONFIG_DB_HOST);
		element=getXpathWebElement(phrsc.CONFIGURATIONS_PORT);
		element.type(nodejsconst.NODEJS_CONFIG_DB_PORT);
		element=getXpathWebElement(phrsc.CONFIGURATIONS_DATABASE_USERNAME_CLICK);
		element.type(nodejsconst.NODEJS_CONFIG_DB_USERNAME);
		element=getXpathWebElement(phrsc.CONFIGURATIONS_TYPE_DATABASE_NAME_CLICK);
		element.type(nodejsconst.NODEJS_CONFIG_DB_DBNAME);
		element=getXpathWebElement(phrsc.CONFIGURATIONS_SAVE);
		element.click();
		waitForElementPresent(phrsc.CONFIGURATIONS_ADD);
		}
	
	public void HTML5WidgetServerConfig(YuiConstantsXml YuiConst) throws InterruptedException,
	IOException, Exception {

		element=getXpathWebElement(YuiConst.HTML5_WIDGET_PROJECT_CREATED_PROJ);
		element.click();
		element=getXpathWebElement(phrsc.CONFIGURATIONS);
		element.click();
		element=getXpathWebElement(phrsc.CONFIGURATIONS_ADD);
		waitForElementPresent(phrsc.CONFIGURATIONS_ADD);
		element.click();
		element=getXpathWebElement(phrsc.CONFIGURATIONS_NAME);
		element.click();
		element.type(YuiConst. HTML5_WIDGET_CONFIG_SERVER_NAME);
		element=getXpathWebElement(phrsc.CONFIGURATIONS_DESCRIPTION);
		element.type(YuiConst.HTML5_WIDGET_PROJECT_DESCRIPTION);
		select(phrsc.CONFIGURATIONS_TYPE,
				phrsc.CONFIGURATIONS_TYPE_SERVER_VALUE);
		element=getXpathWebElement(phrsc.CONFIGURATIONS_TYPE_SERVER_CLICK);
		element.click();
		element=getXpathWebElement(phrsc.CONFIGURATIONS_HOST);
		element.type(YuiConst.HTML5_WIDGET_CONFIG_SERVER_HOST);
		element=getXpathWebElement(phrsc.CONFIGURATIONS_PORT);
		element.type(YuiConst. HTML5_WIDGET_CONFIG_SERVER_PORT);
		element=getXpathWebElement(phrsc.CONFIGURATIONS_SERVER_DEPLOYDIR);
		element.type(YuiConst.HTML5_WIDGET_CONFIG_SERVER_DEPLOY_DIR);
		element=getXpathWebElement(phrsc.CONFIGURATIONS_SERVER_CONTEXT);
		element.type(YuiConst.HTML5_WIDGET_CONFIG_SERVER_CONTEXT_NAME);
		element=getXpathWebElement(phrsc.CONFIGURATIONS_SAVE);
		element.click();
		waitForElementPresent(phrsc.CONFIGURATIONS_ADD);
		}
	
	public void iPhonenativewebserviceConfig(iPhoneConstantsXml iPhoneConst) throws InterruptedException,
	IOException, Exception {

		element=getXpathWebElement(iPhoneConst.CREATEDPROJECT_iPHONENATIVE);
		element.click();
		element=getXpathWebElement(phrsc.CONFIGURATIONS);
		element.click();
		element=getXpathWebElement(phrsc.CONFIGURATIONS_ADD);
		waitForElementPresent(phrsc.CONFIGURATIONS_ADD);
		element.click();
		element=getXpathWebElement(phrsc.CONFIGURATIONS_NAME);
		element.click();
		element=getXpathWebElement(phrsc.CONFIGURATIONS_NAME);
		element.type(iPhoneConst. iPHONENATIVE_WEBSERVICE_CONFIGURATION_NAME_VALUE);
		element=getXpathWebElement(phrsc.CONFIGURATIONS_DESCRIPTION);
		element.type(iPhoneConst.iPHONENATIVE_WEBSERVICE_CONFIGURATION_DESCRIPTION_VALUE);
		select(phrsc.CONFIGURATIONS_TYPE,
				iPhoneConst.iPHONENATIVE_WEBSERVICE_CONFIGURATION_TYPE);
		element=getXpathWebElement(phrsc.CONFIGURATIONS_TYPE);
		element.click();
		
		element=getXpathWebElement(iPhoneConst.iPHONENATIVE_WEBSERVICE_CONFIGURATION_TYPE);
		element.click();
		element=getXpathWebElement(iPhoneConst.iPHONENATIVE_WEBSERVICE_CONFIGURATION_TYPE_CLICK);
		element.click();
		element=getXpathWebElement(phrsc.CONFIGURATIONS_HOST);
		element.type(iPhoneConst.iPHONENATIVE_WEBSERVICE_CONFIGURATION_HOST_NAME_VALUE);
		element=getXpathWebElement(phrsc.CONFIGURATIONS_PORT);
		element.type(iPhoneConst. iPHONENATIVE_WEBSERVICE_CONFIGURATION_PORT_VALUE);
		element=getXpathWebElement(phrsc.CONFIGURATIONS_SERVER_CONTEXT);
		element.type(iPhoneConst.iPHONENATIVE_WEBSERVICE_CONFIGURATION_CONTEXT_VALUE);
		element=getXpathWebElement(phrsc.CONFIGURATIONS_SAVE);
		element.click();
		waitForElementPresent(phrsc.CONFIGURATIONS_ADD);
		}





	public void HTML5WidgetWebServiceConfig(YuiConstantsXml YuiConst,String methodName) throws InterruptedException,
	IOException, Exception {
		Thread.sleep(2000);
		element=getXpathWebElement(YuiConst.HTML5_WIDGET_PROJECT_CREATED_PROJ);
		element.click();
		element=getXpathWebElement(phrsc.CONFIGURATIONS);
		element.click();
		element=getXpathWebElement(phrsc.CONFIGURATIONS_ADD);
		waitForElementPresent(phrsc.CONFIGURATIONS_ADD,methodName);
		element.click();
		element=getXpathWebElement(phrsc.CONFIGURATIONS_NAME);
		element.click();
		element.type(YuiConst. HTML5_WIDGET_CONFIG_WEBSERVICE_NAME);
		element=getXpathWebElement(phrsc.CONFIGURATIONS_DESCRIPTION);
		element.type(YuiConst.HTML5_WIDGET_CONFIG_WEBSERVICE_DESCRIPTION);
		/*select(phrsc.CONFIGURATIONS_TYPE,
				YuiConst.HTML5_WIDGET_CONFIG_TYPE);*/
		element=getXpathWebElement(phrsc.CONFIGURATIONS_TYPE);
		waitForElementPresent(phrsc.CONFIGURATIONS_TYPE,methodName);
		element.click();
		Thread.sleep(1000);
		element=getXpathWebElement(phrsc.CONFIGURATIONS_TYPE_WEBSERVICE_CLICK);
		element.type(phrsc.CONFIGURATIONS_TYPE_WEBSERVICE_CLICK);
		element.click();
		Thread.sleep(2000);
		//element=getXpathWebElement(phrsc.HTML5_WIDGET_CONFIG_TYPE_CLICK);
		//element.click();
		element= getXpathWebElement(phrsc.CONFIGURATIONS_TYPE_PROTOCOL_TYPE);
		waitForElementPresent(phrsc.CONFIGURATIONS_TYPE_PROTOCOL_TYPE,methodName);
		Thread.sleep(3000);
		element.click();
		Thread.sleep(1000);
		element= getXpathWebElement(phrsc.CONFIGURATIONS_TYPE_HTTP_CLICK);
		waitForElementPresent(phrsc.CONFIGURATIONS_TYPE_HTTP_CLICK,methodName);
		element.type(phrsc.CONFIGURATIONS_TYPE_HTTP_CLICK);
		element.click();
		element=getXpathWebElement(phrsc.CONFIGURATIONS_HOST);
		element.type(YuiConst.HTML5_WIDGET_CONFIG_WEBSERVICE_HOST);
		Thread.sleep(2000);
		element=getXpathWebElement(phrsc.CONFIGURATIONS_PORT);
		element.type(YuiConst. HTML5_WIDGET_CONFIG_WEBSERVICE_PORT);
		Thread.sleep(2000);
		element=getXpathWebElement(phrsc.CONFIGURATIONS_SERVER_CONTEXT);
		element.type(YuiConst.HTML5_WIDGET_CONFIG_WEBSERVICE_CONTEXTNAME);
		element=getXpathWebElement(phrsc.CONFIGURATIONS_SAVE);
		element.click();
		waitForElementPresent(phrsc.CONFIGURATIONS_ADD,methodName);

	}





	public void MobilewidgetServerConfig(MobWidgetConstantsXml mobwidg,String methodName)throws InterruptedException,
	IOException, Exception {
		
		element= getXpathWebElement(mobwidg.CREATEDPROJECT_HTML5MOBILEWIDGET);
		waitForElementPresent(mobwidg.CREATEDPROJECT_HTML5MOBILEWIDGET,methodName);
		element.click();
		element= getXpathWebElement(phrsc.CONFIGURATIONS);
		waitForElementPresent(phrsc.CONFIGURATIONS,methodName);
		element.click();
		waitForElementPresent(phrsc.CONFIGURATIONS_ADD,methodName);
		element= getXpathWebElement(phrsc.CONFIGURATIONS_ADD);
		element.click();
		Thread.sleep(1000);
		element= getXpathWebElement(phrsc.CONFIGURATIONS_NAME);
		element.click();
		element.type(mobwidg.MOBILEWIDGET_SERVER_CONFIGURATION_NAME_VALUE);
		element.click();
		element= getXpathWebElement(phrsc.CONFIGURATIONS_DESCRIPTION);
		element.type(mobwidg.MOBILEWIDGET_SERVER_CONFIGURATION_DESCRIPTION_VALUE);
		/*select(phrsc.CONFIGURATIONS_TYPE,
				phrsc.CONFIGURATIONS_TYPE_SERVER_VALUE);*/
		element= getXpathWebElement(phrsc.CONFIGURATIONS_TYPE);
		waitForElementPresent(phrsc.CONFIGURATIONS_TYPE,methodName);
		element.click();
		element= getXpathWebElement(phrsc.CONFIGURATIONS_TYPE_SERVER_CLICK);
		waitForElementPresent(phrsc.CONFIGURATIONS_TYPE_SERVER_CLICK,methodName);
		element.click();
		element= getXpathWebElement(phrsc.CONFIGURATIONS_TYPE_PROTOCOL_TYPE);
		waitForElementPresent(phrsc.CONFIGURATIONS_TYPE_PROTOCOL_TYPE,methodName);
		Thread.sleep(3000);
		element.click();
		Thread.sleep(1000);
		element= getXpathWebElement(phrsc.CONFIGURATIONS_TYPE_HTTP_CLICK);
		waitForElementPresent(phrsc.CONFIGURATIONS_TYPE_HTTP_CLICK,methodName);
		element.type(phrsc.CONFIGURATIONS_TYPE_HTTP_CLICK);
		element.click();
		element= getXpathWebElement(phrsc.CONFIGURATIONS_HOST);
		waitForElementPresent(phrsc.CONFIGURATIONS_HOST,methodName);
		element.click();
		element.type(mobwidg.CONFIGURATIONS_MOBILEWIDGET_SERVER_HOST_VALUE);
		element= getXpathWebElement(phrsc.CONFIGURATIONS_PORT);
		waitForElementPresent(phrsc.CONFIGURATIONS_PORT,methodName);
		element.click();
		element.type(mobwidg. CONFIGURATIONS_MOBILEWIDGET_SERVER_PORT_VALUE);
		
		element= getXpathWebElement(phrsc.CONFIGURATIONS_SERVER_USERNAME_CLICK);
		waitForElementPresent(phrsc.CONFIGURATIONS_SERVER_USERNAME_CLICK,methodName);
		element.click();
		element.type(mobwidg.CONFIGURATIONS_MOBILEWIDGET_SERVER_USERNAME_VALUE);
		element= getXpathWebElement(phrsc.CONFIGURATIONS_SERVER_PASSWORD_CLICK);
		waitForElementPresent(phrsc.CONFIGURATIONS_SERVER_PASSWORD_CLICK,methodName);
		element.click();
		element.type(mobwidg. CONFIGURATIONS_MOBILEWIDGET_SERVER_PASSWORD_VALUE);
		
		element= getXpathWebElement(phrsc.CONFIGURATIONS_SERVER_DEPLOYDIR);
		element.click();
		element.type(mobwidg.CONFIGURATIONS_MOBILEWIDGET_SERVER_DEPLOYDIR_VALUE);
		//type(phrsc. CONFIGURATIONS_SERVER_TYPE_WAMP,phrsc.CONFIGURATIONS_DRUPAL_SERVER_TYPE_VALUE);
		element= getXpathWebElement(phrsc.CONFIGURATIONS_SERVER_CONTEXT);
		element.click();
		element.type(mobwidg.CONFIGURATIONS_MOBILEWIDGET_SERVER_CONTEXT_VALUE);
		element= getXpathWebElement(phrsc.CONFIGURATIONS_SAVE);
		element.click();
		waitForElementPresent(phrsc.CONFIGURATIONS_ADD,methodName);
		
		
		
		/*element=getXpathWebElement(mobwidg.CREATEDPROJECT_HTML5MOBILEWIDGET);
		element.click();
		element=getXpathWebElement(phrsc.CONFIGURATIONS);
		element.click();
		element=getXpathWebElement(phrsc.CONFIGURATIONS_ADD);
		waitForElementPresent(phrsc.CONFIGURATIONS_ADD,methodName);
		element.click();
		element=getXpathWebElement(phrsc.CONFIGURATIONS_NAME);
		element.click();
		element=getXpathWebElement(phrsc.CONFIGURATIONS_NAME);
		element.type(mobwidg. MOBILEWIDGET_SERVER_CONFIGURATION_NAME_VALUE);
		element=getXpathWebElement(phrsc.CONFIGURATIONS_DESCRIPTION);
		element.type(mobwidg.MOBILEWIDGET_SERVER_CONFIGURATION_DESCRIPTION_VALUE);
		select(phrsc.CONFIGURATIONS_TYPE,
				phrsc.CONFIGURATIONS_TYPE_SERVER_VALUE);
		element=getXpathWebElement(phrsc.CONFIGURATIONS_TYPE);
		element.click();
		element=getXpathWebElement(phrsc.CONFIGURATIONS_TYPE_SERVER_VALUE);
		element.click();
		element=getXpathWebElement(phrsc.CONFIGURATIONS_TYPE_SERVER_CLICK);
		element.click();
		element=getXpathWebElement(phrsc.CONFIGURATIONS_HOST);
		element.click();
		element.type(mobwidg.HTML5_MOBILE_CONFIG_SERVER_HOST);
		element=getXpathWebElement(phrsc.CONFIGURATIONS_PORT);
		element.type(mobwidg. HTML5_MOBILE_CONFIG_SERVER_PORT);
		element=getXpathWebElement(phrsc.CONFIGURATIONS_SERVER_DEPLOYDIR);
		element.type(mobwidg.HTML5_MOBILE_CONFIG_SERVER_DEPLOY_DIR);
		element=getXpathWebElement(phrsc.CONFIGURATIONS_SERVER_CONTEXT);
		element.type(mobwidg.HTML5_MOBILE_CONFIG_SERVER_CONTEXT_NAME);
		element=getXpathWebElement(phrsc.CONFIGURATIONS_SAVE);
		element.click();
		waitForElementPresent(phrsc.CONFIGURATIONS_ADD,methodName);*/
	}


}
