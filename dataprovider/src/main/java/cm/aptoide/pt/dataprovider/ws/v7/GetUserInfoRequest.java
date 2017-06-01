package cm.aptoide.pt.dataprovider.ws.v7;

import cm.aptoide.pt.dataprovider.BuildConfig;
import cm.aptoide.pt.model.v7.GetUserInfo;
import java.util.ArrayList;
import lombok.Data;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Observable;

/**
 * Created by pedroribeiro on 01/06/17.
 */

public class GetUserInfoRequest extends V7<GetUserInfo, GetUserInfoRequest.Body> {

  private static final String BASE_HOST = BuildConfig.APTOIDE_WEB_SERVICES_SCHEME
      + "://"
      + BuildConfig.APTOIDE_WEB_SERVICES_V7_HOST
      + "/api/7/";

  protected GetUserInfoRequest(Body body, String baseHost, OkHttpClient httpClient,
      Converter.Factory converterFactory, BodyInterceptor bodyInterceptor) {
    super(body, baseHost, httpClient, converterFactory, bodyInterceptor);
  }

  public static GetUserInfoRequest of(String accessToken, OkHttpClient httpClient,
      Converter.Factory converterFactory, BodyInterceptor bodyInterceptor) {
    Body body = new Body();
    body.setAccessToken(accessToken);
    return new GetUserInfoRequest(body, BASE_HOST, httpClient, converterFactory, bodyInterceptor);
  }

  @Override protected Observable<GetUserInfo> loadDataFromNetwork(Interfaces interfaces,
      boolean bypassCache) {
    return interfaces.getUserInfo(body);
  }

  @Data public static class Body extends BaseBody {

    private ArrayList<String> nodes;

    public Body() {
      nodes = new ArrayList<>();
      nodes.add("meta");
      nodes.add("settings");
    }
  }
}
