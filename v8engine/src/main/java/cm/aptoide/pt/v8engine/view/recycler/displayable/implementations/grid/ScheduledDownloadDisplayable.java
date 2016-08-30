/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 28/07/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid;

import android.content.Context;

import cm.aptoide.pt.actions.PermissionRequest;
import cm.aptoide.pt.database.Database;
import cm.aptoide.pt.database.realm.Download;
import cm.aptoide.pt.database.realm.Scheduled;
import cm.aptoide.pt.downloadmanager.DownloadServiceHelper;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.model.v7.Type;
import cm.aptoide.pt.utils.ShowMessage;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.install.InstallManager;
import cm.aptoide.pt.v8engine.util.DownloadFactory;
import cm.aptoide.pt.v8engine.view.recycler.displayable.SelectableDisplayablePojo;
import io.realm.Realm;
import lombok.Cleanup;
import rx.Observable;

/**
 * created by SithEngineer
 */
public class ScheduledDownloadDisplayable extends SelectableDisplayablePojo<Scheduled> {

	private static final String TAG = ScheduledDownloadDisplayable.class.getSimpleName();

	private InstallManager installManager;
	private Download appDownload;

	public ScheduledDownloadDisplayable() {
	}

	public ScheduledDownloadDisplayable(InstallManager installManager, Scheduled pojo) {
		this(installManager, pojo, false);
	}

	public ScheduledDownloadDisplayable(InstallManager installManager, Scheduled pojo, boolean fixedPerLineCount) {
		super(pojo, fixedPerLineCount);
		this.installManager = installManager;
		this.appDownload = new DownloadFactory().create(pojo);
	}

	@Override
	public Type getType() {
		return Type.SCHEDULED_DOWNLOAD;
	}

	@Override
	public int getViewLayout() {
		return R.layout.displayable_scheduled_download_row;
	}

	public Observable<Void> install(Context context, PermissionRequest permissionRequest, long appId) {
		return installManager.install(context, permissionRequest, appId);
	}

	public void downloadAndInstall(Context context, PermissionRequest permissionRequest, DownloadServiceHelper downloadServiceHelper) {
		// download and install app

		downloadServiceHelper.startDownload(permissionRequest, appDownload).subscribe(download -> {
			if (download.getOverallDownloadStatus() == Download.COMPLETED) {
				install(context, (PermissionRequest) context, appDownload.getAppId()).subscribe(aVoid -> {
					// restore updates and remove them from scheduled downloads
					@Cleanup
					Realm realm = Database.get();
					Database.ScheduledQ.delete(realm, getPojo());
				}, err -> {
					Logger.e(TAG, err);
				});
			}
		});

		/*
		permissionRequest.requestAccessToExternalFileSystem(() -> {

			ShowMessage.asSnack(v, R.string.installing_msg);

			DownloadFactory factory = new DownloadFactory();
			Download appDownload = factory.create(getPojo());

			downloadServiceHelper.startDownload(permissionRequest, appDownload).subscribe(download -> manageDownload(download), err -> {
				Logger.e(TAG, err);
			});
		}, () -> {
			ShowMessage.asSnack(v, R.string.needs_permission_to_fs);
		});
		*/
	}

	private void manageDownload(Download download) {
		if(download.getOverallDownloadStatus() == Download.COMPLETED) {
			
		}
	}
}
