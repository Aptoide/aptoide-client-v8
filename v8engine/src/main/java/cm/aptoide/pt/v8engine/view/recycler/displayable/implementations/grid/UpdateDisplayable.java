/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 08/07/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid;

import android.app.DownloadManager;
import android.content.Context;

import cm.aptoide.pt.actions.PermissionRequest;
import cm.aptoide.pt.database.realm.Download;
import cm.aptoide.pt.database.realm.Update;
import cm.aptoide.pt.downloadmanager.DownloadServiceHelper;
import cm.aptoide.pt.model.v7.Type;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.install.InstallManager;
import cm.aptoide.pt.v8engine.install.Installation;
import cm.aptoide.pt.v8engine.util.DownloadFactory;
import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import rx.Observable;

/**
 * Created by neuro on 17-05-2016.
 */
@AllArgsConstructor
public class UpdateDisplayable extends Displayable {

	@Getter private String packageName;
	@Getter private long appId;
	@Getter private String label;
	@Getter private String icon;
	@Getter private String md5;
	@Getter private String apkPath;
	@Getter private String alternativeApkPath;
	@Getter private String updateVersionName;

	// Obb
	@Getter private String mainObbPath;
	@Getter private String mainObbMd5;
	@Getter private String patchObbPath;
	@Getter private String patchObbMd5;

	private int versionCode;
	private InstallManager installManager;
	private Download download;
	private DownloadServiceHelper downloadManager;

	public UpdateDisplayable() {
	}

	public static UpdateDisplayable create(Update update, InstallManager installManager, DownloadFactory downloadFactory, DownloadServiceHelper downloadManager) {
		return new UpdateDisplayable(update.getPackageName(),update.getAppId(), update.getLabel(), update.getIcon(), update.getMd5(), update.getApkPath(),
				update.getAlternativeApkPath(), update.getUpdateVersionName(), update.getMainObbPath(), update.getMainObbMd5(), update.getPatchObbPath(),
				update.getPatchObbMd5(), update.getVersionCode(), installManager, downloadFactory.create(update), downloadManager);
	}

	public Observable<Void> downloadAndInstall(Context context) {
		return downloadManager.startDownload(context, download).ignoreElements().cast(Void.class)
				.concatWith(installManager.install(context, (PermissionRequest) context, download.getAppId()));
	}

	@Override
	public Type getType() {
		return Type.UPDATE;
	}

	@Override
	public int getViewLayout() {
		return R.layout.update_row;
	}
}
