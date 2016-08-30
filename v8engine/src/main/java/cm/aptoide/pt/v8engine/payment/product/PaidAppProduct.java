/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 16/08/2016.
 */

package cm.aptoide.pt.v8engine.payment.product;

import android.os.Parcel;
import android.os.Parcelable;

import cm.aptoide.pt.v8engine.payment.Product;

/**
 * Created by marcelobenites on 8/16/16.
 */
public class PaidAppProduct implements Product {

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

	private final int id;
	private final String icon;
	private final String title;
	private final String description;
	private final String price;

	private final long appId;
	private final String storeName;

	public PaidAppProduct(int id, String icon, String title, String description, String price, long appId, String storeName) {
		this.id = id;
		this.icon = icon;
		this.title = title;
		this.description = description;
		this.price = price;
		this.appId = appId;
		this.storeName = storeName;
	}

	protected PaidAppProduct(Parcel in) {
		id = in.readInt();
		icon = in.readString();
		title = in.readString();
		description = in.readString();
		price = in.readString();
		appId = in.readLong();
		storeName = in.readString();
	}

	@Override
	public int getId() {
		return id;
	}

	@Override
	public String getIcon() {
		return icon;
	}

	@Override
	public String getTitle() {
		return title;
	}

	@Override
	public String getDescription() {
		return description;
	}

	public String getPriceDescription() {
		return price;
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

		dest.writeInt(id);
		dest.writeString(icon);
		dest.writeString(title);
		dest.writeString(description);
		dest.writeString(price);
		dest.writeLong(appId);
		dest.writeString(storeName);
	}
}
