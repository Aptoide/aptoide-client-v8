/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 05/05/2016.
 */

package cm.aptoide.pt.model.v7.store;

import java.util.List;

import cm.aptoide.pt.model.v7.BaseV7Response;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Created by hsousa on 17/09/15.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class GetStoreTabs extends BaseV7Response {

	private List<Tab> list;

	@Data
	public static class Tab {

		private String label;
		private Event event;

		@Data
		public static class Event {

			public static final String GET_STORE_TAB = "getStore";
			public static final String GET_STORE_WIDGETS_TAB = "getStoreWidgets";
			public static final String GET_APK_COMMENTS_TAB = "getApkComments";
			public static final String GET_REVIEWS_TAB = "getReviews";

			public static final String API_V7_TYPE = "API";
			public static final String API_V3_TYPE = "v3";

			public static final String EVENT_LIST_APPS = "listApps";
			public static final String EVENT_LIST_STORES = "listStores";
			public static final String EVENT_GETSTOREWIDGETS = "getStoreWidgets";
			public static final String EVENT_GETAPKCOMMENTS = "getApkComments";

			private Type type; // API, v3
			private Name name; // listApps, getStore, getStoreWidgets, getApkComments
			private String action;

			public enum Type {
				API,
				v3,
			}

			public enum Name {
				getStore,
				getStoreWidgets,
				getReviews,
				getApkComments,
			}
		}
	}
}
