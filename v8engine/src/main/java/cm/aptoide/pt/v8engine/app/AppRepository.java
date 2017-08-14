/*
 * Copyright (c) 2016.
 * Modified on 04/08/2016.
 */

package cm.aptoide.pt.v8engine.app;

import android.content.SharedPreferences;
import android.content.res.Resources;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.model.v3.PaidApp;
import cm.aptoide.pt.dataprovider.model.v7.GetApp;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v3.GetApkInfoRequest;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.GetAppRequest;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.repository.exception.RepositoryItemNotFoundException;
import cm.aptoide.pt.v8engine.store.StoreCredentialsProvider;
import cm.aptoide.pt.v8engine.store.StoreCredentialsProviderImpl;
import cm.aptoide.pt.v8engine.store.StoreUtils;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Observable;

public class AppRepository {

  private final BodyInterceptor<BaseBody> bodyInterceptorV7;
  private final BodyInterceptor<cm.aptoide.pt.dataprovider.ws.v3.BaseBody> bodyInterceptorV3;
  private final StoreCredentialsProvider storeCredentialsProvider;
  private final OkHttpClient httpClient;
  private final Converter.Factory converterFactory;
  private final TokenInvalidator tokenInvalidator;
  private final SharedPreferences sharedPreferences;
  private final Resources resources;

  public AppRepository(BodyInterceptor<BaseBody> bodyInterceptorV7,
      BodyInterceptor<cm.aptoide.pt.dataprovider.ws.v3.BaseBody> bodyInterceptorV3,
      StoreCredentialsProviderImpl storeCredentialsProvider, OkHttpClient httpClient,
      Converter.Factory converterFactory, TokenInvalidator tokenInvalidator,
      SharedPreferences sharedPreferences, Resources resources) {
    this.bodyInterceptorV7 = bodyInterceptorV7;
    this.bodyInterceptorV3 = bodyInterceptorV3;
    this.storeCredentialsProvider = storeCredentialsProvider;
    this.httpClient = httpClient;
    this.converterFactory = converterFactory;
    this.tokenInvalidator = tokenInvalidator;
    this.sharedPreferences = sharedPreferences;
    this.resources = resources;
  }

  public Observable<GetApp> getApp(long appId, boolean refresh, boolean sponsored, String storeName,
      String packageName) {
    //Only pass the storeName if this is partners
    //If vanilla, don't pass the store name.
    //store name is already in appId
    //[AN-1160] - [AppView] latest version bug
    return GetAppRequest.of(appId, V8Engine.getConfiguration()
            .getPartnerId() == null ? null : storeName,
        StoreUtils.getStoreCredentials(storeName, storeCredentialsProvider), packageName,
        bodyInterceptorV7, httpClient, converterFactory, tokenInvalidator, sharedPreferences)
        .observe(refresh)
        .flatMap(response -> {
          if (response != null && response.isOk()) {
            if (response.getNodes()
                .getMeta()
                .getData()
                .isPaid()) {
              return addPayment(sponsored, response);
            } else {
              return Observable.just(response);
            }
          } else {
            return Observable.error(
                new RepositoryItemNotFoundException("No app found for app id " + appId));
          }
        });
  }

  private Observable<GetApp> addPayment(boolean sponsored, GetApp getApp) {
    return getPaidApp(getApp.getNodes()
        .getMeta()
        .getData()
        .getId(), true).map(paidApp -> {
      if (paidApp.getPayment()
          .isPaid()) {
        getApp.getNodes()
            .getMeta()
            .getData()
            .getFile()
            .setPath(paidApp.getPath()
                .getStringPath());
      }
      getApp.getNodes()
          .getMeta()
          .getData()
          .getPay()
          .setStatus(paidApp.getPayment()
              .getStatus());
      return getApp;
    })
        .onErrorResumeNext(throwable -> {
          if (throwable instanceof RepositoryItemNotFoundException) {
            return Observable.just(getApp);
          }
          return Observable.error(throwable);
        });
  }

  private Observable<PaidApp> getPaidApp(long appId, boolean refresh) {
    return GetApkInfoRequest.of(appId, bodyInterceptorV3, httpClient, converterFactory,
        tokenInvalidator, sharedPreferences, resources)
        .observe(refresh)
        .flatMap(response -> {
          if (response != null && response.isOk() && response.isPaid()) {
            return Observable.just(response);
          } else {
            return Observable.error(
                new RepositoryItemNotFoundException("No paid app found for app id " + appId));
          }
        });
  }

  public Observable<GetApp> getApp(String packageName, boolean refresh, boolean sponsored,
      String storeName) {
    return GetAppRequest.of(packageName, storeName, bodyInterceptorV7, httpClient, converterFactory,
        tokenInvalidator, sharedPreferences)
        .observe(refresh)
        .flatMap(response -> {
          if (response != null && response.isOk()) {
            if (response.getNodes()
                .getMeta()
                .getData()
                .isPaid()) {
              return addPayment(sponsored, response);
            } else {
              return Observable.just(response);
            }
          } else {
            return Observable.error(
                new RepositoryItemNotFoundException("No app found for app package" + packageName));
          }
        });
  }

  public Observable<GetApp> getAppFromMd5(String md5, boolean refresh, boolean sponsored) {
    return GetAppRequest.ofMd5(md5, bodyInterceptorV7, httpClient, converterFactory,
        tokenInvalidator, sharedPreferences)
        .observe(refresh)
        .flatMap(response -> {
          if (response != null && response.isOk()) {
            if (response.getNodes()
                .getMeta()
                .getData()
                .isPaid()) {
              return addPayment(sponsored, response);
            } else {
              return Observable.just(response);
            }
          } else {
            return Observable.error(
                new RepositoryItemNotFoundException("No app found for app md5" + md5));
          }
        });
  }

  public Observable<GetApp> getAppFromUname(String uname, boolean refresh, boolean sponsored) {
    return GetAppRequest.ofUname(uname, bodyInterceptorV7, httpClient, converterFactory,
        tokenInvalidator, sharedPreferences)
        .observe(refresh)
        .flatMap(response -> {
          if (response != null && response.isOk()) {
            if (response.getNodes()
                .getMeta()
                .getData()
                .isPaid()) {
              return addPayment(sponsored, response);
            } else {
              return Observable.just(response);
            }
          } else {
            return Observable.error(
                new RepositoryItemNotFoundException("No app found for app uname" + uname));
          }
        });
  }
}
