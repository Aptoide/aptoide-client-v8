package cm.aptoide.pt.dataprovider.ws.v7;

import android.content.SharedPreferences;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import lombok.EqualsAndHashCode;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Observable;

/**
 * Created by jdandrade on 24/11/2016.
 */

public class ShareCardRequest extends V7<ShareCardResponse, ShareCardRequest.Body> {

  protected ShareCardRequest(Body body, BodyInterceptor<BaseBody> bodyInterceptor,
      OkHttpClient httpClient, Converter.Factory converterFactory,
      TokenInvalidator tokenInvalidator, SharedPreferences sharedPreferences) {
    super(body, getHost(sharedPreferences), httpClient, converterFactory, bodyInterceptor,
        tokenInvalidator);
  }

  public static ShareCardRequest of(String cardId, BodyInterceptor<BaseBody> bodyInterceptor,
      OkHttpClient httpClient, Converter.Factory converterFactory,
      TokenInvalidator tokenInvalidator, SharedPreferences sharedPreferences) {
    final ShareCardRequest.Body body = new ShareCardRequest.Body(cardId);
    return new ShareCardRequest(body, bodyInterceptor, httpClient, converterFactory,
        tokenInvalidator, sharedPreferences);
  }

  public static ShareCardRequest of(String cardId, long storeId, OkHttpClient httpClient,
      Converter.Factory converterFactory, BodyInterceptor<BaseBody> bodyInterceptor,
      TokenInvalidator tokenInvalidator, SharedPreferences sharedPreferences) {
    final ShareCardRequest.Body body = new ShareCardRequest.Body(cardId, storeId);
    return new ShareCardRequest(body, bodyInterceptor, httpClient, converterFactory,
        tokenInvalidator, sharedPreferences);
  }

  @Override protected Observable<ShareCardResponse> loadDataFromNetwork(V7.Interfaces interfaces,
      boolean bypassCache) {
    return interfaces.shareCard(body, body.getAccessToken());
  }

  @EqualsAndHashCode(callSuper = true) public static class Body extends BaseBody {

    private final String cardUid;
    private final Long storeId;

    public Body(String cardId) {
      this.cardUid = cardId;
      this.storeId = null;
    }

    public Body(String cardId, long storeId) {
      this.cardUid = cardId;
      this.storeId = storeId;
    }

    public Long getStoreId() {
      return storeId;
    }

    public String getCardUid() {
      return this.cardUid;
    }

    public String toString() {
      return "ShareCardRequest(cardUid="
          + this.getCardUid()
          + ",storeName="
          + this.getStoreId()
          + ")";
    }
  }
}

