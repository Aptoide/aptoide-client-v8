/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 12/05/2016.
 */

package cm.aptoide.pt.database.realm;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by sithengineer on 12/05/16.
 *
 * TODO check with dataprovider and models...
 */

public class Updates extends RealmObject {
	@PrimaryKey private long appId;
	private String icon;

	private String packageName;
	private int versionCode;
	private String signature;
	private long timestamp;
	private String md5;
	private String url;
	private double fileSize;
	private String updateVersionName;
	private String alternativeUrl;
	private String updateVersionCode;

	public long getAppId() {
		return appId;
	}

	public void setAppId(long appId) {
		this.appId = appId;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public int getVersionCode() {
		return versionCode;
	}

	public void setVersionCode(int versionCode) {
		this.versionCode = versionCode;
	}

	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public String getMd5() {
		return md5;
	}

	public void setMd5(String md5) {
		this.md5 = md5;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public double getFileSize() {
		return fileSize;
	}

	public void setFileSize(double fileSize) {
		this.fileSize = fileSize;
	}

	public String getUpdateVersionName() {
		return updateVersionName;
	}

	public void setUpdateVersionName(String updateVersionName) {
		this.updateVersionName = updateVersionName;
	}

	public String getAlternativeUrl() {
		return alternativeUrl;
	}

	public void setAlternativeUrl(String alternativeUrl) {
		this.alternativeUrl = alternativeUrl;
	}

	public String getUpdateVersionCode() {
		return updateVersionCode;
	}

	public void setUpdateVersionCode(String updateVersionCode) {
		this.updateVersionCode = updateVersionCode;
	}
}
