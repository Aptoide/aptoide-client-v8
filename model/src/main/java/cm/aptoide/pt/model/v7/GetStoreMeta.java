/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 22/04/2016.
 */

package cm.aptoide.pt.model.v7;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Created by neuro on 22-04-2016.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class GetStoreMeta extends BaseV7Response {

	private Data data;

	public static class Data {

		private Number id;
		private String name;
		private String avatar;
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
}
