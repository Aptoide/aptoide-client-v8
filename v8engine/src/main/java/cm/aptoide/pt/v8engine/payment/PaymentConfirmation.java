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
	private final Product product;

	public PaymentConfirmation(String paymentConfirmationId, Product product) {
		this.paymentConfirmationId = paymentConfirmationId;
		this.product = product;
	}

	protected PaymentConfirmation(Parcel in) {
		paymentConfirmationId = in.readString();
		product = in.readParcelable(Product.class.getClassLoader());
	}

	public Product getProduct() {
		return product;
	}

	public String getPaymentConfirmationId() {
		return paymentConfirmationId;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(paymentConfirmationId);
		dest.writeParcelable(product, flags);
	}

	@Override
	public int describeContents() {
		return 0;
	}
}