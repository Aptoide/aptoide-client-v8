/*
 * Copyright (c) 2016.
 * Modified on 01/09/2016.
 */

package cm.aptoide.pt.database;

import cm.aptoide.pt.database.room.DownloadDAO;
import cm.aptoide.pt.database.room.RoomDownload;
import cm.aptoide.pt.downloadmanager.DownloadPersistence;
import hu.akarnokd.rxjava.interop.RxJavaInterop;
import io.reactivex.BackpressureStrategy;
import java.util.ArrayList;
import java.util.List;
import rx.Completable;
import rx.Observable;
import rx.Single;
import rx.schedulers.Schedulers;

public class RoomDownloadPersistence implements DownloadPersistence {

  private DownloadDAO downloadDAO;

  public RoomDownloadPersistence(DownloadDAO downloadDAO) {
    this.downloadDAO = downloadDAO;
  }

  public Observable<List<RoomDownload>> getAll() {
    return RxJavaInterop.toV1Observable(downloadDAO.getAll(), BackpressureStrategy.BUFFER)
        .defaultIfEmpty(new ArrayList<>())
        .subscribeOn(Schedulers.io());
  }

  public Single<RoomDownload> getAsSingle(String md5) {
    return RxJavaInterop.toV1Single(downloadDAO.getAsSingle(md5))
        .onErrorReturn(throwable -> null)
        .subscribeOn(Schedulers.io());
  }

  public Observable<RoomDownload> getAsObservable(String md5) {
    return RxJavaInterop.toV1Observable(downloadDAO.getAsObservable(md5),
        BackpressureStrategy.BUFFER)
        .onErrorReturn(throwable -> null)
        .subscribeOn(Schedulers.io());
  }

  public Completable delete(String md5) {
    return Completable.fromAction(() -> downloadDAO.remove(md5))
        .subscribeOn(Schedulers.io());
  }

  public void save(RoomDownload download) {
    new Thread(() -> downloadDAO.insert(download)).start();
  }

 /* public Completable save(List<RoomDownload> downloads) {
    return Completable.fromAction(() -> downloadDAO.insertAll(downloads))
        .subscribeOn(Schedulers.io());
  }*/

  public Observable<List<RoomDownload>> getRunningDownloads() {
    return RxJavaInterop.toV1Observable(downloadDAO.getRunningDownloads(),
        BackpressureStrategy.BUFFER)
        .onErrorReturn(throwable -> new ArrayList<>())
        .subscribeOn(Schedulers.io());
    /*

    return Observable.fromCallable(() -> database.get())
        .flatMap(realm -> realm.where(RoomDownload.class)
            .equalTo("overallDownloadStatus", RoomDownload.PROGRESS)
            .or()
            .equalTo("overallDownloadStatus", RoomDownload.PENDING)
            .or()
            .equalTo("overallDownloadStatus", RoomDownload.IN_QUEUE)
            .findAll()
            .asObservable())
        .unsubscribeOn(RealmSchedulers.getScheduler())
        .flatMap((data) -> database.copyFromRealm(data))
        .subscribeOn(RealmSchedulers.getScheduler())
        .observeOn(Schedulers.io());
    */
  }

  public Observable<List<RoomDownload>> getInQueueSortedDownloads() {
    return RxJavaInterop.toV1Observable(downloadDAO.getInQueueSortedDownloads(),
        BackpressureStrategy.BUFFER)
        .onErrorReturn(throwable -> new ArrayList<>())
        .subscribeOn(Schedulers.io());

    /*return Observable.fromCallable(() -> database.get())
        .flatMap(realm -> realm.where(RoomDownload.class)
            .equalTo("overallDownloadStatus", RoomDownload.IN_QUEUE)
            .findAllSorted("timeStamp", Sort.ASCENDING)
            .asObservable())
        .unsubscribeOn(RealmSchedulers.getScheduler())
        .flatMap((data) -> database.copyFromRealm(data))
        .subscribeOn(RealmSchedulers.getScheduler())
        .observeOn(Schedulers.io());*/
  }

  public Observable<List<RoomDownload>> getAsList(String md5) {
    return RxJavaInterop.toV1Observable(downloadDAO.getAsList(md5), BackpressureStrategy.BUFFER)
        .defaultIfEmpty(new ArrayList<>())
        .onErrorReturn(throwable -> new ArrayList<>())
        .subscribeOn(Schedulers.io());

    //return database.getAsList(RoomDownload.class, RoomDownload.MD5, md5);
  }

  public Observable<List<RoomDownload>> getUnmovedFilesDownloads() {
    return RxJavaInterop.toV1Observable(downloadDAO.getUnmovedFilesDownloads(),
        BackpressureStrategy.BUFFER)
        .onErrorReturn(throwable -> new ArrayList<>())
        .subscribeOn(Schedulers.io());
/*
    return Observable.fromCallable(() -> database.get())
        .flatMap(realm -> realm.where(RoomDownload.class)
            .equalTo("overallDownloadStatus", RoomDownload.WAITING_TO_MOVE_FILES)
            .findAllSorted("timeStamp", Sort.ASCENDING)
            .asObservable())
        .unsubscribeOn(RealmSchedulers.getScheduler())
        .flatMap((data) -> database.copyFromRealm(data))
        .subscribeOn(RealmSchedulers.getScheduler())
        .observeOn(Schedulers.io());*/
  }
}
