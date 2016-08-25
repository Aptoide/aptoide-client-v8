/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 25/08/2016.
 */

package cm.aptoide.pt.v8engine.deprecated.tables;

import android.database.Cursor;

import io.realm.RealmObject;

/**
 * Created by sithengineer on 24/08/16.
 */
public class ExcludedAd extends BaseTable {
	
	public static final String COLUMN_PACKAGE = "package_name";
	private static final String NAME = "excludedads";
	
	@Override
	public String getTableName() {
		return NAME;
	}

	@Override
	public RealmObject convert(Cursor cursor) {
		cm.aptoide.pt.database.realm.ExcludedAd realmObject = new cm.aptoide.pt.database.realm.ExcludedAd();
		realmObject.setPackageName(cursor.getString(cursor.getColumnIndex(COLUMN_PACKAGE)));
		return realmObject;
	}

	@Override
	public String[] getColumns() {
		return new String[]{COLUMN_PACKAGE};
	}
}
