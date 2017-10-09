package cm.aptoide.pt.dataprovider.ws.v7.billing;

import android.content.SharedPreferences;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.model.v7.BaseV7Response;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.V7;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Observable;

public class DeletePurchaseRequest extends V7<BaseV7Response, DeletePurchaseRequest.RequestBody> {

  private DeletePurchaseRequest(RequestBody body, String baseHost, OkHttpClient httpClient,
      Converter.Factory converterFactory, BodyInterceptor bodyInterceptor,
      TokenInvalidator tokenInvalidator) {
    super(body, baseHost, httpClient, converterFactory, bodyInterceptor, tokenInvalidator);
  }

  public static DeletePurchaseRequest of(long purchaseId, OkHttpClient httpClient,
      Converter.Factory converterFactory, BodyInterceptor bodyInterceptor,
      TokenInvalidator tokenInvalidator, SharedPreferences sharedPreferences) {
    final RequestBody body = new RequestBody();
    body.setPurchaseId(purchaseId);
    return new DeletePurchaseRequest(body, getHost(sharedPreferences), httpClient, converterFactory,
        bodyInterceptor, tokenInvalidator);
  }

  @Override protected Observable<BaseV7Response> loadDataFromNetwork(Interfaces interfaces,
      boolean bypassCache) {
    return interfaces.deleteBillingPurchase(body, bypassCache);
  }

  public static class RequestBody {

    private long purchaseId;

    public long getPurchaseId() {
      return purchaseId;
    }

    public void setPurchaseId(long purchaseId) {
      this.purchaseId = purchaseId;
    }
  }
}
