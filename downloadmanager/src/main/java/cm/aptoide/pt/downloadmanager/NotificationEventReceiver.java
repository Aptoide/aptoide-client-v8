/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 02/09/2016.
 */

package cm.aptoide.pt.downloadmanager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

/**
 * Created by trinkes on 6/23/16.
 */
@Deprecated public class NotificationEventReceiver extends BroadcastReceiver {

  private static final String TAG = NotificationEventReceiver.class.getSimpleName();

  public void onReceive(Intent intent) {

    String action = intent.getAction();
    if (action != null) {
      AptoideDownloadManager downloadManager = AptoideDownloadManager.getInstance();
      switch (action) {
        case AptoideDownloadManager.DOWNLOADMANAGER_ACTION_PAUSE:
          if (intent.hasExtra(AptoideDownloadManager.FILE_MD5_EXTRA)) {
            String md5 = intent.getStringExtra(AptoideDownloadManager.FILE_MD5_EXTRA);
            if (!TextUtils.isEmpty(md5)) {
              downloadManager.pauseDownload(md5);
            } else {
              downloadManager.pauseAllDownloads();
            }
          }
          break;
        case AptoideDownloadManager.DOWNLOADMANAGER_ACTION_OPEN:
          if (downloadManager.getDownloadNotificationActionsInterface() != null) {
            downloadManager.getDownloadNotificationActionsInterface().button1Pressed();
          }
          break;
        case AptoideDownloadManager.DOWNLOADMANAGER_ACTION_START_DOWNLOAD:
          if (intent.hasExtra(AptoideDownloadManager.FILE_MD5_EXTRA)) {
            String md5 = intent.getStringExtra(AptoideDownloadManager.FILE_MD5_EXTRA);
            if (!TextUtils.isEmpty(md5)) {
              downloadManager.getDownload(md5)
                  .subscribe(download -> downloadManager.startDownload(download),
                      throwable -> throwable.printStackTrace());
            }
          }
          break;
        case AptoideDownloadManager.DOWNLOADMANAGER_ACTION_NOTIFICATION:
          if (downloadManager.getDownloadNotificationActionsInterface() != null) {
            if (intent.hasExtra(AptoideDownloadManager.FILE_MD5_EXTRA)) {
              downloadManager.getDownloadNotificationActionsInterface()
                  .notificationPressed(
                      intent.getStringExtra(AptoideDownloadManager.FILE_MD5_EXTRA));
            }
            break;
          }
      }
    }
  }

  @Override public void onReceive(Context context, Intent intent) {
    onReceive(intent);
  }
}
