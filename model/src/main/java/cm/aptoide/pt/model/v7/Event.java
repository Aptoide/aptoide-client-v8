/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 29/06/2016.
 */

package cm.aptoide.pt.model.v7;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Created by neuro on 10-05-2016.
 */
@Data
@Accessors(chain = true)
public class Event {

	private Type type; // API, v3
	private Name name; // listApps, getStore, getStoreWidgets, getApkComments
	private String action;

	public enum Type {
		API,
		CLIENT
	}

	public enum Name {
		// Api
		listApps,
		listStores,
		getStore,
		getStoreWidgets,
		getReviews,
		getApkComments,
		getUserTimeline,

		// Client
		myStores,
		myUpdates,
		myExcludedUpdates,
		myScheduledDownloads,
		myRollbacks,
		getAds,
	}
}
