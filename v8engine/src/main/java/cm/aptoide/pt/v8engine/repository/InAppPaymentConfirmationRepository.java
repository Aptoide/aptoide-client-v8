/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 02/09/2016.
 */

package cm.aptoide.pt.v8engine.repository;

import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.database.accessors.PaymentConfirmationAccessor;
import cm.aptoide.pt.dataprovider.NetworkOperatorManager;
import cm.aptoide.pt.dataprovider.ws.v3.CreateInAppBillingProductPaymentRequest;
import cm.aptoide.pt.model.v3.ProductPaymentResponse;
import cm.aptoide.pt.v8engine.payment.PaymentConfirmation;
import cm.aptoide.pt.v8engine.payment.product.InAppBillingProduct;
import cm.aptoide.pt.v8engine.repository.sync.SyncAdapterBackgroundSync;
import rx.Observable;

/**
 * Created by marcelobenites on 8/18/16.
 */
public class InAppPaymentConfirmationRepository extends PaymentConfirmationRepository {

  private final InAppBillingProduct product;

  public InAppPaymentConfirmationRepository(NetworkOperatorManager operatorManager,
      PaymentConfirmationAccessor paymentDatabase, SyncAdapterBackgroundSync backgroundSync,
      PaymentConfirmationConverter paymentConfirmationConverter, InAppBillingProduct product) {
    super(operatorManager, paymentDatabase, backgroundSync, paymentConfirmationConverter);
    this.product = product;
  }

  @Override public Observable<PaymentConfirmation> createPaymentConfirmation(int paymentId) {
    return CreateInAppBillingProductPaymentRequest.of(product.getId(), paymentId, operatorManager,
        product.getDeveloperPayload(), AptoideAccountManager.getAccessToken())
        .observe()
        .cast(ProductPaymentResponse.class)
        .map(response -> paymentConfirmationConverter.convertToPaymentConfirmation(product.getId(),
            response))
        .doOnNext(paymentConfirmation ->
            syncPaymentConfirmationInBackground(product, paymentConfirmation))
        .flatMap(paymentConfirmation -> getPaymentConfirmation(product));
  }

  @Override
  public Observable<PaymentConfirmation> createPaymentConfirmation(int paymentId,
      String paymentConfirmationId) {
    return createPaymentConfirmation(paymentId, paymentConfirmationId, product);
  }
}