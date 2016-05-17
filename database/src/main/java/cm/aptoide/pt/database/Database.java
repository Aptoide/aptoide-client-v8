/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 17/05/2016.
 */

package cm.aptoide.pt.database;

import android.content.Context;
import android.text.TextUtils;

import cm.aptoide.pt.database.module.AllClassesModule;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmObject;

/**
 * Created by sithengineer on 16/05/16.
 */
public class Database {

	public String KEY = "KRbjij20wgVCTJgjQEyUFhMxm2gUHg0s1HwPUX7DLCp92VKMCaOTBL0JP6et";

	private final RealmConfiguration realmConfig;
	private Realm realm;

	public Database(Context context) {

		StringBuilder strBuilder = new StringBuilder(KEY);
		strBuilder.append(extract(cm.aptoide.pt.model.BuildConfig.APPLICATION_ID));
		strBuilder.append(extract(cm.aptoide.pt.utils.BuildConfig.APPLICATION_ID));
		strBuilder.append(extract(cm.aptoide.pt.database.BuildConfig.APPLICATION_ID));
		strBuilder.append(extract(cm.aptoide.pt.preferences.BuildConfig.APPLICATION_ID));

		String dbName = "library.db.realm";

		// Beware this is the app context
		// So always use a unique name
		// Always use explicit modules in library projects
		realmConfig = new RealmConfiguration.Builder(context)
				.name(dbName)
				.modules(Realm.getDefaultModule(), new AllClassesModule())
				.encryptionKey(strBuilder.toString().substring(0, 64).getBytes())
				// Must be bumped when the schema changes
				.schemaVersion(cm.aptoide.pt.database.BuildConfig.VERSION_CODE)
				// Migration to run instead of throwing an exception
				.migration(new AptoideRealmMigration())
				.build();

		// Reset Realm
		//Realm.deleteRealm(realmConfig);

		KEY = "";
	}

	private String extract(String str) {
		return TextUtils.substring(str, str.lastIndexOf('.'), str.length());
	}

	public void open() {
		// Don't use Realm.setDefaultInstance() in library projects. It is unsafe as app developers can override the
		// default configuration. So always use explicit configurations in library projects.
		realm = Realm.getInstance(realmConfig);
	}

	public void close() {
		realm.close();
	}

	public Realm getRealm() {
		return realm;
	}

	public <T extends RealmObject> boolean save(T realmModel) {
		if(realm!=null && !realm.isClosed()) {
			realm.executeTransactionAsync(
					(Realm bgRealm) -> bgRealm.copyToRealmOrUpdate(realmModel)
			);
			return true;
		}
		return false;
	}

}
