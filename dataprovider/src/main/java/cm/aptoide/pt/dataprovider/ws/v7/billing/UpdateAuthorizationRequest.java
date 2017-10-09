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

public class UpdateAuthorizationRequest
    extends V7<UpdateAuthorizationRequest.ResponseBody, UpdateAuthorizationRequest.RequestBody> {

  private UpdateAuthorizationRequest(RequestBody body, String baseHost, OkHttpClient httpClient,
      Converter.Factory converterFactory, BodyInterceptor bodyInterceptor,
      TokenInvalidator tokenInvalidator) {
    super(body, baseHost, httpClient, converterFactory, bodyInterceptor, tokenInvalidator);
  }

  public static UpdateAuthorizationRequest of(long transactionId, String metadata,
      SharedPreferences sharedPreferences, OkHttpClient httpClient,
      Converter.Factory converterFactory, BodyInterceptor<BaseBody> bodyInterceptorV7,
      TokenInvalidator tokenInvalidator) {
    final RequestBody requestBody = new RequestBody();
    requestBody.setTransactionId(transactionId);
    requestBody.setMetadata(metadata);
    return new UpdateAuthorizationRequest(requestBody, getHost(sharedPreferences), httpClient,
        converterFactory, bodyInterceptorV7, tokenInvalidator);
  }

  @Override protected Observable<ResponseBody> loadDataFromNetwork(Interfaces interfaces,
      boolean bypassCache) {
    return interfaces.updateBillingAuthorization(body, bypassCache);
  }

  public static class RequestBody extends BaseBody {

    private long transactionId;
    private String metadata;

    public long getTransactionId() {
      return transactionId;
    }

    public void setTransactionId(long transactionId) {
      this.transactionId = transactionId;
    }

    public String getMetadata() {
      return metadata;
    }

    public void setMetadata(String metadata) {
      this.metadata = metadata;
    }
  }

  public static class ResponseBody extends BaseV7Response {

    private GetAuthorizationRequest.ResponseBody.Authorization data;

    public GetAuthorizationRequest.ResponseBody.Authorization getData() {
      return data;
    }

    public void setData(GetAuthorizationRequest.ResponseBody.Authorization data) {
      this.data = data;
    }
  }
}
