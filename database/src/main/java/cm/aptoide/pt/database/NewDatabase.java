/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 01/09/2016.
 */

package cm.aptoide.pt.database;

import android.content.Context;
import android.text.TextUtils;

import java.util.List;

import cm.aptoide.pt.database.schedulers.RealmSchedulers;
import cm.aptoide.pt.logger.Logger;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmMigration;
import io.realm.RealmModel;
import io.realm.RealmObject;
import lombok.Cleanup;
import rx.Observable;

/**
 * Created by sithengineer on 16/05/16.
 *
 * This is the main class responsible to offer {@link Realm} database instances
 */
public class NewDatabase {

	private static final String TAG = NewDatabase.class.getSimpleName();
	private static final String KEY = "KRbjij20wgVyUFhMxm2gUHg0s1HwPUX7DLCp92VKMCt";
	private static final String DB_NAME = "aptoide.realm.db";
	private static final AllClassesModule MODULE = new AllClassesModule();
	private static final RealmMigration MIGRATION = new RealmToRealmDatabaseMigration();

	private static boolean isInitialized = false;

	//
	// Static methods
	//

	private static String extract(String str) {
		return TextUtils.substring(str, str.lastIndexOf('.'), str.length());
	}

	public static void initialize(Context context) {
		if(isInitialized) return;

		StringBuilder strBuilder = new StringBuilder(KEY);
		strBuilder.append(extract(cm.aptoide.pt.model.BuildConfig.APPLICATION_ID));
		strBuilder.append(extract(cm.aptoide.pt.utils.BuildConfig.APPLICATION_ID));
		strBuilder.append(extract(BuildConfig.APPLICATION_ID));
		strBuilder.append(extract(cm.aptoide.pt.preferences.BuildConfig.APPLICATION_ID));

		// Beware this is the app context
		// So always use a unique name
		// Always use explicit modules in library projects
		RealmConfiguration realmConfig;
		if (BuildConfig.DEBUG) {
			realmConfig = new RealmConfiguration.Builder(context).name(DB_NAME).modules(MODULE)
					// Must be bumped when the schema changes
					.schemaVersion(BuildConfig.VERSION_CODE)
					.deleteRealmIfMigrationNeeded()
					.build();
		} else {
			realmConfig = new RealmConfiguration.Builder(context).name(DB_NAME).modules(MODULE)
					//.encryptionKey(strBuilder.toString().substring(0, 64).getBytes()) // FIXME: 30/08/16 sithengineer use DB encryption for a safer ride
					// Must be bumped when the schema changes
					.schemaVersion(BuildConfig.VERSION_CODE)
					// Migration to run instead of throwing an exception
					//.migration(MIGRATION)
					.deleteRealmIfMigrationNeeded() // FIXME: 30/08/16 sithengineer use migration script when new DB migrations are needed
					.build();
		}

		if (BuildConfig.DELETE_DB) {
			Realm.deleteRealm(realmConfig);
		}
		Realm.setDefaultConfiguration(realmConfig);
		isInitialized = true;
	}

	public static void save(RealmObject realmObject) {
		@Cleanup Realm realm = Realm.getDefaultInstance();
		realm.beginTransaction();
		realm.insertOrUpdate(realmObject);
		realm.commitTransaction();
	}

	public static void delete(RealmObject realmObject) {
		@Cleanup Realm realm = Realm.getDefaultInstance();
		realm.beginTransaction();
		realmObject.deleteFromRealm();
		realm.commitTransaction();
	}

	public static Realm get() {
		if (!isInitialized) {
			throw new IllegalStateException("You need to call Database.initialize(Context) first");
		}

		return Realm.getDefaultInstance();
	}

	private static Realm INSTANCE;

	/**
	 * this code is expected to run on only a single thread, so no synchronizing primitives were used
	 *
	 * @return singleton Realm instance
	 */
	private static Realm getInternal() {
		if (!isInitialized) {
			throw new IllegalStateException("You need to call Database.initialize(Context) first");
		}

		if(INSTANCE==null) {
			INSTANCE = Realm.getDefaultInstance();
		}

		return INSTANCE;
	}

	//
	// Instance methods
	//

	public <E extends RealmObject> Observable<List<E>> getAll(Class<E> clazz) {
		//			if (RealmSchedulers.isRealmSchedulerThread(Thread.currentThread())) {
		//				throw new IllegalThreadStateException(String.format("Use %s class to get a scheduler to interact with Realm", RealmSchedulers.class
		// .getName()));
		//			}
		return Observable.just(null)
				.observeOn(RealmSchedulers.getScheduler())
				.map(something -> NewDatabase.getInternal())
				.flatMap(realm -> realm.where(clazz).findAll().<List<E>> asObservable())
				.filter(data -> data.isLoaded())
				.map(NewDatabase.getInternal()::copyFromRealm);
	}

	public <E extends RealmObject> Observable<E> get(Class<E> clazz, String key, String value) {
		return Observable.just(null)
				.observeOn(RealmSchedulers.getScheduler())
				.map(something -> NewDatabase.getInternal())
				.flatMap(realm -> realm.where(clazz).equalTo(key, value).findFirst().<E> asObservable())
				.filter(data -> data.isLoaded())
				.map(NewDatabase.getInternal()::copyFromRealm);
	}

	public <E extends RealmObject> Observable<E> get(Class<E> clazz, String key, Integer value) {
		return Observable.just(null)
				.observeOn(RealmSchedulers.getScheduler())
				.map(something -> NewDatabase.getInternal())
				.flatMap(realm -> realm.where(clazz).equalTo(key, value).findFirst().<E> asObservable())
				.filter(data -> data.isLoaded())
				.map(NewDatabase.getInternal()::copyFromRealm);
	}

	public <E extends RealmObject> void delete(Class<E> clazz, String key, String value) {
		@Cleanup Realm realm = get();
		E first = realm.where(clazz).equalTo(key, value).findFirst();
		if (first != null) {
			realm.beginTransaction();
			first.deleteFromRealm();
			realm.commitTransaction();
		}
	}

	public <E extends RealmObject> void delete(Class<E> clazz, String key, Integer value) {
		@Cleanup Realm realm = get();
		E first = realm.where(clazz).equalTo(key, value).findFirst();
		if (first != null) {
			realm.beginTransaction();
			first.deleteFromRealm();
			realm.commitTransaction();
		}
	}
}
