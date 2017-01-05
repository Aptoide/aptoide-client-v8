/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 16/12/2016.
 */

package cm.aptoide.pt.v8engine.repository;

import cm.aptoide.pt.database.accessors.PaymentConfirmationAccessor;
import cm.aptoide.pt.dataprovider.NetworkOperatorManager;
import cm.aptoide.pt.v8engine.payment.PaymentConfirmation;
import cm.aptoide.pt.v8engine.payment.Product;
import cm.aptoide.pt.v8engine.payment.products.AptoideProduct;
import cm.aptoide.pt.v8engine.repository.sync.SyncAdapterBackgroundSync;
import rx.Completable;
import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * Created by marcelobenites on 16/12/16.
 */
public abstract class PaymentConfirmationRepository {

  protected final NetworkOperatorManager operatorManager;
  protected final PaymentConfirmationConverter confirmationConverter;
  private final PaymentConfirmationAccessor confirmationAccessor;
  private final SyncAdapterBackgroundSync backgroundSync;

  public PaymentConfirmationRepository(NetworkOperatorManager operatorManager,
      PaymentConfirmationAccessor confirmationAccessor, SyncAdapterBackgroundSync backgroundSync,
      PaymentConfirmationConverter confirmationConverter) {
    this.operatorManager = operatorManager;
    this.confirmationAccessor = confirmationAccessor;
    this.backgroundSync = backgroundSync;
    this.confirmationConverter = confirmationConverter;
  }

  public abstract Completable createPaymentConfirmation(int paymentId);

  public abstract Completable createPaymentConfirmation(int paymentId,
      String paymentConfirmationId);

  public Observable<PaymentConfirmation> getPaymentConfirmation(Product product) {
    return confirmationAccessor.getPaymentConfirmations(product.getId())
        .flatMap(paymentConfirmations -> Observable.from(paymentConfirmations)
            .map(paymentConfirmation -> confirmationConverter.convertToPaymentConfirmation(
                paymentConfirmation)))
        .doOnSubscribe(() -> backgroundSync.syncConfirmation(((AptoideProduct) product)));
  }

  protected Completable createPaymentConfirmation(int paymentId, String paymentConfirmationId,
      AptoideProduct product) {
    return Completable.fromAction(() -> {
      backgroundSync.syncConfirmation(product, paymentId, paymentConfirmationId);
    }).subscribeOn(Schedulers.io());
  }

  protected void syncPaymentConfirmation(AptoideProduct product) {
    backgroundSync.syncConfirmation(product);
  }
}