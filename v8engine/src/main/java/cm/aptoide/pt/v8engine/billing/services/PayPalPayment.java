/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 11/08/2016.
 */

package cm.aptoide.pt.v8engine.billing.services;

import android.content.Context;
import cm.aptoide.pt.v8engine.billing.Payment;
import cm.aptoide.pt.v8engine.billing.Product;
import cm.aptoide.pt.v8engine.billing.exception.PaymentNotAuthorizedException;
import cm.aptoide.pt.v8engine.billing.repository.AuthorizationFactory;
import cm.aptoide.pt.v8engine.billing.repository.AuthorizationRepository;
import cm.aptoide.pt.v8engine.billing.repository.PaymentRepositoryFactory;
import cm.aptoide.pt.v8engine.repository.exception.RepositoryIllegalArgumentException;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import rx.Completable;

public class PayPalPayment implements Payment {

  private final Context context;
  private final int id;
  private final String name;
  private final String description;
  private final PaymentRepositoryFactory paymentRepositoryFactory;
  private final AuthorizationRepository authorizationRepository;

  public PayPalPayment(Context context, int id, String name, String description,
      PaymentRepositoryFactory paymentRepositoryFactory,
      AuthorizationRepository authorizationRepository) {
    this.context = context;
    this.id = id;
    this.name = name;
    this.description = description;
    this.paymentRepositoryFactory = paymentRepositoryFactory;
    this.authorizationRepository = authorizationRepository;
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
                PayPalConfiguration.getClientMetadataId(context))
            .onErrorResumeNext(throwable -> {
              if (throwable instanceof RepositoryIllegalArgumentException) {
                return authorizationRepository.remove(getId())
                    .andThen(Completable.error(throwable));
              }
              return Completable.error(throwable);
            }));
  }

  public Completable process(Product product, String authorizationCode) {
    return authorizationRepository.createPayPalPaymentAuthorization(getId(), authorizationCode)
        .andThen(process(product));
  }

  private Completable checkAuthorization() {
    return authorizationRepository.getPaymentAuthorization(getId(), AuthorizationFactory.PAYPAL)
        .distinctUntilChanged(authorization -> authorization.getStatus())
        .cast(PayPalAuthorization.class)
        .takeUntil(authorization -> authorization.isAuthorized())
        .flatMapCompletable(authorization -> {

          if (authorization.isAuthorized()) {
            return Completable.complete();
          }

          return Completable.error(
              new PaymentNotAuthorizedException("Pending PayPal SDK user consent"));
        })
        .toCompletable();
  }
}