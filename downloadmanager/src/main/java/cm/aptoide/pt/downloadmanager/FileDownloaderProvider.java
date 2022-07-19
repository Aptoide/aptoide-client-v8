package cm.aptoide.pt.downloadmanager;

import io.reactivex.subjects.PublishSubject;

/**
 * Created by filipegoncalves on 8/3/18.
 */

public interface FileDownloaderProvider {

  FileDownloader createFileDownloader(String md5, String mainDownloadPath, int fileType,
      String packageName, int versionCode, String fileName,
      PublishSubject<FileDownloadCallback> fileDownloadCallback, String attributionId);
}
