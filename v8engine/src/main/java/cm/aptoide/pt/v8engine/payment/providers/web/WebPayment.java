/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 14/10/2016.
 */

package cm.aptoide.pt.v8engine.payment.providers.web;

import android.content.Context;
import cm.aptoide.pt.v8engine.payment.Price;
import cm.aptoide.pt.v8engine.payment.Product;
import cm.aptoide.pt.v8engine.payment.exception.PaymentFailureException;
import cm.aptoide.pt.v8engine.payment.providers.AbstractPayment;
import cm.aptoide.pt.v8engine.repository.PaymentAuthorizationRepository;
import cm.aptoide.pt.v8engine.repository.PaymentConfirmationRepository;
import rx.Completable;
import rx.Observable;

/**
 * Created by marcelobenites on 14/10/16.
 */
public class WebPayment extends AbstractPayment {

  private final Context context;
  private final PaymentAuthorizationRepository authorizationRepository;

  public WebPayment(Context context, int id, String type, Product product, Price price,
      String description, PaymentAuthorizationRepository authorizationRepository,
      PaymentConfirmationRepository confirmationRepository) {
    super(id, type, product, price, description, confirmationRepository);
    this.context = context;
    this.authorizationRepository = authorizationRepository;
  }

  @Override public Completable process() {
    return authorize(getId()).andThen(super.process());
  }

  private Completable authorize(int paymentId) {
    return authorizationRepository.getPaymentAuthorization(paymentId)
        .distinctUntilChanged(paymentAuthorization -> paymentAuthorization.getStatus())
        .takeUntil(paymentAuthorization -> paymentAuthorization.isAuthorized())
        .flatMap(authorization -> {
          if (authorization.isInvalid()) {
            return authorizationRepository.createPaymentAuthorization(getId()).toObservable();
          } else if (authorization.isAuthorized()) {
            return Observable.just(null);
          } else if (authorization.isPending()) {
            return Observable.empty();
          } else {
            return startWebAuthorizationActivity(authorization.getUrl(),
                authorization.getRedirectUrl()).toObservable();
          }
        })
        .toCompletable();
  }

  private Completable startWebAuthorizationActivity(String url, String resultUrl) {
    return Completable.fromAction(
        () -> context.startActivity(WebPaymentActivity.getIntent(context, url, resultUrl)));
  }
}