/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 29/11/2016.
 */

package cm.aptoide.pt.v8engine.billing.repository;

import android.content.Context;
import cm.aptoide.pt.model.v3.PaymentServiceResponse;
import cm.aptoide.pt.v8engine.billing.Payer;
import cm.aptoide.pt.v8engine.billing.Payment;
import cm.aptoide.pt.v8engine.billing.Product;
import cm.aptoide.pt.v8engine.billing.Purchase;
import java.util.List;
import rx.Observable;
import rx.Single;

public abstract class ProductRepository {

  private final PaymentFactory paymentFactory;
  private final PaymentAuthorizationRepository authorizationRepository;
  private final PaymentConfirmationRepository confirmationRepository;
  private final Payer payer;
  private final PaymentAuthorizationFactory authorizationFactory;

  protected ProductRepository(PaymentFactory paymentFactory,
      PaymentAuthorizationRepository authorizationRepository,
      PaymentConfirmationRepository confirmationRepository, Payer payer,
      PaymentAuthorizationFactory authorizationFactory) {
    this.paymentFactory = paymentFactory;
    this.authorizationRepository = authorizationRepository;
    this.confirmationRepository = confirmationRepository;
    this.payer = payer;
    this.authorizationFactory = authorizationFactory;
  }

  public abstract Single<Purchase> getPurchase(Product product);

  public abstract Single<List<Payment>> getPayments(Context context, Product product);

  protected Single<List<Payment>> convertResponseToPayment(Context context,
      List<PaymentServiceResponse> payments) {
    return Observable.from(payments)
        .map(paymentService -> paymentFactory.create(context, paymentService))
        .toList()
        .toSingle();
  }
}
