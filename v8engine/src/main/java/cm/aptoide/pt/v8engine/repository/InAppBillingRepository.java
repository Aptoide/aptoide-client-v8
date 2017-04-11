/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 11/08/2016.
 */

package cm.aptoide.pt.v8engine.repository;

import android.support.annotation.NonNull;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.database.accessors.PaymentConfirmationAccessor;
import cm.aptoide.pt.dataprovider.NetworkOperatorManager;
import cm.aptoide.pt.dataprovider.ws.v3.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v3.InAppBillingAvailableRequest;
import cm.aptoide.pt.dataprovider.ws.v3.InAppBillingConsumeRequest;
import cm.aptoide.pt.dataprovider.ws.v3.InAppBillingPurchasesRequest;
import cm.aptoide.pt.dataprovider.ws.v3.InAppBillingSkuDetailsRequest;
import cm.aptoide.pt.dataprovider.ws.v3.V3;
import cm.aptoide.pt.dataprovider.ws.v7.BodyInterceptor;
import cm.aptoide.pt.iab.SKU;
import cm.aptoide.pt.model.v3.ErrorResponse;
import cm.aptoide.pt.model.v3.InAppBillingPurchasesResponse;
import cm.aptoide.pt.model.v3.InAppBillingSkuDetailsResponse;
import cm.aptoide.pt.v8engine.repository.exception.RepositoryIllegalArgumentException;
import cm.aptoide.pt.v8engine.repository.exception.RepositoryItemNotFoundException;
import java.util.Collections;
import java.util.List;
import rx.Observable;

/**
 * Created by marcelobenites on 8/11/16.
 */
public class InAppBillingRepository {

  private final NetworkOperatorManager operatorManager;
  private final PaymentConfirmationAccessor confirmationAccessor;
  private final AptoideAccountManager accountManager;
  private final BodyInterceptor<BaseBody> bodyInterceptorV3;

  public InAppBillingRepository(NetworkOperatorManager operatorManager,
      PaymentConfirmationAccessor confirmationAccessor, AptoideAccountManager accountManager,
      BodyInterceptor<BaseBody> bodyInterceptorV3) {
    this.operatorManager = operatorManager;
    this.confirmationAccessor = confirmationAccessor;
    this.accountManager = accountManager;
    this.bodyInterceptorV3 = bodyInterceptorV3;
  }

  public Observable<Void> getInAppBilling(int apiVersion, String packageName, String type) {
    return InAppBillingAvailableRequest.of(apiVersion, packageName, type, bodyInterceptorV3)
        .observe()
        .flatMap(response -> {
          if (response != null && response.isOk()) {
            if (response.getInAppBillingAvailable().isAvailable()) {
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

  public Observable<List<SKU>> getSKUs(int apiVersion, String packageName, List<String> skuList,
      String type) {
    return getSKUListDetails(apiVersion, packageName, skuList, type).flatMap(
        details -> Observable.from(details.getPublisherResponse().getDetailList())
            .map(detail -> new SKU(detail.getProductId(), detail.getType(), detail.getPrice(),
                detail.getCurrency(), (long) (detail.getPriceAmount() * 1000000), detail.getTitle(),
                detail.getDescription()))
            .toList());
  }

  private Observable<InAppBillingSkuDetailsResponse> getSKUListDetails(int apiVersion,
      String packageName, List<String> skuList, String type) {
    return InAppBillingSkuDetailsRequest.of(apiVersion, packageName, skuList, operatorManager, type,
        accountManager.getAccessToken(), bodyInterceptorV3).observe().flatMap(response -> {
      if (response != null && response.isOk()) {
        return Observable.just(response);
      } else {
        final List<InAppBillingSkuDetailsResponse.PurchaseDataObject> detailList =
            response.getPublisherResponse().getDetailList();
        if (detailList.isEmpty()) {
          return Observable.error(
              new RepositoryItemNotFoundException(V3.getErrorMessage(response)));
        }
        return Observable.error(
            new RepositoryIllegalArgumentException(V3.getErrorMessage(response)));
      }
    });
  }

  public Observable<InAppBillingPurchasesResponse.PurchaseInformation> getInAppPurchaseInformation(
      int apiVersion, String packageName, String type) {
    return InAppBillingPurchasesRequest.of(apiVersion, packageName, type,
        accountManager.getAccessToken(), bodyInterceptorV3).observe().flatMap(response -> {
      if (response != null && response.isOk()) {
        return Observable.just(response.getPurchaseInformation());
      }
      return Observable.error(new RepositoryIllegalArgumentException(V3.getErrorMessage(response)));
    });
  }

  public Observable<Void> deleteInAppPurchase(int apiVersion, String packageName,
      String purchaseToken) {
    return InAppBillingConsumeRequest.of(apiVersion, packageName, purchaseToken,
        accountManager.getAccessToken(), bodyInterceptorV3).observe().flatMap(response -> {
      if (response != null && response.isOk()) {
        // TODO sync all payment confirmations instead. For now there is no web service for that.
        confirmationAccessor.removeAll();
        return Observable.just(null);
      }
      if (isDeletionItemNotFound(response.getErrors())) {
        return Observable.error(new RepositoryItemNotFoundException(V3.getErrorMessage(response)));
      }
      return Observable.error(new RepositoryIllegalArgumentException(V3.getErrorMessage(response)));
    });
  }

  @NonNull private boolean isDeletionItemNotFound(List<ErrorResponse> errors) {
    for (ErrorResponse error : errors) {
      if (error.code.equals("PRODUCT-201")) {
        return true;
      }
    }
    return false;
  }

  public Observable<InAppBillingSkuDetailsResponse> getSKUDetails(int apiVersion,
      String packageName, String sku, String type) {
    return getSKUListDetails(apiVersion, packageName, Collections.singletonList(sku), type);
  }
}
