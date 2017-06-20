package cm.aptoide.pt.dataprovider.ws.v7.store;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import cm.aptoide.pt.dataprovider.BuildConfig;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.V7;
import cm.aptoide.pt.model.v7.SetComment;
import cm.aptoide.pt.preferences.toolbox.ToolboxManager;
import com.fasterxml.jackson.annotation.JsonProperty;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Observable;

public class PostCommentForStore extends V7<SetComment, PostCommentForStore.Body> {

  private PostCommentForStore(Body body, BodyInterceptor<BaseBody> bodyInterceptor,
      OkHttpClient httpClient, Converter.Factory converterFactory,
      TokenInvalidator tokenInvalidator, SharedPreferences sharedPreferences) {
    super(body, getHost(sharedPreferences), httpClient, converterFactory, bodyInterceptor,
        tokenInvalidator);
  }

  @NonNull public static String getHost(SharedPreferences sharedPreferences) {
    return (ToolboxManager.isToolboxEnableHttpScheme(sharedPreferences) ? "http"
        : BuildConfig.APTOIDE_WEB_SERVICES_SCHEME)
        + "://"
        + BuildConfig.APTOIDE_WEB_SERVICES_WRITE_V7_HOST
        + "/api/7/";
  }

  public static PostCommentForStore of(long storeId, String text,
      BodyInterceptor<BaseBody> bodyInterceptor, OkHttpClient httpClient,
      Converter.Factory converterFactory, TokenInvalidator tokenInvalidator,
      SharedPreferences sharedPreferences) {
    Body body = new Body(storeId, text);
    return new PostCommentForStore(body, bodyInterceptor, httpClient, converterFactory,
        tokenInvalidator, sharedPreferences);
  }

  public static PostCommentForStore of(long storeId, long previousCommentId, String text,
      BodyInterceptor<BaseBody> bodyInterceptor, OkHttpClient httpClient,
      Converter.Factory converterFactory, TokenInvalidator tokenInvalidator,
      SharedPreferences sharedPreferences) {
    Body body = new Body(storeId, text, previousCommentId);
    return new PostCommentForStore(body, bodyInterceptor, httpClient, converterFactory,
        tokenInvalidator, sharedPreferences);
  }

  @Override
  protected Observable<SetComment> loadDataFromNetwork(Interfaces interfaces, boolean bypassCache) {
    return interfaces.postStoreComment(body, true);
  }

  public static class Body extends BaseBody {

    private long storeId;
    @JsonProperty("comment_id") private Long previousCommentId;
    private String body;

    public Body(long storeId, String text, long previousCommentId) {
      this(storeId, text);
      this.previousCommentId = previousCommentId;
    }

    public Body(long storeId, String text) {
      this.storeId = storeId;
      this.body = text;
    }

    public long getStoreId() {
      return storeId;
    }

    public void setStoreId(long storeId) {
      this.storeId = storeId;
    }

    public Long getPreviousCommentId() {
      return previousCommentId;
    }

    public void setPreviousCommentId(Long previousCommentId) {
      this.previousCommentId = previousCommentId;
    }

    public String getBody() {
      return body;
    }

    public void setBody(String body) {
      this.body = body;
    }
  }
}
