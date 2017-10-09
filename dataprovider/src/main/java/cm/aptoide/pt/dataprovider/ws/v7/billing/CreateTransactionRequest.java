package cm.aptoide.pt.dataprovider.ws.v7.billing;

import android.content.SharedPreferences;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.model.v7.BaseV7Response;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.V7;
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

  public static CreateTransactionRequest of(long productId, int serviceId, String payload,
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

  @Override protected Observable<ResponseBody> loadDataFromNetwork(Interfaces interfaces,
      boolean bypassCache) {
    return interfaces.createBillingTransaction(body, bypassCache);
  }

  public static class RequestBody extends BaseBody {

    private long productId;
    private int serviceId;
    private String payload;

    public long getProductId() {
      return productId;
    }

    public void setProductId(long productId) {
      this.productId = productId;
    }

    public int getServiceId() {
      return serviceId;
    }

    public void setServiceId(int serviceId) {
      this.serviceId = serviceId;
    }

    public String getPayload() {
      return payload;
    }

    public void setPayload(String payload) {
      this.payload = payload;
    }
  }

  public static class ResponseBody extends BaseV7Response {

    private GetTransactionsRequest.ResponseBody.Data.Transaction data;

    public GetTransactionsRequest.ResponseBody.Data.Transaction getData() {
      return data;
    }

    public void setData(GetTransactionsRequest.ResponseBody.Data.Transaction data) {
      this.data = data;
    }
  }
}
