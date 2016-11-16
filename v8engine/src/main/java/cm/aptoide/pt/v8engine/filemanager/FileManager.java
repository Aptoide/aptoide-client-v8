package cm.aptoide.pt.v8engine.filemanager;

import cm.aptoide.pt.downloadmanager.AptoideDownloadManager;
import cm.aptoide.pt.downloadmanager.interfaces.CacheManager;
import cm.aptoide.pt.preferences.Application;
import cm.aptoide.pt.utils.FileUtils;
import rx.Observable;

/**
 * Created by trinkes on 11/16/16.
 */

public class FileManager implements CacheManager {

  private final CacheHelper cacheHelper;
  private FileUtils fileUtils;
  private String[] cacheFolders;
  private AptoideDownloadManager downloadManager;

  public FileManager(CacheHelper cacheHelper, FileUtils fileUtils,
      String[] cacheFolders, AptoideDownloadManager downloadManager) {
    this.cacheHelper = cacheHelper;
    this.fileUtils = fileUtils;
    this.cacheFolders = cacheFolders;
    this.downloadManager = downloadManager;
  }

  public static FileManager build() {
    String[] folders = {
        Application.getContext().getCacheDir().getPath(),
        Application.getConfiguration().getCachePath()
    };
    return new FileManager(CacheHelper.build(), new FileUtils(), folders,
        AptoideDownloadManager.getInstance());
  }

  /**
   * deletes expired cache files
   */
  public Observable<Long> cleanCache() {
    return cacheHelper.cleanCache()
        .flatMap(cleaned -> downloadManager.invalidateDatabase().map(success -> cleaned));
  }

  public Observable<Long> deleteCache() {
    return fileUtils.deleteFolder(cacheFolders)
        .flatMap(deletedSize -> {
          if (deletedSize > 0) {
            return downloadManager.invalidateDatabase()
                .map(success -> deletedSize);
          } else {
            return Observable.just(deletedSize);
          }
        });
  }
}
