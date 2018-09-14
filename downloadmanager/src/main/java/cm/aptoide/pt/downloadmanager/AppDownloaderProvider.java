package cm.aptoide.pt.downloadmanager;

/**
 * Created by filipegoncalves on 9/11/18.
 */

public class AppDownloaderProvider {

  private final FileDownloaderProvider fileDownloaderProvider;

  public AppDownloaderProvider(FileDownloaderProvider fileDownloaderProvider) {
    this.fileDownloaderProvider = fileDownloaderProvider;
  }

  public AppDownloader getAppDownloader(DownloadApp downloadApp) {
    return new AppDownloadManager(fileDownloaderProvider, downloadApp);
  }
}
