/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 02/09/2016.
 */

package cm.aptoide.pt.downloadmanager;

import cm.aptoide.pt.database.accessors.DownloadAccessor;
import cm.aptoide.pt.database.realm.Download;
import cm.aptoide.pt.database.realm.FileToDownload;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.utils.FileUtils;
import io.realm.Sort;
import java.io.File;

/**
 * Created by trinkes on 7/7/16.
 */
public class CacheHelper {

  public static String TAG = CacheHelper.class.getSimpleName();

  public void cleanCache(DownloadAccessor downloadAccessor, String cacheDirPath,
      long maxCacheSize) {
    downloadAccessor.getAllSorted(Sort.DESCENDING).first().map(downloads -> {
      int i = 0;
      while (i < downloads.size() - 1 && FileUtils.dirSize(new File(cacheDirPath)) > maxCacheSize) {

        Download download = downloads.get(i);
        for (final FileToDownload fileToDownload : download.getFilesToDownload()) {
          FileUtils.removeFile(fileToDownload.getFilePath());
        }
        downloadAccessor.delete(download.getAppId());
        i++;
      }
      return i;
    }).subscribe(numberDeletedFiles -> {
      if (numberDeletedFiles > 0) {
        Logger.d(TAG, "Cache cleaned");
      }
    }, throwable -> throwable.printStackTrace());
  }
}
