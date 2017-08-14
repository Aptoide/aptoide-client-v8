package cm.aptoide.pt.dataprovider.ws.v7.post;

import android.content.SharedPreferences;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.V7;
import com.fasterxml.jackson.annotation.JsonProperty;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Observable;

public class PostRequest extends V7<PostInTimelineResponse, PostRequest.PostRequestBody> {

  public PostRequest(String baseHost, PostRequestBody body,
      BodyInterceptor<BaseBody> bodyInterceptor, OkHttpClient httpClient,
      Converter.Factory converterFactory, TokenInvalidator tokenInvalidator) {
    super(body, baseHost, httpClient, converterFactory, bodyInterceptor, tokenInvalidator);
  }

  public static PostRequest of(String url, String content, String packageName,
      SharedPreferences sharedPreferences, BodyInterceptor bodyInterceptor,
      OkHttpClient okHttpClient, Converter.Factory converter, TokenInvalidator tokenInvalidator) {

    PostRequestBody body = new PostRequestBody(url, content, packageName);
    return new PostRequest(getHost(sharedPreferences), body, bodyInterceptor, okHttpClient,
        converter, tokenInvalidator);
  }

  @Override protected Observable<PostInTimelineResponse> loadDataFromNetwork(Interfaces interfaces,
      boolean bypassCache) {
    return interfaces.postInTimeline(bypassCache, body);
  }

  public static class PostRequestBody extends BaseBody {
    private String url;
    private String content;
    @JsonProperty("package_name") private String packageName;

    public PostRequestBody(String url, String content, String packageName) {
      this.url = url;
      this.content = content;
      this.packageName = packageName;
    }

    public String getUrl() {
      return url;
    }

    public void setUrl(String url) {
      this.url = url;
    }

    public String getContent() {
      return content;
    }

    public void setContent(String content) {
      this.content = content;
    }

    public String getPackageName() {
      return packageName;
    }

    public void setPackageName(String packageName) {
      this.packageName = packageName;
    }
  }
}
