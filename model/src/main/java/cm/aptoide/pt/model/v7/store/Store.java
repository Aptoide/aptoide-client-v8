/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 12/05/2016.
 */

package cm.aptoide.pt.model.v7.store;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * Created by neuro on 27-04-2016.
 */
@Data
@Accessors(chain = true)
public class Store {

	private long id;
	private String name;
	private String avatar;
	private Date added;
	private Date modified;
	private Appearance appearance;
	private Stats stats;

	@Data
	public static class Stats {

		private int apps;
		private int subscribers;
		private int downloads;
	}

	@Data
	@NoArgsConstructor
	@AllArgsConstructor()
	public static class Appearance {

		private String theme;
		private String description;
	}
}
