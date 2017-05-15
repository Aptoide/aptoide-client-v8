/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 11/08/2016.
 */

package cm.aptoide.pt.v8engine.billing.services;

import android.content.Context;
import cm.aptoide.pt.v8engine.billing.Payer;
import cm.aptoide.pt.v8engine.billing.Product;
import cm.aptoide.pt.v8engine.billing.repository.AuthorizationFactory;
import cm.aptoide.pt.v8engine.billing.repository.AuthorizationRepository;
import cm.aptoide.pt.v8engine.billing.repository.PaymentRepositoryFactory;
import cm.aptoide.pt.v8engine.billing.services.AuthorizedPayment;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import rx.Completable;

public class PayPalPayment extends AuthorizedPayment {

  private final Context context;
  private final int id;
  private final String name;
  private final String description;
  private final PaymentRepositoryFactory paymentRepositoryFactory;
  private final Payer payer;
  private final AuthorizationRepository authorizationRepository;
  private final AuthorizationFactory authorizationFactory;

  public PayPalPayment(Context context, int id, String name, String description,
      PaymentRepositoryFactory paymentRepositoryFactory, Payer payer,
      AuthorizationRepository authorizationRepository, AuthorizationFactory authorizationFactory) {
    super(id, name, description, paymentRepositoryFactory, payer, authorizationRepository,
        authorizationFactory);
    this.context = context;
    this.id = id;
    this.name = name;
    this.description = description;
    this.paymentRepositoryFactory = paymentRepositoryFactory;
    this.payer = payer;
    this.authorizationRepository = authorizationRepository;
    this.authorizationFactory = authorizationFactory;
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
    return checkAuthorization().andThen(
        paymentRepositoryFactory.getPaymentConfirmationRepository(product)
            .createPaymentConfirmation(product, getId(),
                PayPalConfiguration.getClientMetadataId(context)));
  }
}