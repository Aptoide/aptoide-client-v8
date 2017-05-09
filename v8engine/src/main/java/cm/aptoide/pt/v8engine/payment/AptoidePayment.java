/*
 * Copyright (c) 2017.
 * Modified by Marcelo Benites on 02/01/2017.
 */

package cm.aptoide.pt.v8engine.payment;

import cm.aptoide.pt.v8engine.payment.repository.PaymentAuthorizationFactory;
import cm.aptoide.pt.v8engine.payment.repository.PaymentAuthorizationRepository;
import cm.aptoide.pt.v8engine.payment.repository.PaymentConfirmationRepository;
import rx.Completable;

public class AptoidePayment implements Payment {

  private final PaymentConfirmationRepository confirmationRepository;
  private final int id;
  private final String name;
  private final String description;

  public AptoidePayment(int id, String name, String description,
      PaymentConfirmationRepository confirmationRepository) {
    this.id = id;
    this.name = name;
    this.description = description;
    this.confirmationRepository = confirmationRepository;
  }

  @Override public int getId() {
    return id;
  }

  @Override public String getName() {
    return name;
  }

  @Override public String getDescription() {
    return description;
  }

  @Override public Completable process(Product product) {
    return confirmationRepository.createPaymentConfirmation(id, product);
  }
}