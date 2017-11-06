package cm.aptoide.pt.dataprovider.ws.v7.billing;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import cm.aptoide.pt.dataprovider.BuildConfig;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.model.v7.BaseV7Response;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.V7;
import cm.aptoide.pt.preferences.toolbox.ToolboxManager;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Observable;

public class CreateTransactionRequest
    extends V7<CreateTransactionRequest.ResponseBody, CreateTransactionRequest.RequestBody> {

  private CreateTransactionRequest(RequestBody body, String baseHost, OkHttpClient httpClient,
      Converter.Factory converterFactory, BodyInterceptor bodyInterceptor,
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

  public static CreateTransactionRequest of(long productId, long serviceId, String payload,
      BodyInterceptor<BaseBody> bodyInterceptor, OkHttpClient httpClient,
      Converter.Factory converterFactory, TokenInvalidator tokenInvalidator,
      SharedPreferences sharedPreferences) {
    final RequestBody body = new RequestBody();
    body.setProductId(productId);
    body.setServiceId(serviceId);
    body.setPayload(payload);
    return new CreateTransactionRequest(body, getHost(sharedPreferences), httpClient,
        converterFactory, bodyInterceptor, tokenInvalidator);
  }

  public static CreateTransactionRequest of(long productId, long serviceId, String payload,
      String token, BodyInterceptor<BaseBody> bodyInterceptor, OkHttpClient httpClient,
      Converter.Factory converterFactory, TokenInvalidator tokenInvalidator,
      SharedPreferences sharedPreferences) {
    final RequestBody body = new RequestBody();
    body.setProductId(productId);
    body.setServiceId(serviceId);
    body.setPayload(payload);
    final RequestBody.Data serviceData = new RequestBody.Data();
    serviceData.setToken(token);
    body.setServiceData(serviceData);
    return new CreateTransactionRequest(body, getHost(sharedPreferences), httpClient,
        converterFactory, bodyInterceptor, tokenInvalidator);
  }

  @Override protected Observable<ResponseBody> loadDataFromNetwork(Interfaces interfaces,
      boolean bypassCache) {
    return interfaces.createBillingTransaction(body, bypassCache);
  }

  public static class RequestBody extends BaseBody {

    private long productId;
    private long serviceId;
    private String payload;
    private Data serviceData;

    public Data getServiceData() {
      return serviceData;
    }

    public void setServiceData(Data serviceData) {
      this.serviceData = serviceData;
    }

    public long getProductId() {
      return productId;
    }

    public void setProductId(long productId) {
      this.productId = productId;
    }

    public long getServiceId() {
      return serviceId;
    }

    public void setServiceId(long serviceId) {
      this.serviceId = serviceId;
    }

    public String getPayload() {
      return payload;
    }

    public void setPayload(String payload) {
      this.payload = payload;
    }

    public static class Data {
      private String token;

      public String getToken() {
        return token;
      }

      public void setToken(String token) {
        this.token = token;
      }
    }
  }

  public static class ResponseBody extends BaseV7Response {

    private GetTransactionRequest.ResponseBody.Transaction data;

    public GetTransactionRequest.ResponseBody.Transaction getData() {
      return data;
    }

    public void setData(GetTransactionRequest.ResponseBody.Transaction data) {
      this.data = data;
    }
  }
}
