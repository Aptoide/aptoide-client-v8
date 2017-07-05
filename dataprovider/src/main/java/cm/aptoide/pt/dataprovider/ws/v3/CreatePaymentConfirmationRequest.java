/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 16/11/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v3;

import android.content.SharedPreferences;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.model.v3.BaseV3Response;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Observable;

/**
 * Created by marcelobenites on 16/11/16.
 */
public class CreatePaymentConfirmationRequest extends V3<BaseV3Response> {

  private CreatePaymentConfirmationRequest(BaseBody baseBody,
      BodyInterceptor<BaseBody> bodyInterceptor, OkHttpClient httpClient,
      Converter.Factory converterFactory, TokenInvalidator tokenInvalidator,
      SharedPreferences sharedPreferences) {
    super(baseBody, httpClient, converterFactory, bodyInterceptor, tokenInvalidator,
        sharedPreferences);
  }

  public static CreatePaymentConfirmationRequest ofInApp(int productId, int paymentId,
      String developerPayload, BodyInterceptor<BaseBody> bodyInterceptor, OkHttpClient httpClient,
      Converter.Factory converterFactory, TokenInvalidator tokenInvalidator,
      SharedPreferences sharedPreferences, int versionCode) {
    final BaseBody args = getBaseBody(productId, paymentId, versionCode);
    args.put("developerPayload", developerPayload);
    return new CreatePaymentConfirmationRequest(args, bodyInterceptor, httpClient, converterFactory,
        tokenInvalidator, sharedPreferences);
  }

  private static BaseBody getBaseBody(int productId, int paymentId, int versionCode) {
    final BaseBody body = new BaseBody();
    body.put("productid", String.valueOf(productId));
    body.put("payType", String.valueOf(paymentId));
    body.put("reqType", "rest");
    body.put("app_vercode", versionCode);
    return body;
  }

  public static CreatePaymentConfirmationRequest ofInApp(int productId, int paymentId,
      String developerPayload, String paymentConfirmationId,
      BodyInterceptor<BaseBody> bodyInterceptor, OkHttpClient httpClient,
      Converter.Factory converterFactory, TokenInvalidator tokenInvalidator,
      SharedPreferences sharedPreferences, int versionCode) {
    final BaseBody args = getBaseBody(productId, paymentId, versionCode);
    args.put("paykey", paymentConfirmationId);
    args.put("developerPayload", developerPayload);
    return new CreatePaymentConfirmationRequest(args, bodyInterceptor, httpClient, converterFactory,
        tokenInvalidator, sharedPreferences);
  }

  public static CreatePaymentConfirmationRequest ofPaidApp(int productId, int paymentId,
      String store, BodyInterceptor<BaseBody> bodyInterceptor, OkHttpClient httpClient,
      Converter.Factory converterFactory, TokenInvalidator tokenInvalidator,
      SharedPreferences sharedPreferences, int versionCode) {
    final BaseBody args = getBaseBody(productId, paymentId, versionCode);
    args.put("repo", store);
    return new CreatePaymentConfirmationRequest(args, bodyInterceptor, httpClient, converterFactory,
        tokenInvalidator, sharedPreferences);
  }

  public static CreatePaymentConfirmationRequest ofPaidApp(int productId, int paymentId,
      String store, String paymentConfirmationId, BodyInterceptor<BaseBody> bodyInterceptor,
      OkHttpClient httpClient, Converter.Factory converterFactory,
      TokenInvalidator tokenInvalidator, SharedPreferences sharedPreferences, int versionCode) {
    final BaseBody args = getBaseBody(productId, paymentId, versionCode);
    args.put("paykey", paymentConfirmationId);
    args.put("repo", store);
    return new CreatePaymentConfirmationRequest(args, bodyInterceptor, httpClient, converterFactory,
        tokenInvalidator, sharedPreferences);
  }

  @Override
  protected Observable<BaseV3Response> loadDataFromNetwork(Service service, boolean bypassCache) {
    return service.createPaymentConfirmation(map);
  }
}
