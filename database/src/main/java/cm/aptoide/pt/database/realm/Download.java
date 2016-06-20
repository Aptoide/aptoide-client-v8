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
	@PrimaryKey private long appId;

	public long getAppId() {
		return appId;
	}

	public void setAppId(long appId) {
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

	@Override
	public String toString() {
		StringBuilder toReturn = new StringBuilder();
		toReturn.append("appid = ").append(appId);
		if (downloadId != null) {
			toReturn.append("\nDownloadIds: ");
			for (int i = 0; i < downloadId.size(); i++) {
				toReturn.append(" Download n").append(i).append(": ").append(downloadId.get(i).getInteger());
			}
		}
		if (filePaths != null) {
			toReturn.append("\nFile Paths: ");
			for (int i = 0; i < filePaths.size(); i++) {
				toReturn.append(" Download n").append(i).append(": ").append(filePaths.get(i).getString());
			}
		}
		return toReturn.toString();
	}
}
