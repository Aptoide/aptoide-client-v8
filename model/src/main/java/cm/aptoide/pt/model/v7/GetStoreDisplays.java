/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 22/04/2016.
 */

package cm.aptoide.pt.model.v7;

import java.util.List;

/**
 * Created by neuro on 22-04-2016.
 */
public class GetStoreDisplays extends BaseV7Response {

	private List<EventImage> list;

	public static class EventImage {

		public String label;
		public String graphic;
		public Event event;

		public static class Event {

			public String type; // API, v3, EXTERNAL
			public String name; // listApps, getStore, getStoreWidgets, getApkComments
			public String action;
		}
	}
}
