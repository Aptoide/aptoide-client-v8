/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 16/08/2016.
 */

package cm.aptoide.pt.v8engine.payment;

import android.content.Context;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cm.aptoide.pt.v8engine.payment.Payment;
import cm.aptoide.pt.v8engine.payment.Product;
import rx.Observable;

/**
 * Created by marcelobenites on 8/16/16.
 */
public interface PaymentRepository {

	public Observable<List<Payment>> getProductPayments(Context context, Product product);

}