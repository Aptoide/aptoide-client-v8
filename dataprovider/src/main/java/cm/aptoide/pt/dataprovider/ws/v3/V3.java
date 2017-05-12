/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 03/08/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v3;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import cm.aptoide.pt.dataprovider.BuildConfig;
import cm.aptoide.pt.dataprovider.DataProvider;
import cm.aptoide.pt.dataprovider.NetworkOperatorManager;
import cm.aptoide.pt.dataprovider.exception.AptoideWsV3Exception;
import cm.aptoide.pt.dataprovider.ws.v2.GenericResponseV2;
import cm.aptoide.pt.dataprovider.ws.v7.BodyInterceptor;
import cm.aptoide.pt.model.v3.BaseV3Response;
import cm.aptoide.pt.model.v3.CheckUserCredentialsJson;
import cm.aptoide.pt.model.v3.ErrorResponse;
import cm.aptoide.pt.model.v3.GetPushNotificationsResponse;
import cm.aptoide.pt.model.v3.InAppBillingAvailableResponse;
import cm.aptoide.pt.model.v3.InAppBillingPurchasesResponse;
import cm.aptoide.pt.model.v3.InAppBillingSkuDetailsResponse;
import cm.aptoide.pt.model.v3.OAuth;
import cm.aptoide.pt.model.v3.PaidApp;
import cm.aptoide.pt.model.v3.PaymentAuthorizationsResponse;
import cm.aptoide.pt.model.v3.PaymentConfirmationResponse;
import cm.aptoide.pt.networkclient.WebService;
import cm.aptoide.pt.networkclient.util.HashMapNotNull;
import cm.aptoide.pt.preferences.managed.ManagerPreferences;
import java.io.IOException;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Converter;
import retrofit2.adapter.rxjava.HttpException;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
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

  protected final BaseBody map;
  private final String INVALID_ACCESS_TOKEN_CODE = "invalid_token";
  private final BodyInterceptor<BaseBody> bodyInterceptor;
  private boolean accessTokenRetry = false;

  protected V3(BaseBody baseBody, OkHttpClient httpClient, Converter.Factory converterFactory,
      BodyInterceptor<BaseBody> bodyInterceptor) {
    super(Interfaces.class, httpClient, converterFactory, BASE_HOST);
    this.map = baseBody;
    this.bodyInterceptor = bodyInterceptor;
  }

  protected V3(OkHttpClient okHttpClient, Converter.Factory converterFactory,
      BodyInterceptor<BaseBody> bodyInterceptor) {
    this(new BaseBody(), okHttpClient, converterFactory, bodyInterceptor);
  }

  @NonNull public static String getErrorMessage(BaseV3Response response) {
    final StringBuilder builder = new StringBuilder();
    if (response != null && response.getErrors() != null) {
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

  protected static void addNetworkInformation(NetworkOperatorManager operatorManager,
      BaseBody args) {
    String forceCountry = ManagerPreferences.getForceCountry();
    if (ManagerPreferences.isDebug() && !TextUtils.isEmpty(forceCountry)) {
      args.put("simcc", forceCountry);
    } else {
      if (operatorManager.isSimStateReady()) {
        args.put("mcc", operatorManager.getMobileCountryCode());
        args.put("mnc", operatorManager.getMobileNetworkCode());
        args.put("simcc", operatorManager.getSimCountryISO());
      }
    }
  }

  @Override public Observable<U> observe(boolean bypassCache) {
    return bodyInterceptor.intercept(map)
        .flatMapObservable(body -> super.observe(bypassCache)
            .onErrorResumeNext(throwable -> {
              if (throwable instanceof HttpException) {
                try {

                  GenericResponseV3 genericResponseV3 =
                      (GenericResponseV3) converterFactory.responseBodyConverter(
                          GenericResponseV3.class, null, null)
                          .convert(((HttpException) throwable).response()
                              .errorBody());

                  if (INVALID_ACCESS_TOKEN_CODE.equals(genericResponseV3.getError())) {

                    if (!accessTokenRetry) {
                      accessTokenRetry = true;
                      return DataProvider.invalidateAccessToken()
                          .flatMapObservable(s -> {
                            this.map.setAccess_token(s);
                            return V3.this.observe(bypassCache)
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
            }));
  }

  interface Interfaces {

    @POST("getPushNotifications") @FormUrlEncoded
    Observable<GetPushNotificationsResponse> getPushNotifications(@FieldMap BaseBody arg,
        @Header(WebService.BYPASS_HEADER_KEY) boolean bypassCache);

    @POST("addApkFlag") @FormUrlEncoded Observable<GenericResponseV2> addApkFlag(
        @FieldMap BaseBody arg, @Header(WebService.BYPASS_HEADER_KEY) boolean bypassCache);

    @POST("getApkInfo") @FormUrlEncoded Observable<PaidApp> getApkInfo(@FieldMap BaseBody args,
        @Header(WebService.BYPASS_HEADER_KEY) boolean bypassCache);

    @POST("processInAppBilling") @FormUrlEncoded
    Observable<InAppBillingAvailableResponse> getInAppBillingAvailable(@FieldMap BaseBody args);

    @POST("processInAppBilling") @FormUrlEncoded
    Observable<InAppBillingSkuDetailsResponse> getInAppBillingSkuDetails(@FieldMap BaseBody args);

    @POST("processInAppBilling") @FormUrlEncoded
    Observable<InAppBillingPurchasesResponse> getInAppBillingPurchases(@FieldMap BaseBody args);

    @POST("processInAppBilling") @FormUrlEncoded
    Observable<BaseV3Response> deleteInAppBillingPurchase(@FieldMap BaseBody args);

    @POST("checkProductPayment") @FormUrlEncoded
    Observable<PaymentConfirmationResponse> getPaymentConfirmation(@FieldMap BaseBody args);

    @POST("productPurchaseAuthorization") @FormUrlEncoded
    Observable<PaymentAuthorizationsResponse> getPaymentAuthorization(@FieldMap BaseBody args);

    @POST("payProduct") @FormUrlEncoded Observable<BaseV3Response> createPaymentConfirmation(
        @FieldMap BaseBody args);

    @POST("createPurchaseAuthorization") @FormUrlEncoded
    Observable<BaseV3Response> createPaymentAuthorization(@FieldMap BaseBody args);

    @POST("oauth2Authentication") @FormUrlEncoded Observable<OAuth> oauth2Authentication(
        @FieldMap BaseBody args, @Header(WebService.BYPASS_HEADER_KEY) boolean bypassCache);

    @POST("getUserInfo") @FormUrlEncoded Observable<CheckUserCredentialsJson> getUserInfo(
        @FieldMap BaseBody args, @Header(WebService.BYPASS_HEADER_KEY) boolean bypassCache);

    @POST("checkUserCredentials") @FormUrlEncoded
    Observable<CheckUserCredentialsJson> checkUserCredentials(@FieldMap BaseBody args,
        @Header(WebService.BYPASS_HEADER_KEY) boolean bypassCache);

    @POST("createUser") @FormUrlEncoded Observable<BaseV3Response> createUser(
        @FieldMap BaseBody args, @Header(WebService.BYPASS_HEADER_KEY) boolean bypassCache);

    @POST("createUser") @Multipart Observable<BaseV3Response> createUserWithFile(
        @Part MultipartBody.Part user_avatar, @PartMap() HashMapNotNull<String, RequestBody> args,
        @Header(WebService.BYPASS_HEADER_KEY) boolean bypassCache);

    @POST("changeUserSettings") @FormUrlEncoded Observable<BaseV3Response> changeUserSettings(
        @FieldMap BaseBody args, @Header(WebService.BYPASS_HEADER_KEY) boolean bypassCache);

    @POST("changeUserRepoSubscription") @FormUrlEncoded
    Observable<BaseV3Response> changeUserRepoSubscription(@FieldMap BaseBody args);
  }
}
