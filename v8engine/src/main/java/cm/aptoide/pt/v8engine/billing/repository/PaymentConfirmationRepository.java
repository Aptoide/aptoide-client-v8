/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 16/12/2016.
 */

package cm.aptoide.pt.v8engine.billing.repository;

import cm.aptoide.pt.database.accessors.PaymentConfirmationAccessor;
import cm.aptoide.pt.dataprovider.NetworkOperatorManager;
import cm.aptoide.pt.dataprovider.ws.v3.V3;
import cm.aptoide.pt.model.v3.BaseV3Response;
import cm.aptoide.pt.v8engine.billing.Payer;
import cm.aptoide.pt.v8engine.billing.PaymentConfirmation;
import cm.aptoide.pt.v8engine.billing.Product;
import cm.aptoide.pt.v8engine.billing.repository.sync.PaymentSyncScheduler;
import cm.aptoide.pt.v8engine.repository.exception.RepositoryIllegalArgumentException;
import rx.Completable;
import rx.Observable;
import rx.Single;

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

  public Observable<PaymentConfirmation> getPaymentConfirmation(Product product) {
    return payer.getId()
        .flatMapObservable(
            payerId -> confirmationAccessor.getPaymentConfirmations(product.getId(), payerId)
                .flatMap(paymentConfirmations -> Observable.from(paymentConfirmations)
                    .map(paymentConfirmation -> confirmationFactory.convertToPaymentConfirmation(
                        paymentConfirmation))
                    .switchIfEmpty(syncPaymentConfirmation(product).toObservable())));
  }

  public Completable createPaymentConfirmation(Product product, int paymentId, String metadataId) {
    return payer.getId()
        .flatMapCompletable(
            payerId -> createServerConfirmation(product, paymentId, metadataId).flatMapCompletable(
                response -> {
                  if (response != null && response.isOk()) {
                    confirmationAccessor.save(
                        confirmationFactory.convertToDatabasePaymentConfirmation(
                            confirmationFactory.create(product.getId(), metadataId,
                                PaymentConfirmation.Status.COMPLETED, payerId)));
                    return Completable.complete();
                  }
                  confirmationAccessor.save(
                      confirmationFactory.convertToDatabasePaymentConfirmation(
                          confirmationFactory.create(product.getId(), "",
                              PaymentConfirmation.Status.NEW, payerId)));
                  return Completable.error(
                      new RepositoryIllegalArgumentException(V3.getErrorMessage(response)));
                }));
  }

  protected abstract Single<BaseV3Response> createServerConfirmation(Product product, int paymentId,
      String metadataId);

  protected Completable syncPaymentConfirmation(Product product) {
    return backgroundSync.scheduleConfirmationSync(product);
  }

  public Completable remove(int productId) {
    return Completable.fromAction(() -> confirmationAccessor.remove(productId));
  }
}