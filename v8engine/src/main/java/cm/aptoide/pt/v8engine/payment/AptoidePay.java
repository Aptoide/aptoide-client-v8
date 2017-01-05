/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 12/08/2016.
 */

package cm.aptoide.pt.v8engine.payment;

import android.content.Context;
import cm.aptoide.pt.v8engine.payment.authorizations.WebAuthorization;
import cm.aptoide.pt.v8engine.payment.authorizations.WebAuthorizationActivity;
import cm.aptoide.pt.v8engine.payment.exception.PaymentCancellationException;
import cm.aptoide.pt.v8engine.payment.products.AptoideProduct;
import cm.aptoide.pt.v8engine.repository.PaymentAuthorizationRepository;
import cm.aptoide.pt.v8engine.repository.PaymentConfirmationRepository;
import cm.aptoide.pt.v8engine.repository.ProductRepository;
import java.util.List;
import rx.Completable;
import rx.Observable;
import rx.Single;

/**
 * Created by marcelobenites on 8/12/16.
 */
public class AptoidePay {

  private final PaymentConfirmationRepository confirmationRepository;
  private final PaymentAuthorizationRepository authorizationRepository;
  private ProductRepository productRepository;

  public AptoidePay(PaymentConfirmationRepository confirmationRepository,
      PaymentAuthorizationRepository authorizationRepository, ProductRepository productRepository) {
    this.confirmationRepository = confirmationRepository;
    this.authorizationRepository = authorizationRepository;
    this.productRepository = productRepository;
  }

  public Observable<List<Payment>> availablePayments(Context context, AptoideProduct product) {
    return productRepository.getPayments(context, product)
        .flatMapObservable(payments -> authorizationUpdates(payments).flatMap(
            updatedPayment -> updatePayments(payments, updatedPayment)));
  }

  public Completable authorize(Context context, Payment payment) {
    return authorizationRepository.getPaymentAuthorization(payment.getId())
        .distinctUntilChanged(authorization -> authorization.getStatus())
        .flatMap(authorization -> {

          if (authorization.isAuthorized()) {
            return Observable.just(authorization);
          }

          if (authorization.isPending()) {
            return Observable.empty();
          }

          if (authorization.isInvalid()) {
            return authorizationRepository.createPaymentAuthorization(payment.getId())
                .andThen(Observable.empty());
          }

          if (authorization instanceof WebAuthorization) {
            context.startActivity(WebAuthorizationActivity.getIntent(context,
                ((WebAuthorization) authorization).getUrl(),
                ((WebAuthorization) authorization).getRedirectUrl()));
            return Observable.empty();
          }
          return Observable.error(new PaymentCancellationException("Invalid authorization"));
        })
        .first()
        .toCompletable();
  }

  public Observable<Purchase> getPurchase(AptoideProduct product) {
    return confirmationRepository.getPaymentConfirmation(product)
        .distinctUntilChanged(paymentConfirmation -> paymentConfirmation.getStatus())
        .first(paymentConfirmation -> paymentConfirmation.isCompleted()
            || paymentConfirmation.isFailed())
        .flatMap(paymentConfirmation -> {
          if (paymentConfirmation.isFailed()) {
            return Observable.empty();
          }
          return productRepository.getPurchase(product).toObservable();
        });
  }

  public Single<Purchase> process(Payment payment) {
    return payment.process()
        .andThen(productRepository.getPurchase((AptoideProduct) payment.getProduct()));
  }

  private Observable<List<Payment>> updatePayments(List<Payment> payments, Payment updatedPayment) {
    return Observable.from(payments)
        .filter(oldPayment -> oldPayment.getId() != updatedPayment.getId()
            && (!oldPayment.isAuthorizationRequired() || (oldPayment.isAuthorizationRequired()
            && oldPayment.getAuthorization() != null)))
        .toList()
        .map(oldPayments -> {
          oldPayments.add(updatedPayment);
          return oldPayments;
        });
  }

  private Observable<Payment> authorizationUpdates(List<Payment> payments) {
    return Observable.from(payments)
        .map(payment -> addAuthorization(payment))
        .toList()
        .flatMap(observables -> Observable.merge(observables));
  }

  private Observable<Payment> addAuthorization(Payment payment) {

    if (!payment.isAuthorizationRequired()) {
      return Observable.just(payment);
    }

    return authorizationRepository.getPaymentAuthorization(payment.getId()).map(authorization -> {
      payment.setAuthorization(authorization);
      return payment;
    });
  }
}