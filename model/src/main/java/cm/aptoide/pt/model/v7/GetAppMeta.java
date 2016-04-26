/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 26/04/2016.
 */

package cm.aptoide.pt.model.v7;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

import cm.aptoide.pt.model.v7.listapp.File;
import cm.aptoide.pt.model.v7.subclasses.Obb;
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

		private Number id;
		private String name;
		@JsonProperty("package") private String packageName;
		private Number size;
		private String icon;
		private String graphic;
		private String added;
		private String modified;
		private Developer developer;
		private GetStoreMeta.Data store;
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
		private List<String> usedFeatures = new ArrayList<>();
		private List<String> usedPermissions = new ArrayList<>();

		@Data
		public static class Signature {

			private String sha1;
			private String owner;
		}

		@Data
		public static class Hardware {

			private Number sdk;
			private String screen;
			private Number gles;
			private List<String> cpus = new ArrayList<>();
			/**
			 * Second array contains only two values: First value is the screen, second value is the density
			 */
			private List<List<Number>> densities = new ArrayList<>();
		}

		/**
		 * List of various malware reasons http://ws2.aptoide.com/api/7/getApp/apk_md5sum/7de07d96488277d8d76eafa2ef66f5a8
		 * <p>
		 * <p>
		 * RANK2: http://ws2.aptoide.com/api/7/getApp/apk_md5sum/7de07d96488277d8d76eafa2ef66f5a8 http://ws2.aptoide.com/api/7/getApp/apk_md5sum/06c9eb56b787b6d3b606d68473a38f47
		 * <p>
		 * RANK3: http://ws2.aptoide.com/api/7/getApp/apk_md5sum/18f0d5bdb9df1e0e27604890113c3331 http://ws2.aptoide.com/api/7/getApp/apk_md5sum/74cbfde9dc6da43d3d14f4df9cdb9f2f
		 * <p>
		 * Rank can be: TRUSTED, WARNING, UNKNOWN
		 */
		@Data
		public static class Malware {

			private static final String PASSED = "passed";
			private static final String WARN = "warn";

			private static final String TRUSTED = "TRUSTED";
			private static final String WARNING = "WARNING";
			private static final String UNKNOWN = "UNKNOWN";

			private static final String GOOGLE_PLAY = "Google Play";

			private String rank;
			private GetAppMetaFile.Malware.Reason reason;
			private String added;
			private String modified;

			@Data
			public static class Reason {

				private GetAppMetaFile.Malware.Reason.SignatureValidated signatureValidated;
				private GetAppMetaFile.Malware.Reason.ThirdPartyValidated thirdpartyValidated;
				private GetAppMetaFile.Malware.Reason.Manual manual;
				private GetAppMetaFile.Malware.Reason.Scanned scanned;

				@Data
				public static class SignatureValidated {

					private String date;
					/**
					 * possible value: "unknown", "failed", "passed"
					 */
					private String status;
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
					private String status;
					private List<String> av;
				}

				@Data
				public static class Scanned {

					/**
					 * possible values: "passed", "warn"
					 */
					private String status;
					private String date;
					private List<GetAppMetaFile.Malware.Reason.Scanned.AvInfo> avInfo;

					@Data
					public static class AvInfo {

						private List<GetAppMetaFile.Malware.Reason.Scanned.AvInfo.Infection> infections;
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
			private List<GetAppMetaFile.Flags.Vote> votes = new ArrayList<>();

			@Data
			public static class Vote {

				/**
				 * type can be:
				 * <p>
				 * FAKE, FREEZE, GOOD, LICENSE, VIRUS
				 */
				private GetAppMetaFile.Flags.Vote.Type type;
				private Number count;

				public enum Type {
					FAKE, FREEZE, GOOD, LICENSE, VIRUS
				}
			}
		}
	}

	@Data
	public static class Media {

		private List<String> keywords = new ArrayList<>();
		private String description;
		private String news;
		private List<Media.Screenshot> screenshots = new ArrayList<>();
		private List<Media.Video> videos = new ArrayList<>();

		@Data
		public static class Video {

			private String type;
			private String url;
			private String thumbnail;
		}

		@Data
		public static class Screenshot {

			private String url;
			private Number height;
			private Number width;

			public String getOrientation() {
				return height.intValue() > width.intValue() ? "portrait" : "landscape";
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
		private Number downloads;
		private Number pdownloads;

		@Data
		public static class Rating {

			private Number avg;
			private List<Stats.Rating.Vote> votes = new ArrayList<>();

			@Data
			public static class Vote {

				private Number value;
				private Number count;
			}
		}
	}

	@Data
	public static class Pay {

		private Number price;
		private String currency;
		private String symbol;
	}
}
