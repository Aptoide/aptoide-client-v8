/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 30/08/2016.
 */

package cm.aptoide.pt.v8engine.payment.product;

import android.os.Parcel;
import android.os.Parcelable;

import cm.aptoide.pt.v8engine.payment.Product;

/**
 * Created by marcelobenites on 8/30/16.
 */
public abstract class AptoideProduct implements Product, Parcelable {

	private final int id;
	private final String icon;
	private final String title;
	private final String description;
	private final String price;

	public AptoideProduct(int id, String icon, String title, String description, String price) {
		this.id = id;
		this.icon = icon;
		this.title = title;
		this.description = description;
		this.price = price;
	}

	protected AptoideProduct(Parcel in) {
		id = in.readInt();
		icon = in.readString();
		title = in.readString();
		description = in.readString();
		price = in.readString();
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

	@Override
	public String getPriceDescription() {
		return price;
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
	}
}
