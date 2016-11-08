/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 24/05/2016.
 */

package cm.aptoide.accountmanager.ws;

import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.accountmanager.ws.responses.ChangeUserSettingsResponse;
import cm.aptoide.accountmanager.ws.responses.CheckUserCredentialsJson;
import cm.aptoide.accountmanager.ws.responses.GenericResponseV3;
import cm.aptoide.accountmanager.ws.responses.GetUserRepoSubscription;
import cm.aptoide.accountmanager.ws.responses.OAuth;
import cm.aptoide.pt.networkclient.WebService;
import cm.aptoide.pt.networkclient.okhttp.OkHttpClientFactory;
import cm.aptoide.pt.networkclient.okhttp.UserAgentGenerator;
import cm.aptoide.pt.networkclient.okhttp.newCache.PostCacheInterceptor;
import cm.aptoide.pt.networkclient.util.HashMapNotNull;
import cm.aptoide.pt.preferences.Application;
import java.io.IOException;
import lombok.Getter;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import retrofit2.adapter.rxjava.HttpException;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by neuro on 25-04-2016.
 */
public abstract class v3accountManager<U> extends WebService<v3accountManager.Interfaces, U> {

  @Getter protected final BaseBody map;
  private final String INVALID_ACCESS_TOKEN_CODE = "invalid_token";
  private boolean accessTokenRetry = false;

  v3accountManager() {
    super(Interfaces.class,
        OkHttpClientFactory.getSingletonClient(new UserAgentGenerator() {
          @Override public String generateUserAgent() {
            return AptoideAccountManager.getUserEmail();
          }
        }),
        WebService.getDefaultConverter(),
        "https://webservices.aptoide.com/webservices/");
    this.map = new BaseBody();
  }

  v3accountManager(OkHttpClient httpClient, Converter.Factory converterFactory) {
    super(Interfaces.class, httpClient, converterFactory,
        "https://webservices.aptoide.com/webservices/");
    this.map = new BaseBody();
  }

  @Override public Observable<U> observe(boolean bypassCache) {
    return super.observe(bypassCache)
        .observeOn(AndroidSchedulers.mainThread())
        .subscribeOn(Schedulers.io())
        .onErrorResumeNext(throwable -> {
          if (throwable instanceof HttpException) {
            try {

              GenericResponseV3 genericResponseV3 =
                  (GenericResponseV3) converterFactory.responseBodyConverter(
                      GenericResponseV3.class, null, null)
                      .convert(((HttpException) throwable).response().errorBody());

              if (INVALID_ACCESS_TOKEN_CODE.equals(genericResponseV3.getError())) {

                if (!accessTokenRetry) {
                  accessTokenRetry = true;
                  return AptoideAccountManager.invalidateAccessToken(Application.getContext())
                      .flatMap(s -> {
                        this.map.setAccess_token(s);
                        return v3accountManager.this.observe(bypassCache)
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
        })
        .observeOn(AndroidSchedulers.mainThread());
  }

  interface Interfaces {

    @FormUrlEncoded @POST("3/oauth2Authentication")
    @Headers({ PostCacheInterceptor.BYPASS_HEADER_KEY + ":" + PostCacheInterceptor.BYPASS_HEADER_VALUE })
    Observable<OAuth> oauth2Authentication(@FieldMap HashMapNotNull<String, String> args);

    @FormUrlEncoded @POST("3/getUserInfo")
    @Headers({ PostCacheInterceptor.BYPASS_HEADER_KEY + ":" + PostCacheInterceptor.BYPASS_HEADER_VALUE })
    Observable<CheckUserCredentialsJson> getUserInfo(@FieldMap HashMapNotNull<String, String> args);

    @POST("3/createUser") @FormUrlEncoded
    @Headers({ PostCacheInterceptor.BYPASS_HEADER_KEY + ":" + PostCacheInterceptor.BYPASS_HEADER_VALUE })
    Observable<OAuth> createUser(@FieldMap HashMapNotNull<String, String> args);

    @POST("3/changeUserSettings") @FormUrlEncoded
    @Headers({ PostCacheInterceptor.BYPASS_HEADER_KEY + ":" + PostCacheInterceptor.BYPASS_HEADER_VALUE })
    Observable<ChangeUserSettingsResponse> changeUserSettings(
        @FieldMap HashMapNotNull<String, String> args);

    @POST("3/changeUserRepoSubscription") @FormUrlEncoded
    Observable<GenericResponseV3> changeUserRepoSubscription(
        @FieldMap HashMapNotNull<String, String> args);

    @POST("3/getUserRepoSubscription") @FormUrlEncoded
    Observable<GetUserRepoSubscription> getUserRepos(@FieldMap HashMapNotNull<String, String> args);
  }
}
