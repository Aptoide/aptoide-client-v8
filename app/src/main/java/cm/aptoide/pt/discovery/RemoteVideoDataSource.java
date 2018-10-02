package cm.aptoide.pt.discovery;

import android.content.SharedPreferences;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.model.v7.discovery.VideosData;
import cm.aptoide.pt.dataprovider.model.v7.discovery.VideosResponse;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.discovery.GetVideosRequest;
import java.util.ArrayList;
import java.util.List;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Single;

public class RemoteVideoDataSource implements VideoDataSource {

  private BodyInterceptor<BaseBody> bodyInterceptor;
  private OkHttpClient okHttpClient;
  private Converter.Factory converterFactory;
  private TokenInvalidator tokenInvalidator;
  private SharedPreferences sharedPreferences;

  public RemoteVideoDataSource(BodyInterceptor<BaseBody> bodyInterceptor, OkHttpClient okHttpClient,
      Converter.Factory converterFactory, TokenInvalidator tokenInvalidator,
      SharedPreferences sharedPreferences) {
    this.bodyInterceptor = bodyInterceptor;
    this.okHttpClient = okHttpClient;
    this.converterFactory = converterFactory;
    this.tokenInvalidator = tokenInvalidator;
    this.sharedPreferences = sharedPreferences;
  }

  @Override public Single<VideosList> loadFreshVideos(int limit) {
    return GetVideosRequest.of(0, limit, bodyInterceptor, okHttpClient, converterFactory,
        tokenInvalidator, sharedPreferences)
        .observe()
        .toSingle()
        .map(this::mapToVideos);
  }

  @Override public Single<VideosList> loadNextVideos(int offset, int limit) {
    return GetVideosRequest.of(offset, limit, bodyInterceptor, okHttpClient, converterFactory,
        tokenInvalidator, sharedPreferences)
        .observe()
        .toSingle()
        .map(this::mapToVideos);
  }

  @Override public boolean hasMore(Integer offset, String title) {
    return false;
  }

  private VideosList mapToVideos(VideosResponse videosResponse) {
    List<Video> result = new ArrayList<>();
    List<VideosData> videosResponseList = videosResponse.getDataList()
        .getList();

    for (VideosData data : videosResponseList) {
      result.add(new Video(data.getUrl(), data.getApp()
          .getName(), data.getApp()
          .getStats()
          .getRating()
          .getAvg(), data.getApp()
          .getIcon(), data.getType()));
    }
    return new VideosList(result, videosResponse.getDataList()
        .getOffset());
  }
}
