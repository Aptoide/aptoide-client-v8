package cm.aptoide.pt.dataprovider.ws.v7;

import cm.aptoide.pt.dataprovider.BuildConfig;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.model.v7.BaseV7Response;
import cm.aptoide.pt.preferences.toolbox.ToolboxManager;
import lombok.Data;
import lombok.EqualsAndHashCode;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Observable;

/**
 * Created by pedroribeiro on 16/12/16.
 */

public class SetConnectionRequest extends V7<BaseV7Response, SetConnectionRequest.Body> {

  private static final String BASE_HOST = (ToolboxManager.isToolboxEnableHttpScheme() ? "http"
      : BuildConfig.APTOIDE_WEB_SERVICES_SCHEME)
      + "://"
      + BuildConfig.APTOIDE_WEB_SERVICES_WRITE_V7_HOST
      + "/api/7/";

  protected SetConnectionRequest(Body body, BodyInterceptor<BaseBody> bodyInterceptor,
      OkHttpClient httpClient, Converter.Factory converterFactory,
      TokenInvalidator tokenInvalidator) {
    super(body, BASE_HOST, httpClient, converterFactory, bodyInterceptor, tokenInvalidator);
  }

  public static SetConnectionRequest of(String userPhone, BodyInterceptor<BaseBody> bodyInterceptor,
      OkHttpClient httpClient, Converter.Factory converterFactory,
      TokenInvalidator tokenInvalidator) {
    final Body body = new Body(userPhone);
    return new SetConnectionRequest(body, bodyInterceptor, httpClient, converterFactory,
        tokenInvalidator);
  }

  @Override protected Observable<BaseV7Response> loadDataFromNetwork(Interfaces interfaces,
      boolean bypassCache) {
    return interfaces.setConnection(body);
  }

  @Data @EqualsAndHashCode(callSuper = true) public static class Body extends BaseBody {

    private String phoneHash;

    public Body(String userPhone) {
      this.phoneHash = userPhone;
    }
  }
}
