/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 29/11/2016.
 */

package cm.aptoide.pt.v8engine.repository;

import cm.aptoide.pt.model.v3.PaymentServiceResponse;
import cm.aptoide.pt.v8engine.payment.Payment;
import cm.aptoide.pt.v8engine.payment.Purchase;
import cm.aptoide.pt.v8engine.payment.products.AptoideProduct;
import java.util.List;
import rx.Single;

/**
 * Created by marcelobenites on 29/11/16.
 */
public interface ProductRepository {

  Single<Purchase> getPurchase(AptoideProduct product);

  Single<List<PaymentServiceResponse>> getPayments(AptoideProduct product);
}
