/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 29/11/2016.
 */

package cm.aptoide.pt.v8engine.payment;

import cm.aptoide.pt.model.v3.PaymentServiceResponse;
import cm.aptoide.pt.v8engine.payment.repository.PaymentAuthorizationFactory;
import cm.aptoide.pt.v8engine.payment.repository.PaymentAuthorizationRepository;
import cm.aptoide.pt.v8engine.payment.repository.PaymentConfirmationRepository;
import java.util.List;
import rx.Observable;
import rx.Single;

/**
 * Created by marcelobenites on 29/11/16.
 */
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

  public abstract Single<List<Payment>> getPayments();

  protected Single<List<Payment>> convertResponseToPayment(List<PaymentServiceResponse> payments) {
    return Observable.from(payments)
    .map(paymentService -> paymentFactory.create(paymentService))
    .toList().toSingle();
  }
}
