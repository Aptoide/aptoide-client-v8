/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 24/05/2016.
 */

package cm.aptoide.pt.database.realm;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by sithengineer on 17/05/16.
 */
public class Download extends RealmObject {

	RealmList<RealmInteger> downloadId;
	RealmList<RealmString> filePaths;
	@PrimaryKey private int appId;

	public int getAppId() {
		return appId;
	}

	public void setAppId(int appId) {
		this.appId = appId;
	}

	public RealmList<RealmString> getFilePaths() {
		return filePaths;
	}

	public void setFilePaths(RealmList<RealmString> filePaths) {
		this.filePaths = filePaths;
	}

	public RealmList<RealmInteger> getDownloadId() {
		return downloadId;
	}

	public void setDownloadId(RealmList<RealmInteger> downloadId) {
		this.downloadId = downloadId;
	}
}
