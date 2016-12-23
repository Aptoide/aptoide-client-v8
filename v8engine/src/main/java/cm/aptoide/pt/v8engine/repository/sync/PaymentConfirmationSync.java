/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 22/11/2016.
 */

package cm.aptoide.pt.v8engine.repository.sync;

import android.content.SyncResult;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.database.accessors.PaymentConfirmationAccessor;
import cm.aptoide.pt.dataprovider.NetworkOperatorManager;
import cm.aptoide.pt.dataprovider.ws.v3.CheckInAppBillingProductPaymentRequest;
import cm.aptoide.pt.dataprovider.ws.v3.CheckPaidAppProductPaymentRequest;
import cm.aptoide.pt.dataprovider.ws.v3.CreateInAppBillingProductPaymentRequest;
import cm.aptoide.pt.dataprovider.ws.v3.CreatePaidAppProductPaymentRequest;
import cm.aptoide.pt.dataprovider.ws.v3.V3;
import cm.aptoide.pt.model.v3.ProductPaymentResponse;
import cm.aptoide.pt.v8engine.payment.PaymentConfirmation;
import cm.aptoide.pt.v8engine.payment.Product;
import cm.aptoide.pt.v8engine.payment.products.InAppBillingProduct;
import cm.aptoide.pt.v8engine.payment.products.PaidAppProduct;
import cm.aptoide.pt.v8engine.repository.PaymentConfirmationConverter;
import cm.aptoide.pt.v8engine.repository.PaymentConfirmationRepository;
import cm.aptoide.pt.v8engine.repository.exception.RepositoryItemNotFoundException;
import java.io.IOException;
import rx.Single;

/**
 * Created by marcelobenites on 22/11/16.
 */

public class PaymentConfirmationSync extends RepositorySync {

  private final PaymentConfirmationRepository paymentConfirmationRepository;
  private final Product product;
  private final NetworkOperatorManager operatorManager;
  private final PaymentConfirmationAccessor confirmationAccessor;
  private final PaymentConfirmationConverter confirmationConverter;
  private final String paymentConfirmationId;
  private int paymentId;

  public PaymentConfirmationSync(PaymentConfirmationRepository paymentConfirmationRepository,
      Product product, NetworkOperatorManager operatorManager,
      PaymentConfirmationAccessor confirmationAccessor,
      PaymentConfirmationConverter confirmationConverter, String paymentConfirmationId,
      int paymentId) {
    this.paymentConfirmationRepository = paymentConfirmationRepository;
    this.product = product;
    this.operatorManager = operatorManager;
    this.confirmationAccessor = confirmationAccessor;
    this.confirmationConverter = confirmationConverter;
    this.paymentConfirmationId = paymentConfirmationId;
    this.paymentId = paymentId;
  }

  @Override public void sync(SyncResult syncResult) {
    try {
      final Single<PaymentConfirmation> serverPaymentConfirmation;
      if (paymentConfirmationId != null) {
        serverPaymentConfirmation =
            createServerPaymentConfirmation(product, paymentConfirmationId, paymentId);
      } else {
        serverPaymentConfirmation = getServerPaymentConfirmation(product);
      }
      serverPaymentConfirmation.doOnSuccess(
          paymentConfirmation -> saveAndReschedulePendingConfirmation(paymentConfirmation,
              syncResult)).onErrorReturn(throwable -> {
        saveAndRescheduleOnNetworkError(syncResult, throwable);
        return null;
      }).toBlocking().value();
    } catch (RuntimeException e) {
      rescheduleSync(syncResult);
    }
  }

  private void saveAndRescheduleOnNetworkError(SyncResult syncResult, Throwable throwable) {
    if (throwable instanceof IOException) {
      rescheduleSync(syncResult);
    } else {
      confirmationAccessor.save(confirmationConverter.convertToDatabasePaymentConfirmation(
          PaymentConfirmation.syncingError(product.getId())));
    }
  }

  private void saveAndReschedulePendingConfirmation(PaymentConfirmation paymentConfirmation,
      SyncResult syncResult) {
    confirmationAccessor.save(
        confirmationConverter.convertToDatabasePaymentConfirmation(paymentConfirmation));
    if (paymentConfirmation.isPending()) {
      rescheduleSync(syncResult);
    }
  }

  private Single<PaymentConfirmation> createServerPaymentConfirmation(Product product,
      String paymentConfirmationId, int paymentId) {
    return Single.just(product instanceof InAppBillingProduct).flatMap(isInAppBilling -> {
      if (isInAppBilling) {
        return CreateInAppBillingProductPaymentRequest.of(product.getId(), paymentId,
            operatorManager, ((InAppBillingProduct) product).getDeveloperPayload(),
            AptoideAccountManager.getAccessToken(), paymentConfirmationId)
            .observe()
            .cast(ProductPaymentResponse.class)
            .toSingle();
      }
      return CreatePaidAppProductPaymentRequest.of(product.getId(), paymentId, operatorManager,
          ((PaidAppProduct) product).getStoreName(), AptoideAccountManager.getAccessToken(),
          paymentConfirmationId).observe().toSingle();
    }).flatMap(response -> {
      if (response != null && response.isOk()) {
        return Single.just(
            confirmationConverter.convertToPaymentConfirmation(product.getId(), response));
      }
      return Single.error(new RepositoryItemNotFoundException(V3.getErrorMessage(response)));
    });
  }

  private Single<PaymentConfirmation> getServerPaymentConfirmation(Product product) {
    return Single.just(product instanceof InAppBillingProduct).flatMap(isInAppBilling -> {
      if (isInAppBilling) {
        return CheckInAppBillingProductPaymentRequest.of(product.getId(), operatorManager,
            ((InAppBillingProduct) product).getApiVersion(), AptoideAccountManager.getAccessToken())
            .observe()
            .cast(ProductPaymentResponse.class)
            .toSingle();
      }
      return CheckPaidAppProductPaymentRequest.of(product.getId(), operatorManager,
          AptoideAccountManager.getAccessToken()).observe().toSingle();
    }).flatMap(response -> {
      if (response != null && response.isOk()) {
        return Single.just(
            confirmationConverter.convertToPaymentConfirmation(product.getId(), response));
      }
      return Single.error(new RepositoryItemNotFoundException(V3.getErrorMessage(response)));
    });
  }
}
