/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 16/05/2016.
 */

package cm.aptoide.pt.database.realm;

import io.realm.Realm;
import io.realm.RealmObject;

/**
 * Created by sithengineer on 16/05/16.
 */
public abstract class RealmSaveObject extends RealmObject {

	public void save() {
		Realm.getDefaultInstance().executeTransactionAsync(
			new Realm.Transaction() {
				@Override
				public void execute(Realm bgRealm) {
					bgRealm.copyToRealmOrUpdate(RealmSaveObject.this);
				}
			}
		);
	}

}
