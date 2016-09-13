/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 02/09/2016.
 */

package cm.aptoide.pt.database.accessors;

import android.content.Context;
import android.text.TextUtils;
import cm.aptoide.pt.database.BuildConfig;
import cm.aptoide.pt.database.schedulers.RealmSchedulers;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmMigration;
import io.realm.RealmObject;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import java.util.List;
import lombok.Cleanup;
import rx.Observable;

/**
 * Created by sithengineer on 16/05/16.
 *
 * This is the main class responsible to offer {@link Realm} database instances
 */
public final class Database {

  private static final String TAG = Database.class.getSimpleName();
  private static final String KEY = "KRbjij20wgVyUFhMxm2gUHg0s1HwPUX7DLCp92VKMCt";
  private static final String DB_NAME = "aptoide.realm.db";
  private static final RealmMigration MIGRATION = new RealmToRealmDatabaseMigration();

  private static boolean isInitialized = false;

  //
  // Static methods
  //
  private static Realm INSTANCE;

  protected Database() {
  }

  private static String extract(String str) {
    return TextUtils.substring(str, str.lastIndexOf('.'), str.length());
  }

  public static void initialize(Context context) {
    isInitialized = true;
    if (isInitialized) return;

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
      realmConfig = new RealmConfiguration.Builder(context).name(DB_NAME)
          // Must be bumped when the schema changes
          .schemaVersion(BuildConfig.VERSION_CODE).deleteRealmIfMigrationNeeded().build();
    } else {
      realmConfig = new RealmConfiguration.Builder(context).name(DB_NAME)
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

  public static <E extends RealmObject> void save(E realmObject) {
    Realm realm = Realm.getDefaultInstance();
    realm.beginTransaction();
    realm.insertOrUpdate(realmObject);
    realm.commitTransaction();
    realm.close();
  }

  public static <E extends RealmObject> void save(List<E> realmObject) {
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

  protected static Realm get() {
    if (!isInitialized) {
      throw new IllegalStateException("You need to call Database.initialize(Context) first");
    }

    return Realm.getDefaultInstance();
  }

  //
  // Instance methods
  //

  /**
   * this code is expected to run on only a single thread, so no synchronizing primitives were used
   *
   * @return singleton Realm instance
   */
  private static Realm getInternal() {
    if (!isInitialized) {
      throw new IllegalStateException("You need to call Database.initialize(Context) first");
    }

    if (INSTANCE == null) {
      INSTANCE = Realm.getDefaultInstance();
    }

    return INSTANCE;
  }

  private Observable<Realm> getRealm() {
    return Observable.just(null)
        .observeOn(RealmSchedulers.getScheduler())
        .map(something -> Database.getInternal());
  }

  protected <E extends RealmObject> Observable<List<E>> copyFromRealm(RealmResults<E> results) {
    return Observable.just(results)
        .filter(data -> data.isLoaded())
        .map(realmObjects -> Database.getInternal().copyFromRealm(realmObjects));
  }

  protected <E extends RealmObject> Observable<E> copyFromRealm(E object) {
    return Observable.just(object)
        .filter(data -> data.isLoaded())
        .map(realmObject -> Database.getInternal().copyFromRealm(realmObject));
  }

  private <E extends RealmObject> Observable<E> findFirst(RealmQuery<E> query) {
    return Observable.just(query.findFirst())
        .filter(realmObject -> realmObject != null)
        .flatMap(realmObject -> realmObject.<E>asObservable().unsubscribeOn(
            RealmSchedulers.getScheduler()))
        .flatMap(realmObject -> copyFromRealm(realmObject))
        .defaultIfEmpty(null);
  }

  public <E extends RealmObject> Observable<List<E>> getAll(Class<E> clazz) {
    return getRealm().flatMap(
        realm -> realm.where(clazz).findAll().<List<E>>asObservable().unsubscribeOn(
            RealmSchedulers.getScheduler())).flatMap(results -> copyFromRealm(results));
  }

  public <E extends RealmObject> Observable<E> get(Class<E> clazz, String key, String value) {
    return getRealm().map(realm -> realm.where(clazz).equalTo(key, value))
        .flatMap(query -> findFirst(query));
  }

  public <E extends RealmObject> Observable<E> get(Class<E> clazz, String key, Integer value) {
    return getRealm().map(realm -> realm.where(clazz).equalTo(key, value))
        .flatMap(query -> findFirst(query));
  }

  public <E extends RealmObject> Observable<E> get(Class<E> clazz, String key, Long value) {
    return getRealm().map(realm -> realm.where(clazz).equalTo(key, value))
        .flatMap(query -> findFirst(query));
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

  public <E extends RealmObject> void delete(Class<E> clazz, String key, Long value) {
    @Cleanup Realm realm = get();
    E first = realm.where(clazz).equalTo(key, value).findFirst();
    if (first != null) {
      realm.beginTransaction();
      first.deleteFromRealm();
      realm.commitTransaction();
    }
  }
}
