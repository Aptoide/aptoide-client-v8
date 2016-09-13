/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 01/09/2016.
 */

package cm.aptoide.pt.database.accessors;

import java.util.List;

import cm.aptoide.pt.database.realm.Download;
import cm.aptoide.pt.database.schedulers.RealmSchedulers;
import io.realm.Sort;
import rx.Observable;
import rx.schedulers.Schedulers;

public class DownloadAccessor implements Accessor {

	private final Database database;

	public DownloadAccessor(Database db) {
		this.database = db;
	}

	public Observable<List<Download>> getAll() {
		return database.getAll(Download.class);
	}

	public Observable<Download> get(long downloadId) {
		return database.get(Download.class, Download.DOWNLOAD_ID, downloadId);
	}

	public void delete(long downloadId) {
		Observable.fromCallable(() -> {
			database.delete(Download.class, Download.DOWNLOAD_ID, downloadId);
			return null;
		}).subscribeOn(RealmSchedulers.getScheduler()).subscribe(o -> {
		}, throwable -> throwable.printStackTrace());
	}

	public Void save(Download download) {
		Database.save(download);
		return null;
	}

	public Void save(List<Download> download) {
		Database.save(download);
		return null;
	}

	public Observable<List<Download>> getRunningDownloads() {
		return Observable.fromCallable(() -> Database.get())
				.flatMap(realm -> realm.where(Download.class)
						.equalTo("overallDownloadStatus", Download.PROGRESS)
						.or()
						.equalTo("overallDownloadStatus", Download.PENDING)
						.or()
						.equalTo("overallDownloadStatus", Download.IN_QUEUE).findAll().asObservable())
				.unsubscribeOn(RealmSchedulers.getScheduler())
				.flatMap((data) -> database.copyFromRealm(data))
				.subscribeOn(RealmSchedulers.getScheduler())
				.observeOn(Schedulers.io());
	}

	public Observable<List<Download>> getInQueueSortedDownloads() {
		return Observable.fromCallable(() -> Database.get())
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
		return Observable.fromCallable(() -> Database.get())
				.flatMap(realm -> realm.where(Download.class).findAllSorted("timeStamp", sort)
						.asObservable())
				.unsubscribeOn(RealmSchedulers.getScheduler())
				.flatMap((data) -> database.copyFromRealm(data))
				.subscribeOn(RealmSchedulers.getScheduler())
				.observeOn(Schedulers.io());
	}
}
