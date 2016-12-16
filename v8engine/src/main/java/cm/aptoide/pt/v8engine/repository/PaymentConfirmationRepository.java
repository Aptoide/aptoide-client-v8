/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 16/12/2016.
 */

package cm.aptoide.pt.v8engine.repository;

import cm.aptoide.pt.database.accessors.PaymentConfirmationAccessor;
import cm.aptoide.pt.dataprovider.NetworkOperatorManager;
import cm.aptoide.pt.v8engine.payment.PaymentConfirmation;
import cm.aptoide.pt.v8engine.payment.Product;
import cm.aptoide.pt.v8engine.payment.product.AptoideProduct;
import cm.aptoide.pt.v8engine.repository.exception.RepositoryItemNotFoundException;
import cm.aptoide.pt.v8engine.repository.sync.SyncAdapterBackgroundSync;
import java.util.List;
import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * Created by marcelobenites on 16/12/16.
 */
public abstract class PaymentConfirmationRepository {

  protected final NetworkOperatorManager operatorManager;
  protected final PaymentConfirmationConverter paymentConfirmationConverter;
  private final PaymentConfirmationAccessor paymentDatabase;
  private final SyncAdapterBackgroundSync backgroundSync;

  public PaymentConfirmationRepository(NetworkOperatorManager operatorManager,
      PaymentConfirmationAccessor paymentDatabase, SyncAdapterBackgroundSync backgroundSync,
      PaymentConfirmationConverter paymentConfirmationConverter) {
    this.operatorManager = operatorManager;
    this.paymentDatabase = paymentDatabase;
    this.backgroundSync = backgroundSync;
    this.paymentConfirmationConverter = paymentConfirmationConverter;
  }

  public abstract Observable<PaymentConfirmation> createPaymentConfirmation(int paymentId);

  public abstract Observable<PaymentConfirmation> createPaymentConfirmation(int paymentId, String paymentConfirmationId);

  public Observable<PaymentConfirmation> getPaymentConfirmation(Product product) {
    return paymentDatabase.getPaymentConfirmation(product.getId()).flatMap(paymentConfirmation -> {
      if (paymentConfirmation != null) {
        return Observable.just(
            paymentConfirmationConverter.convertToPaymentConfirmation(paymentConfirmation));
      }
      return Observable.error(new RepositoryItemNotFoundException(
          "No payment confirmation found for product id: " + product.getId()));
    });
  }

  public Observable<Void> savePaymentConfirmation(PaymentConfirmation paymentConfirmation) {
    return Observable.<Void>fromCallable(() -> {
      storePaymentConfirmationInDatabase(paymentConfirmation);
      return null;
    }).subscribeOn(Schedulers.io());
  }

  protected Observable<PaymentConfirmation> createPaymentConfirmation(int paymentId,
      String paymentConfirmationId, AptoideProduct product) {
    return Observable.defer(() -> {
      final PaymentConfirmation paymentConfirmation =
          new PaymentConfirmation(paymentConfirmationId, product.getId(),
              PaymentConfirmation.Status.PENDING);
      createPaymentConfirmationInBackground(product, paymentConfirmation, paymentId);
      return getPaymentConfirmation(product);
    }).subscribeOn(Schedulers.io());
  }

  protected void syncPaymentConfirmationInBackground(AptoideProduct product,
      PaymentConfirmation paymentConfirmation) {
    storePaymentConfirmationInDatabase(paymentConfirmation);
    backgroundSync.syncConfirmation(product);
  }

  protected void createPaymentConfirmationInBackground(AptoideProduct product,
      PaymentConfirmation paymentConfirmation, int paymentId) {
    storePaymentConfirmationInDatabase(paymentConfirmation);
    backgroundSync.syncConfirmation(product, paymentConfirmation, paymentId);
  }

  private void storePaymentConfirmationInDatabase(PaymentConfirmation paymentConfirmation) {
    paymentDatabase.save(
        paymentConfirmationConverter.convertToStoredPaymentConfirmation(paymentConfirmation));
  }

  private Observable<List<PaymentConfirmation>> getDatabasePaymentConfirmations() {
    return paymentDatabase.getPaymentConfirmations()
        .flatMap(paymentConfirmations -> Observable.from(paymentConfirmations)
            .map(paymentConfirmation -> paymentConfirmationConverter.convertToPaymentConfirmation(
                paymentConfirmation))
            .toList());
  }

}