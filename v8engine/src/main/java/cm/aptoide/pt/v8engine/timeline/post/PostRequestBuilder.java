package cm.aptoide.pt.v8engine.timeline.post;

import android.content.SharedPreferences;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.v8engine.timeline.TimelineAnalytics;
import cm.aptoide.pt.v8engine.timeline.request.CardPreviewRequest;
import cm.aptoide.pt.v8engine.timeline.request.PostRequest;
import cm.aptoide.pt.v8engine.timeline.request.RelatedAppsRequest;
import okhttp3.OkHttpClient;
import retrofit2.Converter;

class PostRequestBuilder {

  private final AptoideAccountManager accountManager;
  private final BodyInterceptor<BaseBody> bodyInterceptor;
  private final Converter.Factory converterFactory;
  private final OkHttpClient httpClient;
  private final TimelineAnalytics timelineAnalytics;
  private final TokenInvalidator tokenInvalidator;
  private final SharedPreferences sharedPreferences;

  PostRequestBuilder(AptoideAccountManager accountManager,
      BodyInterceptor<BaseBody> bodyInterceptor, Converter.Factory converterFactory,
      OkHttpClient httpClient, TimelineAnalytics timelineAnalytics,
      TokenInvalidator tokenInvalidator, SharedPreferences sharedPreferences) {
    this.accountManager = accountManager;
    this.bodyInterceptor = bodyInterceptor;
    this.converterFactory = converterFactory;
    this.httpClient = httpClient;
    this.timelineAnalytics = timelineAnalytics;
    this.tokenInvalidator = tokenInvalidator;
    this.sharedPreferences = sharedPreferences;
  }

  public PostRequest getPostOnTimelineRequest(String url, String content, String packageName) {

    return null;
  }

  public RelatedAppsRequest getRelatedAppsRequest(String url) {
    return null;
  }

  public CardPreviewRequest getCardPreviewRequest(String url) {
    return null;
  }
}
