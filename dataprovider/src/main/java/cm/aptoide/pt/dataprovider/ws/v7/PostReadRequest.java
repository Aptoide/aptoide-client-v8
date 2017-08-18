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
 * Created by trinkes on 09/08/2017.
 */

public class PostReadRequest extends V7<BaseV7Response, PostReadRequest.Body> {

  protected PostReadRequest(Body body, BodyInterceptor<BaseBody> bodyInterceptor,
      OkHttpClient httpClient, Converter.Factory converterFactory,
      TokenInvalidator tokenInvalidator, SharedPreferences sharedPreferences) {
    super(body, getHost(sharedPreferences), httpClient, converterFactory, bodyInterceptor,
        tokenInvalidator);
  }

  public static PostReadRequest of(String cardId, String cardType,
      BodyInterceptor<BaseBody> bodyInterceptor, OkHttpClient httpClient,
      Converter.Factory converterFactory, TokenInvalidator tokenInvalidator,
      SharedPreferences sharedPreferences) {
    final Body body = new Body(new Post(cardId, cardType));
    return new PostReadRequest(body, bodyInterceptor, httpClient, converterFactory,
        tokenInvalidator, sharedPreferences);
  }

  @Override protected Observable<BaseV7Response> loadDataFromNetwork(Interfaces interfaces,
      boolean bypassCache) {
    return interfaces.setPostRead(bypassCache, body);
  }

  static class Body extends BaseBody {
    @JsonProperty("card") private Post post;

    public Body(Post post) {
      this.post = post;
    }

    public Post getPost() {
      return post;
    }
  }

  static class Post {
    final String uid;
    final String type;

    public Post(String uid, String type) {
      this.uid = uid;
      this.type = type;
    }

    public String getUid() {
      return uid;
    }

    public String getType() {
      return type;
    }
  }
}
