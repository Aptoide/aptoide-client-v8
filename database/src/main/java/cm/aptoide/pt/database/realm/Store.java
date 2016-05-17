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
 * TODO create mapper POJO <> this
 */

public class Store extends RealmObject {
	@PrimaryKey private int storeId;
	private String url;
	private String apkPath;
	private String iconPath;
	private String webServicesPath;
	private String hash;
	private String theme;
	private String avatarUrl;
	private int downloads;
	private String description;
	private String list;
	private String items;
	private int latestTimestamp;
	private int topTimestamp;
	private boolean isUser;
	private boolean isFailed;
	private String storeName;
	private String username;
	private String password;

	public int getStoreId() {
		return storeId;
	}

	public void setStoreId(int storeId) {
		this.storeId = storeId;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getApkPath() {
		return apkPath;
	}

	public void setApkPath(String apkPath) {
		this.apkPath = apkPath;
	}

	public String getIconPath() {
		return iconPath;
	}

	public void setIconPath(String iconPath) {
		this.iconPath = iconPath;
	}

	public String getWebServicesPath() {
		return webServicesPath;
	}

	public void setWebServicesPath(String webServicesPath) {
		this.webServicesPath = webServicesPath;
	}

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

	public String getTheme() {
		return theme;
	}

	public void setTheme(String theme) {
		this.theme = theme;
	}

	public String getAvatarUrl() {
		return avatarUrl;
	}

	public void setAvatarUrl(String avatarUrl) {
		this.avatarUrl = avatarUrl;
	}

	public int getDownloads() {
		return downloads;
	}

	public void setDownloads(int downloads) {
		this.downloads = downloads;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getList() {
		return list;
	}

	public void setList(String list) {
		this.list = list;
	}

	public String getItems() {
		return items;
	}

	public void setItems(String items) {
		this.items = items;
	}

	public int getLatestTimestamp() {
		return latestTimestamp;
	}

	public void setLatestTimestamp(int latestTimestamp) {
		this.latestTimestamp = latestTimestamp;
	}

	public int getTopTimestamp() {
		return topTimestamp;
	}

	public void setTopTimestamp(int topTimestamp) {
		this.topTimestamp = topTimestamp;
	}

	public boolean isUser() {
		return isUser;
	}

	public void setUser(boolean user) {
		isUser = user;
	}

	public boolean isFailed() {
		return isFailed;
	}

	public void setFailed(boolean failed) {
		isFailed = failed;
	}

	public String getStoreName() {
		return storeName;
	}

	public void setStoreName(String storeName) {
		this.storeName = storeName;
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
}
