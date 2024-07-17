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

  private final DownloadDAO downloadDAO;

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

  public Completable save(RoomDownload download) {
    return Completable.fromAction(() -> downloadDAO.insert(download))
        .subscribeOn(Schedulers.io());
  }

  public Observable<List<RoomDownload>> getRunningDownloads() {
    return RxJavaInterop.toV1Observable(downloadDAO.getRunningDownloads(),
            BackpressureStrategy.BUFFER)
        .onErrorReturn(throwable -> new ArrayList<>())
        .subscribeOn(Schedulers.io());
  }

  public Observable<List<RoomDownload>> getInQueueSortedDownloads() {
    return RxJavaInterop.toV1Observable(downloadDAO.getInQueueSortedDownloads(),
            BackpressureStrategy.BUFFER)
        .onErrorReturn(throwable -> new ArrayList<>())
        .subscribeOn(Schedulers.io());
  }

  public Observable<List<RoomDownload>> getAsList(String md5) {
    return RxJavaInterop.toV1Observable(downloadDAO.getAsList(md5), BackpressureStrategy.BUFFER)
        .defaultIfEmpty(new ArrayList<>())
        .onErrorReturn(throwable -> new ArrayList<>())
        .subscribeOn(Schedulers.io());
  }

  @Override public Completable delete(String packageName, int versionCode) {
    return Completable.fromAction(() -> downloadDAO.remove(packageName, versionCode));
  }

  @Override public Observable<List<RoomDownload>> getOutOfSpaceDownloads() {
    return RxJavaInterop.toV1Observable(downloadDAO.getOutOfSpaceDownloads(),
            BackpressureStrategy.BUFFER)
        .onErrorReturn(throwable -> new ArrayList<>())
        .subscribeOn(Schedulers.io());
  }
}
