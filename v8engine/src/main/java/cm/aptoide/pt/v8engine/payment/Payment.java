/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 10/08/2016.
 */

package cm.aptoide.pt.v8engine.payment;

import android.os.Parcel;
import android.os.Parcelable;

import java.math.BigDecimal;

/**
 * Created by marcelobenites on 8/10/16.
 */
public class Payment {

	private final String paymentId;
	private final String currency;
	private final BigDecimal price;
	private final double taxRate;

	public Payment(String paymentId, String currency, BigDecimal price, double taxRate) {
		this.paymentId = paymentId;
		this.currency = currency;
		this.price = price;
		this.taxRate = taxRate;
	}

	public String getPaymentId() {
		return paymentId;
	}

	public String getCurrency() {
		return currency;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public double getTaxRate() {
		return taxRate;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		final Payment payment = (Payment) o;

		if (!paymentId.equals(payment.paymentId)) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		return paymentId.hashCode();
	}

}