/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 02/09/2016.
 */

package cm.aptoide.pt.v8engine.repository;

import cm.aptoide.pt.database.accessors.DeprecatedDatabase;
import cm.aptoide.pt.database.accessors.RollbackAccessor;
import cm.aptoide.pt.database.realm.Rollback;
import cm.aptoide.pt.v8engine.repository.exception.RepositoryItemNotFoundException;
import io.realm.Realm;
import io.realm.RealmResults;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Cleanup;
import rx.Observable;

/**
 * Created by sithengineer on 30/08/16.
 */
@AllArgsConstructor public class RollbackRepository implements Repository {

	private RollbackAccessor accessor;

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

	public Observable<List<Rollback>> getConfirmedRollbacks() {
		return accessor.getConfirmedRollbacks();
	}

	public Observable<Void> addRollbackWithAction(Rollback rollback) {
		return Observable.fromCallable(() -> {
			accessor.save(rollback);
			return null;
		});
	}

	public Observable<Rollback> getNotConfirmedRollback(String packageName) {
		return accessor.getNotConfirmedRollback(packageName);
	}

	public void confirmRollback(Rollback rollback) {
		rollback.setConfirmed(true);
		save(rollback);
	}

	public Observable<Rollback> getRollback(String packageName) {
		return accessor.get(packageName);
	}

	public void save(Rollback rollback) {
		accessor.save(rollback);
	}
}
