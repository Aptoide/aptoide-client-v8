/*
 * Copyright (c) 2016.
 * Modified on 21/07/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v1.notification;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.text.TextUtils;
import cm.aptoide.pt.dataprovider.exception.AptoideWsV7Exception;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.model.v1.GetPullNotificationsResponse;
import cm.aptoide.pt.dataprovider.model.v7.BaseV7Response;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v1.PnpV1WebService;
import cm.aptoide.pt.dataprovider.ws.v1.Service;
import cm.aptoide.pt.preferences.toolbox.ToolboxManager;
import cm.aptoide.pt.utils.AptoideUtils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import retrofit2.adapter.rxjava.HttpException;
import rx.Observable;

public class PullSocialNotificationRequest
    extends PnpV1WebService<List<GetPullNotificationsResponse>> {
  private static List<Integer> pretendedNotificationTypes;
  private final String INVALID_ACCESS_TOKEN_CODE = "AUTH-2";
  private final Map<String, String> options;
  private final String id;
  private final TokenInvalidator tokenInvalidator;
  private final BodyInterceptor<Map<String, String>> interceptor;
  private boolean accessTokenRetry = false;

  private PullSocialNotificationRequest(String id, Map<String, String> options,
      OkHttpClient okHttpClient, Converter.Factory converterFactory,
      TokenInvalidator tokenInvalidator, BodyInterceptor<Map<String, String>> interceptor) {
    super(okHttpClient, converterFactory);
    this.options = options;
    this.id = id;
    this.tokenInvalidator = tokenInvalidator;
    this.interceptor = interceptor;
  }

  public static PullSocialNotificationRequest of(String uniqueIdentifier, String versionName,
      String appId, OkHttpClient httpClient, Converter.Factory converterFactory, String oemid,
      SharedPreferences sharedPreferences, Resources resources, TokenInvalidator tokenInvalidator,
      BodyInterceptor<Map<String, String>> interceptor, boolean isLogged) {

    Map<String, String> options = new HashMap<>();
    pretendedNotificationTypes = new ArrayList<>();

    options.put("language", AptoideUtils.SystemU.getCountryCode(resources));
    options.put("aptoide_version", versionName);

    if (isLogged) {
      pretendedNotificationTypes.add(1);
      pretendedNotificationTypes.add(2);
      pretendedNotificationTypes.add(5);
      pretendedNotificationTypes.add(6);
    }
    pretendedNotificationTypes.add(3);

    if (!TextUtils.isEmpty(oemid)) {
      options.put("oem_id", oemid);
    }
    options.put("aptoide_package", appId);
    if (ToolboxManager.isDebug(sharedPreferences)) {
      options.put("debug", "true");
    }
    options.put("status_in_json", String.valueOf(true));

    return new PullSocialNotificationRequest(uniqueIdentifier, options, httpClient,
        converterFactory, tokenInvalidator, interceptor);
  }

  @Override
  protected Observable<List<GetPullNotificationsResponse>> loadDataFromNetwork(Service interfaces,
      boolean bypassCache) {
    return interceptor.intercept(options)
        .toObservable()
        .flatMap(options -> handleToken(
            interfaces.getPullSocialNotifications(true, id, pretendedNotificationTypes, options),
            true));
  }

  private Observable<List<GetPullNotificationsResponse>> handleToken(
      Observable<List<GetPullNotificationsResponse>> observable, boolean bypassCache) {

    return observable.onErrorResumeNext(throwable -> {
      if (throwable instanceof HttpException) {
        try {

          BaseV7Response genericResponse =
              (BaseV7Response) converterFactory.responseBodyConverter(BaseV7Response.class, null,
                  null)
                  .convert(((HttpException) throwable).response()
                      .errorBody());
          if (INVALID_ACCESS_TOKEN_CODE.equals(genericResponse.getError()
              .getCode())) {
            if (!accessTokenRetry) {
              accessTokenRetry = true;
              return tokenInvalidator.invalidateAccessToken()
                  .andThen(observe(bypassCache));
            }
          } else {
            AptoideWsV7Exception exception = new AptoideWsV7Exception(throwable);
            exception.setBaseResponse(genericResponse);
            return Observable.error(exception);
          }
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
      return Observable.error(throwable);
    });
  }
}
