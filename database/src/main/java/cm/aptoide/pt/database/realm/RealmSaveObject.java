/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 16/05/2016.
 */

package cm.aptoide.pt.database.realm;

import io.realm.Realm;
import io.realm.RealmModel;

/**
 * Created by sithengineer on 16/05/16.
 */
public final class RealmSaveObject {

	public static void save(final RealmModel realmModel) {
		Realm.getDefaultInstance().executeTransactionAsync(
			(bgRealm) -> {
				bgRealm.copyToRealmOrUpdate(realmModel);
			}
		);
	}

}
