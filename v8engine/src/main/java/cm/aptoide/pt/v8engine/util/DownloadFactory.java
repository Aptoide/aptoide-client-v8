/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 29/06/2016.
 */

package cm.aptoide.pt.v8engine.util;

import android.support.annotation.Nullable;
import android.text.TextUtils;

import cm.aptoide.pt.database.realm.Download;
import cm.aptoide.pt.database.realm.FileToDownload;
import cm.aptoide.pt.model.v7.GetAppMeta;
import cm.aptoide.pt.model.v7.Obb;
import cm.aptoide.pt.model.v7.listapp.App;
import cm.aptoide.pt.model.v7.listapp.File;
import io.realm.RealmList;

/**
 * Created by marcelobenites on 6/29/16.
 */
public class DownloadFactory {

	public Download create(GetAppMeta.App appToDownload) throws IllegalArgumentException {
		final GetAppMeta.GetAppMetaFile file = appToDownload.getFile();

		validateApp(appToDownload.getId(), appToDownload.getObb(), appToDownload.getPackageName(), appToDownload.getName(), file != null? file.getPath(): null,
				file != null? file.getPathAlt(): null);

		Download download = new Download();
		download.setAppId(appToDownload.getId());
		download.setAppName(appToDownload.getName());
		download.setFilesToDownload(createFileList(appToDownload.getId(), appToDownload.getPackageName(), appToDownload.getFile().getPath(), appToDownload
				.getFile()
				.getMd5sum(), appToDownload.getObb(), appToDownload.getFile().getPathAlt()));
		return download;
	}

	public Download create(App appToDownload) {
		final File file = appToDownload.getFile();
		validateApp(appToDownload.getId(), appToDownload.getObb(), appToDownload.getPackageName(), appToDownload.getName(), file != null? file.getPath(): null,
				file != null? file.getPathAlt(): null);
		Download download = new Download();
		download.setAppId(appToDownload.getId());
		download.setAppName(appToDownload.getName());
		download.setFilesToDownload(createFileList(appToDownload.getId(), appToDownload.getPackageName(), appToDownload.getFile().getPath(), appToDownload
				.getFile()
				.getMd5sum(), appToDownload.getObb(), appToDownload.getFile().getPathAlt()));
		return download;
	}

	private void validateApp(long appId, Obb appObb, String packageName, String appName, String filePath, String filePathAlt) throws IllegalArgumentException {
		if (appId <= 0) {
			throw new IllegalArgumentException("Invalid AppId");
		} if (TextUtils.isEmpty(filePath) && TextUtils.isEmpty(filePathAlt)) {
			throw new IllegalArgumentException("No download link provided");
		} else if (appObb != null && TextUtils.isEmpty(packageName)) {
			throw new IllegalArgumentException("This app has an OBB and doesn't have the package name specified");
		} else if (TextUtils.isEmpty(appName)) {
			throw new IllegalArgumentException("This app has an OBB and doesn't have the App name specified");
		}
	}

	private RealmList<FileToDownload> createFileList(long appId, String packageName, String filePath, String fileMd5, Obb appObb, @Nullable String altPathToApk) {

		final RealmList<FileToDownload> downloads = new RealmList<>();

		downloads.add(FileToDownload.createFileToDownload(filePath, altPathToApk, appId, fileMd5, null, FileToDownload.APK));

		if (appObb != null) {
			if (appObb.getMain() != null) {
				downloads.add(FileToDownload.createFileToDownload(appObb.getMain().getPath(), null, appId, appObb.getMain().getMd5sum(),
						appObb.getMain().getFilename(), FileToDownload.OBB, packageName));
			}

			if (appObb.getPatch() != null) {
				downloads.add(FileToDownload.createFileToDownload(appObb.getPatch().getPath(), null, appId, appObb.getPatch().getMd5sum(),
						appObb.getPatch().getFilename(), FileToDownload.OBB, packageName));
			}
		}

		return downloads;
	}
}