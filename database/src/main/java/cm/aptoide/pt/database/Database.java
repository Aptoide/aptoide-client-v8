/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 17/05/2016.
 */
package cm.aptoide.pt.database;

import android.content.Context;
import android.text.TextUtils;

import java.util.UUID;

import cm.aptoide.pt.logger.Logger;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmMigration;
import io.realm.RealmObject;
import rx.functions.Action0;
import rx.functions.Action1;

/**
 * Created by sithengineer on 16/05/16.
 */
public class Database {

	private static final String TAG = Database.class.getName();
	private static final String DB_NAME = "library.db.realm";
	private static final AllClassesModule MODULE = new AllClassesModule();
	private static final RealmMigration MIGRATION = new DatabaseMigration();
	private final RealmConfiguration realmConfig;
	public String KEY = "KRbjij20wgVCTJgjQEyUFhMxm2gUHg0s1HwPUX7DLCp92VKMCaOTBL0JP6et";
	private Realm realm;
	private boolean isOpen = false;

	public Database(Context context) {
		StringBuilder strBuilder = new StringBuilder(KEY);
		strBuilder.append(extract(cm.aptoide.pt.model.BuildConfig.APPLICATION_ID));
		strBuilder.append(extract(cm.aptoide.pt.utils.BuildConfig.APPLICATION_ID));
		strBuilder.append(extract(cm.aptoide.pt.database.BuildConfig.APPLICATION_ID));
		strBuilder.append(extract(cm.aptoide.pt.preferences.BuildConfig.APPLICATION_ID));
		// Beware this is the app context
		// So always use a unique name
		// Always use explicit modules in library projects
		realmConfig = new RealmConfiguration.Builder(context).name(DB_NAME)
				.modules(MODULE)
				.encryptionKey(strBuilder.toString().substring(0, 64).getBytes())
				// Must be bumped when the schema changes
				.schemaVersion(cm.aptoide.pt.database.BuildConfig.VERSION_CODE)
				// Migration to run instead of throwing an exception
				.migration(MIGRATION)
				.build();
		// Reset Realm
		//Realm.deleteRealm(realmConfig);
		KEY = "";
	}

	private String extract(String str) {
		return TextUtils.substring(str, str.lastIndexOf('.'), str.length());
	}

	public Database open() {
		// Don't use Realm.setDefaultInstance() in library projects. It is unsafe as app
		// developers can override the
		// default configuration. So always use explicit configurations in library projects.
		realm = Realm.getInstance(realmConfig);
		isOpen = true;
		return this;
	}

	public Database close() {
		if (realm != null && !realm.isClosed()) {
			realm.close();
		}
		isOpen = false;
		return this;
	}

	public Realm getRealm() {
		return realm;
	}

	public <T extends RealmObject> Database copyOrUpdate(T realmModel) {
		return runTransactionSync((bgRealm) -> bgRealm.copyToRealmOrUpdate(realmModel));
	}

	public Database runTransaction(final Action0 toRun) {
		checkOpen();
		try {
			realm.executeTransactionAsync(new Realm.Transaction() {
				@Override
				public void execute(Realm bgRealm) {
					toRun.call();
				}
			}, new Realm.Transaction.OnError() {
				@Override
				public void onError(Throwable error) {
					Logger.e(TAG, "runTransaction", error);
				}
			});
		} catch (Exception e) {
			Logger.e(TAG, "runTransaction", e);
		}
		return this;
	}

	public Database runTransaction(final Action1<Realm> toRun) {
		checkOpen();
		try {
			realm.executeTransactionAsync(new Realm.Transaction() {
				@Override
				public void execute(Realm bgRealm) {
					toRun.call(bgRealm);
				}
			}, new Realm.Transaction.OnError() {
				@Override
				public void onError(Throwable error) {
					Logger.e(TAG, "runTransaction", error);
				}
			});
		} catch (Exception e) {
			Logger.e(TAG, "runTransaction", e);
		}
		return this;
	}

	public Database runTransactionSync(final Action0 toRun) {
		checkOpen();
		try {
			realm.executeTransaction(new Realm.Transaction() {
				@Override
				public void execute(Realm bgRealm) {
					toRun.call();
				}
			});
		} catch (Exception e) {
			Logger.e(TAG, "runTransactionSync", e);
		}
		return this;
	}

	public Database runTransactionSync(final Action1<Realm> toRun) {
		checkOpen();
		try {
			realm.executeTransaction(new Realm.Transaction() {
				@Override
				public void execute(Realm bgRealm) {
					toRun.call(bgRealm);
				}
			});
		} catch (Exception e) {
			Logger.e(TAG, "runTransactionSync", e);
		}
		return this;
	}

	protected String getUuid() {
		return UUID.randomUUID().toString();
	}

	private void checkOpen() {
		if (realm == null) {
			throw new IllegalStateException("database is null");
		}
		if (!isOpen || realm.isClosed()) {
			throw new IllegalStateException("call method open() first");
		}
	}
}