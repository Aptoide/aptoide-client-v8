/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 17/10/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v3;

import cm.aptoide.pt.dataprovider.NetworkOperatorManager;
import cm.aptoide.pt.dataprovider.ws.v7.BodyInterceptor;
import cm.aptoide.pt.model.v3.PaymentConfirmationResponse;
import cm.aptoide.pt.networkclient.WebService;
import cm.aptoide.pt.networkclient.okhttp.OkHttpClientFactory;
import cm.aptoide.pt.preferences.secure.SecurePreferences;
import rx.Observable;

/**
 * Created by marcelobenites on 17/10/16.
 */

public class GetPaymentConfirmationRequest extends V3<PaymentConfirmationResponse> {

  public GetPaymentConfirmationRequest(BaseBody baseBody,
      BodyInterceptor<BaseBody> bodyInterceptor) {
    super(baseBody,
        OkHttpClientFactory.getSingletonClient(() -> SecurePreferences.getUserAgent(), false),
        WebService.getDefaultConverter(), bodyInterceptor);
  }

  public static GetPaymentConfirmationRequest of(int productId,
      NetworkOperatorManager operatorManager, int apiVersion, String accessToken,
      BodyInterceptor<BaseBody> bodyInterceptor) {
    final BaseBody args = getBaseBody(productId, operatorManager, accessToken);
    args.put("reqtype", "iabpurchasestatus");
    args.put("apiversion", String.valueOf(apiVersion));
    return new GetPaymentConfirmationRequest(args, bodyInterceptor);
  }

  private static BaseBody getBaseBody(int productId, NetworkOperatorManager operatorManager,
      String accessToken) {
    final BaseBody args = new BaseBody();
    args.put("mode", "json");
    args.put("payreqtype", "rest");
    args.put("productid", String.valueOf(productId));
    args.put("access_token", accessToken);

    addNetworkInformation(operatorManager, args);
    return args;
  }

  public static GetPaymentConfirmationRequest of(int productId,
      NetworkOperatorManager operatorManager, String accessToken,
      BodyInterceptor<BaseBody> bodyInterceptor) {
    final BaseBody args = getBaseBody(productId, operatorManager, accessToken);
    args.put("reqtype", "apkpurchasestatus");
    return new GetPaymentConfirmationRequest(args, bodyInterceptor);
  }

  @Override
  protected Observable<PaymentConfirmationResponse> loadDataFromNetwork(Interfaces interfaces,
      boolean bypassCache) {
    return interfaces.getPaymentConfirmation(map);
  }
}
