/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 30/08/2016.
 */

package cm.aptoide.pt.v8engine.repository;

import java.util.List;

import cm.aptoide.pt.database.Database;
import cm.aptoide.pt.database.realm.Rollback;
import cm.aptoide.pt.database.realm.Scheduled;
import cm.aptoide.pt.database.schedulers.RealmSchedulers;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.model.v7.GetAppMeta;
import cm.aptoide.pt.v8engine.repository.exception.RepositoryItemNotFoundException;
import io.realm.Realm;
import io.realm.RealmResults;
import lombok.Cleanup;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by sithengineer on 30/08/16.
 */
public class ScheduledDownloadRepository {

	private static final String TAG = ScheduledDownloadRepository.class.getSimpleName();

	public Observable<Scheduled> getScheduledUpdate(long appId){
		final Realm realm = Database.get();
		return Database.ScheduledQ.get(realm, appId).<Scheduled>asObservable().asObservable()
				.filter(scheduledDownload -> scheduledDownload.isLoaded() )
				.flatMap(scheduledDownload -> {
					if (scheduledDownload != null && scheduledDownload.isValid()) {
						return Observable.just(scheduledDownload).doOnCompleted(()->{
							if(realm!=null && !realm.isClosed()){
								realm.close();
							}
						});
					}
					return Observable.error(new RepositoryItemNotFoundException("No scheduled download found for app id: " + appId));
				});
	}

	public Observable<List<Scheduled>> getAllScheduledUpdates() {
		final Realm realm = Database.get();
		return Database.ScheduledQ.getAll(realm).<List<Scheduled>>asObservable().asObservable()
				.filter(scheduledDownloads -> scheduledDownloads.isLoaded())
				.flatMap(scheduledDownloads -> {
					if (scheduledDownloads != null && scheduledDownloads.isValid()) {
						return Observable.just(realm.copyFromRealm(scheduledDownloads)).doOnCompleted(() -> {
							if (realm != null && !realm.isClosed()) {
								realm.close();
							}
						});
					}
					return Observable.error(new RepositoryItemNotFoundException("No scheduled downloads found"));
				});
	}

	public Observable<Void> deleteScheduledDownload(long appId) {
		return Observable.fromCallable(()-> {
			Logger.v(TAG, "Deleting schedule download for app id " + appId);
			@Cleanup Realm realm = Database.get();
			Database.ScheduledQ.delete(realm, appId);
			return null;
		});
	}

	public Observable<Void> upadteRollbackWithAction(Rollback rollback, Rollback.Action action) {
		return Observable.fromCallable(() -> {
			@Cleanup Realm realm = Database.get();
			realm.beginTransaction();
			rollback.setAction(action.name());
			realm.copyToRealmOrUpdate(rollback);
			realm.commitTransaction();
			return null;
		});
	}

	public Observable<Void> upadteRollbackWithAction(String md5, Rollback.Action action) {
		return Observable.fromCallable(() -> {
			@Cleanup Realm realm = Database.get();
			Rollback rollback = realm.where(Rollback.class).equalTo(Rollback.MD5, md5).findFirstAsync();
			realm.beginTransaction();
			rollback.setAction(action.name());
			realm.copyToRealmOrUpdate(rollback);
			realm.commitTransaction();
			return null;
		});
	}

	public Observable<Void> addRollbackWithAction(GetAppMeta.App app, Rollback.Action action) {
		return Observable.fromCallable(() -> {
			@Cleanup Realm realm = Database.get();
			Rollback rollback = new Rollback(app, action);
			realm.beginTransaction();
			realm.copyToRealmOrUpdate(rollback);
			realm.commitTransaction();
			return null;
		});
	}
}
