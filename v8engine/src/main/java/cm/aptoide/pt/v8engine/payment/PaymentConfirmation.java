/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 10/08/2016.
 */

package cm.aptoide.pt.v8engine.payment;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by marcelobenites on 8/10/16.
 */
public class PaymentConfirmation implements Parcelable {

	public static final Creator<PaymentConfirmation> CREATOR = new Creator<PaymentConfirmation>() {
		@Override
		public PaymentConfirmation createFromParcel(Parcel in) {
			return new PaymentConfirmation(in);
		}

		@Override
		public PaymentConfirmation[] newArray(int size) {
			return new PaymentConfirmation[size];
		}
	};

	private final String paymentConfirmationId;
	private final int paymentId;
	private final Product product;
	private final Price price;

	public PaymentConfirmation(String paymentConfirmationId, int paymentId, Product product, Price price) {
		this.paymentConfirmationId = paymentConfirmationId;
		this.paymentId = paymentId;
		this.product = product;
		this.price = price;
	}

	protected PaymentConfirmation(Parcel in) {
		paymentConfirmationId = in.readString();
		paymentId = in.readInt();
		product = in.readParcelable(Product.class.getClassLoader());
		price = in.readParcelable(Price.class.getClassLoader());
	}

	public Product getProduct() {
		return product;
	}

	public String getPaymentConfirmationId() {
		return paymentConfirmationId;
	}

	public int getPaymentId() {
		return paymentId;
	}

	public Price getPrice() {
		return price;
	}

	public static Creator<PaymentConfirmation> getCREATOR() {
		return CREATOR;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(paymentConfirmationId);
		dest.writeInt(paymentId);
		dest.writeParcelable(product, flags);
		dest.writeParcelable(price, flags);
	}

	@Override
	public int describeContents() {
		return 0;
	}
}