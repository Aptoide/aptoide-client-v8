/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 22/11/2016.
 */

package cm.aptoide.pt.v8engine.payment.repository.sync;

import android.content.SyncResult;
import cm.aptoide.pt.database.accessors.PaymentConfirmationAccessor;
import cm.aptoide.pt.dataprovider.NetworkOperatorManager;
import cm.aptoide.pt.dataprovider.ws.v3.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v3.CreatePaymentConfirmationRequest;
import cm.aptoide.pt.dataprovider.ws.v3.GetPaymentConfirmationRequest;
import cm.aptoide.pt.dataprovider.ws.v3.V3;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.model.v3.PaymentConfirmationResponse;
import cm.aptoide.pt.v8engine.payment.Payer;
import cm.aptoide.pt.v8engine.payment.PaymentAnalytics;
import cm.aptoide.pt.v8engine.payment.PaymentConfirmation;
import cm.aptoide.pt.v8engine.payment.Product;
import cm.aptoide.pt.v8engine.payment.products.InAppBillingProduct;
import cm.aptoide.pt.v8engine.payment.products.PaidAppProduct;
import cm.aptoide.pt.v8engine.payment.repository.PaymentConfirmationFactory;
import cm.aptoide.pt.v8engine.payment.repository.PaymentConfirmationRepository;
import cm.aptoide.pt.v8engine.repository.exception.RepositoryIllegalArgumentException;
import cm.aptoide.pt.v8engine.repository.exception.RepositoryItemNotFoundException;
import cm.aptoide.pt.v8engine.sync.RepositorySync;
import java.io.IOException;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
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
  private final Payer payer;
  private final BodyInterceptor<BaseBody> bodyInterceptorV3;
  private final Converter.Factory converterFactory;
  private final OkHttpClient httpClient;
  private final PaymentAnalytics analytics;
  private String paymentConfirmationId;
  private int paymentId;

  public PaymentConfirmationSync(PaymentConfirmationRepository paymentConfirmationRepository,
      Product product, NetworkOperatorManager operatorManager,
      PaymentConfirmationAccessor confirmationAccessor,
      PaymentConfirmationFactory confirmationFactory, String paymentConfirmationId, int paymentId,
      Payer payer, BodyInterceptor<BaseBody> bodyInterceptorV3, Converter.Factory converterFactory,
      OkHttpClient httpClient, PaymentAnalytics analytics) {
    this.paymentConfirmationRepository = paymentConfirmationRepository;
    this.product = product;
    this.operatorManager = operatorManager;
    this.confirmationAccessor = confirmationAccessor;
    this.confirmationFactory = confirmationFactory;
    this.paymentConfirmationId = paymentConfirmationId;
    this.paymentId = paymentId;
    this.payer = payer;
    this.bodyInterceptorV3 = bodyInterceptorV3;
    this.converterFactory = converterFactory;
    this.httpClient = httpClient;
    this.analytics = analytics;
  }

  public PaymentConfirmationSync(PaymentConfirmationRepository paymentConfirmationRepository,
      Product product, NetworkOperatorManager operatorManager,
      PaymentConfirmationAccessor confirmationAccessor,
      PaymentConfirmationFactory confirmationFactory, Payer payer,
      BodyInterceptor<BaseBody> bodyInterceptorV3, Converter.Factory converterFactory,
      OkHttpClient httpClient, PaymentAnalytics analytics) {
    this.paymentConfirmationRepository = paymentConfirmationRepository;
    this.product = product;
    this.operatorManager = operatorManager;
    this.confirmationAccessor = confirmationAccessor;
    this.confirmationFactory = confirmationFactory;
    this.payer = payer;
    this.bodyInterceptorV3 = bodyInterceptorV3;
    this.converterFactory = converterFactory;
    this.httpClient = httpClient;
    this.analytics = analytics;
  }

  @Override public void sync(SyncResult syncResult) {
    try {
      payer.getId()
          .flatMap(payerId -> {
            if (paymentConfirmationId != null) {
              return createServerPaymentConfirmation(product, paymentConfirmationId,
                  paymentId).andThen(Single.fromCallable(
                  () -> confirmationFactory.create(product.getId(), paymentConfirmationId,
                      PaymentConfirmation.Status.COMPLETED, payerId))).onErrorReturn(throwable -> {
                saveAndRescheduleOnNetworkError(syncResult, throwable, payerId);
                return null;
              });
            } else {
              return getServerPaymentConfirmation(product, payerId).onErrorReturn(throwable -> {
                saveAndRescheduleOnNetworkError(syncResult, throwable, payerId);
                return null;
              });
            }
          })
          .doOnSuccess(
              paymentConfirmation -> saveAndReschedulePendingConfirmation(paymentConfirmation,
                  syncResult, paymentConfirmation.getPayerId()))
          .toBlocking()
          .value();
    } catch (RuntimeException e) {
      rescheduleSync(syncResult);
    }
  }

  private Completable createServerPaymentConfirmation(Product product, String paymentConfirmationId,
      int paymentId) {
    return Single.just(product instanceof InAppBillingProduct).flatMap(isInAppBilling -> {
      if (isInAppBilling) {
        return CreatePaymentConfirmationRequest.ofInApp(product.getId(), paymentId, operatorManager,
            ((InAppBillingProduct) product).getDeveloperPayload(), paymentConfirmationId,
            bodyInterceptorV3, httpClient, converterFactory).observe().toSingle();
      }
      return CreatePaymentConfirmationRequest.ofPaidApp(product.getId(), paymentId, operatorManager,
          ((PaidAppProduct) product).getStoreName(), paymentConfirmationId, bodyInterceptorV3,
          httpClient, converterFactory).observe().toSingle();
    }).flatMapCompletable(response -> {
      if (response != null && response.isOk()) {
        return Completable.complete();
      }
      return Completable.error(
          new RepositoryIllegalArgumentException(V3.getErrorMessage(response)));
    });
  }

  private Single<PaymentConfirmation> getServerPaymentConfirmation(Product product,
      String payerId) {
    return Single.just(product instanceof InAppBillingProduct).flatMap(isInAppBilling -> {
      if (isInAppBilling) {
        return GetPaymentConfirmationRequest.of(product.getId(), operatorManager,
            ((InAppBillingProduct) product).getApiVersion(), bodyInterceptorV3, httpClient,
            converterFactory).observe().cast(PaymentConfirmationResponse.class).toSingle();
      }
      return GetPaymentConfirmationRequest.of(product.getId(), operatorManager, bodyInterceptorV3,
          httpClient, converterFactory).observe().toSingle();
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

    analytics.sendPurchaseCompleteEvent(paymentConfirmation, product);
  }

  private void saveAndRescheduleOnNetworkError(SyncResult syncResult, Throwable throwable,
      String payerId) {
    if (throwable instanceof IOException) {
      analytics.sendPurchaseNetworkRetryEvent(product);
      rescheduleSync(syncResult);
    } else {
      confirmationAccessor.save(confirmationFactory.convertToDatabasePaymentConfirmation(
          confirmationFactory.create(product.getId(), "", PaymentConfirmation.Status.NEW,
              payerId)));
    }
  }
}
