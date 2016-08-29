/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 04/08/2016.
 */

package cm.aptoide.pt.database.realm;

import java.util.Calendar;

import cm.aptoide.pt.model.v7.GetAppMeta;
import cm.aptoide.pt.model.v7.Obb;
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
	public static final String APP_NAME = "app_name";
	public static final String ICON = "icon";
	public static final String ACTION = "action";
	public static final String MD5 = "md5";
	public static final String CONFIRMED = "confirmed";
//	public static final String REFERRER = "referrer";

	//	@PrimaryKey private int id = -1;
	private String appName;
	private String packageName;
	private String icon;
	private String versionName;
	private int versionCode;
	private long timestamp;
	private String action;
	@PrimaryKey private String md5;
	private boolean confirmed;
	private String trustedBadge;
	//	private String referrer;

	private long appId;
	private String alternativeApkPath;
	private String apkPath;
	private String mainObbName;
	private String patchObbMd5;
	private String patchObbPath;
	private String patchObbName;
	private String mainObbMd5;
	private String mainObbPath;
	private double fileSize;

	// TODO: 27-05-2016 neuro Nem sei o k fazer a isto..
//	private String previousVersionName;
//	private String storeName;

	public Rollback() {
	}

	public Rollback(GetAppMeta.App app, Action action) {
		setAction(action.name());
		setPackageName(app.getPackageName());
		setVersionCode(app.getFile().getVercode());
		setAppName(app.getName());
		setIconPath(app.getIcon());
		setVersionName(app.getFile().getVername());
		setTimestamp(Calendar.getInstance().getTimeInMillis());
		setMd5(app.getFile().getMd5sum());
//		computeId();

		appId = app.getId();
		appName = app.getName();
		icon = app.getIcon();

		trustedBadge = app.getFile().getMalware().getRank().name();

		packageName = app.getPackageName();
		//		versionCode = app.getFile().getVercode();
		//		signature = app.get;
		//		timestamp = app.getModified();
		md5 = app.getFile().getMd5sum();
		apkPath = app.getFile().getPath();
		fileSize = app.getFile().getFilesize();
		versionName = app.getFile().getVername();
		alternativeApkPath = app.getFile().getPathAlt();
		versionCode = app.getFile().getVercode();

		Obb obb = app.getObb();
		if (obb != null) {
			Obb.ObbItem obbMain = obb.getMain();
			if (obbMain != null) {
				mainObbName = obbMain.getFilename();
				mainObbPath = obbMain.getPath();
				mainObbMd5 = obbMain.getMd5sum();
			}

			Obb.ObbItem patch = obb.getPatch();
			if (patch != null) {
				patchObbName = patch.getFilename();
				patchObbPath = patch.getPath();
				patchObbMd5 = patch.getMd5sum();
			}
		}
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

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public boolean isConfirmed() {
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

	//	public String getReferrer() {
	//		return referrer;
	//	}

	//	public void setReferrer(String referrer) {
	//		this.referrer = referrer;
	//	}

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

	//	public String getCpiUrl() {
	//		return cpiUrl;
	//	}
	//
	//	public void setCpiUrl(String cpiUrl) {
	//		this.cpiUrl = cpiUrl;
	//	}

	public long getAppId() {
		return appId;
	}

	public void setAppId(long appId) {
		this.appId = appId;
	}

	public String getAlternativeApkPath() {
		return alternativeApkPath;
	}

	public void setAlternativeApkPath(String alternativeApkPath) {
		this.alternativeApkPath = alternativeApkPath;
	}

	public String getApkPath() {
		return apkPath;
	}

	public void setApkPath(String apkPath) {
		this.apkPath = apkPath;
	}

	public String getPatchObbMd5() {
		return patchObbMd5;
	}

	public void setPatchObbMd5(String patchObbMd5) {
		this.patchObbMd5 = patchObbMd5;
	}

	public String getPatchObbPath() {
		return patchObbPath;
	}

	public void setPatchObbPath(String patchObbPath) {
		this.patchObbPath = patchObbPath;
	}

	public String getMainObbMd5() {
		return mainObbMd5;
	}

	public void setMainObbMd5(String mainObbMd5) {
		this.mainObbMd5 = mainObbMd5;
	}

	public String getMainObbPath() {
		return mainObbPath;
	}

	public void setMainObbPath(String mainObbPath) {
		this.mainObbPath = mainObbPath;
	}

	public double getFileSize() {
		return fileSize;
	}

	public void setFileSize(double fileSize) {
		this.fileSize = fileSize;
	}

	public String getMainObbName() {
		return mainObbName;
	}

	public void setMainObbName(String mainObbName) {
		this.mainObbName = mainObbName;
	}

	public String getPatchObbName() {
		return patchObbName;
	}

	public void setPatchObbName(String patchObbName) {
		this.patchObbName = patchObbName;
	}

	public String getTrustedBadge() {
		return trustedBadge;
	}

	public enum Action {
		UPDATE, DOWNGRADE, UNINSTALL, INSTALL
	}
}
