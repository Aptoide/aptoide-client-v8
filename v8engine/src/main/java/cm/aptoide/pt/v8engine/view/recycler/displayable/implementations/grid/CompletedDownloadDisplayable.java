/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 27/07/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid;

import android.content.Context;
import cm.aptoide.pt.actions.PermissionRequest;
import cm.aptoide.pt.database.realm.Download;
import cm.aptoide.pt.downloadmanager.DownloadServiceHelper;
import cm.aptoide.pt.model.v7.Type;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.install.Installer;
import cm.aptoide.pt.v8engine.view.recycler.displayable.DisplayablePojo;
import rx.Observable;

/**
 * Created by trinkes on 7/15/16.
 */
public class CompletedDownloadDisplayable extends DisplayablePojo<Download> {

	private Installer installManager;
	private DownloadServiceHelper downloadManager;

	public CompletedDownloadDisplayable() {
		super();
	}

	public CompletedDownloadDisplayable(Download pojo, Installer installManager,
			DownloadServiceHelper downloadManager) {
		super(pojo);
		this.installManager = installManager;
		this.downloadManager = downloadManager;
	}

	public CompletedDownloadDisplayable(Download pojo, boolean fixedPerLineCount) {
		super(pojo, fixedPerLineCount);
	}

	@Override
	public Type getType() {
		return Type.COMPLETED_DOWNLOAD;
	}

	@Override
	public int getViewLayout() {
		return R.layout.completed_donwload_row_layout;
	}

	public void removeDownload(Download download) {
		downloadManager.removeDownload(download.getAppId());
	}

	public Observable<Integer> downloadStatus(Download download) {
		return downloadManager.getDownload(download.getAppId())
				.map(storedDownload -> storedDownload.getOverallDownloadStatus())
				.onErrorReturn(throwable -> Download.NOT_DOWNLOADED);
	}

	public Observable<Download> resumeDownload(PermissionRequest permissionRequest, Download download) {
		return downloadManager.startDownload(permissionRequest, download);
	}

	public Observable<Void> installOrOpenDownload(Context context, Download download) {
		return installManager.isInstalled(download.getAppId()).flatMap(installed -> {
			if (installed) {
				AptoideUtils.SystemU.openApp(download.getFilesToDownload().get(0).getPackageName());
				return Observable.empty();
			}
			return installManager.install(context, (PermissionRequest) context, download.getAppId());
		});
	}
}
