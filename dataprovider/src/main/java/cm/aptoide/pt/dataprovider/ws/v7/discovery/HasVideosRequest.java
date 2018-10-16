package cm.aptoide.pt.dataprovider.ws.v7.discovery;

import android.content.SharedPreferences;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.model.v7.discovery.HasVideosResponse;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.V7;
import java.util.ArrayList;
import java.util.List;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Observable;

/**
 * Created by franciscocalado on 27/09/2018.
 */

public class HasVideosRequest extends V7<HasVideosResponse, HasVideosRequest.Body> {

  protected HasVideosRequest(Body body, OkHttpClient httpClient, Converter.Factory converterFactory,
      BodyInterceptor bodyInterceptor, TokenInvalidator tokenInvalidator,
      SharedPreferences sharedPreferences) {
    super(body, getHost(sharedPreferences), httpClient, converterFactory, bodyInterceptor,
        tokenInvalidator);
  }

  public static HasVideosRequest of(BodyInterceptor<BaseBody> bodyInterceptor,
      OkHttpClient httpClient, Converter.Factory converterFactory,
      TokenInvalidator tokenInvalidator, SharedPreferences sharedPreferences) {
    return new HasVideosRequest(new HasVideosRequest.Body(), httpClient, converterFactory,
        bodyInterceptor, tokenInvalidator, sharedPreferences);
  }

  @Override protected Observable<HasVideosResponse> loadDataFromNetwork(Interfaces interfaces,
      boolean bypassCache) {
    return interfaces.hasVideos(body, bypassCache);
  }

  public static class Body extends BaseBody {
    List<String> packageNames;

    public Body() {
      this.packageNames = new ArrayList<>();
      packageNames.add("com.aptoidetv.com");
      packageNames.add("com.google.pt");
    }

    public List<String> getPackageNames() {
      return packageNames;
    }
  }
}
