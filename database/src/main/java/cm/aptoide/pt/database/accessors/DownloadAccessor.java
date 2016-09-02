/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 01/09/2016.
 */

package cm.aptoide.pt.database.accessors;

import cm.aptoide.pt.database.NewDatabase;
import cm.aptoide.pt.database.realm.Download;
import cm.aptoide.pt.database.schedulers.RealmSchedulers;
import java.util.List;
import rx.Observable;
import rx.schedulers.Schedulers;

public class DownloadAccessor {

	private final NewDatabase database;

	public DownloadAccessor(NewDatabase db) {
		this.database = db;
	}

	public Observable<List<Download>> getAll() {
		return database.getAll(Download.class);
	}

	public Observable<Download> get(long downloadId) {
		return database.get(Download.class, Download.DOWNLOAD_ID, downloadId);
	}

	public void delete(long downloadId) {
		database.delete(Download.class, Download.DOWNLOAD_ID, downloadId);
	}

	public Void save(Download download) {
		NewDatabase.save(download);
		return null;
	}

	public Void save(List<Download> download) {
		NewDatabase.save(download);
		return null;
	}

	//	public Observable<List<Download>> getRunningDownloads() {
	//		return database.getRealm()
	//				.flatMap(realm -> realm.where(Download.class)
	//						.equalTo("overallDownloadStatus", Download.PROGRESS)
	//						.or()
	//						.equalTo("overallDownloadStatus", Download.PENDING)
	//						.or()
	//						.equalTo("overallDownloadStatus", Download.IN_QUEUE)
	//						.findAll()
	//						.asObservable()
	//						.unsubscribeOn(RealmSchedulers.getScheduler()))
	//				.flatMap((data) -> database.copyFromRealm(data))
	//				.subscribeOn(RealmSchedulers.getScheduler())
	//				.observeOn(Schedulers.io());
	//	}

	public Observable<List<Download>> getRunningDownloads() {
		return Observable.fromCallable(() -> NewDatabase.getInternal())
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
}
