/*
 * Copyright (c) 2017.
 * Modified by Marcelo Benites on 13/02/2017.
 */

package cm.aptoide.pt.v8engine.repository;

import cm.aptoide.pt.model.v3.PaymentServiceResponse;
import cm.aptoide.pt.v8engine.payment.Authorization;
import cm.aptoide.pt.v8engine.payment.Payment;
import cm.aptoide.pt.v8engine.payment.PaymentConfirmation;
import cm.aptoide.pt.v8engine.payment.PaymentFactory;
import cm.aptoide.pt.v8engine.payment.Product;
import cm.aptoide.pt.v8engine.payment.exception.PaymentFailureException;
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

  public PaymentRepository(ProductRepository productRepository,
      PaymentConfirmationRepository confirmationRepository,
      PaymentAuthorizationRepository authorizationRepository,
      PaymentAuthorizationFactory authorizationFactory, PaymentFactory paymentFactory) {
    this.productRepository = productRepository;
    this.confirmationRepository = confirmationRepository;
    this.authorizationRepository = authorizationRepository;
    this.authorizationFactory = authorizationFactory;
    this.paymentFactory = paymentFactory;
  }

  public Observable<List<Payment>> getPayments(String payerId) {
    return productRepository.getPayments()
        .map(payments -> sortedPayments(payments))
        .flatMapObservable(
            payments -> Observable.combineLatest(getAuthorizations(payments, payerId),
                Observable.just(payments), (authorizations, paymentList) -> {
                  if (payments.isEmpty() || authorizations.isEmpty()) {
                    return Observable.<List<Payment>> empty();
                  }
                  return getPaymentsWithAuthorizations(payments, authorizations, payerId);
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

  private Observable<List<Payment>> getPaymentsWithAuthorizations(
      List<PaymentServiceResponse> payments, List<Authorization> authorizations, String payerId) {
    return Observable.zip(Observable.from(payments), Observable.from(authorizations),
        (payment, authorization) -> {
          if (!payment.isAuthorizationRequired()) {
            authorization =
                authorizationFactory.create(payment.getId(), Authorization.Status.NONE, payerId);
          }
          return paymentFactory.create(payment, authorization, confirmationRepository);
        }).toList();
  }

  public Observable<Payment> getPayment(int paymentId, String payerId) {
    return getPayments(payerId).flatMap(payments -> Observable.from(payments)
        .filter(payment -> payment.getId() == paymentId)
        .switchIfEmpty(Observable.error(
            new PaymentFailureException("Payment " + paymentId + "not available"))));
  }

  private Observable<List<Authorization>> getAuthorizations(List<PaymentServiceResponse> payments,
      String payerId) {
    return getPaymentIds(payments).flatMapObservable(
        paymentIds -> authorizationRepository.getPaymentAuthorizations(paymentIds, payerId));
  }

  public Observable<PaymentConfirmation> getConfirmation(Product product, String payerId) {
    return confirmationRepository.getPaymentConfirmation(product, payerId);
  }

  private Single<List<Integer>> getPaymentIds(List<PaymentServiceResponse> payments) {
    return Observable.from(payments).map(payment -> payment.getId()).toList().toSingle();
  }
}
