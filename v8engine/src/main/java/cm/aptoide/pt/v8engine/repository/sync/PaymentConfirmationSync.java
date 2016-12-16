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
import cm.aptoide.pt.v8engine.payment.product.InAppBillingProduct;
import cm.aptoide.pt.v8engine.payment.product.PaidAppProduct;
import cm.aptoide.pt.v8engine.repository.PaymentConfirmationConverter;
import cm.aptoide.pt.v8engine.repository.PaymentConfirmationRepository;
import cm.aptoide.pt.v8engine.repository.exception.RepositoryItemNotFoundException;
import rx.Observable;

/**
 * Created by marcelobenites on 22/11/16.
 */

public class PaymentConfirmationSync extends RepositorySync {

  private final PaymentConfirmationRepository paymentConfirmationRepository;
  private final Product product;
  private final NetworkOperatorManager operatorManager;
  private final PaymentConfirmationAccessor confirmationAccessor;
  private final PaymentConfirmationConverter paymentConfirmationConverter;
  private final String paymentConfirmationId;
  private int paymentId;

  public PaymentConfirmationSync(PaymentConfirmationRepository paymentConfirmationRepository,
      Product product, NetworkOperatorManager operatorManager,
      PaymentConfirmationAccessor confirmationAccessor,
      PaymentConfirmationConverter paymentConfirmationConverter, String paymentConfirmationId,
      int paymentId) {
    this.paymentConfirmationRepository = paymentConfirmationRepository;
    this.product = product;
    this.operatorManager = operatorManager;
    this.confirmationAccessor = confirmationAccessor;
    this.paymentConfirmationConverter = paymentConfirmationConverter;
    this.paymentConfirmationId = paymentConfirmationId;
    this.paymentId = paymentId;
  }

  @Override public void sync(SyncResult syncResult) {
    try {
      final Observable<PaymentConfirmation> serverPaymentConfirmation;
      if (paymentConfirmationId != null) {
        serverPaymentConfirmation =
            createServerPaymentConfirmation(product, paymentConfirmationId, paymentId);
      } else {
        serverPaymentConfirmation = getServerPaymentConfirmation(product);
      }
      serverPaymentConfirmation.doOnNext(paymentConfirmation -> confirmationAccessor.save(
          paymentConfirmationConverter.convertToStoredPaymentConfirmation(paymentConfirmation)))
          .onErrorReturn(throwable -> {
            if (throwable instanceof RepositoryItemNotFoundException) {
              confirmationAccessor.delete(product.getId());
            } else {
              rescheduleOrCancelSync(syncResult, throwable);
            }
            return null;
          })
          .toBlocking()
          .subscribe();
    } catch (RuntimeException e) {
      rescheduleSync(syncResult);
    }
  }

  private Observable<PaymentConfirmation> createServerPaymentConfirmation(Product product,
      String paymentConfirmationId, int paymentId) {
    return Observable.just(product instanceof InAppBillingProduct).flatMap(isInAppBilling -> {
      if (isInAppBilling) {
        return CreateInAppBillingProductPaymentRequest.of(product.getId(), paymentId,
            operatorManager, ((InAppBillingProduct) product).getDeveloperPayload(),
            AptoideAccountManager.getAccessToken(), paymentConfirmationId)
            .observe()
            .cast(ProductPaymentResponse.class);
      }
      return CreatePaidAppProductPaymentRequest.of(product.getId(), paymentId, operatorManager,
          ((PaidAppProduct) product).getStoreName(), AptoideAccountManager.getAccessToken(),
          paymentConfirmationId).observe();
    }).flatMap(response -> {
      if (response != null && response.isOk()) {
        return Observable.just(
            paymentConfirmationConverter.convertToPaymentConfirmation(product.getId(), response));
      }
      return Observable.error(new RepositoryItemNotFoundException(V3.getErrorMessage(response)));
    });
  }

  private Observable<PaymentConfirmation> getServerPaymentConfirmation(Product product) {
    return Observable.just(product instanceof InAppBillingProduct).flatMap(isInAppBilling -> {
      if (isInAppBilling) {
        return CheckInAppBillingProductPaymentRequest.of(product.getId(), operatorManager,
            ((InAppBillingProduct) product).getApiVersion(), AptoideAccountManager.getAccessToken())
            .observe()
            .cast(ProductPaymentResponse.class);
      }
      return CheckPaidAppProductPaymentRequest.of(product.getId(), operatorManager,
          AptoideAccountManager.getAccessToken()).observe();
    }).flatMap(response -> {
      if (response != null && response.isOk()) {
        return Observable.just(
            paymentConfirmationConverter.convertToPaymentConfirmation(product.getId(), response));
      }
      return Observable.error(new RepositoryItemNotFoundException(V3.getErrorMessage(response)));
    });
  }

  private void rescheduleIncompletedPaymentSync(PaymentConfirmation paymentConfirmation,
      SyncResult syncResult) {
    if (!paymentConfirmation.isCompleted()) {
      rescheduleSync(syncResult);
    }
  }
}
