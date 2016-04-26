/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 26/04/2016.
 */

package cm.aptoide.pt.model.v7.listapp;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

import cm.aptoide.pt.model.v7.GetStoreMeta;
import lombok.Data;

/**
 * Created by neuro on 22-04-2016.
 */
@Data
public class ListAppData {

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
	private File file;
	private Stats stats;

	@Data
	public static class Stats {

		private Number apps;         // used on Store items
		private Number subscribers;  // used both on App items and Store items
		private Number downloads;    // used on listApps, Store items and listAppsVersions
		private Stats.Rating rating;       // used on App items and listAppsVersions

		@Data
		public static class Rating {

			private Number avg;
			private Number total;
			private List<Stats.Rating.Vote> votes = new ArrayList<>();

			@Data
			public static class Vote {

				private Number value;
				private Number count;
			}
		}
	}
}
