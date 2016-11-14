/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 14/10/2016.
 */

package cm.aptoide.pt.v8engine.payment.providers.boacompra;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.NonNull;
import cm.aptoide.pt.utils.BroadcastRegisterOnSubscribe;
import cm.aptoide.pt.v8engine.payment.Payment;
import cm.aptoide.pt.v8engine.payment.PaymentConfirmation;
import cm.aptoide.pt.v8engine.payment.Price;
import cm.aptoide.pt.v8engine.payment.Product;
import cm.aptoide.pt.v8engine.payment.exception.PaymentCancellationException;
import cm.aptoide.pt.v8engine.payment.exception.PaymentFailureException;
import cm.aptoide.pt.v8engine.repository.PaymentRepository;
import rx.Observable;

/**
 * Created by marcelobenites on 14/10/16.
 */

public class BoaCompraPayment implements Payment {

  private final Context context;
  private final int id;
  private final String type;
  private final Product product;
  private final Price price;
  private final String description;
  private PaymentRepository paymentRepository;

  public BoaCompraPayment(Context context, int id, String type, Product product, Price price,
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
    return Observable.error(new IllegalStateException("Payment not implemented."));
  }

  private Observable<Void> authorize(String url, String resultUrl) {
    final IntentFilter paymentResultFilter = new IntentFilter();
    paymentResultFilter.addAction(BoaCompraAuthorizationActivity.ACTION_AUTHORIZATION_OK);
    paymentResultFilter.addAction(BoaCompraAuthorizationActivity.ACTION_AUTHORIZATION_CANCELLED);
    return Observable.create(
        new BroadcastRegisterOnSubscribe(context, paymentResultFilter, null, null))
        .doOnSubscribe(() -> startBoaCompraAuthorizationActivity(url, resultUrl))
        .filter(intent -> isAuthorizationIntent(intent))
        .flatMap(intent -> convertToAuthorization(intent));
  }

  private boolean isAuthorizationIntent(Intent intent) {
    return intent != null && (BoaCompraAuthorizationActivity.ACTION_AUTHORIZATION_CANCELLED.equals(
        intent.getAction()) || BoaCompraAuthorizationActivity.ACTION_AUTHORIZATION_OK.equals(
        intent.getAction()));
  }

  private void startBoaCompraAuthorizationActivity(String url, String resultUrl) {
    context.startActivity(BoaCompraAuthorizationActivity.getIntent(context, url, resultUrl));
  }

  @NonNull private Observable<Void> convertToAuthorization(Intent intent) {
    switch (intent.getAction()) {
      case BoaCompraAuthorizationActivity.ACTION_AUTHORIZATION_OK:
        return Observable.just(null);
      case BoaCompraAuthorizationActivity.ACTION_AUTHORIZATION_CANCELLED:
        return Observable.error(
            new PaymentCancellationException("BoaCompra authorization cancelled by user"));
      default:
        return Observable.error(new PaymentFailureException(
            "Unexpected authorization intent action " + intent.getAction()));
    }
  }
}
