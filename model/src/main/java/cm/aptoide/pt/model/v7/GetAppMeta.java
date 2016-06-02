/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 31/05/2016.
 */

package cm.aptoide.pt.model.v7;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

import cm.aptoide.pt.model.v7.listapp.File;
import cm.aptoide.pt.model.v7.store.Store;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Created by neuro on 22-04-2016.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class GetAppMeta extends BaseV7Response {

	private App data;

	@Data
	public static class App {

		private long id;
		private String name;
		@JsonProperty("package") private String packageName;
		private long size;
		private String icon;
		private String graphic;
		private String added;
		private String modified;
		private Developer developer;
		private Store store;
		private GetAppMetaFile file;
		private Media media;
		private Urls urls;
		private Stats stats;
		private Obb obb;
		private Pay pay;
	}

	@Data
	public static class Developer {

		private String name;
		private String website;
		private String email;
		private String privacy;
	}

	@Data
	@EqualsAndHashCode(callSuper = true)
	public static class GetAppMetaFile extends File {

		private GetAppMetaFile.Signature signature;
		private GetAppMetaFile.Hardware hardware;
		private GetAppMetaFile.Malware malware;
		private GetAppMetaFile.Flags flags;
		private List<String> usedFeatures;
		private List<String> usedPermissions;

		@Data
		public static class Signature {

			private String sha1;
			private String owner;
		}

		@Data
		public static class Hardware {

			private int sdk;
			private String screen;
			private int gles;
			private List<String> cpus;
			/**
			 * Second array contains only two values: First value is the screen, second value is
			 * the
			 * density
			 */
			private List<List<Integer>> densities;
		}

		/**
		 * List of various malware reasons http://ws2.aptoide
		 * .com/api/7/getApp/apk_md5sum/7de07d96488277d8d76eafa2ef66f5a8
		 * <p>
		 * <p>
		 * RANK2: http://ws2.aptoide.com/api/7/getApp/apk_md5sum/7de07d96488277d8d76eafa2ef66f5a8
		 * http://ws2.aptoide.com/api/7/getApp/apk_md5sum/06c9eb56b787b6d3b606d68473a38f47
		 * <p>
		 * RANK3: http://ws2.aptoide.com/api/7/getApp/apk_md5sum/18f0d5bdb9df1e0e27604890113c3331
		 * http://ws2.aptoide.com/api/7/getApp/apk_md5sum/74cbfde9dc6da43d3d14f4df9cdb9f2f
		 * <p>
		 * Rank can be: TRUSTED, WARNING, UNKNOWN
		 */
		@Data
		public static class Malware {

			public static final String TRUSTED = "TRUSTED";
			public static final String WARNING = "WARNING";
			public static final String UNKNOWN = "UNKNOWN";

			public static final String PASSED = "passed";
			public static final String WARN = "warn";
			public static final String GOOGLE_PLAY = "Google Play";

			private Rank rank;
			private Reason reason;
			private String added;
			private String modified;

			public enum Rank {
				TRUSTED,
				WARNING,
				UNKNOWN
			}

			@Data
			public static class Reason {

				private Reason.SignatureValidated signatureValidated;
				private Reason.ThirdPartyValidated thirdpartyValidated;
				private Reason.Manual manual;
				private Reason.Scanned scanned;

				public enum Status {
					passed, failed, blacklisted, warn
				}

				@Data
				public static class SignatureValidated {

					private String date;
					private Status status;
					private String signatureFrom;
				}

				@Data
				public static class ThirdPartyValidated {

					private String date;
					private String store;
				}

				@Data
				public static class Manual {

					private String date;
					private Status status;
					private List<String> av;
				}

				@Data
				public static class Scanned {

					private Status status;
					private String date;
					private List<AvInfo> avInfo;

					@Data
					public static class AvInfo {

						private List<Infection> infections;
						private String name;

						@Data
						public static class Infection {

							private String name;
							private String description;
						}
					}
				}
			}
		}

		@Data
		public static class Flags {

			public static final String GOOD = "GOOD";
			/**
			 * When there's a review, there are no votes
			 * <p>
			 * flags: { review": "GOOD" },
			 */
			public String review;
			private List<GetAppMetaFile.Flags.Vote> votes;

			@Data
			public static class Vote {

				/**
				 * type can be:
				 * <p>
				 * FAKE, FREEZE, GOOD, LICENSE, VIRUS
				 */
				private GetAppMetaFile.Flags.Vote.Type type;
				private int count;

				public enum Type {
					FAKE, FREEZE, GOOD, LICENSE, VIRUS
				}
			}
		}
	}

	@Data
	public static class Media {

		private List<String> keywords;
		private String description;
		private String news;
		private List<Media.Screenshot> screenshots;
		private List<Media.Video> videos;

		@Data
		public static class Video {

			private String type;
			private String url;
			private String thumbnail;
		}

		@Data
		public static class Screenshot {

			private String url;
			private int height;
			private int width;

			public String getOrientation() {
				return height > width ? "portrait" : "landscape";
			}
		}
	}

	@Data
	public static class Urls {

		private String w;
		private String m;
	}

	@Data
	public static class Stats {

		private Stats.Rating rating;
		private int downloads;
		private int pdownloads;

		@Data
		public static class Rating {

			private float avg;
			private List<Stats.Rating.Vote> votes;

			@Data
			public static class Vote {

				private int value;
				private int count;
			}
		}
	}

	@Data
	public static class Pay {

		private float price;
		private String currency;
		private String symbol;
	}
}
