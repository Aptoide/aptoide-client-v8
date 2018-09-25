package cm.aptoide.pt.file;

import cm.aptoide.pt.dataprovider.cache.L2Cache;
import cm.aptoide.pt.downloadmanager.AptoideDownloadManager;
import cm.aptoide.pt.utils.FileUtils;
import rx.Observable;
import rx.Single;

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

  /**
   * deletes expired cache files
   */
  public Single<Long> purgeCache() {
    return cacheHelper.cleanCache()
        .toSingle()
        .flatMap(cleaned -> downloadManager.invalidateDatabase()
            .andThen(Single.just(cleaned)));
  }

  public Observable<Long> deleteCache() {
    return fileUtils.deleteFolder(cacheFolders)
        .flatMap(deletedSize -> {
          if (deletedSize > 0) {
            return downloadManager.invalidateDatabase()
                .andThen(Observable.just(deletedSize));
          } else {
            return Observable.just(deletedSize);
          }
        })
        .doOnNext(aVoid -> {
          httpClientCache.clean();
        });
  }
}
