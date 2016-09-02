/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 02/09/2016.
 */

package cm.aptoide.pt.v8engine.repository;

import java.util.List;

import cm.aptoide.pt.database.accessors.ScheduledAccessor;
import cm.aptoide.pt.database.realm.Scheduled;
import rx.Observable;

/**
 * Created by sithengineer on 30/08/16.
 */
public class ScheduledDownloadRepository implements Repository {

	private final ScheduledAccessor scheduledAccessor;

	protected ScheduledDownloadRepository(ScheduledAccessor scheduledAccessor) {
		this.scheduledAccessor = scheduledAccessor;
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

	public Observable<List<Scheduled>> setInstalling(List<Scheduled> scheduledList) {
		return scheduledAccessor.setInstalling(scheduledList);
	}

	public Observable<Scheduled> setInstalling(Scheduled scheduled) {
		return scheduledAccessor.setInstalling(scheduled);
	}
}
