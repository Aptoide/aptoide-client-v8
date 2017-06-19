package cm.aptoide.pt.dataprovider.ws.v7;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import cm.aptoide.pt.dataprovider.BuildConfig;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.model.v7.SetComment;
import cm.aptoide.pt.preferences.toolbox.ToolboxManager;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Observable;

public class PostCommentForTimelineArticle
    extends V7<SetComment, PostCommentForTimelineArticle.Body> {

  @NonNull public static String getHost(SharedPreferences sharedPreferences) {
    return (ToolboxManager.isToolboxEnableHttpScheme(sharedPreferences) ? "http"
        : BuildConfig.APTOIDE_WEB_SERVICES_SCHEME)
        + "://"
        + BuildConfig.APTOIDE_WEB_SERVICES_WRITE_V7_HOST
        + "/api/7/";
  }

  private PostCommentForTimelineArticle(Body body, BodyInterceptor<BaseBody> bodyInterceptor,
      OkHttpClient httpClient, Converter.Factory converterFactory,
      TokenInvalidator tokenInvalidator, SharedPreferences sharedPreferences) {
    super(body, getHost(sharedPreferences), httpClient, converterFactory, bodyInterceptor,
        tokenInvalidator);
  }

  public static PostCommentForTimelineArticle of(String timelineArticleId, String text,
      BodyInterceptor<BaseBody> bodyInterceptor, OkHttpClient httpClient,
      Converter.Factory converterFactory, TokenInvalidator tokenInvalidator,
      SharedPreferences sharedPreferences) {
    Body body = new Body(timelineArticleId, text);
    return new PostCommentForTimelineArticle(body, bodyInterceptor, httpClient, converterFactory,
        tokenInvalidator, sharedPreferences);
  }

  public static PostCommentForTimelineArticle of(String timelineArticleId, long previousCommentId,
      String text, BodyInterceptor<BaseBody> bodyInterceptor, OkHttpClient httpClient,
      Converter.Factory converterFactory, TokenInvalidator tokenInvalidator,
      SharedPreferences sharedPreferences) {
    Body body = new Body(timelineArticleId, text, previousCommentId);
    return new PostCommentForTimelineArticle(body, bodyInterceptor, httpClient, converterFactory,
        tokenInvalidator, sharedPreferences);
  }

  @Override
  protected Observable<SetComment> loadDataFromNetwork(Interfaces interfaces, boolean bypassCache) {
    return interfaces.postTimelineComment(body, true);
  }

  @Data @Accessors(chain = false) @EqualsAndHashCode(callSuper = true) public static class Body
      extends BaseBody {

    @JsonProperty("card_uid") private String timelineArticleId;
    @JsonProperty("comment_id") private Long previousCommentId;
    private String body;
    //private String commentType = CommentType.TIMELINE.name();

    public Body(String timelineArticleId, String text, long previousCommentId) {
      this(timelineArticleId, text);
      this.previousCommentId = previousCommentId;
    }

    public Body(String timelineArticleId, String text) {
      this.timelineArticleId = timelineArticleId;
      this.body = text;
    }
  }
}
