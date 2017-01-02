/*
 * Copyright (c) 2017.
 * Modified by Marcelo Benites on 02/01/2017.
 */

package cm.aptoide.pt.v8engine.payment;

import cm.aptoide.pt.v8engine.payment.exception.PaymentFailureException;
import cm.aptoide.pt.v8engine.repository.PaymentConfirmationRepository;
import rx.Completable;
import rx.Observable;

/**
 * Created by marcelobenites on 25/11/16.
 */

public class AptoidePayment implements Payment {

  protected final PaymentConfirmationRepository confirmationRepository;
  private final int id;
  private final String type;
  private final String name;
  private final Product product;
  private final Price price;
  private final String description;

  public AptoidePayment(int id, String type, String name, String description, Product product, Price price,
      PaymentConfirmationRepository confirmationRepository) {
    this.id = id;
    this.type = type;
    this.name = name;
    this.product = product;
    this.price = price;
    this.description = description;
    this.confirmationRepository = confirmationRepository;
  }

  @Override public int getId() {
    return id;
  }

  @Override public String getName() {
    return name;
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