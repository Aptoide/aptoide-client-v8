/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 22/06/2016.
 */

package cm.aptoide.pt.database.realm;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by sithengineer on 12/05/16.
 */
public class ExcludedUpdate extends RealmObject {

	//	public static final String ID = "id";
	public static final String PACKAGE_NAME = "packageName";
	public static final String NAME = "name";
	public static final String ICON = "icon";
	public static final String VERSION_CODE = "versionCode";
	public static final String APK_ID = "apkId";

	//	@PrimaryKey private long id;
	@PrimaryKey private String packageName;
	private String name;
	private String icon;
	private int versionCode;
	private long apkId;

	public ExcludedUpdate() { }

	public ExcludedUpdate(Update pojo) {
		setName(pojo.getLabel());
		setPackageName(pojo.getPackageName());
		setIcon(pojo.getIcon());
		setApkId(pojo.getAppId());
		setVersionCode(pojo.getVersionCode());
	}

//	public long getId() {
//		return id;
//	}
//
//	public void setId(long id) {
//		this.id = id;
//	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public int getVersionCode() {
		return versionCode;
	}

	public void setVersionCode(int versionCode) {
		this.versionCode = versionCode;
	}

	public long getApkId() {
		return apkId;
	}

	public void setApkId(long apkId) {
		this.apkId = apkId;
	}
}
