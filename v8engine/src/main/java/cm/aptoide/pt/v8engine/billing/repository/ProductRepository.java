/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 29/11/2016.
 */

package cm.aptoide.pt.v8engine.billing.repository;

import cm.aptoide.pt.dataprovider.model.v3.PaymentServiceResponse;
import cm.aptoide.pt.v8engine.billing.PaymentMethod;
import cm.aptoide.pt.v8engine.billing.PaymentMethodMapper;
import cm.aptoide.pt.v8engine.billing.Product;
import cm.aptoide.pt.v8engine.billing.Purchase;
import java.util.Collections;
import java.util.List;
import rx.Observable;
import rx.Single;

public abstract class ProductRepository {

  private final PaymentMethodMapper paymentMethodMapper;

  protected ProductRepository(PaymentMethodMapper paymentMethodMapper) {
    this.paymentMethodMapper = paymentMethodMapper;
  }

  public abstract Single<Purchase> getPurchase(Product product);

  public abstract Single<List<PaymentMethod>> getPaymentMethods(Product product);

  protected Single<List<PaymentMethod>> convertResponsesToPaymentMethods(
      List<PaymentServiceResponse> payments) {
    return Observable.from((payments == null) ? Collections.emptyList() : payments)
        .flatMap(paymentService -> {
          try {
            return Observable.just(paymentMethodMapper.map(paymentService));
          } catch (IllegalArgumentException ignored) {
            return Observable.empty();
          }
        })
        .toList()
        .toSingle();
  }
}
