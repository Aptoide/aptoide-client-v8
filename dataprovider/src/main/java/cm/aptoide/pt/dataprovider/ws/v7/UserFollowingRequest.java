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

public class UserFollowingRequest extends V7<BaseV7Response, UserFollowingRequest.Body> {
  private UserFollowingRequest(Body body, OkHttpClient okhttp, Converter.Factory converterFactory,
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

  public static UserFollowingRequest getFollowRequest(Long userId,
      BodyInterceptor<BaseBody> bodyInterceptor,
      OkHttpClient okhttp, Converter.Factory converterFactory, TokenInvalidator tokenInvalidator,
      SharedPreferences sharedPreferences) {
    return new UserFollowingRequest(new UserFollowingRequest.Body(userId, "SUBSCRIBE"), okhttp,
        converterFactory, bodyInterceptor, tokenInvalidator, sharedPreferences);
  }

  public static UserFollowingRequest getUnfollowRequest(Long userId,
      BodyInterceptor<BaseBody> bodyInterceptor, OkHttpClient okhttp,
      Converter.Factory converterFactory, TokenInvalidator tokenInvalidator,
      SharedPreferences sharedPreferences) {
    return new UserFollowingRequest(new UserFollowingRequest.Body(userId, "UNSUBSCRIBE"), okhttp,
        converterFactory,
        bodyInterceptor, tokenInvalidator, sharedPreferences);
  }

  @Override protected Observable<BaseV7Response> loadDataFromNetwork(Interfaces interfaces,
      boolean bypassCache) {
    return interfaces.followingUser(body, bypassCache);
  }

  public static class Body extends BaseBody {
    private Long userId;
    private String action;

    public Body(Long userId, String action) {
      this.userId = userId;
      this.action = action;
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
