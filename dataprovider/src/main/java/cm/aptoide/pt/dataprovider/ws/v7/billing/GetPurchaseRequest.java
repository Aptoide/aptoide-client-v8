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
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import retrofit2.Response;
import rx.Observable;

public class GetPurchaseRequest
    extends V7<Response<GetPurchaseRequest.ResponseBody>, GetPurchaseRequest.RequestBody> {

  public GetPurchaseRequest(GetPurchaseRequest.RequestBody body, String baseHost,
      OkHttpClient httpClient, Converter.Factory converterFactory, BodyInterceptor bodyInterceptor,
      TokenInvalidator tokenInvalidator) {
    super(body, baseHost, httpClient, converterFactory, bodyInterceptor, tokenInvalidator);
  }

  public static String getHost(SharedPreferences sharedPreferences) {
    return (ToolboxManager.isToolboxEnableHttpScheme(sharedPreferences) ? "http"
        : BuildConfig.APTOIDE_WEB_SERVICES_SCHEME)
        + "://"
        + BuildConfig.APTOIDE_WEB_SERVICES_WRITE_V7_HOST
        + "/api/7/";
  }

  public static GetPurchaseRequest of(long productId, BodyInterceptor<BaseBody> bodyInterceptor,
      OkHttpClient httpClient, Converter.Factory converterFactory,
      TokenInvalidator tokenInvalidator, SharedPreferences sharedPreferences) {
    final RequestBody body = new RequestBody();
    body.setProductId(productId);
    return new GetPurchaseRequest(body, getHost(sharedPreferences), httpClient, converterFactory,
        bodyInterceptor, tokenInvalidator);
  }

  @Override protected Observable<Response<GetPurchaseRequest.ResponseBody>> loadDataFromNetwork(
      Interfaces interfaces, boolean bypassCache) {
    return interfaces.getBillingPurchase(body, bypassCache);
  }

  public static class RequestBody extends BaseBody {

    private long productId;

    public long getProductId() {
      return productId;
    }

    public void setProductId(long productId) {
      this.productId = productId;
    }
  }

  public static class ResponseBody extends BaseV7Response {

    private PurchaseResponse data;

    public PurchaseResponse getData() {
      return data;
    }

    public void setData(PurchaseResponse data) {
      this.data = data;
    }
  }
}
