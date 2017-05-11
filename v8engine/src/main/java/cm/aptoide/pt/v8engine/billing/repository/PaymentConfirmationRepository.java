/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 16/12/2016.
 */

package cm.aptoide.pt.v8engine.billing.repository;

import cm.aptoide.pt.database.accessors.PaymentConfirmationAccessor;
import cm.aptoide.pt.dataprovider.NetworkOperatorManager;
import cm.aptoide.pt.v8engine.billing.Payer;
import cm.aptoide.pt.v8engine.billing.PaymentConfirmation;
import cm.aptoide.pt.v8engine.billing.Product;
import cm.aptoide.pt.v8engine.billing.repository.sync.PaymentSyncScheduler;
import rx.Completable;
import rx.Observable;

/**
 * Created by marcelobenites on 16/12/16.
 */
public abstract class PaymentConfirmationRepository {

  protected final NetworkOperatorManager operatorManager;
  protected final PaymentConfirmationFactory confirmationFactory;
  private final Payer payer;
  private final PaymentConfirmationAccessor confirmationAccessor;
  private final PaymentSyncScheduler backgroundSync;

  public PaymentConfirmationRepository(NetworkOperatorManager operatorManager,
      PaymentConfirmationAccessor confirmationAccessor, PaymentSyncScheduler backgroundSync,
      PaymentConfirmationFactory confirmationFactory, Payer payer) {
    this.operatorManager = operatorManager;
    this.confirmationAccessor = confirmationAccessor;
    this.backgroundSync = backgroundSync;
    this.confirmationFactory = confirmationFactory;
    this.payer = payer;
  }

  public abstract Completable createPaymentConfirmation(int paymentId, Product product);

  public abstract Completable createPaymentConfirmation(int paymentId, String paymentConfirmationId,
      Product product);

  public Observable<PaymentConfirmation> getPaymentConfirmation(Product product) {
    return payer.getId()
        .flatMapObservable(payerId -> syncPaymentConfirmation(product).andThen(
            confirmationAccessor.getPaymentConfirmations(product.getId(), payerId)
                .flatMap(paymentConfirmations -> Observable.from(paymentConfirmations)
                    .map(paymentConfirmation -> confirmationFactory.convertToPaymentConfirmation(
                        paymentConfirmation)))));
  }

  protected Completable syncPaymentConfirmation(Product product) {
    return backgroundSync.scheduleConfirmationSync(product);
  }

  protected Completable createPaymentConfirmation(Product product, int paymentId,
      String paymentConfirmationId) {
    return backgroundSync.scheduleConfirmationSync(product, paymentId, paymentConfirmationId);
  }
}