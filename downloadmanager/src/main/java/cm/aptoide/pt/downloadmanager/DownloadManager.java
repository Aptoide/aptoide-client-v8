package cm.aptoide.pt.downloadmanager;

import cm.aptoide.pt.downloads_database.data.database.model.DownloadEntity;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;
import java.util.List;
import org.jetbrains.annotations.NotNull;

/**
 * Created by filipegoncalves on 7/27/18.
 */

public interface DownloadManager {

  void start();

  void stop();

  Completable startDownload(DownloadEntity download);

  Observable<DownloadEntity> getDownloadAsObservable(String md5);

  Single<DownloadEntity> getDownloadAsSingle(String md5);

  Observable<List<DownloadEntity>> getDownloadsList();

  Observable<DownloadEntity> getCurrentInProgressDownload();

  Observable<List<DownloadEntity>> getCurrentActiveDownloads();

  Completable removeDownload(String md5);

  Completable invalidateDatabase();

  Observable<DownloadEntity> getCompletedDownload(@NotNull String packageName);
}
