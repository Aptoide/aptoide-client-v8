package cm.aptoide.pt.v8engine.deprecated.tables;

import cm.aptoide.pt.database.accessors.DownloadAccessor;
import cm.aptoide.pt.database.realm.Download;
import cm.aptoide.pt.database.realm.FileToDownload;
import cm.aptoide.pt.utils.AptoideUtils;
import io.realm.RealmList;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;

/**
 * Created by sithengineer on 29/09/16.
 */

public class Downloads {

  private static final String PATH_TO_OLD_DOWNLOADS = "/.aptoide/apks";
  private static final String PATH_TO_NEW_DOWNLOADS = "/.aptoide/apks";
  private static final long MAX_SIZE_CACHE = 200 * 1024 * 1024; // 200 MB

  /**
   * Migrates 200MB (default cache size) of the most recent download files
   * and stores download files info in the download table.
   *
   * @param downloadAccessor {@link DownloadAccessor} database accessor instance
   */
  public void migrate(DownloadAccessor downloadAccessor) throws FileNotFoundException {

    File oldPathToDownloads = new File(PATH_TO_OLD_DOWNLOADS);
    if (!oldPathToDownloads.isDirectory()) {
      throw new FileNotFoundException("Path to old downloads is invalid");
    }

    File newPathToDownloads = new File(PATH_TO_NEW_DOWNLOADS);
    if (!newPathToDownloads.isDirectory()) {
      throw new FileNotFoundException("Path to new downloads is invalid");
    }

    File[] files = oldPathToDownloads.listFiles();
    // sort files having most recent first
    // does some un-necessary auto-boxing to compare 2 longs
    Arrays.sort(files, (f1, f2) -> Long.valueOf(f1.lastModified())
        .compareTo(f2.lastModified()));
    long cacheSum = 0;
    for (File downloadFile : files) {
      if (downloadFile.exists() && downloadFile.isFile()) {
        // move file and create a Download table entry for this file
        long fileSize = downloadFile.length();

        // only migrate APKs
        if ((MAX_SIZE_CACHE > (cacheSum + fileSize)) && downloadFile.getName()
            .endsWith(".apk") && downloadFile.renameTo(
            new File(newPathToDownloads, downloadFile.getName()))) {
          cacheSum += fileSize;
          saveDbEntry(downloadFile, downloadAccessor);
        } else {
          // cache has filled, delete file
          if (!downloadFile.delete()) {
            downloadFile.deleteOnExit();
          }
        }
      }
    }
  }

  private void saveDbEntry(File downloadFile, DownloadAccessor downloadAccessor) {

    String downloadFileMd5 = AptoideUtils.AlgorithmU.computeMd5(downloadFile);

    FileToDownload fileToDownload = new FileToDownload();

    fileToDownload.setFileName(downloadFile.getName());
    fileToDownload.setFileType(FileToDownload.APK);
    fileToDownload.setPath(downloadFile.getPath());
    fileToDownload.setMd5(downloadFileMd5);

    Download downloadEntry = new Download();

    downloadEntry.setMd5(downloadFileMd5);
    downloadEntry.setAppName(downloadFile.getName());
    downloadEntry.setTimeStamp(downloadFile.lastModified());
    downloadEntry.setFilesToDownload(new RealmList<>(fileToDownload));
    downloadEntry.setDownloadSpeed(0);
    downloadEntry.setIcon(null);
    downloadEntry.setOverallDownloadStatus(Download.COMPLETED);
    downloadEntry.setOverallProgress(100);

    downloadAccessor.insert(downloadEntry);
  }
}
