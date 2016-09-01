/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 01/09/2016.
 */

package cm.aptoide.pt.database.accessors;

import java.util.List;

import cm.aptoide.pt.database.NewDatabase;
import cm.aptoide.pt.database.realm.Download;
import io.realm.RealmResults;
import rx.Observable;
import rx.Subscriber;

public class DownloadAccessor {

	private final NewDatabase database;
	private List<Download> downloads;
	private Subscriber<? super List<Download>> subscriber;
	private RealmResults<Download> downloads1;

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

	public void save(Download download) {
		NewDatabase.save(download);
	}

	public Void save(List<Download> download) {
		NewDatabase.save(download);
		return null;
	}

	public Observable<List<Download>> getRunningDownloads() {
		return database.getRealm()
				.flatMap(realm -> realm.where(Download.class)
						.equalTo("overallDownloadStatus", Download.PROGRESS)
						.or()
						.equalTo("overallDownloadStatus", Download.PENDING)
						.or()
						.equalTo("overallDownloadStatus", Download.IN_QUEUE)
						.findAll()
						.asObservable())
				.flatMap((data) -> database.copyFromRealm(data));
	}

	public Observable<List<Download>> getRunningDownloads2() {
		database.getRealm().map(realm -> {
			downloads1 = realm.where(Download.class)
					.equalTo("overallDownloadStatus", Download.PROGRESS)
					.or()
					.equalTo("overallDownloadStatus", Download.PENDING)
					.or()
					.equalTo("overallDownloadStatus", Download.IN_QUEUE)
					.findAll();
			downloads1.addChangeListener(element -> {
				if (!subscriber.isUnsubscribed()) {
					subscriber.onNext(realm.copyFromRealm(element));
				}
			});
			return downloads1;
		}).first().subscribe(downloads1 -> downloads = downloads1);

		return Observable.create(new Observable.OnSubscribe<List<Download>>() {
			@Override
			public void call(Subscriber<? super List<Download>> subscriber) {
				DownloadAccessor.this.subscriber = subscriber;
				subscriber.onNext(downloads);
			}
		});
	}
}
