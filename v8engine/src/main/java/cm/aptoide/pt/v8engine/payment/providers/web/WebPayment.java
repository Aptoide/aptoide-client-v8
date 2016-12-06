/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 14/10/2016.
 */

package cm.aptoide.pt.v8engine.payment.providers.web;

import android.content.Context;
import android.support.annotation.NonNull;
import cm.aptoide.pt.v8engine.payment.BackgroundSync;
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
  private final BackgroundSync backgroundSync;

  public WebPayment(Context context, int id, String type, Product product, Price price,
      String description, PaymentAuthorizationRepository authorizationRepository,
      PaymentConfirmationRepository confirmationRepository, BackgroundSync backgroundSync) {
    super(id, type, product, price, description);
    this.context = context;
    this.authorizationRepository = authorizationRepository;
    this.confirmationRepository = confirmationRepository;
    this.backgroundSync = backgroundSync;
  }

  @Override public Observable<PaymentConfirmation> process() {
    return authorize().flatMap(authorized -> confirmationRepository.createPaymentConfirmation(this));
  }

  private Observable<Void> authorize() {
    return isAuthorized()
        .flatMap(authorized -> {
          if (authorized) {
            return Observable.just(null);
          }
          return createAuthorization();
        })
        .onErrorResumeNext(throwable -> {
          if (throwable instanceof RepositoryItemNotFoundException) {
            return createAuthorization();
          }
          return Observable.error(throwable);
        });
  }

  private Observable<Boolean> isAuthorized() {
    return authorizationRepository.getPaymentAuthorization(getId())
        .distinctUntilChanged(paymentAuthorization -> paymentAuthorization.getStatus())
        .flatMap(authorization -> {
          if (authorization.isAuthorized()) {
            return Observable.just(true);
          } else if (authorization.isPending()) {
            backgroundSync.schedule();
            return Observable.empty();
          } else {
            return Observable.just(false);
          }
        })
        .first();
  }

  private Observable<Void> createAuthorization() {
    return authorizationRepository.createPaymentAuthorization(getId())
        .distinctUntilChanged(paymentAuthorization -> paymentAuthorization.getStatus())
        .flatMap(authorization -> {
          if (authorization.isAuthorized()) {
            return Observable.just(null);
          } else if (authorization.isPending()) {
            backgroundSync.schedule();
            return Observable.empty();
          } else if (authorization.isCancelled()) {
            return Observable.error(new PaymentFailureException("Authorization cancelled."));
          } else {
            backgroundSync.schedule();
            startWebAuthorizationActivity(authorization.getUrl(), authorization.getRedirectUrl());
            return Observable.empty();
          }
        });
  }

  private void startWebAuthorizationActivity(String url, String resultUrl) {
    context.startActivity(WebPaymentActivity.getIntent(context, url, resultUrl));
  }
}