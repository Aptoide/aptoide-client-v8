/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 25/07/2016.
 */

package cm.aptoide.pt.v8engine.install.download;

import cm.aptoide.pt.database.realm.Download;
import cm.aptoide.pt.downloadmanager.DownloadServiceHelper;
import cm.aptoide.pt.v8engine.install.Installation;
import cm.aptoide.pt.v8engine.install.InstallationException;
import cm.aptoide.pt.v8engine.install.InstallationProvider;
import lombok.AllArgsConstructor;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by marcelobenites on 7/25/16.
 */
@AllArgsConstructor
public class DownloadInstallationProvider implements InstallationProvider {

	private final DownloadServiceHelper downloadManager;

	@Override
	public Observable<Installation> getInstallation(long id) {
		return downloadManager.getDownloadAsync(id).subscribeOn(AndroidSchedulers.mainThread()).first().flatMap(download -> {
			if (download.getOverallDownloadStatus() == Download.COMPLETED) {
				return Observable.just(new DownloadInstallation(download));
			}
			return Observable.error(new InstallationException("Installation file not available."));
		});
	}
}
