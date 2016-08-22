/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 10/08/2016.
 */

package cm.aptoide.pt.v8engine.payment;

import android.os.Parcelable;
import android.support.annotation.DrawableRes;
import android.support.annotation.LayoutRes;

import com.fasterxml.jackson.annotation.JsonProperty;

import cm.aptoide.pt.v8engine.payment.exception.PaymentException;

/**
 * Created by marcelobenites on 8/10/16.
 */
public interface Payment {

	int getId();

	String getType();

	Product getProduct();

	Price getPrice();

	String getDescription();

	void removeListener();

	boolean isProcessing();

	void process(PaymentConfirmationListener listener);

	static interface PaymentConfirmationListener {

		void onSuccess(PaymentConfirmation paymentConfirmation);

		void onError(PaymentException exception);
	}
}