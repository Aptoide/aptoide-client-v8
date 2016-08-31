/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 24/08/2016.
 */

package cm.aptoide.pt.database.realm;

import cm.aptoide.pt.model.v7.Obb;
import cm.aptoide.pt.model.v7.listapp.App;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by sithengineer on 12/05/16.
 *
 * TODO check with dataprovider and models...
 */

public class Update extends RealmObject {

	public static final String APP_ID = "appId";
	public static final String ICON = "icon";
	public static final String PACKAGE_NAME = "packageName";
	public static final String VERSION_CODE = "versionCode";
	public static final String SIGNATURE = "signature";
	public static final String TIMESTAMP = "timestamp";
	public static final String MD5 = "md5";
	public static final String APK_PATH = "apkPath";
	public static final String FILE_SIZE = "fileSize";
	public static final String UPDATE_VERSION_NAME = "updateVersionName";
	public static final String ALTERNATIVE_URL = "alternativeApkPath";
	public static final String UPDATE_VERSION_CODE = "updateVersionCode";
	public static final String EXCLUDED = "excluded";

	@PrimaryKey private String packageName;
	private long appId;
	private String label;
	private String icon;
	private int versionCode;
	//	private String signature;
	private long timestamp;
	private String md5;
	private String apkPath;
	private double fileSize;
	private String updateVersionName;
	private int updateVersionCode;
	private boolean excluded;
	private String trustedBadge;
	private String alternativeApkPath;

	// Obb
	private String mainObbName;
	private String mainObbPath;
	private String mainObbMd5;
	private String patchObbName;
	private String patchObbPath;
	private String patchObbMd5;

	public Update() {
	}

	public Update(App app) {
		appId = app.getId();
		label = app.getName();
		icon = app.getIcon();

		packageName = app.getPackageName();
//		versionCode = app.getFile().getVercode();
//		signature = app.get;
//		timestamp = app.getModified();
		md5 = app.getFile().getMd5sum();
		apkPath = app.getFile().getPath();
		fileSize = app.getFile().getFilesize();
		updateVersionName = app.getFile().getVername();
		alternativeApkPath = app.getFile().getPathAlt();
		updateVersionCode = app.getFile().getVercode();
		trustedBadge = app.getFile().getMalware().getRank().name();

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

	public long getAppId() {
		return appId;
	}

	public void setAppId(long appId) {
		this.appId = appId;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
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

//	public String getSignature() {
//		return signature;
//	}
//
//	public void setSignature(String signature) {
//		this.signature = signature;
//	}

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

	public String getApkPath() {
		return apkPath;
	}

	public void setApkPath(String apkPath) {
		this.apkPath = apkPath;
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

	public String getAlternativeApkPath() {
		return alternativeApkPath;
	}

	public void setAlternativeApkPath(String alternativeApkPath) {
		this.alternativeApkPath = alternativeApkPath;
	}

	public int getUpdateVersionCode() {
		return updateVersionCode;
	}

	public void setUpdateVersionCode(int updateVersionCode) {
		this.updateVersionCode = updateVersionCode;
	}

	public String getMainObbPath() {
		return mainObbPath;
	}

	public void setMainObbPath(String mainObbPath) {
		this.mainObbPath = mainObbPath;
	}

	public String getMainObbMd5() {
		return mainObbMd5;
	}

	public void setMainObbMd5(String mainObbMd5) {
		this.mainObbMd5 = mainObbMd5;
	}

	public String getPatchObbPath() {
		return patchObbPath;
	}

	public void setPatchObbPath(String patchObbPath) {
		this.patchObbPath = patchObbPath;
	}

	public String getPatchObbMd5() {
		return patchObbMd5;
	}

	public void setPatchObbMd5(String patchObbMd5) {
		this.patchObbMd5 = patchObbMd5;
	}

	public boolean isExcluded() {
		return excluded;
	}

	public void setExcluded(boolean excluded) {
		this.excluded = excluded;
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
}
