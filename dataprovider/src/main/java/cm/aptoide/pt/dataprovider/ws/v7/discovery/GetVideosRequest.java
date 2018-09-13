package cm.aptoide.pt.dataprovider.ws.v7.discovery;

import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.model.v7.discovery.GetVideos;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.V7;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Observable;

public class GetVideosRequest extends V7<GetVideos, GetVideosRequest.Body> {

  protected GetVideosRequest(Body body, String baseHost, OkHttpClient httpClient,
      Converter.Factory converterFactory, BodyInterceptor bodyInterceptor,
      TokenInvalidator tokenInvalidator) {
    super(body, baseHost, httpClient, converterFactory, bodyInterceptor, tokenInvalidator);
  }

  @Override
  protected Observable<GetVideos> loadDataFromNetwork(Interfaces interfaces, boolean bypassCache) {
    // TODO: 10/09/2018
    return null;
  }

  public class Body extends BaseBody {

    @Override public void setRefresh(boolean refresh) {
    }
  }
}
