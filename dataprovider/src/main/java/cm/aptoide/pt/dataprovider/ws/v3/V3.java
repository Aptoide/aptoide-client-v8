/*
 * Copyright (c) 2016.
 * Modified on 03/08/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v3;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import cm.aptoide.pt.dataprovider.BuildConfig;
import cm.aptoide.pt.dataprovider.WebService;
import cm.aptoide.pt.dataprovider.exception.AptoideWsV3Exception;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.model.v3.BaseV3Response;
import cm.aptoide.pt.dataprovider.model.v3.ErrorResponse;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.preferences.toolbox.ToolboxManager;
import java.io.IOException;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import retrofit2.adapter.rxjava.HttpException;
import rx.Observable;

/**
 * Created on 21/07/16.
 */
public abstract class V3<U> extends WebService<Service, U> {

  protected final BaseBody map;
  private final String INVALID_ACCESS_TOKEN_CODE = "invalid_token";
  private final BodyInterceptor<BaseBody> bodyInterceptor;
  private final TokenInvalidator tokenInvalidator;
  private boolean accessTokenRetry = false;

  protected V3(BaseBody baseBody, OkHttpClient httpClient, Converter.Factory converterFactory,
      BodyInterceptor<BaseBody> bodyInterceptor, TokenInvalidator tokenInvalidator,
      SharedPreferences sharedPreferences) {
    super(Service.class, httpClient, converterFactory, getHost(sharedPreferences));
    this.map = baseBody;
    this.bodyInterceptor = bodyInterceptor;
    this.tokenInvalidator = tokenInvalidator;
  }

  protected V3(OkHttpClient okHttpClient, Converter.Factory converterFactory,
      BodyInterceptor<BaseBody> bodyInterceptor, TokenInvalidator tokenInvalidator,
      SharedPreferences sharedPreferences) {
    this(new BaseBody(), okHttpClient, converterFactory, bodyInterceptor, tokenInvalidator,
        sharedPreferences);
  }

  public static String getHost(SharedPreferences sharedPreferences) {
    return (ToolboxManager.isToolboxEnableHttpScheme(sharedPreferences) ? "http"
        : BuildConfig.APTOIDE_WEB_SERVICES_SCHEME)
        + "://"
        + BuildConfig.APTOIDE_WEB_SERVICES_HOST
        + "/webservices/3/";
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
                      return tokenInvalidator.invalidateAccessToken()
                          .andThen(V3.this.observe(bypassCache));
                    }
                  } else {
                    AptoideWsV3Exception exception = new AptoideWsV3Exception(throwable);
                    exception.setBaseResponse(genericResponseV3);
                    return Observable.error(exception);
                  }
                } catch (IOException e) {
                  e.printStackTrace();
                }
              }
              return Observable.error(throwable);
            }));
  }
}
