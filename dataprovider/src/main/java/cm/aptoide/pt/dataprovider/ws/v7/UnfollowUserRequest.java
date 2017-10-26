package cm.aptoide.pt.dataprovider.ws.v7;

import android.content.SharedPreferences;
import cm.aptoide.pt.dataprovider.BuildConfig;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.model.v7.BaseV7Response;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.preferences.toolbox.ToolboxManager;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Observable;

/**
 * Created by jdandrade on 23/10/2017.
 */

public class UnfollowUserRequest extends V7<BaseV7Response, UnfollowUserRequest.Body> {
  private UnfollowUserRequest(Body body, OkHttpClient okhttp, Converter.Factory converterFactory,
      BodyInterceptor<BaseBody> bodyInterceptor, TokenInvalidator tokenInvalidator,
      SharedPreferences sharedPreferences) {
    super(body, getHost(sharedPreferences), okhttp, converterFactory, bodyInterceptor,
        tokenInvalidator);
  }

  public static String getHost(SharedPreferences sharedPreferences) {
    return (ToolboxManager.isToolboxEnableHttpScheme(sharedPreferences) ? "http"
        : BuildConfig.APTOIDE_WEB_SERVICES_SCHEME)
        + "://"
        + BuildConfig.APTOIDE_WEB_SERVICES_WRITE_V7_HOST
        + "/api/7/";
  }

  public static UnfollowUserRequest of(Long userId, BodyInterceptor<BaseBody> bodyInterceptor,
      OkHttpClient okhttp, Converter.Factory converterFactory, TokenInvalidator tokenInvalidator,
      SharedPreferences sharedPreferences) {
    return new UnfollowUserRequest(new UnfollowUserRequest.Body(userId), okhttp, converterFactory,
        bodyInterceptor, tokenInvalidator, sharedPreferences);
  }

  @Override protected Observable<BaseV7Response> loadDataFromNetwork(Interfaces interfaces,
      boolean bypassCache) {
    return interfaces.unfollowUser(body, bypassCache);
  }

  public static class Body extends BaseBody {
    private Long userId;
    private String action = "UNSUBSCRIBE";

    public Body(Long userId) {

      this.userId = userId;
    }

    public Long getUserId() {
      return userId;
    }

    public void setUserId(Long userId) {
      this.userId = userId;
    }

    public String getAction() {
      return action;
    }

    public void setAction(String action) {
      this.action = action;
    }
  }
}
