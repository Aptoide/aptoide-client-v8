/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 22/04/2016.
 */

package cm.aptoide.pt.model.v7;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Created by hsousa on 17/09/15.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class GetStoreTabs extends BaseV7Response {

	@JsonProperty("list") private List<Tab> tabList;

	@Data
	public static class Tab {

		private String label;
		private Tab.Event event;

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

			private String type; // API, v3
			private String name; // listApps, getStore, getStoreWidgets, getApkComments
			private String action;
		}
	}
}
