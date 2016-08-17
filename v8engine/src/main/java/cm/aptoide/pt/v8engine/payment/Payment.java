/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 10/08/2016.
 */

package cm.aptoide.pt.v8engine.payment;

import android.os.Parcelable;
import android.support.annotation.DrawableRes;

import com.fasterxml.jackson.annotation.JsonProperty;

import cm.aptoide.pt.v8engine.payment.exception.PaymentException;

/**
 * Created by marcelobenites on 8/10/16.
 */
public interface Payment {

	int getId();

	@DrawableRes int getIcon();

	double getPrice();

	String getCurrency();

	double getTaxRate();

	void cancel();

	boolean isProcessing();

	void process(Product product, PaymentConfirmationListener listener);

	static interface PaymentConfirmationListener {

		void onSuccess(PaymentConfirmation paymentConfirmation);

		void onError(PaymentException exception);
	}
}