/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 25/11/2016.
 */

package cm.aptoide.pt.v8engine.payment.providers.dummy;

import cm.aptoide.pt.v8engine.payment.Price;
import cm.aptoide.pt.v8engine.payment.Product;
import cm.aptoide.pt.v8engine.payment.providers.AbstractPayment;
import cm.aptoide.pt.v8engine.repository.PaymentConfirmationRepository;
import rx.Completable;

/**
 * Created by marcelobenites on 25/11/16.
 */

public class DummyPayment extends AbstractPayment {

  public DummyPayment(int id, String type, Product product, Price price, String description,
      PaymentConfirmationRepository confirmationRepository) {
    super(id, type, product, price, description, confirmationRepository);
  }
}
