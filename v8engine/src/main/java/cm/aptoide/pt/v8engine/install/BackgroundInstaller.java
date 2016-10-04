/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 30/09/2016.
 */

package cm.aptoide.pt.v8engine.install;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.NonNull;
import android.util.Log;
import cm.aptoide.pt.database.accessors.DownloadAccessor;
import cm.aptoide.pt.database.exceptions.DownloadNotFoundException;
import cm.aptoide.pt.database.realm.Download;
import cm.aptoide.pt.database.realm.Scheduled;
import cm.aptoide.pt.downloadmanager.AptoideDownloadManager;
import cm.aptoide.pt.utils.BroadcastRegisterOnSubscribe;
import cm.aptoide.pt.v8engine.install.installer.InstallService;
import rx.Observable;
import rx.schedulers.Schedulers;

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

  public void stopInstallation(Context context, long installationId) {
    Intent intent = new Intent(context, InstallService.class);
    intent.setAction(InstallService.ACTION_STOP_INSTALL);
    intent.putExtra(InstallService.EXTRA_INSTALLATION_ID, installationId);
    context.startService(intent);
  }

  private void stopAllInstallations(Context context) {
    Intent intent = new Intent(context, InstallService.class);
    intent.setAction(InstallService.ACTION_STOP_ALL_INSTALLS);
    context.startService(intent);
  }

  public void removeInstallationFile(long installationId) {
    aptoideDownloadManager.removeDownload(installationId);
  }

  public Observable<Void> uninstall(Context context, String packageName) {
    return installer.uninstall(context, packageName);
  }

  public Observable<Progress<Download>> getInstallations() {
    return aptoideDownloadManager.getDownloads()
        .observeOn(Schedulers.io())
        .flatMapIterable(downloadList -> downloadList)
        .map(download -> convertToProgress(download));
  }

  public Observable<Progress<Download>> getCurrentInstallation() {
    return getInstallations().filter(progress -> isInstalling(progress));
  }

  public Observable<Progress<Download>> getInstallation(long installationId) {
    return aptoideDownloadManager.getDownload(installationId)
        .map(download -> convertToProgress(download));
  }

  public boolean isInstalling(Progress<Download> progress) {
    return isDownloading(progress) || !progress.isDone() && progress.getRequest().getOverallDownloadStatus() == Download.COMPLETED;
  }

  public boolean isDownloading(Progress<Download> progress) {
    return progress.getRequest().getOverallDownloadStatus() != Download.PROGRESS
        || progress.getRequest().getOverallDownloadStatus() != Download.IN_QUEUE
        || progress.getRequest().getOverallDownloadStatus() != Download.PENDING;
  }

  public Observable<Progress<Download>> install(Context context, Download download) {
    return getInstallation(download.getAppId()).first()
        .retryWhen(errors -> createDownloadAndRetry(errors, download))
        .flatMap(progress -> installInBackground(context, progress));
  }

  private Observable<Throwable> createDownloadAndRetry(Observable<? extends Throwable> errors,
      Download download) {
    return errors.flatMap(throwable -> {
      if (throwable instanceof DownloadNotFoundException) {
        downloadAccessor.save(download);
        return Observable.just(throwable);
      } else {
        return Observable.error(throwable);
      }
    });
  }

  private Progress<Download> convertToProgress(Download currentDownload) {
    if (currentDownload.getOverallDownloadStatus() != Download.ERROR) {
      return new Progress<>(currentDownload, currentDownload.getOverallDownloadStatus() == Download.COMPLETED,
          AptoideDownloadManager.PROGRESS_MAX_VALUE, currentDownload.getOverallProgress(),
          currentDownload.getDownloadSpeed(), false);
    }
    throw new IllegalStateException("Download " + currentDownload.getPackageName() + " error.");
  }

  private Observable<Progress<Download>> installInBackground(Context context, Progress<Download> progress) {
    return getInstallation(progress.getRequest().getAppId())
        .mergeWith(startBackgroundInstallationAndWait(context, progress));
  }

  @NonNull
  private Observable<Progress<Download>> startBackgroundInstallationAndWait(Context context,
      Progress<Download> progress) {
    return waitBackgroundInstallationResult(context, progress.getRequest().getAppId())
    .doOnSubscribe(() -> startBackgroundInstallation(context, progress.getRequest().getAppId()))
    .map(success -> {
      progress.setDone(true);
      return progress;
    });
  }

  private Observable<Void> waitBackgroundInstallationResult(Context context, long installationId) {
    return Observable.create(new BroadcastRegisterOnSubscribe(context,
        new IntentFilter(InstallService.ACTION_INSTALL_FINISHED), null, null))
        .filter(intent -> intent != null && InstallService.ACTION_INSTALL_FINISHED.equals(
            intent.getAction()))
        .first(intent -> intent.getLongExtra(InstallService.EXTRA_INSTALLATION_ID, -1)
            == installationId).<Void>map(intent -> null);
  }

  private void startBackgroundInstallation(Context context, long installationId) {
    Intent intent = new Intent(context, InstallService.class);
    intent.setAction(InstallService.ACTION_START_INSTALL);
    intent.putExtra(InstallService.EXTRA_INSTALLATION_ID, installationId);
    context.startService(intent);
  }
}
