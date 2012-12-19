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
/*
 * $Id: Login.java 471756 2006-11-06 15:01:43Z husted $
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.photon.phresco.framework.actions;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.photon.phresco.commons.model.Customer;
import com.photon.phresco.commons.model.User;
import com.photon.phresco.exception.PhrescoException;
import com.photon.phresco.util.Credentials;

public class Login extends FrameworkBaseAction {

    private static final long serialVersionUID = -1858839078372821734L;
    private static final Logger S_LOGGER = Logger.getLogger(Login.class);
    private static Boolean isDebugEnabled = S_LOGGER.isDebugEnabled();
    private static Boolean debugEnabled  = S_LOGGER.isDebugEnabled();
    
    private String username = null;
    private String password = null;
    private boolean loginFirst = true;
    
    private String logoImgUrl = "";
    private String brandingColor = "";
    
    public String login() throws IOException {
        if (isDebugEnabled) {
            S_LOGGER.debug("Entering Method  Login.login()");
        }
        
        User user = (User) getSessionAttribute(SESSION_USER_INFO);
        if (user != null) {
            return SUCCESS;
        }
        if (loginFirst) {
            setReqAttribute(REQ_LOGIN_ERROR, "");
            return LOGIN_FAILURE;   
        }
        if (validateLogin()) {
            return authenticate();
        }
        
        return LOGIN_FAILURE;
    }
    
    public String logout() {
        if (debugEnabled) {
            S_LOGGER.debug("Entering Method  Login.logout()");
        }
        
        removeSessionAttribute(SESSION_USER_INFO);
        String errorTxt = (String) getSessionAttribute(REQ_LOGIN_ERROR);
        if (StringUtils.isNotEmpty(errorTxt)) {
            setReqAttribute(REQ_LOGIN_ERROR, getText(errorTxt));
        } else {
            setReqAttribute(REQ_LOGIN_ERROR, getText(SUCCESS_LOGOUT));
        }
        removeSessionAttribute(REQ_LOGIN_ERROR);
        
        return SUCCESS;
    }
    
    private String authenticate() throws IOException {
        if (isDebugEnabled) {
            S_LOGGER.debug("Entering Method  Login.authenticate()");
        }
        
        User user = null;
        try {
            Credentials credentials = new Credentials(getUsername(), getPassword());
            user = doLogin(credentials);
            if (user == null) {
                setReqAttribute(REQ_LOGIN_ERROR, getText(ERROR_LOGIN));
                
                return LOGIN_FAILURE;
            }
            if (!user.isPhrescoEnabled()) {
                setReqAttribute(REQ_LOGIN_ERROR, getText(ERROR_LOGIN_ACCESS_DENIED));
                
                return LOGIN_FAILURE;
            }
            setSessionAttribute(SESSION_USER_INFO, user);
            
            //encode the password
            byte[] encodedPwd = Base64.encodeBase64(getPassword().getBytes());
            String encodedString = new String(encodedPwd);
            
            setSessionAttribute(SESSION_USER_PASSWORD, encodedString);
        } catch (PhrescoException e) {
            setReqAttribute(REQ_LOGIN_ERROR, getText(ERROR_EXCEPTION));
            return LOGIN_FAILURE;
        }
            
        return SUCCESS;
    }

    private boolean validateLogin() {
        if (isDebugEnabled) {
            S_LOGGER.debug("Entering Method  Login.validateLogin()");
        }
        
        if (StringUtils.isEmpty(getUsername())) {
            setReqAttribute(REQ_LOGIN_ERROR, getText(ERROR_LOGIN_INVALID_USERNAME));
            return false;
        }
        if (StringUtils.isEmpty(getPassword())) {
            setReqAttribute(REQ_LOGIN_ERROR, getText(ERROR_LOGIN_INVALID_PASSWORD));
            return false;
        }
        
        return true;
    }
    
    public String fetchLogoImgUrl() {
    	FileOutputStream fileOutputStream = null;
    	InputStream fileInputStream = null;
    	try {
    		StringBuilder sb = new StringBuilder(IMAGES)
    		.append(FORWARD_SLASH)
    		.append(CUSTOMERS)
    		.append(FORWARD_SLASH)
    		.append(getCustomerId())
    		.append(PNG);
    		String path = getHttpRequest().getSession().getServletContext().getRealPath(sb.toString());
    		File imageFilePath = new File(path);
    		if (!imageFilePath.exists()) {
    			fileOutputStream =  new FileOutputStream(imageFilePath);
        		byte[] imagebyte = new byte[1024];
        		int length;
        		fileInputStream = getServiceManager().getIcon(getCustomerId());
        		while ((length = fileInputStream.read(imagebyte)) != -1) {
        			fileOutputStream.write(imagebyte, 0, length);
        		}
    		}
    		setLogoImgUrl(sb.toString());
    		User user = (User) getSessionAttribute(SESSION_USER_INFO);
    		List<Customer> customers = user.getCustomers();
    		for (Customer customer : customers) {
				if (customer.getId().equals(getCustomerId())) {
					setBrandingColor(customer.getBrandingColor());
					break;
				}
			}
    	} catch (PhrescoException e) {
    		return showErrorPopup(e, getText(""));
    	} catch (FileNotFoundException e) {
    		return showErrorPopup(new PhrescoException(e), getText(""));
		} catch (IOException e) {
			return showErrorPopup(new PhrescoException(e), getText(""));
		} finally {
    		try {
    			if (fileOutputStream != null) {
    				fileOutputStream.close();
    			}
    			if (fileInputStream != null) {
    				fileInputStream.close();
    			}
			} catch (IOException e) {
				return showErrorPopup(new PhrescoException(e), getText(""));
			}
    	}
    	
    	return SUCCESS;
    }
    
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    
    public boolean isLoginFirst() {
        return loginFirst;
    }

    public void setLoginFirst(boolean loginFirst) {
        this.loginFirst = loginFirst;
    }

	public String getLogoImgUrl() {
		return logoImgUrl;
	}

	public void setLogoImgUrl(String logoImgUrl) {
		this.logoImgUrl = logoImgUrl;
	}

	public String getBrandingColor() {
		return brandingColor;
	}

	public void setBrandingColor(String brandingColor) {
		this.brandingColor = brandingColor;
	}
}