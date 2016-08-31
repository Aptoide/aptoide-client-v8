/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 16/08/2016.
 */

package cm.aptoide.pt.v8engine.payment.product;

import android.os.Parcel;

/**
 * Created by marcelobenites on 8/16/16.
 */
public class PaidAppProduct extends AptoideProduct {

	public static final Creator<PaidAppProduct> CREATOR = new Creator<PaidAppProduct>() {
		@Override
		public PaidAppProduct createFromParcel(Parcel in) {
			return new PaidAppProduct(in);
		}

		@Override
		public PaidAppProduct[] newArray(int size) {
			return new PaidAppProduct[size];
		}
	};

	private final long appId;
	private final String storeName;

	public PaidAppProduct(int id, String icon, String title, String description, String price, long appId, String storeName) {
		super(id, icon, title, description, price);
		this.appId = appId;
		this.storeName = storeName;
	}

	protected PaidAppProduct(Parcel in) {
		super(in);
		appId = in.readLong();
		storeName = in.readString();
	}

	public long getAppId() {
		return appId;
	}

	public String getStoreName() {
		return storeName;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		super.writeToParcel(dest, flags);
		dest.writeLong(appId);
		dest.writeString(storeName);
	}
}
