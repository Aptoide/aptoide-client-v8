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
  private final String type;
  private final String name;
  private final Product product;
  private final Price price;
  private final String description;
  private final boolean requiresAuthorization;

  private Authorization authorization;
  private PaymentConfirmation confirmation;

  public AptoidePayment(int id, String type, String name, String description, Product product,
      Price price, boolean requiresAuthorization,
      PaymentConfirmationRepository confirmationRepository) {
    this.id = id;
    this.type = type;
    this.name = name;
    this.product = product;
    this.price = price;
    this.description = description;
    this.requiresAuthorization = requiresAuthorization;
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

  @Override public Authorization getAuthorization() {
    return authorization;
  }

  @Override public Status getStatus() {

    if (confirmation.isCompleted()) {
      return Status.COMPLETED;
    }

    if (confirmation.isPending() || authorization.isPending()) {
      return Status.PENDING;
    }

    return Status.NEW;
  }

  @Override public void setAuthorization(Authorization authorization) {
    this.authorization = authorization;
  }

  @Override public PaymentConfirmation getConfirmation() {
    return confirmation;
  }

  @Override public void setConfirmation(PaymentConfirmation confirmation) {
    this.confirmation = confirmation;
  }

  @Override public boolean isAuthorizationRequired() {
    return requiresAuthorization;
  }

  @Override public Completable process() {
    return confirmationRepository.createPaymentConfirmation(id);
  }
}