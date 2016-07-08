/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 08/07/2016.
 */

package cm.aptoide.pt.v8engine.util;

import android.support.annotation.Nullable;
import android.text.TextUtils;

import cm.aptoide.pt.database.realm.Download;
import cm.aptoide.pt.database.realm.FileToDownload;
import cm.aptoide.pt.database.realm.Update;
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

	public Download create(Update update) {
		validateApp(update.getAppId(), null, update.getPackageName(), update.getLabel(), update.getApkPath(), update.getAlternativeApkPath());
		Download download = new Download();
		download.setAppId(update.getAppId());
		download.setAppName(update.getLabel());
		download.setFilesToDownload(createFileList(update.getAppId(), update.getPackageName(), update.getApkPath(), update.getAlternativeApkPath(), update
				.getMd5(), update
				.getMainObbPath(), update.getMainObbMd5(), update.getPatchObbPath(), update.getPatchObbMd5()));
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
		return createFileList(appId, packageName, filePath, altPathToApk, fileMd5, appObb.getMain().getPath(), appObb.getMain().getMd5sum(), appObb.getPatch()
				.getPath(), appObb.getPatch().getMd5sum());
	}

	private RealmList<FileToDownload> createFileList(long appId, String packageName, String filePath, @Nullable String altPathToApk, String fileMd5, String
			mainObbPath, String mainObbMd5, String patchObbPath, String patchObbMd5) {

		final RealmList<FileToDownload> downloads = new RealmList<>();

		downloads.add(FileToDownload.createFileToDownload(filePath, altPathToApk, appId, fileMd5, null, FileToDownload.APK));

		if (mainObbPath != null) {
			downloads.add(FileToDownload.createFileToDownload(mainObbPath, null, appId, mainObbMd5, null, FileToDownload.OBB, packageName));
		}

		if (patchObbPath != null) {
			downloads.add(FileToDownload.createFileToDownload(patchObbPath, null, appId, patchObbMd5, null, FileToDownload.OBB, packageName));
		}

		return downloads;
	}
}