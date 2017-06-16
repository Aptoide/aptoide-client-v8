package cm.aptoide.pt.dataprovider.ws.v7;

import cm.aptoide.pt.dataprovider.BuildConfig;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.model.v7.GetUserInfo;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Observable;

/**
 * Created by pedroribeiro on 01/06/17.
 */

public class GetUserInfoRequest extends V7<GetUserInfo, GetUserInfoRequest.Body> {

  public static String getHost() {
    return BuildConfig.APTOIDE_WEB_SERVICES_SCHEME
        + "://"
        + BuildConfig.APTOIDE_WEB_SERVICES_V7_HOST
        + "/api/7/";
  }

  protected GetUserInfoRequest(Body body, String baseHost, OkHttpClient httpClient,
      Converter.Factory converterFactory, BodyInterceptor bodyInterceptor,
      TokenInvalidator tokenInvalidator) {
    super(body, baseHost, httpClient, converterFactory, bodyInterceptor, tokenInvalidator);
  }

  public static GetUserInfoRequest of(String accessToken, OkHttpClient httpClient,
      Converter.Factory converterFactory, BodyInterceptor bodyInterceptor,
      TokenInvalidator tokenInvalidator) {
    final List<String> nodes = new ArrayList<>();
    nodes.add("meta");
    nodes.add("settings");
    final Body body = new Body(nodes);
    body.setAccessToken(accessToken);
    return new GetUserInfoRequest(body, getHost(), httpClient, converterFactory, bodyInterceptor,
        tokenInvalidator);
  }

  @Override protected Observable<GetUserInfo> loadDataFromNetwork(Interfaces interfaces,
      boolean bypassCache) {
    body.setRefresh(bypassCache);
    return interfaces.getUserInfo(body, bypassCache);
  }

  @Data @EqualsAndHashCode(callSuper = true) public static class Body extends BaseBody {

    private List<String> nodes;
    private boolean refresh;

    public Body(List<String> nodes) {
      this.nodes = nodes;
    }
  }
}
