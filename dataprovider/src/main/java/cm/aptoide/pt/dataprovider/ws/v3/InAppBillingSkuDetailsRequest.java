/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 11/08/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v3;

import android.content.SharedPreferences;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.model.v3.InAppBillingSkuDetailsResponse;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import java.util.List;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Observable;

/**
 * Created by marcelobenites on 8/11/16.
 */
public class InAppBillingSkuDetailsRequest extends V3<InAppBillingSkuDetailsResponse> {

  public InAppBillingSkuDetailsRequest(BaseBody baseBody, BodyInterceptor<BaseBody> bodyInterceptor,
      OkHttpClient httpClient, Converter.Factory converterFactory,
      TokenInvalidator tokenInvalidator, SharedPreferences sharedPreferences) {
    super(baseBody, httpClient, converterFactory, bodyInterceptor, tokenInvalidator,
        sharedPreferences);
  }

  public static InAppBillingSkuDetailsRequest of(int apiVersion, String packageName,
      List<String> skuList, String type, BodyInterceptor<BaseBody> bodyInterceptor,
      OkHttpClient httpClient, Converter.Factory converterFactory,
      TokenInvalidator tokenInvalidator, SharedPreferences sharedPreferences) {
    BaseBody args = new BaseBody();
    args.put("mode", "json");
    args.put("package", packageName);
    args.put("apiversion", String.valueOf(apiVersion));
    args.put("reqtype", "iabskudetails");
    args.put("purchasetype", type);

    if (!skuList.isEmpty()) {
      final StringBuilder stringBuilder = new StringBuilder();
      for (String sku : skuList) {
        stringBuilder.append(sku);
        stringBuilder.append(",");
      }
      stringBuilder.deleteCharAt(stringBuilder.length() - 1);
      args.put("skulist", stringBuilder.toString());
    }

    return new InAppBillingSkuDetailsRequest(args, bodyInterceptor, httpClient, converterFactory,
        tokenInvalidator, sharedPreferences);
  }

  @Override
  protected Observable<InAppBillingSkuDetailsResponse> loadDataFromNetwork(Service service,
      boolean bypassCache) {
    return service.getInAppBillingSkuDetails(map);
  }
}
