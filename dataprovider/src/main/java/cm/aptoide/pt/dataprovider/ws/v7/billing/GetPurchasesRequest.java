/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 12/08/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v7.billing;

import android.content.SharedPreferences;
import cm.aptoide.pt.dataprovider.BuildConfig;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.model.v7.BaseV7Response;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.V7;
import cm.aptoide.pt.preferences.toolbox.ToolboxManager;
import java.util.List;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import retrofit2.Response;
import rx.Observable;

public class GetPurchasesRequest
    extends V7<Response<GetPurchasesRequest.ResponseBody>, GetPurchasesRequest.RequestBody> {

  public GetPurchasesRequest(GetPurchasesRequest.RequestBody body, String baseHost,
      OkHttpClient httpClient, Converter.Factory converterFactory, BodyInterceptor bodyInterceptor,
      TokenInvalidator tokenInvalidator) {
    super(body, baseHost, httpClient, converterFactory, bodyInterceptor, tokenInvalidator);
  }

  public static String getHost(SharedPreferences sharedPreferences) {
    return (ToolboxManager.isToolboxEnableHttpScheme(sharedPreferences) ? "http"
        : BuildConfig.APTOIDE_WEB_SERVICES_SCHEME)
        + "://"
        + BuildConfig.APTOIDE_WEB_SERVICES_READ_V7_HOST
        + "/api/7/";
  }

  public static GetPurchasesRequest of(String merchantName,
      BodyInterceptor<BaseBody> bodyInterceptor, OkHttpClient httpClient,
      Converter.Factory converterFactory, TokenInvalidator tokenInvalidator,
      SharedPreferences sharedPreferences) {
    final RequestBody body = new RequestBody();
    body.setPackageName(merchantName);
    return new GetPurchasesRequest(body, getHost(sharedPreferences), httpClient, converterFactory,
        bodyInterceptor, tokenInvalidator);
  }

  @Override protected Observable<Response<GetPurchasesRequest.ResponseBody>> loadDataFromNetwork(
      Interfaces interfaces, boolean bypassCache) {
    return interfaces.getBillingPurchases(body, bypassCache);
  }

  public static class RequestBody extends BaseBody {

    private String packageName;

    public String getPackageName() {
      return packageName;
    }

    public void setPackageName(String packageName) {
      this.packageName = packageName;
    }
  }

  public static class ResponseBody extends BaseV7Response {

    private List<PurchaseResponse> list;

    public List<PurchaseResponse> getList() {
      return list;
    }

    public void setList(List<PurchaseResponse> list) {
      this.list = list;
    }
  }
}
