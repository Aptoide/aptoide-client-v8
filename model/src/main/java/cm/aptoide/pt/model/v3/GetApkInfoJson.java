/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 01/08/2016.
 */

package cm.aptoide.pt.model.v3;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

import cm.aptoide.pt.model.v2.Comment;

/**
 * Created by marcelobenites on 7/27/16.
 */
public class GetApkInfoJson {
	public Apk apk;
	public String latest;
	public Malware malware;
	public Media media;
	public Meta meta;
	public Payment payment;
	public Signature signature;
	public String status;
	public ObbObject obb;
	public List<ErrorResponse> errors;

	public Apk getApk() {
		return apk;
	}

	public String getStatus() {
		return status;
	}

	public Signature getSignature() {
		return signature;
	}

	public String getLatest() {
		return latest;
	}

	public Malware getMalware() {
		return malware;
	}

	public Media getMedia() {
		return media;
	}

	public Payment getPayment() {
		return payment;
	}

	public ObbObject getObb() {
		return obb;
	}

	public List<ErrorResponse> getErrors() {
		return errors;
	}

	public Meta getMeta() {

		return meta;
	}

	public static class Media {
		public List<String> sshots;
		public List<Screenshots> sshots_hd;
		public List<Videos> videos;

		public static class Videos {
			public String thumb;
			public String type;
			public String url;
		}

		public static class Screenshots {
			public String path;
			public String orient;
		}
	}

	public static class Payment {

		public Double amount;

		@JsonProperty("currency_symbol")
		public String symbol;

		public String apkpath;
		public Metadata metadata;
		public List<PaymentServices> payment_services;
		public String status;

		public String getStatus() {
			return status;
		}

		public Number getAmount() {
			return amount;
		}

		public boolean isPaidApp() {
			//			symbol = "$";
			//			amount = 0.10;
			return ((amount != null) && (amount > 0.0));
		}

		public static class Metadata {
			public int id;
		}
	}

	/**
	 * Changes on the webservice's Json
	 * Created by neuro on 16-02-2015.
	 */
	public static class CategoriesJson {

		public String status;
		public Categories categories;
		List<Error> errors;

		public String getStatus() {
			return status;
		}

		public void setStatus(String status) {
			this.status = status;
		}

		public List<Error> getErrors() {
			return errors;
		}

		public void setErrors(List<Error> errors) {
			this.errors = errors;
		}

		public Categories getCategories() {
			return categories;
		}

		public void setCategories(Categories categories) {
			this.categories = categories;
		}

		public static class Categories {
			public List<Category> standard;
			public List<Category> custom;

			public List<Category> getStandard() {
				return standard;
			}

			public void setStandard(List<Category> standard) {
				this.standard = standard;
			}

			public List<Category> getCustom() {
				return custom;
			}

			public void setCustom(List<Category> custom) {
				this.custom = custom;
			}
		}

		public static class Category{

			public Number id;
			public Number parent;
			public String name;

			public Category() {
			}

			public Category(int i) {
				id = i;
			}

			public Number getId() {
				return id;
			}

			public void setId(Number id) {
				this.id = id;
			}

			public Number getParent() {
				return parent;
			}

			public void setParent(Number parent) {
				this.parent = parent;
			}

			public String getName() {
				return name;
			}

			public void setName(String name) {
				this.name = name;
			}

			@Override
			public boolean equals(Object o) {
				if (o == this) {
					return true;
				}

				if (o instanceof Category) {
					return ((Category) o).getId().equals(id);
				}

				return super.equals(o);
			}
		}

	}

	public static class Meta {
		public List<Comment> comments;
		public String description;
		public Developer developer;
		public Likevotes likevotes;
		public String news;
		public String title;
		public String wurl;
		public Flags flags;
		public int downloads;
		public CategoriesJson.Categories categories;

		public List<Comment> getComments() {
			return comments;
		}

		public String getDescription() {
			return description;
		}

		public Developer getDeveloper() {
			return developer;
		}

		public Likevotes getLikevotes() {
			return likevotes;
		}

		public String getNews() {
			return news;
		}

		public String getWurl() {
			return wurl;
		}

		public Flags getFlags() {
			return flags;
		}

		public int getDownloads() {
			return downloads;
		}

		public String getTitle() {
			return title;
		}

		public static class Likevotes {
			public Number dislikes;
			public Number likes;
			public Number rating;
			public String uservote;
		}

		public static class Flags {
			public Votes votes;
			public String uservote;
			public Veredict veredict;
		}

		public static class Veredict {
			public String flag;
			public String review;
		}

		public static class Votes {
			public Number fake;
			public Number freeze;
			public Number good;
			public Number license;
			public Number virus;
		}

		public static class Developer {
			public Info info;
			public List<String> packages;

			public static class Info {
				public String email;
				public String name;
				public String privacy_policy;
				public String website;
			}
		}
	}

	public static class Malware {
		public Reason reason;
		public String status;

		public String getStatus() {
			return status;
		}

		public Reason getReason() {
			return reason;
		}

		public static class Scanned {
			public List<Av_info> av_info;
			public String date;
			public String status;
		}

		public static class Reason {
			public Scanned scanned;
			public Signature_validated signature_validated;
			public Thirdparty_validated thirdparty_validated;
			public Manual_qa manual_qa;

			public static class Signature_validated {
				public String date;
				public String signature_from;
				public String status;
			}

			public static class Thirdparty_validated {
				public String date;
				public String store;
			}

			public static class Manual_qa {
				public String date;
				public String tester;
				public String status;
			}
		}
	}

	public static class Av_info {
		public List<Infection> infections;
		public String name;
	}

	public static class Infection {
		public String description;
		public String name;
	}

	public static class Signature {
		@JsonProperty("SHA1")
		public String SHA1;

		public String getSHA1() {
			return SHA1;
		}
	}

	public static class Apk {
		public String icon;
		public Number id;
		public String md5sum;
		public Number minSdk;
		public String minScreen;
		@JsonProperty("package")
		public String packageName;
		public String path;
		public String altpath;
		public List<String> permissions;
		public String repo;
		public Number size;
		public Number vercode;
		public String vername;
		public String icon_hd;
		private String altPath;

		public String getMd5sum() {
			return md5sum;
		}

		public String getAltPath() {
			return altPath;
		}

		public String getIcon() {
			return icon;
		}

		public Number getId() {
			return id;
		}

		public Number getMinSdk() {
			return minSdk;
		}

		public String getMinScreen() {
			return minScreen;
		}

		public String getPackageName() {
			return packageName;
		}

		public String getPath() {
			return path;
		}

		public String getAltpath() {
			return altpath;
		}

		public List<String> getPermissions() {
			return permissions;
		}

		public String getRepo() {
			return repo;
		}

		public Number getSize() {
			return size;
		}

		public Number getVercode() {
			return vercode;
		}

		public String getIcon_hd() {
			return icon_hd;
		}

		public String getVername() {
			return vername;
		}


	}

	public static class ObbObject {
		public Obb main;
		public Obb patch;
	}
}
