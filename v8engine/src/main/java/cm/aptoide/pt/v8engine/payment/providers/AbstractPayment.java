/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 25/11/2016.
 */

package cm.aptoide.pt.v8engine.payment.providers;

import cm.aptoide.pt.v8engine.payment.Payment;
import cm.aptoide.pt.v8engine.payment.PaymentConfirmation;
import cm.aptoide.pt.v8engine.payment.Price;
import cm.aptoide.pt.v8engine.payment.Product;
import cm.aptoide.pt.v8engine.payment.exception.PaymentFailureException;
import cm.aptoide.pt.v8engine.repository.PaymentConfirmationRepository;
import rx.Completable;
import rx.Observable;

/**
 * Created by marcelobenites on 25/11/16.
 */

public abstract class AbstractPayment implements Payment {

  protected final PaymentConfirmationRepository confirmationRepository;
  private final int id;
  private final String type;
  private final Product product;
  private final Price price;
  private final String description;

  public AbstractPayment(int id, String type, Product product, Price price, String description,
      PaymentConfirmationRepository confirmationRepository) {
    this.id = id;
    this.type = type;
    this.product = product;
    this.price = price;
    this.description = description;
    this.confirmationRepository = confirmationRepository;
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

  @Override public Observable<PaymentConfirmation> getConfirmation() {
    return confirmationRepository.getPaymentConfirmation(getProduct());
  }

  @Override public Completable process() {
    return confirmationRepository.createPaymentConfirmation(id).andThen(completePaymentOrFail());
  }

  protected Completable process(String paymentConfirmationId) {
    return confirmationRepository.createPaymentConfirmation(id, paymentConfirmationId)
        .andThen(completePaymentOrFail());
  }

  private Completable completePaymentOrFail() {
    return getConfirmation().takeUntil(
        paymentConfirmation -> paymentConfirmation.isCompleted()
            || paymentConfirmation.isFailed()).flatMap(paymentConfirmation -> {
      if (paymentConfirmation.isFailed()) {
        return Observable.error(new PaymentFailureException(
            "Payment " + getId() + "failed for product " + getProduct().getId()));
      }
      return Observable.empty();
    }).toCompletable();
  }

}