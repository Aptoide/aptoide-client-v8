/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 12/05/2016.
 */

package cm.aptoide.pt.database.realm;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by sithengineer on 12/05/16.
 */

public class Rollback extends RealmObject {

	public enum Action {
		UPDATE, DOWNGRADE, UNINSTALL, INSTALL
	}

	@PrimaryKey private int id;
	private String versionName;
	private String previousVersionName;
	private String timestamp;
	private String name;
	private String icon;
	private int action;
	private String storeName;
	private int confirmed;
	private String previousVersionMd5;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getAction() {
		return action;
	}

	public void setAction(int action) {
		this.action = action;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public String getMd5() {
		return previousVersionMd5;
	}

	public void setMd5(String md5) {
		this.previousVersionMd5 = md5;
	}

	public String getIconPath() {
		return icon;
	}

	public void setIconPath(String iconPath) {
		this.icon = iconPath;
	}

	public String getPreviousVersionMd5() {
		return previousVersionMd5;
	}

	public void setPreviousVersionMd5(String previousVersionMd5) {
		this.previousVersionMd5 = previousVersionMd5;
	}

	public String getPreviousVersionName() {
		return previousVersionName;
	}

	public void setPreviousVersionName(String previousVersionName) {
		this.previousVersionName = previousVersionName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getConfirmed() {
		return confirmed;
	}

	public void setConfirmed(int confirmed) {
		this.confirmed = confirmed;
	}

	public String getStoreName() {
		return storeName;
	}

	public String getVersionName() {
		return versionName;
	}

	public void setVersionName(String versionName) {
		this.versionName = versionName;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public void setStoreName(String storeName) {
		this.storeName = storeName;
	}
}
