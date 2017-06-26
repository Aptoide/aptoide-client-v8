/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 17/10/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v3;

import android.content.SharedPreferences;
import cm.aptoide.pt.dataprovider.NetworkOperatorManager;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.model.v3.PaymentConfirmationResponse;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Observable;

/**
 * Created by marcelobenites on 17/10/16.
 */

public class GetPaymentConfirmationRequest extends V3<PaymentConfirmationResponse> {

  public GetPaymentConfirmationRequest(BaseBody baseBody, BodyInterceptor<BaseBody> bodyInterceptor,
      OkHttpClient httpClient, Converter.Factory converterFactory,
      TokenInvalidator tokenInvalidator, SharedPreferences sharedPreferences) {
    super(baseBody, httpClient, converterFactory, bodyInterceptor, tokenInvalidator,
        sharedPreferences);
  }

  public static GetPaymentConfirmationRequest of(int productId,
      NetworkOperatorManager operatorManager, int apiVersion,
      BodyInterceptor<BaseBody> bodyInterceptor, OkHttpClient httpClient,
      Converter.Factory converterFactory, TokenInvalidator tokenInvalidator,
      SharedPreferences sharedPreferences) {
    final BaseBody args = getBaseBody(productId, operatorManager, sharedPreferences);
    args.put("reqtype", "iabpurchasestatus");
    args.put("apiversion", String.valueOf(apiVersion));
    return new GetPaymentConfirmationRequest(args, bodyInterceptor, httpClient, converterFactory,
        tokenInvalidator, sharedPreferences);
  }

  private static BaseBody getBaseBody(int productId, NetworkOperatorManager operatorManager,
      SharedPreferences sharedPreferences) {
    final BaseBody args = new BaseBody();
    args.put("mode", "json");
    args.put("payreqtype", "rest");
    args.put("productid", String.valueOf(productId));

    addNetworkInformation(operatorManager, args, sharedPreferences);
    return args;
  }

  public static GetPaymentConfirmationRequest of(int productId,
      NetworkOperatorManager operatorManager, BodyInterceptor<BaseBody> bodyInterceptor,
      OkHttpClient httpClient, Converter.Factory converterFactory,
      TokenInvalidator tokenInvalidator, SharedPreferences sharedPreferences) {
    final BaseBody args = getBaseBody(productId, operatorManager, sharedPreferences);
    args.put("reqtype", "apkpurchasestatus");
    return new GetPaymentConfirmationRequest(args, bodyInterceptor, httpClient, converterFactory,
        tokenInvalidator, sharedPreferences);
  }

  @Override
  protected Observable<PaymentConfirmationResponse> loadDataFromNetwork(Service service,
      boolean bypassCache) {
    return service.getPaymentConfirmation(map);
  }
}
