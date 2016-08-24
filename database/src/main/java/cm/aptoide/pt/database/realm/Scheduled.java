/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 28/07/2016.
 */

package cm.aptoide.pt.database.realm;

import cm.aptoide.pt.model.v7.GetAppMeta;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import lombok.AllArgsConstructor;

/**
 * Created by sithengineer on 12/05/16.
 */
@AllArgsConstructor
public class Scheduled extends RealmObject {

	public static final String APP_ID = "appId";
	public static final String NAME = "name";
	public static final String VERSION_NAME = "versionName";
	public static final String ICON = "icon";
	public static final String PATH = "path";
	public static final String MD5 = "md5";
	public static final String VER_CODE = "verCode";


	@PrimaryKey private long appId;
	private String name;
	private String versionName;
	private String icon;
	private String path;
	private String md5;
	private int verCode;

	public Scheduled() { }

	public static Scheduled from(GetAppMeta.App app) {
		return new Scheduled(app.getId(), app.getName(), app.getFile().getVername(), app.getIcon(), app.getFile().getPath(), app.getFile()
				.getMd5sum(), app.getFile().getVercode());
	}

	public long getAppId() {
		return appId;
	}

	public void setAppId(long appId) {
		this.appId = appId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getMd5() {
		return md5;
	}

	public void setMd5(String md5) {
		this.md5 = md5;
	}

	public int getVerCode() {
		return verCode;
	}

	public void setVerCode(int verCode) {
		this.verCode = verCode;
	}
}
