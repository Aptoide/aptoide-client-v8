package cm.aptoide.pt.dataprovider.ws.v7;

import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.model.v7.BaseV7Response;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import com.fasterxml.jackson.annotation.JsonProperty;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Observable;

/**
 * Created by trinkes on 09/08/2017.
 */

public class PostReadRequest extends V7<BaseV7Response, PostReadRequest.Body> {

  private static final String TAG = PostReadRequest.class.getSimpleName();
  private final HttpUrl url;

  protected PostReadRequest(HttpUrl url, Body body, BodyInterceptor<BaseBody> bodyInterceptor,
      OkHttpClient httpClient, Converter.Factory converterFactory,
      TokenInvalidator tokenInvalidator) {
    super(body, url.scheme() + "://" + url.host(), httpClient, converterFactory, bodyInterceptor,
        tokenInvalidator);
    this.url = url;
  }

  public static PostReadRequest of(String url, String cardId, String cardType,
      BodyInterceptor<BaseBody> bodyInterceptor, OkHttpClient httpClient,
      Converter.Factory converterFactory, TokenInvalidator tokenInvalidator) {
    final Body body = new Body(new Post(cardId, cardType));
    return new PostReadRequest(HttpUrl.parse(url), body, bodyInterceptor, httpClient,
        converterFactory, tokenInvalidator);
  }

  @Override protected Observable<BaseV7Response> loadDataFromNetwork(Interfaces interfaces,
      boolean bypassCache) {
    return interfaces.setPostRead(bypassCache, body, url.encodedPath());
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
