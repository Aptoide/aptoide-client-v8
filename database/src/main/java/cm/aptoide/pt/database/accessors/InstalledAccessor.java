/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 02/09/2016.
 */

package cm.aptoide.pt.database.accessors;

import java.util.List;

import cm.aptoide.pt.database.realm.Installed;
import rx.Observable;

/**
 * Created by sithengineer on 01/09/16.
 */
public class InstalledAccessor implements Accessor {

	private final Database database;

	protected InstalledAccessor(Database db) {
		this.database = db;
	}

	public Observable<List<Installed>> getAll() {
		return database.getAll(Installed.class);
	}

	public Observable<Installed> get(String packageName) {
		return database.get(Installed.class, Installed.PACKAGE_NAME, packageName);
	}

	public void delete(String packageName) {
		database.delete(Installed.class, Installed.PACKAGE_NAME, packageName);
	}

	public Observable<Boolean> isInstalled(String packageName) {
		return get(packageName).map(installed -> installed!=null);
	}
}
