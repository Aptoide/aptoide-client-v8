/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 27/04/2016.
 */

package cm.aptoide.pt.model.v2;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * GetAdsResponse.
 */
@lombok.Data
public class GetAdsResponse {

	final private List<Ads> ads;
	final private Options options;

	@lombok.Data
	public static class Data {

		final private long id;
		final private String name;
		final private String repo;
		@JsonProperty("package") final private String packageName;
		final private String md5sum;
		final private long size;
		final private int vercode;
		final private String vername;
		final private String icon;
		final private int downloads;
		final private int stars;
		final private String description;
	}

	@lombok.Data
	public static class Ads {

		final private Data data;
		final private Info info;
		final private Partner partner;
		final private Partner tracker;
	}

	@lombok.Data
	public static class Info {

		final private long adId;
		final private String adType;
		final private String cpcUrl;
		final private String cpiUrl;
		final private String cpdUrl;
	}

	@lombok.Data
	public static class Partner {

		final private Info info;
		final private Data data;

		@lombok.Data
		public static class Info {

			final private int id;
			final private String name;
		}

		@lombok.Data
		public static class Data {

			final private String clickUrl;
			final private String impressionUrl;
		}
	}

	@lombok.Data
	public static class Options {

		final private Boolean mediation = true;
	}
}
