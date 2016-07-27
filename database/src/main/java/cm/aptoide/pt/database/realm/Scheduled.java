/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 19/07/2016.
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


	@PrimaryKey private long appId;
	private String name;
	private String versionName;
	private String icon;

	public Scheduled() { }

	public static Scheduled from(GetAppMeta.App app) {
		return new Scheduled(app.getId(), app.getName(), app.getFile().getVername(), app.getIcon());
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
}
