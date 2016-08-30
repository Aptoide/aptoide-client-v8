/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 25/07/2016.
 */

package cm.aptoide.pt.v8engine.install.provider;

import java.io.File;

import cm.aptoide.pt.database.realm.Download;
import cm.aptoide.pt.v8engine.install.Installation;

/**
 * Created by marcelobenites on 7/22/16.
 */
public class DownloadInstallationAdapter implements Installation {

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

}