/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 16/12/2016.
 */

package cm.aptoide.pt.v8engine.repository;

import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.database.accessors.PaymentConfirmationAccessor;
import cm.aptoide.pt.dataprovider.NetworkOperatorManager;
import cm.aptoide.pt.dataprovider.ws.v3.CreatePaidAppProductPaymentRequest;
import cm.aptoide.pt.v8engine.payment.PaymentConfirmation;
import cm.aptoide.pt.v8engine.payment.product.PaidAppProduct;
import cm.aptoide.pt.v8engine.repository.sync.SyncAdapterBackgroundSync;
import rx.Observable;

/**
 * Created by marcelobenites on 16/12/16.
 */

public class PaidAppPaymentConfirmationRepository extends PaymentConfirmationRepository {

  private final PaidAppProduct product;

  public PaidAppPaymentConfirmationRepository(NetworkOperatorManager operatorManager,
      PaymentConfirmationAccessor paymentDatabase, SyncAdapterBackgroundSync backgroundSync,
      PaymentConfirmationConverter paymentConfirmationConverter, PaidAppProduct product) {
    super(operatorManager, paymentDatabase, backgroundSync, paymentConfirmationConverter);
    this.product = product;
  }

  @Override public Observable<PaymentConfirmation> createPaymentConfirmation(int paymentId) {
    return CreatePaidAppProductPaymentRequest.of(product.getId(), paymentId, operatorManager,
        product.getStoreName(), AptoideAccountManager.getAccessToken())
        .observe()
        .map(response -> paymentConfirmationConverter.convertToPaymentConfirmation(product.getId(),
            response))
        .doOnNext(paymentConfirmation -> syncPaymentConfirmationInBackground(product,
            paymentConfirmation))
        .flatMap(paymentConfirmation -> getPaymentConfirmation(product));
  }

  @Override
  public Observable<PaymentConfirmation> createPaymentConfirmation(int paymentId,
      String paymentConfirmationId) {
    return createPaymentConfirmation(paymentId, paymentConfirmationId, product);
  }
}
