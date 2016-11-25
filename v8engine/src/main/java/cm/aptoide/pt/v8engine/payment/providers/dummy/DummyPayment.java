/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 25/11/2016.
 */

package cm.aptoide.pt.v8engine.payment.providers.dummy;

import cm.aptoide.pt.v8engine.payment.PaymentConfirmation;
import cm.aptoide.pt.v8engine.payment.Price;
import cm.aptoide.pt.v8engine.payment.Product;
import cm.aptoide.pt.v8engine.payment.providers.AbstractPayment;
import cm.aptoide.pt.v8engine.repository.PaymentConfirmationRepository;
import rx.Observable;

/**
 * Created by marcelobenites on 25/11/16.
 */

public class DummyPayment extends AbstractPayment {

  private final PaymentConfirmationRepository confirmationRepository;

  public DummyPayment(int id, String type, Product product, Price price, String description,
      PaymentConfirmationRepository confirmationRepository) {
    super(id, type, product, price, description);
    this.confirmationRepository = confirmationRepository;
  }

  @Override public Observable<PaymentConfirmation> process() {
    return confirmationRepository.createPaymentConfirmation(this);
  }
}
