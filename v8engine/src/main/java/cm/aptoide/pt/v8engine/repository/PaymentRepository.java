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
import cm.aptoide.pt.v8engine.payment.exception.PaymentFailureException;
import cm.aptoide.pt.v8engine.payment.products.AptoideProduct;
import java.util.Collections;
import java.util.Comparator;
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
      PaymentAuthorizationFactory authorizationFactory, PaymentFactory paymentFactory,
      Payer payer) {
    this.productRepository = productRepository;
    this.confirmationRepository = confirmationRepository;
    this.authorizationRepository = authorizationRepository;
    this.authorizationFactory = authorizationFactory;
    this.paymentFactory = paymentFactory;
    this.payer = payer;
  }

  public Observable<List<Payment>> getPayments(AptoideProduct product) {
    return productRepository.getPayments(product)
        .map(payments -> sortedPayments(payments))
        .flatMapObservable(payments -> Observable.combineLatest(getConfirmation(product),
            getAuthorizations(payments, payer.getId()), Observable.just(payments),
            (confirmation, authorizations, paymentList) -> {
              if (payments.isEmpty() || authorizations.isEmpty()) {
                return Observable.<List<Payment>>empty();
              }
              return getCompletePayments(payments, authorizations, payer.getId(), confirmation,
                  product);
            }))
        .flatMap(result -> result);
  }

  private List<PaymentServiceResponse> sortedPayments(List<PaymentServiceResponse> payments) {
    Collections.sort(payments, new Comparator<PaymentServiceResponse>() {
      @Override public int compare(PaymentServiceResponse x, PaymentServiceResponse y) {
        return (x.getId() < y.getId()) ? -1 : ((x.getId() == y.getId()) ? 0 : 1);
      }
    });
    return payments;
  }

  private Observable<List<Payment>> getCompletePayments(List<PaymentServiceResponse> payments,
      List<Authorization> authorizations, String payerId, PaymentConfirmation confirmation,
      AptoideProduct product) {
    return Observable.zip(Observable.from(payments), Observable.from(authorizations),
        (payment, authorization) -> {
          if (!payment.isAuthorizationRequired()) {
            authorization =
                authorizationFactory.create(payment.getId(), Authorization.Status.NONE, payerId);
          }
          return paymentFactory.create(payment, product, authorization, confirmation);
        }).toList();
  }

  public Observable<Payment> getPayment(int paymentId, AptoideProduct product) {
    return getPayments(product).flatMap(payments -> Observable.from(payments)
        .filter(payment -> payment.getId() == paymentId)
        .switchIfEmpty(Observable.error(
            new PaymentFailureException("Payment " + paymentId + "not available"))));
  }

  private Observable<List<Authorization>> getAuthorizations(List<PaymentServiceResponse> payments,
      String payerId) {
    return getPaymentIds(payments).flatMapObservable(
        paymentIds -> authorizationRepository.getPaymentAuthorizations(paymentIds, payerId));
  }

  private Observable<PaymentConfirmation> getConfirmation(AptoideProduct product) {
    return confirmationRepository.getPaymentConfirmation(product, payer.getId());
  }

  private Single<List<Integer>> getPaymentIds(List<PaymentServiceResponse> payments) {
    return Observable.from(payments).map(payment -> payment.getId()).toList().toSingle();
  }
}
