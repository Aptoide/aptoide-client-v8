/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 01/09/2016.
 */

package cm.aptoide.pt.database.accessors;

import cm.aptoide.pt.database.realm.Download;
import cm.aptoide.pt.database.schedulers.RealmSchedulers;
import io.realm.Sort;
import java.util.List;
import rx.Observable;
import rx.schedulers.Schedulers;

public class DownloadAccessor extends SimpleAccessor<Download> {

  public DownloadAccessor(Database db) {
    super(db, Download.class);
  }

  public Observable<List<Download>> getAll() {
    return database.getAll(Download.class);
  }

  public Observable<Download> get(long downloadId) {
    return database.get(Download.class, Download.DOWNLOAD_ID, downloadId);
  }

  public Observable<Download> get(String md5) {
    return database.get(Download.class, Download.MD5, md5);
  }

  @Deprecated public void delete(long downloadId) {
    Observable.fromCallable(() -> {
      database.delete(Download.class, Download.DOWNLOAD_ID, downloadId);
      return null;
    })
        .subscribeOn(RealmSchedulers.getScheduler())
        .subscribe(o -> {
        }, throwable -> throwable.printStackTrace());
  }

  public void delete(String md5) {
    Observable.fromCallable(() -> {
      database.delete(Download.class, Download.MD5, md5);
      return null;
    })
        .subscribeOn(RealmSchedulers.getScheduler())
        .subscribe(o -> {
        }, throwable -> throwable.printStackTrace());
  }

  public void save(Download download) {
    database.insert(download);
  }

  public void save(List<Download> download) {
    database.insertAll(download);
  }

  public Observable<List<Download>> getRunningDownloads() {
    return Observable.fromCallable(() -> Database.getInternal())
        .flatMap(realm -> realm.where(Download.class)
            .equalTo("overallDownloadStatus", Download.PROGRESS)
            .or()
            .equalTo("overallDownloadStatus", Download.PENDING)
            .or()
            .equalTo("overallDownloadStatus", Download.IN_QUEUE)
            .findAll()
            .asObservable())
        .unsubscribeOn(RealmSchedulers.getScheduler())
        .flatMap((data) -> database.copyFromRealm(data))
        .subscribeOn(RealmSchedulers.getScheduler())
        .observeOn(Schedulers.io());
  }

  public Observable<List<Download>> getInQueueSortedDownloads() {
    return Observable.fromCallable(() -> Database.getInternal())
        .flatMap(realm -> realm.where(Download.class)
            .equalTo("overallDownloadStatus", Download.IN_QUEUE)
            .findAllSorted("timeStamp", Sort.ASCENDING)
            .asObservable())
        .unsubscribeOn(RealmSchedulers.getScheduler())
        .flatMap((data) -> database.copyFromRealm(data))
        .subscribeOn(RealmSchedulers.getScheduler())
        .observeOn(Schedulers.io());
  }

  public Observable<List<Download>> getAllSorted(Sort sort) {
    return Observable.fromCallable(() -> Database.getInternal())
        .flatMap(realm -> realm.where(Download.class)
            .findAllSorted("timeStamp", sort)
            .asObservable())
        .unsubscribeOn(RealmSchedulers.getScheduler())
        .flatMap((data) -> database.copyFromRealm(data))
        .subscribeOn(RealmSchedulers.getScheduler())
        .observeOn(Schedulers.io());
  }

  public Observable<List<Download>> getAsList(String md5) {
    return database.getAsList(Download.class, Download.MD5, md5);
  }
}
