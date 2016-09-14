/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 02/09/2016.
 */

package cm.aptoide.pt.downloadmanager;

import android.content.Context;
import android.content.Intent;
import cm.aptoide.pt.actions.PermissionManager;
import cm.aptoide.pt.actions.PermissionRequest;
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
public class DownloadServiceHelper {

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
   * @param appId appId of the download to stop
   */
  public void pauseDownload(long appId) {
    startDownloadService(appId, AptoideDownloadManager.DOWNLOADMANAGER_ACTION_PAUSE);
  }

  /**
   * Starts a download. If there is a download running it is added to queue
   *
   * @param download Download to provide info to be able to make the download
   * @return An observable that reports the download state
   */
  public Observable<Download> startDownload(DownloadAccessor downloadAccessor,
      PermissionRequest permissionRequest, Download download) {
    return permissionManager.requestExternalStoragePermission(permissionRequest)
        .flatMap(success -> permissionManager.requestDownloadAccess(permissionRequest))
        .flatMap(success -> Observable.fromCallable(() -> {
          getDownload(download.getAppId()).first().subscribe(storedDownload -> {
            startDownloadService(download.getAppId(),
                AptoideDownloadManager.DOWNLOADMANAGER_ACTION_START_DOWNLOAD);
          }, throwable -> {
            if (throwable instanceof DownloadNotFoundException) {
              downloadAccessor.save(download);
              startDownloadService(download.getAppId(),
                  AptoideDownloadManager.DOWNLOADMANAGER_ACTION_START_DOWNLOAD);
            } else {
              throwable.printStackTrace();
            }
          });
          return download;
        }).flatMap(aDownload -> getDownload(download.getAppId())));
  }

  public Observable<Download> startDownload(PermissionRequest permissionRequest,
      Download download) {
    return startDownload(AccessorFactory.getAccessorFor(Download.class), permissionRequest,
        download);
  }

  private void startDownloadService(long appId, String action) {
    Observable.fromCallable(() -> {
      Intent intent = new Intent(Application.getContext(), DownloadService.class);
      intent.putExtra(AptoideDownloadManager.APP_ID_EXTRA, appId);
      intent.setAction(action);
      Application.getContext().startService(intent);
      return null;
    }).subscribeOn(Schedulers.computation()).subscribe(o -> {
    }, Throwable::printStackTrace);
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

  /**
   * This method finds the download with the appId
   *
   * @param appId appId to the app
   * @return an observable with the download
   */
  public Observable<Download> getDownload(long appId) {
    return aptoideDownloadManager.getDownload(appId);
  }

  public void removeDownload(long appId) {
    aptoideDownloadManager.removeDownload(appId);
  }
}
