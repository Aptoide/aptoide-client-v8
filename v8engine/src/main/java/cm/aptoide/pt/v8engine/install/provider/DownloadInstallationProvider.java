/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 25/07/2016.
 */

package cm.aptoide.pt.v8engine.install.provider;

import cm.aptoide.pt.database.realm.Download;
import cm.aptoide.pt.downloadmanager.DownloadServiceHelper;
import cm.aptoide.pt.v8engine.install.InstallationProvider;
import cm.aptoide.pt.v8engine.install.RollbackInstallation;
import cm.aptoide.pt.v8engine.install.exception.InstallationException;
import lombok.AllArgsConstructor;
import rx.Observable;

/**
 * Created by marcelobenites on 7/25/16.
 */
@AllArgsConstructor
public class DownloadInstallationProvider implements InstallationProvider {

	private final DownloadServiceHelper downloadManager;

	@Override public Observable<RollbackInstallation> getInstallation(long id) {
		return downloadManager.getDownload(id).first().flatMap(download -> {
			if (download.getOverallDownloadStatus() == Download.COMPLETED) {
				return Observable.just(new DownloadInstallationAdapter(download));
			}
			return Observable.error(new InstallationException("Installation file not available."));
		});
	}
}
