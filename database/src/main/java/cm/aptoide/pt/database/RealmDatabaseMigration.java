/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 24/08/2016.
 */

package cm.aptoide.pt.database;

import java.util.Locale;

import cm.aptoide.pt.logger.Logger;
import io.realm.DynamicRealm;
import io.realm.RealmMigration;


/**
 * Created by sithengineer on 12/05/16.
 *
 * This code is responsible to migrate Realm versions in between
 */
class RealmDatabaseMigration implements RealmMigration {

	private static final String TAG = RealmDatabaseMigration.class.getName();

	@Override
	public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {
		Logger.w(TAG, String.format(Locale.ROOT, "realm database migration from version %d to %d",
				oldVersion, newVersion));
	}
}
