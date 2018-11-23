package cm.aptoide.pt.downloadmanager;

import java.util.HashMap;

/**
 * Created by filipegoncalves on 9/11/18.
 */

public class AppDownloaderProvider {

  private final RetryFileDownloaderProvider fileDownloaderProvider;
  private final DownloadErrorAnalytics downloadErrorAnalytics;

  public AppDownloaderProvider(RetryFileDownloaderProvider fileDownloaderProvider,
      DownloadErrorAnalytics downloadErrorAnalytics) {
    this.fileDownloaderProvider = fileDownloaderProvider;
    this.downloadErrorAnalytics = downloadErrorAnalytics;
  }

  public AppDownloader getAppDownloader(DownloadApp downloadApp) {
    return new AppDownloadManager(fileDownloaderProvider, downloadApp, new HashMap<>(),
        downloadErrorAnalytics);
  }
}
