/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 30/09/2016.
 */

package cm.aptoide.pt.v8engine.install;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import cm.aptoide.pt.database.accessors.DownloadAccessor;
import cm.aptoide.pt.database.exceptions.DownloadNotFoundException;
import cm.aptoide.pt.database.realm.Download;
import cm.aptoide.pt.downloadmanager.AptoideDownloadManager;
import cm.aptoide.pt.utils.BroadcastRegisterOnSubscribe;
import cm.aptoide.pt.v8engine.install.installer.InstallService;
import java.util.List;
import rx.Observable;

/**
 * Created by marcelobenites on 9/29/16.
 */

public class BackgroundInstaller {

  private final AptoideDownloadManager aptoideDownloadManager;
  private final Installer installer;
  private DownloadAccessor downloadAccessor;

  public BackgroundInstaller(AptoideDownloadManager aptoideDownloadManager, Installer installer,
      DownloadAccessor downloadAccessor) {
    this.aptoideDownloadManager = aptoideDownloadManager;
    this.installer = installer;
    this.downloadAccessor = downloadAccessor;
  }

  public void pauseAll() {
    aptoideDownloadManager.pauseAllDownloads();
  }

  public void pause(int installationId) {
    aptoideDownloadManager.pauseDownload(installationId);
  }

  public Observable<Progress<Download>> getCurrent() {
    return aptoideDownloadManager.getCurrentDownload().map(download -> convertToProgress(download));
  }

  public Observable<List<Progress<Download>>> getAll() {
    return aptoideDownloadManager.getDownloads()
        .flatMap(downloads -> Observable.just(downloads)
            .flatMapIterable(downloadList -> downloadList)
            .map(download -> convertToProgress(download))
            .toList());
  }

  public Observable<Progress<Download>> get(long installationId) {
    return aptoideDownloadManager.getDownload(installationId)
        .map(download -> convertToProgress(download));
  }

  public void remove(long installationId) {
    aptoideDownloadManager.removeDownload(installationId);
  }

  public Observable<Boolean> isInstalled(long installationId) {
    return installer.isInstalled(installationId);
  }

  public Observable<Void> uninstall(Context context, String packageName) {
    return installer.uninstall(context, packageName);
  }

  public Observable<Progress<Download>> install(Context context, Download download) {
    return get(download.getAppId()).first()
        .flatMap(installation -> installInBackground(context, installation.getRequest()))
        .onErrorResumeNext(
            throwable -> createInstallationAndInstallInBackground(context, download, throwable))
        .flatMap(progress -> awaitInstallation(context, progress));
  }

  private Observable<Progress<Download>> awaitInstallation(Context context,
      Progress<Download> progress) {
    if (progress.getRequest().getOverallDownloadStatus() == Download.COMPLETED) {
      progress.setIndeterminate(true);
      return awaitInstallationIntent(context, progress.getRequest().getAppId()).doOnNext(
          installed -> progress.setDone(true)).map(success -> progress).startWith(progress);
    }
    return Observable.just(progress);
  }

  private Progress<Download> convertToProgress(Download currentDownload) {
    return new Progress<>(currentDownload, false, 100, currentDownload.getOverallProgress(),
        currentDownload.getDownloadSpeed(), false);
  }

  private Observable<Progress<Download>> createInstallationAndInstallInBackground(Context context,
      Download download, Throwable throwable) {
    if (throwable instanceof DownloadNotFoundException) {
      downloadAccessor.save(download);
      return installInBackground(context, download);
    } else {
      return Observable.error(throwable);
    }
  }

  private Observable<Void> awaitInstallationIntent(Context context, long installationId) {
    return Observable.create(
        new BroadcastRegisterOnSubscribe(context, new IntentFilter(InstallService.ACTION_INSTALL_FINISHED), null, null))
        .filter(intent -> intent != null && InstallService.ACTION_INSTALL_FINISHED.equals(intent.getAction()))
        .first(intent -> intent.getLongExtra(InstallService.EXTRA_INSTALLATION_ID, -1) == installationId).<Void>map(intent -> null);
  }

  private Observable<Progress<Download>> installInBackground(Context context, Download download) {
    startBackgroundInstallation(context, download);
    return aptoideDownloadManager.getDownload(download.getAppId())
        .map(currentDownload -> convertToProgress(currentDownload));
  }

  private void startBackgroundInstallation(Context context, Download download) {
    Intent intent = new Intent(context, InstallService.class);
    intent.setAction(InstallService.ACTION_START_INSTALL);
    intent.putExtra(InstallService.EXTRA_INSTALLATION_ID, download.getAppId());
    context.startService(intent);
  }
}
