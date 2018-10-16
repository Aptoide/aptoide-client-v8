package cm.aptoide.pt.dataprovider.ws.v7.discovery;

import android.content.SharedPreferences;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.model.v7.discovery.VideosResponse;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.V7;
import java.util.ArrayList;
import java.util.List;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Observable;

public class GetVideosRequest extends V7<VideosResponse, GetVideosRequest.Body> {

  protected GetVideosRequest(Body body, OkHttpClient httpClient, Converter.Factory converterFactory,
      BodyInterceptor bodyInterceptor, TokenInvalidator tokenInvalidator,
      SharedPreferences sharedPreferences) {
    super(body, getHost(sharedPreferences), httpClient, converterFactory, bodyInterceptor,
        tokenInvalidator);
  }

  public static GetVideosRequest of(int offset, int limit,
      BodyInterceptor<BaseBody> bodyInterceptor, OkHttpClient httpClient,
      Converter.Factory converterFactory, TokenInvalidator tokenInvalidator,
      SharedPreferences sharedPreferences) {
    return new GetVideosRequest(new Body(offset, limit), httpClient, converterFactory,
        bodyInterceptor, tokenInvalidator, sharedPreferences);
  }

  @Override protected Observable<VideosResponse> loadDataFromNetwork(Interfaces interfaces,
      boolean bypassCache) {
    return interfaces.getVideos(body, bypassCache);
  }

  public static class Body extends BaseBody {
    private final int offset;
    private final int limit;
    private final List<String> packageNames;

    public Body(int offset, int limit) {
      this.offset = offset;
      this.limit = limit;
      this.packageNames = new ArrayList<>();
      packageNames.add("com.aptoidetv.com");
      packageNames.add("com.google.pt");
    }

    public List<String> getPackageNames() {
      return packageNames;
    }

    public int getLimit() {
      return limit;
    }

    public int getOffset() {
      return offset;
    }
  }
}
