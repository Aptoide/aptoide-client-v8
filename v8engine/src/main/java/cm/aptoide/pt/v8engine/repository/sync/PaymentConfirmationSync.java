/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 22/11/2016.
 */

package cm.aptoide.pt.v8engine.repository.sync;

import android.content.SyncResult;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.database.accessors.PaymentConfirmationAccessor;
import cm.aptoide.pt.dataprovider.NetworkOperatorManager;
import cm.aptoide.pt.dataprovider.ws.v3.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v3.CreatePaymentConfirmationRequest;
import cm.aptoide.pt.dataprovider.ws.v3.GetPaymentConfirmationRequest;
import cm.aptoide.pt.dataprovider.ws.v3.V3;
import cm.aptoide.pt.dataprovider.ws.v7.BodyInterceptor;
import cm.aptoide.pt.model.v3.PaymentConfirmationResponse;
import cm.aptoide.pt.v8engine.payment.PaymentConfirmation;
import cm.aptoide.pt.v8engine.payment.Product;
import cm.aptoide.pt.v8engine.payment.products.InAppBillingProduct;
import cm.aptoide.pt.v8engine.payment.products.PaidAppProduct;
import cm.aptoide.pt.v8engine.repository.PaymentConfirmationFactory;
import cm.aptoide.pt.v8engine.repository.PaymentConfirmationRepository;
import cm.aptoide.pt.v8engine.repository.exception.RepositoryIllegalArgumentException;
import cm.aptoide.pt.v8engine.repository.exception.RepositoryItemNotFoundException;
import java.io.IOException;
import rx.Completable;
import rx.Single;

/**
 * Created by marcelobenites on 22/11/16.
 */

public class PaymentConfirmationSync extends RepositorySync {

  private final PaymentConfirmationRepository paymentConfirmationRepository;
  private final Product product;
  private final NetworkOperatorManager operatorManager;
  private final PaymentConfirmationAccessor confirmationAccessor;
  private final PaymentConfirmationFactory confirmationFactory;
  private final AptoideAccountManager accountManager;
  private final BodyInterceptor<BaseBody> bodyInterceptorV3;

  private String paymentConfirmationId;
  private String paymentId;

  public PaymentConfirmationSync(PaymentConfirmationRepository paymentConfirmationRepository,
      Product product, NetworkOperatorManager operatorManager,
      PaymentConfirmationAccessor confirmationAccessor,
      PaymentConfirmationFactory confirmationFactory, String paymentConfirmationId,
      String paymentId, AptoideAccountManager accountManager,
      BodyInterceptor<BaseBody> bodyInterceptorV3) {
    this.paymentConfirmationRepository = paymentConfirmationRepository;
    this.product = product;
    this.operatorManager = operatorManager;
    this.confirmationAccessor = confirmationAccessor;
    this.confirmationFactory = confirmationFactory;
    this.paymentConfirmationId = paymentConfirmationId;
    this.paymentId = paymentId;
    this.accountManager = accountManager;
    this.bodyInterceptorV3 = bodyInterceptorV3;
  }

  public PaymentConfirmationSync(PaymentConfirmationRepository paymentConfirmationRepository,
      Product product, NetworkOperatorManager operatorManager,
      PaymentConfirmationAccessor confirmationAccessor,
      PaymentConfirmationFactory confirmationFactory, AptoideAccountManager accountManager,
      BodyInterceptor<BaseBody> bodyInterceptorV3) {
    this.paymentConfirmationRepository = paymentConfirmationRepository;
    this.product = product;
    this.operatorManager = operatorManager;
    this.confirmationAccessor = confirmationAccessor;
    this.confirmationFactory = confirmationFactory;
    this.accountManager = accountManager;
    this.bodyInterceptorV3 = bodyInterceptorV3;
  }

  @Override public void sync(SyncResult syncResult) {
    try {
      final String accessToken = accountManager.getAccessToken();
      final String payerId = accountManager.getAccountEmail();
      final Single<PaymentConfirmation> serverPaymentConfirmation;
      if (paymentConfirmationId != null) {
        final int paymentId = Integer.valueOf(this.paymentId);
        serverPaymentConfirmation =
            createServerPaymentConfirmation(product, paymentConfirmationId, paymentId,
                accessToken).andThen(Single.fromCallable(
                () -> confirmationFactory.create(product.getId(), paymentConfirmationId,
                    PaymentConfirmation.Status.COMPLETED, payerId)));
      } else {
        serverPaymentConfirmation = getServerPaymentConfirmation(product, payerId, accessToken);
      }
      serverPaymentConfirmation.doOnSuccess(
          paymentConfirmation -> saveAndReschedulePendingConfirmation(paymentConfirmation,
              syncResult, payerId)).onErrorReturn(throwable -> {
        saveAndRescheduleOnNetworkError(syncResult, throwable, payerId);
        return null;
      }).toBlocking().value();
    } catch (RuntimeException e) {
      rescheduleSync(syncResult);
    }
  }

  private Completable createServerPaymentConfirmation(Product product, String paymentConfirmationId,
      int paymentId, String accessToken) {
    return Single.just(product instanceof InAppBillingProduct).flatMap(isInAppBilling -> {
      if (isInAppBilling) {
        return CreatePaymentConfirmationRequest.ofInApp(product.getId(), paymentId, operatorManager,
            ((InAppBillingProduct) product).getDeveloperPayload(), accessToken,
            paymentConfirmationId, bodyInterceptorV3).observe().toSingle();
      }
      return CreatePaymentConfirmationRequest.ofPaidApp(product.getId(), paymentId, operatorManager,
          ((PaidAppProduct) product).getStoreName(), accessToken, paymentConfirmationId,
          bodyInterceptorV3).observe().toSingle();
    }).flatMapCompletable(response -> {
      if (response != null && response.isOk()) {
        return Completable.complete();
      }
      return Completable.error(
          new RepositoryIllegalArgumentException(V3.getErrorMessage(response)));
    });
  }

  private Single<PaymentConfirmation> getServerPaymentConfirmation(Product product, String payerId,
      String accessToken) {
    return Single.just(product instanceof InAppBillingProduct).flatMap(isInAppBilling -> {
      if (isInAppBilling) {
        return GetPaymentConfirmationRequest.of(product.getId(), operatorManager,
            ((InAppBillingProduct) product).getApiVersion(), accessToken, bodyInterceptorV3)
            .observe()
            .cast(PaymentConfirmationResponse.class)
            .toSingle();
      }
      return GetPaymentConfirmationRequest.of(product.getId(), operatorManager, accessToken,
          bodyInterceptorV3).observe().toSingle();
    }).flatMap(response -> {
      if (response != null && response.isOk()) {
        return Single.just(
            confirmationFactory.convertToPaymentConfirmation(product.getId(), response, payerId));
      }
      return Single.error(new RepositoryItemNotFoundException(V3.getErrorMessage(response)));
    });
  }

  private void saveAndReschedulePendingConfirmation(PaymentConfirmation paymentConfirmation,
      SyncResult syncResult, String payerId) {
    confirmationAccessor.save(
        confirmationFactory.convertToDatabasePaymentConfirmation(paymentConfirmation));

    if (paymentConfirmation.isPending()) {
      rescheduleSync(syncResult);
    }
  }

  private void saveAndRescheduleOnNetworkError(SyncResult syncResult, Throwable throwable,
      String payerId) {
    if (throwable instanceof IOException) {
      rescheduleSync(syncResult);
    } else {
      confirmationAccessor.save(confirmationFactory.convertToDatabasePaymentConfirmation(
          confirmationFactory.create(product.getId(), "", PaymentConfirmation.Status.NEW,
              payerId)));
    }
  }
}
