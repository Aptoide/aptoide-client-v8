/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 27/04/2016.
 */

package cm.aptoide.pt.model.v7.store;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

import cm.aptoide.pt.model.v7.BaseV7Response;
import cm.aptoide.pt.model.v7.Datalist;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Created by neuro on 22-04-2016.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class GetStoreWidgets extends BaseV7Response {

	private Datalist<WSWidget> datalist;

	@Data
	public static class WSWidget {

		/**
		 * Constants for values of type
		 */
		public static final String ADS_TYPE = "ADS";
		public static final String APPS_GROUP_TYPE = "APPS_GROUP";
		public static final String CATEGORIES_TYPE = "DISPLAYS";
		public static final String TIMELINE_TYPE = "TIMELINE";
		public static final String REVIEWS_TYPE = "REVIEWS";
		public static final String COMMENTS_TYPE = "COMMENTS";
		public static final String STORE_GROUP = "STORES_GROUP";

		private String type;
		private String tag;
		private String title; // Highlighted, Games, Categories, Timeline, Recommended for you, Aptoide Publishers
		@JsonProperty("view") private String listApps;
		private List<Action> actions = new ArrayList<>();
		private Data data;

		@lombok.Data
		public static class Data {

			private String layout; // GRID, LIST, BRICK
			private String icon;
			private List<Data.Categories> categories = new ArrayList<>(); //only present if type": "DISPLAYS"

			@lombok.Data
			public static class Categories {

				private Number id;
				private String ref_id;
				private String parent_id;
				private String parent_ref_id;
				private String name;
				private String graphic;
				private String icon;
				private Number ads_count;
			}
		}

		@lombok.Data
		public static class Action {

			private String type; // button
			private String label;
			private String tag;
			private Action.Event event;

			@lombok.Data
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

				public String type; // API, v3
				public String name; // listApps, getStore, getStoreWidgets, getApkComments
				public String action;
			}
		}
	}
}
