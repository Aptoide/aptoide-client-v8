/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 14/10/2016.
 */

package cm.aptoide.pt.v8engine.payment.providers.web;

import android.content.Context;
import cm.aptoide.pt.v8engine.payment.BackgroundSync;
import cm.aptoide.pt.v8engine.payment.Payment;
import cm.aptoide.pt.v8engine.payment.PaymentConfirmation;
import cm.aptoide.pt.v8engine.payment.Price;
import cm.aptoide.pt.v8engine.payment.Product;
import cm.aptoide.pt.v8engine.repository.PaymentAuthorizationRepository;
import cm.aptoide.pt.v8engine.repository.PaymentConfirmationRepository;
import rx.Observable;

/**
 * Created by marcelobenites on 14/10/16.
 */
public class WebPayment implements Payment {

  private final Context context;
  private final int id;
  private final String type;
  private final Product product;
  private final Price price;
  private final String description;
  private final PaymentAuthorizationRepository authorizationRepository;
  private final PaymentConfirmationRepository confirmationRepository;
  private final BackgroundSync backgroundSync;

  public WebPayment(Context context, int id, String type, Product product, Price price,
      String description, PaymentAuthorizationRepository authorizationRepository,
      PaymentConfirmationRepository confirmationRepository, BackgroundSync backgroundSync) {
    this.context = context;
    this.id = id;
    this.type = type;
    this.product = product;
    this.price = price;
    this.description = description;
    this.authorizationRepository = authorizationRepository;
    this.confirmationRepository = confirmationRepository;
    this.backgroundSync = backgroundSync;
  }

  @Override public int getId() {
    return id;
  }

  @Override public String getType() {
    return type;
  }

  @Override public Product getProduct() {
    return product;
  }

  @Override public Price getPrice() {
    return price;
  }

  @Override public String getDescription() {
    return description;
  }

  @Override public Observable<PaymentConfirmation> process() {
    return authorizationRepository.getPaymentAuthorization(id).flatMap(authorization -> {
      if (authorization.isAuthorized()) {
        return confirmationRepository.createPaymentConfirmation(this);
      } else if (authorization.isPending()) {
        return Observable.empty();
      } else {
        startWebAuthorizationActivity(authorization.getUrl(), authorization.getRedirectUrl());
        backgroundSync.schedule();
        return Observable.empty();
      }
    });
  }

  private void startWebAuthorizationActivity(String url, String resultUrl) {
    context.startActivity(WebPaymentActivity.getIntent(context, url, resultUrl));
  }
}