/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 14/10/2016.
 */

package cm.aptoide.pt.v8engine.payment.providers.web;

import android.content.Context;
import cm.aptoide.pt.v8engine.payment.PaymentConfirmation;
import cm.aptoide.pt.v8engine.payment.Price;
import cm.aptoide.pt.v8engine.payment.Product;
import cm.aptoide.pt.v8engine.payment.exception.PaymentFailureException;
import cm.aptoide.pt.v8engine.payment.providers.AbstractPayment;
import cm.aptoide.pt.v8engine.repository.PaymentAuthorizationRepository;
import cm.aptoide.pt.v8engine.repository.PaymentConfirmationRepository;
import cm.aptoide.pt.v8engine.repository.exception.RepositoryItemNotFoundException;
import rx.Observable;

/**
 * Created by marcelobenites on 14/10/16.
 */
public class WebPayment extends AbstractPayment {

  private final Context context;
  private final PaymentAuthorizationRepository authorizationRepository;
  private final PaymentConfirmationRepository confirmationRepository;

  public WebPayment(Context context, int id, String type, Product product, Price price,
      String description, PaymentAuthorizationRepository authorizationRepository,
      PaymentConfirmationRepository confirmationRepository) {
    super(id, type, product, price, description);
    this.context = context;
    this.authorizationRepository = authorizationRepository;
    this.confirmationRepository = confirmationRepository;
  }

  @Override public Observable<PaymentConfirmation> process() {
    return authorize(getId()).flatMap(authorized -> confirmationRepository.createPaymentConfirmation(getId()));
  }

  private Observable<Void> authorize(int paymentId) {
    return isAuthorized(paymentId)
        .flatMap(authorized -> {
          if (authorized) {
            return Observable.just(null);
          }
          return createAuthorization(paymentId);
        })
        .onErrorResumeNext(throwable -> {
          if (throwable instanceof RepositoryItemNotFoundException) {
            return createAuthorization(paymentId);
          }
          return Observable.error(throwable);
        });
  }

  private Observable<Boolean> isAuthorized(int paymentId) {
    return authorizationRepository.getPaymentAuthorization(paymentId)
        .distinctUntilChanged(paymentAuthorization -> paymentAuthorization.getStatus())
        .flatMap(authorization -> {
          if (authorization.isAuthorized()) {
            return Observable.just(true);
          } else if (authorization.isPending()) {
            return Observable.empty();
          } else {
            return Observable.just(false);
          }
        })
        .first();
  }

  private Observable<Void> createAuthorization(int paymentId) {
    return authorizationRepository.createPaymentAuthorization(getId())
        .distinctUntilChanged(paymentAuthorization -> paymentAuthorization.getStatus())
        .flatMap(authorization -> {
          if (authorization.isAuthorized()) {
            return Observable.just(null);
          } else if (authorization.isPending()) {
            return Observable.empty();
          } else if (authorization.isCancelled()) {
            return Observable.error(new PaymentFailureException("Authorization cancelled."));
          } else {
            startWebAuthorizationActivity(authorization.getUrl(), authorization.getRedirectUrl());
            return Observable.empty();
          }
        });
  }

  private void startWebAuthorizationActivity(String url, String resultUrl) {
    context.startActivity(WebPaymentActivity.getIntent(context, url, resultUrl));
  }
}