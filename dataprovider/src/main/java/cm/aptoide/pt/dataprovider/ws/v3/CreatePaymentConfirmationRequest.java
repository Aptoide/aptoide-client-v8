/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 16/11/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v3;

import cm.aptoide.pt.dataprovider.NetworkOperatorManager;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.model.v3.BaseV3Response;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Observable;

/**
 * Created by marcelobenites on 16/11/16.
 */
public class CreatePaymentConfirmationRequest extends V3<BaseV3Response> {

  private CreatePaymentConfirmationRequest(BaseBody baseBody,
      BodyInterceptor<BaseBody> bodyInterceptor, OkHttpClient httpClient,
      Converter.Factory converterFactory) {
    super(baseBody, httpClient, converterFactory, bodyInterceptor);
  }

  public static CreatePaymentConfirmationRequest ofInApp(int productId, int paymentId,
      NetworkOperatorManager operatorManager, String developerPayload,
      BodyInterceptor<BaseBody> bodyInterceptor, OkHttpClient httpClient,
      Converter.Factory converterFactory) {
    final BaseBody args = getBaseBody(productId, paymentId, operatorManager);
    args.put("developerPayload", developerPayload);
    return new CreatePaymentConfirmationRequest(args, bodyInterceptor, httpClient,
        converterFactory);
  }

  private static BaseBody getBaseBody(int productId, int paymentId,
      NetworkOperatorManager operatorManager) {
    BaseBody body = new BaseBody();
    body.put("productid", String.valueOf(productId));
    body.put("payType", String.valueOf(paymentId));
    body.put("reqType", "rest");

    addNetworkInformation(operatorManager, body);

    return body;
  }

  public static CreatePaymentConfirmationRequest ofInApp(int productId, int paymentId,
      NetworkOperatorManager operatorManager, String developerPayload, String paymentConfirmationId,
      BodyInterceptor<BaseBody> bodyInterceptor, OkHttpClient httpClient,
      Converter.Factory converterFactory) {
    final BaseBody args = getBaseBody(productId, paymentId, operatorManager);
    args.put("paykey", paymentConfirmationId);
    args.put("developerPayload", developerPayload);
    return new CreatePaymentConfirmationRequest(args, bodyInterceptor, httpClient,
        converterFactory);
  }

  public static CreatePaymentConfirmationRequest ofPaidApp(int productId, int paymentId,
      NetworkOperatorManager operatorManager, String store,
      BodyInterceptor<BaseBody> bodyInterceptor, OkHttpClient httpClient,
      Converter.Factory converterFactory) {
    final BaseBody args = getBaseBody(productId, paymentId, operatorManager);
    args.put("repo", store);
    return new CreatePaymentConfirmationRequest(args, bodyInterceptor, httpClient,
        converterFactory);
  }

  public static CreatePaymentConfirmationRequest ofPaidApp(int productId, int paymentId,
      NetworkOperatorManager operatorManager, String store, String paymentConfirmationId,
      BodyInterceptor<BaseBody> bodyInterceptor, OkHttpClient httpClient,
      Converter.Factory converterFactory) {
    final BaseBody args = getBaseBody(productId, paymentId, operatorManager);
    args.put("paykey", paymentConfirmationId);
    args.put("repo", store);
    return new CreatePaymentConfirmationRequest(args, bodyInterceptor, httpClient,
        converterFactory);
  }

  @Override protected Observable<BaseV3Response> loadDataFromNetwork(Interfaces interfaces,
      boolean bypassCache) {
    return interfaces.createPaymentConfirmation(map);
  }
}
