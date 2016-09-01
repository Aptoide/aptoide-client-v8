/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 01/09/2016.
 */

package cm.aptoide.pt.database.accessors;

import java.util.List;

import cm.aptoide.pt.database.NewDatabase;
import cm.aptoide.pt.database.realm.Installed;
import cm.aptoide.pt.database.realm.Scheduled;
import rx.Observable;

/**
 * Created by sithengineer on 01/09/16.
 */
public class ScheduledAccessor {

	private final NewDatabase database;

	public ScheduledAccessor(NewDatabase db) {
		this.database = db;
	}

	public Observable<List<Scheduled>> getAll() {
		return database.getAll(Scheduled.class);
	}

	public Observable<Scheduled> get(long appId) {
		return database.get(Scheduled.class, Scheduled.APP_ID, appId);
	}

	public void delete(long appId) {
		database.delete(Installed.class, Scheduled.APP_ID, appId);
	}

}
