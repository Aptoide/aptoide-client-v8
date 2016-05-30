/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 25/05/2016.
 */

package cm.aptoide.pt.database.realm;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;

import java.util.Locale;

import cm.aptoide.pt.database.Database;
import cm.aptoide.pt.preferences.Application;
import cm.aptoide.pt.utils.AlgorithmUtils;
import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;
import lombok.Cleanup;

/**
 * Created by sithengineer on 12/05/16.
 */
public class Installed extends RealmObject {

	public static final String ID = "id";
	public static final String ICON = "icon";
	public static final String PACKAGE_NAME = "packageName";
	public static final String NAME = "name";
	public static final String VERSION_CODE = "versionCode";
	public static final String VERSION_NAME = "versionName";
	public static final String SIGNATURE = "signature";

	@PrimaryKey private int id = -1;
	private String icon;
	@Index private String packageName;
	private String name;
	private int versionCode;
	private String versionName;
	private String signature;

	public Installed() {
	}

	public Installed(PackageInfo packageInfo, @NonNull PackageManager packageManager) {
		setIcon("android.resource://" + packageInfo.packageName + "/" + packageInfo.applicationInfo.icon);
		setName(packageInfo.applicationInfo.loadLabel(packageManager).toString());
		setPackageName(packageInfo.packageName);
		setSignature(AlgorithmUtils.computeSHA1sumFromBytes(packageInfo.signatures[0].toByteArray())
				.toUpperCase(Locale.ENGLISH));
		setVersionCode(packageInfo.versionCode);
		setVersionName(packageInfo.versionName);
	}

	public void update(PackageInfo packageInfo, Realm realm) {
		realm.beginTransaction();
		setIcon("android.resource://" + packageInfo.packageName + "/" + packageInfo.applicationInfo.icon);
		setVersionCode(packageInfo.versionCode);
		setVersionName(packageInfo.versionName);
		realm.commitTransaction();
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getVersionCode() {
		return versionCode;
	}

	public void setVersionCode(int versionCode) {
		this.versionCode = versionCode;
	}

	public String getVersionName() {
		return versionName;
	}

	public void setVersionName(String versionName) {
		this.versionName = versionName;
	}

	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}

	public void computeId() {
		@Cleanup Realm realm = Database.get(Application.getContext());
		int n;
		Number max = realm.where(Installed.class).max(Installed.ID);
		if (max != null) {
			n = max.intValue() + 1;
		} else {
			n = 0;
		}
		id = n;
	}
}
