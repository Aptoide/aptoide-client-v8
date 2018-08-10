package cm.aptoide.pt.downloadmanager;

/**
 * Created by filipegoncalves on 8/3/18.
 */

public interface FileDownloaderProvider {

  FileDownloader createFileDownloader(String mainDownloadPath, int fileType, String packageName,
      int versionCode, String fileName);
}
