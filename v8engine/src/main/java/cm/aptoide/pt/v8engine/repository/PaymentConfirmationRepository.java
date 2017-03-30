/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 16/12/2016.
 */

package cm.aptoide.pt.v8engine.repository;

import cm.aptoide.pt.database.accessors.PaymentConfirmationAccessor;
import cm.aptoide.pt.dataprovider.NetworkOperatorManager;
import cm.aptoide.pt.v8engine.payment.PaymentConfirmation;
import cm.aptoide.pt.v8engine.payment.Product;
import cm.aptoide.pt.v8engine.repository.sync.SyncAdapterBackgroundSync;
import rx.Completable;
import rx.Observable;

/**
 * Created by marcelobenites on 16/12/16.
 */
public abstract class PaymentConfirmationRepository {

  protected final NetworkOperatorManager operatorManager;
  protected final PaymentConfirmationFactory confirmationFactory;
  private final PaymentConfirmationAccessor confirmationAccessor;
  private final SyncAdapterBackgroundSync backgroundSync;

  public PaymentConfirmationRepository(NetworkOperatorManager operatorManager,
      PaymentConfirmationAccessor confirmationAccessor, SyncAdapterBackgroundSync backgroundSync,
      PaymentConfirmationFactory confirmationFactory) {
    this.operatorManager = operatorManager;
    this.confirmationAccessor = confirmationAccessor;
    this.backgroundSync = backgroundSync;
    this.confirmationFactory = confirmationFactory;
  }

  public abstract Completable createPaymentConfirmation(int paymentId, Product product);

  public abstract Completable createPaymentConfirmation(int paymentId, String paymentConfirmationId,
      Product product);

  public Observable<PaymentConfirmation> getPaymentConfirmation(Product product, String payerId) {
    return syncPaymentConfirmation(product).andThen(
        confirmationAccessor.getPaymentConfirmations(product.getId(), payerId)
            .flatMap(paymentConfirmations -> Observable.from(paymentConfirmations)
                .map(paymentConfirmation -> confirmationFactory.convertToPaymentConfirmation(
                    paymentConfirmation))));
  }

  protected Completable syncPaymentConfirmation(Product product) {
    return backgroundSync.syncConfirmation(product);
  }

  protected Completable createPaymentConfirmation(Product product, int paymentId,
      String paymentConfirmationId) {
    return backgroundSync.syncConfirmation(product, paymentId, paymentConfirmationId);
  }
}