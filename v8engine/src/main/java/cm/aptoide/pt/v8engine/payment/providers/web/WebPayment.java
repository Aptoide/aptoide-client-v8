/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 14/10/2016.
 */

package cm.aptoide.pt.v8engine.payment.providers.web;

import android.content.Context;
import cm.aptoide.pt.v8engine.payment.Payment;
import cm.aptoide.pt.v8engine.payment.PaymentAuthorization;
import cm.aptoide.pt.v8engine.payment.PaymentConfirmation;
import cm.aptoide.pt.v8engine.payment.Price;
import cm.aptoide.pt.v8engine.payment.Product;
import cm.aptoide.pt.v8engine.repository.PaymentRepository;
import cm.aptoide.pt.v8engine.repository.exception.RepositoryItemNotFoundException;
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
  private PaymentRepository paymentRepository;

  public WebPayment(Context context, int id, String type, Product product, Price price,
      String description, PaymentRepository paymentRepository) {
    this.context = context;
    this.id = id;
    this.type = type;
    this.product = product;
    this.price = price;
    this.description = description;
    this.paymentRepository = paymentRepository;
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
    return getOrCreateAuthorization().flatMap(authorization -> {
      if (authorization.isAuthorized()) {
        return paymentRepository.createPaymentConfirmation(this);
      }
      startWebAuthorizationActivity(authorization.getUrl(), authorization.getRedirectUrl());
      return Observable.empty();
    });
  }

  private Observable<PaymentAuthorization> getOrCreateAuthorization() {
    return paymentRepository.getPaymentAuthorization(id).onErrorResumeNext(throwable -> {
      if (throwable instanceof RepositoryItemNotFoundException) {
        return paymentRepository.createPaymentAuthorization(id)
        .flatMap(paymentAuthorization -> paymentRepository.savePaymentAuthorization(paymentAuthorization))
        .flatMap(success -> paymentRepository.getPaymentAuthorization(id));
      }
      return Observable.<PaymentAuthorization>error(throwable);
    });
  }

  private void startWebAuthorizationActivity(String url, String resultUrl) {
    context.startActivity(WebPaymentActivity.getIntent(context, url, resultUrl));
  }
}