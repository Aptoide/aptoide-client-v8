/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 27/04/2016.
 */

package cm.aptoide.pt.model.v7.store;

import java.util.Date;

import lombok.Data;

/**
 * Created by neuro on 27-04-2016.
 */
@Data
public class Store {

	private Number id;
	private String name;
	private String avatar;
	private Date added;
	private Date modified;
	private Appearance appearance;
	private Stats stats;

	public static class Stats {

		private Number apps;
		private Number subscribers;
		private Number downloads;
	}

	public static class Appearance {

		private String theme;
		private String description;
	}
}
