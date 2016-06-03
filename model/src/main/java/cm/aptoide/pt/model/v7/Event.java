/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 25/05/2016.
 */

package cm.aptoide.pt.model.v7;

import lombok.Data;

/**
 * Created by neuro on 10-05-2016.
 */
@Data
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
		getStore,
		getStoreWidgets,
		getReviews,
		getApkComments,

		// Client
		myUpdates,
		myStores
	}
}
