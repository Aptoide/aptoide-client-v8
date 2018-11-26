package cm.aptoide.pt.downloadmanager;

import java.util.HashMap;

/**
 * Created by filipegoncalves on 9/11/18.
 */

public class AppDownloaderProvider {

  private final RetryFileDownloaderProvider fileDownloaderProvider;
  private final DownloadAnalytics downloadAnalytics;

  public AppDownloaderProvider(RetryFileDownloaderProvider fileDownloaderProvider,
      DownloadAnalytics downloadAnalytics) {
    this.fileDownloaderProvider = fileDownloaderProvider;
    this.downloadAnalytics = downloadAnalytics;
  }

  public AppDownloader getAppDownloader(DownloadApp downloadApp) {
    return new AppDownloadManager(fileDownloaderProvider, downloadApp, new HashMap<>(),
        downloadAnalytics);
  }
}
