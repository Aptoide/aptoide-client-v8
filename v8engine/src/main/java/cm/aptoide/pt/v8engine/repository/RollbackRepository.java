/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 02/09/2016.
 */

package cm.aptoide.pt.v8engine.repository;

import java.util.List;

import cm.aptoide.pt.database.accessors.DeprecatedDatabase;
import cm.aptoide.pt.database.realm.Rollback;
import cm.aptoide.pt.model.v7.GetAppMeta;
import cm.aptoide.pt.v8engine.repository.exception.RepositoryItemNotFoundException;
import io.realm.Realm;
import io.realm.RealmResults;
import lombok.Cleanup;
import rx.Observable;

/**
 * Created by sithengineer on 30/08/16.
 */
public class RollbackRepository {

	public Observable<Rollback> getRollback(String packageName, Rollback.Action action) {
		final Realm realm = DeprecatedDatabase.get();
		Rollback rollback = DeprecatedDatabase.RollbackQ.get(realm, packageName, action);
		if (rollback != null) {
			rollback.<Rollback> asObservable().filter(rollbackFilter -> rollbackFilter.isLoaded()).flatMap
					(rollbackFlat -> {
				if (rollbackFlat != null && rollbackFlat.isValid()) {
					return Observable.just(rollbackFlat).doOnCompleted(() -> {
						if (realm != null && !realm.isClosed()) {
							realm.close();
						}
					});
				}
				return Observable.error(new RepositoryItemNotFoundException(String.format("No scheduled download found for package name %s and action %s",
						packageName, action
						.name())));
			});
		}
		return Observable.error(new RepositoryItemNotFoundException(String.format("No scheduled download found for package name %s and action %s",
				packageName, action.name())));
	}

	public Observable<RealmResults<Rollback>> getAllRollbacks() {
		final Realm realm = DeprecatedDatabase.get();
		return DeprecatedDatabase.RollbackQ.getAll(realm).<List<Rollback>> asObservable().asObservable()
				.filter(rollbacks -> rollbacks.isLoaded())
				.flatMap(rollbacks -> {
					if (rollbacks != null && rollbacks.isValid()) {
						return Observable.just(rollbacks).doOnCompleted(() -> {
							if (realm != null && !realm.isClosed()) {
								realm.close();
							}
						});
					}
					return Observable.error(new RepositoryItemNotFoundException("No scheduled downloads found"));
				});
	}

	public Observable<Void> deleteRollback(String packageName, Rollback.Action action) {
		return Observable.fromCallable(() -> {
			@Cleanup Realm realm = DeprecatedDatabase.get();
			DeprecatedDatabase.RollbackQ.delete(realm, packageName, action);
			return null;
		});
	}

	public Observable<Void> deleteAllRollbacks() {
		return Observable.fromCallable(() -> {
			@Cleanup Realm realm = DeprecatedDatabase.get();
			DeprecatedDatabase.RollbackQ.deleteAll(realm);
			return null;
		});
	}

	public Observable<Void> upadteRollbackWithAction(Rollback rollback, Rollback.Action action) {
		return Observable.fromCallable(() -> {
			@Cleanup Realm realm = DeprecatedDatabase.get();
			realm.beginTransaction();
			rollback.setAction(action.name());
			realm.copyToRealmOrUpdate(rollback);
			realm.commitTransaction();
			return null;
		});
	}

	public Observable<Void> upadteRollbackWithAction(String md5, Rollback.Action action) {
		return Observable.fromCallable(() -> {
			@Cleanup Realm realm = DeprecatedDatabase.get();
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
			@Cleanup Realm realm = DeprecatedDatabase.get();
			Rollback rollback = new Rollback(app, action);
			realm.beginTransaction();
			realm.copyToRealmOrUpdate(rollback);
			realm.commitTransaction();
			return null;
		});
	}
}
