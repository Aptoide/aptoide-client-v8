/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 16/08/2016.
 */

package cm.aptoide.pt.v8engine.payment.product;

import android.os.Parcel;

/**
 * Created by marcelobenites on 8/16/16.
 */
public class InAppBillingProduct extends AptoideProduct {

	public static final Creator<InAppBillingProduct> CREATOR = new Creator<InAppBillingProduct>() {
		@Override
		public InAppBillingProduct createFromParcel(Parcel in) {
			return new InAppBillingProduct(in);
		}

		@Override
		public InAppBillingProduct[] newArray(int size) {
			return new InAppBillingProduct[size];
		}
	};

	private final int apiVersion;
	private final String sku;
	private final String packageName;
	private final String developerPayload;
	private final String type;

	public InAppBillingProduct(int id, String icon, String title, String description, String price, int apiVersion, String sku, String packageName,
	                           String developerPayload, String type) {
		super(id, icon, title, description, price);
		this.apiVersion = apiVersion;
		this.sku = sku;
		this.packageName = packageName;
		this.developerPayload = developerPayload;
		this.type = type;
	}

	protected InAppBillingProduct(Parcel in) {
		super(in);
		apiVersion = in.readInt();
		sku = in.readString();
		packageName = in.readString();
		developerPayload = in.readString();
		type = in.readString();
	}

	public int getApiVersion() {
		return apiVersion;
	}

	public String getSku() {
		return sku;
	}

	public String getPackageName() {
		return packageName;
	}

	public String getDeveloperPayload() {
		return developerPayload;
	}

	public String getType() {
		return type;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		super.writeToParcel(dest,flags);
		dest.writeInt(apiVersion);
		dest.writeString(sku);
		dest.writeString(packageName);
		dest.writeString(developerPayload);
		dest.writeString(type);
	}
}
