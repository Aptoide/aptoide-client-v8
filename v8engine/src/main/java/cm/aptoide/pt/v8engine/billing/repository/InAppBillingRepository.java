/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 11/08/2016.
 */

package cm.aptoide.pt.v8engine.billing.repository;

import android.content.SharedPreferences;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.model.v3.ErrorResponse;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v3.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v3.InAppBillingAvailableRequest;
import cm.aptoide.pt.dataprovider.ws.v3.InAppBillingConsumeRequest;
import cm.aptoide.pt.dataprovider.ws.v3.V3;
import cm.aptoide.pt.v8engine.billing.TransactionPersistence;
import cm.aptoide.pt.v8engine.repository.exception.RepositoryIllegalArgumentException;
import cm.aptoide.pt.v8engine.repository.exception.RepositoryItemNotFoundException;
import java.util.List;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Completable;
import rx.Observable;

public class InAppBillingRepository {

  private final TransactionPersistence transactionPersistence;
  private final BodyInterceptor<BaseBody> bodyInterceptorV3;
  private final OkHttpClient httpClient;
  private final Converter.Factory converterFactory;
  private final TokenInvalidator tokenInvalidator;
  private final SharedPreferences sharedPreferences;

  public InAppBillingRepository(TransactionPersistence transactionPersistence,
      BodyInterceptor<BaseBody> bodyInterceptorV3, OkHttpClient httpClient,
      Converter.Factory converterFactory, TokenInvalidator tokenInvalidator,
      SharedPreferences sharedPreferences) {
    this.transactionPersistence = transactionPersistence;
    this.bodyInterceptorV3 = bodyInterceptorV3;
    this.httpClient = httpClient;
    this.converterFactory = converterFactory;
    this.tokenInvalidator = tokenInvalidator;
    this.sharedPreferences = sharedPreferences;
  }

  public Observable<Void> getInAppBilling(int apiVersion, String packageName, String type) {
    return InAppBillingAvailableRequest.of(apiVersion, packageName, type, bodyInterceptorV3,
        httpClient, converterFactory, tokenInvalidator, sharedPreferences)
        .observe()
        .flatMap(response -> {
          if (response != null && response.isOk()) {
            if (response.getInAppBillingAvailable()
                .isAvailable()) {
              return Observable.just(null);
            } else {
              return Observable.error(
                  new RepositoryItemNotFoundException(V3.getErrorMessage(response)));
            }
          } else {
            return Observable.error(
                new RepositoryIllegalArgumentException(V3.getErrorMessage(response)));
          }
        });
  }

  public Completable deleteInAppPurchase(int apiVersion, String packageName, String purchaseToken) {
    return InAppBillingConsumeRequest.of(apiVersion, packageName, purchaseToken, bodyInterceptorV3,
        httpClient, converterFactory, tokenInvalidator, sharedPreferences)
        .observe()
        .first()
        .toSingle()
        .flatMapCompletable(response -> {
          if (response != null && response.isOk()) {
            // TODO sync all payment confirmations instead. For now there is no web service for that.
            transactionPersistence.removeAllTransactions();
            return Completable.complete();
          }
          if (isDeletionItemNotFound(response.getErrors())) {
            return Completable.error(
                new RepositoryItemNotFoundException(V3.getErrorMessage(response)));
          }
          return Completable.error(
              new RepositoryIllegalArgumentException(V3.getErrorMessage(response)));
        });
  }

  private boolean isDeletionItemNotFound(List<ErrorResponse> errors) {
    for (ErrorResponse error : errors) {
      if (error.code.equals("PRODUCT-201")) {
        return true;
      }
    }
    return false;
  }
}
