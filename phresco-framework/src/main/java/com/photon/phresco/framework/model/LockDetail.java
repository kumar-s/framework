package com.photon.phresco.framework.model;

import java.util.Date;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

public class LockDetail {

	private String actionType = "";
    private String userName = "";
    private Date startedDate;
    private String appId = "";
    private String uniqueKey = "";
	
    public LockDetail() {
    	
    }

	public LockDetail(String appId, String actionType, String userName, String uniqueKey) {
    	this.setAppId(appId);
		this.setActionType(actionType);
		this.setUserName(userName);
		this.setUniqueKey(uniqueKey);
		this.setStartedDate(new Date());
	}

	public void setActionType(String actionType) {
		this.actionType = actionType;
	}

	public String getActionType() {
		return actionType;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserName() {
		return userName;
	}

	public void setStartedDate(Date startedDate) {
		this.startedDate = startedDate;
	}

	public Date getStartedDate() {
		return startedDate;
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getUniqueKey() {
		return uniqueKey;
	}

	public void setUniqueKey(String uniqueKey) {
		this.uniqueKey = uniqueKey;
	}

	public String toString() {
        return new ToStringBuilder(this,
                ToStringStyle.DEFAULT_STYLE)
                .append(super.toString())
                .append("actionType", getActionType())
                .append("userName", getUserName())
                .append("startedDate", getStartedDate())
                .append("appId", getAppId())
                .append("uniqueKey", getUniqueKey())
                .toString();
    }
}