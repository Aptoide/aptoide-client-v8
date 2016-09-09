/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 25/07/2016.
 */

package cm.aptoide.pt.v8engine.install.provider;

import cm.aptoide.pt.database.realm.Download;
import cm.aptoide.pt.v8engine.install.RollbackInstallation;
import java.io.File;

/**
 * Created by marcelobenites on 7/22/16.
 */
public class DownloadInstallationAdapter implements RollbackInstallation {

	private final Download download;

	public DownloadInstallationAdapter(Download download) {
		this.download = download;
	}

	@Override
	public long getId() {
		return download.getAppId();
	}

	@Override
	public String getPackageName() {
		return download.getFilesToDownload().get(0).getPackageName();
	}

	@Override
	public int getVersionCode() {
		return download.getFilesToDownload().get(0).getVersionCode();
	}

	@Override
	public File getFile() {
		return new File(download.getFilesToDownload().get(0).getFilePath());
	}

	@Override public String getAppName() {
		return download.getAppName();
	}

	@Override public String getIcon() {
		return download.getIcon();
	}

	@Override public String downloadLink() {
		return download.getFilesToDownload().get(0).getLink();
	}

	@Override public String getAltDownloadLink() {
		return download.getFilesToDownload().get(0).getAltLink();
	}

	@Override public String getMainObbName() {
		if (download.getFilesToDownload().size() > 1) {
			return download.getFilesToDownload().get(1).getFileName();
		} else {
			return null;
		}
	}

	@Override public String getMainObbPath() {
		if (download.getFilesToDownload().size() > 1) {
			return download.getFilesToDownload().get(1).getLink();
		} else {
			return null;
		}
	}

	@Override public long getTimeStamp() {
		return download.getTimeStamp();
	}

	@Override public String getPatchObbName() {
		if (download.getFilesToDownload().size() > 2) {
			return download.getFilesToDownload().get(2).getFileName();
		} else {
			return null;
		}
	}

	@Override public String getPatchObbPath() {
		if (download.getFilesToDownload().size() > 2) {
			return download.getFilesToDownload().get(2).getLink();
		} else {
			return null;
		}
	}

}