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
public class InAppBillingProduct implements Product {

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

	private final int id;
	private final String type;
	private final String icon;
	private final double price;
	private final String currency;
	private final double taxRate;

	private final String developerPayload;
	private final int apiVersion;
	private final String packageName;
	private final String description;

	public InAppBillingProduct(int id, String type, String icon, double price, String currency, double taxRate, String developerPayload, int apiVersion, String packageName, String sku) {
		this.id = id;
		this.type = type;
		this.icon = icon;
		this.price = price;
		this.currency = currency;
		this.taxRate = taxRate;
		this.developerPayload = developerPayload;
		this.apiVersion = apiVersion;
		this.packageName = packageName;
		this.description = sku;
	}

	protected InAppBillingProduct(Parcel in) {
		id = in.readInt();
		type = in.readString();
		icon = in.readString();
		price = in.readDouble();
		currency = in.readString();
		taxRate = in.readDouble();
		developerPayload = in.readString();
		apiVersion = in.readInt();
		packageName = in.readString();
		description = in.readString();
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

	public String getDeveloperPayload() {
		return developerPayload;
	}

	public int getApiVersion() {
		return apiVersion;
	}

	public String getPackageName() {
		return packageName;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(id);
		dest.writeString(type);
		dest.writeString(icon);
		dest.writeDouble(price);
		dest.writeString(currency);
		dest.writeDouble(taxRate);
		dest.writeString(developerPayload);
		dest.writeInt(apiVersion);
		dest.writeString(packageName);
		dest.writeString(description);
	}

	@Override
	public int describeContents() {
		return 0;
	}
}
