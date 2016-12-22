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
import cm.aptoide.pt.v8engine.payment.product.AptoideProduct;
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
            .defaultIfEmpty(
                new PaymentConfirmation("", product.getId(), PaymentConfirmation.Status.ERROR)))
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
    final PaymentConfirmation paymentConfirmation =
        new PaymentConfirmation("", product.getId(), PaymentConfirmation.Status.SYNCING);
    confirmationAccessor.save(
        confirmationConverter.convertToDatabasePaymentConfirmation(paymentConfirmation));
    backgroundSync.syncConfirmation(product);
  }

  private void syncPaymentConfirmation(int paymentId, AptoideProduct product,
      String confirmationId) {
    final PaymentConfirmation paymentConfirmation =
        new PaymentConfirmation(confirmationId, product.getId(),
            PaymentConfirmation.Status.SYNCING);
    confirmationAccessor.save(
        confirmationConverter.convertToDatabasePaymentConfirmation(paymentConfirmation));
    backgroundSync.syncConfirmation(product, paymentConfirmation, paymentId);
  }

  private void storePaymentConfirmationInDatabase(PaymentConfirmation paymentConfirmation) {
    confirmationAccessor.save(
        confirmationConverter.convertToDatabasePaymentConfirmation(paymentConfirmation));
  }
}