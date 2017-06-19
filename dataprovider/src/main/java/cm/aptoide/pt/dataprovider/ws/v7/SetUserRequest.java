package cm.aptoide.pt.dataprovider.ws.v7;

import android.content.SharedPreferences;
import cm.aptoide.pt.dataprovider.BuildConfig;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.model.v7.BaseV7Response;
import cm.aptoide.pt.preferences.toolbox.ToolboxManager;
import lombok.Data;
import lombok.EqualsAndHashCode;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Observable;

/**
 * Created by pedroribeiro on 16/12/16.
 */

public class SetUserRequest extends V7<BaseV7Response, SetUserRequest.Body> {

  public static String getHost(SharedPreferences sharedPreferences) {
    return (ToolboxManager.isToolboxEnableHttpScheme(sharedPreferences) ? "http"
        : BuildConfig.APTOIDE_WEB_SERVICES_SCHEME)
        + "://"
        + BuildConfig.APTOIDE_WEB_SERVICES_WRITE_V7_HOST
        + "/api/7/";
  }

  protected SetUserRequest(Body body, BodyInterceptor<BaseBody> bodyInterceptor,
      OkHttpClient httpClient, Converter.Factory converterFactory,
      TokenInvalidator tokenInvalidator, SharedPreferences sharedPreferences) {
    super(body, getHost(sharedPreferences), httpClient, converterFactory, bodyInterceptor,
        tokenInvalidator);
  }

  public static SetUserRequest of(String userAccess, BodyInterceptor<BaseBody> bodyInterceptor,
      OkHttpClient httpClient, Converter.Factory converterFactory,
      TokenInvalidator tokenInvalidator, SharedPreferences sharedPreferences) {
    Body body = new Body(userAccess, null);
    return new SetUserRequest(body, bodyInterceptor, httpClient, converterFactory, tokenInvalidator,
        sharedPreferences);
  }

  public static SetUserRequest ofWithName(String userName,
      BodyInterceptor<BaseBody> bodyInterceptor, OkHttpClient httpClient,
      Converter.Factory converterFactory, TokenInvalidator tokenInvalidator,
      SharedPreferences sharedPreferences) {
    Body body = new Body(null, userName);
    return new SetUserRequest(body, bodyInterceptor, httpClient, converterFactory, tokenInvalidator,
        sharedPreferences);
  }

  @Override protected Observable<BaseV7Response> loadDataFromNetwork(Interfaces interfaces,
      boolean bypassCache) {
    return interfaces.setUser(body);
  }

  @Data @EqualsAndHashCode(callSuper = true) public static class Body extends BaseBody {

    public String user_access;
    public UserProperties userProperties;

    public Body(String user_access, String userName) {
      this.user_access = user_access;
      userProperties = new UserProperties(userName);
    }
  }

  @Data public static class UserProperties {

    private String name;

    public UserProperties(String name) {
      this.name = name;
    }
  }
}
