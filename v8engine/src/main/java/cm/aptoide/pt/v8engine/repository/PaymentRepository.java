/*
 * Copyright (c) 2017.
 * Modified by Marcelo Benites on 13/02/2017.
 */

package cm.aptoide.pt.v8engine.repository;

import cm.aptoide.pt.model.v3.PaymentServiceResponse;
import cm.aptoide.pt.v8engine.payment.Authorization;
import cm.aptoide.pt.v8engine.payment.Payer;
import cm.aptoide.pt.v8engine.payment.Payment;
import cm.aptoide.pt.v8engine.payment.PaymentConfirmation;
import cm.aptoide.pt.v8engine.payment.PaymentFactory;
import cm.aptoide.pt.v8engine.payment.products.AptoideProduct;
import java.util.List;
import rx.Observable;
import rx.Single;

/**
 * Created by marcelobenites on 13/02/17.
 */

public class PaymentRepository {

  private final ProductRepository productRepository;
  private final PaymentConfirmationRepository confirmationRepository;
  private final PaymentAuthorizationRepository authorizationRepository;
  private final PaymentAuthorizationFactory authorizationFactory;
  private final PaymentFactory paymentFactory;
  private final Payer payer;

  public PaymentRepository(ProductRepository productRepository,
      PaymentConfirmationRepository confirmationRepository,
      PaymentAuthorizationRepository authorizationRepository,
      PaymentAuthorizationFactory authorizationFactory, PaymentFactory paymentFactory, Payer payer) {
    this.productRepository = productRepository;
    this.confirmationRepository = confirmationRepository;
    this.authorizationRepository = authorizationRepository;
    this.authorizationFactory = authorizationFactory;
    this.paymentFactory = paymentFactory;
    this.payer = payer;
  }

  public Observable<List<Payment>> getPayments(AptoideProduct product) {
    return productRepository.getPayments(product)
        .flatMapObservable(payments -> Observable.combineLatest(getConfirmations(product, payments),
        getAuthorizations(payments, payer.getId()), Observable.just(payments),
        (confirmations, authorizations, paymentList) -> {
          if (payments.isEmpty() || authorizations.isEmpty() || confirmations.isEmpty()) {
            return Observable.<List<Payment>>empty();
          }
          return getCompletePayments(payments, authorizations, payer.getId(), confirmations,
              product);
        }))
        .flatMap(result -> result);
  }

  private Observable<List<Payment>> getCompletePayments(List<PaymentServiceResponse> payments,
      List<Authorization> authorizations, String payerId, List<PaymentConfirmation> confirmations,
      AptoideProduct product) {
    return Observable.zip(Observable.from(payments), Observable.from(authorizations),
        Observable.from(confirmations), (payment, authorization, confirmation) -> {
          if (!payment.isAuthorizationRequired()) {
            authorization = authorizationFactory.create(payment.getId(), Authorization.Status.NONE, payerId);
          }
          return paymentFactory.create(payment, product, authorization, confirmation);
        }).toList();
  }

  private Observable<List<Authorization>> getAuthorizations(List<PaymentServiceResponse> payments,
      String payerId) {
    return getPaymentIds(payments).flatMapObservable(
        paymentIds -> authorizationRepository.getPaymentAuthorizations(paymentIds, payerId));
  }

  private Observable<List<PaymentConfirmation>> getConfirmations(AptoideProduct product,
      List<PaymentServiceResponse> payments) {
    return getPaymentIds(payments).flatMapObservable(
        paymentIds -> confirmationRepository.getPaymentConfirmations(product, payer.getId(),
            paymentIds));
  }

  private Single<List<Integer>> getPaymentIds(List<PaymentServiceResponse> payments) {
    return Observable.from(payments).map(payment -> payment.getId()).toList().toSingle();
  }


}
