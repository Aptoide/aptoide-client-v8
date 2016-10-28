/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 04/08/2016.
 */

package cm.aptoide.pt.v8engine.repository;

import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.dataprovider.NetworkOperatorManager;
import cm.aptoide.pt.dataprovider.repository.IdsRepositoryImpl;
import cm.aptoide.pt.dataprovider.ws.v3.GetApkInfoRequest;
import cm.aptoide.pt.dataprovider.ws.v7.GetAppRequest;
import cm.aptoide.pt.model.v3.PaidApp;
import cm.aptoide.pt.model.v3.PaymentService;
import cm.aptoide.pt.model.v7.GetApp;
import cm.aptoide.pt.v8engine.payment.ProductFactory;
import cm.aptoide.pt.v8engine.repository.exception.RepositoryItemNotFoundException;
import cm.aptoide.pt.v8engine.util.StoreUtils;
import java.util.List;
import lombok.AllArgsConstructor;
import rx.Observable;

/**
 * Created by marcelobenites on 7/27/16.
 */
@AllArgsConstructor public class AppRepository {

  private final NetworkOperatorManager operatorManager;
  private final ProductFactory productFactory;

  public Observable<GetApp> getApp(long appId, boolean refresh, boolean sponsored,
      String storeName) {
    return GetAppRequest.of(appId, storeName, StoreUtils.getStoreCredentials(storeName),
        AptoideAccountManager.getAccessToken(),
        new IdsRepositoryImpl(SecurePreferencesImplementation.getInstance(),
            DataProvider.getContext()).getAptoideClientUUID())
        .observe(refresh)
        .flatMap(response -> {
      if (response != null && response.isOk()) {
        if (response.getNodes().getMeta().getData().isPaid()) {
          return addPayment(sponsored, response, refresh);
        } else {
          return Observable.just(response);
        }
      } else {
        return Observable.error(
            new RepositoryItemNotFoundException("No app found for app id " + appId));
      }
    });
  }

  public Observable<GetApp> getApp(String packageName, boolean refresh, boolean sponsored,
      String storeName) {
    return GetAppRequest.of(packageName, storeName, AptoideAccountManager.getAccessToken(),
        new IdsRepositoryImpl(SecurePreferencesImplementation.getInstance(),
            DataProvider.getContext()).getAptoideClientUUID())
        .observe(refresh)
        .flatMap(response -> {
      if (response != null && response.isOk()) {
        return addPayment(sponsored, response, refresh);
      } else {
        return Observable.error(
            new RepositoryItemNotFoundException("No app found for app package" + packageName));
      }
    });
  }

  public Observable<List<PaymentService>> getPaymentServices(long appId, boolean sponsored,
      String storeName, boolean refresh) {
    return getPaidApp(appId, sponsored, storeName, refresh).map(
        paidApp -> paidApp.getPayment().getPaymentServices());
  }

  private Observable<GetApp> addPayment(boolean sponsored, GetApp getApp, boolean refresh) {
    return getPaidApp(getApp.getNodes().getMeta().getData().getId(), sponsored,
        getApp.getNodes().getMeta().getData().getStore().getName(), refresh).map(paidApp -> {

      if (paidApp.getPayment().isPaid()) {
        getApp.getNodes().getMeta().getData().getFile().setPath(paidApp.getPath().getStringPath());
      } else {
        getApp.getNodes()
            .getMeta()
            .getData()
            .getPay()
            .setProductId(paidApp.getPayment().getMetadata().getId());
        getApp.getNodes()
            .getMeta()
            .getData()
            .getPay()
            .setPaymentServices(paidApp.getPayment().getPaymentServices());
      }
      getApp.getNodes().getMeta().getData().getPay().setStatus(paidApp.getPayment().getStatus());
      return getApp;
    }).onErrorResumeNext(throwable -> {
      if (throwable instanceof RepositoryItemNotFoundException) {
        return Observable.just(getApp);
      }
      return Observable.error(throwable);
    });
  }

  public Observable<PaidApp> getPaidApp(long appId, boolean sponsored, String storeName,
      boolean refresh) {
    return GetApkInfoRequest.of(appId, operatorManager, sponsored, storeName,
        AptoideAccountManager.getAccessToken())
        .observe(refresh)
        .flatMap(response -> {
          if (response != null && response.isOk() && response.isPaid()) {
            return Observable.just(response);
          } else {
            return Observable.error(new RepositoryItemNotFoundException(
                "No paid app found for app id " + appId + " in store " +
                    storeName));
          }
        });
  }

  public Observable<GetApp> getAppFromMd5(String md5, boolean refresh, boolean sponsored) {
    return GetAppRequest.ofMd5(md5, AptoideAccountManager.getAccessToken(),
        new IdsRepositoryImpl(SecurePreferencesImplementation.getInstance(),
            DataProvider.getContext()).getAptoideClientUUID())
        .observe(refresh)
        .flatMap(response -> {
      if (response != null && response.isOk()) {
        return addPayment(sponsored, response, refresh);
      } else {
        return Observable.error(
            new RepositoryItemNotFoundException("No app found for app md5" + md5));
      }
    });
  }
}