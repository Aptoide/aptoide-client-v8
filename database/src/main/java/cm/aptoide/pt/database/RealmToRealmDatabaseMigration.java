/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 20/05/2016.
 */

package cm.aptoide.pt.database;

import java.util.Locale;

import cm.aptoide.pt.logger.Logger;
import io.realm.DynamicRealm;
import io.realm.Realm;
import io.realm.RealmMigration;


/**
 * Created by sithengineer on 12/05/16.
 */
class RealmToRealmDatabaseMigration implements RealmMigration {

	private static final String TAG = RealmToRealmDatabaseMigration.class.getName();

	@Override
	public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {
		// Migrate from version 1 to version 2
		if (oldVersion == 1) {
			// TODO
		}

		if (oldVersion == 2) {
			// TODO
		}

		Logger.w(TAG, String.format(Locale.ROOT, "realm database migration from version %d to %d",
				oldVersion, newVersion));


		// FIXME
		Realm.deleteRealm(realm.getConfiguration());
	}
}
