package cm.aptoide.pt.v8engine.filemanager;

import cm.aptoide.pt.database.accessors.AccessorFactory;
import cm.aptoide.pt.database.realm.Download;
import cm.aptoide.pt.database.realm.Installed;
import cm.aptoide.pt.downloadmanager.AptoideDownloadManager;
import cm.aptoide.pt.downloadmanager.interfaces.CacheManager;
import cm.aptoide.pt.preferences.Application;
import cm.aptoide.pt.utils.FileUtils;
import cm.aptoide.pt.v8engine.InstallManager;
import cm.aptoide.pt.v8engine.Progress;
import cm.aptoide.pt.v8engine.install.InstallerFactory;
import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * Created by trinkes on 11/16/16.
 */

public class FileManager implements CacheManager {

  private final CacheHelper cacheHelper;
  private InstallManager installManager;
  private FileUtils fileUtils;
  private String[] cacheFolders;
  private AptoideDownloadManager downloadManager;

  public FileManager(CacheHelper cacheHelper, InstallManager installManager, FileUtils fileUtils,
      String[] cacheFolders, AptoideDownloadManager downloadManager) {
    this.cacheHelper = cacheHelper;
    this.installManager = installManager;
    this.fileUtils = fileUtils;
    this.cacheFolders = cacheFolders;
    this.downloadManager = downloadManager;
  }

  public static FileManager build() {
    String[] folders = {
        Application.getContext().getCacheDir().getPath(),
        Application.getConfiguration().getCachePath()
    };
    return new FileManager(CacheHelper.build(),
        new InstallManager(AptoideDownloadManager.getInstance(),
            new InstallerFactory().create(Application.getContext(), InstallerFactory.ROLLBACK),
            AccessorFactory.getAccessorFor(Download.class),
            AccessorFactory.getAccessorFor(Installed.class)), new FileUtils(), folders,
        AptoideDownloadManager.getInstance());
  }

  /**
   * deletes expired cache files
   */
  public Observable<Long> cleanCache() {
    return cacheHelper.cleanCache()
        .flatMap(cleaned -> downloadManager.invalidateDatabase().map(success -> cleaned));
  }

  /**
   * deletes cache files
   *
   * @throws DownloadIsRunningException the observable will get an exception if download is running
   */
  public Observable<Long> clearCache() {
    return checkInstalling().observeOn(Schedulers.io())
        .flatMap(eResponse -> fileUtils.deleteFolder(cacheFolders))
        .flatMap(deletedSize -> {
          if (deletedSize > 0) {
            return AptoideDownloadManager.getInstance()
                .invalidateDatabase()
                .map(success -> deletedSize);
          } else {
            return Observable.just(deletedSize);
          }
        });
  }

  /**
   * @throws DownloadIsRunningException if download is running
   */
  private Observable<Void> checkInstalling() {
    return installManager.getInstallationsAsList()
        .first()
        .observeOn(Schedulers.computation())
        .flatMapIterable(progresses -> progresses)
        .filter(progress -> progress.getState() == Progress.ACTIVE)
        .toList()
        .map(progresses -> progresses != null && progresses.size() > 0)
        .flatMap(isDownload -> {
          if (isDownload) {
            return Observable.error(new DownloadIsRunningException());
          } else {
            return Observable.just(null);
          }
        });
  }

  public static class DownloadIsRunningException extends RuntimeException {
  }
}
