/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 29/11/2016.
 */

package cm.aptoide.pt.v8engine.payment;

import cm.aptoide.pt.model.v3.PaymentServiceResponse;
import java.util.List;
import rx.Single;

/**
 * Created by marcelobenites on 29/11/16.
 */
public interface ProductRepository {

  Single<Purchase> getPurchase(Product product);

  Single<List<PaymentServiceResponse>> getPayments();
}
