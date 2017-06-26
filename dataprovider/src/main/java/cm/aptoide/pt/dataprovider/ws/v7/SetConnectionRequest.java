package cm.aptoide.pt.dataprovider.ws.v7;

import android.content.SharedPreferences;
import cm.aptoide.pt.dataprovider.BuildConfig;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.model.v7.BaseV7Response;
import cm.aptoide.pt.preferences.toolbox.ToolboxManager;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Observable;

/**
 * Created by pedroribeiro on 16/12/16.
 */

public class SetConnectionRequest extends V7<BaseV7Response, SetConnectionRequest.Body> {

  protected SetConnectionRequest(Body body, BodyInterceptor<BaseBody> bodyInterceptor,
      OkHttpClient httpClient, Converter.Factory converterFactory,
      TokenInvalidator tokenInvalidator, SharedPreferences sharedPreferences) {
    super(body, getHost(sharedPreferences), httpClient, converterFactory, bodyInterceptor,
        tokenInvalidator);
  }

  public static String getHost(SharedPreferences sharedPreferences) {
    return (ToolboxManager.isToolboxEnableHttpScheme(sharedPreferences) ? "http"
        : BuildConfig.APTOIDE_WEB_SERVICES_SCHEME)
        + "://"
        + BuildConfig.APTOIDE_WEB_SERVICES_WRITE_V7_HOST
        + "/api/7/";
  }

  public static SetConnectionRequest of(String userPhone, BodyInterceptor<BaseBody> bodyInterceptor,
      OkHttpClient httpClient, Converter.Factory converterFactory,
      TokenInvalidator tokenInvalidator, SharedPreferences sharedPreferences) {
    final Body body = new Body(userPhone);
    return new SetConnectionRequest(body, bodyInterceptor, httpClient, converterFactory,
        tokenInvalidator, sharedPreferences);
  }

  @Override protected Observable<BaseV7Response> loadDataFromNetwork(Interfaces interfaces,
      boolean bypassCache) {
    return interfaces.setConnection(body);
  }

  public static class Body extends BaseBody {

    private String phoneHash;

    public Body(String userPhone) {
      this.phoneHash = userPhone;
    }

    public String getPhoneHash() {
      return phoneHash;
    }

    public void setPhoneHash(String phoneHash) {
      this.phoneHash = phoneHash;
    }
  }
}
