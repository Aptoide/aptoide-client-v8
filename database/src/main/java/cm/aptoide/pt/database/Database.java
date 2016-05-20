/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 20/05/2016.
 */

package cm.aptoide.pt.database;

import android.content.Context;
import android.text.TextUtils;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmMigration;

/**
 * Created by sithengineer on 16/05/16.
 */
public class Database {

	private static final String KEY = "KRbjij20wgVyUFhMxm2gUHg0s1HwPUX7DLCp92VKMCt";
	private static final String DB_NAME = "aptoide.realm.db";
	private static final AllClassesModule MODULE = new AllClassesModule();
	private static final RealmMigration MIGRATION = new RealmToRealmDatabaseMigration();

	private static String extract(String str) {
		return TextUtils.substring(str, str.lastIndexOf('.'), str.length());
	}

	public static Realm get(Context context) {
		StringBuilder strBuilder = new StringBuilder(KEY);
		strBuilder.append(extract(cm.aptoide.pt.model.BuildConfig.APPLICATION_ID));
		strBuilder.append(extract(cm.aptoide.pt.utils.BuildConfig.APPLICATION_ID));
		strBuilder.append(extract(cm.aptoide.pt.database.BuildConfig.APPLICATION_ID));
		strBuilder.append(extract(cm.aptoide.pt.preferences.BuildConfig.APPLICATION_ID));

		// Beware this is the app context
		// So always use a unique name
		// Always use explicit modules in library projects
		RealmConfiguration realmConfig;
		if (BuildConfig.DEBUG) {
			realmConfig = new RealmConfiguration.Builder(context).name(DB_NAME).modules(MODULE)
					// Must be bumped when the schema changes
					.schemaVersion(cm.aptoide.pt.database.BuildConfig.VERSION_CODE)
					// Migration to run instead of throwing an exception
					.migration(MIGRATION).build();
		} else {
			realmConfig = new RealmConfiguration.Builder(context).name(DB_NAME)
					.modules(MODULE)
					.encryptionKey(strBuilder.toString().substring(0, 64).getBytes())
					// Must be bumped when the schema changes
					.schemaVersion(cm.aptoide.pt.database.BuildConfig.VERSION_CODE)
					// Migration to run instead of throwing an exception
					.migration(MIGRATION)
					.build();
		}

		// Reset Realm
		//Realm.deleteRealm(realmConfig);

		return Realm.getInstance(realmConfig);
	}

}