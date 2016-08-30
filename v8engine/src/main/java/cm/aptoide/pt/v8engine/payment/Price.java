/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 18/08/2016.
 */

package cm.aptoide.pt.v8engine.payment;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by marcelobenites on 8/18/16.
 */
public class Price implements Parcelable {

	public static final Creator<Price> CREATOR = new Creator<Price>() {
		@Override
		public Price createFromParcel(Parcel in) {
			return new Price(in);
		}

		@Override
		public Price[] newArray(int size) {
			return new Price[size];
		}
	};

	private final double price;
	private final String currency;
	private final double taxRate;

	public Price(double price, String currency, double taxRate) {
		this.price = price;
		this.currency = currency;
		this.taxRate = taxRate;
	}

	protected Price(Parcel in) {
		price = in.readDouble();
		currency = in.readString();
		taxRate = in.readDouble();
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

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {

		dest.writeDouble(price);
		dest.writeString(currency);
		dest.writeDouble(taxRate);
	}
}
