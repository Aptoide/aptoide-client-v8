/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 29/11/2016.
 */

package cm.aptoide.pt.v8engine.repository;

import android.content.Context;
import cm.aptoide.pt.v8engine.payment.Payment;
import cm.aptoide.pt.v8engine.payment.Purchase;
import cm.aptoide.pt.v8engine.payment.product.AptoideProduct;
import java.util.List;
import rx.Observable;

/**
 * Created by marcelobenites on 29/11/16.
 */
public interface ProductRepository {
  Observable<Purchase> getPurchase(AptoideProduct product);

  Observable<List<Payment>> getPayments(Context context, AptoideProduct product);
}
