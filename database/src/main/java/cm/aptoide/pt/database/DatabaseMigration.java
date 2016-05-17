/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 17/05/2016.
 */

package cm.aptoide.pt.database;

import io.realm.DynamicRealm;
import io.realm.RealmMigration;


/**
 * Created by sithengineer on 12/05/16.
 */
class DatabaseMigration implements RealmMigration {

	@Override
	public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {
		// Migrate from version 1 to version 2
		if (oldVersion == 1) {
			// TODO
		}

		// FIXME
		realm.deleteAll();
	}
}
