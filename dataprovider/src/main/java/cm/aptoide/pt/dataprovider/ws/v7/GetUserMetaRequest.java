package cm.aptoide.pt.dataprovider.ws.v7;

import cm.aptoide.pt.dataprovider.BuildConfig;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.model.v7.GetUserMeta;
import lombok.Data;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Observable;

/**
 * Created by pedroribeiro on 29/05/17.
 */

public class GetUserMetaRequest extends V7<GetUserMeta, GetUserMetaRequest.Body> {

  private static final String BASE_HOST = BuildConfig.APTOIDE_WEB_SERVICES_SCHEME
      + "://"
      + BuildConfig.APTOIDE_WEB_SERVICES_V7_HOST
      + "/api/7/";

  protected GetUserMetaRequest(GetUserMetaRequest.Body body, OkHttpClient httpClient,
      Converter.Factory converterFactory, BodyInterceptor bodyInterceptor) {
    super(body, BASE_HOST, httpClient, converterFactory, bodyInterceptor);
  }

  public static GetUserMetaRequest of(boolean refresh, String accessToken, String userName,
      BodyInterceptor<BaseBody> bodyInterceptor, OkHttpClient httpClient,
      Converter.Factory converterFactory) {
    Body body = new Body(refresh, userName);
    body.setAccessToken(accessToken);
    return new GetUserMetaRequest(body, httpClient, converterFactory, bodyInterceptor);
  }

  @Override protected Observable<GetUserMeta> loadDataFromNetwork(Interfaces interfaces,
      boolean bypassCache) {
    return interfaces.getUserMeta(body);
  }

  @Data public static class Body extends BaseBody {

    private boolean refresh;
    private String userName;

    public Body(boolean refresh, String userName) {
      this.refresh = refresh;
      this.userName = userName;
    }
  }
}
