package cm.aptoide.pt.dataprovider.ws.v7;

import android.content.SharedPreferences;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.model.v7.BaseV7Response;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Observable;

/**
 * Created by jdandrade on 24/11/2016.
 */

public class ShareInstallCardRequest extends V7<BaseV7Response, ShareInstallCardRequest.Body> {

  private final String packageName;
  private final String type;

  protected ShareInstallCardRequest(Body body, String packageName, String type,
      BodyInterceptor<BaseBody> bodyInterceptor, OkHttpClient httpClient,
      Converter.Factory converterFactory, TokenInvalidator tokenInvalidator,
      SharedPreferences sharedPreferences) {
    super(body, getHost(sharedPreferences), httpClient, converterFactory, bodyInterceptor,
        tokenInvalidator);
    this.packageName = packageName;
    this.type = type;
  }

  public static ShareInstallCardRequest of(String packageName, Long storeId, String shareType,
      BodyInterceptor<BaseBody> bodyInterceptor, OkHttpClient httpClient,
      Converter.Factory converterFactory, TokenInvalidator tokenInvalidator,
      SharedPreferences sharedPreferences) {
    ShareInstallCardRequest.Body body = new ShareInstallCardRequest.Body(packageName, storeId);
    return new ShareInstallCardRequest(body, packageName, shareType, bodyInterceptor, httpClient,
        converterFactory, tokenInvalidator, sharedPreferences);
  }

  @Override protected Observable<BaseV7Response> loadDataFromNetwork(Interfaces interfaces,
      boolean bypassCache) {
    return interfaces.shareInstallCard(body, packageName, body.getAccessToken(), type);
  }

  public static class Body extends BaseBody {

    private String packageName;
    private Long storeId;

    public Body(String packageName) {
      this.packageName = packageName;
      this.storeId = null;
    }

    public Body(String packageName, Long cardUid) {
      this.packageName = packageName;
      this.storeId = cardUid;
    }

    public String getPackageName() {
      return packageName;
    }

    public void setPackageName(String packageName) {
      this.packageName = packageName;
    }

    public Long getStoreId() {
      return storeId;
    }

    public void setStoreId(Long storeId) {
      this.storeId = storeId;
    }
  }
}

