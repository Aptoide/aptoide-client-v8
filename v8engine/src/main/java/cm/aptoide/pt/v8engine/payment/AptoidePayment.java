/*
 * Copyright (c) 2017.
 * Modified by Marcelo Benites on 02/01/2017.
 */

package cm.aptoide.pt.v8engine.payment;

import cm.aptoide.pt.v8engine.repository.PaymentConfirmationRepository;
import rx.Completable;

/**
 * Created by marcelobenites on 25/11/16.
 */

public class AptoidePayment implements Payment {

  private final PaymentConfirmationRepository confirmationRepository;
  private final int id;
  private final String name;
  private final String description;
  private final Authorization authorization;

  public AptoidePayment(int id, String name, String description,
      PaymentConfirmationRepository confirmationRepository, Authorization authorization) {
    this.id = id;
    this.name = name;
    this.description = description;
    this.confirmationRepository = confirmationRepository;
    this.authorization = authorization;
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

  @Override public Authorization getAuthorization() {
    return authorization;
  }

  @Override public Completable process(Product product) {
    return confirmationRepository.createPaymentConfirmation(id, product);
  }
}