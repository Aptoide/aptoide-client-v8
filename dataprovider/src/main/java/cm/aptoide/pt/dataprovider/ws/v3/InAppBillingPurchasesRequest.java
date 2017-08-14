/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 12/08/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v3;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.model.v3.InAppBillingPurchasesResponse;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Observable;

/**
 * Created by marcelobenites on 8/12/16.
 */
public class InAppBillingPurchasesRequest extends V3<InAppBillingPurchasesResponse> {

  private InAppBillingPurchasesRequest(BaseBody baseBody, BodyInterceptor<BaseBody> bodyInterceptor,
      OkHttpClient httpClient, Converter.Factory converterFactory,
      TokenInvalidator tokenInvalidator, SharedPreferences sharedPreferences) {
    super(baseBody, httpClient, converterFactory, bodyInterceptor, tokenInvalidator,
        sharedPreferences);
  }

  public static InAppBillingPurchasesRequest of(int apiVersion, String packageName,
      BodyInterceptor<BaseBody> bodyInterceptor, OkHttpClient httpClient,
      Converter.Factory converterFactory, TokenInvalidator tokenInvalidator,
      SharedPreferences sharedPreferences, int packageVersionCode) {
    BaseBody args = getBaseArgs(apiVersion, packageName, packageVersionCode);
    return new InAppBillingPurchasesRequest(args, bodyInterceptor, httpClient, converterFactory,
        tokenInvalidator, sharedPreferences);
  }

  @NonNull
  private static BaseBody getBaseArgs(int apiVersion, String packageName, int packageVersionCode) {
    BaseBody args = new BaseBody();
    args.put("mode", "json");
    args.put("aptvercode", packageVersionCode);
    args.put("package", packageName);
    args.put("apiversion", String.valueOf(apiVersion));
    args.put("reqtype", "iabpurchases");
    args.put("purchasetype", "inapp");
    return args;
  }

  @Override protected Observable<InAppBillingPurchasesResponse> loadDataFromNetwork(Service service,
      boolean bypassCache) {
    return service.getInAppBillingPurchases(map);
  }
}
