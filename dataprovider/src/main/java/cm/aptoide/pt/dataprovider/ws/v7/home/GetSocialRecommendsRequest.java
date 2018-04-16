package cm.aptoide.pt.dataprovider.ws.v7.home;

import android.content.SharedPreferences;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.V7;
import java.util.List;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Observable;

/**
 * Created by jdandrade on 26/03/2018.
 */

public class GetSocialRecommendsRequest
    extends V7<SocialResponse, GetSocialRecommendsRequest.Body> {
  private final String url;

  public GetSocialRecommendsRequest(String url, Body body, SharedPreferences sharedPreferences,
      OkHttpClient httpClient, Converter.Factory converterFactory, BodyInterceptor bodyInterceptor,
      TokenInvalidator tokenInvalidator) {
    super(body, getHost(sharedPreferences), httpClient, converterFactory, bodyInterceptor,
        tokenInvalidator);
    this.url = url;
  }

  public static GetSocialRecommendsRequest ofAction(String url,
      BodyInterceptor<BaseBody> bodyInterceptor, OkHttpClient httpClient,
      Converter.Factory converterFactory, TokenInvalidator tokenInvalidator,
      SharedPreferences sharedPreferences, List<String> packageNames) {
    return new GetSocialRecommendsRequest(url, new Body(packageNames), sharedPreferences,
        httpClient, converterFactory, bodyInterceptor, tokenInvalidator);
  }

  @Override protected Observable<SocialResponse> loadDataFromNetwork(Interfaces interfaces,
      boolean bypassCache) {
    return interfaces.getRecommends(url, body, bypassCache);
  }

  public static class Body extends BaseBody {
    private List<String> packageNames;

    public Body(List<String> packageNames) {
      this.packageNames = packageNames;
    }

    public List<String> getPackageNames() {
      return packageNames;
    }

    public void setPackageNames(List<String> packageNames) {
      this.packageNames = packageNames;
    }
  }
}
