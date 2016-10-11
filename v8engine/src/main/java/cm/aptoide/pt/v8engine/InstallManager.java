/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 04/10/2016.
 */

package cm.aptoide.pt.v8engine;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.NonNull;
import cm.aptoide.pt.database.accessors.DownloadAccessor;
import cm.aptoide.pt.database.accessors.InstalledAccessor;
import cm.aptoide.pt.database.exceptions.DownloadNotFoundException;
import cm.aptoide.pt.database.realm.Download;
import cm.aptoide.pt.downloadmanager.AptoideDownloadManager;
import cm.aptoide.pt.utils.BroadcastRegisterOnSubscribe;
import cm.aptoide.pt.v8engine.install.Installer;
import java.util.List;
import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * Created by marcelobenites on 9/29/16.
 */

public class InstallManager {

  private final AptoideDownloadManager aptoideDownloadManager;
  private final Installer installer;
  private DownloadAccessor downloadAccessor;
  private InstalledAccessor installedAccessor;

  public InstallManager(AptoideDownloadManager aptoideDownloadManager, Installer installer,
      DownloadAccessor downloadAccessor, InstalledAccessor installedDatabase) {
    this.aptoideDownloadManager = aptoideDownloadManager;
    this.installer = installer;
    this.downloadAccessor = downloadAccessor;
    this.installedAccessor = installedDatabase;
  }

  public void stopInstallation(Context context, String md5) {
    Intent intent = new Intent(context, InstallService.class);
    intent.setAction(InstallService.ACTION_STOP_INSTALL);
    intent.putExtra(InstallService.EXTRA_INSTALLATION_MD5, md5);
    context.startService(intent);
  }

  public void stopAllInstallations(Context context) {
    Intent intent = new Intent(context, InstallService.class);
    intent.setAction(InstallService.ACTION_STOP_ALL_INSTALLS);
    context.startService(intent);
  }

  public void removeInstallationFile(String md5) {
    aptoideDownloadManager.removeDownload(md5);
  }

  public Observable<Void> uninstall(Context context, String packageName) {
    return installer.uninstall(context, packageName);
  }

  public Observable<Progress<Download>> getInstallations() {
    return aptoideDownloadManager.getDownloads()
        .observeOn(Schedulers.io())
        .flatMapIterable(downloadList -> downloadList)
        .flatMap(download -> convertToProgress(download));
  }

  public Observable<List<Progress<Download>>> getInstallationsAsList() {
    return aptoideDownloadManager.getDownloads()
        .observeOn(Schedulers.io())
        .flatMap(downloadList -> Observable.from(downloadList)
            .flatMap(download -> convertToProgress(download))
            .toList());
  }

  public Observable<Progress<Download>> getCurrentInstallation() {
    return getInstallations().filter(progress -> isInstalling(progress));
  }

  public Observable<Progress<Download>> getInstallation(String md5) {
    return aptoideDownloadManager.getDownload(md5).flatMap(download -> convertToProgress(download));
  }

  public boolean isInstalling(Progress<Download> progress) {
    return isDownloading(progress) || (progress.getState() != Progress.DONE
        && progress.getRequest().getOverallDownloadStatus() == Download.COMPLETED);
  }

  public boolean isDownloading(Progress<Download> progress) {
    return progress.getRequest().getOverallDownloadStatus() == Download.PROGRESS;
  }

  public boolean isPending(Progress<Download> progress) {
    return progress.getRequest().getOverallDownloadStatus() == Download.PENDING
        || progress.getRequest().getOverallDownloadStatus() == Download.IN_QUEUE;
  }

  public Observable<Progress<Download>> install(Context context, Download download) {
    return getInstallation(download.getMd5()).first()
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

  private Observable<Progress<Download>> convertToProgress(Download currentDownload) {
    return convertToProgressStatus(currentDownload).map(status -> new Progress<>(currentDownload,
        currentDownload.getOverallDownloadStatus() == Download.COMPLETED,
        AptoideDownloadManager.PROGRESS_MAX_VALUE, currentDownload.getOverallProgress(),
        currentDownload.getDownloadSpeed(), status));
  }

  private Observable<Progress<Download>> installInBackground(Context context,
      Progress<Download> progress) {
    return getInstallation(progress.getRequest().getMd5()).mergeWith(
        startBackgroundInstallationAndWait(context, progress));
  }

  @NonNull
  private Observable<Progress<Download>> startBackgroundInstallationAndWait(Context context,
      Progress<Download> progress) {
    return waitBackgroundInstallationResult(context, progress.getRequest().getMd5()).doOnSubscribe(
        () -> startBackgroundInstallation(context, progress.getRequest().getMd5())).map(success -> {
      progress.setState(Progress.DONE);
      return progress;
    });
  }

  private Observable<Void> waitBackgroundInstallationResult(Context context, String md5) {
    return Observable.create(new BroadcastRegisterOnSubscribe(context,
        new IntentFilter(InstallService.ACTION_INSTALL_FINISHED), null, null))
        .filter(intent -> intent != null && InstallService.ACTION_INSTALL_FINISHED.equals(
            intent.getAction()))
        .first(intent -> md5.equals(intent.getStringExtra(InstallService.EXTRA_INSTALLATION_MD5)))
        .map(intent -> null);
  }

  private void startBackgroundInstallation(Context context, String md5) {
    Intent intent = new Intent(context, InstallService.class);
    intent.setAction(InstallService.ACTION_START_INSTALL);
    intent.putExtra(InstallService.EXTRA_INSTALLATION_MD5, md5);
    context.startService(intent);
  }

  private Observable<Integer> convertToProgressStatus(Download download) {
    return installedAccessor.get(download.getPackageName())
        .first()
        .map(installed -> installed != null
            && installed.getVersionCode() == download.getVersionCode())
        .map(isInstalled -> {

          if (isInstalled) {
            return Progress.DONE;
          }

          final int progressStatus;
          switch (download.getOverallDownloadStatus()) {
            case Download.COMPLETED:
            case Download.PROGRESS:
            case Download.PENDING:
            case Download.IN_QUEUE:
            case Download.INVALID_STATUS:
              progressStatus = Progress.ACTIVE;
              break;
            case Download.PAUSED:
              progressStatus = Progress.INACTIVE;
              break;
            case Download.WARN:
            case Download.BLOCK_COMPLETE:
            case Download.CONNECTED:
            case Download.RETRY:
            case Download.STARTED:
            case Download.NOT_DOWNLOADED:
            case Download.ERROR:
            case Download.FILE_MISSING:
              progressStatus = Progress.ERROR;
              break;
            default:
              progressStatus = Progress.INACTIVE;
              break;
          }
          return progressStatus;
        });
  }
}
