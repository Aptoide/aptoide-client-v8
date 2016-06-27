/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 15/06/2016.
 */

package cm.aptoide.pt.database.realm;

import android.content.pm.PackageInfo;

import java.util.Calendar;

import cm.aptoide.pt.utils.AptoideUtils;
import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by sithengineer on 12/05/16.
 */

public class Rollback extends RealmObject {

	//	public static final String ID = "id";
	public static final String VERSION_NAME = "versionName";
	public static final String VERSION_CODE = "versionCode";
	public static final String PACKAGE_NAME = "packageName";
	public static final String TIMESTAMP = "timestamp";
	public static final String NAME = "name";
	public static final String ICON = "icon";
	public static final String ACTION = "action";
	public static final String MD5 = "md5";
	public static final String CONFIRMED = "confirmed";
	public static final String REFERRER = "referrer";

	//	@PrimaryKey private int id = -1;
	private String name;
	private String packageName;
	private String icon;
	private String versionName;
	private int versionCode;
	private long timestamp;
	private String action;
	@PrimaryKey private String md5;
	private boolean confirmed;
	private String referrer;

	// TODO: 27-05-2016 neuro Nem sei o k fazer a isto..
//	private String previousVersionName;
//	private String storeName;

	public Rollback() {
	}

	public Rollback(PackageInfo packageInfo, Action action) {
		setAction(action.name());
		setPackageName(packageInfo.packageName);
		setVersionCode(packageInfo.versionCode);
		setName(AptoideUtils.SystemU.getApkLabel(packageInfo));
		setIconPath(AptoideUtils.SystemU.getApkIconPath(packageInfo));
		setVersionName(packageInfo.versionName);
		setTimestamp(Calendar.getInstance().getTimeInMillis());
		setMd5(AptoideUtils.AlgorithmU.computeMd5(packageInfo));
//		computeId();
	}

	public void confirm(Realm realm) {
		realm.beginTransaction();
		setConfirmed(true);
		realm.commitTransaction();
	}

//	public int getId() {
//		return id;
//	}

//	public void setId(int id) {
//		this.id = id;
//	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
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

	public String getIconPath() {
		return icon;
	}

	public void setIconPath(String iconPath) {
		this.icon = iconPath;
	}

//	public String getPreviousVersionName() {
//		return previousVersionName;
//	}
//
//	public void setPreviousVersionName(String previousVersionName) {
//		this.previousVersionName = previousVersionName;
//	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean getConfirmed() {
		return confirmed;
	}

	public void setConfirmed(boolean confirmed) {
		this.confirmed = confirmed;
	}

//	public String getStoreName() {
//		return storeName;
//	}

//	public void setStoreName(String storeName) {
//		this.storeName = storeName;
//	}

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

	public int getVersionCode() {
		return versionCode;
	}

	public void setVersionCode(int versionCode) {
		this.versionCode = versionCode;
	}

	public String getReferrer() {
		return referrer;
	}

	public void setReferrer(String referrer) {
		this.referrer = referrer;
	}

	//	public void computeId() {
//		@Cleanup Realm realm = Database.get(Application.getContext());
//		int n;
//		Number max = realm.where(Rollback.class).max(Rollback.ID);
//		if (max != null) {
//			n = max.intValue() + 1;
//		} else {
//			n = 0;
//		}
//		id = n;
//	}

	public enum Action {
		UPDATE, DOWNGRADE, UNINSTALL, INSTALL
	}
}
