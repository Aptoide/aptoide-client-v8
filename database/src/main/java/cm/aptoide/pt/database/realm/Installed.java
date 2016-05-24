/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 24/05/2016.
 */

package cm.aptoide.pt.database.realm;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by sithengineer on 12/05/16.
 */
public class Installed extends RealmObject {

	public static final String ID = "id";
	public static final String PACKAGE_NAME = "packageName";
	public static final String NAME = "name";
	public static final String VERSION_CODE = "versionCode";
	public static final String VERSION_NAME = "versionName";
	public static final String SIGNATURE = "signature";

	@PrimaryKey private int id;
	private String packageName;
	private String name;
	private String versionCode;
	private String versionName;
	private String signature;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
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

	public String getVersionCode() {
		return versionCode;
	}

	public void setVersionCode(String versionCode) {
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
}
