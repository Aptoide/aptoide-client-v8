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
public class PaymentConfirmation {

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

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		final PaymentConfirmation that = (PaymentConfirmation) o;

		if (!paymentConfirmationId.equals(that.paymentConfirmationId)) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		return paymentConfirmationId.hashCode();
	}

}