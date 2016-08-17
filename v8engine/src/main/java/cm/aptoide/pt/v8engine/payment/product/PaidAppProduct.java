/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 16/08/2016.
 */

package cm.aptoide.pt.v8engine.payment.product;

import android.os.Parcel;

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
	private final String type;
	private final String icon;
	private final double price;
	private final String currency;
	private final double taxRate;
	private final long appId;
	private final String name;
	private final String storeName;

	public PaidAppProduct(int id, String type, String icon, double price, String currency, double taxRate, long appId, String name, String storeName) {
		this.id = id;
		this.type = type;
		this.icon = icon;
		this.price = price;
		this.currency = currency;
		this.taxRate = taxRate;
		this.appId = appId;
		this.name = name;
		this.storeName = storeName;
	}

	protected PaidAppProduct(Parcel in) {
		id = in.readInt();
		type = in.readString();
		icon = in.readString();
		price = in.readDouble();
		currency = in.readString();
		taxRate = in.readDouble();
		appId = in.readLong();
		name = in.readString();
		storeName = in.readString();
	}

	public int getId() {
		return id;
	}

	public String getType() {
		return type;
	}

	public String getIcon() {
		return icon;
	}

	public double getPrice() {
		return price;
	}

	public String getCurrency() {
		return currency;
	}

	public double getTaxRate() {
		return taxRate;
	}

	public long getAppId() {
		return appId;
	}

	@Override
	public String getDescription() {
		return name;
	}

	public String getStoreName() {
		return storeName;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(id);
		dest.writeString(type);
		dest.writeString(icon);
		dest.writeDouble(price);
		dest.writeString(currency);
		dest.writeDouble(taxRate);
		dest.writeLong(appId);
		dest.writeString(name);
		dest.writeString(storeName);
	}

	@Override
	public int describeContents() {
		return 0;
	}
}
