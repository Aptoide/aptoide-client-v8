/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 14/10/2016.
 */

package cm.aptoide.pt.v8engine.payment.providers.boacompra;

import cm.aptoide.pt.v8engine.payment.Payment;
import cm.aptoide.pt.v8engine.payment.PaymentConfirmation;
import cm.aptoide.pt.v8engine.payment.Price;
import cm.aptoide.pt.v8engine.payment.Product;
import cm.aptoide.pt.v8engine.payment.UserAuthorization;
import rx.Observable;

/**
 * Created by marcelobenites on 14/10/16.
 */

public class BoaCompraPayment implements Payment {

  private final BoaCompraApi api;
  private final BoaCompraAuthorization authorization;
  private final UserAuthorization userAuthorization;

  public BoaCompraPayment(String apiUrl, BoaCompraAuthorization authorization,
      UserAuthorization userAuthorization) {
    this.userAuthorization = userAuthorization;
    this.api =  BoaCompraApiFactory.create(apiUrl, authorization);
    this.authorization = authorization;
  }

  @Override public int getId() {
    return 0;
  }

  @Override public String getType() {
    return null;
  }

  @Override public Product getProduct() {
    return null;
  }

  @Override public Price getPrice() {
    return null;
  }

  @Override public String getDescription() {
    return null;
  }

  @Override public Observable<PaymentConfirmation> process() {
    return null;
  }
}
