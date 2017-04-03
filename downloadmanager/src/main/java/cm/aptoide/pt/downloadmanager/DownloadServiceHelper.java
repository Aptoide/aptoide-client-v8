/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 02/09/2016.
 */

package cm.aptoide.pt.downloadmanager;

import android.content.Context;
import android.content.Intent;
import cm.aptoide.pt.actions.PermissionManager;
import cm.aptoide.pt.actions.PermissionService;
import cm.aptoide.pt.v8engine.crashreports.CrashReport;
import cm.aptoide.pt.database.accessors.AccessorFactory;
import cm.aptoide.pt.database.accessors.DownloadAccessor;
import cm.aptoide.pt.database.exceptions.DownloadNotFoundException;
import cm.aptoide.pt.database.realm.Download;
import cm.aptoide.pt.preferences.Application;
import java.util.List;
import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * Created by trinkes on 7/4/16.
 */
@Deprecated public class DownloadServiceHelper {

  private final AptoideDownloadManager aptoideDownloadManager;
  private PermissionManager permissionManager;

  public DownloadServiceHelper(AptoideDownloadManager aptoideDownloadManager,
      PermissionManager permissionManager) {
    this.aptoideDownloadManager = aptoideDownloadManager;
    this.permissionManager = permissionManager;
  }

  /**
   * Pause all the running downloads
   */
  public void pauseAllDownloads() {
    Context context = Application.getContext();
    Intent intent = new Intent(context, DownloadService.class);
    intent.setAction(AptoideDownloadManager.DOWNLOADMANAGER_ACTION_PAUSE);
    context.startService(intent);
  }

  /**
   * Pause a download
   *
   * @param md5 md5 sum of the download to stop
   */
  public void pauseDownload(String md5) {
    startDownloadService(md5, AptoideDownloadManager.DOWNLOADMANAGER_ACTION_PAUSE);
  }

  private void startDownloadService(String md5, String action) {
    Observable.fromCallable(() -> {
      Intent intent = new Intent(Application.getContext(), DownloadService.class);
      intent.putExtra(AptoideDownloadManager.FILE_MD5_EXTRA, md5);
      intent.setAction(action);
      Application.getContext().startService(intent);
      return null;
    }).subscribeOn(Schedulers.computation()).subscribe(o -> {
    }, e -> {
      CrashReport.getInstance().log(e);
    });
  }

  public Observable<Download> startDownload(PermissionService permissionRequest,
      Download download) {
    return startDownload(AccessorFactory.getAccessorFor(Download.class), permissionRequest,
        download).doOnError(e -> {
      CrashReport.getInstance().log(e);
    });
  }

  /**
   * Starts a download. If there is a download running it is added to queue
   *
   * @param download Download to provide info to be able to make the download
   *
   * @return An observable that reports the download state
   */
  public Observable<Download> startDownload(DownloadAccessor downloadAccessor,
      PermissionService permissionRequest, Download download) {
    return permissionManager.requestExternalStoragePermission(permissionRequest)
        .flatMap(success -> permissionManager.requestDownloadAccess(permissionRequest))
        .flatMap(success -> Observable.fromCallable(() -> {
          getDownload(download.getMd5()).first().subscribe(storedDownload -> {
            startDownloadService(download.getMd5(),
                AptoideDownloadManager.DOWNLOADMANAGER_ACTION_START_DOWNLOAD);
          }, e -> {
            if (e instanceof DownloadNotFoundException) {
              downloadAccessor.save(download);
              startDownloadService(download.getMd5(),
                  AptoideDownloadManager.DOWNLOADMANAGER_ACTION_START_DOWNLOAD);
            } else {
              e.printStackTrace();
              CrashReport.getInstance().log(e);
            }
          });
          return download;
        }).flatMap(aDownload -> getDownload(download.getMd5())));
  }

  /**
   * This method finds the download with the appId
   *
   * @param md5 md5 sum of the app file
   *
   * @return an observable with the download
   */
  public Observable<Download> getDownload(String md5) {
    return aptoideDownloadManager.getDownload(md5);
  }

  /**
   * Finds the download that is currently running
   *
   * @return an observable that reports the current download state
   */
  public Observable<Download> getCurrentDownlaod() {
    return aptoideDownloadManager.getCurrentDownload();
  }

  /**
   * Gets all the recorded downloads
   *
   * @return an observable with all downloads in database
   */
  public Observable<List<Download>> getAllDownloads() {
    return aptoideDownloadManager.getDownloads();
  }

  public void removeDownload(String md5) {
    aptoideDownloadManager.removeDownload(md5);
  }
}
