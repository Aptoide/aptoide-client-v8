/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 30/08/2016.
 */

package cm.aptoide.pt.v8engine.repository;

import java.util.List;

import cm.aptoide.pt.database.NewDatabase;
import cm.aptoide.pt.database.accessors.ScheduledAccessor;
import cm.aptoide.pt.database.realm.Scheduled;
import rx.Observable;

/**
 * Created by sithengineer on 30/08/16.
 */
public class ScheduledDownloadRepository {

	private static final String TAG = ScheduledDownloadRepository.class.getSimpleName();

	private ScheduledAccessor scheduledAccessor;

	public ScheduledDownloadRepository() {
		scheduledAccessor = new ScheduledAccessor(new NewDatabase());
	}

	public Observable<Scheduled> getScheduledUpdate(long appId){
		return scheduledAccessor.get(appId);
	}

	public Observable<List<Scheduled>> getAllScheduledDownloads() {
		return scheduledAccessor.getAll();
	}

	public void deleteScheduledDownload(long appId) {
		scheduledAccessor.delete(appId);
	}
}
