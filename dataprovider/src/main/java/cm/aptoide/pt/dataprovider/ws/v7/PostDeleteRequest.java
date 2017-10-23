package cm.aptoide.pt.dataprovider.ws.v7;

import android.content.SharedPreferences;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.model.v7.BaseV7Response;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import com.fasterxml.jackson.annotation.JsonProperty;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Observable;

/**
 * Created by jdandrade on 16/10/2017.
 */

public class PostDeleteRequest extends V7<BaseV7Response, PostDeleteRequest.Body> {
  protected PostDeleteRequest(Body body, OkHttpClient httpClient,
      Converter.Factory converterFactory, BodyInterceptor bodyInterceptor,
      TokenInvalidator tokenInvalidator, SharedPreferences sharedPreferences) {
    super(body, getHost(sharedPreferences), httpClient, converterFactory, bodyInterceptor,
        tokenInvalidator);
  }

  public static PostDeleteRequest of(String postId, BodyInterceptor<BaseBody> bodyInterceptor,
      OkHttpClient okhttp, Converter.Factory converterFactory, TokenInvalidator tokenInvalidator,
      SharedPreferences sharedPreferences) {
    return new PostDeleteRequest(new PostDeleteRequest.Body(postId), okhttp, converterFactory,
        bodyInterceptor, tokenInvalidator, sharedPreferences);
  }

  @Override protected Observable<BaseV7Response> loadDataFromNetwork(Interfaces interfaces,
      boolean bypassCache) {
    return interfaces.deletePost(body, bypassCache);
  }

  public static class Body extends BaseBody {
    @JsonProperty("card_uid") private String postId;

    public Body(String postId) {
      this.postId = postId;
    }

    public String getPostId() {
      return postId;
    }

    public void setPostId(String postId) {
      this.postId = postId;
    }
  }
}
