/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 22/04/2016.
 */

package cm.aptoide.pt.model.v7;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Created by neuro on 22-04-2016.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ListAppVersions extends BaseV7Response {

	/**
	 * The other versions list always returns one item (itself), as per the web team.
	 */
	private List<ViewItem> list = new ArrayList<>();

	@Data
	public static class ViewItem {

		private Number id;
		private String name;
		@JsonProperty("package") private String packageName;
		private Number size;
		private String icon;
		private String graphic;
		private String added;
		private String modified;
		private String updated;
		private String uptype;
		/**
		 * Class used on an App item
		 */
		private GetStoreMeta.Data store;
		private ViewItem.File file;
		private ViewItem.Stats stats;
		private ViewItem.Appearance appearance;

		private String avatar; // used only on Store

		/**
		 * Class used on an App item
		 */
		@Data
		public static class File {

			private String vername;
			private Number vercode;
			private String md5sum;
		}

		/**
		 * Class used on an Store item
		 */
		@Data
		public static class Appearance {

			private String theme;
			private String description;
			private String view;
		}

		@Data
		public static class Stats {

			private Number apps;         // used on Store items
			private Number subscribers;  // used both on App items and Store items
			private Number downloads;    // used on listApps, Store items and listAppsVersions
			private ViewItem.Stats.Rating rating;       // used on App items and listAppsVersions

			@Data
			public static class Rating {

				private Number avg;
				private Number total;
				private List<ViewItem.Stats.Rating.Vote> votes = new ArrayList<>();

				@Data
				public static class Vote {

					private Number value;
					private Number count;
				}
			}
		}
	}
}
