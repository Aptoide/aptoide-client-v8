package cm.aptoide.pt.dataprovider.ws.v7;

import cm.aptoide.pt.dataprovider.BuildConfig;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.model.v7.GetUserInfo;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import java.util.ArrayList;
import java.util.List;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Observable;

public class GetUserInfoRequest extends V7<GetUserInfo, GetUserInfoRequest.Body> {

  protected GetUserInfoRequest(Body body, String baseHost, OkHttpClient httpClient,
      Converter.Factory converterFactory, BodyInterceptor bodyInterceptor,
      TokenInvalidator tokenInvalidator) {
    super(body, baseHost, httpClient, converterFactory, bodyInterceptor, tokenInvalidator);
  }

  public static String getHost() {
    return BuildConfig.APTOIDE_WEB_SERVICES_SCHEME
        + "://"
        + BuildConfig.APTOIDE_WEB_SERVICES_V7_HOST
        + "/api/7/";
  }

  public static GetUserInfoRequest of(OkHttpClient httpClient, Converter.Factory converterFactory,
      BodyInterceptor bodyInterceptor, TokenInvalidator tokenInvalidator) {
    final List<String> nodes = new ArrayList<>();
    nodes.add("meta");
    nodes.add("settings");
    final Body body = new Body(nodes);
    return new GetUserInfoRequest(body, getHost(), httpClient, converterFactory, bodyInterceptor,
        tokenInvalidator);
  }

  @Override protected Observable<GetUserInfo> loadDataFromNetwork(Interfaces interfaces,
      boolean bypassCache) {
    body.setRefresh(bypassCache);
    return interfaces.getUserInfo(body, bypassCache);
  }

  public static class Body extends BaseBody {

    private List<String> nodes;
    private boolean refresh;

    public Body(List<String> nodes) {
      this.nodes = nodes;
    }

    public List<String> getNodes() {
      return nodes;
    }

    public void setNodes(List<String> nodes) {
      this.nodes = nodes;
    }

    public void setRefresh(boolean refresh) {
      this.refresh = refresh;
    }
  }
}
