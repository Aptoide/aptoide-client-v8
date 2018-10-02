package cm.aptoide.pt.discovery;

import android.content.SharedPreferences;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.model.v7.discovery.HasVideosResponse;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.discovery.HasVideosRequest;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Observable;

/**
 * Created by franciscocalado on 27/09/2018.
 */

public class InfoVideoService {

  private BodyInterceptor<BaseBody> bodyInterceptor;
  private OkHttpClient okHttpClient;
  private Converter.Factory converterFactory;
  private TokenInvalidator tokenInvalidator;
  private SharedPreferences sharedPreferences;

  public InfoVideoService(BodyInterceptor<BaseBody> bodyInterceptor, OkHttpClient okHttpClient,
      Converter.Factory converterFactory, TokenInvalidator tokenInvalidator,
      SharedPreferences sharedPreferences) {
    this.bodyInterceptor = bodyInterceptor;
    this.okHttpClient = okHttpClient;
    this.converterFactory = converterFactory;
    this.tokenInvalidator = tokenInvalidator;
    this.sharedPreferences = sharedPreferences;
  }

  public Observable<String> shouldShowVideos() {
    return HasVideosRequest.of(bodyInterceptor, okHttpClient, converterFactory, tokenInvalidator,
        sharedPreferences)
        .observe()
        .map(this::mapResponse);
  }

  private String mapResponse(HasVideosResponse hasVideosResponse) {

    if (hasVideosResponse.getData()
        .getLiveStreaming() != 0) {
      return "streaming";
    } else if (hasVideosResponse.getData()
        .getNonStreaming() != 0) {
      return "not_streaming";
    } else {
      return "no_videos";
    }
  }
}
