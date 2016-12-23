/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 16/12/2016.
 */

package cm.aptoide.pt.v8engine.repository;

import cm.aptoide.pt.database.accessors.PaymentConfirmationAccessor;
import cm.aptoide.pt.dataprovider.NetworkOperatorManager;
import cm.aptoide.pt.model.v3.ProductPaymentResponse;
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
                paymentConfirmation))
            .defaultIfEmpty(PaymentConfirmation.syncingError(product.getId())))
        .doOnSubscribe(() -> syncPaymentConfirmation((AptoideProduct) product));
  }

  protected Completable createPaymentConfirmation(int paymentId, String paymentConfirmationId,
      AptoideProduct product) {
    return Completable.fromAction(() -> {
      syncPaymentConfirmation(paymentId, product, paymentConfirmationId);
    }).subscribeOn(Schedulers.io());
  }

  protected void syncPaymentConfirmation(int paymentId, ProductPaymentResponse response,
      AptoideProduct product) {
    confirmationAccessor.save(
        confirmationConverter.convertToDatabasePaymentConfirmation(paymentId, response));
    backgroundSync.syncConfirmation(product);
  }

  private void syncPaymentConfirmation(AptoideProduct product) {
    final PaymentConfirmation paymentConfirmation = PaymentConfirmation.syncing(product.getId());
    confirmationAccessor.save(
        confirmationConverter.convertToDatabasePaymentConfirmation(paymentConfirmation));
    backgroundSync.syncConfirmation(product);
  }

  private void syncPaymentConfirmation(int paymentId, AptoideProduct product,
      String paymentConfirmationId) {
    confirmationAccessor.save(confirmationConverter.convertToDatabasePaymentConfirmation(
        PaymentConfirmation.syncing(paymentConfirmationId, product.getId())));
    backgroundSync.syncConfirmation(product, paymentId, paymentConfirmationId);
  }

  private void storePaymentConfirmationInDatabase(PaymentConfirmation paymentConfirmation) {
    confirmationAccessor.save(
        confirmationConverter.convertToDatabasePaymentConfirmation(paymentConfirmation));
  }
}