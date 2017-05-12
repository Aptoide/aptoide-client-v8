package cm.aptoide.pt.v8engine.filemanager;

import cm.aptoide.pt.downloadmanager.AptoideDownloadManager;
import cm.aptoide.pt.networkclient.okhttp.cache.L2Cache;
import cm.aptoide.pt.preferences.Application;
import cm.aptoide.pt.utils.FileUtils;
import rx.Observable;

/**
 * Created by trinkes on 11/16/16.
 */

public class FileManager {

  private final CacheHelper cacheHelper;
  private final FileUtils fileUtils;
  private final String[] cacheFolders;
  private final AptoideDownloadManager downloadManager;
  private final L2Cache httpClientCache;

  public FileManager(CacheHelper cacheHelper, FileUtils fileUtils, String[] cacheFolders,
      AptoideDownloadManager downloadManager, L2Cache httpClientCache) {
    this.cacheHelper = cacheHelper;
    this.fileUtils = fileUtils;
    this.cacheFolders = cacheFolders;
    this.downloadManager = downloadManager;
    this.httpClientCache = httpClientCache;
  }

  public static FileManager build(AptoideDownloadManager downloadManager, L2Cache httpClientCache) {
    String[] folders = {
        Application.getContext()
            .getCacheDir().getPath(), Application.getConfiguration().getCachePath()
    };
    return new FileManager(CacheHelper.build(), new FileUtils(), folders, downloadManager,
        httpClientCache);
  }

  /**
   * deletes expired cache files
   */
  public Observable<Long> purgeCache() {
    return cacheHelper.cleanCache()
        .flatMap(cleaned -> downloadManager.invalidateDatabase()
            .map(success -> cleaned));
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
        })
        .doOnNext(aVoid -> {
          httpClientCache.clean();
        });
  }
}
