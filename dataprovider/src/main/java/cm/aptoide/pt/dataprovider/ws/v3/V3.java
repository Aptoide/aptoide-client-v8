/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 03/08/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v3;

import android.support.annotation.NonNull;
import cm.aptoide.pt.dataprovider.BuildConfig;
import cm.aptoide.pt.dataprovider.DataProvider;
import cm.aptoide.pt.dataprovider.exception.AptoideWsV3Exception;
import cm.aptoide.pt.dataprovider.ws.v2.GenericResponseV2;
import cm.aptoide.pt.model.v3.BaseV3Response;
import cm.aptoide.pt.model.v3.ErrorResponse;
import cm.aptoide.pt.model.v3.GetPushNotificationsResponse;
import cm.aptoide.pt.model.v3.InAppBillingAvailableResponse;
import cm.aptoide.pt.model.v3.InAppBillingProductPaymentResponse;
import cm.aptoide.pt.model.v3.InAppBillingPurchasesResponse;
import cm.aptoide.pt.model.v3.InAppBillingSkuDetailsResponse;
import cm.aptoide.pt.model.v3.PaidApp;
import cm.aptoide.pt.model.v3.GetProductPurchaseAuthorizationResponse;
import cm.aptoide.pt.model.v3.ProductPaymentResponse;
import cm.aptoide.pt.networkclient.WebService;
import cm.aptoide.pt.networkclient.okhttp.OkHttpClientFactory;
import cm.aptoide.pt.networkclient.okhttp.cache.PostCacheInterceptor;
import cm.aptoide.pt.preferences.secure.SecurePreferences;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import retrofit2.adapter.rxjava.HttpException;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.POST;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by sithengineer on 21/07/16.
 */
public abstract class V3<U> extends WebService<V3.Interfaces, U> {

  protected static final String BASE_HOST = BuildConfig.APTOIDE_WEB_SERVICES_SCHEME
      + "://"
      + BuildConfig.APTOIDE_WEB_SERVICES_HOST
      + "/webservices/3/";

  private static final int REFRESH_TOKEN_DELAY = 1000;

  protected final BaseBody map;
  private final String INVALID_ACCESS_TOKEN_CODE = "invalid_token";
  private boolean accessTokenRetry = false;

  protected V3(String baseHost) {
    this(baseHost, new BaseBody());
  }

  protected V3(String baseHost, BaseBody baseBody) {
    super(Interfaces.class,
        OkHttpClientFactory.getSingletonClient(() -> SecurePreferences.getUserAgent(), isDebug()),
        WebService.getDefaultConverter(), baseHost);
    this.map = baseBody;
  }

  @NonNull public static String getErrorMessage(BaseV3Response response) {
    final StringBuilder builder = new StringBuilder();
    if (response != null) {
      for (ErrorResponse error : response.getErrors()) {
        builder.append(error.msg);
        builder.append(". ");
      }
      if (builder.length() == 0) {
        builder.append("Server failed with empty error list.");
      }
    } else {
      builder.append("Server returned null response.");
    }
    return builder.toString();
  }

  @Override public Observable<U> observe(boolean bypassCache) {
    return super.observe(bypassCache).onErrorResumeNext(throwable -> {
      if (throwable instanceof HttpException) {
        try {

          GenericResponseV3 genericResponseV3 =
              (GenericResponseV3) converterFactory.responseBodyConverter(GenericResponseV3.class,
                  null, null).convert(((HttpException) throwable).response().errorBody());

          if (INVALID_ACCESS_TOKEN_CODE.equals(genericResponseV3.getError())) {

            if (!accessTokenRetry) {
              accessTokenRetry = true;
              return DataProvider.invalidateAccessToken().flatMap(s -> {
                this.map.setAccess_token(s);
                return V3.this.observe(bypassCache)
                    .delaySubscription(REFRESH_TOKEN_DELAY, TimeUnit.MILLISECONDS)
                    .observeOn(AndroidSchedulers.mainThread());
              });
            }
          } else {
            return Observable.error(
                new AptoideWsV3Exception(throwable).setBaseResponse(genericResponseV3));
          }
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
      return Observable.error(throwable);
    });
  }

  interface Interfaces {

    @POST("getPushNotifications") @FormUrlEncoded
    Observable<GetPushNotificationsResponse> getPushNotifications(@FieldMap BaseBody arg,
        @Header(PostCacheInterceptor.BYPASS_HEADER_KEY) boolean bypassCache);

    @POST("addApkFlag") @FormUrlEncoded Observable<GenericResponseV2> addApkFlag(
        @FieldMap BaseBody arg,
        @Header(PostCacheInterceptor.BYPASS_HEADER_KEY) boolean bypassCache);

    @POST("getApkInfo") @FormUrlEncoded Observable<PaidApp> getApkInfo(@FieldMap BaseBody args,
        @Header(PostCacheInterceptor.BYPASS_HEADER_KEY) boolean bypassCache);

    @POST("processInAppBilling") @FormUrlEncoded
    Observable<InAppBillingAvailableResponse> getInAppBillingAvailable(@FieldMap BaseBody args);

    @POST("processInAppBilling") @FormUrlEncoded
    Observable<InAppBillingSkuDetailsResponse> getInAppBillingSkuDetails(@FieldMap BaseBody args);

    @POST("processInAppBilling") @FormUrlEncoded
    Observable<InAppBillingPurchasesResponse> getInAppBillingPurchases(@FieldMap BaseBody args);

    @POST("processInAppBilling") @FormUrlEncoded
    Observable<BaseV3Response> deleteInAppBillingPurchase(@FieldMap BaseBody args);

    @POST("checkProductPayment") @FormUrlEncoded Observable<ProductPaymentResponse> checkPaidAppProductPayment(
        @FieldMap BaseBody args);

    @POST("checkProductPayment") @FormUrlEncoded
    Observable<InAppBillingPurchasesResponse> checkInAppProductPayment(@FieldMap BaseBody args);

    @POST("productPurchaseAuthorization") @FormUrlEncoded
    Observable<GetProductPurchaseAuthorizationResponse> getProductPurchaseAuthorization(@FieldMap BaseBody args);

    @POST("payProduct") @FormUrlEncoded
    Observable<InAppBillingProductPaymentResponse> createInAppBillingProductPayment(@FieldMap BaseBody args);

    @POST("payProduct") @FormUrlEncoded
    Observable<ProductPaymentResponse> createPaidAppProductPayment(@FieldMap BaseBody args);
  }
}
