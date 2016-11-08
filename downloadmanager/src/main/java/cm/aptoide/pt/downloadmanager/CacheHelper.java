/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 02/09/2016.
 */

package cm.aptoide.pt.downloadmanager;

import android.text.format.DateUtils;
import cm.aptoide.pt.database.accessors.DownloadAccessor;
import cm.aptoide.pt.database.realm.Download;
import cm.aptoide.pt.database.realm.FileToDownload;
import cm.aptoide.pt.downloadmanager.interfaces.CacheManager;
import cm.aptoide.pt.downloadmanager.interfaces.DownloadSettingsInterface;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.utils.FileUtils;
import io.realm.Sort;
import java.io.File;
import lombok.AllArgsConstructor;

/**
 * Created by trinkes on 7/7/16.
 */
@AllArgsConstructor public class CacheHelper implements CacheManager {

  private static final int VALUE_TO_CONVERT_MB_TO_BYTES = 1024 * 1024;
  public static String TAG = CacheHelper.class.getSimpleName();
  private DownloadAccessor downloadAccessor;
  private DownloadSettingsInterface dirSettings;

  public void cleanCache() {
    long maxCacheSize = dirSettings.getMaxCacheSize() * VALUE_TO_CONVERT_MB_TO_BYTES;
    String cacheDirPath = dirSettings.getDownloadDir();
    long now = System.currentTimeMillis();

    downloadAccessor.getAllSorted(Sort.ASCENDING).first().map(downloads -> {
      int i = 0;
      while (i < downloads.size() - 1
          && FileUtils.dirSize(new File(cacheDirPath)) > maxCacheSize
          && (now - downloads.get(i).getTimeStamp()) > DateUtils.HOUR_IN_MILLIS) {

        Download download = downloads.get(i);
        for (final FileToDownload fileToDownload : download.getFilesToDownload()) {
          if (!(fileToDownload.getFileType() == FileToDownload.OBB
              && download.getOverallDownloadStatus() == Download.COMPLETED)) {
            FileUtils.removeFile(fileToDownload.getFilePath());
          }
        }
        downloadAccessor.delete(download.getMd5());
        i++;
      }
      return i;
    }).subscribe(numberDeletedFiles -> {
      if (numberDeletedFiles > 0) {
        Logger.d(TAG, "Cache cleaned: " + numberDeletedFiles);
      } else {
        Logger.d(TAG, "Cache not cleaned");
      }
    }, throwable -> throwable.printStackTrace());
  }
}
