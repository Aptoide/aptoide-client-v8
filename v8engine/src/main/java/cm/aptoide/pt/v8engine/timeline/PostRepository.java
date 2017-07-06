package cm.aptoide.pt.v8engine.timeline;

import android.content.SharedPreferences;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.model.v7.DataList;
import cm.aptoide.pt.v8engine.social.data.Card;
import cm.aptoide.pt.v8engine.timeline.model.CardPreview;
import cm.aptoide.pt.v8engine.timeline.model.RelatedApp;
import cm.aptoide.pt.v8engine.timeline.model.StillProcessingException;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Single;

public class PostRepository {
  
  private final BodyInterceptor<BaseBody> bodyInterceptor;
  private final OkHttpClient httpClient;
  private final Converter.Factory converterFactory;
  private final TokenInvalidator tokenInvalidator;
  private final SharedPreferences sharedPreferences;

  public PostRepository(BodyInterceptor<BaseBody> bodyInterceptor, OkHttpClient httpClient,
      Converter.Factory converterFactory, TokenInvalidator tokenInvalidator,
      SharedPreferences sharedPreferences) {
    this.bodyInterceptor = bodyInterceptor;
    this.httpClient = httpClient;
    this.converterFactory = converterFactory;
    this.tokenInvalidator = tokenInvalidator;
    this.sharedPreferences = sharedPreferences;
  }

  /**
   * @return Card inserted in the timeline. Possible types of cards: SOCIAL_APP, SOCIAL_ARTICLE,
   * SOCIAL_VIDEO
   */
  public Single<Card> postOnTimeline(String url, String content, String packageName) {
    return Single.just(null);
  }

  /**
   * This service may need a long pooling technique since it can return an error while
   * the web service is processing the request.
   *
   * @return a {@link DataList} of {@link RelatedApp}s found in the url
   * or {@link StillProcessingException} if the system is still processing
   * the request.
   */
  public Single<DataList<RelatedApp>> getRelatedApps(String url) {
    return Single.just(null);
  }

  public Single<CardPreview> getCardPreview(String url) {
    return Single.just(null);
  }
}
